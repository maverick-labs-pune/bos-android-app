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

package net.mavericklabs.bos.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.mavericklabs.bos.BosApplication;
import net.mavericklabs.bos.R;
import net.mavericklabs.bos.adapter.DailyPlannerAdapter;
import net.mavericklabs.bos.model.LoginResponse;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.sync.SyncAdapter;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.NetworkConnection;
import net.mavericklabs.bos.utils.SharedPreferenceUtil;
import net.mavericklabs.bos.utils.Util;

import java.util.List;

import static net.mavericklabs.bos.realm.RealmHandler.getTranslation;


public class DailyPlannerFragment extends Fragment {

    private LoginResponse loginResponse;
    private String locale;
    private final Object updateLock = new Object();
    private ContentObserver syncCompletedObserver;
    private ContentObserver dailyPlannerObserver;
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_daily_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginResponse = RealmHandler.getLoginResponse();
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        FloatingActionButton floatActionButtonSync = view.findViewById(R.id.floating_action_button_sync);

        floatActionButtonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                if (context != null) {

                    if (NetworkConnection.isNetworkAvailable(context)) {
                        boolean result = SyncAdapter.requestSync(context, SyncAdapter.SYNC_EVERYTHING);
                        if (!result) {
                            Toast.makeText(context, getTranslation(locale, "SYNC_ALREADY_RUNNING"), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, getTranslation(locale, "SYNC_STARTED"), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(context, getTranslation(locale, "NO_NETWORK"), Toast.LENGTH_SHORT).show();

                    }


                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        List<RealmEvaluationResource> evaluationResources = RealmHandler.getUnevaluatedOrUnSyncedResources();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new DailyPlannerAdapter(getContext(), evaluationResources));
        Util.setEmptyMessageIfNeeded(evaluationResources, recyclerView, emptyView);

        setTranslations();
        if (loginResponse != null) {

            syncCompletedObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                public void onChange(boolean selfChange) {
                    appLogger.logDebug("On change syncCompletedObserver");
                    synchronized (updateLock) {
                        Toast.makeText(getContext(), getTranslation(locale, "SYNC_COMPLETE"), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            dailyPlannerObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                public void onChange(boolean selfChange) {
                    appLogger.logDebug("On change syncCompletedObserver");
                    synchronized (updateLock) {
                        List<RealmEvaluationResource> evaluationResources = RealmHandler.getUnevaluatedOrUnSyncedResources();
                        recyclerView.setAdapter(new DailyPlannerAdapter(getContext(), evaluationResources));
                        Util.setEmptyMessageIfNeeded(evaluationResources, recyclerView, emptyView);
                    }
                }
            };
            Uri syncCompletedURI = Uri.withAppendedPath(BosApplication.BASE_URI,
                    BosApplication.SYNC_COMPLETED);
            Uri dailyPlannerURI = Uri.withAppendedPath(BosApplication.BASE_URI,
                    BosApplication.DAILY_PLANNER);
            Activity activity = getActivity();
            if (activity != null) {
                ContentResolver contentResolver = activity.getContentResolver();
                contentResolver.registerContentObserver(syncCompletedURI,
                        true, syncCompletedObserver);
                contentResolver.registerContentObserver(dailyPlannerURI,
                        true, dailyPlannerObserver);
                appLogger.logDebug("Registered observers");
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Activity activity = getActivity();
        if (activity != null) {
            ContentResolver contentResolver = activity.getContentResolver();
            if (syncCompletedObserver != null) {
                contentResolver.unregisterContentObserver(syncCompletedObserver);
            }
            if (dailyPlannerObserver != null) {
                contentResolver.unregisterContentObserver(dailyPlannerObserver);
            }
        }
    }

    private void setTranslations() {
        locale = SharedPreferenceUtil.getLocale(getContext());
    }

}
