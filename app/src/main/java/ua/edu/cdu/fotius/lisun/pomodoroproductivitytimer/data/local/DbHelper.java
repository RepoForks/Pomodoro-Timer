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
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Preferences;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;

@Singleton
public class DbHelper {

    @Inject
    public DbHelper() {
    }

    public Project newProject(String name) {
        Realm realm = Realm.getDefaultInstance();
        Project project = createProjectInstance(realm, name);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(project);
        realm.commitTransaction();
        realm.close();
        return project;
    }

    private Project createProjectInstance(Realm realm, String name) {
        Project project = new Project();
        project.setId(generateId(realm));
        project.setName(name);
        project.setCreationDate(new Date());
        project.setWorkSessionDuration(Preferences.DefaultValues.WORK_SESSION_DURATION);
        return project;
    }

    private long generateId(Realm realm) {
        Number id = realm.where(Project.class).max("id");
        long nextId = (id == null) ? 1 : id.longValue() + 1;
        return nextId;
    }

    public Project renameProject(long id, String name) {
        Realm realm = Realm.getDefaultInstance();
        Project project = realm.where(Project.class)
                .equalTo(DbAttributes._ID_PROJECT, id)
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
        Project project= realm.where(Project.class)
                .equalTo(DbAttributes._ID_PROJECT, id)
                .findFirst();
        Project projectCopy = realm.copyFromRealm(project);
        realm.beginTransaction();
        project.removeFromRealm();
        realm.commitTransaction();
        realm.close();
        return projectCopy;
    }

    public List<Project> projects() {
        Realm realm = Realm.getDefaultInstance();
        List<Project> projects = realm.where(Project.class).findAll();
        projects = realm.copyFromRealm(projects);
        realm.close();
        return projects;
    }
}
