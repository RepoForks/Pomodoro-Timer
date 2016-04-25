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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.timer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerTimerFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerState;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerSessionManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.Time;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerService;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.dialogs.TwoButtonsDialogFragment;


public class TimerFragment extends BaseFragment implements TimerView, ServiceConnection {
    public static String FRAGMENT_TAG = "timer_fragment";
    @Inject
    RxBus mRxBus;
    @Bind(R.id.tv_time)
    TextView mTimeTv;
    @Bind(R.id.fab_start_stop_timer)
    FloatingActionButton mStartStopFab;
    @Bind(R.id.tv_session)
    TextView mSessionNameTv;
    private Context mContext;
    private Subscription mTimeUpdateSubscription;
    private Subscription mStateUpdateSubscription;
    private Subscription mDialogSubscription;
    private TimerService mService;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerTimerFragmentComponent.builder()
                .applicationComponent(PomodoroProductivityTimerApplication.get(mContext)
                        .getApplicationComponent())
                .build().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(mContext, TimerService.class);
        mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        RxUtil.unsubscribe(mTimeUpdateSubscription);
        RxUtil.unsubscribe(mStateUpdateSubscription);
        RxUtil.unsubscribe(mDialogSubscription);
        mContext.unbindService(this);
    }

    @OnClick(R.id.fab_start_stop_timer)
    void startStopTimer() {
        if (mService.isTimerRunning()) {
            mService.stopTimer();
        } else {
            mService.startTimer();
        }
    }

    @Override
    public void showTime(Time time) {
        showTime(time.getMinutes(), time.getSeconds());
    }

    private void showTime(long minutes, long seconds) {
        mTimeTv.setText(getString(R.string.time, minutes, seconds));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        TimerService.Binder binder = (TimerService.Binder) service;
        mService = binder.getService();
        mTimeUpdateSubscription = binder.getTimeUpdateEvents().subscribe(this::showTime);
        mStateUpdateSubscription = binder.getStateUpdateEvents().subscribe(this::updateSession);
        TimerState initState = mService.getInitState();
        showTime(initState.getDuration(), 0);
        showSessionName(initState.getSession());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    private void updateSession(TimerState event) {
        int state = event.getState();
        if (state == TimerState.STATE_READY) {
            mStartStopFab.setImageResource(R.drawable.ic_start_24dp);
            showSessionName(event.getSession());
            showTime(event.getDuration(), 0);
            if ((event.getSession() == TimerSessionManager.BREAK)
                    || (event.getSession() == TimerSessionManager.LONG_BREAK)) {
                RxUtil.unsubscribe(mDialogSubscription);
                mDialogSubscription =
                        TwoButtonsDialogFragment.show(getFragmentManager(), mRxBus,
                                getString(R.string.break_dialog_title),
                                getString(R.string.break_dialog_message),
                                getString(R.string.break_dialog_take),
                                getString(R.string.break_dialog_skip))
                                .subscribe(result -> {
                                    if (result.getResultCode() ==
                                            TwoButtonsDialogFragment.Result.OK) {
                                        mService.startTimer();
                                    } else if (result.getResultCode() ==
                                            TwoButtonsDialogFragment.Result.CANCEL) {
                                        mService.nextSession();
                                    }
                                });

            }
        } else  if (state == TimerState.STATE_STARTED) {
            mStartStopFab.setImageResource(R.drawable.ic_stop_24dp);
        } else if(state == TimerState.STATE_STOPPED) {
            mStartStopFab.setImageResource(R.drawable.ic_start_24dp);
        }
    }

    @Override
    public void showSessionName(int sessionType) {
          if(sessionType == TimerSessionManager.WORK){
              mSessionNameTv.setText(R.string.timer_fragment_session_work);
          } else if(sessionType == TimerSessionManager.BREAK) {
              mSessionNameTv.setText(R.string.timer_fragment_break);
          } else if(sessionType == TimerSessionManager.LONG_BREAK) {
              mSessionNameTv.setText(R.string.timer_fragment_long_break);
          }
    }
}
