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

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class EvaluationResource {
    @SerializedName("key")
    private String key;
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("data")
    private JsonObject data;
    @SerializedName("label")
    private String label;
    @SerializedName("description")
    private String description;
    @SerializedName("type")
    private String type;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("evaluated_user")
    private String userKey;
    @SerializedName("evaluated_group")
    private String groupKey;
    @SerializedName("ngo")
    private String ngo;
    @SerializedName("is_evaluated")
    private Boolean isEvaluated;
    @SerializedName("creation_time")
    private Date creationTime;
    @SerializedName("last_modification_time")
    private Date lastModificationTime;

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public JsonObject getData() {
        return data;
    }

    public String getNgo() {
        return ngo;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNgo(String ngo) {
        this.ngo = ngo;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setData(JsonObject data) {
        this.data = data;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getEvaluated() {
        return isEvaluated;
    }

    public void setEvaluated(Boolean evaluated) {
        isEvaluated = evaluated;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getResourceType() {
        return resourceType;
    }
}
