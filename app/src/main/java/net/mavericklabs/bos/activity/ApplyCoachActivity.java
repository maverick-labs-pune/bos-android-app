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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.textfield.TextInputEditText;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.MeasurementReadingAdapter;
import net.mavericklabs.bos.model.Measurement;
import net.mavericklabs.bos.model.NGO;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmMeasurement;
import net.mavericklabs.bos.retrofit.ApiClient;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.Gender;
import net.mavericklabs.bos.utils.NetworkConnection;
import net.mavericklabs.bos.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.mavericklabs.bos.utils.Util.convertResourceToTrainingSession;
import static net.mavericklabs.bos.utils.Util.getGender;

public class ApplyCoachActivity extends AppCompatActivity {

    private Spinner ngoSpinner;
    List<NGO> ngos;
    private NGO selectedNGO;
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private TrainingSession trainingSession;
    private RecyclerView recyclerView;
    private MeasurementReadingAdapter measurementReadingsAdapter;
    private RelativeLayout relativeLayout;
    private ProgressDialog progress;
    private Spinner genderSpinner;
    private Button applyAsCoach;
    final String[] selectedGender = {null};


    private Callback<Resource> coachRegistrationFormCallback = new Callback<Resource>() {
        @Override
        public void onResponse(Call<Resource> call, Response<Resource> response) {
            if (response.isSuccessful()) {
                if (response.body() == null) {
                    onError();
                    return;
                }
                trainingSession = convertResourceToTrainingSession(response.body());
                fetchMeasurementsFromKeys();

            } else {
                onError();
                ToastUtils.showToast(ApplyCoachActivity.this, "Please try again", Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onFailure(Call<Resource> call, Throwable t) {
            appLogger.logDebug("onFailure");
            onError();
        }
    };
    private Callback<List<NGO>> getNGOsCallback = new Callback<List<NGO>>() {
        @Override
        public void onResponse(Call<List<NGO>> call, Response<List<NGO>> response) {
            if (response.isSuccessful()) {
                ngos = response.body();
                if (CollectionUtils.isEmpty(ngos)) {
                    onError();
                    return;
                }
                createNGOArrayAdapter();

            } else {
                ToastUtils.showToast(ApplyCoachActivity.this, "Response failed", Toast.LENGTH_SHORT);
                onError();
            }
        }

        @Override
        public void onFailure(Call<List<NGO>> call, Throwable t) {
            appLogger.logDebug("onFailure");
            onError();
        }
    };
    private Callback<List<Measurement>> measurementsFromKeysCallback = new Callback<List<Measurement>>() {
        @Override
        public void onResponse(Call<List<Measurement>> call, Response<List<Measurement>> response) {
            if (response.isSuccessful()) {
                List<Measurement> measurements = response.body();
                if (CollectionUtils.isEmpty(measurements)) {
                    return;
                }
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                for (Measurement measurement : measurements) {
                    RealmMeasurement realmMeasurement = new RealmMeasurement(measurement);
                    realm.copyToRealmOrUpdate(realmMeasurement);
                }
                realm.commitTransaction();
                realm.close();

                measurementReadingsAdapter = new MeasurementReadingAdapter(ApplyCoachActivity.this, trainingSession);
                recyclerView.setAdapter(measurementReadingsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ApplyCoachActivity.this));
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ApplyCoachActivity.this,
                        R.array.gender_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                genderSpinner.setAdapter(adapter);
                genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        appLogger.logInformation(String.valueOf(parent.getItemAtPosition(position)));
                        selectedGender[0] = String.valueOf(parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                applyAsCoach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        applyAsCoach();
                    }
                });
                onSuccess();

            } else {
                onError();
                ToastUtils.showToast(ApplyCoachActivity.this, "Response failed", Toast.LENGTH_SHORT);
            }


        }

        @Override
        public void onFailure(Call<List<Measurement>> call, Throwable t) {
            onError();
        }
    };
    private TextInputEditText firstNameEditText, middleNameEditText, lastNameEditText;

