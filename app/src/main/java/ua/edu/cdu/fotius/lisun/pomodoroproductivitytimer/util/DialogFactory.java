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
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import static butterknife.ButterKnife.findById;

import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;

public class DialogFactory {

    public static final int NUMBER_PICKER_DEFAULT_MAX = 99;
    public static final int NUMBER_PICKER_DEFAULT_MIN = 1;

    public static Dialog createNumberPickerDialog(Context context, String title) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogContent = inflater.inflate(R.layout.dialog_number_picker, null);
        NumberPicker numberPicker = findById(dialogContent, R.id.number_picker);
        numberPicker.setMaxValue(NUMBER_PICKER_DEFAULT_MAX);
        numberPicker.setMinValue(NUMBER_PICKER_DEFAULT_MIN);
        numberPicker.setWrapSelectorWheel(false);
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(dialogContent)
                .setPositiveButton(context.getString(R.string.dialog_positive_button),
                        (dialog, which) -> Timber.i("Positive click"))
                .setNegativeButton(context.getString(R.string.dialog_negative_button),
                        (dialog, which) -> Timber.i("Negative click"))
                .create();
    };
}
