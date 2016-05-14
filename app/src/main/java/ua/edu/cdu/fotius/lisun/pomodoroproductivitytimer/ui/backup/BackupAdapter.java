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

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Backup;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.statistics.StatisticsAdapter;

import static butterknife.ButterKnife.findById;

public class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.ViewHolder>{

    public interface MenuItemClickListener {
        void onRestoreClicked(Backup backup);
        void onToDriveClicked(Backup backup);
    }

    private List<Backup> mBackups;
    private MenuItemClickListener mMenuItemClickListener;

    @Inject
    public BackupAdapter(MenuItemClickListener menuItemClickListener) {
        mBackups = new ArrayList<>();
        mMenuItemClickListener = menuItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.backup_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mBackupNameView.setText(mBackups.get(position).getBackupName());
    }

    @Override
    public int getItemCount() {
        return mBackups.size();
    }

    public void setBackups(List<Backup> backups) {
        mBackups = backups;
        notifyDataSetChanged();
    }

    public void addBackup(Backup backup) {
        mBackups.add(backup);
        notifyItemInserted(mBackups.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener{

        @Bind(R.id.tv_backup_name)
        TextView mBackupNameView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.ib_menu)
        public void showItemMenu(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.menu_backup_overflow);
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getLayoutPosition();
            Backup backup = mBackups.get(position);
            if(item.getItemId() == R.id.mbo_restore) {
                mMenuItemClickListener.onRestoreClicked(backup);
            } else if(item.getItemId() == R.id.mbo_to_drive) {
                mMenuItemClickListener.onToDriveClicked(backup);
            }
            return true;
        }
    }
}
