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

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import timber.log.Timber;

public class TimerService extends Service {

    class Binder extends android.os.Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    //TODO: Time is hardcoded now. In future will depend on chosen project
    private final int TOTAL_TIME = 25000;
    private final int SINGLE_ITERATION_TIME = 10;

    private android.os.Binder mBinder = new Binder();
    //TODO: maybe to separate class and inject
    private CountDownTimer mTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startTimer() {
        Timber.i("startTimer");
        mTimer = new CountDownTimer(TOTAL_TIME, SINGLE_ITERATION_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                //postTime to rx event bus
                Timber.i("Millis until finished: %d", millisUntilFinished);
            }

            @Override
            public void onFinish() {
            }
        }.start();
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
