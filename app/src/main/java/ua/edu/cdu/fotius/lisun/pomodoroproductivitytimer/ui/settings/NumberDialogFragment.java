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
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import javax.inject.Inject;

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.RxBus;

public class NumberDialogFragment extends DialogFragment{

    public static void show(FragmentManager fragmentManager, String title, long initValue, String pref) {
        Bundle args = new Bundle();
        args.putLong(KEY_INIT_VALUE, initValue);
        args.putString(KEY_TITLE, title);
        args.putString(KEY_PREF, pref);
        NumberDialogFragment fragment = new NumberDialogFragment();
        fragment.setArguments(args);
        fragment.show(fragmentManager, NumberDialogFragment.class.getName());
    }

    private static final String KEY_INIT_VALUE = "init_value";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PREF = "pref";
    private static final int NUMBER_PICKER_DEFAULT_MAX = 99;
    private static final int NUMBER_PICKER_DEFAULT_MIN = 2;

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
        NumberPicker numberPicker = createNumberPicker();
        return new AlertDialog.Builder(mContext)
                .setTitle(getArguments().getString(KEY_TITLE))
                .setView(createWrapperLayout(numberPicker))
                .setPositiveButton(mContext.getString(R.string.dialog_positive_button), (dialog, which) -> {
                    mRxBus.send(new Result(numberPicker.getValue(), getArguments().getString(KEY_PREF)));
                    NumberDialogFragment.this.dismiss();
                })
                .setNegativeButton(mContext.getString(R.string.dialog_negative_button),
                        (dialog, which) -> NumberDialogFragment.this.dismiss())
                .create();

    }

    private NumberPicker createNumberPicker() {
        NumberPicker numberPicker = new NumberPicker(mContext);
        numberPicker.setMaxValue(NUMBER_PICKER_DEFAULT_MAX);
        numberPicker.setMinValue(NUMBER_PICKER_DEFAULT_MIN);
        numberPicker.setWrapSelectorWheel(false);
        return numberPicker;
    }

    private FrameLayout createWrapperLayout(NumberPicker numberPicker) {
        FrameLayout.LayoutParams params = new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.addView(numberPicker, params);
        return frameLayout;
    }

    public class Result {
        private int mNewNumber;
        private String mPref;

        public Result(int newNumber, String pref) {
            mNewNumber = newNumber;
            mPref = pref;
        }

        public int getNewNumber() {
            return mNewNumber;
        }

        public String getPref() {
            return mPref;
        }
    }
}
