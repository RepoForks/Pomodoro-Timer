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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.statistics;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ProjectStatistics;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerStatisticsActivityComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.modules.StatisticsActivityModule;

import static butterknife.ButterKnife.findById;

public class StatisticsActivity extends AppCompatActivity implements StatisticsView {

    @Inject
    StatisticsAdapter mAdapter;
    @Inject
    StatisticsPresenter mPresenter;
    @Bind(R.id.root_layout)
    CoordinatorLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);
        DaggerStatisticsActivityComponent.builder()
                .statisticsActivityModule(new StatisticsActivityModule(this))
                .applicationComponent(PomodoroProductivityTimerApplication.get(this)
                        .getApplicationComponent())
                .build().inject(this);
        ButterKnife.bind(this);
        setUpToolbar();
        setUpSpinner();
        setUpList();
        mPresenter.attach(this);
        spinnerItemSelected(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    private void setUpToolbar() {
        Toolbar toolbar = findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.toolbar_spinner_values, R.layout.toolbar_spinner_checked);
        adapter.setDropDownViewResource(R.layout.toolbar_spinner_item);
        Spinner spinner = findById(this, R.id.sp_periods);
        spinner.setAdapter(adapter);
    }

    private void setUpList() {
        RecyclerView list = findById(this, R.id.rv_statistics);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);
    }

    @OnItemSelected(R.id.sp_periods)
    public void spinnerItemSelected(int position) {
        String errorMessage = getString(R.string.statistics_error_loading);
        if(position == 0) {
            mPresenter.loadTodayStatistics(errorMessage);
        } if(position == 1) {
            mPresenter.loadWeekStatistics(errorMessage);
        }
    }

    @Override
    public void showStatistics(List<ProjectStatistics> statistics) {
        mAdapter.setStatistics(statistics);
    }

    @Override
    public void showError(String error) {
        makeSnack(error);
    }

    @Override
    public void showNoStatistics() {
        mAdapter.setStatistics(new ArrayList<>());
        makeSnack(getString(R.string.statistics_no_statistics));
    }

    private void makeSnack(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
