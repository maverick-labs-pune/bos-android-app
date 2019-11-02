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

package net.mavericklabs.bos.realm;

import net.mavericklabs.bos.model.RealmTranslation;
import net.mavericklabs.bos.util.Constants;
import net.mavericklabs.bos.util.DateUtil;
import net.mavericklabs.bos.util.Logger;
import net.mavericklabs.bos.model.LoginResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RealmHandler {

    public static String getAccessToken() {
        Realm realm = Realm.getDefaultInstance();
        LoginResponse loginResponse = realm.where(LoginResponse.class).findFirst();
        String accessToken = null;
        if (loginResponse != null) {
            LoginResponse obj = realm.copyFromRealm(loginResponse);
            accessToken = obj.getToken();
        }
        realm.close();
        return accessToken;
    }

    public static LoginResponse getLoginResponse() {
        Realm realm = Realm.getDefaultInstance();
        LoginResponse loginResponse = realm.where(LoginResponse.class).findFirst();
        LoginResponse obj = null;
        if (loginResponse != null) {
            obj = realm.copyFromRealm(loginResponse);

        }
        realm.close();
        return obj;
    }

    public static void clearRealmDatabase() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
    }


    public static String getTranslation(String locale, String key) {
        Realm realm = Realm.getDefaultInstance();
        RealmTranslation realmTranslation = realm.where(RealmTranslation.class)
                .equalTo("locale", locale)
                .equalTo("key", key).findFirst();
        String value = key;
        if (realmTranslation != null) {
            value = realmTranslation.getValue();
        }
        realm.close();
        Logger.d(value);
        return value;

    }

}
