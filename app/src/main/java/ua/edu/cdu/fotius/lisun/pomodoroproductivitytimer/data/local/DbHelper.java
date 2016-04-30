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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmQuery;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.FinishedSession;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.MathUtil;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ProjectStatistics;

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
        Number id = realm.where(clazz).max(DbAttributes.ID);
        long nextId = (id == null) ? 1 : id.longValue() + 1;
        return nextId;
    }

    public Project renameProject(long id, String name) {
        Realm realm = Realm.getDefaultInstance();
        Project project = realm.where(Project.class)
                .equalTo(DbAttributes.ID, id)
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
                .equalTo(DbAttributes.ID, id)
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

    public FinishedSession saveFinishedSession(long projectId, long workedInMillis) {
        Realm realm = Realm.getDefaultInstance();
        FinishedSession session = new FinishedSession();
        session.setId(generateId(realm, FinishedSession.class));
        session.setProjectId(projectId);
        session.setTimestamp(new Date());
        session.setWorkedTimeInMillis(workedInMillis);
        realm.beginTransaction();
        realm.copyToRealm(session);
        realm.commitTransaction();
        realm.close();
        return session;
    }
    
    public List<ProjectStatistics> statistics(Date from, Date to) {
        Realm realm = Realm.getDefaultInstance();
        List<FinishedSession> sessions = distinctFinishedSessions(realm, from, to);
        ProjectStatistics unknown = new ProjectStatistics(Project.NO_ID_VALUE, null);
        long totalWorked = totalWorked(realm, from, to);
        long projectId, worked;
        String projectName;
        double workPercentage;
        List<ProjectStatistics> statistics = new ArrayList<>();
        for (FinishedSession session : sessions) {
            Project project = project(realm, session.getProjectId());
            if(project != null) {
                projectName = project.getName();
                projectId = project.getId();
                worked = totalProjectWorked(realm, from, to, projectId);
                workPercentage = MathUtil.percentage(totalWorked, worked);
                addToStatisticsList(statistics, new ProjectStatistics(projectId, projectName,
                        workPercentage, worked));
            } else {
                worked = totalProjectWorked(realm, from, to, session.getProjectId());
                workPercentage = MathUtil.percentage(totalWorked, worked);
                unknown.setWorkedInMillis(unknown.getWorkedInMillis() + worked);
                unknown.setWorkPercentage(unknown.getWorkPercentage() + workPercentage);
            }
        }
        addToStatisticsList(statistics, unknown);
        realm.close();
        return statistics;
    }

    private long totalWorked(Realm realm, Date from, Date to) {
        return realm.where(FinishedSession.class)
                .between(DbAttributes.SESSION_TIMESTAMP, from, to)
                .sum(DbAttributes.SESSION_WORKED).longValue();
    }

    private long totalProjectWorked(Realm realm, Date from, Date to, long id) {
        return realm.where(FinishedSession.class)
                .between(DbAttributes.SESSION_TIMESTAMP, from, to)
                .equalTo(DbAttributes.SESSION_PROJECT_ID, id)
                .sum(DbAttributes.SESSION_WORKED)
                .longValue();
    }

    private List<FinishedSession> distinctFinishedSessions(Realm realm, Date from, Date to) {
        List<FinishedSession> sessions = realm.where(FinishedSession.class)
                .between(DbAttributes.SESSION_TIMESTAMP, from, to)
                .distinct(DbAttributes.SESSION_PROJECT_ID);
        return realm.copyFromRealm(sessions);
    }

    private Project project(Realm realm, long id) {
        Project project = realm.where(Project.class).equalTo(DbAttributes.ID, id).findFirst();
        if(project != null) {
            project = realm.copyFromRealm(project);
        }
        return project;
    }

    private void addToStatisticsList(List<ProjectStatistics> stats, ProjectStatistics projectStat) {
        if(projectStat.getWorkedInMillis() > 0) {
            stats.add(projectStat);
        }
    }

    public List<FinishedSession> finishedSessions(Date from, Date to) {
        Realm realm = Realm.getDefaultInstance();
        List<FinishedSession> sessions = realm.where(FinishedSession.class)
                .between(DbAttributes.SESSION_TIMESTAMP, from, to)
                .findAll();
        sessions = realm.copyFromRealm(sessions);
        realm.close();
        return sessions;
    }
}
