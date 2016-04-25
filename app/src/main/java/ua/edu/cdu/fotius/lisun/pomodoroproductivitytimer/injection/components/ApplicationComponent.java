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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components;

import javax.inject.Singleton;

import dagger.Component;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.DataManager;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.local.PreferencesKeys;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.modules.ApplicationModule;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.services.TimerService;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.dialogs.NumberDialogFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.dialogs.ProjectNameDialogFragment;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.dialogs.TwoButtonsDialogFragment;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(TimerService service);
    void inject(NumberDialogFragment fragment);
    void inject(ProjectNameDialogFragment fragment);
    void inject(TwoButtonsDialogFragment fragment);
    RxBus provideTimerValueBus();
    PreferencesKeys providePreferencesKeys();
    DataManager provideDataManager();
}
