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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerProjectsFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects.adapter.MenuClickResult;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects.adapter.ProjectsAdapter;

import static butterknife.ButterKnife.findById;

public class ProjectsFragment extends BaseFragment implements ProjectsView {

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
    private Subscription mNewNameDialogSubscription;
    private Subscription mRenameDialogSubscription;
    private Subscription mDeleteDialogSubscription;
    private Subscription mMenuSubscription;

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
        View v = inflater.inflate(R.layout.projects_fragment, container, false);
        RecyclerView recyclerView = findById(v, R.id.rv_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mProjectsAdapter.setDateFormatString(getString(R.string.date_formater_string));
        recyclerView.setAdapter(mProjectsAdapter);
        ButterKnife.bind(this, v);
        mPresenter.getProjects(getString(R.string.projects_error_loading));
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMenuSubscription = subscribeToMenuEvents();
        mNewNameDialogSubscription = mRxBus.getObservable(NameDialogFragment.Result.class)
                .subscribe(result -> mPresenter.createProject(result.getName(), getString(R.string.projects_error_creating)));
        mRenameDialogSubscription = mRxBus.getObservable(RenameDialogFragment.Result.class)
                .subscribe(result -> mPresenter.renameProject(result.getProjectId(),
                        result.getNewName(),  result.getListPosition(), getString(R.string.projects_error_renaming)));
        mDeleteDialogSubscription = mRxBus.getObservable(DeleteDialogFragment.Result.class)
                .subscribe(r -> mPresenter.deleteProject(r.getId(), r.getAdapterPosition(), getString(R.string.projects_error_deleting)));
    }

    @Override
    public void onStop() {
        super.onStop();
        RxUtil.unsubscribe(mMenuSubscription);
        RxUtil.unsubscribe(mNewNameDialogSubscription);
        RxUtil.unsubscribe(mRenameDialogSubscription);
        RxUtil.unsubscribe(mDeleteDialogSubscription);
    }

    @Override
    public void showProjects(List<Project> projects) {
        mProjectsAdapter.setProjects(projects);
    }

    @Override
    public void showNoProjects() {
        mProjectsAdapter.setProjects(new ArrayList<>());
        String message = getString(R.string.snack_msg_no_projects);
        showSnack(message);
    }

    @Override
    public void showCreated(Project project) {
        mProjectsAdapter.addProject(project);
        showSnack(getString(R.string.snack_msg_created, project.getName()));
    }

    @Override
    public void showRenamed(int position, String newName) {
        mProjectsAdapter.renameProject(position, newName);
        showSnack(getString(R.string.snack_msg_renamed, newName));
    }

    @Override
    public void projectDeleted(Project project, int position) {
        mProjectsAdapter.deleteProject(position);
        String message = ProjectsFragment.this.getString(R.string.snack_msg_deleted, project.getName());
        showSnack(message);
    }

    @Override
    public void showError(String error) {
        showSnack(error);
    }

    @OnClick(R.id.fab_new_project)
    public void addProjectClicked() {
        String title = getString(R.string.dialog_new_project_title);
        NameDialogFragment.showDialog(getFragmentManager(), title);
    }

    private Subscription subscribeToMenuEvents() {
        return mProjectsAdapter.getObserver().subscribe(result -> {
            Project project = result.getProject();
            if (MenuClickResult.ACTION_RENAME.equals(result.getAction())) {
                String title = ProjectsFragment.this.getString(R.string.dialog_rename_project_title);
                RenameDialogFragment.showDialog(ProjectsFragment.this.getFragmentManager(), project.getId(), title,
                        project.getName(), result.getPosition());
            } else if (MenuClickResult.ACTION_DELETE.equals(result.getAction())) {
                DeleteDialogFragment.show(getFragmentManager(), project.getId(), result.getPosition());
            }
        });
    }

    private void showSnack(String message) {
        Snackbar.make(mNewProjectFab, message, Snackbar.LENGTH_LONG).show();
    }
}
