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
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.local.PreferencesKeys;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.PreferencePair;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.Preferences;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.TimeUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerSettingsFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsView {

    public static final String FRAGMENT_TAG = "settings_fragment";
    @Inject
    PreferencesKeys mPreferencesKeys;
    @Inject
    SettingsPresenter mPresenter;
    @Inject
    RxBus mRxBus;

    private Subscription mNumberDialogSubscription;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        DaggerSettingsFragmentComponent.builder()
                .applicationComponent(PomodoroProductivityTimerApplication.get(mContext)
                        .getApplicationComponent())
                .build().inject(this);
        addPreferenceListener();
        mPresenter.attach(this);
        mPresenter.loadPreferences(getString(R.string.preferences_error_loading));
    }

    @Override
    public void onStart() {
        super.onStart();
        mNumberDialogSubscription = mRxBus.getObservable(NumberDialogFragment.Result.class)
                .map(result -> new PreferencePair(result.getPref(), Integer.toString(result.getNewNumber())))
                .subscribe(preferencePair -> mPresenter.savePreference(preferencePair, getString(R.string.preferences_error_saving)));
    }

    @Override
    public void onStop() {
        super.onStop();
        RxUtil.unsubscribe(mNumberDialogSubscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    private void addPreferenceListener() {
        Preference workSessionDuration = findPreference(mPreferencesKeys.getWorkSessionDuration());
        workSessionDuration
                .setOnPreferenceClickListener(createNumberPickerListener());

        Preference shortBreakDuration = findPreference(mPreferencesKeys.getShortBreakDuration());
        shortBreakDuration.
                setOnPreferenceClickListener(createNumberPickerListener());

        Preference longBreakDuration = findPreference(mPreferencesKeys.getLongBreakDuration());
        longBreakDuration
                .setOnPreferenceClickListener(createNumberPickerListener());

        Preference longBreakInterval = findPreference(mPreferencesKeys.getLongBreakInterval());
        longBreakInterval
                .setOnPreferenceClickListener(createNumberPickerListener());
    }

    private Preference.OnPreferenceClickListener createNumberPickerListener() {
       return pref -> {
           NumberDialogFragment.show(getFragmentManager(), pref.getTitle().toString(), 0, pref.getKey());
           return true;
       };
    }

    @Override
    public void setPreferencesSummary(Preferences preferences) {
        setPreferenceSummary(mPreferencesKeys.getWorkSessionDuration(),
                preferences.getWorkSessionDuration());
        setPreferenceSummary(mPreferencesKeys.getShortBreakDuration(),
                preferences.getShortBreakDuration());
        setPreferenceSummary(mPreferencesKeys.getLongBreakDuration(),
                preferences.getLongBreakDuration());
        setPreferenceSummary(mPreferencesKeys.getLongBreakInterval(),
                preferences.getLongBreakInterval());
    }

    @Override
    public void setPreferenceSummary(String key, String value) {
        findPreference(key).setSummary(getSummaryString(key, value));
    }

    @Override
    public void showError(String error) {
        Snackbar.make(getListView(), error, Snackbar.LENGTH_SHORT).show();
    }

    private String getSummaryString(String key, String value) {
        int stringRes = (key.equals(mPreferencesKeys.getLongBreakInterval())) ?
                R.string.pref_summary_n_intervals : R.string.pref_summary_n_minutes;
        return getString(stringRes, value);
    }
}
