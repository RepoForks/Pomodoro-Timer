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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.dialogs;

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
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxBus;

public class ProjectNameDialogFragment extends DialogFragment {

    public static Observable<Result> show(FragmentManager fragmentManager, RxBus rxBus,
                                          String title, String initValue) {
        Bundle args = new Bundle();
        args.putString(KEY_INIT_VALUE, initValue);
        args.putString(KEY_TITLE, title);
        ProjectNameDialogFragment fragment = new ProjectNameDialogFragment();
        fragment.setArguments(args);
        fragment.show(fragmentManager, ProjectNameDialogFragment.class.getName());
        return rxBus.getObservable(Result.class);
    }

    private static final String KEY_INIT_VALUE = "init_value";
    private static final String KEY_TITLE = "title";

    @Inject
    RxBus mRxBus;

    private Context mContext;

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
        String initText = getArguments().getString(KEY_INIT_VALUE, "");
        editText.setText(initText);
        editText.setSelection(initText.length());
        return new AlertDialog.Builder(mContext)
                .setTitle(getArguments().getString(KEY_TITLE))
                .setView(createWrapperLayout(editText))
                .setPositiveButton(mContext.getString(R.string.dialog_positive_button), (dialog, which) -> {
                    mRxBus.send(new Result(editText.getText().toString()));
                    ProjectNameDialogFragment.this.dismiss();
                })
                .setNegativeButton(mContext.getString(R.string.dialog_negative_button),
                        (dialog, which) -> {
                            ProjectNameDialogFragment.this.dismiss();
                        })
                .create();

    }

    private FrameLayout createWrapperLayout(EditText editText) {
        FrameLayout.LayoutParams params = new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.addView(editText, params);
        return frameLayout;
    }

    public class Result {
        private String mNewName;

        public Result(String newName) {
            mNewName = newName;
        }

        public String getNewName() {
            return mNewName;
        }
    }
}
