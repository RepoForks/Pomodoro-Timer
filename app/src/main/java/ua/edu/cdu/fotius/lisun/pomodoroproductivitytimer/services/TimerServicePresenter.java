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

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.FinishedSession;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Preferences;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.ShortenSubscriber;

public class TimerServicePresenter extends MvpPresenter<TimerServiceView> {

    private DataManager mDataManager;
    private Subscription mLoadSubscription;
    private Subscription mSaveSubscription;

    @Inject
    public TimerServicePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detach() {
        super.detach();
        RxUtil.unsubscribe(mLoadSubscription);
        RxUtil.unsubscribe(mSaveSubscription);
    }

    public void loadPreferences() {
        RxUtil.unsubscribe(mLoadSubscription);
        mLoadSubscription = mDataManager.getPreferences()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<Preferences>() {
                    @Override
                    public void onNext(Preferences preferences) {
                        getView().setPreferences(preferences);
                    }
                });
    }

    public void saveFinishedSession(long projectId, int workedInMinutes) {
        RxUtil.unsubscribe(mSaveSubscription);
        // Do nothing onNext.
        // Only for handling errors
        mSaveSubscription = mDataManager.saveFinishedSession(projectId, workedInMinutes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<FinishedSession>() {
                    @Override
                    public void onNext(FinishedSession finishedSession) {
                    }
                });
    }
}
