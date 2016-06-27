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

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Backup;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.components.DaggerBackupActivityComponent;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.injection.modules.BackupActivityModule;

import static butterknife.ButterKnife.findById;

public class BackupActivity extends AppCompatActivity implements BackupView, BackupAdapter.MenuItemClickListener{

    @Inject
    BackupPresenter mPresenter;
    @Inject
    BackupAdapter mAdapter;
    @Bind(R.id.backup_root_layout)
    CoordinatorLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);

        Toolbar toolbar = findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        ButterKnife.bind(this);

        DaggerBackupActivityComponent.builder()
                .backupActivityModule(new BackupActivityModule(this))
                .applicationComponent(PomodoroProductivityTimerApplication.get(this).getApplicationComponent())
                .build().inject(this);


        RecyclerView recyclerView = findById(this, R.id.rv_backups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mPresenter.attach(this);
        mPresenter.loadBackups(getString(R.string.backup_error_loading));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    @OnClick(R.id.btn_create_backup)
    public void createBackupClicked() {
        mPresenter.createBackup(getFilesDir(), getString(R.string.backup_error_creating));
    }

    @Override
    public void showError(String errorMessage) {
        showSnack(errorMessage);
    }

    @Override
    public void showNoBackups() {
        showSnack(getString(R.string.backup_no_backups));
    }

    @Override
    public void showBackups(List<Backup> backups) {
        mAdapter.setBackups(backups);
    }

    @Override
    public void showCreatedBackup(Backup backup) {
        mAdapter.addBackup(backup);
        showSnack(getString(R.string.backup_new_created));
    }

    @Override
    public void showBackupRestored(Backup backup) {
        showSnack(getString(R.string.backup_restored));
    }

    private void showSnack(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRestoreClicked(Backup backup) {
        Timber.i("BackupActivity#onRestoreClicked.");
        mPresenter.restoreBackup(backup, getString(R.string.backup_error_restoring));
    }
}
