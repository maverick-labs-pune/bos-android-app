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
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmMeasurement;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.MeasurementInputType;
import net.mavericklabs.bos.utils.ToastUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static net.mavericklabs.bos.utils.Util.getMeasurementInputType;

public class MeasurementReadingAdapter extends RecyclerView.Adapter<MeasurementReadingAdapter.MeasurementReadingViewHolder> {
    private final TrainingSession trainingSession;
    private Context context;
    private List<Measurement> measurements;
    private final AppLogger appLogger = new AppLogger(getClass().toString());

    public MeasurementReadingAdapter(Context context, TrainingSession trainingSession) {
        this.context = context;
        this.measurements = trainingSession.getMeasurements();
        this.trainingSession = trainingSession;
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
        Realm realm = Realm.getDefaultInstance();
        RealmMeasurement realmMeasurement = RealmHandler.getMeasurementFromKey(measurement.getKey());
        TextWatcher textWatcher = new TextWatcher() {
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
        };
        switch (getMeasurementInputType(realmMeasurement.getInputType())) {
            case TEXT:
                holder.editTextView.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                showEditTextAndHideSpinner(holder);
                checkIfTrainingSessionEvaluated(holder, measurement, realmMeasurement, textWatcher);
                break;
            case NUMERIC:
                holder.editTextView.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
                showEditTextAndHideSpinner(holder);
                checkIfTrainingSessionEvaluated(holder, measurement, realmMeasurement, textWatcher);
                break;
            case BOOLEAN:
                holder.spinnerTextInputLayout.setHint(realmMeasurement.getLabel());

                hideEditTextAndShowSpinner(holder);

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

        realm.close();
    }

    private void checkIfTrainingSessionEvaluated(MeasurementReadingViewHolder holder, Measurement measurement, RealmMeasurement realmMeasurement, TextWatcher textWatcher) {
        if (trainingSession.isEvaluated()) {
            holder.editTextViewTextInputLayout.setHint(realmMeasurement.getLabel());
            String measurementWithUOM = measurement.getReading();
            if (!TextUtils.isEmpty(realmMeasurement.getUnitOfMeasurement())) {
                measurementWithUOM = measurementWithUOM + ' ' + realmMeasurement.getUnitOfMeasurement();
            }
            holder.editTextView.setText(measurementWithUOM);
            disableEditText(holder.editTextView);
        } else {
            String hint = realmMeasurement.getLabel();
            if (!TextUtils.isEmpty(realmMeasurement.getUnitOfMeasurement())) {
                hint = hint + " in " + realmMeasurement.getUnitOfMeasurement();
            }
            holder.editTextView.setText(measurement.getReading());
            holder.editTextViewTextInputLayout.setHint(hint);
            holder.editTextView.addTextChangedListener(textWatcher);
        }

    }

    private void showEditTextAndHideSpinner(MeasurementReadingViewHolder holder) {
        holder.editTextViewTextInputLayout.setVisibility(View.VISIBLE);
        holder.editTextView.setVisibility(View.VISIBLE);
        holder.spinnerTextInputLayout.setVisibility(View.GONE);
        holder.spinner.setVisibility(View.GONE);
    }

    private void hideEditTextAndShowSpinner(MeasurementReadingViewHolder holder) {
        holder.editTextViewTextInputLayout.setVisibility(View.GONE);
        holder.editTextView.setVisibility(View.GONE);
        holder.spinnerTextInputLayout.setVisibility(View.VISIBLE);
        holder.spinner.setVisibility(View.VISIBLE);
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }

    public boolean verifyReadings() {
        for (Measurement measurement : measurements) {
            if (measurement.isRequired()) {
                if (TextUtils.isEmpty(measurement.getReading())) {
                    Realm realm = Realm.getDefaultInstance();
                    RealmMeasurement realmMeasurement = RealmHandler.getMeasurementFromKey(measurement.getKey());
                    ToastUtils.showToast(context, realmMeasurement.getLabel() + " is empty", Toast.LENGTH_SHORT);
                    realm.close();
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
        private final EditText editTextView;
        private final Spinner spinner;
        private final TextInputLayout editTextViewTextInputLayout, spinnerTextInputLayout;

        MeasurementReadingViewHolder(@NonNull View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.text_view_label);
            editTextView = itemView.findViewById(R.id.edit_text_view);
            editTextViewTextInputLayout = itemView.findViewById(R.id.text_input_layout_edit_text_view);
            spinnerTextInputLayout = itemView.findViewById(R.id.text_input_layout_edit_spinner);
            spinner = itemView.findViewById(R.id.spinner);
        }
    }

}


