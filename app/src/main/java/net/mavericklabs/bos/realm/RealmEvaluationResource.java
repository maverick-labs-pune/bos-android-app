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

import net.mavericklabs.bos.model.EvaluationResource;
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
    private String key;
    private String label;
    private String description;
    private RealmResource resource;
    private RealmUser user;
    private RealmGroup group;
    private Boolean isEvaluated;
    private Boolean isSynced;
    private String data;
    private Date creationTime;
    private Date lastModificationTime;
    private String evaluationResourcetype;
    private String resourceType;

    public RealmEvaluationResource() {

    }

    public RealmEvaluationResource(RealmResource realmResource, RealmUser user) {
        Date currentTime = DateUtil.getCurrentTime();
        this.uuid = UUID.randomUUID().toString();
        this.key = null;
        this.resource = realmResource;
        this.label = realmResource.getLabel();
        this.description = realmResource.getDescription();
        this.data = Util.convertRealmResourceDataToRealmEvaluationResourceData(realmResource, uuid, true, currentTime);
        this.user = user;
        this.group = null;
        this.creationTime = currentTime;
        this.lastModificationTime = currentTime;
        this.isEvaluated = false;
        this.isSynced = false;
        this.evaluationResourcetype = Constants.USER;
        this.resourceType = realmResource.getType();
    }

    public RealmEvaluationResource(RealmResource realmResource, RealmGroup group) {
        Date currentTime = DateUtil.getCurrentTime();
        this.uuid = UUID.randomUUID().toString();
        this.key = null;
        this.resource = realmResource;
        this.label = realmResource.getLabel();
        this.description = realmResource.getDescription();
        // Add uuid and is evaluated fields to the data.
        this.data = Util.convertRealmResourceDataToRealmEvaluationResourceData(realmResource, uuid, true, currentTime);
        this.user = null;
        this.group = group;
        this.creationTime = currentTime;
        this.lastModificationTime = currentTime;
        this.isEvaluated = false;
        this.isSynced = false;
        this.evaluationResourcetype = Constants.GROUP;
        this.resourceType = realmResource.getType();
    }

    public RealmEvaluationResource(EvaluationResource evaluationResource, RealmUser realmUser) {
        this.uuid = evaluationResource.getUuid();
        this.key = evaluationResource.getKey();
        this.resource = null;
        this.label = evaluationResource.getLabel();
        this.description = evaluationResource.getDescription();
        this.data = evaluationResource.getData().toString();
        this.user = realmUser;
        this.group = null;
        this.creationTime = evaluationResource.getCreationTime();
        this.lastModificationTime = evaluationResource.getLastModificationTime();
        this.isEvaluated = evaluationResource.getEvaluated();
        this.isSynced = true;
        this.evaluationResourcetype = Constants.USER;
        this.resourceType = evaluationResource.getResourceType();
    }

    public RealmEvaluationResource(EvaluationResource evaluationResource, RealmGroup realmGroup) {
        this.uuid = evaluationResource.getUuid();
        this.key = evaluationResource.getKey();
        this.resource = null;
        this.label = evaluationResource.getLabel();
        this.description = evaluationResource.getDescription();
        this.data = evaluationResource.getData().toString();
        this.user = null;
        this.group = realmGroup;
        this.creationTime = evaluationResource.getCreationTime();
        this.lastModificationTime = evaluationResource.getLastModificationTime();
        this.isEvaluated = evaluationResource.getEvaluated();
        this.isSynced = true;
        this.evaluationResourcetype = Constants.GROUP;
        this.resourceType = evaluationResource.getResourceType();
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

    public String getEvaluationResourcetype() {
        return evaluationResourcetype;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getResourceType() {
        return resourceType;
    }
}
