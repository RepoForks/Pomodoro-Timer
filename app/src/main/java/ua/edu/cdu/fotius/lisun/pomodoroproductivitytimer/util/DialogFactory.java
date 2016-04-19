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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.util;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.PreferencePair;

public class DialogFactory {

    public static final int NUMBER_PICKER_DEFAULT_MAX = 99;
    public static final int NUMBER_PICKER_DEFAULT_MIN = 2;

    public static Dialog createNumberPickerDialog(Context context, String title,
                                                  DialogEventBus eventBus,
                                                  String preferenceKey) {
        NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMaxValue(NUMBER_PICKER_DEFAULT_MAX);
        numberPicker.setMinValue(NUMBER_PICKER_DEFAULT_MIN);
        numberPicker.setWrapSelectorWheel(false);
        FrameLayout.LayoutParams params = new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(numberPicker, params);

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(frameLayout)
                .setPositiveButton(context.getString(R.string.dialog_positive_button),
                        (dialog, which) -> eventBus.send(
                                new PreferencePair(preferenceKey,
                                        Integer.toString(numberPicker.getValue()))))
                .setNegativeButton(context.getString(R.string.dialog_negative_button),
                        (dialog, which) -> dialog.dismiss())
                .create();
    }

    public static Dialog createNewProjectDialog(Context context, String title,
                                                  DialogEventBus eventBus, String initValue) {
        FrameLayout.LayoutParams params = new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        FrameLayout frameLayout = new FrameLayout(context);
        EditText editText = new EditText(context);
        frameLayout.addView(editText, params);

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(frameLayout)
                .setPositiveButton(context.getString(R.string.dialog_positive_button),
                        (dialog, which) -> eventBus.send(editText.getText().toString()))
                .setNegativeButton(context.getString(R.string.dialog_negative_button),
                        (dialog, which) -> dialog.dismiss())
                .create();
    }

}
