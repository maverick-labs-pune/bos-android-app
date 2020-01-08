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

package net.mavericklabs.bos.utils;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.mavericklabs.bos.model.UserReading;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Day;
import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmReading;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.realm.RealmUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static net.mavericklabs.bos.utils.Constants.CURRICULUM;
import static net.mavericklabs.bos.utils.Constants.TRAINING_SESSION;


public class Util {

    public static void setEmptyMessageIfNeeded(List data, RecyclerView recyclerView, TextView emptyView) {
        if (data.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public static void setEmptyMessageIfNeeded(Object data, RecyclerView recyclerView, TextView emptyView) {
        if (data == null) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public static Curriculum convertRealmResourceToCurriculum(RealmResource realmResource) {
        Gson gson = new Gson();
        Curriculum curriculum = gson.fromJson(realmResource.getData(), Curriculum.class);
        curriculum.setKey(realmResource.getKey());
        curriculum.setLabel(realmResource.getLabel());
        curriculum.setDescription(realmResource.getDescription());
        return curriculum;
    }

    public static TrainingSession convertRealmResourceToTrainingSession(RealmResource realmResource) {
        Gson gson = new Gson();
        TrainingSession trainingSession = gson.fromJson(realmResource.getData(), TrainingSession.class);
        trainingSession.setKey(realmResource.getKey());
        trainingSession.setLabel(realmResource.getLabel());
        trainingSession.setDescription(realmResource.getDescription());
        return trainingSession;
    }

    public static Curriculum convertRealmResourceToCurriculum(RealmEvaluationResource realmEvaluationResource) {
        Gson gson = new Gson();
        Curriculum curriculum = gson.fromJson(realmEvaluationResource.getData(), Curriculum.class);
        curriculum.setUuid(realmEvaluationResource.getUuid());
        curriculum.setKey(realmEvaluationResource.getResource().getKey());
        curriculum.setLabel(realmEvaluationResource.getResource().getLabel());
        curriculum.setDescription(realmEvaluationResource.getResource().getDescription());
        return curriculum;
    }

    public static TrainingSession convertRealmResourceToTrainingSession(RealmEvaluationResource realmEvaluationResource) {
        Gson gson = new Gson();
        TrainingSession trainingSession = gson.fromJson(realmEvaluationResource.getData(), TrainingSession.class);
        trainingSession.setUuid(realmEvaluationResource.getUuid());
        trainingSession.setKey(realmEvaluationResource.getResource().getKey());
        trainingSession.setLabel(realmEvaluationResource.getResource().getLabel());
        trainingSession.setDescription(realmEvaluationResource.getResource().getDescription());
        return trainingSession;
    }

    public static UserRole getRole(String role) {
        if (role.toLowerCase().equals(UserRole.ADMIN.label)) {
            return UserRole.ADMIN;
        }
        if (role.toLowerCase().equals(UserRole.COACH.label)) {
            return UserRole.COACH;
        }
        if (role.toLowerCase().equals(UserRole.ATHLETE.label)) {
            return UserRole.ATHLETE;
        }
        return UserRole.UNKNOWN;
    }

    public static EvaluationResourceType getEvaluationResourceType(String evaluationResourceType) {
        if (evaluationResourceType.toLowerCase().equals(EvaluationResourceType.USER.label)) {
            return EvaluationResourceType.USER;
        }
        if (evaluationResourceType.toLowerCase().equals(EvaluationResourceType.GROUP.label)) {
            return EvaluationResourceType.GROUP;
        }
        return EvaluationResourceType.UNKNOWN;
    }

    public static ActivityMode getActivityMode(String activityMode) {
        if (activityMode.toLowerCase().equals(ActivityMode.READ.label)) {
            return ActivityMode.READ;
        }
        if (activityMode.toLowerCase().equals(ActivityMode.EVALUATION.label)) {
            return ActivityMode.EVALUATION;
        }
        return ActivityMode.UNKNOWN;
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String convertRealmResourceDataToRealmEvaluationResourceData(RealmResource realmResource,
                                                                               String uuid,
                                                                               boolean isInit,
                                                                               Date currentTime) {

        switch (realmResource.getType().toLowerCase()) {
            case CURRICULUM:
                Curriculum curriculum = convertRealmResourceToCurriculum(realmResource);
                curriculum.setLastModificationTime(currentTime);
                if (isInit) {
                    curriculum.setUuid(uuid);
                    curriculum.setEvaluated(false);
                    for (Day day : curriculum.getDays()) {
                        day.setUuid(getRandomUUID());
                        day.setEvaluated(false);
                        day.setLastModificationTime(currentTime);

                        for (TrainingSession trainingSession : day.getSessions()) {
                            trainingSession.setUuid(getRandomUUID());
                            trainingSession.setEvaluated(false);
                            trainingSession.setLastModificationTime(currentTime);
                            for (Measurement measurement : trainingSession.getMeasurements()) {
                                measurement.setReading("");
                            }
                        }
                    }
                }
                return Util.convertToJson(curriculum);
            case TRAINING_SESSION:
                TrainingSession trainingSession = convertRealmResourceToTrainingSession(realmResource);
                trainingSession.setLastModificationTime(currentTime);
                if (isInit) {
                    trainingSession.setUuid(uuid);
                    trainingSession.setEvaluated(false);
                    for (Measurement measurement : trainingSession.getMeasurements()) {
                        measurement.setReading("");
                    }
                }
                return Util.convertToJson(trainingSession);
        }
        return null;
    }

    public static List<RealmUser> getAthletes(List<RealmUser> users) {
        List<RealmUser> athletes = new ArrayList<>();
        for (RealmUser user : users) {
            if (Util.getRole(user.getRole()) == UserRole.ATHLETE) {
                athletes.add(user);
            }
        }
        return athletes;
    }

    public static String updateCurriculum(Curriculum curriculum, String uuid,
                                          List<Measurement> measurementsWithReadings) {
        // Find the day and training session in the curriculum
        Day dayToBeCheckedIfEvaluated = null;
        Date modifiedTime = DateUtil.getCurrentTime();
        List<Day> days = curriculum.getDays();
        for (Day day : days) {
            for (TrainingSession trainingSession : day.getSessions()) {
                if (trainingSession.getUuid().equals(uuid)) {
                    // Found the day and training session
                    trainingSession.setMeasurements(measurementsWithReadings);
                    trainingSession.setEvaluated(true);
                    trainingSession.setLastModificationTime(modifiedTime);
                    dayToBeCheckedIfEvaluated = day;
                    break;
                }
            }
        }
        if (dayToBeCheckedIfEvaluated != null) {
            boolean isDayEvaluated = true;
            for (TrainingSession trainingSession : dayToBeCheckedIfEvaluated.getSessions()) {
                if (!trainingSession.isEvaluated()) {
                    isDayEvaluated = false;
                    break;
                }
            }
            dayToBeCheckedIfEvaluated.setEvaluated(isDayEvaluated);
            dayToBeCheckedIfEvaluated.setLastModificationTime(modifiedTime);
            if (isDayEvaluated) {
                // Check if curriculum is evaluated
                boolean isCurriculumEvaluated = true;
                for (Day day : days) {
                    if (!day.isEvaluated()) {
                        isCurriculumEvaluated = false;
                        break;
                    }
                }
                curriculum.setEvaluated(isCurriculumEvaluated);
            }
            curriculum.setLastModificationTime(modifiedTime);
        }
        return convertToJson(curriculum);
    }

    public static String updateTrainingSession(TrainingSession trainingSession,
                                               List<Measurement> measurementsWithReadings) {
        // Find the day and training session in the curriculum
        Date modifiedTime = DateUtil.getCurrentTime();
        // Found the day and training session
        trainingSession.setMeasurements(measurementsWithReadings);
        trainingSession.setEvaluated(true);
        trainingSession.setLastModificationTime(modifiedTime);
        return convertToJson(trainingSession);
    }

    public static String convertToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);

    }
}
