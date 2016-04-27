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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.ToolbarActivity;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects.ProjectsFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.settings.SettingsActivity;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.timer.TimerFragment;

public class NavigationActivity extends ToolbarActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.nav_view) NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        if(!restoreFragment(savedInstanceState)){
           setInitialFragment();
        }
    }

    @Override
    protected int contentView() {
        return R.layout.activity_main;
    }

    private void setInitialFragment() {
        int defaultId = R.id.nav_timer;
        onNavigationItemSelected(mNavigationView.getMenu().findItem(defaultId));
        mNavigationView.setCheckedItem(defaultId);
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
            setFragment(new ProjectsFragment(), ProjectsFragment.FRAGMENT_TAG);
        } else if (id == R.id.nav_statistics) {

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