    private void applyAsCoach() {
        String firstName = firstNameEditText.getText().toString();
        String middleName = middleNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        Gender gender = getGender(selectedGender[0]);

        if (io.realm.internal.Util.isEmptyString(firstName)) {
            firstNameEditText.setError("This field is required");
            return;
        }
        if (io.realm.internal.Util.isEmptyString(lastName)) {
            lastNameEditText.setError("This field is required");
            return;
        }

        if (!measurementReadingsAdapter.verifyReadings()) {
            return;
        }

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("first_name", firstName);
            paramObject.put("middle_name", middleName);
            paramObject.put("last_name", lastName);
            paramObject.put("gender", gender.label);
            paramObject.put("ngo", selectedNGO.getKey());
            paramObject.put("is_active", true);

            JSONObject dataObject = new JSONObject();
            JSONArray array = new JSONArray();
            List<net.mavericklabs.bos.object.Measurement> measurementsWithReadings = measurementReadingsAdapter.getMeasurementsWithReadings();
            for (net.mavericklabs.bos.object.Measurement measurementWithReading : measurementsWithReadings) {
                JSONObject measurementObject = new JSONObject();
                measurementObject.put("key", measurementWithReading.getKey());
                measurementObject.put("value", measurementWithReading.getReading());
                array.put(measurementObject);
            }
            dataObject.put("measurements", array);
            paramObject.put("data", dataObject);

            progress = new ProgressDialog(ApplyCoachActivity.this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
            ApiClient.getApiInterface(ApplyCoachActivity.this)
                    .createCoachRequest(paramObject.toString()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    progress.dismiss();
                    if (response.isSuccessful()) {
                        ToastUtils.showToast(ApplyCoachActivity.this, "Application submitted", Toast.LENGTH_SHORT);
                        RealmHandler.clearRealmDatabase();
                        finish();
                    } else {
                        ToastUtils.showToast(ApplyCoachActivity.this, "Response failed", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    progress.dismiss();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_coach);
        this.setTitle("Apply as a coach");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ngoSpinner = findViewById(R.id.ngo_spinner);
        firstNameEditText = findViewById(R.id.edit_text_first_name);
        middleNameEditText = findViewById(R.id.edit_text_middle_name);
        lastNameEditText = findViewById(R.id.edit_text_last_name);
        genderSpinner = findViewById(R.id.spinner_gender);
        relativeLayout = findViewById(R.id.relative_layout_data);
        applyAsCoach = findViewById(R.id.button_apply_as_coach);
        recyclerView = findViewById(R.id.recycler_view_measurements);
        recyclerView.setLayoutManager(new LinearLayoutManager(ApplyCoachActivity.this));


        if (!NetworkConnection.isNetworkAvailable(getApplicationContext())) {
            ToastUtils.showToast(getApplicationContext(), "No internet", Toast.LENGTH_SHORT);
        } else {
            ApiClient.getApiInterface(getApplicationContext()).getNGOs().enqueue(getNGOsCallback);
        }


    }


    private void createNGOArrayAdapter() {
        ArrayList<String> ngoNames = new ArrayList<>();
        for (NGO ngo : ngos
        ) {
            ngoNames.add(ngo.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ngoNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ngoSpinner.setAdapter(adapter);
        ngoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNGO = ngos.get(position);
                progress = new ProgressDialog(ApplyCoachActivity.this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.setCancelable(false);
                progress.show();
                ApiClient.getApiInterface(ApplyCoachActivity.this)
                        .getCoachRegistrationForm(selectedNGO.getKey())
                        .enqueue(coachRegistrationFormCallback);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void onError() {
        relativeLayout.setVisibility(View.GONE);
        recyclerView.setAdapter(null);
        progress.dismiss();
    }


    private void onSuccess() {
        relativeLayout.setVisibility(View.VISIBLE);
        progress.dismiss();
    }

    private void fetchMeasurementsFromKeys() {
        ArrayList<String> measurementKeys = new ArrayList<>();
        for (net.mavericklabs.bos.object.Measurement measurement : trainingSession.getMeasurements()) {
            measurementKeys.add(measurement.getKey());
        }
        ApiClient.getApiInterface(ApplyCoachActivity.this)
                .getMeasurementsFromKeys(selectedNGO.getKey(), measurementKeys)
                .enqueue(measurementsFromKeysCallback);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
