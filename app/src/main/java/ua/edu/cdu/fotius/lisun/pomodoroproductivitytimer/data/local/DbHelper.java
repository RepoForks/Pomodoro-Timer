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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.local;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmQuery;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.FinishedSession;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;

@Singleton
public class DbHelper {

    @Inject
    public DbHelper() {
    }

    public Project insertProject(Project project) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(project);
        realm.commitTransaction();
        realm.close();
        return project;
    }

    public Project newProject(String name) {
        Realm realm = Realm.getDefaultInstance();
        Project project = createProjectInstance(realm, name);
        realm.beginTransaction();
        realm.copyToRealm(project);
        realm.commitTransaction();
        realm.close();
        return project;
    }

    private Project createProjectInstance(Realm realm, String name) {
        Project project = new Project();
        project.setId(generateId(realm, Project.class));
        project.setName(name);
        project.setCreationDate(new Date());
        return project;
    }

    private long generateId(Realm realm, Class clazz) {
        Number id = realm.where(clazz).max(DbAttributes._ID);
        long nextId = (id == null) ? 1 : id.longValue() + 1;
        return nextId;
    }

    public Project renameProject(long id, String name) {
        Realm realm = Realm.getDefaultInstance();
        Project project = realm.where(Project.class)
                .equalTo(DbAttributes._ID, id)
                .findFirst();
        realm.beginTransaction();
        project.setName(name);
        realm.commitTransaction();
        project = realm.copyFromRealm(project);
        realm.close();
        return project;
    }

    public Project deleteProject(long id) {
        Realm realm = Realm.getDefaultInstance();
        Project project = realm.where(Project.class)
                .equalTo(DbAttributes._ID, id)
                .findFirst();
        Project projectCopy = realm.copyFromRealm(project);
        realm.beginTransaction();
        project.removeFromRealm();
        realm.commitTransaction();
        realm.close();
        return projectCopy;
    }

    public List<Project> projects() {
        //TODO: sort name ascending
        Realm realm = Realm.getDefaultInstance();
        List<Project> projects = realm.where(Project.class).findAll();
        projects = realm.copyFromRealm(projects);
        realm.close();
        return projects;
    }


    public FinishedSession saveFinishedSession(long projectId, int workedInMillis) {
        Realm realm = Realm.getDefaultInstance();
        //all sessions with unexisted project
        //ids will be treated as "Unknown" project
        if (!isProjectExists(realm, projectId))
            projectId = Project.NO_ID_VALUE;
        FinishedSession session = new FinishedSession();
        session.setId(generateId(realm, FinishedSession.class));
        session.setProjectId(projectId);
        session.setTimestamp(new Date());
        session.setWorkedTimeInMinutes(workedInMillis);
        realm.beginTransaction();
        realm.copyToRealm(session);
        realm.commitTransaction();
        realm.close();
        return session;
    }

    private boolean isProjectExists(Realm realm, long projectId) {
        RealmQuery query = realm.where(Project.class).equalTo(DbAttributes._ID, projectId);
        return !(query.count() == 0);
    }

    public List<FinishedSession> completedSessions(Date from, Date to) {
        Realm realm = Realm.getDefaultInstance();
        List<FinishedSession> sessions = realm.where(FinishedSession.class)
                .between(DbAttributes._SESSION_TIMESTAMP, from, to)
                .findAll();
        sessions = realm.copyFromRealm(sessions);
        realm.close();
        return sessions;
    }
}
