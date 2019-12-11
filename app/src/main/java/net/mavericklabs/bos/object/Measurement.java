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

public class Measurement implements Parcelable {
    private String key;
    private String description;
    private boolean is_required;

    protected Measurement(Parcel in) {
        key = in.readString();
        description = in.readString();
        is_required = in.readInt() != 0;
    }

    public static final Creator<Measurement> CREATOR = new Creator<Measurement>() {
        @Override
        public Measurement createFromParcel(Parcel in) {
            return new Measurement(in);
        }

        @Override
        public Measurement[] newArray(int size) {
            return new Measurement[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(description);
        dest.writeInt(is_required ? 1 : 0);
    }

    public String getKey() {
        return key;
    }
}
