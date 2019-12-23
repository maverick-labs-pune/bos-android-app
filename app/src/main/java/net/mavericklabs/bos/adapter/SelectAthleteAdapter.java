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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.realm.RealmUser;

import java.util.ArrayList;
import java.util.List;

public class SelectAthleteAdapter extends RecyclerView.Adapter<SelectAthleteAdapter.AthleteViewHolder> {
    private List<RealmUser> athletes;
    private List<RealmUser> selectedAthletes;

    public SelectAthleteAdapter(List<RealmUser> athletes) {
        this.athletes = athletes;
        this.selectedAthletes = new ArrayList<>();
    }

    @NonNull
    @Override
    public AthleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_select_athlete, parent, false);
        return new AthleteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AthleteViewHolder holder, int position) {
        final RealmUser realmUser = athletes.get(position);
        holder.fullNameTextView.setText(realmUser.getFullName());
        holder.checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Add to selected list
                    addToSelectedAthletes(realmUser);
                } else {
                    // Remove from selected list
                    removeFromSelectedAthletes(realmUser);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return athletes.size();
    }

    class AthleteViewHolder extends RecyclerView.ViewHolder {

        private final TextView fullNameTextView;
        private final CheckBox checkBox;

        AthleteViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.text_view_full_name);
            checkBox = itemView.findViewById(R.id.check_box);
        }

    }

    public List<RealmUser> getSelectedAthletes() {
        return selectedAthletes;
    }

    private void addToSelectedAthletes(RealmUser realmUser) {
        if (!selectedAthletes.contains(realmUser)) {
            selectedAthletes.add(realmUser);
        }
    }

    private void removeFromSelectedAthletes(RealmUser realmUser) {
        selectedAthletes.remove(realmUser);
    }
}


