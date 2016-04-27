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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;

public class ProjectsAdapter extends BaseAdapter {
    List<Project> mProjects;

    @Inject
    public ProjectsAdapter() {
        mProjects = new ArrayList<>();

    }

    @Override
    public int getCount() {
        return mProjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mProjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mProjects.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Context context = parent.getContext();
        if(convertView == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.spinner_item, parent, false);
        } else {
            view = convertView;
        }

        if(view instanceof TextView) {
            Project project = mProjects.get(position);
            String text = ((project.getName() == null) || (project.getId() == Project.NO_ID_VALUE))
                    ? context.getString(R.string.unknown_project_name) : project.getName();
            ((TextView)view).setText(text);
        }
        return view;
    }

    public void setProjects(List<Project> projects) {
        mProjects = projects;
        notifyDataSetChanged();
    }
}
