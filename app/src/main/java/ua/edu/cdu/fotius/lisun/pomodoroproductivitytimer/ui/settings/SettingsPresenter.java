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

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.PreferencePair;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.Preferences;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ShortenSubscriber;

public class SettingsPresenter extends MvpPresenter<SettingsView> {

    private final DataManager mDataManager;
    private Subscription mSaveSubscription;
    private Subscription mLoadSubscription;

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detach() {
        super.detach();
        RxUtil.unsubscribe(mSaveSubscription);
        RxUtil.unsubscribe(mLoadSubscription);
    }

    public void savePreference(PreferencePair preferencePair) {
        RxUtil.unsubscribe(mSaveSubscription);
        mSaveSubscription = mDataManager.savePreference(preferencePair)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<PreferencePair>() {
                    @Override
                    public void onNext(PreferencePair preferencePair) {
                        getView().setPreferenceSummary(preferencePair.getKey(), preferencePair.getValue());
                    }
                });
    }

    public void loadPreferences() {
        RxUtil.unsubscribe(mLoadSubscription);
        mLoadSubscription = mDataManager.getPreferences()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<Preferences>() {
                    @Override
                    public void onNext(Preferences preferences) {
                        getView().setPreferencesSummary(preferences);
                    }
                });
    }
}
