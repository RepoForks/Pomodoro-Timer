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
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import rx.Subscription;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerTimerFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimeUpdate;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerService;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerSessionManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerState;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;

import static butterknife.ButterKnife.findById;


public class TimerFragment extends BaseFragment implements TimerView, ServiceConnection {
    public static String FRAGMENT_TAG = "timer_fragment";
    @Inject
    RxBus mRxBus;
    @Inject
    ProjectsAdapter mProjectsAdapter;
    @Inject
    TimerPresenter mPresenter;
    @Bind(R.id.tv_time)
    TextView mTimeTv;
    @Bind(R.id.fab_start_stop_timer)
    FloatingActionButton mStartStopFab;
    @Bind(R.id.tv_session)
    TextView mSessionNameTv;
    @Bind(R.id.tv_worked_today_counter)
    TextView mWorkedTodayTv;
    private Context mContext;
    private Subscription mTimeUpdateSubscription;
    private Subscription mStateUpdateSubscription;
    private Subscription mBreakDialogSubscription;
    private TimerService mService;
    private DialogFragment mBreakDialogFragment;

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
        mPresenter.attach(this);
        mPresenter.loadProjects(getString(R.string.timer_error_loading_projects));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timer_fragment, container, false);
        ButterKnife.bind(this, v);
        Spinner spinner = findById(v, R.id.sp_projects);
        spinner.setAdapter(mProjectsAdapter);
        loadTodaysTotal();
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
        mBreakDialogSubscription = mRxBus.getObservable(BreakDialogFragment.Result.class)
                .subscribe(result -> {
                    if (result.getResultCode() == BreakDialogFragment.Result.TAKE) {
                        mService.startTimer();
                    } else if (result.getResultCode() == BreakDialogFragment.Result.SKIP) {
                        mService.nextSession();
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        RxUtil.unsubscribe(mTimeUpdateSubscription);
        RxUtil.unsubscribe(mStateUpdateSubscription);
        RxUtil.unsubscribe(mBreakDialogSubscription);
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
    public void showTime(TimeUpdate timeUpdate) {
        showTime(timeUpdate.getMinutes(), timeUpdate.getSeconds());
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
        TimerState initState = mService.getState();
        showTime(initState.getDuration(), 0);
        showSessionName(initState.getSession());
        updateSession(initState);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    private void updateSession(TimerState event) {
        dismissBreakDialog();
        int state = event.getState();
        if (state == TimerState.STATE_READY) {
            mStartStopFab.setImageResource(R.drawable.ic_start_timer_24dp);
            showSessionName(event.getSession());
            showTime(event.getDuration(), 0);
            if ((event.getSession() == TimerSessionManager.BREAK)
                    || (event.getSession() == TimerSessionManager.LONG_BREAK)) {
                loadTodaysTotal();
                mBreakDialogFragment = BreakDialogFragment.show(getFragmentManager());
            }
        } else  if (state == TimerState.STATE_STARTED) {
            mStartStopFab.setImageResource(R.drawable.ic_stop_timer_24dp);
        }
    }

    //if action has been performed from notification
    private void dismissBreakDialog() {
        if(mBreakDialogFragment != null) {
            mBreakDialogFragment.dismiss();
            mBreakDialogFragment = null;
        }
    }

    private void loadTodaysTotal() {
        mPresenter.loadTodayTotal(getString(R.string.timer_error_loading_total_quantity));
    }

    @Override
    public void showSessionName(int sessionType) {
          if(sessionType == TimerSessionManager.WORK){
              mSessionNameTv.setText(R.string.work_prompt);
          } else if(sessionType == TimerSessionManager.BREAK) {
              mSessionNameTv.setText(R.string.break_prompt);
          } else if(sessionType == TimerSessionManager.LONG_BREAK) {
              mSessionNameTv.setText(R.string.long_break_prompt);
          }
    }

    @Override
    public void showProjects(List<Project> projects) {
        mProjectsAdapter.setProjects(projects);
    }

    @Override
    public void showTodaysTotal(int completed) {
        mWorkedTodayTv.setText(Integer.toString(completed));
    }

    @Override
    public void showError(String error) {
        Snackbar.make(mStartStopFab, error, Snackbar.LENGTH_SHORT).show();
    }

    @OnItemSelected(R.id.sp_projects)
    public void spinnerItemSelected(int position, long id) {
        Project project = mProjectsAdapter.getProject(position);
        mService.setProject(project.getId());
    }
}
