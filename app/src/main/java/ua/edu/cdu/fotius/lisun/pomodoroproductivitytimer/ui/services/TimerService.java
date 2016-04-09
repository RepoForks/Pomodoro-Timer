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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import timber.log.Timber;

public class TimerService extends Service{

    class Binder extends android.os.Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    private Binder mBinder = new Binder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startTimer() {
        Timber.i("startTimer");
    }

    public void stopTimer() {
        Timber.i("stopTimer");
    }

    public boolean isTimerRunning() {
        Timber.i("isTimerRunning");
        return false;
    }

    //TODO:
    // setProject
    // setTime - time for this particular project
    // reset(?)
    // persistData(?)
}
