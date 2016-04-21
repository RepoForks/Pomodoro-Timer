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
import rx.functions.Action1;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerProjectsFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects.adapter.MenuClickResult;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects.adapter.ProjectsAdapter;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.dialogs.ProjectNameDialogFragment;

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
    private Subscription mDialogSubscription;
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
        View v = inflater.inflate(R.layout.fragment_projects, container, false);
        RecyclerView recyclerView = findById(v, R.id.rv_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
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
    public void onStart() {
        super.onStart();
        mMenuSubscription = subscribeToMenuEvents();
    }

    @Override
    public void onStop() {
        super.onStop();
        RxUtil.unsubscribe(mMenuSubscription);
        RxUtil.unsubscribe(mDialogSubscription);
    }

    @Override
    public void showProjects(List<Project> projects) {
        mProjectsAdapter.setProjects(projects);
    }

    @Override
    public void showNoProjects() {
        mProjectsAdapter.setProjects(new ArrayList<>());
        String message = getString(R.string.snack_msg_no_projects);
        showSnack(message, null);
    }

    @Override
    public void showProject(Project project) {
        mProjectsAdapter.addProject(project);
    }

    @Override
    public void showProject(Project project, int position) {
        mProjectsAdapter.insertProject(project, position);
    }

    @Override
    public void removeProject(int position) {
        mProjectsAdapter.deleteProject(position);
    }

    @Override
    public void updateProjectName(int adapterPosition, String newName) {
        mProjectsAdapter.renameProject(adapterPosition, newName);
    }

    @OnClick(R.id.fab_new_project)
    public void addProjectClicked() {
        String title = getString(R.string.dialog_new_project_title);
        showNameDialog(title, "", result -> mPresenter.createProject(result.getNewName()));
    }

    private Subscription subscribeToMenuEvents() {
        return mProjectsAdapter.getObserver().subscribe(result -> {
            long id = result.getProject().getId();
            String projectName = result.getProject().getName();
            int position = result.getAdapterPosition();
            if (result.getAction() == MenuClickResult.ACTION_RENAME) {
                String title = getString(R.string.dialog_rename_project_title);
                showNameDialog(title, projectName, r -> {
                    String newName = r.getNewName();
                    mPresenter.renameProject(position, id, r.getNewName());
                    String msg = getString(R.string.snack_msg_rename, projectName, newName);
                    showSnack(msg, v -> mPresenter.renameProject(position, id, projectName));
                });
            } else if (result.getAction() == MenuClickResult.ACTION_DELETE) {
                String message = getString(R.string.snack_msg_delete, projectName);
                mPresenter.deleteProject(id, position);
                showSnack(message, v -> mPresenter.insertProject(result.getProject(), position));
            }
        });
    }

    private void showSnack(String message, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(mNewProjectFab, message, Snackbar.LENGTH_LONG);
        if (action != null) {
            snack.setAction(R.string.snack_undo, action);
        }
        snack.show();
    }

    private void showNameDialog(String title, String initValue,
                                Action1<ProjectNameDialogFragment.Result> action) {
        RxUtil.unsubscribe(mDialogSubscription);
        mDialogSubscription = ProjectNameDialogFragment
                .show(getFragmentManager(), mRxBus, title, initValue)
                .subscribe(action);
    }
}
