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
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.model.NGO;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.retrofit.ApiClient;
import net.mavericklabs.bos.util.Logger;
import net.mavericklabs.bos.util.NetworkConnection;
import net.mavericklabs.bos.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyCoachActivity extends AppCompatActivity {

    private Spinner ngo_spinner;
    List<NGO> ngos;
    private Resource resource;
    private ConstraintLayout constraintLayout;
    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_coach);
//        Toolbar toolbar = findViewById(R.id.tool_bar);
//        setSupportActionBar(toolbar);
        this.setTitle("Apply as a coach");

        ngo_spinner = findViewById(R.id.ngo_spinner);
        constraintLayout = findViewById(R.id.root);

        if (!NetworkConnection.isNetworkAvailable(getApplicationContext())){
            ToastUtils.showToast(getApplicationContext(),"No internet",Toast.LENGTH_SHORT);
        }else{

        }


        ApiClient.getApiInterface(getApplicationContext()).getNGOs().enqueue(new Callback<List<NGO>>() {
            @Override
            public void onResponse(Call<List<NGO>> call, Response<List<NGO>> response) {
                if (response.isSuccessful()) {
                    ngos = response.body();
                    createArrayAdapter();

                } else {
//                    Toast.makeText(getContext(), getTranslation(locale, "OLD_PASSWORD_IS_WRONG"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NGO>> call, Throwable t) {
                Logger.d("onFailure");
            }
        });

    }

    private void createArrayAdapter() {
        ArrayList<String> ngoNames = new ArrayList<>();
        for (int i = 0; i < ngos.size(); i++) {
            ngoNames.add(ngos.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ngoNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ngo_spinner.setAdapter(adapter);
        ngo_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NGO selectedNGO = ngos.get(position);

                ApiClient.getApiInterface(getApplicationContext()).getCoachRegistrationForm(selectedNGO.getKey()).enqueue(new Callback<Resource>() {
                    @Override
                    public void onResponse(Call<Resource> call, Response<Resource> response) {
                        if (response.isSuccessful()) {
                            resource = response.body();
                            createFormElements();
                            Toast.makeText(getApplicationContext(), "Received resource", Toast.LENGTH_SHORT).show();

                        } else {
//                    Toast.makeText(getContext(), getTranslation(locale, "OLD_PASSWORD_IS_WRONG"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Resource> call, Throwable t) {
                        Logger.d("onFailure");
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createFormElements() {

        int previousViewID = 0;
        JsonArray measurements = resource.getData().getAsJsonArray("measurements");
        for (int i = 0; i < measurements.size(); i++) {
            JsonObject measurement = (JsonObject) measurements.get(i);
            JsonElement label = measurement.get("key");
            JsonElement key = measurement.get("key");
            EditText view = new EditText(this);
            int viewID = View.generateViewId();
            view.setId(viewID);
            view.setHint(label.getAsString());
            view.setTag(key.getAsString());
            view.setMaxLines(1);


            view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            constraintLayout.addView(view);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            if (i==0){
                constraintSet.connect(view.getId(), ConstraintSet.TOP, ngo_spinner.getId(), ConstraintSet.BOTTOM, 18);
            }else{
                constraintSet.connect(view.getId(), ConstraintSet.TOP, previousViewID, ConstraintSet.BOTTOM, 18);
            }
            constraintSet.connect(view.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, 18);
            constraintSet.connect(view.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, 18);

            constraintSet.applyTo(constraintLayout);
            previousViewID = viewID;
        }

    }
}
