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
import android.os.TestLooperManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.FilesAdapter;
import net.mavericklabs.bos.adapter.MeasurementAdapter;
import net.mavericklabs.bos.adapter.MeasurementReadingAdapter;
import net.mavericklabs.bos.adapter.ResourceAdapter;
import net.mavericklabs.bos.adapter.SelectAthleteAdapterForEvaluation;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.File;
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

import org.w3c.dom.Text;

import java.util.List;

import io.realm.Realm;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ACTIVITY_MODE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_EVALUATION_RESOURCE_TYPE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_EVALUATION_RESOURCE_UUID;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_IS_PART_OF_CURRICULUM;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_TRAINING_SESSION;

public class TrainingSessionActivity extends AppCompatActivity {
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private boolean isPartOfCurriculum;

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
        ActivityMode activityMode = Util.getActivityMode(getIntent().getStringExtra(BUNDLE_KEY_ACTIVITY_MODE));
        appLogger.logInformation("Activity Mode " + activityMode.label);
        appLogger.logInformation("trainingSession isEvaluated " + trainingSession.isEvaluated());
        EvaluationResourceType evaluationResourceType = Util.getEvaluationResourceType(getIntent().getStringExtra(BUNDLE_KEY_EVALUATION_RESOURCE_TYPE));
        isPartOfCurriculum = getIntent().getBooleanExtra(BUNDLE_KEY_IS_PART_OF_CURRICULUM, false);
        TextView readingsTextView = findViewById(R.id.text_view_label_readings);
        TextView athletesTextView = findViewById(R.id.text_view_label_athletes);
        TextView filesTextView = findViewById(R.id.text_view_label_files);
        CardView cardView = findViewById(R.id.card_view);
        TextView description = findViewById(R.id.text_view_description);
        if (!TextUtils.isEmpty(trainingSession.getLabel())) {
            setTitle(trainingSession.getLabel());
        }
        if (TextUtils.isEmpty(trainingSession.getDescription())) {
            cardView.setVisibility(View.GONE);
        } else {
            description.setText(trainingSession.getDescription());
        }
        Button evaluateTrainingSessionButton = findViewById(R.id.button_evaluate_training_session);
        TextView emptyView = findViewById(R.id.empty_view);
        RecyclerView measurementsRecyclerView = findViewById(R.id.recycler_view_measurements);
        RecyclerView filesRecyclerView = findViewById(R.id.recycler_view_files);
        RecyclerView usersRecyclerView = findViewById(R.id.recycler_view_users);
        measurementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<File> files = trainingSession.getFiles();
        if (files.size() == 0) {
            filesTextView.setVisibility(View.GONE);
        } else {
            filesRecyclerView.setAdapter(new FilesAdapter(this, trainingSession.getFiles()));
        }
        appLogger.logInformation(String.valueOf(files.size()));
        appLogger.logInformation("YOLO files");
        switch (activityMode) {
            case READ:
                readingsTextView.setText("Measurements for this session");
                athletesTextView.setVisibility(View.GONE);
                measurementsRecyclerView.setAdapter(new MeasurementAdapter(getApplicationContext(), trainingSession.getMeasurements(), activityMode));

                break;
            case EVALUATION:
                String evaluationResourceUUID = getIntent().getStringExtra(BUNDLE_KEY_EVALUATION_RESOURCE_UUID);
                appLogger.logInformation(evaluationResourceUUID);
                final RealmEvaluationResource realmEvaluationResource = RealmHandler.getEvaluationResourceByUUID(evaluationResourceUUID);
                final Curriculum curriculum = Util.convertRealmResourceToCurriculum(realmEvaluationResource);
                final MeasurementReadingAdapter measurementReadingAdapter =
                        new MeasurementReadingAdapter(getApplicationContext(), trainingSession);
                measurementsRecyclerView.setAdapter(measurementReadingAdapter);

                if (trainingSession.isEvaluated()) {
                    athletesTextView.setVisibility(View.GONE);
                    evaluateTrainingSessionButton.setVisibility(View.GONE);
                    usersRecyclerView.setVisibility(View.GONE);
                    filesTextView.setVisibility(View.GONE);
                    readingsTextView.setText("Readings collected below");


                } else {
                    usersRecyclerView.setVisibility(View.VISIBLE);
                    athletesTextView.setVisibility(View.VISIBLE);
                    evaluateTrainingSessionButton.setVisibility(View.VISIBLE);
                }
                // Check if evaluationResourceType
                switch (evaluationResourceType) {
                    case GROUP: {
                        List<RealmUser> athletes = Util.getAthletes(realmEvaluationResource.getGroup().getUsers());
                        final SelectAthleteAdapterForEvaluation selectAthleteAdapterForEvaluation = new SelectAthleteAdapterForEvaluation(athletes);
                        usersRecyclerView.setAdapter(selectAthleteAdapterForEvaluation);
                        evaluateTrainingSessionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!measurementReadingAdapter.verifyReadings()) {
                                    return;
                                }

                                if (realmEvaluationResource.getGroup() != null) {
                                    appLogger.logInformation("This is for a group.");
                                    List<RealmUser> selectedAthletes = selectAthleteAdapterForEvaluation.getSelectedAthletes();
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
                                                    athlete, selfRealmUser,
                                                    trainingSession.getUuid(),
                                                    realmEvaluationResource, measurementWithReading);
                                            realm.beginTransaction();
                                            realm.copyToRealm(realmReading);
                                            realm.commitTransaction();
                                        }

                                    }

                                    String data;
                                    boolean isEvaluated;
                                    if (isPartOfCurriculum) {
                                        data = Util.updateCurriculum(curriculum, trainingSession.getUuid(),
                                                measurementReadingAdapter.getMeasurementsWithReadings());
                                        isEvaluated = curriculum.isEvaluated();
                                    } else {
                                        data = Util.updateTrainingSession(trainingSession,
                                                measurementReadingAdapter.getMeasurementsWithReadings());
                                        isEvaluated = true;
                                    }
                                    realm.beginTransaction();
                                    realmEvaluationResource.setData(data);
                                    realmEvaluationResource.setEvaluated(isEvaluated);
                                    realmEvaluationResource.setSynced(false);
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
                        athletesTextView.setVisibility(View.GONE);
                        evaluateTrainingSessionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!measurementReadingAdapter.verifyReadings()) {
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
                                                athlete, selfRealmUser,
                                                trainingSession.getUuid(),
                                                realmEvaluationResource, measurementWithReading);
                                        realm.beginTransaction();
                                        realm.copyToRealm(realmReading);
                                        realm.commitTransaction();

                                    }

                                    // Check if resource is a curriculum or a standalone training session
                                    String data;
                                    boolean isEvaluated;
                                    if (isPartOfCurriculum) {
                                        data = Util.updateCurriculum(curriculum, trainingSession.getUuid(),
                                                measurementReadingAdapter.getMeasurementsWithReadings());
                                        isEvaluated = curriculum.isEvaluated();
                                    } else {
                                        data = Util.updateTrainingSession(trainingSession,
                                                measurementReadingAdapter.getMeasurementsWithReadings());
                                        isEvaluated = true;
                                    }

                                    realm.beginTransaction();
                                    realmEvaluationResource.setData(data);
                                    realmEvaluationResource.setEvaluated(isEvaluated);
                                    realmEvaluationResource.setSynced(false);
                                    realm.copyToRealmOrUpdate(realmEvaluationResource);
                                    realm.commitTransaction();
                                    realm.close();
                                    finish();
                                }
                            }
                        });
                        break;
                }
                break;
        }
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
