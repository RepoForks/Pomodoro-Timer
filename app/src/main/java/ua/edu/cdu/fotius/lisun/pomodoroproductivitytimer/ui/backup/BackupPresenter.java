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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.backup;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Backup;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ShortenSubscriber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;

public class BackupPresenter extends MvpPresenter<BackupView> {

    private final DataManager mDataManager;
    private Subscription mLoadSubscription;
    private Subscription mCreateSubscription;

    @Inject
    public BackupPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detach() {
        super.detach();
        RxUtil.unsubscribe(mLoadSubscription);
        RxUtil.unsubscribe(mCreateSubscription);
    }

    public void loadBackups(String errorMessage) {
        RxUtil.unsubscribe(mLoadSubscription);
        mLoadSubscription = mDataManager.getBackups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<List<Backup>>() {
                    @Override
                    public void onError(Throwable e) {
                        getView().showError(errorMessage);
                    }

                    @Override
                    public void onNext(List<Backup> backups) {
                        if(backups.size() > 0) {
                            getView().showBackups(backups);
                        } else {
                            getView().showNoBackups();
                        }
                    }
                });
    }

    public void createBackup(File rootDir, String errorMessage) {
        RxUtil.unsubscribe(mCreateSubscription);
        mCreateSubscription = mDataManager.createBackup(rootDir)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<Backup>() {
                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "");
                        getView().showError(errorMessage);
                    }

                    @Override
                    public void onNext(Backup backup) {
                        if(backup != null) {
                            getView().showCreatedBackup(backup);
                        }
                    }
                });
    }
}
