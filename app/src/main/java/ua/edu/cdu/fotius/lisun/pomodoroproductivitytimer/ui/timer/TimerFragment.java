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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerTimerFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.Time;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerService;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxUtil;


public class TimerFragment extends BaseFragment implements TimerView, ServiceConnection {
    public static String FRAGMENT_TAG = "timer_fragment";

    @Bind(R.id.tv_time)
    TextView mTimeTv;

    private Context mContext;
    private Subscription mSubscription;
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
        RxUtil.unsubscribe(mSubscription);
        mContext.unbindService(this);
    }

    @OnClick(R.id.fab_start_stop_timer)
    void startStopTimer(View v) {
        int icon;
        if(mService.isTimerRunning()) {
            mService.stopTimer();
            icon = R.drawable.ic_start_24dp;
        } else {
            mService.startTimer();
            icon = R.drawable.ic_stop_24dp;
        }
        ((FloatingActionButton) v).setImageResource(icon);
    }

    @Override
    public void updateTime(Time time) {
        mTimeTv.setText(getString(R.string.time, time.getMinutes(), time.getSeconds()));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        TimerService.Binder binder = (TimerService.Binder) service;
        mService = binder.getService();
        mSubscription = binder.getObservable().subscribe(this::updateTime);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }
}
