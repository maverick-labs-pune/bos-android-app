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

package net.mavericklabs.bos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.UserRole;
import net.mavericklabs.bos.utils.Util;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<RealmUser> realmUsers;

    public UserAdapter(List<RealmUser> realmUsers) {
        this.realmUsers = realmUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_user, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        RealmUser realmUser = realmUsers.get(position);
        holder.getFullName().setText(realmUser.getFullName());
        UserRole userRole = Util.getRole(realmUser.getRole());
        switch (userRole){
            case ADMIN:
                holder.getRole().setText("Admin");
                break;
            case COACH:
                holder.getRole().setText("Coach");
                break;
            case ATHLETE:
                holder.getRole().setText("Athlete");
                break;
            case UNKNOWN:
                holder.getRole().setText("Unknown role");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return realmUsers.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView fullName,role;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.text_view_full_name);
            role = itemView.findViewById(R.id.text_view_role);
        }

        TextView getFullName() {
            return fullName;
        }

        public TextView getRole() {
            return role;
        }
    }

}


