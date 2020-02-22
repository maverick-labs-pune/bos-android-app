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

import android.app.MediaRouteButton;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.CollectionUtils;
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.MeasurementReadingAdapter;
import net.mavericklabs.bos.model.Measurement;
import net.mavericklabs.bos.model.NGO;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.realm.RealmMeasurement;
import net.mavericklabs.bos.retrofit.ApiClient;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.NetworkConnection;
import net.mavericklabs.bos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.mavericklabs.bos.utils.Util.convertResourceToTrainingSession;

public class ApplyCoachActivity extends AppCompatActivity {

    private Spinner ngo_spinner;
    List<NGO> ngos;
    private boolean isLoading = true;
    private NGO selectedNGO;
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private TrainingSession trainingSession;
    private RecyclerView recyclerView;
    private MeasurementReadingAdapter measurementsReadingAdapter;
    private RelativeLayout relativeLayout;
    private RoundedHorizontalProgressBar progressBar;


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
                createArrayAdapter();

            } else {
                ToastUtils.showToast(ApplyCoachActivity.this, "Response failed", Toast.LENGTH_SHORT);
                onError();
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

                measurementsReadingAdapter = new MeasurementReadingAdapter(ApplyCoachActivity.this, trainingSession);
                recyclerView.setAdapter(measurementsReadingAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ApplyCoachActivity.this));
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_coach);
        this.setTitle("Apply as a coach");
        ngo_spinner = findViewById(R.id.ngo_spinner);
        relativeLayout = findViewById(R.id.relative_layout_data);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view_measurements);
        recyclerView.setLayoutManager(new LinearLayoutManager(ApplyCoachActivity.this));

        if (!NetworkConnection.isNetworkAvailable(getApplicationContext())) {
            ToastUtils.showToast(getApplicationContext(), "No internet", Toast.LENGTH_SHORT);
        } else {
            ApiClient.getApiInterface(getApplicationContext()).getNGOs().enqueue(getNGOsCallback);
        }
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
                progressBar.setVisibility(View.VISIBLE);

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
        progressBar.setVisibility(View.GONE);
    }


    private void onSuccess() {
        relativeLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void fetchMeasurementsFromKeys() {
        ArrayList<String> measurementKeys = new ArrayList<>();
        for (net.mavericklabs.bos.object.Measurement measurement : trainingSession.getMeasurements()) {
            measurementKeys.add(measurement.getKey());
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApiClient.getApiInterface(ApplyCoachActivity.this)
                .getMeasurementsFromKeys(selectedNGO.getKey(), measurementKeys)
                .enqueue(measurementsFromKeysCallback);

    }

}
