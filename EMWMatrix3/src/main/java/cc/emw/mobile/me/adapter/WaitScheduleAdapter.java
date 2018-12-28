package cc.emw.mobile.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.util.StringUtils;

public class WaitScheduleAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CalendarInfo> mDataList;
    private boolean flag;
    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public WaitScheduleAdapter(Context context, boolean flag) {
        this.mContext = context;
    }

    public void setData(ArrayList<CalendarInfo> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_item_relation, null);
            vh.img = (ImageView) convertView
                    .findViewById(R.id.iv_wait_handle_tag);
            vh.content = (TextView) convertView
                    .findViewById(R.id.tv_wait_handle_content);
//            vh.tag = (TextView) convertView
//                    .findViewById(R.id.tv_waithandle_tag);
            vh.time = (TextView) convertView
                    .findViewById(R.id.tv_wait_handle_time);
//            vh.waithandleTag = (ImageView) convertView.findViewById(R.id.img_waithandle_tag);
            AnimationSet set = anmi();
            vh.set = set;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final CalendarInfo waitInfo = mDataList.get(position);
        String startTime = waitInfo.StartTime;

//        String endTime = waitInfo.OverTime;
//        Calendar sCal = Calendar.getInstance();
//        Calendar eCal = Calendar.getInstance();
//        Calendar sWcal = Calendar.getInstance();
//        Calendar eWcal = Calendar.getInstance();
//        String nowMonth = "";
//        final int month = sCal.get(Calendar.MONTH) + 1;
//        if (month < 10) {
//            nowMonth = "0" + month;
//        } else {
//            nowMonth = month + "";
//        }
//        String nowTime = sCal.get(Calendar.YEAR) + "-" + nowMonth + "-" + sCal.get(Calendar.DAY_OF_MONTH) + " " + "00:00:00";
//        String nowEndTime = sCal.get(Calendar.YEAR) + "-" + nowMonth + "-" + sCal.get(Calendar.DAY_OF_MONTH) + " " + "23:59:59";
//        try {
//            sWcal.setTimeInMillis(time.parse(startTime).getTime());
//            eWcal.setTimeInMillis(time.parse(endTime).getTime());
//            sCal.setTimeInMillis(time.parse(nowTime).getTime());
//            eCal.setTimeInMillis(time.parse(nowEndTime).getTime());
//            if (sWcal.getTimeInMillis() <= eCal.getTimeInMillis() && eWcal.getTimeInMillis() >= sCal.getTimeInMillis()) {
//                vh.waithandleTag.setImageResource(R.drawable.dynamic_circletime);
//            } else {
//                vh.waithandleTag.setImageResource(R.drawable.circle_calendar_other_time_tags);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        String times = StringUtils.friendly_time(startTime);
//        String duringTime = getTwoDay(endTime, startTime);
//        if (startTime.length() > 10) {
//            times = startTime.substring(5, 7) + "月"
//                    + startTime.substring(8, 10) + "日";
//        }
//        vh.nameTv.setCompoundDrawables(left, null, null, null);
//        imgLeft.setBounds(0, 0, DisplayUtil.dip2px(mContext, 10), DisplayUtil.dip2px(mContext, 10));
//        vh.content.setCompoundDrawables(imgLeft, null, null, null);
//        vh.content.setCompoundDrawablePadding(DisplayUtil.dip2px(mContext, 8));
        vh.img.setImageResource(R.drawable.daiban_richen);
//        vh.content.setText(duringTime);
        vh.content.setText(waitInfo.Title);
        vh.time.setText(times);
