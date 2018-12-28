package cc.emw.mobile.calendar.fragment;

import cc.emw.mobile.entity.CalendarInfo;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarEditActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CalendarUtil;
import cc.emw.mobile.util.ToastUtil;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author zrjt
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.fragment_calendar_day)
public class CalendarDayFragment extends BaseFragment {

    @ViewInject(R.id.tv_month_tag)
    private TextView tvMonthTag;
    @ViewInject(R.id.calendar_day_main)
    private LinearLayout linearLayout;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.calendar_scroll_day)
    private ScrollView scrollView;
    @ViewInject(R.id.ll_blank_calendar)
    private LinearLayout blankLayout;
    private List<CalendarInfo> mDataList; // 代办列表数据
    private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
    public static final String ACTION_REFRESH_SCHEDULE_LIST = "cc.emew.mobile.refresh.calendar";
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout; // 网络错误提示的Layout

    private MyBroadcastReceive receive;

    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_SCHEDULE_LIST.equals(action)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    @Event(R.id.ll_network_tips)
    private void onNetworkTipsClick(View v) {
        mPtrFrameLayout.autoRefresh(false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        receive = new MyBroadcastReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_SCHEDULE_LIST);
        getActivity().registerReceiver(receive, filter);
        init();
    }

    private void init() {
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        scrollView, header);
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
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
    }

    @Override
    public void onFirstUserVisible() {
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    private void getScheduleList() {
        API.TalkerAPI.GetCalenderListByTimeSpan("2015-12-01 00:00:00",
                "2030-12-12 23:59:59", 1, new RequestCallback<String>(
                        String.class) {
                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        ToastUtil.showToast(getActivity(), "服务器异常");
                        mPtrFrameLayout.refreshComplete();
                        if (arg0 instanceof ConnectException) {
                            blankLayout.setVisibility(View.GONE);
                            mNetworkTipsLayout.setVisibility(View.VISIBLE);
                            tvMonthTag.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onFinished() {
                        mPtrFrameLayout.refreshComplete();
                    }

                    @Override
                    public void onSuccess(String respInfo) {
                        if (!respInfo.equals("\"null\"")) {
                            mNetworkTipsLayout.setVisibility(View.GONE);
                            tvMonthTag.setVisibility(View.VISIBLE);
                            linearLayout.removeAllViews();
                            Gson gson = new Gson();
                            mDataList = new ArrayList<CalendarInfo>();
                            JsonArray array = new JsonParser().parse(respInfo)
                                    .getAsJsonArray();
                            for (JsonElement jsonElement : array) {
                                mDataList.add(gson.fromJson(jsonElement,
                                        CalendarInfo.class));
                            }
                            events.clear();
                            // 用于标记有事项的天数
                            Collections.reverse(mDataList);
                            for (int i = 0, count = mDataList.size(); i < count; i++) {
                                CalendarInfo waitInfo = mDataList.get(i);
                                String start = waitInfo.StartTime;// 日程开始的时间
                                String end = waitInfo.OverTime;// 日程结束的时间
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
                                    event.setColor(waitInfo.Color);
                                    events.add(event);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            Collections.sort(events);
                            Collections.sort(mDataList);
                            paintView(mDataList);
                        } else {
                            ToastUtil.showToast(getActivity(), "返回数据为空");
                        }
                    }
                });
    }

    private void paintView(List<CalendarInfo> infos) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeWeeks = new SimpleDateFormat("yyyy-MM-dd");
        Calendar sCal; // 日程开始的时间
        Calendar eCal; // 日程结束的时间
        Calendar toDayFirst; // 今天开始的时间
        Calendar toDayLast; // 今天结束的时间
        Calendar benYueEnd; // 本月结束的时间
        Calendar benYueFirst; // 本月开始的时间 (测试)
        int todayCals = 0;
        for (int i = 0; i < infos.size(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.item_calendar_day_info, null);
            TextView tvTime = (TextView) view
                    .findViewById(R.id.tv_time_tag_to_day);
            TextView tvTimeWeek = (TextView) view
                    .findViewById(R.id.tv_time_tag_to_week);
            TextView tvCalendarInfo = (TextView) view
                    .findViewById(R.id.tv_calendar_scan_info);
            final CalendarInfo calendarInfo = infos.get(i);
            String startTime = calendarInfo.StartTime;
            String endTime = calendarInfo.OverTime;
            String week = "";
            try {
                CalendarUtil calUtil = new CalendarUtil();
                Calendar nowCal = Calendar.getInstance();// 系统当前的Cal
                sCal = Calendar.getInstance();
                eCal = Calendar.getInstance();
                toDayFirst = Calendar.getInstance();
                toDayLast = Calendar.getInstance();
                benYueEnd = Calendar.getInstance();
                benYueFirst = Calendar.getInstance();
                Date dayStart = time.parse(startTime);
                Date dayEnd = time.parse(endTime);
                int nowYear = nowCal.get(Calendar.YEAR);// 当前年份
                int nowMonth = nowCal.get(Calendar.MONTH) + 1;// 当前的月份
                int nowMonthEnd = calUtil.getLastDayOfMonth(nowYear, nowMonth);// 等到本月有多少天
                int nowDay = toDayFirst.get(Calendar.DAY_OF_MONTH);// 今天是多少号
                String benYueFirstStr = nowYear + "-" + nowMonth + "-" + 1
                        + " " + "00:00:00";// 本月开始的时间测试
                String nowStr = nowYear + "-" + nowMonth + "-" + nowDay + " "
                        + "00:00:00";// 今天开始的时间
                String nowEndStr = nowYear + "-" + nowMonth + "-" + nowDay
                        + " " + "23:59:59";// 今天开始的时间
                String benYueEndStr = nowYear + "-" + nowMonth + "-"
                        + nowMonthEnd + " " + "23:59:59";// 本月结束的时间
                sCal.setTimeInMillis(dayStart.getTime());
                eCal.setTimeInMillis(dayEnd.getTime());
                toDayFirst.setTimeInMillis(time.parse(nowStr).getTime());
                toDayLast.setTimeInMillis(time.parse(nowEndStr).getTime());
                benYueEnd.setTimeInMillis(time.parse(benYueEndStr).getTime());
                benYueFirst.setTimeInMillis(time.parse(benYueFirstStr)
                        .getTime());
                // 判断日程是否是本月的日程且是今天以后的
                if (sCal.getTimeInMillis() <= benYueEnd.getTimeInMillis()
                        && eCal.getTimeInMillis() >= toDayFirst
                        .getTimeInMillis()) {
                    String eventTimeStr = nowYear + "-" + nowMonth + "-"
                            + sCal.get(Calendar.DAY_OF_MONTH);
                    Calendar eventCal = Calendar.getInstance();
                    eventCal.setTimeInMillis(timeWeeks.parse(eventTimeStr)
                            .getTime());
                    week = getWeek(eventCal); // 时间开始的星期
                    String monthTag = nowYear + "年" + nowMonth + "月";

                    tvTimeWeek.setText(week);
                    if ((sCal.get(Calendar.MONTH) + 1) != nowMonth) {
                        tvTimeWeek
                                .setText((sCal.get(Calendar.MONTH) + 1) + "月");
                    }
                    tvTime.setText(sCal.get(Calendar.DAY_OF_MONTH) + "");
                    tvMonthTag.setText(monthTag);
                    tvMonthTag.setVisibility(View.VISIBLE);
                    tvCalendarInfo.setText(calendarInfo.Title);
                    // 判断是否是今天的事件
                    if (sCal.getTimeInMillis() <= toDayLast.getTimeInMillis()
                            && eCal.getTimeInMillis() >= toDayFirst
                            .getTimeInMillis()) {
                        todayCals++;
                        tvCalendarInfo.setTextColor(getResources().getColor(
                                R.color.white));
                        int colorId = calendarInfo.Color;
                        switch (colorId) {
                            case 0:
                                tvCalendarInfo
                                        .setBackgroundResource(R.drawable.round_cal_bg_0);
                                break;
                            case 1:
                                tvCalendarInfo
                                        .setBackgroundResource(R.drawable.round_cal_bg_1);
                                break;
                            case 2:
                                tvCalendarInfo
                                        .setBackgroundResource(R.drawable.round_cal_bg_2);
                                break;
                            case 3:
                                tvCalendarInfo
                                        .setBackgroundResource(R.drawable.round_cal_bg_3);
                                break;
                            case 4:
                                tvCalendarInfo
                                        .setBackgroundResource(R.drawable.round_cal_bg_4);
                                break;
                            case 5:
                                tvCalendarInfo
                                        .setBackgroundResource(R.drawable.round_cal_bg_5);
                                break;
                        }
                    }

                    linearLayout.addView(view);

                    view.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(getActivity(),
                                    CalendarEditActivity.class);
                            Bundle args = new Bundle();
                            args.putSerializable("calendarInfo", calendarInfo);
                            intent.putExtras(args);
                            startActivity(intent);
                        }
                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (todayCals == 0) {
            tvMonthTag.setVisibility(View.GONE);
            blankLayout.setVisibility(View.VISIBLE);
        } else {
            blankLayout.setVisibility(View.GONE);
        }
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

    @Override
    public void onDestroy() {
        if (receive != null)
            getActivity().unregisterReceiver(receive); // 取消监听
        super.onDestroy();
    }
}
