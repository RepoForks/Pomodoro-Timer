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
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.FinishedSession;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Preferences;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;

public class TimerServicePresenter extends MvpPresenter<TimerServiceView> {

    private DataManager mDataManager;

    @Inject
    public TimerServicePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void loadPreferences() {
        mDataManager.getPreferences().subscribe(new Subscriber<Preferences>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "");
            }

            @Override
            public void onNext(Preferences preferences) {
                getView().setPreferences(preferences);
            }
        });
    }

    public void saveFinishedSession(long projectId, int workedInMinutes) {
        // Do nothing onNext.
        // Only for handling errors
        mDataManager.saveFinishedSession(projectId, workedInMinutes).subscribe(new Subscriber<FinishedSession>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "");
            }

            @Override
            public void onNext(FinishedSession finishedSession) {

            }
        });
    }
}
