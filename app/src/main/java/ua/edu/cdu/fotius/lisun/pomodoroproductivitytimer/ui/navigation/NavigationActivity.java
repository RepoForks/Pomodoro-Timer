/*
 * Pomodoro Productivity Timer
 * Copyright (C) 2016  Lisun Andrii
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.navigation;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.settings.SettingsFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.timer.TimerFragment;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.nav_view) NavigationView mNavigationView;

    public final String CURRENT_FRAGMENT_TAG_KEY = "current_fragment_key";
    public Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        if(!restoreFragment(savedInstanceState)){
           setInitialFragment();
        }
    }

    private boolean restoreFragment(Bundle savedInstanceState) {
        if(savedInstanceState == null) return false;
        String savedFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG_KEY);
        mCurrentFragment = getSupportFragmentManager()
                .findFragmentByTag(savedFragmentTag);
        return true;
    }

    private void setInitialFragment() {
        int defaultId = R.id.nav_timer;
        onNavigationItemSelected(mNavigationView.getMenu().findItem(defaultId));
        mNavigationView.setCheckedItem(defaultId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_FRAGMENT_TAG_KEY, mCurrentFragment.getTag());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_timer) {
            setFragment(new TimerFragment(), TimerFragment.FRAGMENT_TAG);
        } else if (id == R.id.nav_projects) {

        } else if (id == R.id.nav_statistics) {

        } else if (id == R.id.nav_settings) {
            setFragment(new SettingsFragment(), SettingsFragment.FRAGMENT_TAG);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    * This method is intended for both "add"
    * and "replace" fragment operations
    */
    private void setFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
        mCurrentFragment = fragment;
    }
}
