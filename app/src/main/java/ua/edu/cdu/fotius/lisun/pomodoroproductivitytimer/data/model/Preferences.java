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

//TODO: make builder for this
public class Preferences {

    public interface DefaultValues {
        long WORK_SESSION_DURATION = 25;
        long SHORT_BREAK_DURATION = 5;
        long LONG_BREAK_DURATION = 15;
        int LONG_BREAK_INTERVAL = 4;
    }

    private long mWorkSessionDuration;
    private long mShortBreakDuration;
    private long mLongBreakDuration;
    private int mLongBreakInterval;

    public long getWorkSessionDuration() {
        return mWorkSessionDuration;
    }

    public Preferences setWorkSessionDuration(long workSessionDuration) {
        mWorkSessionDuration = workSessionDuration;
        return this;
    }

    public long getShortBreakDuration() {
        return mShortBreakDuration;
    }

    public Preferences setShortBreakDuration(long shortBreakDuration) {
        mShortBreakDuration = shortBreakDuration;
        return this;
    }

    public long getLongBreakDuration() {
        return mLongBreakDuration;
    }

    public Preferences setLongBreakDuration(long longBreakDuration) {
        mLongBreakDuration = longBreakDuration;
        return this;
    }

    public int getLongBreakInterval() {
        return mLongBreakInterval;
    }

    public Preferences setLongBreakInterval(int longBreakInterval) {
        mLongBreakInterval = longBreakInterval;
        return this;
    }
}
