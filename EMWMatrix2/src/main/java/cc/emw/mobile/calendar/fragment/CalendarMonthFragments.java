package cc.emw.mobile.calendar.fragment;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.prolificinteractive.materialcalendarview.decorators.EventDecorator;
import com.prolificinteractive.materialcalendarview.decorators.TodayDecorator;
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarDayActivitys;
import cc.emw.mobile.calendar.CalendarEditActivity;
import cc.emw.mobile.calendar.adapter.ScalingListViewMonthAdapter;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.map.ToastUtil;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CalendarUtil;
import cc.emw.mobile.util.DisplayUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * @author zrjt
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.fragment_calendar_months)
public class CalendarMonthFragments extends BaseFragment {

    // 左箭头
    @ViewInject(R.id.btn_calender_left_controll)
    private ImageView leftImg;
    // 右箭头
    @ViewInject(R.id.btn_calender_right_controll)
    private ImageView RightImg;
    @ViewInject(R.id.calendarView)
    private MaterialCalendarView widget;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.lv_calendar_month)
    private ListView mListViews;
    @ViewInject(R.id.tv_calendar_time)
    private TextView tvTime;
    @ViewInject(R.id.btn_calendar_dayinfo)
    private ImageButton btnDay; //进入日视图的按钮
    @ViewInject(R.id.tv_no_calender_tips)
    private TextView tvBlankTips;
    @ViewInject(R.id.show_year_view)
    private TextView tvCurrentTime;
    @ViewInject(R.id.down_scroll)
    private LinearLayout mRefreshLayout;
    // 更新日程数据
    private MyBroadcastReceive mReceive;
    // 刷新的action
    public static final String ACTION_REFRESH_SCHEDULE_MONTH_LIST = "cc.emw.mobile.refresh_schedule_month_list";
    private List<CalendarInfo> mDataList = new ArrayList<>(); // 代办列表数据
    private List<CalendarInfo> mAllDataList = new ArrayList<>(); // 代办列表数据
    private List<CalendarDay> calendarDays = new ArrayList<>(); //绘制有事件的天数
    public static Calendar mClickDate;
    private ScalingListViewMonthAdapter scalingListViewMonthAdapter;
    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean flag = false;

    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_SCHEDULE_MONTH_LIST.equals(action)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    public CalendarMonthFragments() {
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_SCHEDULE_MONTH_LIST);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
        //设置今天为默认选中的天
        Calendar toDayCalendar = Calendar.getInstance();
        CalendarDay toDayCalendarDay = CalendarDay.from(toDayCalendar);
        mClickDate = toDayCalendar;

        // widget
        widget.setLeftArrowMask(getResources().getDrawable(R.drawable.btn_calendar_month_leftarrow));
        widget.setRightArrowMask(getResources().getDrawable(R.drawable.btn_calendar_month_rightarrow));
        widget.setTitleFormatter(new DateFormatTitleFormatter()); //设置顶部日期格式
        widget.setTileWidth(DisplayUtil.getDisplayWidth(getActivity()) / 7); //设置单元格宽度
        widget.setCurrentDate(toDayCalendarDay); //跳转到指定日期
        widget.setSelectedDate(toDayCalendarDay);
        widget.addDecorator(new TodayDecorator(getActivity())); //当前日期空心圆显示
        // 隐藏头部时间标识
        widget.setTopbarVisible(false);
        widget.clearAnimation();
        // 初始当前的时间
        CalendarDay currentDate = widget.getCurrentDate();
        int year = currentDate.getYear();
        int month = currentDate.getMonth() + 1;
        tvCurrentTime.setText(year + "/" + month);

        widget.setShowOtherDates(MaterialCalendarView.SHOW_NONE);

        // 点击可跳转下个月
        // widget.setAllowClickDaysOutsideCurrentMonth(true);

        widget.setAlwaysDrawnWithCacheEnabled(true);

        widget.setFilterTouchesWhenObscured(true);

        // TODO 监听widget的快速滑动

        // 监听滑动时改变的时间
        widget.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widgets, final CalendarDay date) {
                mClickDate = date.getCalendar();
                int year = date.getYear();
                int month = date.getMonth() + 1;
                tvCurrentTime.setText(year + "/" + month);
                widgets.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sortEvent(mAllDataList);
                        widget.setSelectedDate(date);
                    }
                }, 250);
            }
        });


        widget.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mClickDate = date.getCalendar();
                widget.setSelectedDate(date);
                sortEvent(mAllDataList);
            }
        });

        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mRefreshLayout, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getScheduleList();
            }
        });
        // header
        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(getActivity(), 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setEnabled(true);

        mPtrFrameLayout.disableWhenHorizontalMove(true);
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);

        scalingListViewMonthAdapter = new ScalingListViewMonthAdapter(mDataList, getActivity());
        mListViews.setAdapter(scalingListViewMonthAdapter);
    }

    // 左右按钮的点击事件
    @Event(value = {R.id.btn_calender_left_controll, R.id.btn_calender_right_controll})
    private void topClick(View view) {
        switch (view.getId()) {
            case R.id.btn_calender_left_controll:
                widget.goToPrevious();
                break;
            case R.id.btn_calender_right_controll:
                widget.goToNext();
                break;
        }
    }

    @Override
    public void onFirstUserVisible() {
//        mPtrFrameLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrameLayout.autoRefresh(false);
//            }
//        }, 100);
    }

    private void getScheduleList() {
        API.TalkerAPI.GetCalenderListByTimeSpan("2016-01-01 00:00:00",
                "2030-01-01 23:59:59", 1, new RequestCallback<CalendarInfo>(
                        CalendarInfo.class) {

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        mPtrFrameLayout.refreshComplete();
                        ToastUtil.show(getActivity(), "服务器异常");
                    }

                    @Override
                    public void onParseSuccess(List<CalendarInfo> respList) {
                        mPtrFrameLayout.refreshComplete();
                        if (respList != null && respList.size() > 0) {
                            mAllDataList.clear();
                            calendarDays.clear();
                            mAllDataList.addAll(respList);
                            Calendar startTime = Calendar.getInstance();
                            Calendar endTime = Calendar.getInstance();
                            Calendar duringCal;
                            for (int i = 0; i < respList.size(); i++) {
                                CalendarInfo waitInfo = respList.get(i);
                                String start = waitInfo.StartTime;
                                String end = waitInfo.OverTime;
                                try {
                                    startTime.setTimeInMillis(time.parse(start)
                                            .getTime());
                                    endTime.setTimeInMillis(time.parse(end)
                                            .getTime());
                                    duringCal = startTime;
                                    // 有事件的天集合
                                    calendarDays.add(CalendarDay.from(startTime));
                                    if (startTime.getTimeInMillis() < endTime.getTimeInMillis()) {
                                        // 绘制有事件的天的小圆点
                                        StringBuilder startBuilder = new StringBuilder();
                                        StringBuilder endBuilder = new StringBuilder();
                                        startBuilder.append(startTime.get(Calendar.YEAR)).append("-").append(startTime.get(Calendar.MONTH) + 1).append("-").append(startTime.get(Calendar.DAY_OF_MONTH));
                                        endBuilder.append(endTime.get(Calendar.YEAR)).append("-").append(endTime.get(Calendar.MONTH) + 1).append("-").append(endTime.get(Calendar.DAY_OF_MONTH));
                                        int between = Integer.valueOf(CalendarUtil.getTwoDay(endBuilder.toString(), startBuilder.toString()));
                                        if (between != 0) {
                                            for (int j = 0; j < between; j++) {
                                                duringCal.add(Calendar.DAY_OF_MONTH, 1);
                                                calendarDays.add(CalendarDay.from(duringCal));
                                            }
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            widget.removeDecorators();
                            widget.addDecorator(new TodayDecorator(getActivity())); //当前日期空心圆显示
                            //添加事件，显示小圆点
                            widget.addDecorator(new EventDecorator(getResources().getColor(R.color.mcv_event_normal), calendarDays));
                            sortEvent(mAllDataList);
                        }
                    }
                }
        );
    }

    private void sortEvent(List<CalendarInfo> cInfos) {
        mDataList.clear();
        //日程的时间
        Calendar sCal;
        Calendar eCal;
        //点击的时间
        final Calendar clickCalLast = Calendar.getInstance();
        final Calendar clickCalFirst = Calendar.getInstance();
        //现在的时间
        final Calendar nowCalFirst = Calendar.getInstance();
        final Calendar nowCalLast = Calendar.getInstance();
        int nowYear = nowCalFirst.get(Calendar.YEAR);
        int nowMonth = nowCalFirst.get(Calendar.MONTH) + 1;
        int nowDay = nowCalFirst.get(Calendar.DAY_OF_MONTH);
        // 点击的天
        int cDays = 0;
        StringBuilder nowTimeLast = new StringBuilder();
        StringBuilder nowTimeFirst = new StringBuilder();
        nowTimeLast.append(nowYear).append("-").append(nowMonth).append("-").append(nowDay).append(" ").append("23:59:59");
        nowTimeFirst.append(nowYear).append("-").append(nowMonth).append("-").append(nowDay).append(" ").append("00:00:00");
        for (int i = 0; i < cInfos.size(); i++) {
            final CalendarInfo waitInfo = cInfos.get(i);
            try {
                // 当前的日期
                Date nowDateLast = null;
                nowDateLast = time.parse(nowTimeLast.toString());
                Date nowDateFirst = time.parse(nowTimeFirst.toString());
                // 当前的时间
                nowCalLast.setTimeInMillis(nowDateLast.getTime());
                nowCalFirst.setTimeInMillis(nowDateFirst.getTime());
                //日程的时间
                sCal = Calendar.getInstance();
                eCal = Calendar.getInstance();
                String startTimes = waitInfo.StartTime;
                String endTimes = waitInfo.OverTime;
                //日程开始
                Date dayStart = time.parse(startTimes);
                sCal.setTimeInMillis(dayStart.getTime());
                //日程结束
                Date dayEnd = time.parse(endTimes);
                eCal.setTimeInMillis(dayEnd.getTime());
                if (mClickDate != null) {
                    int cYear = mClickDate.get(Calendar.YEAR);
                    int cMonth = mClickDate.get(Calendar.MONTH) + 1;
                    cDays = mClickDate.get(Calendar.DAY_OF_MONTH);
                    String clickTimeFirst = cYear + "-" + cMonth + "-" + cDays
                            + " " + "00:00:00";
                    String clickTimeLast = cYear + "-" + cMonth + "-" + cDays
                            + " " + "23:59:59";
                    Date clickDateFirst = time.parse(clickTimeFirst);
                    Date clickDateLast = time.parse(clickTimeLast);
                    clickCalFirst.setTimeInMillis(clickDateFirst.getTime());
                    clickCalLast.setTimeInMillis(clickDateLast.getTime());
                    tvTime.setText(cDays + "日");
                    if (cDays == nowDay && cMonth == nowMonth && cYear == nowYear)
                        tvTime.setText("今天");
                }
                // 判断是否是点击天的日程
                if (clickCalLast.getTimeInMillis() >= sCal
                        .getTimeInMillis()
                        && clickCalFirst.getTimeInMillis() <= eCal
                        .getTimeInMillis()) {
                    mDataList.add(waitInfo);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (mDataList != null && mDataList.size() > 0) {
            mListViews.setVisibility(View.VISIBLE);
            tvBlankTips.setVisibility(View.GONE);
            btnDay.setVisibility(View.VISIBLE);
            Collections.reverse(mDataList);
            Collections.sort(mDataList);
        } else {
            mListViews.setVisibility(View.GONE);
            tvBlankTips.setVisibility(View.VISIBLE);
            btnDay.setVisibility(View.GONE);
        }
        btnDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        CalendarDayActivitys.class);

                Bundle args = new Bundle();
                args.putSerializable("clickDate", clickCalFirst);
                intent.putExtras(args);

                startActivity(intent);
            }
        });
        scalingListViewMonthAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
    }

}
