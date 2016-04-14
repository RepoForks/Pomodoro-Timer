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

import android.content.Context;
import android.content.SharedPreferences;

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Preferences;

public class PreferencesHelper {

    private final String PREFS_FILE_NAME = "pomodoro_productivity_timer_prefs";
    private final String WORK_SESSION_DURATION_KEY = "work_session_duration";
    private final String SHORT_BREAK_DURATION_KEY = "short_break_duration";
    private final String LONG_BREAK_DURATION_KEY = "long_break_duration";
    private final String LONG_BREAK_INTERVAL_KEY = "long_break_interval";

    private SharedPreferences mSharedPrefs;

    public PreferencesHelper(Context context) {
        mSharedPrefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void save(Preferences prefs) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putLong(WORK_SESSION_DURATION_KEY, prefs.getWorkSessionDuration());
        editor.putLong(SHORT_BREAK_DURATION_KEY, prefs.getShortBreakDuration());
        editor.putLong(LONG_BREAK_DURATION_KEY, prefs.getLongBreakDuration());
        editor.putLong(LONG_BREAK_INTERVAL_KEY, prefs.getLongBreakInterval());
        editor.commit();
    }

    public Preferences get() {
        long sessionDuration =
                mSharedPrefs.getLong(WORK_SESSION_DURATION_KEY, Preferences.DefaultValues.WORK_SESSION_DURATION);
        long shortBreakDuration =
                mSharedPrefs.getLong(SHORT_BREAK_DURATION_KEY, Preferences.DefaultValues.SHORT_BREAK_DURATION);
        long longBreakDuration =
                mSharedPrefs.getLong(LONG_BREAK_DURATION_KEY, Preferences.DefaultValues.LONG_BREAK_DURATION);
        int longBreakInterval =
                mSharedPrefs.getInt(LONG_BREAK_INTERVAL_KEY, Preferences.DefaultValues.LONG_BREAK_INTERVAL);
        return new Preferences()
                .setWorkSessionDuration(sessionDuration)
                .setShortBreakDuration(shortBreakDuration)
                .setLongBreakDuration(longBreakDuration)
                .setLongBreakInterval(longBreakInterval);
    }
}
