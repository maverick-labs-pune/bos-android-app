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

package net.mavericklabs.bos;

import android.accounts.Account;
import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;

import net.mavericklabs.bos.realm.RealmMigration;
import net.mavericklabs.bos.sync.SyncAdapter;
import net.mavericklabs.bos.utils.Constants;
import net.mavericklabs.bos.utils.SharedPreferenceUtil;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BosApplication extends Application {
    private static final String NET_MAVERICKLABS_BOS_DATASYNC_PROVIDER = "net.mavericklabs.bos.datasync.provider";
    public static final String MEASUREMENTS = "measurements";
    public static final String SYNC_COMPLETED = "sync_stop";
    public static final String ATHLETES = "athletes";
    public static final String GROUPS = "groups";
    public static final String TRANSLATIONS = "translations";
    public static final String RESOURCES = "resources";
    public static final String DAILY_PLANNER = "daily_planner";
    private static final String BASE_URL = "content://" + NET_MAVERICKLABS_BOS_DATASYNC_PROVIDER + "/";
    public static final Uri BASE_URI = Uri.parse(BASE_URL);

    @Override
    public void onCreate() {
        super.onCreate();
        initRealm();
        String locale = SharedPreferenceUtil.getLocale(this);
        if (locale == null)
            SharedPreferenceUtil.setStringPreference(this, "locale", Constants.en_INLocale);

        SyncAdapter.createSyncAccount(getApplicationContext());
        String AUTHORITY = getResources().getString(R.string.content_authority);
        Account ACCOUNT = SyncAdapter.getAccount(getApplicationContext());
        ContentResolver.setSyncAutomatically(ACCOUNT, AUTHORITY, true);
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("bosapplication.realm")
                .schemaVersion(1)
                .migration(new RealmMigration())
                .build();
        Realm.setDefaultConfiguration(config);
    }


    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }

}
