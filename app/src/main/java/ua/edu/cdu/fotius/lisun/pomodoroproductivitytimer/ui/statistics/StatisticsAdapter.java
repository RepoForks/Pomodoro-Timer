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

package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.statistics;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.R;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.ProjectStatistics;
import ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.helpers.TimeUtil;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder>{

    private List<ProjectStatistics> mStatistics;
    private Context mContext;

    @Inject
    public StatisticsAdapter(Context context) {
        mStatistics = new ArrayList<>();
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.statistics_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProjectStatistics stat = mStatistics.get(position);
        String name = stat.getProjectName();
        if(name == null) {
            name = mContext.getString(R.string.unknown_project_name);
        }
        holder.mProjectNameTv.setText(name);
        long millis = stat.getWorkedInMillis();
        long hours = TimeUtil.hours(millis);
        long minutes = TimeUtil.minutes(hours, millis);
        holder.mWorkedTimeTv.setText(mContext.getString(R.string.statistics_time,  hours, minutes));
        holder.mWorkedPercentageTv.setText(mContext.getString(R.string.percentage, stat.getWorkPercentage()));
    }

    @Override
    public int getItemCount() {
        return mStatistics.size();
    }

    public void setStatistics(List<ProjectStatistics> statistics) {
        mStatistics = statistics;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_project_name)
        public TextView mProjectNameTv;
        @Bind(R.id.tv_worked_time)
        public TextView mWorkedTimeTv;
        @Bind(R.id.tv_worked_percentage)
        public TextView mWorkedPercentageTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
