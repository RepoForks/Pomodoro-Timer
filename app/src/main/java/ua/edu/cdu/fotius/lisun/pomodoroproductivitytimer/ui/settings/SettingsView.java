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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.settings;

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.Preferences;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.MvpView;

public interface SettingsView extends MvpView {
    void setPreferencesSummary(Preferences preferences);
    void setPreferenceSummary(String key, String value);
    void showError(String error);
}
