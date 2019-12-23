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


import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.utils.DateUtil;

import java.util.Date;

import io.realm.RealmObject;

public class RealmReading extends RealmObject {
    private String key;
    private RealmUser user;
    private String ngo;
    private RealmUser enteredByUser;
    private RealmMeasurement measurement;
    private RealmResource resource;
    private RealmEvaluationResource evaluationResource;
    private String value;
    private Date creationTime;

    public RealmReading() {

    }

    public RealmReading(RealmMeasurement realmMeasurement, RealmUser realmUser, RealmUser enteredByUser,
                        RealmEvaluationResource realmEvaluationResource, Measurement measurement) {
        this.key = null;
        this.user = realmUser;
        this.ngo = realmUser.getNgo();
        this.enteredByUser = realmUser;
        this.measurement = realmMeasurement;
        this.resource = realmEvaluationResource.getResource();
        this.evaluationResource = realmEvaluationResource;
        this.value = measurement.getReading();
        this.creationTime = DateUtil.getCurrentTime();
    }

    public String getKey() {
        return key;
    }

    public RealmUser getUser() {
        return user;
    }

    public String getNgo() {
        return ngo;
    }

    public RealmUser getEnteredByUser() {
        return enteredByUser;
    }

    public RealmMeasurement getMeasurement() {
        return measurement;
    }

    public RealmResource getResource() {
        return resource;
    }

    public RealmEvaluationResource getEvaluationResource() {
        return evaluationResource;
    }

    public String getValue() {
        return value;
    }

    public Date getCreationTime() {
        return creationTime;
    }
}
