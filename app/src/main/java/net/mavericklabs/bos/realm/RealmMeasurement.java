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

import net.mavericklabs.bos.model.Measurement;
import net.mavericklabs.bos.model.MeasurementType;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmMeasurement extends RealmObject {
    @PrimaryKey
    private String key;
    private String ngo;
    private String label;
    private RealmList<RealmMeasurementType> types;
    private Boolean isActive;
    private String inputType;
    private String unitOfMeasurement;

    public RealmMeasurement() {

    }
    public RealmMeasurement(Measurement measurement) {
        this.key = measurement.getKey();
        this.ngo = measurement.getNgo();
        this.label = measurement.getLabel();
        types = new RealmList<>();
        for (MeasurementType measurementType : measurement.getTypes()){
            types.add(new RealmMeasurementType(measurementType));
        }
        this.isActive = measurement.getActive();
        this.inputType = measurement.getInputType();
        this.unitOfMeasurement = measurement.getUnitOfMeasurement();
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

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public RealmList<RealmMeasurementType> getTypes() {
        return types;
    }

    public void setTypes(RealmList<RealmMeasurementType> types) {
        this.types = types;
    }
}
