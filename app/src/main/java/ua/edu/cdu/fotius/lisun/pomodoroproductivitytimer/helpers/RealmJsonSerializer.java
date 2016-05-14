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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;

public class RealmJsonSerializer {

    private Gson mGson;

    public RealmJsonSerializer() {
        mGson = new GsonBuilder()
                .setExclusionStrategies(mExclusionStrategy)
                .create();
    }

    public String toJson(Class realmClass) {
        Realm realm = Realm.getDefaultInstance();
        List projects = realm.where(realmClass).findAll();
        projects = realm.copyFromRealm(projects);
        realm.close();
        return mGson.toJson(projects);
    }

    private ExclusionStrategy mExclusionStrategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getDeclaringClass().equals(RealmObject.class);
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

    };
}
