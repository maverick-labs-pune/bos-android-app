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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.AthleteAdapter;
import net.mavericklabs.bos.adapter.AthleteReadingAdapter;
import net.mavericklabs.bos.adapter.SelectAthleteAdapter;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmReading;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.ToastUtils;
import net.mavericklabs.bos.utils.Util;

import java.util.List;
import java.util.zip.Inflater;

import io.realm.Realm;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ATHLETE_UUID;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_RESOURCE_KEY;

public class SelectAthleteActivity extends AppCompatActivity {
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private RecyclerView recyclerView;
    private TextView emptyView;
    private SelectAthleteAdapter selectAthleteAdapter;
    private RealmResource realmResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_athlete);
        setTitle("Select athlete");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String resourceKey = getIntent().getStringExtra(BUNDLE_KEY_RESOURCE_KEY);
        realmResource = RealmHandler.getResourceByKey(resourceKey);
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);
        List<RealmUser> athletes = RealmHandler.getAllAthletes();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectAthleteAdapter = new SelectAthleteAdapter(athletes);
        recyclerView.setAdapter(selectAthleteAdapter);
        Util.setEmptyMessageIfNeeded(athletes,recyclerView,emptyView);
        appLogger.logInformation(String.valueOf(athletes.size()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.select_athlete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        } if (item.getItemId() == R.id.done) {
            List<RealmUser> selectedRealmUsers =
                    selectAthleteAdapter.getSelectedAthletes();
            if (selectedRealmUsers.size() == 0){
                ToastUtils.showToast(this,
                        "Please select at least one athlete",Toast.LENGTH_SHORT);
                return true;
            }
            for (RealmUser selectedRealmUser: selectedRealmUsers){
                RealmEvaluationResource realmEvaluationResource =
                        new RealmEvaluationResource(realmResource, selectedRealmUser);
                RealmHandler.copyToRealm(realmEvaluationResource);

            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
