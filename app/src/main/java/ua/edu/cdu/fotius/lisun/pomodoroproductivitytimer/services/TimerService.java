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

/* Considering that services is MVP views. Therefore we can separate Android specific code from
*  pure java in Presenter*/
public class TimerService extends Service implements TimerServiceView {
    public class Binder extends android.os.Binder {
        public Observable<Time> getTimeUpdateEvents() {
            return mRxBus.getObservable(Time.class);
        }

        public Observable<TimerState> getStateUpdateEvents() {
            return mRxBus.getObservable(TimerState.class);
        }

        public TimerService getService() {
            return TimerService.this;
        }
    }

    private final int SINGLE_ITERATION_TIME = 10;

    @Inject
    RxBus mRxBus;
    @Inject
    TimerServicePresenter mPresenter;

    private android.os.Binder mBinder;
    private CountDownTimer mTimer;
    private boolean mIsTimerRunning;
    private long mCurrentProject = Project.NO_ID_VALUE;
    private TimerSessionManager mSessionManager;
    private Ringtone mRingtone;

    @Override
    public void onCreate() {
        super.onCreate();
        PomodoroProductivityTimerApplication.get(this)
                .getApplicationComponent().inject(this);

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void setPreferences(Preferences preferences) {
        mSessionManager = new TimerSessionManager(preferences);
    }

    public void startTimer() {
        //mTimer = new CountDownTimer(TimeUnit.MINUTES.toMillis(mSessionManager.getDuration()), SINGLE_ITERATION_TIME) {
        //TODO: only debug
        mTimer = new CountDownTimer(4000, SINGLE_ITERATION_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                postTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                playSound();
                dropTimer();
                if(mSessionManager.getSession() == TimerSessionManager.WORK) {
                    Timber.i("Project id: %d; Duration: %d", mCurrentProject, mSessionManager.getDuration());
                    mPresenter.saveFinishedSession(mCurrentProject, TimeUnit.MINUTES.toMillis((long)mSessionManager.getDuration()));
                } else {
                    nextSessionAndPost();
                }
            }
        }.start();
        mIsTimerRunning = true;
        postState(TimerState.STATE_STARTED);
    }

    private void playSound() {
        mRingtone.play();
    }

    @Override
    public void finishedSessionSaved() {
        nextSessionAndPost();
    }

    public TimerState getInitState() {
        return getStateInstance(TimerState.STATE_READY);
    }

    public void nextSession() {
        nextSessionAndPost();
    }

    private void nextSessionAndPost() {
        mSessionManager.nextSession();
        postState(TimerState.STATE_READY);
    }

    private void postTime(long millis) {
        mRxBus.send(new Time(millis));
    }

    private void postState(int state) {
        TimerState event = getStateInstance(state);
        mRxBus.send(event);
    }

    private TimerState getStateInstance(int state) {
        return new TimerState(state,
                mSessionManager.getDuration(), mSessionManager.getSession());
    }

    private void dropTimer() {
        postTime(0);
        mTimer = null;
        mIsTimerRunning = false;
    }

    public void stopTimer() {
        if((mTimer != null) && mIsTimerRunning) {
            mTimer.cancel();
            dropTimer();
        }
        postState(TimerState.STATE_STOPPED);
    }

    public boolean isTimerRunning() {
        return mIsTimerRunning;
    }

    public void setProject(long id) {
        mCurrentProject = id;
    }
}
