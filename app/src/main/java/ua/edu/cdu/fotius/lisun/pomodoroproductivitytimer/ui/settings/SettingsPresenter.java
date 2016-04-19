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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.settings;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.PreferencePair;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Preferences;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;

public class SettingsPresenter extends MvpPresenter<SettingsView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attach(SettingsView settingsView) {
        super.attach(settingsView);
    }

    @Override
    public void detach() {
        super.detach();
        unsubscribe();
    }

    private void unsubscribe() {
        if((mSubscription != null) && (!mSubscription.isUnsubscribed())) {
            mSubscription.unsubscribe();
        }
    }

    public void savePreference(PreferencePair preferencePair) {
        unsubscribe();
        mSubscription = mDataManager.savePreference(preferencePair).subscribe(new Subscriber<PreferencePair>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "");
            }

            @Override
            public void onNext(PreferencePair preferencePair) {
                getView().setPreferenceSummary(preferencePair.getKey(), preferencePair.getValue());
            }
        });
    }

    public void loadPreferences() {
        unsubscribe();
        mSubscription = mDataManager.getPreferences()
                .subscribe(new Subscriber<Preferences>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "");
                    }

                    @Override
                    public void onNext(Preferences preferences) {
                        getView().setPreferencesSummary(preferences);
                    }
                });
    }
}
