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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects.adapter;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxBus;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder>{

    private List<Project> mProjects;
    private RxBus mRxBus;

    @Inject
    public ProjectsAdapter(RxBus rxBus) {
        mProjects = new ArrayList<>();
        mRxBus = rxBus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.cardview_single_project, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mProjectName.setText(mProjects.get(position).getName());
        holder.mCreationDate.setText(mProjects.get(position).getCreationDateString());
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    public void setProjects(List<Project> projects) {
        mProjects = projects;
        notifyDataSetChanged();
    }

    public void addProject(Project project) {
        mProjects.add(project);
        notifyItemInserted(mProjects.size() - 1);
    }

    public Observable<MenuClickResult> getObserver() {
        return mRxBus.getObservable(MenuClickResult.class);
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements PopupMenu.OnMenuItemClickListener {

        @Bind(R.id.tv_project_name)
        public TextView mProjectName;
        @Bind(R.id.tv_creation_date)
        public TextView mCreationDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ib_menu)
        public void showItemMenu(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.menu_project_overflow);
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            String action = null;
            if(item.getItemId() == R.id.mpo_delete) {
                action = MenuClickResult.ACTION_DELETE;
            } else if(item.getItemId() == R.id.mpo_rename) {
                action = MenuClickResult.ACTION_RENAME;
            }
            Project project = mProjects.get(getLayoutPosition());
            mRxBus.send(new MenuClickResult(action, project));
            return true;
        }
    }

}
