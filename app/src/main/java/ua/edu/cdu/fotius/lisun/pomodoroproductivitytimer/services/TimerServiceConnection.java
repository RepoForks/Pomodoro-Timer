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

import android.content.ComponentName;
import android.os.IBinder;
import javax.inject.Inject;
import timber.log.Timber;

public class TimerServiceConnection implements android.content.ServiceConnection{

    private TimerService mService;

    @Inject
    public TimerServiceConnection(){
        Timber.i("TimerServiceConnection constructor");
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        TimerService.Binder binder = (TimerService.Binder) service;
        mService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    public TimerService getService() {
        if(mService == null) {
            throw new IllegalStateException("You should bind " +
                    "to service before calling getService()");
        }
        return mService;
    }

    public boolean isConnected() {
        return (mService != null);
    }
}
