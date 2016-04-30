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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ShortenSubscriber;

public class ProjectsPresenter extends MvpPresenter<ProjectsView> {

    private final DataManager mDataManager;
    private Subscription mInsertSubscription;
    private Subscription mCreateSubscription;
    private Subscription mRenameSubscription;
    private Subscription mDeleteSubscription;
    private Subscription mProjectSubscription;

    @Inject
    public ProjectsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detach() {
        super.detach();
        RxUtil.unsubscribe(mInsertSubscription);
        RxUtil.unsubscribe(mCreateSubscription);
        RxUtil.unsubscribe(mRenameSubscription);
        RxUtil.unsubscribe(mDeleteSubscription);
        RxUtil.unsubscribe(mProjectSubscription);
    }

    public void createProject(String name) {
        RxUtil.unsubscribe(mCreateSubscription);
        mCreateSubscription = mDataManager.createProject(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<Project>() {
                    @Override
                    public void onNext(Project project) {
                        getView().showProject(project);
                    }
                });
    }

    public void renameProject(long id, String name) {
        RxUtil.unsubscribe(mRenameSubscription);
        mRenameSubscription = mDataManager.renameProject(id, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<Project>() {
                    //TODO: error
                    @Override
                    public void onNext(Project project) {
                    }
                });
    }

    public void deleteProject(long id) {
        RxUtil.unsubscribe(mDeleteSubscription);
        mDeleteSubscription = mDataManager.deleteProject(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<Project>() {
                    @Override
                    public void onNext(Project project) {

                    }
                    //TODO: error
                });
    }

    public void getProjects() {
        RxUtil.unsubscribe(mProjectSubscription);
        mProjectSubscription = mDataManager.getProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ShortenSubscriber<List<Project>>() {
                    @Override
                    public void onNext(List<Project> projects) {
                        if (projects.isEmpty()) {
                            getView().showNoProjects();
                        } else {
                            getView().showProjects(projects);
                        }
                    }
                });
    }
}
