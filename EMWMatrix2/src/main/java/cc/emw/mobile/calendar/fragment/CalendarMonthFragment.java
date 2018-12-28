package cc.emw.mobile.calendar.fragment;

import cc.emw.mobile.calendar.CalendarDayActivitys;
import cc.emw.mobile.map.ToastUtil;
import cc.emw.mobile.view.FixedSpeedScroller;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarEditActivity;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.caledar.util.DateUtil;
import com.example.calendar.doim.CalendarViewBuilder;
import com.example.calendar.doim.CustomDate;
import com.example.calendar.widget.CalendarView;
import com.example.calendar.widget.CalendarView.CallBack;
import com.example.calendar.widget.CalendarViewPagerLisenter;
import com.example.calendar.widget.CustomViewPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author zrjt
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.fragment_calendar_month)
public class CalendarMonthFragment extends BaseFragment implements CallBack {

    // 左箭头
    @ViewInject(R.id.btn_calender_left_controll)
    private ImageView leftImg;
    // 右箭头
    @ViewInject(R.id.btn_calender_right_controll)
    private ImageView RightImg;

    @ViewInject(R.id.test)
    private ScrollView scrollView;

    @ViewInject(R.id.calendar_mian)
    private LinearLayout linearLayout;

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新

    @ViewInject(R.id.down_scroll)
    private LinearLayout mLineLayout; // 自头部刷新

    @ViewInject(R.id.nothing_tips)
    private TextView textView;

    @ViewInject(R.id.tv_calendar_time)
    private TextView tvTime;

    @ViewInject(R.id.btn_calendar_dayinfo)
    private ImageButton btnDay; //进入日视图的按钮

    @ViewInject(R.id.tv_no_calender_tips)
    private TextView tvBlankTips;

    private List<CalendarInfo> mDataList = new ArrayList<CalendarInfo>(); // 代办列表数据
    private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();// 传递给日视图WeekView的日程信息
    private ViewPager viewPager;
    private CalendarView[] views;
    private TextView showYearView;
    private TextView showMonthView;
    private TextView showWeekView;
    private CalendarViewBuilder builder = new CalendarViewBuilder();
    private CustomDate mClickDate = new CustomDate();
    public static final String MAIN_ACTIVITY_CLICK_DATE = "main_click_date";

    private int months;

    // 更新日程数据
    private MyBroadcastReceive mReceive;
    // 刷新的action
    public static final String ACTION_REFRESH_SCHEDULE_MONTH_LIST = "cc.emw.mobile.refresh_schedule_month_list";

    private Dialog mLoadingDialog; // 加载框
    private ImageButton btn;
    private List<CustomDate> lists = new ArrayList<CustomDate>();// 用于回调的服务器日程列表
    private List<CustomDate> customDates;
    private List<CalendarInfo> calendarInfos; // 所选时间区内的所有日程


    public CalendarMonthFragment() {

    }

    @Override
    public void onUserInvisible() {
        super.onUserInvisible();
        btn.setVisibility(View.GONE);
    }

