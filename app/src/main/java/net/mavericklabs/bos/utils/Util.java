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

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Day;
import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.realm.RealmUser;

import java.util.ArrayList;
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

    public static Curriculum convertRealmResourceToCurriculum(RealmEvaluationResource realmEvaluationResource) {
        Gson gson = new Gson();
        Curriculum curriculum = gson.fromJson(realmEvaluationResource.getData(), Curriculum.class);
        curriculum.setUuid(realmEvaluationResource.getUuid());
        curriculum.setKey(realmEvaluationResource.getResource().getKey());
        curriculum.setLabel(realmEvaluationResource.getResource().getLabel());
        curriculum.setDescription(realmEvaluationResource.getResource().getDescription());
        return curriculum;
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

    public static String getRandomUUID(){
        return UUID.randomUUID().toString();
    }

    public static String convertRealmResourceDataToRealmEvaluationResourceData(RealmResource realmResource, String uuid, boolean isInit) {

        switch (realmResource.getType().toLowerCase()) {
            case CURRICULUM:
                Curriculum curriculum = convertRealmResourceToCurriculum(realmResource);
                if (isInit){
                    curriculum.setUuid(uuid);
                    curriculum.setEvaluated(false);
                    for (Day day : curriculum.getDays()){
                        day.setUuid(getRandomUUID());
                        day.setEvaluated(false);
                        for (TrainingSession trainingSession : day.getSessions()){
                            trainingSession.setUuid(getRandomUUID());
                            trainingSession.setEvaluated(false);
                            for (Measurement measurement : trainingSession.getMeasurements()){
                                measurement.setReading("");
                            }
                        }
                    }
                }
                return Util.convertToJson(curriculum);
            case TRAINING_SESSION:
                break;
        }
        return null;
    }

    public static List<RealmUser> getAthletes(List<RealmUser> users) {
        List<RealmUser> athletes = new ArrayList<>();
        for (RealmUser user : users){
            if (Util.getRole(user.getRole()) == UserRole.ATHLETE){
                athletes.add(user);
            }
        }
        return athletes;
    }

    public static String updateCurriculum(Curriculum curriculum, String uuid,
                                          List<Measurement> measurementsWithReadings) {
        // Find the day and training session in the curriculum
        Day dayToBeCheckedIfEvaluated = null;
        List<Day> days = curriculum.getDays();
        for (Day day : days){
            for (TrainingSession trainingSession : day.getSessions()){
                if (trainingSession.getUuid().equals(uuid)){
                    // Found the day and training session
                    trainingSession.setMeasurements(measurementsWithReadings);
                    trainingSession.setEvaluated(true);
                    dayToBeCheckedIfEvaluated = day;
                    break;
                }
            }
        }
        if (dayToBeCheckedIfEvaluated != null){
            boolean isDayEvaluated = true;
            for (TrainingSession trainingSession : dayToBeCheckedIfEvaluated.getSessions()){
                if (!trainingSession.isEvaluated()){
                    isDayEvaluated = false;
                    break;
                }
            }
            dayToBeCheckedIfEvaluated.setEvaluated(isDayEvaluated);
        }
        return convertToJson(curriculum);
    }

    public static String convertToJson(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);

    }
}
