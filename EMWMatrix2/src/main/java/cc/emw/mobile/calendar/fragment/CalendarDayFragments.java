package cc.emw.mobile.calendar.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarEditActivity;
import cc.emw.mobile.calendar.adapter.ScalingListViewAdapter;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CalendarUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * @author zrjt
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.fragment_calendar_days)
public class CalendarDayFragments extends BaseFragment {

    @ViewInject(R.id.tv_month_tag)
    private TextView tvMonthTag;
    @ViewInject(R.id.lv_calendar_day)
    private ListView mListView;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.ll_blank_calendar)
    private LinearLayout blankLayout;
    private List<CalendarInfo> mDataList; // 代办列表数据
    public static final String ACTION_REFRESH_SCHEDULE_LIST = "cc.emew.mobile.refresh.calendar";
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout; // 网络错误提示的Layout

    private MyBroadcastReceive receive;
    private ScalingListViewAdapter scalingListViewAdapter;

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
                        mListView, header);
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
        mDataList = new ArrayList<>();
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
                "2030-12-12 23:59:59", 1, new RequestCallback<CalendarInfo>(
                        CalendarInfo.class) {
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
                    public void onParseSuccess(List<CalendarInfo> respList) {
                        mNetworkTipsLayout.setVisibility(View.GONE);
                        if (respList != null && respList.size() > 0) {
                            mDataList.clear();
                            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            CalendarUtil calUtil = new CalendarUtil();
                            Calendar nowCal = Calendar.getInstance();// 系统当前的Cal
                            Calendar toDayFirst = Calendar.getInstance();
                            Calendar toDayLast = Calendar.getInstance();
                            Calendar benYueEnd = Calendar.getInstance();
                            Calendar benYueFirst = Calendar.getInstance();
                            int nowYear = nowCal.get(Calendar.YEAR);// 当前年份
                            int nowMonth = nowCal.get(Calendar.MONTH) + 1;// 当前的月份
                            int nowMonthEnd = calUtil.getLastDayOfMonth(nowYear, nowMonth);// 等到本月有多少天
                            int nowDay = nowCal.get(Calendar.DAY_OF_MONTH);// 今天是多少号
                            String benYueFirstStr = nowYear + "-" + nowMonth + "-" + 1
                                    + " " + "00:00:00";// 本月开始的时间测试
                            String nowStr = nowYear + "-" + nowMonth + "-" + nowDay + " "
                                    + "00:00:00";// 今天开始的时间
                            String nowEndStr = nowYear + "-" + nowMonth + "-" + nowDay
                                    + " " + "23:59:59";// 今天开始的时间
                            String benYueEndStr = nowYear + "-" + nowMonth + "-"
                                    + nowMonthEnd + " " + "23:59:59";// 本月结束的时间
                            try {
                                toDayFirst.setTimeInMillis(time.parse(nowStr).getTime());
                                toDayLast.setTimeInMillis(time.parse(nowEndStr).getTime());
                                benYueEnd.setTimeInMillis(time.parse(benYueEndStr).getTime());
                                benYueFirst.setTimeInMillis(time.parse(benYueFirstStr).getTime());

                                for (int i = 0; i < respList.size(); i++) {
                                    CalendarInfo calendarInfo = respList.get(i);
                                    Calendar sCal = Calendar.getInstance();// 日程开始的时间
                                    Calendar eCal = Calendar.getInstance();// 日程结束的时间

                                    String startTime = calendarInfo.StartTime;
                                    String endTime = calendarInfo.OverTime;
                                    Date dayStart = time.parse(startTime);
                                    Date dayEnd = time.parse(endTime);
                                    sCal.setTimeInMillis(dayStart.getTime());
                                    eCal.setTimeInMillis(dayEnd.getTime());
                                    // 判断日程是否是本月的日程且是今天以后的
                                    if (sCal.getTimeInMillis() <= benYueEnd.getTimeInMillis()
                                            && eCal.getTimeInMillis() >= toDayFirst
                                            .getTimeInMillis()) {
                                        mListView.setVisibility(View.VISIBLE);
                                        tvMonthTag.setVisibility(View.VISIBLE);
                                        tvMonthTag.setText(nowYear + "年" + nowMonth + "月");
                                        blankLayout.setVisibility(View.GONE);
                                        mDataList.add(calendarInfo);
                                    }
                                }
                                if (mDataList.size() > 0) {
                                    Collections.sort(mDataList);
                                } else {
                                    mListView.setVisibility(View.GONE);
                                    tvMonthTag.setVisibility(View.GONE);
                                    blankLayout.setVisibility(View.VISIBLE);
                                }
                                scalingListViewAdapter = new ScalingListViewAdapter(mDataList, getActivity());
                                mListView.setAdapter(scalingListViewAdapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            mListView.setVisibility(View.GONE);
                            tvMonthTag.setVisibility(View.GONE);
                            blankLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        if (receive != null)
            getActivity().unregisterReceiver(receive); // 取消监听
        super.onDestroy();
    }
}
