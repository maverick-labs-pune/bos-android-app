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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmMeasurement;
import net.mavericklabs.bos.utils.ActivityMode;
import net.mavericklabs.bos.utils.AppLogger;

import java.util.List;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder> {
    private Context context;
    private List<Measurement> measurements;
    private final AppLogger appLogger = new AppLogger(getClass().toString());

    public MeasurementAdapter(Context context, List<Measurement> measurements) {
        this.context = context;
        this.measurements = measurements;
    }

    @NonNull
    @Override
    public MeasurementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_measurement, parent, false);
        return new MeasurementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MeasurementViewHolder holder, int position) {
        final Measurement measurement = measurements.get(position);
        RealmMeasurement realmMeasurement = RealmHandler.getMeasurementFromKey(measurement.getKey());
        holder.getLabel().setText(realmMeasurement.getLabel());
        if(TextUtils.isEmpty(realmMeasurement.getUnitOfMeasurement())){
            holder.getUom().setText(realmMeasurement.getUnitOfMeasurement());
        }
        holder.getImageView().setImageResource(R.drawable.ic_measurement);



    }

    @Override
    public int getItemCount() {
        return measurements.size();
    }

    class MeasurementViewHolder extends RecyclerView.ViewHolder {

        private final TextView label;
        private final TextView uom;
        private final ImageView imageView;
        private final CardView cardView;

        MeasurementViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.text_view_label);
            uom = itemView.findViewById(R.id.text_view_uom);
            imageView = itemView.findViewById(R.id.image_view);
            cardView = itemView.findViewById(R.id.card_view);
        }

        TextView getLabel() {
            return label;
        }

        TextView getUom() {
            return uom;
        }

        ImageView getImageView() {
            return imageView;
        }

        public CardView getCardView() {
            return cardView;
        }
    }

}


