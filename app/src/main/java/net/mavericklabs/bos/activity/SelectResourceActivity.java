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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.ResourceAdapter;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.EvaluationResourceType;
import net.mavericklabs.bos.utils.Util;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ATHLETE_KEY;

public class SelectResourceActivity extends AppCompatActivity {
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private RecyclerView recyclerView;
    private TextView emptyView;
    private String athleteKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        setTitle("Select resource");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        athleteKey = getIntent().getStringExtra(BUNDLE_KEY_ATHLETE_KEY);
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);


    }

    @Override
    protected void onResume() {
        super.onResume();
        RealmUser realmUser = RealmHandler.getAthleteByKey(athleteKey);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(new ResourceAdapter(SelectResourceActivity.this,
                realmUser.getResources(), EvaluationResourceType.USER, realmUser));

        Util.setEmptyMessageIfNeeded(realmUser.getResources(), recyclerView, emptyView);

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
