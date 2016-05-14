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
package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data;


import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.local.DbHelper;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.local.PreferencesHelper;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Backup;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.FinishedSession;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.PreferencePair;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.Preferences;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ProjectStatistics;

@Singleton
public class DataManager {

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    DbHelper mDbHelper;

    @Inject
    public DataManager() {
    }

    public Observable<PreferencePair> savePreference(PreferencePair preferencePair) {
        return Observable.fromCallable(() -> mPreferencesHelper.putPreference(preferencePair));
    }

    public Observable<Preferences> getPreferences() {
        return Observable.fromCallable(() -> mPreferencesHelper.preferences());
    }

    public Observable<Project> insertProject(Project project) {
        return Observable.fromCallable(() -> mDbHelper.insertProject(project));
    }

    public Observable<Project> createProject(String name) {
        return Observable.fromCallable(() -> mDbHelper.newProject(name));
    }

    public Observable<Project> renameProject(long id, String name) {
        return Observable.fromCallable(() -> mDbHelper.renameProject(id, name));
    }

    public Observable<Project> deleteProject(long id) {
        return Observable.fromCallable(() -> mDbHelper.deleteProject(id));
    }

    public Observable<List<Project>> getProjects() {
        return Observable.fromCallable(() -> mDbHelper.projects());
    }

    public Observable<FinishedSession> saveFinishedSession(long projectId, long workedInMillis) {
        return Observable.fromCallable(() -> mDbHelper.saveFinishedSession(projectId, workedInMillis));
    }

    public Observable<List<FinishedSession>> getCompletedSessions(Date from, Date to) {
        return Observable.fromCallable(() -> mDbHelper.finishedSessions(from, to));
    }

    public Observable<List<ProjectStatistics>> getStatistics(Date from, Date to) {
        return Observable.fromCallable(() -> mDbHelper.statistics(from, to));
    }

    public Observable<List<Backup>> getBackups() {
        return Observable.fromCallable(() -> mDbHelper.backups());
    }

    public Observable<Backup> createBackup(File rootDir) {
        return Observable.fromCallable(() -> mDbHelper.backup(rootDir));
    }

    public Observable<Backup> restoreBackup(Backup backup) {
        return Observable.fromCallable(() -> mDbHelper.restore(backup));
    }
}
