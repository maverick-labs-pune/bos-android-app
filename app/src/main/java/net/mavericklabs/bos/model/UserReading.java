/*
 * Copyright (c) 2020. Maverick Labs
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

package net.mavericklabs.bos.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserReading {
    @SerializedName("key")
    private String key;
    @SerializedName("user")
    private String user;
    @SerializedName("ngo")
    private String ngo;
    @SerializedName("by_user")
    private String byUser;
    @SerializedName("entered_by")
    private String enteredBy;
    @SerializedName("measurement")
    private String measurement;
    @SerializedName("training_session_uuid")
    private String trainingSessionUUID;
    @SerializedName("evaluation_session_uuid")
    private String evaluationSessionUUID;
    @SerializedName("value")
    private String value;
    @SerializedName("is_active")
    private Boolean isActive;
    @SerializedName("recorded_at")
    private Date recordedAt;
    @SerializedName("creation_time")
    private Date creationTime;
    @SerializedName("last_modification_time")
    private Date lastModificationTime;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNgo() {
        return ngo;
    }

    public void setNgo(String ngo) {
        this.ngo = ngo;
    }

    public String getByUser() {
        return byUser;
    }

    public void setByUser(String byUser) {
        this.byUser = byUser;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }


    public Date getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Date recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(Date lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public String getTrainingSessionUUID() {
        return trainingSessionUUID;
    }

    public void setTrainingSessionUUID(String trainingSessionUUID) {
        this.trainingSessionUUID = trainingSessionUUID;
    }

    public String getEvaluationSessionUUID() {
        return evaluationSessionUUID;
    }

    public void setEvaluationSessionUUID(String evaluationSessionUUID) {
        this.evaluationSessionUUID = evaluationSessionUUID;
    }
}
