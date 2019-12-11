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

import com.google.gson.annotations.SerializedName;

import net.mavericklabs.bos.model.Group;
import net.mavericklabs.bos.model.Measurement;
import net.mavericklabs.bos.model.MeasurementType;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.model.User;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmGroup extends RealmObject {
    @PrimaryKey
    private String key;
    private String ngo;
    private String label;
    private RealmList<RealmUser> users;
    private RealmList<RealmResource> resources;
    private Boolean isActive;
    private Date creationTime;
    private Date lastModificationTime;

    public RealmGroup() {

    }
    public RealmGroup(Group group) {
        this.key = group.getKey();
        this.ngo = group.getNgo();
        this.label = group.getLabel();
        users = new RealmList<>();
        for (User user : group.getUsers()){
            users.add(new RealmUser(user));
        }
        resources = new RealmList<>();
        for (Resource resource : group.getResources()){
            resources.add(new RealmResource(resource));
        }
        this.isActive = group.getActive();
        this.creationTime = group.getCreationTime();
        this.lastModificationTime = group.getLastModificationTime();
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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

    public List<RealmUser> getUsers() {
        return users;
    }

    public void setUsers(RealmList<RealmUser> users) {
        this.users = users;
    }

    public List<RealmResource> getResources() {
        return resources;
    }

    public void setResources(RealmList<RealmResource> resources) {
        this.resources = resources;
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
