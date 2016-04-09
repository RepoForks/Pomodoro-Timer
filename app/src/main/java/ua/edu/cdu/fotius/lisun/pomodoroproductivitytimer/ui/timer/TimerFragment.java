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

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerService;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerServiceConnection;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;


public class TimerFragment extends BaseFragment implements TimerView{

    public static String FRAGMENT_TAG = "timer_fragment";

    private Context mContext;
    //TODO: for injector
    private TimerServiceConnection mServiceConnection = new TimerServiceConnection();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("TimerFragment.onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(mContext, TimerService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mServiceConnection.isConnected()) {
            mContext.unbindService(mServiceConnection);
        }
    }

    @OnClick(R.id.fab)
    void startStopTimer() {
        TimerService service = mServiceConnection.getService();
        //TODO: also change fab image here
        if(service.isTimerRunning()) {
            service.stopTimer();
        } else {
            service.startTimer();
        }
    }
}
