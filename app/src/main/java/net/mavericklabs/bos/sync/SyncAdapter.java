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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.mavericklabs.bos.BosApplication;
import net.mavericklabs.bos.model.RealmTranslation;
import net.mavericklabs.bos.util.Constants;
import net.mavericklabs.bos.util.Logger;
import net.mavericklabs.bos.R;
import net.mavericklabs.bos.model.LoginResponse;
import net.mavericklabs.bos.model.Translation;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.retrofit.ApiClient;
import net.mavericklabs.bos.retrofit.ApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.ACCOUNT_SERVICE;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_EVERYTHING = 0;
    public static final int SYNC_BOOKS = 1;
    public static final int SYNC_SESSIONS = 2;
    public static final int SYNC_TRANSLATIONS = 3;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static boolean requestSync(Context context, int syncType) {
        Logger.d("requestSync syncAdapter");
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

        Logger.d("onPerformSync");
        LoginResponse loginResponse = RealmHandler.getLoginResponse();
        if (loginResponse == null) {
            Logger.d("login response null");
            notifySyncStopped();
            return;
        }
        int syncType = extras.getInt("syncType");
        switch (syncType) {
            case SYNC_TRANSLATIONS:
            case SYNC_EVERYTHING:
                syncTranslations();
                notifySyncStopped();
                break;
        }
    }


    private void notifySyncStopped() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.SYNC_COMPLETED);
        contentResolver.notifyChange(uri, null);
        Logger.d("notifyChange Sync stopped");
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
                        Logger.d(String.valueOf(entry.getValue()));
                    }
                    realm.commitTransaction();
                    realm.close();
                }
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri uri = Uri.withAppendedPath(BosApplication.BASE_URI, BosApplication.TRANSLATIONS);
                contentResolver.notifyChange(uri, null);
                Logger.d("notifyChange TRANSLATIONS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
