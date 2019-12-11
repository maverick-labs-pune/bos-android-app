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

package net.mavericklabs.bos.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Resource {
    @SerializedName("key")
    private String key;
    @SerializedName("ngo")
    private String ngo;
    @SerializedName("label")
    private String label;
    @SerializedName("description")
    private String description;
    @SerializedName("type")
    private String type;
    @SerializedName("is_active")
    private Boolean isActive;
    @SerializedName("is_shared")
    private Boolean isShared;
    @SerializedName("data")
    private JsonObject data;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsShared() {
        return isShared;
    }

    public void setIsShared(Boolean isShared) {
        this.isShared = isShared;
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
}
