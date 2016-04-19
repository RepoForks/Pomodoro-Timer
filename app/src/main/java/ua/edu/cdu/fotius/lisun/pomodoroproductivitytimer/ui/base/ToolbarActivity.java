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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;

public abstract class ToolbarActivity extends AppCompatActivity{

    public final String CURRENT_FRAGMENT_TAG_KEY = "current_fragment_key";

    protected Fragment mCurrentFragment;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView());
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_FRAGMENT_TAG_KEY, mCurrentFragment.getTag());
        super.onSaveInstanceState(outState);
    }

    protected abstract int contentView();

    protected boolean restoreFragment(Bundle savedInstanceState) {
        if(savedInstanceState == null) return false;
        String savedFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG_KEY);
        mCurrentFragment = getSupportFragmentManager()
                .findFragmentByTag(savedFragmentTag);
        return (mCurrentFragment != null);
    }

    /*
    * This method is intended for both "add"
    * and "replace" fragment operations
    */
    protected void setFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
        mCurrentFragment = fragment;
    }
}
