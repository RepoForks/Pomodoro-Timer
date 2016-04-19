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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;

@Singleton
public class DbHelper {

    @Inject
    public DbHelper() {
    }

    public Project save(Project project) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setProjectId(realm, project);
        realm.copyToRealm(project);
        realm.commitTransaction();
        realm.close();
        return project;
    }

    private void setProjectId(Realm realm, Project project) {
        Number id = realm.where(Project.class).max("id");
        long nextId = (id == null) ? 1 : id.longValue() + 1;
        project.setId(nextId);
    }

    public List<Project> projects() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<Project> projects = realm.where(Project.class).findAll();
        projects = realm.copyFromRealm(projects);
        realm.commitTransaction();
        realm.close();
        return projects;
    }
}
