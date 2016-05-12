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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.backup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.local.DbHelper;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerBackupActivityComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerStatisticsActivityComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.modules.StatisticsActivityModule;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.statistics.StatisticsPresenter;

public class BackupActivity extends AppCompatActivity {

    @Inject
    BackupPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        DaggerBackupActivityComponent.builder()
                .applicationComponent(PomodoroProductivityTimerApplication.get(this).getApplicationComponent())
                .build().inject(this);

        //TODO: DEBUG!!!
        mPresenter.saveDbSnapshot();
    }
}
