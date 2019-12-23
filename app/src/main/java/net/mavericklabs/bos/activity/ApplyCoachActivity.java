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

package net.mavericklabs.bos.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.model.Measurement;
import net.mavericklabs.bos.model.NGO;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.retrofit.ApiClient;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.NetworkConnection;
import net.mavericklabs.bos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class ApplyCoachActivity extends AppCompatActivity {

    private Spinner ngo_spinner;
    List<NGO> ngos;
    private Resource resource;
    private ConstraintLayout constraintLayout;
    private boolean isLoading = true;
    private NGO selectedNGO;
    private AppLogger appLogger = new AppLogger(getClass().toString());

    private Callback<Resource> coachRegistrationFormCallback = new Callback<Resource>() {
        @Override
        public void onResponse(Call<Resource> call, Response<Resource> response) {
            if (response.isSuccessful()) {
                resource = response.body();
                fetchMeasurementsFromKeys();
                Toast.makeText(getApplicationContext(), "Received resource", Toast.LENGTH_SHORT).show();

            } else {
                ToastUtils.showToast(ApplyCoachActivity.this, "Please try again", Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onFailure(Call<Resource> call, Throwable t) {
            appLogger.logDebug("onFailure");
        }
    };

    private Callback<List<NGO>> getNGOsCallback = new Callback<List<NGO>>() {
        @Override
        public void onResponse(Call<List<NGO>> call, Response<List<NGO>> response) {
            if (response.isSuccessful()) {
                ngos = response.body();
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createArrayAdapter();

            } else {
                ToastUtils.showToast(ApplyCoachActivity.this, "Response failed", Toast.LENGTH_SHORT);
//                    Toast.makeText(getContext(), getTranslation(locale, "OLD_PASSWORD_IS_WRONG"), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<List<NGO>> call, Throwable t) {
            appLogger.logDebug("onFailure");
        }
    };
    private Callback<List<Measurement>> measurementsFromKeysCallback = new Callback<List<Measurement>>() {
        @Override
        public void onResponse(Call<List<Measurement>> call, Response<List<Measurement>> response) {
            if (response.isSuccessful()) {
                measurements = response.body();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createFormElements();

            } else {
                ToastUtils.showToast(ApplyCoachActivity.this, "Response failed", Toast.LENGTH_SHORT);
//                    Toast.makeText(getContext(), getTranslation(locale, "OLD_PASSWORD_IS_WRONG"), Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onFailure(Call<List<Measurement>> call, Throwable t) {

        }
    };
    private List<Measurement> measurements;
    private List<View> measurementViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_coach);
        this.setTitle("Apply as a coach");
        ngo_spinner = findViewById(R.id.ngo_spinner);
        constraintLayout = findViewById(R.id.root);

        if (!NetworkConnection.isNetworkAvailable(getApplicationContext())) {
            ToastUtils.showToast(getApplicationContext(), "No internet", Toast.LENGTH_SHORT);
        } else {

        }
        ApiClient.getApiInterface(getApplicationContext()).getNGOs().enqueue(getNGOsCallback);

    }


    private void createArrayAdapter() {
        ArrayList<String> ngoNames = new ArrayList<>();
        for (int i = 0; i < ngos.size(); i++) {
            ngoNames.add(ngos.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ngoNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ngo_spinner.setAdapter(adapter);
        ngo_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNGO = ngos.get(position);
                ApiClient.getApiInterface(ApplyCoachActivity.this).getCoachRegistrationForm(selectedNGO.getKey()).enqueue(coachRegistrationFormCallback);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetchMeasurementsFromKeys() {
        ArrayList<String> measurementKeys = new ArrayList<>();
        JsonArray measurements = resource.getData().getAsJsonArray("measurements");
        for (int i = 0; i < measurements.size(); i++) {
            JsonObject measurement = (JsonObject) measurements.get(i);
            JsonElement key = measurement.get("key");
            measurementKeys.add(key.getAsString());
        }
        ApiClient.getApiInterface(ApplyCoachActivity.this).getMeasurementsFromKeys(selectedNGO.getKey(), measurementKeys).enqueue(measurementsFromKeysCallback);
    }

    private void createFormElements() {
        int previousViewID = 0;
        for (int i = 0; i < measurements.size(); i++) {
            Measurement measurement = measurements.get(i);
            EditText view = new EditText(this);
            int viewID = View.generateViewId();
            view.setId(viewID);
            view.setHint(measurement.getLabel());
            view.setTag(measurement.getKey());
            view.setMaxLines(1);
            view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            constraintLayout.addView(view);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            if (i == 0) {
                constraintSet.connect(viewID, ConstraintSet.TOP, ngo_spinner.getId(), ConstraintSet.BOTTOM, 18);
            } else {
                constraintSet.connect(viewID, ConstraintSet.TOP, previousViewID, ConstraintSet.BOTTOM, 18);
            }
            constraintSet.connect(viewID, ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, 18);
            constraintSet.connect(viewID, ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, 18);

            constraintSet.applyTo(constraintLayout);
            previousViewID = viewID;
            measurementViews.add(view);

        }

    }

}