//        if (waitInfo.Allday == 1) {
//            vh.time.setText("全天");
//        }
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CalendarInfoActivity2.class);
                intent.putExtra("calendarInfo", waitInfo);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationInWindow(location);
                intent.putExtra("click_pos_y", location[1]);
                mContext.startActivity(intent);
            }
        });
        convertView.startAnimation(vh.set);
        return convertView;
    }

    static class ViewHolder {
        ImageView img;
        TextView content;
        //        TextView tag;
        TextView time;
        //        ImageView waithandleTag;
        AnimationSet set;
    }

    /**
     * 得到二个日期间的间隔的时间
     *
     * @param sj1
     * @param sj2
     * @return
     */
    public String getTwoDay(String sj1, String sj2) {
        String res = "";
        int yearTime = 12 * 30 * 24 * 60;
        int monthTime = 24 * 60 * 30;
        int dayTime = 24 * 60;
        int hourTime = 60;
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long time = 0;
        try {
            java.util.Date date = myFormatter.parse(sj1);
            java.util.Date mydate = myFormatter.parse(sj2);
            time = (date.getTime() - mydate.getTime()) / (60 * 1000);
            int year = (int) time / yearTime;
            int month = (int) time / monthTime;
            int day = (int) time / dayTime;
            int hour = (int) (time / hourTime);
            if (year != 0) {
                if (time % yearTime == 0) {
                    res = year + "年";
                } else {
                    time = time - year * yearTime;
                    month = (int) (time / monthTime);
                    if (time % monthTime == 0) {
                        res = year + "年" + month + "月";
                    } else {
                        time = time - month * monthTime;
                        day = (int) (time / dayTime);
                        if (time % dayTime == 0) {
                            res = year + "年" + month + "月" + day + "天";
                            if (month == 0) {
                                res = year + "年" + day + "天";
                            }
                        } else {
                            time = time - day * dayTime;
                            hour = (int) (time / hourTime);
                            if (time % hourTime == 0) {
                                res = year + "年" + month + "月" + day + "天" + hour + "小时";
                                if (month == 0 && day == 0) {
                                    res = year + "年" + hour + "小时";
                                } else if (month == 0) {
                                    res = year + "年" + day + "天" + hour + "小时";
                                } else if (day == 0) {
                                    res = year + "年" + month + "月" + hour + "小时";
                                }
                            } else {
                                time = time - hour * hourTime;
                                res = year + "年" + month + "月" + day + "天" + hour + "小时" + time + "分钟";
                                if (month == 0 && day == 0 && hour == 0) {
                                    res = year + "年" + time + "分钟";
                                } else if (month == 0 && hour == 0) {
                                    res = year + "年" + day + "天" + time + "分钟";
                                } else if (month == 0 && day == 0) {
                                    res = year + "年" + hour + "小时" + time + "分钟";
                                } else if (hour == 0 && day == 0) {
                                    res = year + "年" + month + "月" + time + "分钟";
                                } else if (month == 0) {
                                    res = year + "年" + day + "天" + hour + "小时" + time + "分钟";
                                } else if (day == 0) {
                                    res = year + "年" + month + "月" + hour + "小时" + time + "分钟";
                                } else if (hour == 0) {
                                    res = year + "年" + month + "月" + day + "天" + time + "分钟";
                                }
                            }
                        }
                    }
                }
            } else if (month != 0) {
                if (time % monthTime == 0) {
                    res = month + "月";
                } else {
                    time = time - month * monthTime;
                    day = (int) (time / dayTime);
                    if (time % dayTime == 0) {
                        res = month + "月" + day + "天";
                    } else {
                        time = time - day * dayTime;
                        hour = (int) (time / hourTime);
                        if (time % hourTime == 0) {
                            res = month + "月" + day + "天" + hour + "小时";
                            if (day == 0) {
                                res = month + "月" + hour + "小时";
                            }
                        } else {
                            time = time - hour * hourTime;
                            res = month + "月" + day + "天" + hour + "小时" + time + "分钟";
                            if (day == 0 && hour == 0) {
                                res = month + "月" + time + "分钟";
                            } else if (hour == 0) {
                                res = month + "月" + day + "天" + time + "分钟";
                            } else if (day == 0) {
                                res = month + "月" + hour + "小时" + time + "分钟";
                            }
                        }
                    }
                }
            } else if (day != 0) {
                if (time % dayTime == 0) {
                    res = day + "天";
                } else {
                    time = time - day * dayTime;
                    hour = (int) (time / hourTime);
                    if (time % hourTime == 0) {
                        res = day + "天" + hour + "小时";
                    } else {
                        time = time - hour * hourTime;
                        res = day + "天" + hour + "小时" + time + "分钟";
                        if (hour == 0) {
                            res = day + "天" + time + "分钟";
                        }
                    }
                }
            } else if (hour != 0) {
                if (time % hourTime == 0) {
                    res = hour + "小时";
                } else {
                    time = time - hour * hourTime;
                    res = hour + "小时" + time + "分钟";
                }
            } else {
                res = time + "分钟";
            }

        } catch (Exception e) {
            return "";
        }
        return res;
    }

    private AnimationSet anmi() {
        // 动画集合
        AnimationSet set = new AnimationSet(false);
        // 缩放动画
        ScaleAnimation scale = new ScaleAnimation(0.9f, 1, 0.9f, 1,
                Animation.RELATIVE_TO_SELF, 0.9f, Animation.RELATIVE_TO_SELF,
                0.9f);
        scale.setDuration(800);// 动画时间
        scale.setFillAfter(true);// 保持动画状态
        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0.6f, 1);
        alpha.setDuration(800);// 动画时间
        alpha.setFillAfter(true);// 保持动画状态
        set.addAnimation(scale);
        set.addAnimation(alpha);
        return set;
    }
}
