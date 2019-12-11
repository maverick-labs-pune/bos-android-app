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

import net.mavericklabs.bos.model.LoginResponse;
import net.mavericklabs.bos.utils.Constants;

import java.util.List;

import io.realm.Realm;

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
        return value;

    }

    public static List<RealmMeasurement> getAllMeasurements() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmMeasurement> realmMeasurements = realm.where(RealmMeasurement.class).findAll();
        realm.close();
        return realmMeasurements;

    }

    public static List<RealmResource> getAllResources() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmResource> realmResource = realm.where(RealmResource.class).findAll();
        realm.close();
        return realmResource;

    }

    public static List<RealmGroup> getAllGroups() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmGroup> realmGroups = realm.where(RealmGroup.class).findAll();
        realm.close();
        return realmGroups;

    }

    public static List<RealmUser> getAllUsers() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmUser> realmUsers = realm.where(RealmUser.class).findAll();
        realm.close();
        return realmUsers;

    }

    public static List<RealmUser> getAllAthletes() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmUser> realmUsers = realm.where(RealmUser.class).equalTo("role",
                Constants.ATHLETE).findAll();
        realm.close();
        return realmUsers;

    }

    public static List<RealmEvaluationResource> getEvaluationResources() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmEvaluationResource> realmEvaluationResources =
                realm.where(RealmEvaluationResource.class).findAll();
        realm.close();
        return realmEvaluationResources;

    }

    public static RealmResource getResourceByKey(String resourceKey) {
        Realm realm = Realm.getDefaultInstance();
        RealmResource realmResource = realm.where(RealmResource.class)
                .equalTo("key",resourceKey).findFirst();
        realm.close();
        return realmResource;
    }

    public static RealmMeasurement getMeasurementFromKey(String measurementKey) {
        Realm realm = Realm.getDefaultInstance();
        RealmMeasurement realmMeasurement = realm.where(RealmMeasurement.class)
                .equalTo("key",measurementKey).findFirst();
        realm.close();
        return realmMeasurement;
    }

    public static RealmGroup getGroup(String groupKey) {
        Realm realm = Realm.getDefaultInstance();
        RealmGroup realmGroup = realm.where(RealmGroup.class)
                .equalTo("key",groupKey).findFirst();
        realm.close();
        return realmGroup;
    }
}
