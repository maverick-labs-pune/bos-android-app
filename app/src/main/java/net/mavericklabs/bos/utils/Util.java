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

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Day;
import net.mavericklabs.bos.object.File;
import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmGroup;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.realm.RealmUser;

import org.json.JSONException;
import org.json.JSONObject;

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
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
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

    private static void convertFilesInsideTrainingSession(TrainingSession trainingSession) {
        List<File> convertedFiles = new ArrayList<>();
        for (File file : trainingSession.getFiles()
        ) {
            RealmResource fileResource = RealmHandler.getResourceByKey(file.getKey());
            if (fileResource != null) {
                File convertedFile = convertRealmResourceToFile(fileResource);
                convertedFiles.add(convertedFile);
            }
        }
        trainingSession.setFiles(convertedFiles);
    }

    private static void convertFilesInsideCurriculum(Curriculum curriculum) {
        for (Day day : curriculum.getDays()) {
            for (TrainingSession trainingSession : day.getSessions()) {
                convertFilesInsideTrainingSession(trainingSession);
            }
        }

    }

    public static Curriculum convertRealmResourceToCurriculum(RealmResource realmResource) {
        Gson gson = new Gson();
        Curriculum curriculum = gson.fromJson(realmResource.getData(), Curriculum.class);
        curriculum.setLabel(realmResource.getLabel());
        curriculum.setDescription(realmResource.getDescription());
        convertFilesInsideCurriculum(curriculum);
        return curriculum;
    }

    public static TrainingSession convertRealmResourceToTrainingSession(RealmResource realmResource) {
        Gson gson = new Gson();
        TrainingSession trainingSession = gson.fromJson(realmResource.getData(), TrainingSession.class);
        trainingSession.setLabel(realmResource.getLabel());
        trainingSession.setDescription(realmResource.getDescription());
        convertFilesInsideTrainingSession(trainingSession);
        return trainingSession;
    }

    public static Curriculum convertRealmResourceToCurriculum(RealmEvaluationResource realmEvaluationResource) {
        Gson gson = new Gson();
        Curriculum curriculum = gson.fromJson(realmEvaluationResource.getData(), Curriculum.class);
        curriculum.setUuid(realmEvaluationResource.getUuid());
        curriculum.setLabel(realmEvaluationResource.getLabel());
        curriculum.setDescription(realmEvaluationResource.getDescription());
        convertFilesInsideCurriculum(curriculum);
        return curriculum;
    }

    public static TrainingSession convertRealmResourceToTrainingSession(RealmEvaluationResource realmEvaluationResource) {
        Gson gson = new Gson();
        TrainingSession trainingSession = gson.fromJson(realmEvaluationResource.getData(), TrainingSession.class);
        trainingSession.setUuid(realmEvaluationResource.getUuid());
        trainingSession.setLabel(realmEvaluationResource.getLabel());
        trainingSession.setDescription(realmEvaluationResource.getDescription());
        convertFilesInsideTrainingSession(trainingSession);
        return trainingSession;
    }

    public static TrainingSession convertResourceToTrainingSession(Resource resource) {
        Gson gson = new Gson();
        TrainingSession trainingSession = gson.fromJson(resource.getData(), TrainingSession.class);
        trainingSession.setUuid(getRandomUUID());
        trainingSession.setLabel(resource.getLabel());
        trainingSession.setDescription(resource.getDescription());
        convertFilesInsideTrainingSession(trainingSession);
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

    public static MeasurementInputType getMeasurementInputType(String measurementType) {
        if (measurementType.toLowerCase().equals(MeasurementInputType.TEXT.label)) {
            return MeasurementInputType.TEXT;
        }
        if (measurementType.toLowerCase().equals(MeasurementInputType.NUMERIC.label)) {
            return MeasurementInputType.NUMERIC;
        }
        if (measurementType.toLowerCase().equals(MeasurementInputType.BOOLEAN.label)) {
            return MeasurementInputType.BOOLEAN;
        }
        return MeasurementInputType.UNKNOWN;
    }

    public static Gender getGender(String gender) {
        if (gender.toLowerCase().equals(Gender.MALE.label)) {
            return Gender.MALE;
        }
        if (gender.toLowerCase().equals(Gender.FEMALE.label)) {
            return Gender.FEMALE;
        }
        return Gender.UNKNOWN;
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

    public static void removeResourceFromList(List<RealmResource> realmResources,
                                              RealmResource realmResourceToBeRemoved) {
        for (RealmResource realmResource :
                realmResources) {
            if (realmResource.getKey().equals(realmResourceToBeRemoved.getKey())) {
                realmResources.remove(realmResource);
                return;
            }
        }
    }

    public static void removeGroupFromList(List<RealmGroup> realmGroups,
                                           RealmGroup realmGroupToBeRemoved) {
        for (RealmGroup realmGroup :
                realmGroups) {
            if (realmGroup.getKey().equals(realmGroupToBeRemoved.getKey())) {
                realmGroups.remove(realmGroup);
                return;
            }
        }
    }

    public static void setImageViewBasedOnGender(ImageView imageView, RealmUser realmUser) {
        switch (Util.getGender(realmUser.getGender())) {
            case MALE:
                imageView.setImageResource(R.drawable.ic_athlete_male);
                break;
            case FEMALE:
                imageView.setImageResource(R.drawable.ic_athlete_female);
                break;
        }
    }

    public static String getGenderLabel(String genderLabel) {
        Gender gender = Util.getGender(genderLabel);
        switch (gender) {
            case MALE:
                return "Male";
            case FEMALE:
                return "Female";
            case UNKNOWN:
                return "";
        }
        return "";
    }

    public static int computeCurriculumProgress(Curriculum curriculum) {
        int totalNumberOfTrainingSessions = 0;
        int evaluatedTrainingSessions = 0;

        for (Day day : curriculum.getDays()) {
            for (TrainingSession trainingSession : day.getSessions()) {
                if (trainingSession.isEvaluated()) {
                    evaluatedTrainingSessions++;
                }
                totalNumberOfTrainingSessions++;
            }
        }
        return (int) ((evaluatedTrainingSessions * 1.0) / totalNumberOfTrainingSessions * 100.0);
    }

    public static String getBOSFilesDirectory(Context context) {
        return context.getNoBackupFilesDir().getAbsolutePath() + "/files/";
    }

    public static String getFileExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf("."));

    }

    public static File convertRealmResourceToFile(RealmResource realmResource) {
        Gson gson = new Gson();
        File file = gson.fromJson(realmResource.getData(), File.class);
        file.setLabel(realmResource.getLabel());
        file.setKey(realmResource.getKey());
        return file;
    }

    public static java.io.File getFileInsideBOSDirectory(File file, Context context) {
        java.io.File bosFilesDirectory = new java.io.File(Util.getBOSFilesDirectory(context));

        if (!bosFilesDirectory.exists()) {
            bosFilesDirectory.mkdir();
        }

        String filePath = bosFilesDirectory.getAbsolutePath() + file.getKey() + Util.getFileExtension(file.getUrl());
        return new java.io.File(filePath);

    }

    public static JSONObject getJSONObjectForEvaluationResource(RealmEvaluationResource realmEvaluationResource) {
        String evaluationResourceType = null;
        String userKey = null;
        String groupKey = null;
        switch (Util.getEvaluationResourceType(realmEvaluationResource.getEvaluationResourcetype())) {
            case USER:
                evaluationResourceType = EvaluationResourceType.USER.label;
                userKey = realmEvaluationResource.getUser().getKey();
                break;
            case GROUP:
                evaluationResourceType = EvaluationResourceType.GROUP.label;
                groupKey = realmEvaluationResource.getGroup().getKey();
                break;
            default:
                return null;
        }
        String uuid = realmEvaluationResource.getUuid();
        String data = realmEvaluationResource.getData();
        String label = realmEvaluationResource.getLabel();
        String description = realmEvaluationResource.getDescription();
        Boolean isEvaluated = realmEvaluationResource.getEvaluated();
        String resourceType = realmEvaluationResource.getResourceType();

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("uuid", uuid);
            paramObject.put("data", data);
            paramObject.put("label", label);
            paramObject.put("description", description);
            paramObject.put("evaluated_user", userKey);
            paramObject.put("evaluated_group", groupKey);

            paramObject.put("is_evaluated", isEvaluated);
            paramObject.put("type", evaluationResourceType);
            paramObject.put("resource_type", resourceType);
            return paramObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
