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

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Preferences;

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

    public void save(Preferences prefs) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putLong(mPreferencesKeys.getWorkSessionDuration(), prefs.getWorkSessionDuration());
        editor.putLong(mPreferencesKeys.getShortBreakDuration(), prefs.getShortBreakDuration());
        editor.putLong(mPreferencesKeys.getLongBreakDuration(), prefs.getLongBreakDuration());
        editor.putLong(mPreferencesKeys.getLongBreakInterval(), prefs.getLongBreakInterval());
        editor.apply();
    }

    public void saveItem(String key, String value) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void saveItem(String key, Long value) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void saveItem(String key, Integer value) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public Preferences get() {
        long sessionDuration =
                mSharedPrefs.getLong(mPreferencesKeys.getWorkSessionDuration(),
                        Preferences.DefaultValues.WORK_SESSION_DURATION);
        long shortBreakDuration =
                mSharedPrefs.getLong(mPreferencesKeys.getShortBreakDuration(),
                        Preferences.DefaultValues.SHORT_BREAK_DURATION);
        long longBreakDuration =
                mSharedPrefs.getLong(mPreferencesKeys.getLongBreakDuration(),
                        Preferences.DefaultValues.LONG_BREAK_DURATION);
        int longBreakInterval =
                mSharedPrefs.getInt(mPreferencesKeys.getLongBreakInterval(),
                        Preferences.DefaultValues.LONG_BREAK_INTERVAL);
        return new Preferences()
                .setWorkSessionDuration(sessionDuration)
                .setShortBreakDuration(shortBreakDuration)
                .setLongBreakDuration(longBreakDuration)
                .setLongBreakInterval(longBreakInterval);
    }
}
