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

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;

public class MenuClickResult {
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_RENAME = "rename";

    private String mAction;
    private Project mProject;
    private int mAdapterPosition;

    public MenuClickResult(String action, Project project, int adapterPosition) {
        mAction = action;
        mProject = project;
        mAdapterPosition = adapterPosition;
    }

    public String getAction() {
        return mAction;
    }

    public Project getProject() {
        return mProject;
    }

    public int getPosition() {
        return mAdapterPosition;
    }
}
