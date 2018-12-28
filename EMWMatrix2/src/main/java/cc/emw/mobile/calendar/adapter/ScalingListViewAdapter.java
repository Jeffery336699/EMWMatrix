package cc.emw.mobile.calendar.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarEditActivity;
import cc.emw.mobile.entity.CalendarInfo;

public class ScalingListViewAdapter extends BaseAdapter {

    private List<CalendarInfo> infos;
    private Context mActivity;

    public ScalingListViewAdapter(List<CalendarInfo> infos, Context mActivity) {
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
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeWeeks = new SimpleDateFormat("yyyy-MM-dd");
        Calendar sCal; // 日程开始的时间
        Calendar eCal; // 日程结束的时间
        Calendar toDayFirst; // 今天开始的时间
        Calendar toDayLast; // 今天结束的时间
        Calendar benYueEnd; // 本月结束的时间
        Calendar benYueFirst; // 本月开始的时间 (测试)
        int todayCals = 0;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_calendar_day_info, null);
            holder.tvTime = (TextView) convertView
                    .findViewById(R.id.tv_time_tag_to_day);
            holder.tvTimeWeek = (TextView) convertView
                    .findViewById(R.id.tv_time_tag_to_week);
            holder.tvCalendarInfo = (TextView) convertView
                    .findViewById(R.id.tv_calendar_scan_info);

            // 动画集合
            AnimationSet set = new AnimationSet(false);
            // 缩放动画
            ScaleAnimation scale = new ScaleAnimation(0.7f, 1, 0.7f, 1,
                    Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF,
                    0.7f);
            scale.setDuration(800);// 动画时间
            scale.setFillAfter(true);// 保持动画状态
            // 渐变动画
            AlphaAnimation alpha = new AlphaAnimation(0.6f, 1);
            alpha.setDuration(1000);// 动画时间
            alpha.setFillAfter(true);// 保持动画状态
            set.addAnimation(scale);
            set.addAnimation(alpha);
            holder.set = set;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CalendarInfo calendarInfo = infos.get(position);
        String startTime = calendarInfo.StartTime;
        String endTime = calendarInfo.OverTime;
        String week = "";
        try {
            Calendar nowCal = Calendar.getInstance();// 系统当前的Cal
            sCal = Calendar.getInstance();
            eCal = Calendar.getInstance();
            toDayFirst = Calendar.getInstance();
            toDayLast = Calendar.getInstance();
            Date dayStart = time.parse(startTime);
            Date dayEnd = time.parse(endTime);
            int nowYear = nowCal.get(Calendar.YEAR);// 当前年份
            int nowMonth = nowCal.get(Calendar.MONTH) + 1;// 当前的月份
            int nowDay = toDayFirst.get(Calendar.DAY_OF_MONTH);// 今天是多少号
            String nowStr = nowYear + "-" + nowMonth + "-" + nowDay + " "
                    + "00:00:00";// 今天开始的时间
            String nowEndStr = nowYear + "-" + nowMonth + "-" + nowDay
                    + " " + "23:59:59";// 今天开始的时间
            sCal.setTimeInMillis(dayStart.getTime());
            eCal.setTimeInMillis(dayEnd.getTime());
            toDayFirst.setTimeInMillis(time.parse(nowStr).getTime());
            toDayLast.setTimeInMillis(time.parse(nowEndStr).getTime());

            String eventTimeStr = nowYear + "-" + nowMonth + "-"
                    + sCal.get(Calendar.DAY_OF_MONTH);
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTimeInMillis(timeWeeks.parse(eventTimeStr)
                    .getTime());
            week = getWeek(eventCal); // 时间开始的星期
            holder.tvTimeWeek.setText(week);
            if ((sCal.get(Calendar.MONTH) + 1) != nowMonth) {
                holder.tvTimeWeek
                        .setText((sCal.get(Calendar.MONTH) + 1) + "月");
            }
            holder.tvTime.setText(sCal.get(Calendar.DAY_OF_MONTH) + "");
            holder.tvCalendarInfo.setText(calendarInfo.Title);
            // 判断是否是今天的事件
            if (sCal.getTimeInMillis() <= toDayLast.getTimeInMillis()
                    && eCal.getTimeInMillis() >= toDayFirst
                    .getTimeInMillis()) {
                todayCals++;
                holder.tvCalendarInfo.setTextColor(mActivity.getResources().getColor(
                        R.color.white));
                int colorId = calendarInfo.Color;
                switch (colorId) {
                    case 0:
                        holder.tvCalendarInfo
                                .setBackgroundResource(R.drawable.round_cal_bg_0);
                        break;
                    case 1:
                        holder.tvCalendarInfo
                                .setBackgroundResource(R.drawable.round_cal_bg_1);
                        break;
                    case 2:
                        holder.tvCalendarInfo
                                .setBackgroundResource(R.drawable.round_cal_bg_2);
                        break;
                    case 3:
                        holder.tvCalendarInfo
                                .setBackgroundResource(R.drawable.round_cal_bg_3);
                        break;
                    case 4:
                        holder.tvCalendarInfo
                                .setBackgroundResource(R.drawable.round_cal_bg_4);
                        break;
                    case 5:
                        holder.tvCalendarInfo
                                .setBackgroundResource(R.drawable.round_cal_bg_5);
                        break;
                }
            } else {
                holder.tvCalendarInfo.setBackgroundResource(R.drawable.round_cal_bg_normol);
            }
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(mActivity,
                            CalendarEditActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("calendarInfo", calendarInfo);
                    intent.putExtras(args);
                    mActivity.startActivity(intent);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
        convertView.startAnimation(holder.set);
        return convertView;
    }

    /**
     * 根据一个字符串判短是星期几
     *
     * @param cal
     * @return
     */
    private String getWeek(Calendar cal) {
        String week = "";
        int str = cal.get(Calendar.DAY_OF_WEEK);
        switch (str) {
            case 1:
                week = "周日";
                break;
            case 2:
                week = "周一";
                break;
            case 3:
                week = "周二";
                break;
            case 4:
                week = "周三";
                break;
            case 5:
                week = "周四";
                break;
            case 6:
                week = "周五";
                break;
            case 7:
                week = "周六";
                break;
        }
        return week;
    }

    private static class ViewHolder {
        AnimationSet set;
        TextView tvTime;
        TextView tvTimeWeek;
        TextView tvCalendarInfo;
    }
}
