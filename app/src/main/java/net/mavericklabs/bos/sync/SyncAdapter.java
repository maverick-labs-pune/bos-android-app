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

import com.google.android.gms.common.util.CollectionUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.mavericklabs.bos.BosApplication;
import net.mavericklabs.bos.model.EvaluationResource;
import net.mavericklabs.bos.model.Group;
import net.mavericklabs.bos.model.Measurement;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.model.User;
import net.mavericklabs.bos.model.UserReading;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Day;
import net.mavericklabs.bos.object.File;
import net.mavericklabs.bos.object.TrainingSession;
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
import net.mavericklabs.bos.utils.SharedPreferenceUtil;
import net.mavericklabs.bos.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Response;

import static android.content.Context.ACCOUNT_SERVICE;
import static net.mavericklabs.bos.utils.Constants.CURRICULUM;
import static net.mavericklabs.bos.utils.Constants.FILE;
import static net.mavericklabs.bos.utils.Constants.REGISTRATION_FORM;
import static net.mavericklabs.bos.utils.Constants.TRAINING_SESSION;
import static net.mavericklabs.bos.utils.Util.removeGroupFromList;
import static net.mavericklabs.bos.utils.Util.removeResourceFromList;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private AppLogger appLogger = new AppLogger(getClass().toString());
    public static final int SYNC_EVERYTHING = 0;
    public static final int SYNC_DAILY_FRAGMENT = 1;
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

        boolean firstSyncAfterLogin = (loginResponse.getFirstLogin() != null) ? loginResponse.getFirstLogin() : false;
        appLogger.logInformation("firstSyncAfterLogin " + firstSyncAfterLogin);
        if (!firstSyncAfterLogin) {
            if (!firstSyncAfterLogin(loginResponse)) {
                return;
            }
            RealmHandler.setFirstLoginResponse();
            postFetchEvaluationResourceSync();
            return;
        }
        int syncType = extras.getInt("syncType");
        switch (syncType) {
            case SYNC_TRANSLATIONS:
            case SYNC_EVERYTHING:
//                syncTranslations();
                basicSync(loginResponse);
                syncFiles();
                syncOfflineAthletes();
                syncEvaluationResourceReadings();
                syncAthleteBaselines();
                notifySyncStopped();
                break;
        }
    }

    private boolean firstSyncAfterLogin(LoginResponse loginResponse) {
        boolean successful = basicSync(loginResponse);
        if (!successful) {
            return false;
        }
        successful = fetchEvaluationResources(loginResponse.getUserKey());
        return successful;
    }

    private boolean basicSync(LoginResponse loginResponse) {
        boolean successful;
        successful = syncMeasurements(loginResponse.getNgoKey());
        if (!successful) {
            return false;
        }
        successful = syncResources(loginResponse.getUserKey());
        if (!successful) {
            return false;
        }
        successful = syncGroups(loginResponse.getUserKey());
        if (!successful) {
            return false;
        }
        return syncAthletes(loginResponse.getUserKey());
    }

    private void postFetchEvaluationResourceSync() {
        syncFiles();
        syncOfflineAthletes();
        syncEvaluationResourceReadings();
        syncAthleteBaselines();
        notifySyncStopped();
    }

    private void notifySyncStopped() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.SYNC_COMPLETED);
        contentResolver.notifyChange(uri, null);
        appLogger.logDebug("notifyChange Sync stopped");
    }

    private boolean syncMeasurements(String ngoKey) {

        try {
            Response<List<Measurement>> response = ApiClient.getApiInterface(getContext()).getAllMeasurements(ngoKey).execute();
            if (response.isSuccessful()) {
                List<Measurement> measurements = response.body();
                if (CollectionUtils.isEmpty(measurements)) {
                    appLogger.logInformation("measurements from server empty");
                    return true;
                }

                Realm realm = Realm.getDefaultInstance();
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
                appLogger.logDebug("notifyChange MEASUREMENTS");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean syncResources(String userKey) {

        try {
            Response<List<Resource>> response = ApiClient.getApiInterface(getContext()).getUserResources(userKey).execute();
            if (response.isSuccessful()) {
                List<Resource> resources = response.body();
                if (CollectionUtils.isEmpty(resources)) {
                    return true;
                }
                Realm realm = Realm.getDefaultInstance();
                List<RealmResource> realmResources = RealmHandler.getAllActiveResources();
                appLogger.logInformation("Realm resources " + realmResources.size());
                List<RealmResource> inMemoryRealmResources = realm.copyFromRealm(realmResources);

                realm.beginTransaction();
                RealmList<RealmResource> selfUserResources = new RealmList<>();
                for (Resource resource : resources) {
                    RealmResource realmResource = new RealmResource(resource);
                    realmResource = realm.copyToRealmOrUpdate(realmResource);
                    selfUserResources.add(realmResource);
                    removeResourceFromList(inMemoryRealmResources, realmResource);
                }
                RealmUser selfUser = RealmHandler.getSelfRealmUser();
                selfUser.setResources(selfUserResources);
                appLogger.logInformation("Realm deactivated resources " + inMemoryRealmResources.size());
                for (RealmResource deactivatedResource : inMemoryRealmResources) {
                    RealmHandler.deactivateResource(deactivatedResource);
                }

                realm.commitTransaction();
                realm.close();
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.RESOURCES);
                contentResolver.notifyChange(uri, null);
                appLogger.logDebug("notifyChange RESOURCES");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean syncGroups(String userKey) {

        try {
            Response<List<Group>> response = ApiClient.getApiInterface(getContext()).getGroups(userKey).execute();
            if (response.isSuccessful()) {
                List<Group> groups = response.body();
                if (CollectionUtils.isEmpty(groups)) {
                    return true;
                }

                Realm realm = Realm.getDefaultInstance();
                List<RealmGroup> realmGroups = RealmHandler.getAllActiveGroups();
                appLogger.logInformation("Realm groups " + realmGroups.size());
                List<RealmGroup> inMemoryRealmGroups = realm.copyFromRealm(realmGroups);

                for (Group group : groups) {
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
                    removeGroupFromList(inMemoryRealmGroups, realmGroup);

                }

                appLogger.logInformation("Realm deactivated groups " + inMemoryRealmGroups.size());
                for (RealmGroup realmGroup : inMemoryRealmGroups) {
                    RealmHandler.deactivateGroup(realmGroup);
                }

                realm.close();
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.GROUPS);
                contentResolver.notifyChange(uri, null);
                appLogger.logDebug("notifyChange GROUPS");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean syncAthletes(String userKey) {

        try {
            Response<List<User>> response = ApiClient.getApiInterface(getContext()).getAthletes(userKey).execute();
            if (response.isSuccessful()) {
                List<User> athletes = response.body();
                if (CollectionUtils.isEmpty(athletes)) {
                    return true;
                }

                Realm realm = Realm.getDefaultInstance();
                List<RealmUser> realmAthletes = RealmHandler.getAllActiveAthletes();
                appLogger.logInformation("Realm athletes " + realmAthletes.size());
                List<RealmUser> inMemoryRealmAthletes = realm.copyFromRealm(realmAthletes);

                for (User athlete : athletes) {
                    RealmUser realmUser = RealmHandler.findUserOrCreate(athlete);
                    fetchUserResources(realmUser);
                    fetchUserReadings(realmUser);
                }

                appLogger.logInformation("Realm deactivated athletes " + inMemoryRealmAthletes.size());
                realm.beginTransaction();
                for (RealmUser realmUser : inMemoryRealmAthletes) {
                    RealmHandler.deactivateUser(realmUser);
                }
                realm.commitTransaction();
                realm.close();
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.ATHLETES);
                contentResolver.notifyChange(uri, null);
                appLogger.logDebug("notifyChange ATHLETES");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean fetchUserReadings(RealmUser realmUser) {
        try {
            Response<List<UserReading>> response = ApiClient.getApiInterface(getContext())
                    .getUserReadings(realmUser.getKey()).execute();
            if (response.isSuccessful()) {
                List<UserReading> userReadings = response.body();
                if (CollectionUtils.isEmpty(userReadings)) {
                    return true;
                }
                for (UserReading userReading : userReadings) {
                    RealmMeasurement realmMeasurement = RealmHandler.getMeasurementFromKey(userReading.getMeasurement());
                    RealmHandler.findReadingOrCreate(userReading, realmUser, realmMeasurement);
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean fetchUserResources(RealmUser realmUser) {

        try {
            Response<List<Resource>> response = ApiClient.getApiInterface(getContext()).getUserResources(realmUser.getKey()).execute();
            if (response.isSuccessful()) {
                List<Resource> resources = response.body();
                if (CollectionUtils.isEmpty(resources)) {
                    return true;
                }

                Realm realm = Realm.getDefaultInstance();
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
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void syncEvaluationResourceReadings() {

//        Get unsynced user readings
        Realm realm = Realm.getDefaultInstance();
        List<RealmEvaluationResource> unSyncedEvaluatedResources = RealmHandler.getUnSyncedEvaluatedResources();

        appLogger.logInformation("unSyncedEvaluatedResources " + unSyncedEvaluatedResources.size());

        for (RealmEvaluationResource unSyncedEvaluatedResource : unSyncedEvaluatedResources) {
            List<RealmReading> unSyncedRealmReadings = RealmHandler.getUnSyncedReadingsForEvaluationResource(unSyncedEvaluatedResource);
            appLogger.logInformation("unsynced readings " + unSyncedRealmReadings.size());

            boolean didAllReadingsSync = true;
            for (RealmReading unSyncedRealmReading : unSyncedRealmReadings) {
                boolean synced = createUserReading(unSyncedRealmReading, true);
                if (!synced) {
                    didAllReadingsSync = false;
                }
            }

            if (didAllReadingsSync) {
                String evaluationResourceKey = unSyncedEvaluatedResource.getKey();
                boolean successful;
                if (evaluationResourceKey == null) {
                    successful = createEvaluationResourceOnServer(unSyncedEvaluatedResource);
                } else {
                    successful = updateEvaluationResourceOnServer(unSyncedEvaluatedResource);
                }
                if (successful) {
                    realm.beginTransaction();
                    unSyncedEvaluatedResource.setSynced(true);
                    realm.copyToRealmOrUpdate(unSyncedEvaluatedResource);
                    realm.commitTransaction();
                }
            }
        }
        realm.close();
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.DAILY_PLANNER);
        contentResolver.notifyChange(uri, null);
        appLogger.logDebug("notifyChange DAILY_PLANNER");

    }

    private void syncAthleteBaselines() {
        //        Get unsynced user readings
        Realm realm = Realm.getDefaultInstance();
        List<RealmReading> unSyncedBaselineReadings = RealmHandler.getUnSyncedBaselinesForAthletes();
        appLogger.logInformation("unSyncedBaselineReadings :" + unSyncedBaselineReadings.size());
        for (RealmReading unSyncedRealmReading : unSyncedBaselineReadings) {
            createUserReading(unSyncedRealmReading, false);
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
                paramObject.put("gender", offlineAthlete.getGender());
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
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        realm.close();


    }

    private void syncBaselineForAthlete(RealmUser offlineAthlete) {
        List<RealmReading> unSyncedRealmReadings = RealmHandler.getBaselineForAthlete(offlineAthlete);
        for (RealmReading unSyncedRealmReading : unSyncedRealmReadings) {
            createUserReading(unSyncedRealmReading, false);
        }
    }

    private boolean createUserReading(RealmReading unSyncedRealmReading, boolean deleteReadingIfUserNotFound) {

        String userKey = unSyncedRealmReading.getUser() != null ? unSyncedRealmReading.getUser().getKey() : null;
        if (userKey == null) {
//            TODO log Delete reading
            if (deleteReadingIfUserNotFound) {
                RealmHandler.deleteUserReading(unSyncedRealmReading);
            }
            return false;
        }
        String ngoKey = unSyncedRealmReading.getNgo();
        String measurementKey = unSyncedRealmReading.getMeasurement().getKey();
        String trainingSessionUUID = unSyncedRealmReading.getTrainingSessionUUID();

        String evaluationResourceUUID = unSyncedRealmReading.getEvaluationResource() != null ? unSyncedRealmReading.getEvaluationResource().getUuid() : null;
        String value = unSyncedRealmReading.getValue();
        String recordedAtDateTimeTZString = DateUtil.getTZDateString(unSyncedRealmReading.getRecordedAt());

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("user", userKey);
            paramObject.put("ngo", ngoKey);
            paramObject.put("measurement", measurementKey);
            paramObject.put("training_session_uuid", trainingSessionUUID);
            paramObject.put("evaluation_resource_uuid", evaluationResourceUUID);
            paramObject.put("value", value);
            paramObject.put("recorded_at", recordedAtDateTimeTZString);

            Response<UserReading> response = ApiClient.getApiInterface(getContext())
                    .createUserReading(paramObject.toString()).execute();
            if (response.isSuccessful()) {
                UserReading userReading = response.body();
                if (userReading == null) {
                    return false;
                }

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                unSyncedRealmReading.setKey(userReading.getKey());
                realm.copyToRealmOrUpdate(unSyncedRealmReading);
                realm.commitTransaction();
                realm.close();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean updateEvaluationResourceOnServer(RealmEvaluationResource realmEvaluationResource) {

        String realmEvaluationResourceKey = realmEvaluationResource.getKey();
        JSONObject paramObject = Util.getJSONObjectForEvaluationResource(realmEvaluationResource);
        if (paramObject == null) {
            return false;
        }

        try {
            Response<EvaluationResource> response = ApiClient.getApiInterface(getContext())
                    .updateEvaluationResource(realmEvaluationResourceKey, paramObject.toString()).execute();
            if (response.isSuccessful()) {
                EvaluationResource evaluationResource = response.body();
                return evaluationResource != null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    private boolean createEvaluationResourceOnServer(RealmEvaluationResource realmEvaluationResource) {

        JSONObject paramObject = Util.getJSONObjectForEvaluationResource(realmEvaluationResource);
        if (paramObject == null) {
            return false;
        }
        try {

            Response<EvaluationResource> response = ApiClient.getApiInterface(getContext())
                    .createEvaluationResource(paramObject.toString()).execute();
            if (response.isSuccessful()) {
                EvaluationResource evaluationResource = response.body();
                if (evaluationResource == null) {
                    return false;
                }

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realmEvaluationResource.setKey(evaluationResource.getKey());
                realm.copyToRealmOrUpdate(realmEvaluationResource);
                realm.commitTransaction();
                realm.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean fetchEvaluationResources(String userKey) {
        try {
            Response<List<EvaluationResource>> response = ApiClient.getApiInterface(getContext()).getEvaluationResources(userKey).execute();
            if (response.isSuccessful()) {
                List<EvaluationResource> evaluationResources = response.body();
                if (CollectionUtils.isEmpty(evaluationResources)) {
                    return true;
                }

                Realm realm = Realm.getDefaultInstance();
                for (EvaluationResource evaluationResource : evaluationResources) {
                    RealmUser realmUser;
                    RealmGroup realmGroup;
                    RealmEvaluationResource realmEvaluationResource;
                    switch (Util.getEvaluationResourceType(evaluationResource.getType())) {

                        case USER:
                            realmUser = RealmHandler.getUser(evaluationResource.getUserKey());
                            if (realmUser == null) {
//                              TODO handle this error
//                                Maybe delete the evaluation resource on the server
                                continue;
                            }
                            realm.beginTransaction();
                            realmEvaluationResource = new RealmEvaluationResource(evaluationResource, realmUser);
                            realm.copyToRealmOrUpdate(realmEvaluationResource);
                            realm.commitTransaction();
                            break;
                        case GROUP:
                            realmGroup = RealmHandler.getGroup(evaluationResource.getGroupKey());
                            if (realmGroup == null) {
//                              TODO handle this error
                                // Maybe delete the evaluation resource on the server

                                continue;
                            }
                            realm.beginTransaction();
                            realmEvaluationResource = new RealmEvaluationResource(evaluationResource, realmGroup);
                            realm.copyToRealmOrUpdate(realmEvaluationResource);
                            realm.commitTransaction();
                            break;
                        default:
                    }

                }

                realm.close();
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.DAILY_PLANNER);
                contentResolver.notifyChange(uri, null);
                appLogger.logDebug("notifyChange EvaluationResource");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void syncFiles() {

        Realm realm = Realm.getDefaultInstance();
        List<RealmResource> realmResources = RealmHandler.getAllActiveResources();
        for (RealmResource realmResource : realmResources) {
            switch (realmResource.getType()) {
                case CURRICULUM: {
                    // Check for files inside all the sessions
                    List<String> resourceIDsToDownload = new ArrayList<>();
                    Curriculum curriculum = Util.convertRealmResourceToCurriculumForSyncAdapter(realmResource);
                    appLogger.logInformation("YOLO Curriculum " + curriculum.getLabel());
                    for (Day day : curriculum.getDays()) {
                        appLogger.logInformation("YOLO Day " + day.getLabel());
                        for (TrainingSession trainingSession : day.getSessions()) {
                            appLogger.logInformation("YOLO TrainingSession " + trainingSession.getLabel());
                            for (File file : trainingSession.getFiles()) {
                                appLogger.logInformation("YOLO File " + file.getLabel());
                                if (!resourceIDsToDownload.contains(file.getKey())) {
                                    resourceIDsToDownload.add(file.getKey());
                                }
                            }
                        }
                    }
                    appLogger.logInformation("YOLO resourceIDsToDownload " + resourceIDsToDownload);
                    for (String resourceKey : resourceIDsToDownload) {
                        fetchResourcesByID(resourceKey);
                    }
                }
                break;

                case FILE:
                    // Check if file downloaded and save to location
                    processFileResource(realmResource);
                    break;

                case REGISTRATION_FORM:
                case TRAINING_SESSION: {
                    // Check for files inside the session
                    TrainingSession trainingSession = Util.convertRealmResourceToTrainingSessionForSyncAdapter(realmResource);
                    List<String> resourceIDsToDownload = new ArrayList<>();

                    for (File file : trainingSession.getFiles()) {
                        if (!resourceIDsToDownload.contains(file.getKey())) {
                            resourceIDsToDownload.add(file.getKey());
                        }
                    }
                    for (String resourceKey : resourceIDsToDownload) {
                        fetchResourcesByID(resourceKey);
                    }
                }
                break;

                default:
                    break;
            }
        }
        realm.close();
    }

    private void fetchResourcesByID(String resourceKey) {
        appLogger.logInformation("fetchResourcesByID " + resourceKey);
        try {
            Response<Resource> response = ApiClient.getApiInterface(getContext()).getResource(resourceKey).execute();
            if (response.isSuccessful()) {
                Resource resource = response.body();
                if (resource == null) {
                    return;
                }
                appLogger.logInformation("Resource name : " + resource.getLabel());
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                RealmResource realmResource = new RealmResource(resource);
                realmResource = realm.copyToRealmOrUpdate(realmResource);
                realm.commitTransaction();
                realm.close();
                processFileResource(realmResource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFileResource(RealmResource realmResource) {
        File file = Util.convertRealmResourceToFile(realmResource);
        downloadFile(file);
    }

    private boolean downloadFile(File file) {
        java.io.File newResourceFile;
        try {
            // Check if file already present

            newResourceFile = Util.getFileInsideBOSDirectory(file, getContext());
            if (newResourceFile.exists()) {
                appLogger.logInformation("file exists");
                return true;
            }

            URL url = new URL(file.getUrl());//Create Download URl
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();

//            If Connection response is not OK then show Logs
            if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                appLogger.logError("Server returned HTTP " + c.getResponseCode()
                        + " " + c.getResponseMessage());
                return false;
            }


            //Create New File if not present
            if (!newResourceFile.exists()) {
                newResourceFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(newResourceFile);//Get OutputStream for NewFile Location

            InputStream is = c.getInputStream();//Get InputStream for connection

            byte[] buffer = new byte[1024];//Set buffer type
            int len1 = 0;//init length
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);//Write new file
            }

            //Close all connection after doing task
            fos.close();
            is.close();
        } catch (Exception e) {

            //Read exception if something went wrong
            e.printStackTrace();
            newResourceFile = null;
        }
        return false;
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
