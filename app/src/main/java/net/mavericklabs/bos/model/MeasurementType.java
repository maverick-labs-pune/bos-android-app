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

public class MeasurementType{
    @SerializedName("key")
    private String key;
    @SerializedName("ngo")
    private String ngo;
    @SerializedName("label")
    private String label;

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

    public void setKey(String key) {
        this.key = key;
    }

    public void setNgo(String ngo) {
        this.ngo = ngo;
    }
}
