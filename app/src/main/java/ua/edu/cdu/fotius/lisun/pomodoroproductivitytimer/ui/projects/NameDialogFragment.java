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
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base.BaseDialogFragment;

public class NameDialogFragment extends BaseDialogFragment {

    public static void showDialog(FragmentManager fragmentManager, String title) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        NameDialogFragment fragment = new NameDialogFragment();
        fragment.setArguments(args);
        fragment.show(fragmentManager, NameDialogFragment.class.getName());
    }

    private static final String KEY_TITLE = "title";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        EditText editText = (EditText) LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_edit_text, null);
        return new AlertDialog.Builder(getContext())
                .setTitle(getArguments().getString(KEY_TITLE))
                .setView(editText)
                .setPositiveButton(getString(R.string.dialog_positive_button), (dialog, which) ->
                        sendResultAndClose(new Result(editText.getText().toString())))
                .setNegativeButton(getString(R.string.dialog_negative_button),
                        (dialog, which) -> NameDialogFragment.this.dismiss())
                .create();

    }

    private void sendResultAndClose(Result result) {
        getRxBus().send(result);
        dismiss();
    }

    public class Result {
        private String mName;

        public Result(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }
}
