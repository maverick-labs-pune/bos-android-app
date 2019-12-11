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
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmUser;

import java.util.List;

public class DailyPlannerAdapter extends RecyclerView.Adapter<DailyPlannerAdapter.EvaluationResourceViewHolder> {
    private List<RealmEvaluationResource> realmEvaluationResources;

    public DailyPlannerAdapter(List<RealmEvaluationResource> realmEvaluationResources) {
        this.realmEvaluationResources = realmEvaluationResources;
    }

    @NonNull
    @Override
    public EvaluationResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_athlete, parent, false);
        return new EvaluationResourceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EvaluationResourceViewHolder holder, int position) {
        RealmEvaluationResource realmUser = realmEvaluationResources.get(position);
        holder.getFullName().setText(realmUser.getUser().getFullName());
    }

    @Override
    public int getItemCount() {
        return realmEvaluationResources.size();
    }

    class EvaluationResourceViewHolder extends RecyclerView.ViewHolder {

        private final TextView fullName;

        EvaluationResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.text_view_full_name);
        }

        TextView getFullName() {
            return fullName;
        }
    }

}


