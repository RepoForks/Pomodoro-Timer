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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.projects;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseDialogFragment;

public class DeleteDialogFragment extends BaseDialogFragment {

    public static void show(FragmentManager fragmentManager, long id, int adapterPosition) {
        Bundle args = new Bundle();
        args.putLong(KEY_PROJECT_ID, id);
        args.putInt(KEY_ADAPTER_POSITION, adapterPosition);
        DeleteDialogFragment fragment = new DeleteDialogFragment();
        fragment.setArguments(args);
        fragment.show(fragmentManager, DeleteDialogFragment.class.getName());
    }

    private static String KEY_PROJECT_ID = "project_id";
    private static String KEY_ADAPTER_POSITION = "adapter_position";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.delete_dialog_title))
                .setMessage(getContext().getString(R.string.delete_dialog_message))
                .setPositiveButton(getContext().getString(R.string.delete_dialog_positive), (dialog, which) -> {
                    getRxBus().send(new Result(getArguments().getLong(KEY_PROJECT_ID),
                            getArguments().getInt(KEY_ADAPTER_POSITION)));
                    DeleteDialogFragment.this.dismiss();
                })
                .setNegativeButton(getContext().getString(R.string.delete_dialog_negative),
                        (dialog, which) -> DeleteDialogFragment.this.dismiss())
                .create();
    }

    public class Result {
        private long mId;
        private int mAdapterPosition;

        public Result(long id, int adapterPosition) {
            mId = id;
            mAdapterPosition = adapterPosition;
        }

        public long getId() {
            return mId;
        }

        public int getAdapterPosition() {
            return mAdapterPosition;
        }
    }
}
