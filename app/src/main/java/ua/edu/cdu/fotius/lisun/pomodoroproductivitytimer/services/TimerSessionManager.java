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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services;

import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.Preferences;

public class TimerSessionManager {
    public static final int WORK = 0;
    public static final int BREAK = 1;
    public static final int LONG_BREAK = 2;

    private int mSession = WORK;
    private int mWorksBeforeLongBreak = 0;

    /*position is state(WORK, BREAK, etc.).
    value at position is duration*/
    private int [] mDurations = new int[3];

    private int mLongBreakInterval;

    public TimerSessionManager(Preferences preferences) {
        mDurations[WORK] = Integer.parseInt(preferences.getWorkSessionDuration());
        mDurations[BREAK] = Integer.parseInt(preferences.getShortBreakDuration());
        mDurations[LONG_BREAK] = Integer.parseInt(preferences.getLongBreakDuration());
        mLongBreakInterval = Integer.parseInt(preferences.getLongBreakInterval());
    }

    public void setDuration(int state, int duration) {
        mDurations[state] = duration;
    }

    public int getDuration() {
        return mDurations[mSession];
    }

    public int getSession() {
        return mSession;
    }

    public void nextSession() {
        int nextSession;
        if(mSession == WORK) {
            ++mWorksBeforeLongBreak;
            nextSession = (mWorksBeforeLongBreak == mLongBreakInterval) ? LONG_BREAK : BREAK;
        } else if(mSession == LONG_BREAK){
            mWorksBeforeLongBreak = 0;
            nextSession = WORK;
        } else {
            nextSession = WORK;
        }
        mSession = nextSession;
    }
}
