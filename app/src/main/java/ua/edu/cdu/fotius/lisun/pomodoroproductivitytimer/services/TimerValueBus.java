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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class TimerValueBus {
    private Subject<Object, Object> mSubject = new SerializedSubject<>(PublishSubject.create());

    public void send(Object object) {
        mSubject.onNext(object);
    }

    public Observable<Object> getObservable()  {
        return mSubject;
    }
}
