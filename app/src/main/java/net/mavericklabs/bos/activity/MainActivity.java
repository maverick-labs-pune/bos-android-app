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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import net.mavericklabs.bos.fragment.AthleteFragment;
import net.mavericklabs.bos.fragment.ChangeLanguageFragment;
import net.mavericklabs.bos.fragment.GroupFragment;
import net.mavericklabs.bos.fragment.DailyPlannerFragment;
import net.mavericklabs.bos.fragment.ResetPasswordFragment;
import net.mavericklabs.bos.fragment.ResourceFragment;
import net.mavericklabs.bos.sync.SyncAdapter;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.Constants;
import net.mavericklabs.bos.utils.NetworkConnection;
import net.mavericklabs.bos.utils.SharedPreferenceUtil;
import net.mavericklabs.bos.R;
import net.mavericklabs.bos.model.LoginResponse;
import net.mavericklabs.bos.realm.RealmHandler;

import io.realm.Realm;

import static net.mavericklabs.bos.realm.RealmHandler.clearRealmDatabase;
import static net.mavericklabs.bos.realm.RealmHandler.getTranslation;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ChangeLanguageListener {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    boolean doubleBackToExitPressedOnce = false;
    private String locale;
    private TextView textViewUserName;
    private AppLogger appLogger = new AppLogger(getClass().toString());
    private TextView textViewNGOName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Realm realm = Realm.getDefaultInstance();
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        locale = SharedPreferenceUtil.getLocale(getApplicationContext());
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        textViewNGOName = headerView.findViewById(R.id.text_view_ngo_name);
        textViewUserName = headerView.findViewById(R.id.text_view_user_name);
        selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));

        LoginResponse loginResponse = RealmHandler.getLoginResponse();
        if (loginResponse != null) {
            languageChanged();
            String name = loginResponse.getFirstName() + " " + loginResponse.getLastName();
            textViewUserName.setText(name);
            textViewNGOName.setText(loginResponse.getNgoName());
            if (NetworkConnection.isNetworkAvailable(this)) {
                boolean result = SyncAdapter.requestSync(this, SyncAdapter.SYNC_EVERYTHING);
                if (!result) {
                    Toast.makeText(this, getTranslation(locale, "SYNC_ALREADY_RUNNING"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getTranslation(locale, "SYNC_STARTED"), Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(this, getTranslation(locale, "NO_NETWORK"), Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getTranslation(locale, "LABEL_EXIT_APP"), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);
        return true;
    }

    private void selectDrawerItem(MenuItem item) {
        if (item.isChecked() && item.getItemId() != R.id.nav_language) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        String locale = SharedPreferenceUtil.getLocale(getApplicationContext());
        Class fragmentClass = null;
        Fragment fragment = null;
        FragmentTransaction fragmentTransaction;
        String title = "";
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                fragmentClass = DailyPlannerFragment.class;
//                title = getTranslation(locale, "SESSION_FRAGMENT_TITLE");
                break;
            case R.id.nav_athletes:
                fragmentClass = AthleteFragment.class;
                break;
            case R.id.nav_resources:
                fragmentClass = ResourceFragment.class;
                break;
            case R.id.nav_groups:
                fragmentClass = GroupFragment.class;
                break;
            case R.id.nav_change_password:
                fragmentClass = ResetPasswordFragment.class;
                title = getTranslation(locale, "RESET_PASSWORD_TITLE");
                break;
            case R.id.nav_language:
                fragmentClass = ChangeLanguageFragment.class;
                title = getTranslation(locale, "CHANGE_LANGUAGE_TITLE");
                break;
            case R.id.nav_logout:
                boolean isSyncActive = SyncAdapter.isSyncActive(getApplicationContext());
                if (isSyncActive) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("LABEL_LOGOUT_ERROR");
                    builder.setMessage("SYNC_ALREADY_RUNNING");
                    builder.setCancelable(false);
                    builder.setNeutralButton("LABEL_OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                    return;
                }

                clearRealmDatabase();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
//            case R.id.nav_about:
//                fragmentClass = AboutFragment.class;
//                title = getTranslation(locale, "ABOUT_TITLE");
//                break;
            default:
                break;
        }

        if (fragmentClass != null) {
            item.setChecked(true);
            setTitle(title);
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            boolean fragmentPopped = fragmentManager.popBackStackImmediate(title, 0);
            if (!fragmentPopped) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content, fragment, title);
                fragmentTransaction.addToBackStack(title);
                fragmentTransaction.commit();
            }

            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.content);
                    appLogger.logDebug(" count onBackStackChanged" + getSupportFragmentManager().getBackStackEntryCount());
                    updateTitleAndDrawer(f);
                }
            });
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    private void logout() {
        RealmHandler.clearRealmDatabase();
        SharedPreferenceUtil.clearSharedPrefs(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void updateTitleAndDrawer(Fragment fragment) {
        String locale = SharedPreferenceUtil.getLocale(getApplicationContext());
        String name = fragment.getClass().getName();
        String title = "";
        int id = R.id.nav_home;
        if (name.equals(DailyPlannerFragment.class.getName())) {
            title = getTranslation(locale, "Daily planner");
            id = R.id.nav_home;
        } else if (name.equals(ResetPasswordFragment.class.getName())) {
            title = getTranslation(locale, "Reset password");
            id = R.id.nav_change_password;
        } else if (name.equals(ChangeLanguageFragment.class.getName())) {
            title = getTranslation(locale, "Change language");
            id = R.id.nav_language;
        }else if (name.equals(AthleteFragment.class.getName())) {
//            title = getTranslation(locale, "CHANGE_LANGUAGE_TITLE");
            title = "Athlete";
            id = R.id.nav_athletes;
        }else if (name.equals(ResourceFragment.class.getName())) {
//            title = getTranslation(locale, "CHANGE_LANGUAGE_TITLE");
            title = "Resource";
            id = R.id.nav_resources;
        }else if (name.equals(GroupFragment.class.getName())) {
//            title = getTranslation(locale, "CHANGE_LANGUAGE_TITLE");
            title = "Group";
            id = R.id.nav_groups;
        }
        setTitle(title);
        navigationView.setCheckedItem(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.session_details_request_code && resultCode == RESULT_OK) {
            if (NetworkConnection.isNetworkAvailable(this)) {
                boolean result = SyncAdapter.requestSync(this, SyncAdapter.SYNC_SESSIONS);
                if (!result) {
                    Toast.makeText(this, getTranslation(locale, "SYNC_ALREADY_RUNNING"), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, getTranslation(locale, "SYNC_STARTED"), Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(this, getTranslation(locale, "NO_NETWORK"), Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void languageChanged() {
        Menu menu = navigationView.getMenu();
        locale = SharedPreferenceUtil.getLocale(getApplicationContext());
        MenuItem item = menu.findItem(R.id.nav_home);
        item.setTitle(getTranslation(locale, "Daily planner"));
        item = menu.findItem(R.id.nav_change_password);
        item.setTitle(getTranslation(locale, "Reset password"));
        item = menu.findItem(R.id.nav_language);
        item.setTitle(getTranslation(locale, "Change language"));
        item = menu.findItem(R.id.nav_logout);
        item.setTitle(getTranslation(locale, "Logout"));
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
