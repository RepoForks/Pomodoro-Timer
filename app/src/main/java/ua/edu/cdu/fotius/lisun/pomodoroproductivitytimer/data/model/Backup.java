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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Backup extends RealmObject {

    @PrimaryKey
    private String backupName;
    private String projectsBackupPath;
    private String sessionsBackupPath;
    private long creationDate;
    private long projectsQuantity;
    private int totalWorked;

    public String getBackupName() {
        return backupName;
    }

    public void setBackupName(String backupName) {
        this.backupName = backupName;
    }

    public String getProjectsBackupPath() {
        return projectsBackupPath;
    }

    public void setProjectsBackupPath(String projectsBackupPath) {
        this.projectsBackupPath = projectsBackupPath;
    }

    public String getSessionsBackupPath() {
        return sessionsBackupPath;
    }

    public void setSessionsBackupPath(String sessionsBackupPath) {
        this.sessionsBackupPath = sessionsBackupPath;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getProjectsQuantity() {
        return projectsQuantity;
    }

    public void setProjectsQuantity(long projectsQuantity) {
        this.projectsQuantity = projectsQuantity;
    }

    public int getTotalWorked() {
        return totalWorked;
    }

    public void setTotalWorked(int totalWorked) {
        this.totalWorked = totalWorked;
    }

    @Override
    public String toString() {
        return "BACKUP: [name=" + backupName +
                "; projectBackupPath=" + projectsBackupPath +
                "; sessionBackUpPath=" + sessionsBackupPath +
                "; creationDate=" + creationDate +
                "; projectQuantity=" + projectsQuantity +
                "; totalWorked=" + totalWorked;
    }
}
