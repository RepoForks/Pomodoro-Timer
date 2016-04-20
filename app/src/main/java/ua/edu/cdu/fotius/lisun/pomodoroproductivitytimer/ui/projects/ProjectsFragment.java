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


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerProjectsFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.DialogFactory;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.dialogs.ProjectNameDialogFragment;

import static butterknife.ButterKnife.findById;

public class ProjectsFragment extends BaseFragment implements ProjectsView, PopupMenu.OnMenuItemClickListener {

    public static final String FRAGMENT_TAG = "projects_fragment";

    @Inject
    ProjectsAdapter mProjectsAdapter;
    @Inject
    RxBus mRxBus;
    @Inject
    ProjectsPresenter mPresenter;

    @Bind(R.id.fab_new_project)
    FloatingActionButton mNewProjectFab;

    private Context mContext;
    private Subscription mSubscription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerProjectsFragmentComponent.builder()
                .applicationComponent(PomodoroProductivityTimerApplication.get(mContext)
                        .getApplicationComponent())
                .build().inject(this);
        mPresenter.attach(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_projects, container, false);
        RecyclerView recyclerView = findById(v, R.id.rv_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mProjectsAdapter.setMenuItemClickListener(this);
        recyclerView.setAdapter(mProjectsAdapter);
        ButterKnife.bind(this, v);
        mPresenter.getProjects();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if ((mSubscription != null) &&
                (!mSubscription.isUnsubscribed())) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void showProjects(List<Project> projects) {
        mProjectsAdapter.setProjects(projects);
    }

    @Override
    public void showNoProjects() {
        mProjectsAdapter.setProjects(new ArrayList<>());
        Snackbar.make(mNewProjectFab, R.string.fragment_no_projects_message,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showNewProject(Project project) {
        mProjectsAdapter.addProject(project);
    }

    @OnClick(R.id.fab_new_project)
    public void addProjectClicked() {
        mSubscription = ProjectNameDialogFragment
                .show(getFragmentManager(), mRxBus, getString(R.string.dialog_new_project_title), "")
                .map(result -> {
                    Project project = new Project();
                    project.setName(result.getNewName());
                    return project;
                })
                .subscribe(project -> mPresenter.saveProject(project));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.mpo_delete) {
            Timber.i("Delete");
        } else if(item.getItemId() == R.id.mpo_rename) {
            Timber.i("Rename");
        }
        return true;
    }
}
