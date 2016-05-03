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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

public class TimerNotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_STOP = "ua.edu.cdu.fotius.lisun.action_stop";
    public static final String ACTION_START = "ua.edu.cdu.fotius.lisun.action_start";
    public static final String ACTION_SKIP = "ua.edu.cdu.fotius.lisun.action_skip";
    public static final String ACTION_TAKE = "ua.edu.cdu.fotius.lisun.action_take";

    private TimerNotificationManager.NotificationActions mActions;

    public TimerNotificationReceiver(TimerNotificationManager.NotificationActions actions) {
        mActions = actions;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_STOP:
                mActions.notificationStopClicked();
                break;
            case ACTION_START:
                mActions.notificationStartClicked();
                break;
            case ACTION_TAKE:
                mActions.notificationTakeClicked();
                break;
            case ACTION_SKIP:
                mActions.notificationSkipClicked();
                break;
        }
    }
}
