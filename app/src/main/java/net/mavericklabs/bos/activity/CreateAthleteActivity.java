/*
 * Copyright (c) 2020. Maverick Labs
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

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.MeasurementAdapter;
import net.mavericklabs.bos.adapter.MeasurementReadingAdapter;
import net.mavericklabs.bos.model.NGO;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmMeasurement;
import net.mavericklabs.bos.realm.RealmReading;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.retrofit.ApiClient;
import net.mavericklabs.bos.utils.ActivityMode;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.NetworkConnection;
import net.mavericklabs.bos.utils.ToastUtils;
import net.mavericklabs.bos.utils.UserRole;
import net.mavericklabs.bos.utils.Util;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;
import static net.mavericklabs.bos.utils.Util.convertRealmResourceToTrainingSession;

public class CreateAthleteActivity extends AppCompatActivity {

    private AppLogger appLogger = new AppLogger(getClass().toString());
    private RecyclerView measurementsRecyclerView;
    private TextView emptyView;
    private MeasurementReadingAdapter measurementReadingAdapter;
    private TextInputEditText firstNameEditText, middleNameEditText, lastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_athlete);
        setTitle("Create an athlete");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button createAthleteButton = findViewById(R.id.button_create_athlete);
        measurementsRecyclerView = findViewById(R.id.recycler_view_measurements);
        emptyView = findViewById(R.id.empty_view);
        firstNameEditText = findViewById(R.id.edit_text_first_name);
        middleNameEditText = findViewById(R.id.edit_text_middle_name);
        lastNameEditText = findViewById(R.id.edit_text_last_name);
        measurementsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        final RealmResource realmResource = RealmHandler.getAthleteRegistrationForm();
        Util.setEmptyMessageIfNeeded(realmResource, measurementsRecyclerView, emptyView);

        TrainingSession trainingSession = convertRealmResourceToTrainingSession(realmResource);
        measurementReadingAdapter = new MeasurementReadingAdapter(getApplicationContext(), trainingSession.getMeasurements());
        measurementsRecyclerView.setAdapter(measurementReadingAdapter);


        createAthleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verifyBasicInformation()) {
                    ToastUtils.showToast(getApplicationContext(), "Incomplete basic information", Toast.LENGTH_SHORT);
                    return;

                }
                if (!measurementReadingAdapter.verifyReadings()) {
                    ToastUtils.showToast(getApplicationContext(), "Incomplete extra information", Toast.LENGTH_SHORT);
                    return;
                }

                String firstName = firstNameEditText.getText().toString();
                String middleName = middleNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
//              Create an offline athlete
//              Save data and mark as evaluated
                RealmUser selfRealmUser = RealmHandler.getSelfRealmUser();
                if (selfRealmUser == null) {
                    appLogger.logError("Self user is null");
                    return;
                }
                Realm realm = Realm.getDefaultInstance();
                RealmUser newAthlete = new RealmUser(firstName, middleName, lastName, UserRole.ATHLETE, selfRealmUser.getNgo());
                realm.beginTransaction();
                newAthlete = realm.copyToRealm(newAthlete);
                realm.commitTransaction();

                List<net.mavericklabs.bos.object.Measurement> measurementsWithReadings = measurementReadingAdapter.getMeasurementsWithReadings();
                for (Measurement measurementWithReading : measurementsWithReadings) {
                    RealmMeasurement realmMeasurement = RealmHandler.getMeasurementFromKey(measurementWithReading.getKey());

                    // Add reading
                    realm.beginTransaction();
                    RealmReading realmReading = new RealmReading(realmMeasurement,
                            newAthlete, selfRealmUser, realmResource,
                            null, measurementWithReading);
                    realm.copyToRealm(realmReading);
                    realm.commitTransaction();
                }
                realm.close();
                finish();

            }
        });
    }

    private boolean verifyBasicInformation() {
        if (TextUtils.isEmpty(firstNameEditText.getText())) {
            return false;
        }
        if (TextUtils.isEmpty(lastNameEditText.getText())) {
            return false;
        }
        return true;
    }


}