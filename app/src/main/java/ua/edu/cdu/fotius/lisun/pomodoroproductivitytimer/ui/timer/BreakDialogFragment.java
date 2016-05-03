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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.timer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import javax.inject.Inject;

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseDialogFragment;

public class BreakDialogFragment extends BaseDialogFragment {

    public static DialogFragment show(FragmentManager fragmentManager) {
        BreakDialogFragment fragment = new BreakDialogFragment();
        fragment.show(fragmentManager, BreakDialogFragment.class.getName());
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.break_dialog_title))
                .setMessage(getString(R.string.break_dialog_message))
                .setPositiveButton(getString(R.string.break_dialog_take), (dialog, which) -> {
                    getRxBus().send(new Result(Result.TAKE));
                    BreakDialogFragment.this.dismiss();
                })
                .setNegativeButton(getString(R.string.break_dialog_skip),
                        (dialog, which) -> BreakDialogFragment.this.dismiss())
                .create();
    }

    public class Result {
        public static final int TAKE = 1;
        public static final int SKIP = 2;

        private int mResultCode;

        public Result(int resultCode) {
            mResultCode = resultCode;
        }

        public int getResultCode() {
            return mResultCode;
        }
    }
}
