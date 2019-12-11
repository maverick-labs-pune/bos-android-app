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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.activity.GroupDetailActivity;
import net.mavericklabs.bos.realm.RealmGroup;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.UserRole;
import net.mavericklabs.bos.utils.Util;

import java.util.List;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_GROUP_KEY;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private Context context;
    private List<RealmGroup> realmGroups;

    public GroupAdapter(Context context, List<RealmGroup> realmGroups) {
        this.context = context;
        this.realmGroups = realmGroups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_group, parent, false);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        final RealmGroup realmGroup = realmGroups.get(position);
        holder.getLabel().setText(realmGroup.getLabel());
        List<RealmUser> realmUsers = realmGroup.getUsers();
        int numberOfAthletes = 0, numberOfCoaches = 0, numberOfAdmins = 0;
        for (RealmUser realmUser : realmUsers) {
            UserRole userRole = Util.getRole(realmUser.getRole());
            switch (userRole) {
                case ADMIN:
                    numberOfAdmins++;
                    break;
                case ATHLETE:
                    numberOfAthletes++;
                    break;
                case COACH:
                    numberOfCoaches++;
                    break;
            }
        }
        String userDetailText = "";
        if (numberOfAthletes != 0) {
            if (numberOfAthletes == 1){
                userDetailText = userDetailText.concat("1 Athlete ");
            }else{
                userDetailText = userDetailText.concat(numberOfAthletes + " Athletes ");

            }
        }
        if (numberOfCoaches != 0) {
            if (numberOfCoaches == 1){
                userDetailText = userDetailText.concat("1 Coach ");
            }else{
                userDetailText = userDetailText.concat(numberOfCoaches + " Coaches ");

            }
        }
        holder.getUsers().setText(userDetailText);
        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupDetailActivity.class);
                intent.putExtra(BUNDLE_KEY_GROUP_KEY, realmGroup.getKey());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return realmGroups.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        private final TextView label;
        private final TextView users;
        private final CardView cardView;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.text_view_label);
            users = itemView.findViewById(R.id.text_view_users);
            cardView = itemView.findViewById(R.id.card_view);
        }

        public TextView getUsers() {
            return users;
        }

        public TextView getLabel() {
            return label;
        }

        public CardView getCardView() {
            return cardView;
        }
    }

}


