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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.FinishedSessionRealmProxy;
import io.realm.ProjectRealmProxy;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.json.FinishedSessionSerializer;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.json.ProjectSerializer;
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
        resetFinishedSession(realm, project.getId());
        project.removeFromRealm();
        realm.commitTransaction();
        realm.close();
        return projectCopy;
    }

    private void resetFinishedSession(Realm realm, long id) {
        RealmResults<FinishedSession> sessions = realm.where(FinishedSession.class)
                .equalTo(DbAttributes.SESSION_PROJECT_ID, id)
                .findAll();
        for(int i = 0; i < sessions.size(); i++) {
            sessions.get(i).setProjectId(Project.NO_ID_VALUE);
        }
    }

    public List<Project> projects() {
        Realm realm = Realm.getDefaultInstance();
        List<Project> projects = realm.where(Project.class)
                .findAllSorted(DbAttributes.PROJECT_NAME, Sort.ASCENDING);
        projects = realm.copyFromRealm(projects);
        realm.close();
        return projects;
    }

    public FinishedSession saveFinishedSession(long projectId, long workedInMillis) {
        Realm realm = Realm.getDefaultInstance();
        FinishedSession session = new FinishedSession();
        session.setId(generateId(realm, FinishedSession.class));
        projectId = (project(realm, projectId) == null) ? Project.NO_ID_VALUE : projectId;
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
        List<ProjectStatistics> statistics = new ArrayList<>();
        List<FinishedSession> sessions = distinctFinishedSessions(realm, from, to);
        long totalWorked = totalWorked(realm, from, to);
        ProjectStatistics unknown = unknownStatistics(realm, from, to, totalWorked);
        addToStatisticsList(statistics, unknown);
        long worked;
        double workPercentage;
        for (FinishedSession session : sessions) {
            Project project = project(realm, session.getProjectId());
            if((project != null) && (project.getId() != Project.NO_ID_VALUE)) {
                worked = totalProjectWorked(realm, from, to, project.getId());
                workPercentage = MathUtil.percentage(totalWorked, worked);
                addToStatisticsList(statistics, new ProjectStatistics(project.getId(), project.getName(),
                        workPercentage, worked));
            }
        };
        realm.close();
        return statistics;
    }

    private ProjectStatistics unknownStatistics(Realm realm, Date from, Date to, long totalWorked) {
        long worked = totalProjectWorked(realm, from, to, Project.NO_ID_VALUE);
        double workPercentage = MathUtil.percentage(totalWorked, worked);
        ProjectStatistics unknown = new ProjectStatistics(Project.NO_ID_VALUE, null);
        unknown.setWorkedInMillis(worked);
        unknown.setWorkPercentage(workPercentage);
        return unknown;
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

    public void saveDbSnapshot() throws ClassNotFoundException {
        Realm realm = Realm.getDefaultInstance();

        List<Project> projects = realm.where(Project.class).findAll();
        projects = realm.copyFromRealm(projects);

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(Project.class, new ProjectSerializer())
                .registerTypeAdapter(FinishedSession.class,
                        new FinishedSessionSerializer())
                .create();

        String json = gson.toJson(projects);
        Timber.i("DbHelper#saveDbSnapshot. JSON: %s", json);

        List<FinishedSession> sessions = realm.where(FinishedSession.class).findAll();
        sessions = realm.copyFromRealm(sessions);
        String finishedJson = gson.toJson(sessions);
        Timber.i("DbHelper#saveDbSnapshot. SESSIONS: %s", finishedJson);
        realm.close();
    }
}
