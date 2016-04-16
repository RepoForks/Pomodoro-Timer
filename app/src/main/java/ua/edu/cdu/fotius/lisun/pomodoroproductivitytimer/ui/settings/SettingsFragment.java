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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;


import javax.inject.Inject;

import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.local.PreferencesHelper;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.local.PreferencesKeys;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerSettingsFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.DialogFactory;

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsView,
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    PreferencesKeys mPreferencesKeys;

    public static final String FRAGMENT_TAG = "settings_fragment";
    //TODO: setRetainInstance or remove it from all fragments
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerSettingsFragmentComponent.builder()
                .applicationComponent(PomodoroProductivityTimerApplication.get(mContext)
                        .getApplicationComponent())
                .build().inject(this);
        addPreferenceListener();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Timber.i("Shared Preference with key %s changed", key);
    }

    private void addPreferenceListener() {
        Preference workSessionDuration = findPreference(mPreferencesKeys.getWorkSessionDuration());
        workSessionDuration
                .setOnPreferenceClickListener(createNumberPickerListener(workSessionDuration));

        Preference shortBreakDuration = findPreference(mPreferencesKeys.getShortBreakDuration());
        shortBreakDuration.
                setOnPreferenceClickListener(createNumberPickerListener(shortBreakDuration));

        Preference longBreakDuration = findPreference(mPreferencesKeys.getLongBreakDuration());
        longBreakDuration
                .setOnPreferenceClickListener(createNumberPickerListener(longBreakDuration));

        Preference longBreakInterval = findPreference(mPreferencesKeys.getLongBreakInterval());
        longBreakInterval
                .setOnPreferenceClickListener(createNumberPickerListener(longBreakInterval));
    }

    private Preference.OnPreferenceClickListener createNumberPickerListener(Preference preference) {
       return preference1 -> {
           DialogFactory.createNumberPickerDialog(mContext, preference1.getTitle().toString())
                   .show();
           return true;
       };
    }
}
