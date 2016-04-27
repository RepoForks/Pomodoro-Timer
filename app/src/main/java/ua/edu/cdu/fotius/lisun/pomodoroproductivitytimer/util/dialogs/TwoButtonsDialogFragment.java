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

import javax.inject.Inject;

import rx.Observable;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.PomodoroProductivityTimerApplication;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util.RxBus;

public class TwoButtonsDialogFragment extends DialogFragment{

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_POS_BUTTON = "positive_button";
    private static final String KEY_NEG_BUTTON = "negative_button";

    public static Observable<Result> show(FragmentManager fragmentManager, RxBus rxBus,
                                          String title, String message, String positiveButton,
                                          String negativeButton) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_POS_BUTTON, positiveButton);
        args.putString(KEY_NEG_BUTTON, negativeButton);
        TwoButtonsDialogFragment fragment = new TwoButtonsDialogFragment();
        fragment.setArguments(args);
        fragment.show(fragmentManager, TwoButtonsDialogFragment.class.getName());
        return rxBus.getObservable(Result.class);
    }

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
        return new AlertDialog.Builder(mContext)
                .setTitle(getArguments().getString(KEY_TITLE))
                .setMessage(getArguments().getString(KEY_MESSAGE))
                .setPositiveButton(getArguments().getString(KEY_POS_BUTTON), (dialog, which) -> {
                    mRxBus.send(new Result(Result.OK));
                    TwoButtonsDialogFragment.this.dismiss();
                })
                .setNegativeButton(getArguments().getString(KEY_NEG_BUTTON),
                        (dialog, which) -> {
                            mRxBus.send(new Result(Result.CANCEL));
                            TwoButtonsDialogFragment.this.dismiss();
                        })
                .create();
    }

    public class Result {
        public static final int OK = 1;
        public static final int CANCEL = 2;

        private int mResultCode;

        public Result(int resultCode) {
            mResultCode = resultCode;
        }

        public int getResultCode() {
            return mResultCode;
        }
    }
}
