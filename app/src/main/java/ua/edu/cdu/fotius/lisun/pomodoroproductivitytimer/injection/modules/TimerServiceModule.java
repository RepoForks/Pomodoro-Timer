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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.modules;

import android.app.Service;

import dagger.Module;
import dagger.Provides;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerService;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.notification.TimerNotificationManager;

@Module
public class TimerServiceModule {
    private TimerService mService;

    public TimerServiceModule(TimerService service) {
        mService = service;
    }

    @Provides
    TimerNotificationManager.NotificationActions provideNotificationActions() {
        return mService;
    }

    @Provides
    Service provideService() {
        return mService;
    }
}
