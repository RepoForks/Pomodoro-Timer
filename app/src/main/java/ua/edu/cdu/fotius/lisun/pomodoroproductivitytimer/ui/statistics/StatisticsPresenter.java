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

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ProjectStatistics;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ShortenSubscriber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.TimeUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;

public class StatisticsPresenter extends MvpPresenter<StatisticsView> {

    private final DataManager mDataManager;

    @Inject
    public StatisticsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void loadTodayStatistics(String errorMessage) {
        Date today = TimeUtil.timestampMidnight();
        Date now = TimeUtil.timestampNow();
        loadStatistics(today, now, errorMessage);
    }

    public void loadWeekStatistics(String errorMessage) {
        Date startOfWeek = TimeUtil.timestampStartOfWeek();
        Date now = TimeUtil.timestampNow();
        loadStatistics(startOfWeek, now, errorMessage);
    }

    private void loadStatistics(Date from, Date to, String errorMessage) {
        mDataManager.getStatistics(from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<List<ProjectStatistics>>() {
                    @Override
                    public void onError(Throwable e) {
                        getView().showError(errorMessage);
                    }

                    @Override
                    public void onNext(List<ProjectStatistics> stats) {
                        getView().showStatistics(stats);
                    }
                });
    }
}
