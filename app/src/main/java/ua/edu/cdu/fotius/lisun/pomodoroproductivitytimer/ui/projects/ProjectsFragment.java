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
import android.support.v7.widget.SearchView;
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
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerProjectsFragmentComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects.adapter.MenuClickResult;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects.adapter.ProjectsAdapter;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.dialogs.ProjectNameDialogFragment;

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
        View v = inflater.inflate(R.layout.projects_fragment, container, false);
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
        showSnack(message, null, null);
    }

    @Override
    public void showProject(Project project) {
        mProjectsAdapter.addProject(project);
    }

    @OnClick(R.id.fab_new_project)
    public void addProjectClicked() {
        String title = getString(R.string.dialog_new_project_title);
        showNameDialog(title, "", result -> mPresenter.createProject(result.getNewName()));
    }

    private Subscription subscribeToMenuEvents() {
        return mProjectsAdapter.getObserver().subscribe(result -> {
            if (result.getAction() == MenuClickResult.ACTION_RENAME) {
                onRenameAction(result.getProject(), result);
            } else if (result.getAction() == MenuClickResult.ACTION_DELETE) {
                onDeleteAction(result.getProject(), result);
            }
        });
    }

    private void onRenameAction(Project project, MenuClickResult result) {
        String title = ProjectsFragment.this.getString(R.string.dialog_rename_project_title);
        ProjectsFragment.this.showNameDialog(title, project.getName(), new Action1<ProjectNameDialogFragment.Result>() {
            @Override
            public void call(ProjectNameDialogFragment.Result r) {
                String newName = r.getNewName();
                mProjectsAdapter.renameProject(result.getPosition(), newName);
                String msg = ProjectsFragment.this.getString(R.string.snack_msg_rename, project.getName(), newName);
                showSnack(msg, v -> mProjectsAdapter.renameProject(result.getPosition(), project.getName()), new Snackbar.Callback() {
                    //FIXME: bad idea to make async calls in onDismissed,
                    //but db solutions is forcing to do this. Fix after redesigning db
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if(event != DISMISS_EVENT_ACTION) {
                            mPresenter.renameProject(project.getId(), newName);
                        }
                    }
                });
            }
        });
    }

    private void onDeleteAction(Project project, MenuClickResult result) {
        String message = ProjectsFragment.this.getString(R.string.snack_msg_delete, project.getName());
        mProjectsAdapter.deleteProject(result.getPosition());
        showSnack(message, v -> mProjectsAdapter.insertProject(result.getProject(), result.getPosition()), new Snackbar.Callback() {
            //FIXME: bad idea to make async calls in onDismissed,
            //but db solutions is forcing to do this. Fix after redesigning db
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if(event != DISMISS_EVENT_ACTION) {
                    mPresenter.deleteProject(project.getId());
                }
            }
        });
        Snackbar snack = Snackbar.make(mNewProjectFab, message, Snackbar.LENGTH_LONG);
        snack.setAction(R.string.snack_undo, v -> mProjectsAdapter.insertProject(result.getProject(), result.getPosition()));
    }

    private void showSnack(String message, View.OnClickListener action, Snackbar.Callback callback) {
        Snackbar snack = Snackbar.make(mNewProjectFab, message, Snackbar.LENGTH_LONG);
        if (action != null) {
            snack.setAction(R.string.snack_undo, action);
        }
        if (callback != null) {
            snack.setCallback(callback);
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
