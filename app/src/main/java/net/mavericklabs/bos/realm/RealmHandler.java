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
import net.mavericklabs.bos.model.User;
import net.mavericklabs.bos.model.UserReading;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.Constants;
import net.mavericklabs.bos.utils.UserRole;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

import static net.mavericklabs.bos.utils.Constants.REGISTRATION_FORM;

public class RealmHandler {
    static AppLogger appLogger = new AppLogger("RealmHandler");

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

    public static List<RealmEvaluationResource> getUnevaluatedOrUnSyncedResources() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmEvaluationResource> realmEvaluationResources =
                realm.where(RealmEvaluationResource.class)
                        .equalTo("isEvaluated", false)
                        .or()
                        .equalTo("isSynced", false)
                        .sort("lastModificationTime", Sort.DESCENDING)
                        .findAll();
        realm.close();
        return realmEvaluationResources;

    }

    public static RealmResource getResourceByKey(String resourceKey) {
        Realm realm = Realm.getDefaultInstance();
        RealmResource realmResource = realm.where(RealmResource.class)
                .equalTo("key", resourceKey).findFirst();
        realm.close();
        return realmResource;
    }

    public static RealmMeasurement getMeasurementFromKey(String measurementKey) {
        Realm realm = Realm.getDefaultInstance();
        RealmMeasurement realmMeasurement = realm.where(RealmMeasurement.class)
                .equalTo("key", measurementKey).findFirst();
        realm.close();
        return realmMeasurement;
    }

    public static RealmGroup getGroup(String groupKey) {
        Realm realm = Realm.getDefaultInstance();
        RealmGroup realmGroup = realm.where(RealmGroup.class)
                .equalTo("key", groupKey).findFirst();
        realm.close();
        return realmGroup;
    }

    public static void copyToRealm(RealmEvaluationResource realmEvaluationResource) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(realmEvaluationResource);
        realm.commitTransaction();
        realm.close();
    }

    public static RealmEvaluationResource getEvaluationResourceByUUID(String uuid) {
        Realm realm = Realm.getDefaultInstance();
        RealmEvaluationResource realmEvaluationResource = realm.where(RealmEvaluationResource.class)
                .equalTo("uuid", uuid)
                .findFirst();
        realm.close();
        return realmEvaluationResource;
    }

    public static RealmUser getSelfRealmUser() {
        LoginResponse loginResponse = getLoginResponse();
        Realm realm = Realm.getDefaultInstance();
        RealmUser realmUser = realm.where(RealmUser.class)
                .equalTo("key", loginResponse.getUserKey())
                .findFirst();
        realm.close();
        return realmUser;

    }

    public static RealmUser getAthleteByUUID(String athleteUUID) {
        Realm realm = Realm.getDefaultInstance();
        RealmUser realmUser = realm.where(RealmUser.class)
                .equalTo("uuid", athleteUUID)
                .findFirst();
        realm.close();
        return realmUser;
    }

    public static List<RealmReading> getReadingsForAthlete(RealmUser realmUser) {
        Realm realm = Realm.getDefaultInstance();
        List<RealmReading> realmReading = realm.where(RealmReading.class)
                .equalTo("user.uuid", realmUser.getUuid())
                .sort("creationTime", Sort.DESCENDING)
                .findAll();
        realm.close();
        return realmReading;
    }

    public static List<RealmResource> getResourcesForAthlete(String athleteKey) {
        Realm realm = Realm.getDefaultInstance();
        List<RealmResource> realmResources = realm.where(RealmResource.class)
                .equalTo("user.key", athleteKey)
                .sort("creationTime", Sort.DESCENDING)
                .findAll();
        realm.close();
        return realmResources;
    }

    public static RealmUser findUserOrCreate(User user) {
        Realm realm = Realm.getDefaultInstance();
        RealmUser realmUser = realm.where(RealmUser.class)
                .equalTo("key", user.getKey())
                .findFirst();
        if (realmUser == null) {
            realm.beginTransaction();
            realmUser = new RealmUser(user, new RealmList<RealmResource>());
            realmUser = realm.copyToRealmOrUpdate(realmUser);
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            realmUser.setFirstName(user.getFirstName());
            realmUser.setMiddleName(user.getMiddleName());
            realmUser.setLastName(user.getLastName());
            realmUser.setEmail(user.getEmail());
            realmUser.setActive(user.getActive());
            realmUser.setRole(user.getRole());
            realmUser = realm.copyToRealmOrUpdate(realmUser);
            realm.commitTransaction();
        }
        realm.close();
        return realmUser;
    }

    public static List<RealmReading> getUnSyncedReadings() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmReading> realmReadings = realm.where(RealmReading.class)
                .isNull("key")
                .sort("creationTime", Sort.DESCENDING)
                .findAll();
        realm.close();
        return realmReadings;
    }

    public static List<RealmReading> getUnSyncedReadingsForEvaluationResource(RealmEvaluationResource realmEvaluationResource) {
        Realm realm = Realm.getDefaultInstance();
        List<RealmReading> realmReadings = realm.where(RealmReading.class)
                .isNull("key")
                .equalTo("evaluationResource.uuid", realmEvaluationResource.getUuid())
                .findAll();
        realm.close();
        appLogger.logInformation(String.valueOf(realmReadings.size()));
        return realmReadings;
    }

    public static boolean areReadingsForEvaluatedResourceSynced(RealmEvaluationResource realmEvaluationResource) {
        return getUnSyncedReadingsForEvaluationResource(realmEvaluationResource).size() == 0;
    }

    public static List<RealmEvaluationResource> getUnSyncedEvaluatedResources() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmEvaluationResource> realmEvaluationResources =
                realm.where(RealmEvaluationResource.class)
                        .equalTo("isSynced", false)
                        .findAll();
        realm.close();
        return realmEvaluationResources;

    }

    public static RealmResource getAthleteRegistrationForm() {
        Realm realm = Realm.getDefaultInstance();
        RealmResource realmResource = realm.where(RealmResource.class)
                .equalTo("type", REGISTRATION_FORM)
                .findFirst();
        realm.close();
        return realmResource;
    }

    public static List<RealmUser> getOfflineAthletes() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmUser> offlineAthletes =
                realm.where(RealmUser.class)
                        .isNull("key")
                        .equalTo("role", UserRole.ATHLETE.label)
                        .findAll();
        realm.close();
        return offlineAthletes;

    }

    public static List<RealmReading> getBaselineForAthlete(RealmUser offlineAthlete) {
        Realm realm = Realm.getDefaultInstance();
        List<RealmReading> realmReadings = realm.where(RealmReading.class)
                .isNull("key")
                .equalTo("user.uuid", offlineAthlete.getUuid())
                .isNull("resource")
                .findAll();
        realm.close();
        return realmReadings;

    }

    public static List<RealmReading> getUnSyncedBaselinesForAthletes() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmReading> realmReadings = realm.where(RealmReading.class)
                .isNull("key")
                .findAll();
        realm.close();
        return realmReadings;

    }

    public static RealmReading findReadingOrCreate(UserReading userReading, RealmUser realmUser,
                                                   RealmMeasurement realmMeasurement) {
        Realm realm = Realm.getDefaultInstance();
        RealmReading realmReading = realm.where(RealmReading.class)
                .equalTo("key", userReading.getKey())
                .findFirst();
        if (realmReading == null) {
            realm.beginTransaction();
            realmReading = new RealmReading(userReading, realmUser, realmMeasurement);
            realmReading = realm.copyToRealmOrUpdate(realmReading);
            realm.commitTransaction();
        }
        realm.close();
        return realmReading;
    }
}
