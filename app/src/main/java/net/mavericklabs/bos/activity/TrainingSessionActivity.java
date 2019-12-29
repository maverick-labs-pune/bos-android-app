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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.MeasurementAdapter;
import net.mavericklabs.bos.adapter.MeasurementReadingAdapter;
import net.mavericklabs.bos.adapter.SelectAthleteAdapter;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmMeasurement;
import net.mavericklabs.bos.realm.RealmReading;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.ActivityMode;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.EvaluationResourceType;
import net.mavericklabs.bos.utils.ToastUtils;
import net.mavericklabs.bos.utils.Util;

import java.util.List;

import io.realm.Realm;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ACTIVITY_MODE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_EVALUATION_RESOURCE_TYPE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_EVALUATION_RESOURCE_UUID;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_TRAINING_SESSION;

public class TrainingSessionActivity extends AppCompatActivity {
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private RecyclerView measurementsRecyclerView;
    private TextView emptyView;
    private ActivityMode activityMode;
    private RecyclerView usersRecyclerView;
    private EvaluationResourceType evaluationResourceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_session);
        setTitle("Training session");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final TrainingSession trainingSession = getIntent().getParcelableExtra(BUNDLE_KEY_TRAINING_SESSION);
        activityMode = Util.getActivityMode(getIntent().getStringExtra(BUNDLE_KEY_ACTIVITY_MODE));
        evaluationResourceType = Util.getEvaluationResourceType(getIntent().getStringExtra(BUNDLE_KEY_EVALUATION_RESOURCE_TYPE));
        TextView label = findViewById(R.id.text_view_label);
        TextView description = findViewById(R.id.text_view_description);
        Button evaluateTrainingSessionButton = findViewById(R.id.button_evaluate_training_session);
        measurementsRecyclerView = findViewById(R.id.recycler_view_measurements);
        usersRecyclerView = findViewById(R.id.recycler_view_users);
        emptyView = findViewById(R.id.empty_view);
        measurementsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        switch (activityMode) {
            case READ:
                measurementsRecyclerView.setAdapter(new MeasurementAdapter(getApplicationContext(), trainingSession.getMeasurements(), activityMode));
                break;
            case EVALUATION:
                String evaluationResourceUUID = getIntent().getStringExtra(BUNDLE_KEY_EVALUATION_RESOURCE_UUID);
                appLogger.logInformation(evaluationResourceUUID);
                final RealmEvaluationResource realmEvaluationResource = RealmHandler.getEvaluationResourceByUUID(evaluationResourceUUID);
                final Curriculum curriculum = Util.convertRealmResourceToCurriculum(realmEvaluationResource);
                final MeasurementReadingAdapter measurementReadingAdapter =
                        new MeasurementReadingAdapter(getApplicationContext(), trainingSession.getMeasurements(), curriculum);
                measurementsRecyclerView.setAdapter(measurementReadingAdapter);

                usersRecyclerView.setVisibility(View.VISIBLE);

                // Check if evaluationResourceType
                switch (evaluationResourceType) {
                    case GROUP: {
                        List<RealmUser> athletes = Util.getAthletes(realmEvaluationResource.getGroup().getUsers());
                        final SelectAthleteAdapter selectAthleteAdapter = new SelectAthleteAdapter(athletes);
                        usersRecyclerView.setAdapter(selectAthleteAdapter);
                        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        evaluateTrainingSessionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!measurementReadingAdapter.verifyReadings()) {
                                    ToastUtils.showToast(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT);
                                    return;
                                }
//                        ToastUtils.showToast(getApplicationContext(), "Correct", Toast.LENGTH_SHORT);

                                if (realmEvaluationResource.getGroup() != null) {
                                    appLogger.logInformation("This is for a group.");
                                    List<RealmUser> selectedAthletes = selectAthleteAdapter.getSelectedAthletes();
                                    if (selectedAthletes.size() == 0) {
                                        ToastUtils.showToast(getApplicationContext(), "Did not select any athlete", Toast.LENGTH_SHORT);
                                        return;
                                    }
//                                Save data and mark as evaluated
                                    RealmUser selfRealmUser = RealmHandler.getSelfRealmUser();
                                    if (selfRealmUser == null) {
                                        appLogger.logError("Self user is null");
                                        return;
                                    }
                                    List<Measurement> measurementsWithReadings = measurementReadingAdapter.getMeasurementsWithReadings();
                                    Realm realm = Realm.getDefaultInstance();

                                    for (Measurement measurementWithReading : measurementsWithReadings) {
                                        RealmMeasurement realmMeasurement = RealmHandler.getMeasurementFromKey(measurementWithReading.getKey());
                                        for (RealmUser athlete : selectedAthletes) {

                                            // Add reading
                                            RealmReading realmReading = new RealmReading(realmMeasurement,
                                                    athlete, selfRealmUser, realmEvaluationResource, measurementWithReading);
                                            realm.beginTransaction();
                                            realm.copyToRealm(realmReading);
                                            realm.commitTransaction();
                                        }

                                    }
                                    String data = Util.updateCurriculum(curriculum, trainingSession.getUuid(),
                                            measurementReadingAdapter.getMeasurementsWithReadings());
                                    realm.beginTransaction();
                                    realmEvaluationResource.setData(data);
                                    realm.copyToRealmOrUpdate(realmEvaluationResource);
                                    realm.commitTransaction();
                                    realm.close();
                                    finish();

                                }

                            }
                        });
                    }
                    break;
                    case USER:

                        final RealmUser athlete = realmEvaluationResource.getUser();
                        usersRecyclerView.setVisibility(View.GONE);
                        evaluateTrainingSessionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!measurementReadingAdapter.verifyReadings()) {
                                    ToastUtils.showToast(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT);
                                    return;
                                }

                                if (athlete != null) {
                                    appLogger.logInformation("This is for an athlete.");
//                                Save data and mark as evaluated
                                    RealmUser selfRealmUser = RealmHandler.getSelfRealmUser();
                                    if (selfRealmUser == null) {
                                        appLogger.logError("Self user is null");
                                        return;
                                    }
                                    List<Measurement> measurementsWithReadings = measurementReadingAdapter.getMeasurementsWithReadings();
                                    Realm realm = Realm.getDefaultInstance();

                                    for (Measurement measurementWithReading : measurementsWithReadings) {
                                        RealmMeasurement realmMeasurement = RealmHandler.getMeasurementFromKey(measurementWithReading.getKey());

                                        // Add reading
                                        RealmReading realmReading = new RealmReading(realmMeasurement,
                                                athlete, selfRealmUser, realmEvaluationResource, measurementWithReading);
                                        realm.beginTransaction();
                                        realm.copyToRealm(realmReading);
                                        realm.commitTransaction();

                                    }
                                    String data = Util.updateCurriculum(curriculum, trainingSession.getUuid(),
                                            measurementReadingAdapter.getMeasurementsWithReadings());
                                    realm.beginTransaction();
                                    realmEvaluationResource.setData(data);
                                    realm.copyToRealmOrUpdate(realmEvaluationResource);
                                    realm.commitTransaction();
                                    realm.close();
                                    finish();

                                }
                            }
                        });

                        break;

                }

                evaluateTrainingSessionButton.setVisibility(View.VISIBLE);

                break;
        }
        appLogger.logDebug(String.valueOf(trainingSession.getMeasurements().size()));

        Util.setEmptyMessageIfNeeded(measurementsRecyclerView, measurementsRecyclerView, emptyView);
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
