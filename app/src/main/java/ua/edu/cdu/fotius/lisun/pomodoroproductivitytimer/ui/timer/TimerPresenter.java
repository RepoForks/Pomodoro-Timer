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

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.TimeUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ShortenSubscriber;

public class TimerPresenter extends MvpPresenter<TimerView> {
    private final DataManager mDataManager;
    private Subscription mProjectsSubscription;
    private Subscription mTotalsSubscription;

    @Inject
    public TimerPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detach() {
        super.detach();
        RxUtil.unsubscribe(mProjectsSubscription);
        RxUtil.unsubscribe(mTotalsSubscription);
    }

    public void loadProjects(String errorMessage) {
        RxUtil.unsubscribe(mProjectsSubscription);
        mProjectsSubscription = mDataManager.getProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<List<Project>>() {
                    @Override
                    public void onError(Throwable e) {
                        getView().showError(errorMessage);
                    }

                    @Override
                    public void onNext(List<Project> projects) {
                        Project unknown = new Project();
                        unknown.setId(Project.NO_ID_VALUE);
                        unknown.setName(null);
                        projects.add(0, unknown);
                        getView().showProjects(projects);
                    }
                });
    }

    public void loadTodayTotal(String errorMessage) {
        RxUtil.unsubscribe(mTotalsSubscription);
        Date today = TimeUtil.timestampMidnight();
        Date now = TimeUtil.timestampNow();
        mTotalsSubscription = mDataManager.getCompletedSessions(today, now)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(List::size)
                .subscribe(new ShortenSubscriber<Integer>() {
                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "");
                        getView().showError(errorMessage);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        getView().showTodaysTotal(integer);
                    }
                });
    }
}
