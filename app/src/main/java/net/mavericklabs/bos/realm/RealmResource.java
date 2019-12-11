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

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import net.mavericklabs.bos.model.Measurement;
import net.mavericklabs.bos.model.MeasurementType;
import net.mavericklabs.bos.model.Resource;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmResource extends RealmObject {
    @PrimaryKey
    private String key;
    private String ngo;
    private String label;
    private String description;
    private String type;
    private Boolean isActive;
    private Boolean isShared;
    private String data;
    private Date creationTime;
    private Date lastModificationTime;

    public RealmResource() {

    }
    public RealmResource(Resource resource) {
        this.key = resource.getKey();
        this.ngo = resource.getNgo();
        this.label = resource.getLabel();
        this.description = resource.getDescription();
        this.type = resource.getType();
        this.isActive = resource.getIsActive();
        this.isShared = resource.getIsShared();
        this.data = resource.getData().toString();
        this.creationTime = resource.getCreationTime();
        this.lastModificationTime = resource.getLastModificationTime();
    }


    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String getNgo() {
        return ngo;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setNgo(String ngo) {
        this.ngo = ngo;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsShared() {
        return isShared;
    }

    public void setIsShared(Boolean isShared) {
        this.isShared = isShared;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(Date lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
