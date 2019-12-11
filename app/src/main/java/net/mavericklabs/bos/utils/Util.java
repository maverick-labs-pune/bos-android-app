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

package net.mavericklabs.bos.utils;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.mavericklabs.bos.model.User;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.realm.RealmResource;

import java.util.List;


public class Util {

    public static void setEmptyMessageIfNeeded(List data, RecyclerView recyclerView, TextView emptyView){
        if (data.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public static void setEmptyMessageIfNeeded(Object data, RecyclerView recyclerView, TextView emptyView){
        if (data == null) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public static Curriculum convertRealmResourceToCurriculum(RealmResource realmResource){
        Gson gson = new Gson();
        Curriculum curriculum = gson.fromJson(realmResource.getData(),Curriculum.class);
        curriculum.setKey(realmResource.getKey());
        curriculum.setLabel(realmResource.getLabel());
        curriculum.setDescription(realmResource.getDescription());
        return curriculum;
    }

    public static UserRole getRole(String role){
        if (role.toLowerCase().equals(UserRole.ADMIN.label)){
            return UserRole.ADMIN;
        }if (role.toLowerCase().equals(UserRole.COACH.label)){
            return UserRole.COACH;
        }if (role.toLowerCase().equals(UserRole.ATHLETE.label)){
            return UserRole.ATHLETE;
        }
        return UserRole.UNKNOWN;
    }
}
