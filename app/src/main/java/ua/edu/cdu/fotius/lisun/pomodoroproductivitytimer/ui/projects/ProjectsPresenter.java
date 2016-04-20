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

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpPresenter;

public class ProjectsPresenter extends MvpPresenter<ProjectsView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public ProjectsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
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

    public void saveProject(String name) {
        Project project = new Project();
        project.setName(name);
        project.setCreationDate(new Date());
        unsubscribe();
        mSubscription = mDataManager.saveProject(project).subscribe(new Subscriber<Project>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "");
            }

            @Override
            public void onNext(Project project) {
                getView().showNewProject(project);
            }
        });
    }

    public void getProjects() {
        unsubscribe();
        mSubscription = mDataManager.getProjects().subscribe(new Subscriber<List<Project>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "");
            }

            @Override
            public void onNext(List<Project> projects) {
                if(projects.isEmpty()) {
                    getView().showNoProjects();
                } else {
                    getView().showProjects(projects);
                }
            }
        });
    }
}
