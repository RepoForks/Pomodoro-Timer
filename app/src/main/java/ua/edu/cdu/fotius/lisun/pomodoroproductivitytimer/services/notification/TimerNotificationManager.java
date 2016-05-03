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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimeUpdate;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerSessionManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerState;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.navigation.NavigationActivity;

public class TimerNotificationManager {

    public interface NotificationActions {
        void notificationStopClicked();

        void notificationStartClicked();

        void notificationTakeClicked();

        void notificationSkipClicked();
    }

    private final int NOTIFICATION_ID = 1;
    private final int BROADCAST_REQUEST_CODE = 1;

    private RxBus mRxBus;
    private Subscription mTimerStateSubscription;
    private Subscription mTimeUpdateSubscription;
    private NotificationManager mNotificationManager;
    private Service mService;

    private TimerNotificationReceiver mActionsReceiver;
    private PendingIntent mStopAction;
    private PendingIntent mStartAction;
    private PendingIntent mTakeAction;
    private PendingIntent mSkipAction;
    private PendingIntent mContentAction;
    private boolean mIsStarted;
    private long mLastUpdateMinutes;

    @Inject
    public TimerNotificationManager(RxBus rxBus, Service service, NotificationActions actionsListener) {
        mRxBus = rxBus;
        mService = service;
        mActionsReceiver = new TimerNotificationReceiver(actionsListener);
        setUpActions();
        setUpNotificationManager();
    }

    private void setUpActions() {
        mStopAction = PendingIntent.getBroadcast(mService, BROADCAST_REQUEST_CODE,
                new Intent(TimerNotificationReceiver.ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT);
        mStartAction = PendingIntent.getBroadcast(mService, BROADCAST_REQUEST_CODE,
                new Intent(TimerNotificationReceiver.ACTION_START), PendingIntent.FLAG_UPDATE_CURRENT);
        mTakeAction = PendingIntent.getBroadcast(mService, BROADCAST_REQUEST_CODE,
                new Intent(TimerNotificationReceiver.ACTION_TAKE), PendingIntent.FLAG_UPDATE_CURRENT);
        mSkipAction = PendingIntent.getBroadcast(mService, BROADCAST_REQUEST_CODE,
                new Intent(TimerNotificationReceiver.ACTION_SKIP), PendingIntent.FLAG_UPDATE_CURRENT);
        mContentAction = PendingIntent.getBroadcast(mService, BROADCAST_REQUEST_CODE,
                new Intent(mService, NavigationActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void setUpNotificationManager() {
        mNotificationManager =
                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    public void start(TimeUpdate time) {
        if (mIsStarted) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerNotificationReceiver.ACTION_STOP);
        filter.addAction(TimerNotificationReceiver.ACTION_START);
        filter.addAction(TimerNotificationReceiver.ACTION_TAKE);
        filter.addAction(TimerNotificationReceiver.ACTION_SKIP);
        mService.registerReceiver(mActionsReceiver, filter);
        subscribe();
        mIsStarted = true;
        mService.startForeground(NOTIFICATION_ID, updateOngoingNotification(time));
    }

    public void stop() {
        if (!mIsStarted) {
            return;
        }
        unsubscribe();
        mService.stopForeground(true);
        mService.unregisterReceiver(mActionsReceiver);
        mIsStarted = false;
    }

    private void subscribe() {
        mTimeUpdateSubscription = mRxBus.getObservable(TimeUpdate.class)
                .filter(timeUpdate -> (timeUpdate.getMinutes() - mLastUpdateMinutes >= 1))
                .subscribe(TimerNotificationManager.this::updateOngoingNotification);
        mTimerStateSubscription = mRxBus.getObservable(TimerState.class)
                .subscribe(this::updateState);
    }

    private void unsubscribe() {
        RxUtil.unsubscribe(mTimerStateSubscription);
        RxUtil.unsubscribe(mTimeUpdateSubscription);
    }

    private void updateState(TimerState state) {
        if (state.getState() == TimerState.STATE_READY) {
            if ((state.getSession() == TimerSessionManager.BREAK) ||
                    (state.getSession() == TimerSessionManager.LONG_BREAK)) {
                buildBreakReadyNotification();
            } else if ((state.getSession() == TimerSessionManager.WORK)) {
                buildWorkReadyNotification();
            }
        } else if (state.getState() == TimerState.STATE_STARTED) {
            updateOngoingNotification(new TimeUpdate(state.getSession(),
                    TimeUnit.MINUTES.toMillis(state.getDuration())));
        }
    }

    private Notification updateOngoingNotification(TimeUpdate time) {
        int titleId;
        if (time.getSession() == TimerSessionManager.WORK) {
            titleId = R.string.work_prompt;
        } else if (time.getSession() == TimerSessionManager.BREAK) {
            titleId = R.string.break_prompt;
        } else {
            titleId = R.string.long_break_prompt;
        }
        Notification notification = createCommonBuilder()
                .setContentTitle(mService.getString(R.string.notification_title, mService.getString(titleId)))
                .setContentText(mService.getString(R.string.notification_time_left, time.getMinutes()))
                .addAction(R.drawable.ic_stop_timer_24dp, mService.getString(R.string.notification_stop), mStopAction)
                .build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
        mLastUpdateMinutes = time.getMinutes();
        return notification;
    }

    private void buildWorkReadyNotification() {
        Notification notification = createCommonReadyBuilder()
                .setContentTitle(mService.getString(R.string.notification_work_title))
                .setContentText(mService.getString(R.string.notification_work_content))
                .addAction(R.drawable.ic_start_timer_24dp, mService.getString(R.string.notification_start), mStartAction)
                .build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private NotificationCompat.Builder createCommonReadyBuilder() {
        return createCommonBuilder()
                .addAction(R.drawable.ic_close_24dp, mService.getString(R.string.notification_dismiss), mStopAction);
    }

    private NotificationCompat.Builder createCommonBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mService);
        builder.setVisibility(android.support.v7.app.NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(ContextCompat.getColor(mService, R.color.colorPrimary))
                .setContentIntent(mContentAction)
                .setSmallIcon(R.drawable.ic_start_timer_24dp)
                .setShowWhen(false);
        return builder;
    }

    private void buildBreakReadyNotification() {
        Notification notification = createCommonReadyBuilder()
                .setContentTitle(mService.getString(R.string.notification_break_title))
                .setContentText(mService.getString(R.string.notification_break_content))
                .addAction(R.drawable.ic_take_break_24dp,
                        mService.getString(R.string.notification_break_take_action), mTakeAction)
                .addAction(R.drawable.ic_skip_next_24dp,
                        mService.getString(R.string.notification_break_skip_action), mSkipAction)
                .build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
