/*
 * Copyright (c) 2019. Maverick Labs
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as,
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package net.mavericklabs.bos.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.mavericklabs.bos.BosApplication;
import net.mavericklabs.bos.model.Group;
import net.mavericklabs.bos.model.Measurement;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.model.User;
import net.mavericklabs.bos.model.UserReading;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmGroup;
import net.mavericklabs.bos.realm.RealmMeasurement;
import net.mavericklabs.bos.realm.RealmReading;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.realm.RealmTranslation;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.Constants;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.R;
import net.mavericklabs.bos.model.LoginResponse;
import net.mavericklabs.bos.model.Translation;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.retrofit.ApiClient;
import net.mavericklabs.bos.utils.DateUtil;
import net.mavericklabs.bos.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Response;

import static android.content.Context.ACCOUNT_SERVICE;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private AppLogger appLogger = new AppLogger(getClass().toString());
    public static final int SYNC_EVERYTHING = 0;
    public static final int SYNC_BOOKS = 1;
    public static final int SYNC_SESSIONS = 2;
    public static final int SYNC_TRANSLATIONS = 3;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static boolean requestSync(Context context, int syncType) {
        String AUTHORITY = context.getResources().getString(R.string.content_authority);
        String ACCOUNT_NAME = context.getResources().getString(R.string.sync_account_name);
        String ACCOUNT_TYPE = context.getResources().getString(R.string.sync_account_type);

        Account ACCOUNT = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        boolean isSyncOn = ContentResolver.isSyncActive(ACCOUNT, AUTHORITY);
        if (!isSyncOn) {
            Bundle settingsBundle = new Bundle();
            settingsBundle.putInt("syncType", syncType);
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(ACCOUNT, AUTHORITY, settingsBundle);
            return true;
        } else {
            return false;

        }
    }

    public static boolean isSyncActive(Context context) {
        String AUTHORITY = context.getResources().getString(R.string.content_authority);
        String ACCOUNT_NAME = context.getResources().getString(R.string.sync_account_name);
        String ACCOUNT_TYPE = context.getResources().getString(R.string.sync_account_type);
        Account ACCOUNT = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        return ContentResolver.isSyncActive(ACCOUNT, AUTHORITY);
    }

    public static Account getAccount(Context context) {
        String ACCOUNT_NAME = context.getResources().getString(R.string.sync_account_name);
        String ACCOUNT_TYPE = context.getResources().getString(R.string.sync_account_type);
        return new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
    }

    public static void createSyncAccount(Context context) {
        Account account = getAccount(context);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if (accountManager != null && accountManager.addAccountExplicitly(account, null, null)) {
            final long SYNC_FREQUENCY = 60 * 60; // 1 hour (seconds)
            String AUTHORITY = context.getResources().getString(R.string.content_authority);
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, AUTHORITY, new Bundle(), SYNC_FREQUENCY);
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        appLogger.logDebug("onPerformSync");
        LoginResponse loginResponse = RealmHandler.getLoginResponse();
        if (loginResponse == null) {
            appLogger.logDebug("login response null");
            notifySyncStopped();
            return;
        }
        int syncType = extras.getInt("syncType");
        switch (syncType) {
            case SYNC_TRANSLATIONS:
            case SYNC_EVERYTHING:
//                syncTranslations();
                syncMeasurements(loginResponse.getNgoKey());
                syncResources(loginResponse.getUserKey());
                syncGroups(loginResponse.getUserKey());
                syncAthletes(loginResponse.getUserKey());
//                sync(loginResponse.getNgoKey());
                syncOfflineAthletes();
                syncEvaluationResourceReadings();
                syncAthleteBaseline();
                notifySyncStopped();
                break;
        }
    }

    private void notifySyncStopped() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.SYNC_COMPLETED);
        contentResolver.notifyChange(uri, null);
        appLogger.logDebug("notifyChange Sync stopped");
    }

    private void syncMeasurements(String ngoKey) {

        try {
            Response<List<Measurement>> response = ApiClient.getApiInterface(getContext()).getAllMeasurements(ngoKey).execute();
            if (response.isSuccessful()) {
                List<Measurement> measurements = response.body();
                Realm realm = Realm.getDefaultInstance();
                List<RealmMeasurement> realmMeasurements = RealmHandler.getAllMeasurements();
                appLogger.logInformation("Realm measurements " + realmMeasurements.size());


                if (measurements == null) {
                    return;
                }
                realm.beginTransaction();
                for (Measurement measurement : measurements) {
                    RealmMeasurement realmMeasurement = new RealmMeasurement(measurement);
                    realm.copyToRealmOrUpdate(realmMeasurement);
                }
                realm.commitTransaction();
                realm.close();
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.MEASUREMENTS);
                contentResolver.notifyChange(uri, null);
                appLogger.logDebug("notifyChange GROUPS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void syncResources(String userKey) {

        try {
            Response<List<Resource>> response = ApiClient.getApiInterface(getContext()).getUserResources(userKey).execute();
            if (response.isSuccessful()) {
                List<Resource> resources = response.body();
                Realm realm = Realm.getDefaultInstance();
                List<RealmResource> realmResources = RealmHandler.getAllResources();
                appLogger.logInformation("Realm resources " + realmResources.size());


                if (resources == null) {
                    return;
                }
                realm.beginTransaction();

                for (Resource resource : resources) {
                    RealmResource realmResource = new RealmResource(resource);
                    realm.copyToRealmOrUpdate(realmResource);
                }
                realm.commitTransaction();
                realm.close();
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.RESOURCES);
                contentResolver.notifyChange(uri, null);
                appLogger.logDebug("notifyChange RESOURCES");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void syncGroups(String userKey) {

        try {
            Response<List<Group>> response = ApiClient.getApiInterface(getContext()).getGroups(userKey).execute();
            if (response.isSuccessful()) {
                List<Group> groups = response.body();
                Realm realm = Realm.getDefaultInstance();
                List<RealmGroup> realmGroups = RealmHandler.getAllGroups();
                appLogger.logInformation("Realm groups " + realmGroups.size());

                if (groups == null) {
                    return;
                }

                for (Group group : groups) {
                    appLogger.logInformation(String.valueOf(group.getResources().size()));

                    // Get Realm users
                    RealmList<RealmUser> realmUsers = new RealmList<>();
                    for (User user : group.getUsers()) {
                        RealmUser realmUser = RealmHandler.findUserOrCreate(user);
                        realmUsers.add(realmUser);
                    }
                    realm.beginTransaction();

                    RealmGroup realmGroup = new RealmGroup(group, realmUsers);
                    realm.copyToRealmOrUpdate(realmGroup);
                    realm.commitTransaction();

                }
                realm.close();
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.GROUPS);
                contentResolver.notifyChange(uri, null);
                appLogger.logDebug("notifyChange GROUPS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void syncAthletes(String userKey) {

        try {
            Response<List<User>> response = ApiClient.getApiInterface(getContext()).getAthletes(userKey).execute();
            if (response.isSuccessful()) {
                List<User> athletes = response.body();
                Realm realm = Realm.getDefaultInstance();
                List<RealmUser> realmAthletes = RealmHandler.getAllAthletes();
                appLogger.logInformation("Realm athletes " + realmAthletes.size());

                if (athletes == null) {
                    return;
                }

                for (User athlete : athletes) {
                    fetchUserResources(athlete);
                }
                realm.close();
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.ATHLETES);
                contentResolver.notifyChange(uri, null);
                appLogger.logDebug("notifyChange TRANSLATIONS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchUserResources(User athlete) {

        try {
            Response<List<Resource>> response = ApiClient.getApiInterface(getContext()).getUserResources(athlete.getKey()).execute();
            if (response.isSuccessful()) {
                List<Resource> resources = response.body();
                Realm realm = Realm.getDefaultInstance();

                if (resources == null) {
                    return;
                }

                RealmUser realmUser = RealmHandler.findUserOrCreate(athlete);
                realm.beginTransaction();
                RealmList<RealmResource> realmResources = new RealmList<>();
                for (Resource resource : resources) {
                    RealmResource realmResource = new RealmResource(resource);
                    realmResource = realm.copyToRealmOrUpdate(realmResource);
                    realmResources.add(realmResource);
                }
                appLogger.logInformation("realmResources :" + realmResources.size());
                realmUser.setResources(realmResources);
                realm.copyToRealmOrUpdate(realmUser);
                realm.commitTransaction();
                realm.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void syncEvaluationResourceReadings() {

//        Get unsynced user readings
        Realm realm = Realm.getDefaultInstance();
        List<RealmEvaluationResource> unSyncedEvaluatedResources = RealmHandler.getUnSyncedEvaluatedResources();

        appLogger.logInformation("unSyncedEvaluatedResources " + unSyncedEvaluatedResources.size());

        for (RealmEvaluationResource unSyncedEvaluatedResource : unSyncedEvaluatedResources) {
            List<RealmReading> unSyncedRealmReadings = RealmHandler.getUnSyncedReadingsForEvaluationResource(unSyncedEvaluatedResource);
            appLogger.logInformation("unsynced readings " + unSyncedRealmReadings.size());

            for (RealmReading unSyncedRealmReading : unSyncedRealmReadings) {
                createUserReading(unSyncedRealmReading);
            }

            realm.beginTransaction();
            unSyncedEvaluatedResource.setSynced(true);
            realm.copyToRealmOrUpdate(unSyncedEvaluatedResource);
            realm.commitTransaction();


        }
        realm.close();


    }

    private void syncAthleteBaseline() {
        //        Get unsynced user readings
        Realm realm = Realm.getDefaultInstance();
        List<RealmReading> unSyncedBaselineReadings = RealmHandler.getUnSyncedBaselinesForAthletes();
        appLogger.logInformation("unSyncedBaselineReadings :" + unSyncedBaselineReadings.size());
        for (RealmReading unSyncedRealmReading : unSyncedBaselineReadings) {
            createUserReading(unSyncedRealmReading);
        }
        realm.close();

    }


    private void syncOfflineAthletes() {

        Realm realm = Realm.getDefaultInstance();
        List<RealmUser> offlineAthletes = RealmHandler.getOfflineAthletes();
        appLogger.logInformation("offline athletes " + offlineAthletes.size());

        for (RealmUser offlineAthlete : offlineAthletes) {

            try {
                JSONObject paramObject = new JSONObject();
                paramObject.put("first_name", offlineAthlete.getFirstName());
                paramObject.put("middle_name", offlineAthlete.getMiddleName());
                paramObject.put("last_name", offlineAthlete.getLastName());
                paramObject.put("email", offlineAthlete.getEmail());
                paramObject.put("ngo", offlineAthlete.getNgo());
                paramObject.put("is_active", true);

                Response<User> response = ApiClient.getApiInterface(getContext())
                        .createAthlete(paramObject.toString()).execute();
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user == null) {
                        return;
                    }
                    Realm realmInstance = Realm.getDefaultInstance();
                    realmInstance.beginTransaction();
                    offlineAthlete.setKey(user.getKey());
                    realmInstance.copyToRealmOrUpdate(offlineAthlete);
                    realmInstance.commitTransaction();
                    realmInstance.close();

                    // Sync baseline data for athlete
                    syncBaselineForAthlete(offlineAthlete);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        realm.close();


    }

    private void syncBaselineForAthlete(RealmUser offlineAthlete) {
        List<RealmReading> unSyncedRealmReadings = RealmHandler.getBaselineForAthlete(offlineAthlete);
        for (RealmReading unSyncedRealmReading : unSyncedRealmReadings) {
            createUserReading(unSyncedRealmReading);
        }
    }

    private void createUserReading(RealmReading unSyncedRealmReading) {

        String userKey = unSyncedRealmReading.getUser() != null ? unSyncedRealmReading.getUser().getKey() : null;
        if (userKey == null) {
            return;
        }
        String ngoKey = unSyncedRealmReading.getNgo();
//                String enteredBy =  unSyncedRealmReading.getEnteredByUser() != null ? unSyncedRealmReading.getEnteredByUser().getKey() : null;
        String measurementKey = unSyncedRealmReading.getMeasurement().getKey();
        String resourceKey = unSyncedRealmReading.getResource() != null ? unSyncedRealmReading.getResource().getKey() : null;
//                String resourceSessionKey = unSyncedRealmReading.getMeasurement().getKey();
        String value = unSyncedRealmReading.getValue();
        String enteredDateTime = DateUtil.getTZDateString(unSyncedRealmReading.getCreationTime());

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("user", userKey);
            paramObject.put("ngo", ngoKey);
            paramObject.put("measurement", measurementKey);
            paramObject.put("resource", resourceKey);
            paramObject.put("value", value);
            paramObject.put("entered_date_time", enteredDateTime);

            Response<UserReading> response = ApiClient.getApiInterface(getContext())
                    .createUserReading(paramObject.toString()).execute();
            if (response.isSuccessful()) {
                UserReading userReading = response.body();
                if (userReading == null) {
                    return;
                }

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                unSyncedRealmReading.setKey(userReading.getKey());
                realm.copyToRealmOrUpdate(unSyncedRealmReading);
                realm.commitTransaction();
                realm.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncTranslations() {
        try {
            Response<Translation> response = ApiClient.getApiInterface(getContext()).getTranslation().execute();
            if (response.isSuccessful()) {
                Translation translation = response.body();
                if (translation != null) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.delete(RealmTranslation.class);

                    JsonObject englishJson = translation.getEnglish();
                    Set<Map.Entry<String, JsonElement>> englishEntries = englishJson.entrySet();//will return members of your object

                    for (Map.Entry<String, JsonElement> entry : englishEntries) {
                        RealmTranslation realmTranslation = new RealmTranslation(Constants.en_INLocale, entry.getKey(), entry.getValue().getAsString());
                        realm.copyToRealm(realmTranslation);
                    }
                    JsonObject marathiJson = translation.getMarathi();
                    Set<Map.Entry<String, JsonElement>> marathiEntries = marathiJson.entrySet();//will return members of your object

                    for (Map.Entry<String, JsonElement> entry : marathiEntries) {
                        RealmTranslation realmTranslation = new RealmTranslation(Constants.mr_INLocale, entry.getKey(), entry.getValue().getAsString());
                        realm.copyToRealm(realmTranslation);
                        appLogger.logDebug(String.valueOf(entry.getValue()));
                    }
                    realm.commitTransaction();
                    realm.close();
                }
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.TRANSLATIONS);
                contentResolver.notifyChange(uri, null);
                appLogger.logDebug("notifyChange TRANSLATIONS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
