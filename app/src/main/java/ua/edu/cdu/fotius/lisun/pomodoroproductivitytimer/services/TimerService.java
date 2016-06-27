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

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.Preferences;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerTimerServiceComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.modules.TimerServiceModule;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.notification.TimerNotificationManager;

/* Considering that services is MVP views. Therefore we can separate Android specific code from
*  pure java in Presenter*/
public class TimerService extends Service implements TimerServiceView, TimerNotificationManager.NotificationActions {

    public class Binder extends android.os.Binder {
        public Observable<TimeUpdate> getTimeUpdateEvents() {
            return mRxBus.getObservable(TimeUpdate.class);
        }

        public Observable<TimerState> getStateUpdateEvents() {
            return mRxBus.getObservable(TimerState.class);
        }

        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Inject
    RxBus mRxBus;
    @Inject
    TimerServicePresenter mPresenter;
    @Inject
    TimerNotificationManager mNotificationManager;

    private final int SINGLE_ITERATION_TIME = 10;
    private android.os.Binder mBinder;
    private CountDownTimer mTimer;
    private boolean mIsTimerRunning;
    private long mCurrentProject = Project.NO_ID_VALUE;
    private TimerSessionManager mSessionManager;
    private Ringtone mRingtone;
    private int mState;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerTimerServiceComponent.builder()
                .timerServiceModule(new TimerServiceModule(this))
                .applicationComponent(PomodoroProductivityTimerApplication.get(this)
                        .getApplicationComponent())
                .build()
                .inject(this);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mRingtone = RingtoneManager.getRingtone(this, notification);
        mBinder = new Binder();
        mPresenter.attach(this);
        mPresenter.loadPreferences();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        mPresenter.detach();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startTimer() {
        //mTimer = new CountDownTimer(TimeUnit.MINUTES.toMillis(mSessionManager.getDuration()), SINGLE_ITERATION_TIME) {
        mTimer = new CountDownTimer(5000, SINGLE_ITERATION_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                postTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                playSound();
                dropTimer();
                if (mSessionManager.getSession() == TimerSessionManager.WORK) {
                    Timber.i("TimerService#onFinish. SAVING FINISHED SESSION");
                    mPresenter.saveFinishedSession(mCurrentProject,
                            TimeUnit.MINUTES.toMillis((long) mSessionManager.getDuration()));
                } else {
                    nextSessionAndPost();
                }
            }
        }.start();
        mIsTimerRunning = true;
        mNotificationManager.start(new TimeUpdate(mSessionManager.getSession(),
                TimeUnit.MINUTES.toMillis(mSessionManager.getDuration())));
        startService(new Intent(getApplicationContext(), this.getClass()));
        postState(TimerState.STATE_STARTED);
    }

    public void stopTimer() {
        if ((mTimer != null) && mIsTimerRunning) {
            mTimer.cancel();
            dropTimer();
        }
        postState(TimerState.STATE_READY);
        mNotificationManager.stop();
        stopSelf();
    }

    public TimerState getState() {
        return createStateInstance(mState);
    }

    public void nextSession() {
        nextSessionAndPost();
    }

    public boolean isTimerRunning() {
        return mIsTimerRunning;
    }

    public void setProject(long id) {
        mCurrentProject = id;
    }

    //----------------VIEW IMPLEMENTATION----------------
    @Override
    public void setPreferences(Preferences preferences) {
        mSessionManager = new TimerSessionManager(preferences);
    }

    @Override
    public void finishedSessionSaved() {
        nextSessionAndPost();
    }

    //----------------NOTIFICATION ACTIONS----------------
    @Override
    public void notificationStopClicked() {
        stopTimer();
    }

    @Override
    public void notificationStartClicked() {
        startTimer();
    }

    @Override
    public void notificationTakeClicked() {
        startTimer();
    }

    @Override
    public void notificationSkipClicked() {
        nextSession();
    }

    private void playSound() {
        mRingtone.play();
    }

    private void nextSessionAndPost() {
        mSessionManager.nextSession();
        postState(TimerState.STATE_READY);
    }

    private void postTime(long millis) {
        mRxBus.send(new TimeUpdate(mSessionManager.getSession(), millis));
    }

    private void postState(int state) {
        mState = state;
        TimerState event = createStateInstance(state);
        mRxBus.send(event);
    }

    private TimerState createStateInstance(int state) {
        return new TimerState(state,
                mSessionManager.getDuration(), mSessionManager.getSession());
    }

    private void dropTimer() {
        mTimer = null;
        mIsTimerRunning = false;
    }
}
