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

import net.mavericklabs.bos.utils.Constants;
import net.mavericklabs.bos.utils.DateUtil;
import net.mavericklabs.bos.utils.Util;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.UUID;

public class RealmEvaluationResource extends RealmObject {
    @PrimaryKey
    private String uuid;
    private RealmResource resource;
    private RealmUser user;
    private RealmGroup group;
    private Boolean isEvaluated;
    private Boolean isSynced;
    private String data;
    private Date creationTime;
    private Date lastModificationTime;
    private String type;

    public RealmEvaluationResource() {

    }

    public RealmEvaluationResource(RealmResource realmResource, RealmUser user) {
        Date currentTime = DateUtil.getCurrentTime();
        this.uuid = UUID.randomUUID().toString();
        this.resource = realmResource;
        this.data = Util.convertRealmResourceDataToRealmEvaluationResourceData(realmResource, uuid, true, currentTime);
        this.user = user;
        this.group = null;
        this.creationTime = currentTime;
        this.lastModificationTime = currentTime;
        this.isEvaluated = false;
        this.isSynced = false;
        this.type = Constants.USER;

    }

    public RealmEvaluationResource(RealmResource realmResource, RealmGroup group) {
        Date currentTime = DateUtil.getCurrentTime();
        this.uuid = UUID.randomUUID().toString();
        this.resource = realmResource;
        // Add uuid and is evaluated fields to the data.
        this.data = Util.convertRealmResourceDataToRealmEvaluationResourceData(realmResource, uuid, true, currentTime);
        this.user = null;
        this.group = group;
        this.creationTime = currentTime;
        this.lastModificationTime = currentTime;
        this.isEvaluated = false;
        this.isSynced = false;
        this.type = Constants.GROUP;

    }

    public String getUuid() {
        return uuid;
    }

    public RealmResource getResource() {
        return resource;
    }

    public RealmUser getUser() {
        return user;
    }

    public Boolean getEvaluated() {
        return isEvaluated;
    }

    public void setEvaluated(Boolean evaluated) {
        isEvaluated = evaluated;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Date getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(Date lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public String getType() {
        return type;
    }

    public RealmGroup getGroup() {
        return group;
    }

    public Boolean getSynced() {
        return isSynced;
    }

    public void setSynced(Boolean synced) {
        isSynced = synced;
    }
}
