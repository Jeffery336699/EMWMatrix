package cc.emw.mobile.calendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarDayActivitys;
import cc.emw.mobile.calendar.CalendarEditActivity;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.net.ApiEntity;


public class ScalingListViewMonthAdapter extends BaseAdapter {

    private List<CalendarInfo> infos;
    private Context mActivity;
    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ScalingListViewMonthAdapter(List<CalendarInfo> infos, Context mActivity) {
        this.infos = infos;
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        return this.infos.size();
    }

    @Override
    public Object getItem(int position) {
        return this.infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_calendar_scan_info, null);
            holder.tvCalendar = (TextView) convertView
                    .findViewById(R.id.tv_calendar_scan_info);

//            // 动画集合
//            AnimationSet set = new AnimationSet(false);
//            // 缩放动画
//            ScaleAnimation scale = new ScaleAnimation(0.8f, 1, 0.8f, 1,
//                    Animation.RELATIVE_TO_SELF, 0.8f, Animation.RELATIVE_TO_SELF,
//                    0.8f);
//            scale.setDuration(400);// 动画时间
//            scale.setFillAfter(true);// 保持动画状态
//            // 渐变动画
//            AlphaAnimation alpha = new AlphaAnimation(0.6f, 1);
//            alpha.setDuration(800);// 动画时间
//            alpha.setFillAfter(true);// 保持动画状态
//            set.addAnimation(scale);
//            set.addAnimation(alpha);
//            holder.set = set;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final CalendarInfo calInfo = infos.get(position);

        String hours = "";
        String mins = "";
        Calendar sCal = Calendar.getInstance();
        try {
            sCal.setTimeInMillis(time.parse(calInfo.StartTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int hour = sCal.get(Calendar.HOUR_OF_DAY);
        int min = sCal.get(Calendar.MINUTE);
        if (hour < 10) {
            hours = "0" + hour;
        } else {
            hours = hour + "";
        }
        if (min < 10) {
            mins = "0" + min;
        } else {
            mins = min + "";
        }
        String taskStartTime = hours + ":" + mins;

        holder.tvCalendar.setText(taskStartTime + " " + calInfo.Title);
        int colorId = calInfo.Color;
        switch (colorId) {
            case 0:
                holder.tvCalendar
                        .setBackgroundResource(R.drawable.round_cal_bg_0);
                break;
            case 1:
                holder.tvCalendar
                        .setBackgroundResource(R.drawable.round_cal_bg_1);
                break;
            case 2:
                holder.tvCalendar
                        .setBackgroundResource(R.drawable.round_cal_bg_2);
                break;
            case 3:
                holder.tvCalendar
                        .setBackgroundResource(R.drawable.round_cal_bg_3);
                break;
            case 4:
                holder.tvCalendar
                        .setBackgroundResource(R.drawable.round_cal_bg_4);
                break;
            case 5:
                holder.tvCalendar
                        .setBackgroundResource(R.drawable.round_cal_bg_5);
                break;
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mActivity,
                        CalendarEditActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("calendarInfo", calInfo);
                intent.putExtras(args);
                mActivity.startActivity(intent);
            }
        });
//        convertView.startAnimation(holder.set);
        return convertView;
    }

    private static class ViewHolder {
        AnimationSet set;
        TextView tvCalendar;
    }
}
