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
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;

public class NameDialogFragment extends DialogFragment {

    public static void showDialog(FragmentManager fragmentManager, String title) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        NameDialogFragment fragment = new NameDialogFragment();
        fragment.setArguments(args);
        fragment.show(fragmentManager, NameDialogFragment.class.getName());
    }

    private static final String KEY_TITLE = "title";

    @Inject
    RxBus mRxBus;

    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PomodoroProductivityTimerApplication
                .get(mContext).getApplicationComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        EditText editText = new EditText(mContext);
        return new AlertDialog.Builder(mContext)
                .setTitle(getArguments().getString(KEY_TITLE))
                .setView(createWrapperLayout(editText))
                .setPositiveButton(mContext.getString(R.string.dialog_positive_button), (dialog, which) ->
                        sendResultAndClose(new Result(editText.getText().toString())))
                .setNegativeButton(mContext.getString(R.string.dialog_negative_button),
                        (dialog, which) -> NameDialogFragment.this.dismiss())
                .create();

    }

    private void sendResultAndClose(Result result) {
        mRxBus.send(result);
        dismiss();
    }

    protected FrameLayout createWrapperLayout(EditText editText) {
        FrameLayout.LayoutParams params = new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.addView(editText, params);
        return frameLayout;
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
