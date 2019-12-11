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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.CurriculumDayAdapter;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.Util;

public class CurriculumActivity extends AppCompatActivity {
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private RecyclerView recyclerView;
    private TextView emptyView;
    private RealmResource realmResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculum);
        setTitle("Curriculum");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String resourceKey = getIntent().getStringExtra("resourceKey");
        realmResource = RealmHandler.getResourceByKey(resourceKey);
        TextView label = findViewById(R.id.text_view_label);
        TextView description = findViewById(R.id.text_view_description);
        recyclerView = findViewById(R.id.recycler_view_days);
        emptyView = findViewById(R.id.empty_view);


        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Curriculum curriculum = Util.convertRealmResourceToCurriculum(realmResource);

        label.setText(curriculum.getLabel());
        description.setText(curriculum.getDescription());
        appLogger.logDebug(realmResource.getData());
        appLogger.logDebug(String.valueOf(curriculum.getDays().size()));
        appLogger.logDebug(curriculum.getDescription());

        recyclerView.setAdapter(new CurriculumDayAdapter(getApplicationContext(), curriculum));
        Util.setEmptyMessageIfNeeded(recyclerView, recyclerView, emptyView);
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
