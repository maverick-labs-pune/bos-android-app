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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.ResourceAdapter;
import net.mavericklabs.bos.adapter.UserAdapter;
import net.mavericklabs.bos.realm.RealmGroup;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.utils.AppLogger;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_GROUP_KEY;

public class GroupDetailActivity extends AppCompatActivity {
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private RecyclerView usersRecyclerView;
    private RecyclerView resourcesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        setTitle("Group Details");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String groupKey = getIntent().getStringExtra(BUNDLE_KEY_GROUP_KEY);
        RealmGroup realmGroup = RealmHandler.getGroup(groupKey);
        final ImageView usersExpandImageView = findViewById(R.id.image_view_users_expand);
        final ImageView resourcesExpandImageView = findViewById(R.id.image_view_resources_expand);
        TextView labelTextView = findViewById(R.id.text_view_label);
//        Button viewResourcesButton = findViewById(R.id.button_view_resources);
//        Button assignResourcesButton = findViewById(R.id.button_assign_resources);
        labelTextView.setText(realmGroup.getLabel());
        usersRecyclerView = findViewById(R.id.recycler_view_users);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        usersRecyclerView.setAdapter(new UserAdapter(realmGroup.getUsers()));

        resourcesRecyclerView = findViewById(R.id.recycler_view_resources);
        resourcesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        resourcesRecyclerView.setAdapter(new ResourceAdapter(getApplicationContext(), realmGroup.getResources()));

        usersExpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersRecyclerView.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    usersRecyclerView.setVisibility(View.VISIBLE);
                    usersExpandImageView.setImageResource(R.drawable.baseline_expand_less_black_24);
                } else {
                    // it's expanded - collapse it
                    usersRecyclerView.setVisibility(View.GONE);
                    usersExpandImageView.setImageResource(R.drawable.baseline_expand_more_black_24);
                }
            }
        });
        resourcesExpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resourcesRecyclerView.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    resourcesRecyclerView.setVisibility(View.VISIBLE);
                    resourcesExpandImageView.setImageResource(R.drawable.baseline_expand_less_black_24);
                } else {
                    // it's expanded - collapse it
                    resourcesRecyclerView.setVisibility(View.GONE);
                    resourcesExpandImageView.setImageResource(R.drawable.baseline_expand_more_black_24);
                }
            }
        });
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
