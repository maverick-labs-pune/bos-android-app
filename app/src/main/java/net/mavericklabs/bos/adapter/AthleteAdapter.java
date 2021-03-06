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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.activity.AthleteActivity;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.Util;

import java.util.List;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ATHLETE_UUID;

public class AthleteAdapter extends RecyclerView.Adapter<AthleteAdapter.AthleteViewHolder> {
    private List<RealmUser> athletes;
    private Context context;

    public AthleteAdapter(List<RealmUser> athletes, Context context) {
        this.athletes = athletes;
        this.context = context;
    }

    @NonNull
    @Override
    public AthleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_athlete, parent, false);
        return new AthleteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AthleteViewHolder holder, int position) {
        final RealmUser realmUser = athletes.get(position);
        holder.fullNameTextView.setText(realmUser.getFullName());
        holder.genderTextView.setText(Util.getGenderLabel(realmUser.getGender()));
        Util.setImageViewBasedOnGender(holder.imageView, realmUser);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AthleteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(BUNDLE_KEY_ATHLETE_UUID, realmUser.getUuid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return athletes.size();
    }

    class AthleteViewHolder extends RecyclerView.ViewHolder {

        private final TextView fullNameTextView, genderTextView;
        private final CardView cardView;
        private final ImageView imageView;

        AthleteViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.text_view_full_name);
            genderTextView = itemView.findViewById(R.id.text_view_gender);
            cardView = itemView.findViewById(R.id.card_view);
            imageView = itemView.findViewById(R.id.image_view);

        }

    }

}


