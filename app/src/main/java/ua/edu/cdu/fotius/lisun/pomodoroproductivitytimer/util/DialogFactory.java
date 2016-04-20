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

import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.PreferencePair;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.data.model.Project;

public class DialogFactory {

//    public static Dialog createUpdateProjectDialog(Context context, String title,
//                                                   DialogEventBus eventBus, Project project) {
//        FrameLayout.LayoutParams params = new FrameLayout
//                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//        FrameLayout frameLayout = new FrameLayout(context);
//        EditText editText = new EditText(context);
//        frameLayout.addView(editText, params);
//
//        return new AlertDialog.Builder(context)
//                .setTitle(title)
//                .setView(frameLayout)
//                .setPositiveButton(context.getString(R.string.dialog_positive_button),
//                        (dialog, which) -> {
//                            project.setName(editText.getText().toString());
//                            eventBus.send(project);
//                        })
//                .setNegativeButton(context.getString(R.string.dialog_negative_button),
//                        (dialog, which) -> dialog.dismiss())
//                .create();
//    }

}
