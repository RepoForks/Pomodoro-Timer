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

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;

@Singleton
public class PreferencesKeys {

    public static final String PREFS_FILE_NAME = "pomodoro_productivity_timer_prefs";

    private String mWorkSessionDuration;
    private String mShortBreakDuration;
    private String mLongBreakDuration;
    private String mLongBreakInterval;

    @Inject
    public PreferencesKeys(Application application) {
        mWorkSessionDuration =
                application.getResources().getString(R.string.work_session_duration_key);
        mShortBreakDuration =
                application.getResources().getString(R.string.short_break_duration_key);
        mLongBreakDuration =
                application.getResources().getString(R.string.long_break_duration_key);
        mLongBreakInterval =
                application.getResources().getString(R.string.long_break_interval_key);
    }

    public String getWorkSessionDuration() {
        return mWorkSessionDuration;
    }

    public PreferencesKeys setWorkSessionDuration(String workSessionDuration) {
        mWorkSessionDuration = workSessionDuration;
        return this;
    }

    public String getShortBreakDuration() {
        return mShortBreakDuration;
    }

    public PreferencesKeys setShortBreakDuration(String shortBreakDuration) {
        mShortBreakDuration = shortBreakDuration;
        return this;
    }

    public String getLongBreakDuration() {
        return mLongBreakDuration;
    }

    public PreferencesKeys setLongBreakDuration(String longBreakDuration) {
        mLongBreakDuration = longBreakDuration;
        return this;
    }

    public String getLongBreakInterval() {
        return mLongBreakInterval;
    }

    public PreferencesKeys setLongBreakInterval(String longBreakInterval) {
        mLongBreakInterval = longBreakInterval;
        return this;
    }
}
