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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerTimerFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.Time;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerEventBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerService;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerServiceConnection;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;


public class TimerFragment extends BaseFragment implements TimerView {
    public static String FRAGMENT_TAG = "timer_fragment";

    @Inject
    TimerServiceConnection mServiceConnection;
    @Inject
    TimerEventBus mEventBus;
    @Bind(R.id.countdown_minutes)
    TextView mCountdownMinutes;
    @Bind(R.id.countdown_seconds)
    TextView mCountdownSeconds;

    private Context mContext;
    private Subscription mSubscription;

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
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mSubscription = mEventBus.getObservable().subscribe(obj -> {
            updateTime((Time) obj);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if((mSubscription != null) && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        if(mServiceConnection.isConnected()) {
            mContext.unbindService(mServiceConnection);
        }
    }

    @OnClick(R.id.fab_start_stop_timer)
    void startStopTimer() {
        TimerService service = mServiceConnection.getService();
        //TODO: also change fab image here
        if(service.isTimerRunning()) {
            service.stopTimer();
        } else {
            service.startTimer();
        }
    }

    public void updateTime(Time time) {
        mCountdownMinutes.setText(time.minutesToString());
        mCountdownSeconds.setText(time.secondsToString());
    }
}
