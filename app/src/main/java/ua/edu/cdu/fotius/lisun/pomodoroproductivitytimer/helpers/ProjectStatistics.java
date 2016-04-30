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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers;

public class ProjectStatistics {
    private long mProjectId;
    private String mProjectName;
    private double mWorkPercentage;
    private long mWorkedInMillis;

    public ProjectStatistics(long projectId, String projectName, double workPercentage,
                             long workedInMillis) {
        mProjectId = projectId;
        mProjectName = projectName;
        mWorkPercentage = workPercentage;
        mWorkedInMillis = workedInMillis;
    }

    public ProjectStatistics(long projectId, String projectName) {
        mProjectId = projectId;
        mProjectName = projectName;
    }

    public ProjectStatistics() {
    }

    public long getProjectId() {
        return mProjectId;
    }

    public void setProjectId(long projectId) {
        mProjectId = projectId;
    }

    public String getProjectName() {
        return mProjectName;
    }

    public void setProjectName(String projectName) {
        mProjectName = projectName;
    }

    public double getWorkPercentage() {
        return mWorkPercentage;
    }

    public void setWorkPercentage(double workPercentage) {
        mWorkPercentage = workPercentage;
    }

    public long getWorkedInMillis() {
        return mWorkedInMillis;
    }

    public void setWorkedInMillis(long workedInMillis) {
        mWorkedInMillis = workedInMillis;
    }

    public String toString() {
        return ("[mProjectId=" + mProjectId + "; mProjectName=" +
                mProjectName + "; mWorkPercentage=" + mWorkPercentage +
                "; mWorkedInMillis=" + mWorkedInMillis);
    }
}
