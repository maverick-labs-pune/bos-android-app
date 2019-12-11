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

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Group {
    @SerializedName("key")
    private String key;
    @SerializedName("ngo")
    private String ngo;
    @SerializedName("label")
    private String label;
    @SerializedName("users")
    private List<User> users;
    @SerializedName("resources")
    private List<Resource> resources;
    @SerializedName("is_active")
    private Boolean isActive;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Date getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(Date lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public String getNgo() {
        return ngo;
    }

    public void setNgo(String ngo) {
        this.ngo = ngo;
    }
}
