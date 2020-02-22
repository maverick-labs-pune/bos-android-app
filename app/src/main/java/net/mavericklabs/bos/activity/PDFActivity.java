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

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.SelectAthleteAdapter;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.ToastUtils;
import net.mavericklabs.bos.utils.Util;

import java.util.List;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_RESOURCE_KEY;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_URI;

public class PDFActivity extends AppCompatActivity {
    private AppLogger appLogger = new AppLogger(getClass().toString());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        setTitle("PDF activity");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String uri = getIntent().getStringExtra(BUNDLE_KEY_URI);
        String resourceKey = getIntent().getStringExtra(BUNDLE_KEY_RESOURCE_KEY);
        RealmResource realmResource = RealmHandler.getResourceByKey(resourceKey);
        setTitle(realmResource.getLabel());
        PDFView pdfView = findViewById(R.id.pdf_view);
        Uri uriData = Uri.parse(uri);
        pdfView.fromUri(uriData).load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return super.onCreateOptionsMenu(menu);
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
