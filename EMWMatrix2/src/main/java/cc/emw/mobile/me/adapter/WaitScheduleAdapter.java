package cc.emw.mobile.me.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarEditActivity;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;

public class WaitScheduleAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CalendarInfo> mDataList;
    private boolean flag;

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
                    R.layout.listitem_wait_handle, null);
            vh.iconIv = (ImageView) convertView
                    .findViewById(R.id.iv_waithandle_tag);
            vh.nameTv = (TextView) convertView
                    .findViewById(R.id.tv_waithandle_content);
            vh.finishBtn = (Button) convertView
                    .findViewById(R.id.btn_waithandle_finish);
            vh.sort = (TextView) convertView
                    .findViewById(R.id.tv_waithandle_sort);
            vh.time = (TextView) convertView
                    .findViewById(R.id.tv_waithandle_time);
            vh.tv_state = (TextView) convertView
                    .findViewById(R.id.tv_waithandle_contents);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final CalendarInfo waitInfo = mDataList.get(position);
        String times = "";
        String startTime = waitInfo.StartTime;
        String endTime = waitInfo.OverTime;
        String duringTime = getTwoDay(endTime, startTime);
        if (startTime.length() > 10) {
            times = startTime.substring(5, 7) + "月"
                    + startTime.substring(8, 10) + "日";
        }
        Drawable left = mContext.getResources().getDrawable(HelpUtil.getScheduleResId(waitInfo.Color));
        left.setBounds(0, 0, DisplayUtil.dip2px(mContext, 10), DisplayUtil.dip2px(mContext, 10));
        vh.nameTv.setCompoundDrawables(left, null, null, null);
        vh.nameTv.setText(waitInfo.Title);
        vh.sort.setText(duringTime);
        Drawable imgLeft = mContext.getResources().getDrawable(R.drawable.iconfont_time);
        imgLeft.setBounds(0, 0, DisplayUtil.dip2px(mContext, 10), DisplayUtil.dip2px(mContext, 10));
        vh.sort.setCompoundDrawables(imgLeft, null, null, null);
        vh.sort.setCompoundDrawablePadding(DisplayUtil.dip2px(mContext, 9));
        if (waitInfo.Allday == 1) {
            vh.sort.setText("全天");
        }
        vh.time.setVisibility(View.VISIBLE);
        vh.time.setText(times);
        vh.tv_state.setVisibility(View.GONE);
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CalendarEditActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("calendarInfo", waitInfo);
                intent.putExtras(args);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView iconIv;
        TextView nameTv;
        Button finishBtn;
        TextView sort;
        TextView time;
        TextView tv_state;
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
}
