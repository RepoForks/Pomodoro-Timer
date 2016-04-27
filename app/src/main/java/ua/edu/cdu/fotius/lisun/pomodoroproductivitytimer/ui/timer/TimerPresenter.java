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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.ShortenSubscriber;

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

    public void loadProjects() {
        RxUtil.unsubscribe(mProjectsSubscription);
        mProjectsSubscription = mDataManager.getProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<List<Project>>() {
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

    public void loadTodaysTotal() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        Date now = new Date();
        RxUtil.unsubscribe(mTotalsSubscription);
        mTotalsSubscription = mDataManager.getCompletedSessions(today, now)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(List::size)
                .subscribe(new ShortenSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        getView().showTodaysTotal(integer);
                    }
                });
    }
}
