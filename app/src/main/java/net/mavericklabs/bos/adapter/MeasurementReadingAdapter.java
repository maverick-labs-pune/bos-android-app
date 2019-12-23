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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmMeasurement;
import net.mavericklabs.bos.utils.AppLogger;

import java.util.ArrayList;
import java.util.List;

import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;

public class MeasurementReadingAdapter extends RecyclerView.Adapter<MeasurementReadingAdapter.MeasurementReadingViewHolder> {
    private Context context;
    private List<Measurement> measurements;
    private Curriculum curriculum;
    private final AppLogger appLogger = new AppLogger(getClass().toString());

    public MeasurementReadingAdapter(Context context, List<Measurement> measurements, Curriculum curriculum) {
        this.context = context;
        this.measurements = measurements;
        this.curriculum = curriculum;
    }

    @NonNull
    @Override
    public MeasurementReadingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_measurement_reading, parent, false);
        return new MeasurementReadingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MeasurementReadingViewHolder holder, final int measurementPosition) {
        Measurement measurement = measurements.get(measurementPosition);
        RealmMeasurement realmMeasurement = RealmHandler.getMeasurementFromKey(measurement.getKey());
        appLogger.logDebug(realmMeasurement.getKey());
        appLogger.logDebug(realmMeasurement.getLabel());
        appLogger.logDebug(realmMeasurement.getInputType());
        holder.labelTextView.setText(realmMeasurement.getLabel());
        holder.uomTextView.setText(realmMeasurement.getUnitOfMeasurement());
        switch (realmMeasurement.getInputType()) {
            case "text":
                holder.editTextView.setInputType(TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                holder.editTextView.setVisibility(View.VISIBLE);
                holder.spinner.setVisibility(View.GONE);
                holder.editTextView.setText(measurement.getReading());
                holder.editTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        measurements.get(measurementPosition).setReading(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                break;
            case "numeric":
                holder.editTextView.setInputType(TYPE_NUMBER_FLAG_DECIMAL);
                holder.editTextView.setVisibility(View.VISIBLE);
                holder.spinner.setVisibility(View.GONE);
                holder.editTextView.setText(measurement.getReading());
                holder.editTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        measurements.get(measurementPosition).setReading(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                break;
            case "boolean":
                holder.editTextView.setVisibility(View.GONE);
                final List<String> choices = new ArrayList<>();
                choices.add("-");
                choices.add("Yes");
                choices.add("No");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, choices);
                holder.spinner.setAdapter(adapter);
                String reading = measurement.getReading();
                int indexOfChoice = choices.indexOf(reading);
                if (indexOfChoice > 0) {
                    holder.spinner.setSelection(indexOfChoice);
                } else {
                    holder.spinner.setSelection(0);
                }
                holder.spinner.setVisibility(View.VISIBLE);
                holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        holder.spinner.setSelection(position);
                        String choice = choices.get(position);
                        if (position == 0) {
                            choice = null;
                        }
                        measurements.get(measurementPosition).setReading(choice);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        }


    }

    public boolean verifyReadings() {
        for (Measurement measurement : measurements) {
            appLogger.logInformation(measurement.getReading());
            if (measurement.isRequired()) {
                if (measurement.getReading() == null || measurement.getReading().length() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Measurement> getMeasurementsWithReadings() {
        return measurements;
    }

    @Override
    public int getItemCount() {
        return measurements.size();
    }

    class MeasurementReadingViewHolder extends RecyclerView.ViewHolder {

        private final TextView labelTextView;
        private final TextView uomTextView;
        private final CardView cardView;
        private final EditText editTextView;
        private final Spinner spinner;

        MeasurementReadingViewHolder(@NonNull View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.text_view_label);
            uomTextView = itemView.findViewById(R.id.text_view_uom);
            cardView = itemView.findViewById(R.id.card_view);
            editTextView = itemView.findViewById(R.id.edit_text_view);
            spinner = itemView.findViewById(R.id.spinner);
        }
    }

}


