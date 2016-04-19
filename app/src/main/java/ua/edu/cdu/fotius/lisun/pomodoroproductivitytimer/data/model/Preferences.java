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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model;

public class Preferences {

    public interface DefaultValues {
        String WORK_SESSION_DURATION = "25";
        String SHORT_BREAK_DURATION = "5";
        String LONG_BREAK_DURATION = "15";
        String LONG_BREAK_INTERVAL = "4";
    }

    private String mWorkSessionDuration;
    private String mShortBreakDuration;
    private String mLongBreakDuration;
    private String mLongBreakInterval;

    public String getWorkSessionDuration() {
        return mWorkSessionDuration;
    }

    public Preferences setWorkSessionDuration(String workSessionDuration) {
        mWorkSessionDuration = workSessionDuration;
        return this;
    }

    public String getShortBreakDuration() {
        return mShortBreakDuration;
    }

    public Preferences setShortBreakDuration(String shortBreakDuration) {
        mShortBreakDuration = shortBreakDuration;
        return this;
    }

    public String getLongBreakDuration() {
        return mLongBreakDuration;
    }

    public Preferences setLongBreakDuration(String longBreakDuration) {
        mLongBreakDuration = longBreakDuration;
        return this;
    }

    public String getLongBreakInterval() {
        return mLongBreakInterval;
    }

    public Preferences setLongBreakInterval(String longBreakInterval) {
        mLongBreakInterval = longBreakInterval;
        return this;
    }
}
