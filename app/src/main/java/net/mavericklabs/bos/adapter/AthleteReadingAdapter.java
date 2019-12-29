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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.activity.AthleteActivity;
import net.mavericklabs.bos.realm.RealmReading;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.DateUtil;

import java.util.List;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ATHLETE_KEY;

public class AthleteReadingAdapter extends RecyclerView.Adapter<AthleteReadingAdapter.AthleteReadingViewHolder> {
    private Context context;
    private List<RealmReading> realmReadings;

    public AthleteReadingAdapter(Context context, List<RealmReading> realmReadings) {
        this.realmReadings = realmReadings;
        this.context = context;
    }


    @NonNull
    @Override
    public AthleteReadingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_athlete_reading, parent, false);
        return new AthleteReadingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AthleteReadingViewHolder holder, int position) {
        if (position == 0) {
            holder.dateTextView.setText("Date");
            holder.readingMeasurementTextView.setText("Measurement");
            holder.readingValueTextView.setText("Value");
        } else {
            final RealmReading realmReading = realmReadings.get(position - 1);
            holder.dateTextView.setText(DateUtil.dateToString(realmReading.getCreationTime()));
            holder.readingMeasurementTextView.setText(realmReading.getMeasurement().getLabel());
            holder.readingValueTextView.setText(realmReading.getValue());
            if (position % 2 == 1) {
                holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }

    }

    @Override
    public int getItemCount() {
        return realmReadings.size() + 1;
    }

    class AthleteReadingViewHolder extends RecyclerView.ViewHolder {

        private final TextView dateTextView, readingMeasurementTextView, readingValueTextView;
        private final LinearLayout linearLayout;

        AthleteReadingViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.text_date);
            readingMeasurementTextView = itemView.findViewById(R.id.text_reading_measurement);
            readingValueTextView = itemView.findViewById(R.id.text_reading_value);
            linearLayout = itemView.findViewById(R.id.linear_layout);
        }

    }

}


