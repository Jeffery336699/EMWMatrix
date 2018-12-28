package cc.emw.mobile.calendar;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.decorators.TodayDecorator;
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.main.fragment.worker.CalendarFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;

/**
 * @author zrjt
 */
@ContentView(R.layout.activity_calendar_day)
public class CalendarDayActivitys extends BaseActivity implements WeekView.EventClickListener, WeekView.MonthChangeListener {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderSendBtn; // 顶部条类型
    @ViewInject(R.id.weekView)
    private WeekView mWeekView;
    @ViewInject(R.id.calendarView)
    private MaterialCalendarView widget;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = 1;

    private List<CalendarInfo> mDataList; // 代办列表数据
    private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();// 传递过来当天的事件
    private Calendar calendarInfo; // 传递过来选择的日程

    public static final String ACTION_REFRESH_DAY_VIEW = "cc.emw.mobile.refresh_day_view"; // 刷新的action
    private MyBroadcastReceive mReceive;
    private int clickTemp;
    private Calendar selectDate;
    private Dialog mDialog; // 加载框
    private Calendar mClickDate;    // 点击的天数

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        for (CalendarInfo uwh : mDataList) {
            if (uwh.ID == event.getId()) {
                Intent intent = new Intent(CalendarDayActivitys.this,
                        CalendarEditActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("calendarInfo", uwh);
                intent.putExtras(args);
                startActivity(intent);
            }
        }
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        return events;
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_DAY_VIEW.equals(action)) {
                getScheduleList();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
    }

    private void init() {

        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderSendBtn.setImageResource(R.drawable.nav_btn_share);
        mHeaderSendBtn.setVisibility(View.VISIBLE);
        mHeaderBackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        mHeaderSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(CalendarDayActivitys.this,
                        CalendarCreateActivitys.class);
                intent.putExtra(CalendarFragment.CREATEDATE, mClickDate);
                startActivity(intent);
            }
        });

        IntentFilter intentFilter = new IntentFilter(ACTION_REFRESH_DAY_VIEW);
        mReceive = new MyBroadcastReceive();
        this.registerReceiver(mReceive, intentFilter); // 注册监听
        mDialog = createLoadingDialog(getString(R.string.loading));

        mDataList = new ArrayList<CalendarInfo>();

        calendarInfo = (Calendar) getIntent().getSerializableExtra("clickDate");

        mClickDate = calendarInfo;

        // 初始当前的时间
        CalendarDay currentDate = widget.getCurrentDate();
        int year = currentDate.getYear();
        int month = currentDate.getMonth() + 1;

        mHeaderTitleTv.setText(year + "/" + month);

        widget.setTitleFormatter(new DateFormatTitleFormatter()); //设置顶部日期格式
        widget.setTileWidth(DisplayUtil.getDisplayWidth(this) / 7); //设置单元格宽度
        widget.addDecorator(new TodayDecorator(this)); //当前日期空心圆显示
        // 隐藏头部时间标识
        widget.setTopbarVisible(false);

        CalendarDay currentCalendarDay = CalendarDay.from(calendarInfo);

        widget.setCurrentDate(currentCalendarDay); //跳转到指定日期

        widget.setSelectedDate(currentCalendarDay);

        widget.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Calendar calendar = date.getCalendar();
                widget.setSelectedDate(calendar);
                mWeekView.goToDate(calendar);
                mClickDate = date.getCalendar();
            }
        });

        widget.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                Calendar calendar = date.getCalendar();
                int year = date.getYear();
                int month = date.getMonth() + 1;
                widget.setSelectedDate(calendar);
                mWeekView.goToDate(calendar);
                mClickDate = date.getCalendar();
                mHeaderTitleTv.setText(year + "/" + month);
            }
        });


        //weekView
        mWeekView.setOnEventClickListener(this);

        mWeekView.setMonthChangeListener(this);

        setupDateTimeInterpreter(false);

        mWeekView.goToDate(calendarInfo);

        mWeekViewType = TYPE_DAY_VIEW;
        mWeekView.setNumberOfVisibleDays(1);

        // Lets change some dimensions to best fit the view.
        mWeekView.setColumnGap((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                        .getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources()
                        .getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources()
                        .getDisplayMetrics()));

        mWeekView.setScrollListener(new WeekView.ScrollListener() {
            @Override
            public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
//                widget.setDateSelected(newFirstVisibleDay, true); 多选
                widget.setSelectedDate(newFirstVisibleDay);
                widget.setCurrentDate(newFirstVisibleDay);
            }

        });

        getScheduleList();

    }

    /**
     * Set up a date time interpreter which will show short date values when in
     * week view and long date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat(
                        "EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M-d", Locale
                        .getDefault());

                // All android api level do not have a standard way of getting
                // the first letter of
                // the week day name. Hence we get the first char
                // programmatically.
                // Details:
                // http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                // return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM"
                // : hour + " AM");
                return hour + ":00";
            }
        });
    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d",
                time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE),
                time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    private void getScheduleList() {
        API.TalkerAPI.GetCalenderListByTimeSpan("2016-01-01 00:00:00",
                "2030-01-01 23:59:59", 1, new RequestCallback<CalendarInfo>(
                        CalendarInfo.class) {

                    @Override
                    public void onCancelled(CancelledException arg0) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        mDialog.dismiss();
                        Toast.makeText(CalendarDayActivitys.this,
                                arg0.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinished() {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onParseSuccess(List<CalendarInfo> respList) {
                        mDataList.clear();
                        mDataList.addAll(respList);
                        events.clear();
                        // 用于标记有事项的天数
                        Collections.reverse(mDataList);
                        for (int i = 0, count = mDataList.size(); i < count; i++) {
                            CalendarInfo waitInfo = mDataList.get(i);
                            String start = waitInfo.StartTime;// 日程开始的时间
                            String end = waitInfo.OverTime;// 日程结束的时间
                            if (waitInfo.Allday == 1) {
                                end = end.substring(0, 11);
                                end = end + "23:59:59";
                            }
                            SimpleDateFormat time = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss");
                            Calendar startTime;
                            Calendar endTime;

                            WeekViewEvent event;
                            try {
                                startTime = Calendar.getInstance();
                                endTime = Calendar.getInstance();

                                startTime.setTimeInMillis(time.parse(start)
                                        .getTime());
                                endTime.setTimeInMillis(time.parse(end)
                                        .getTime());
                                event = new WeekViewEvent(waitInfo.ID,
                                        waitInfo.Title, startTime, endTime);
                                int colorId = waitInfo.Color;
                                int color = 0;
                                switch (colorId) {
                                    case 0:
                                        color = R.color.cal_color0;
                                        break;
                                    case 1:
                                        color = R.color.cal_color1;
                                        break;
                                    case 2:
                                        color = R.color.cal_color2;
                                        break;
                                    case 3:
                                        color = R.color.cal_color3;
                                        break;
                                    case 4:
                                        color = R.color.cal_color4;
                                        break;
                                    case 5:
                                        color = R.color.cal_color5;
                                        break;
                                }
                                event.setColor(getResources().getColor(color));
                                if (startTime.getTimeInMillis() < endTime.getTimeInMillis())
                                    events.add(event);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        Collections.sort(events);
                        mWeekView.notifyDatasetChanged();
                    }

                    @Override
                    public void onStarted() {
                        mDialog.show();
                    }

                });
    }

}
