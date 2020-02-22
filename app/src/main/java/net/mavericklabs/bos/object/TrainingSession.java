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

package net.mavericklabs.bos.object;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrainingSession implements Parcelable {
    private String uuid;
    private String key;
    private String label;
    private String description;
    private String type;
    private boolean isEvaluated;
    private List<File> files = new ArrayList<>();
    private List<Measurement> measurements = new ArrayList<>();
    private Date lastModificationTime;

    public Date getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(Date lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    private TrainingSession(Parcel in) {
        uuid = in.readString();
        key = in.readString();
        label = in.readString();
        description = in.readString();
        type = in.readString();
        isEvaluated = in.readInt() != 0;
        in.readTypedList(files,File.CREATOR);
        in.readTypedList(measurements,Measurement.CREATOR);
        lastModificationTime = new Date(in.readLong());
    }

    public static final Creator<TrainingSession> CREATOR = new Creator<TrainingSession>() {
        @Override
        public TrainingSession createFromParcel(Parcel in) {
            return new TrainingSession(in);
        }

        @Override
        public TrainingSession[] newArray(int size) {
            return new TrainingSession[size];
        }
    };

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public List<File> getFiles() {
        return files;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setEvaluated(boolean evaluated) {
        isEvaluated = evaluated;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(key);
        dest.writeString(label);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeInt(isEvaluated ? 1 : 0);
        dest.writeTypedList(files);
        dest.writeTypedList(measurements);
        dest.writeLong(lastModificationTime !=null ? lastModificationTime.getTime() : 0);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isEvaluated() {
        return isEvaluated;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