    @Override
    public void onUserVisible() {
        super.onUserVisible();
        btn.setImageResource(R.drawable.nav_btn_share);
        btn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        // getScheduleList();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_SCHEDULE_MONTH_LIST);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
        leftImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                viewPager.arrowScroll(17);
            }
        });

        RightImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                viewPager.arrowScroll(66);
            }
        });
    }

    private void refresh() {
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                // here check list view, not content.
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mLineLayout, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getScheduleList();
            }
        });
    }

    private void initView(View view) {
        btn = (ImageButton) getActivity()
                .findViewById(R.id.cm_header_btn_right);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        showMonthView = (TextView) view.findViewById(R.id.show_month_view);
        showYearView = (TextView) view.findViewById(R.id.show_year_view);
        showWeekView = (TextView) view.findViewById(R.id.show_week_view);
        views = builder.createMassCalendarViews(getActivity(), 5, this);
        // 默认点击的是当天
        Calendar calendar = Calendar.getInstance();
        mClickDate.setYear(calendar.get(Calendar.YEAR));
        mClickDate.setMonth(calendar.get(Calendar.MONTH) + 1);
        mClickDate.setDay(calendar.get(Calendar.DAY_OF_MONTH));

        // Viewpager
        setViewPagerScrollSpeed();
        setViewPager();

        mPtrFrameLayout.setPinContent(false);
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

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);

        refresh();
    }

    private void setViewPagerScrollSpeed() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {

        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        }
    }

    @Override
    public void onFirstUserVisible() {

    }

    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_SCHEDULE_MONTH_LIST.equals(action)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    private void thingsDeal() {
        viewPager.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                viewPager.requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
    }

    private void setViewPager() {
        CustomViewPagerAdapter<CalendarView> viewPagerAdapter = new CustomViewPagerAdapter<CalendarView>(
                views);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(498);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new CalendarViewPagerLisenter(
                viewPagerAdapter));
    }

    public void setShowDateViewText(int year, int month) {
        months = month;
        String transMonths = "";
        // switch (months) {
        // case 1:
        // transMonths = "一月";
        // break;
        // case 2:
        // transMonths = "二月";
        // break;
        // case 3:
        // transMonths = "三月";
        // break;
        // case 4:
        // transMonths = "四月";
        // break;
        // case 5:
        // transMonths = "五月";
        // break;
        // case 6:
        // transMonths = "六月";
        // break;
        // case 7:
        // transMonths = "七月";
        // break;
        // case 8:
        // transMonths = "八月";
        // break;
        // case 9:
        // transMonths = "九月";
        // break;
        // case 10:
        // transMonths = "十月";
        // break;
        // case 11:
        // transMonths = "十一月";
        // break;
        // case 12:
        // transMonths = "十二月";
        // break;
        // }
        showYearView.setText(year + "/" + month);
        showMonthView.setText(transMonths + "月");
        showWeekView.setText(DateUtil.weekName[DateUtil.getWeekDay() - 1]);
    }

    @Override
    public void onMesureCellHeight(int cellSpace) {
    }

    @Override
    public void changeDate(CustomDate date) {
        setShowDateViewText(date.year, date.month);
    }

    private void getScheduleList() {
        API.TalkerAPI.GetCalenderListByTimeSpan("2016-01-01 00:00:00",
                "2030-01-01 23:59:59", 1, new RequestCallback<String>(
                        String.class) {

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        mPtrFrameLayout.refreshComplete();
                        ToastUtil.show(getActivity(), "服务器异常" + arg0.toString());
                    }

                    @Override
                    public void onSuccess(String respInfo) {
                        mPtrFrameLayout.refreshComplete();
                        if (respInfo != null) {
                            mDataList.clear();
                            Gson gson = new Gson();
                            JsonArray array = new JsonParser().parse(respInfo)
                                    .getAsJsonArray();
                            for (JsonElement jsonElement : array) {
                                mDataList.add(gson.fromJson(jsonElement,
                                        CalendarInfo.class));
                            }
                            events.clear();
                            // 用于标记有事项的天数
                            customDates = new ArrayList<CustomDate>();
                            WeekViewEvent event;
                            Collections.reverse(mDataList);
                            SimpleDateFormat time = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss");
                            for (int i = 0, count = mDataList.size(); i < count; i++) {
                                CalendarInfo waitInfo = mDataList.get(i);
                                String start = waitInfo.StartTime;
                                String end = waitInfo.OverTime;
                                Calendar startTime;
                                Calendar endTime;
                                try {
                                    startTime = Calendar.getInstance();
                                    startTime.setTimeInMillis(time.parse(start)
                                            .getTime());
                                    endTime = Calendar.getInstance();
                                    endTime.setTimeInMillis(time.parse(end)
                                            .getTime());

                                    event = new WeekViewEvent(waitInfo.ID,
                                            waitInfo.Title, startTime, endTime);

                                    // 标记有事件的天数
                                    CustomDate customDate = new CustomDate();
                                    customDate.setYear(event.getStartTime().get(
                                            Calendar.YEAR));
                                    customDate.setMonth(event.getStartTime().get(
                                            Calendar.MONTH) + 1);
                                    customDate.setDay(event.getStartTime().get(
                                            Calendar.DAY_OF_MONTH));
                                    customDate.setEndDay(event.getEndTime().get(
                                            Calendar.DAY_OF_MONTH));
                                    customDate.setEndMonth(event.getEndTime().get(
                                            Calendar.MONTH) + 1);
                                    customDate.setEndYear(event.getEndTime().get(
                                            Calendar.YEAR));
                                    customDate.setStart(event.getStartTime());
                                    customDate.setEnd(event.getEndTime());
                                    customDates.add(customDate);
                                    event.setColor(waitInfo.Color);
                                    events.add(event);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                            Collections.sort(events);
                            Collections.sort(mDataList);
                            paintView(mDataList, events);
                            if (views != null) {
                                for (int i = 0; i < views.length; i++) {
                                    views[i].initList(customDates);
                                    views[i].update();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void clickDate(CustomDate date) {
        mClickDate = date;
        paintView(mDataList, events);
    }

    /**
     * 日程数据
     *
     * @param calendarInfos
     */
    private void paintView(List<CalendarInfo> calendarInfos,
                           List<WeekViewEvent> events) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar sCal;
        Calendar eCal;
        int isHaveCalenders = 0;    //判断点击天是否有事件
        int isTodayHaveCalenders = 0;   //判断今天是否有事件
        try {
            //点击的时间
            final Calendar clickCalLast = Calendar.getInstance();
            final Calendar clickCalFirst = Calendar.getInstance();
            final Calendar nowCalFirst = Calendar.getInstance();
            //现在的时间
            Calendar nowCalLast = Calendar.getInstance();
            int nowYear = nowCalFirst.get(Calendar.YEAR);
            int nowMonth = nowCalFirst.get(Calendar.MONTH) + 1;
            int nowDay = nowCalFirst.get(Calendar.DAY_OF_MONTH);

            // 点击的天
            int cDays = 0;

            if (mClickDate != null) {
                int cYear = mClickDate.getYear();
                int cMonth = mClickDate.getMonth();
                cDays = mClickDate.getDay();
                String clickTimeFirst = cYear + "-" + cMonth + "-" + cDays
                        + " " + "00:00:00";
                Date clickDateFirst = time.parse(clickTimeFirst);
                clickCalFirst.setTimeInMillis(clickDateFirst.getTime());
            }

            StringBuilder nowTimeLast = new StringBuilder();
            StringBuilder nowTimeFirst = new StringBuilder();
            nowTimeLast.append(nowYear).append("-").append(nowMonth).append("-").append(nowDay).append(" ").append("23:59:59");
            nowTimeFirst.append(nowYear).append("-").append(nowMonth).append("-").append(nowDay).append(" ").append("00:00:00");

//            String nowTimeLast = nowYear + "-" + nowMonth + "-" + nowDay
//                    + " " + "23:59:59";
//            String nowTimeFirst = nowYear + "-" + nowMonth + "-" + nowDay
//                    + " " + "00:00:00";

            // 当前的日期
            Date nowDateLast = time.parse(nowTimeLast.toString());
            Date nowDateFirst = time.parse(nowTimeFirst.toString());

            // 当前的时间
            nowCalLast.setTimeInMillis(nowDateLast.getTime());
            nowCalFirst.setTimeInMillis(nowDateFirst.getTime());
            linearLayout.removeAllViews();
            if (calendarInfos.size() > 0) {
                for (int i = 0; i < calendarInfos.size(); i++) {

                    View view = LayoutInflater.from(getActivity()).inflate(
                            R.layout.item_calendar_scan_info, null);
                    TextView tvCalendar = (TextView) view
                            .findViewById(R.id.tv_calendar_scan_info);
                    sCal = Calendar.getInstance();
                    eCal = Calendar.getInstance();

                    final CalendarInfo calInfo = calendarInfos.get(i);
                    final WeekViewEvent event = events.get(i);

                    String startTimes = calInfo.StartTime;
                    String endTimes = calInfo.OverTime;
                    String hours = "";
                    String mins = "";

                    //日程开始的Date
                    Date dayStart = time.parse(startTimes);
                    sCal.setTimeInMillis(dayStart.getTime());
                    //日程结束的Date
                    Date dayEnd = time.parse(endTimes);
                    eCal.setTimeInMillis(dayEnd.getTime());
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

                    if (mClickDate != null) {
                        int cYear = mClickDate.getYear();
                        int cMonth = mClickDate.getMonth();
                        int cDay = mClickDate.getDay();

                        String clickTimeLast = cYear + "-" + cMonth + "-" + cDay
                                + " " + "23:59:59";
                        String clickTimeFirst = cYear + "-" + cMonth + "-" + cDay
                                + " " + "00:00:00";
                        // 点击的日期
                        Date clickDateLast = time.parse(clickTimeLast);
                        Date clickDateFirst = time.parse(clickTimeFirst);

                        // 点击的时间
                        clickCalLast.setTimeInMillis(clickDateLast.getTime());
                        clickCalFirst.setTimeInMillis(clickDateFirst.getTime());

                        if (nowCalFirst.equals(clickCalFirst)) {
                            tvTime.setText("今天");
                        } else {
                            tvTime.setText(cDay + "日");
                        }
                        btnDay.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                Intent intent = new Intent(getActivity(),
                                        CalendarDayActivitys.class);

                                Bundle args = new Bundle();

                                args.putSerializable("LinClickTask", event);
                                args.putSerializable("clickDate", clickCalFirst);
                                intent.putExtras(args);

                                startActivity(intent);
                            }
                        });

                        if (clickCalLast.getTimeInMillis() >= sCal
                                .getTimeInMillis()
                                && clickCalFirst.getTimeInMillis() <= eCal
                                .getTimeInMillis()) {
                            isHaveCalenders++;
                            tvCalendar.setText(taskStartTime + " " + calInfo.Title);
                            int colorId = calInfo.Color;
                            switch (colorId) {
                                case 0:
                                    tvCalendar
                                            .setBackgroundResource(R.drawable.round_cal_bg_0);
                                    break;
                                case 1:
                                    tvCalendar
                                            .setBackgroundResource(R.drawable.round_cal_bg_1);
                                    break;
                                case 2:
                                    tvCalendar
                                            .setBackgroundResource(R.drawable.round_cal_bg_2);
                                    break;
                                case 3:
                                    tvCalendar
                                            .setBackgroundResource(R.drawable.round_cal_bg_3);
                                    break;
                                case 5:
                                    tvCalendar
                                            .setBackgroundResource(R.drawable.round_cal_bg_5);
                                    break;
                            }
                            linearLayout.addView(view);
                        }
                    }

                    view.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(getActivity(),
                                    CalendarEditActivity.class);
                            Bundle args = new Bundle();
                            args.putSerializable("calendarInfo", calInfo);
                            intent.putExtras(args);
                            startActivity(intent);
                        }
                    });


                }
                //判断当天是否有事件
                if (isHaveCalenders > 0 || isTodayHaveCalenders > 0) {
                    btnDay.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    tvBlankTips.setVisibility(View.GONE);
                    isHaveCalenders = 0;
                    isTodayHaveCalenders = 0;
                } else {
                    btnDay.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                    tvBlankTips.setVisibility(View.VISIBLE);
                }
            } else {
                if (mClickDate != null) {
                    if (nowCalFirst.equals(clickCalFirst)) {
                        tvTime.setText("今天");
                    } else {
                        tvTime.setText(cDays + "日");
                    }
                } else {
                    tvTime.setText("今天");
                }
                tvBlankTips.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                btnDay.setVisibility(View.GONE);
                //btnDay.setOnClickListener(null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
