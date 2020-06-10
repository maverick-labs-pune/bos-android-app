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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.MeasurementReadingAdapter;
import net.mavericklabs.bos.object.Measurement;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmMeasurement;
import net.mavericklabs.bos.realm.RealmReading;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.Gender;
import net.mavericklabs.bos.utils.UserRole;
import net.mavericklabs.bos.utils.Util;

import java.util.List;

import io.realm.Realm;

import static net.mavericklabs.bos.utils.Util.convertRealmResourceToTrainingSession;
import static net.mavericklabs.bos.utils.Util.getGender;

public class CreateAthleteActivity extends AppCompatActivity {

    private AppLogger appLogger = new AppLogger(getClass().toString());
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
        RecyclerView measurementsRecyclerView = findViewById(R.id.recycler_view_measurements);
        TextView emptyView = findViewById(R.id.empty_view);
        firstNameEditText = findViewById(R.id.edit_text_first_name);
        middleNameEditText = findViewById(R.id.edit_text_middle_name);
        lastNameEditText = findViewById(R.id.edit_text_last_name);
        final String[] selectedGender = {null};
        measurementsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        final Spinner spinner = findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appLogger.logInformation(String.valueOf(parent.getItemAtPosition(position)));
                selectedGender[0] = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final RealmResource realmResource = RealmHandler.getAthleteRegistrationForm();
        Util.setEmptyMessageIfNeeded(realmResource, measurementsRecyclerView, emptyView);

        TrainingSession trainingSession = convertRealmResourceToTrainingSession(realmResource);
        measurementReadingAdapter = new MeasurementReadingAdapter(getApplicationContext(), trainingSession);
        measurementsRecyclerView.setAdapter(measurementReadingAdapter);


        createAthleteButton.setOnClickListener(v -> {
            if (!verifyBasicInformation()) {
                return;
            }
            if (!measurementReadingAdapter.verifyReadings()) {
                return;
            }

            String firstName = firstNameEditText.getText().toString();
            String middleName = middleNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            Gender gender = getGender(selectedGender[0]);
//              Create an offline athlete
//              Save data and mark as evaluated
            RealmUser selfRealmUser = RealmHandler.getSelfRealmUser();
            if (selfRealmUser == null) {
                appLogger.logError("Self user is null");
                return;
            }
            Realm realm = Realm.getDefaultInstance();
            RealmUser newAthlete = new RealmUser(firstName,
                    middleName,
                    lastName,
                    UserRole.ATHLETE,
                    gender,
                    selfRealmUser.getNgo());
            realm.beginTransaction();
            newAthlete = realm.copyToRealm(newAthlete);
            realm.commitTransaction();

            List<Measurement> measurementsWithReadings = measurementReadingAdapter.getMeasurementsWithReadings();
            for (Measurement measurementWithReading : measurementsWithReadings) {
                RealmMeasurement realmMeasurement = RealmHandler.getMeasurementFromKey(measurementWithReading.getKey());

                // Add reading
                realm.beginTransaction();
                RealmReading realmReading = new RealmReading(realmMeasurement,
                        newAthlete, selfRealmUser, null,
                        null, measurementWithReading);
                realm.copyToRealm(realmReading);
                realm.commitTransaction();
            }
            realm.close();
            finish();

        });
    }

    private boolean verifyBasicInformation() {
        if (TextUtils.isEmpty(firstNameEditText.getText())) {
            firstNameEditText.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(lastNameEditText.getText())) {
            lastNameEditText.setError("This field is required");
            return false;
        }
        return true;
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
