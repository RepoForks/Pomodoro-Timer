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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.local;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.PreferencePair;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.Preferences;

@Singleton
public class PreferencesHelper {

    @Inject
    PreferencesKeys mPreferencesKeys;

    private SharedPreferences mSharedPrefs;

    @Inject
    public PreferencesHelper(Application application) {
        mSharedPrefs = application
                .getSharedPreferences(PreferencesKeys.PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    public PreferencePair putPreference(PreferencePair preferencePair) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(preferencePair.getKey(), preferencePair.getValue());
        editor.apply();
        return preferencePair;
    }

    public Preferences preferences() {
        String sessionDuration =
                mSharedPrefs.getString(mPreferencesKeys.getWorkSessionDuration(),
                        Long.toString(Preferences.DefaultValues.WORK_SESSION_DURATION));
        String shortBreakDuration =
                mSharedPrefs.getString(mPreferencesKeys.getShortBreakDuration(),
                        Long.toString(Preferences.DefaultValues.SHORT_BREAK_DURATION));
        String longBreakDuration =
                mSharedPrefs.getString(mPreferencesKeys.getLongBreakDuration(),
                        Long.toString(Preferences.DefaultValues.LONG_BREAK_DURATION));
        String longBreakInterval =
                mSharedPrefs.getString(mPreferencesKeys.getLongBreakInterval(),
                        Long.toString(Preferences.DefaultValues.LONG_BREAK_INTERVAL));
        return new Preferences()
                .setWorkSessionDuration(sessionDuration)
                .setShortBreakDuration(shortBreakDuration)
                .setLongBreakDuration(longBreakDuration)
                .setLongBreakInterval(longBreakInterval);
    }
}
