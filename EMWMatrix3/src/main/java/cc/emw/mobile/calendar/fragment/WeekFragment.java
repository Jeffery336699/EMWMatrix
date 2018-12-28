package cc.emw.mobile.calendar.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarUtils;
import cc.emw.mobile.calendar.adapter.CalendarWeekAdapter;
import cc.emw.mobile.calendar.adapter.ScalingListViewMonthAdapter;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.EventGroup;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CacheUtils;
import cc.emw.mobile.util.CalendarUtil;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_week)
public class WeekFragment extends BaseFragment {

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.elv_calendar)
    private ExpandableListView mListView;
    @ViewInject(R.id.fl_elv_calendar)
    private FrameLayout frameLayout;
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;
    @ViewInject(R.id.ll_dynamic_blank)
    private LinearLayout mBlankLayout;
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mSearchListView;

    private CalendarWeekAdapter adapter;
    private ScalingListViewMonthAdapter scalingListViewMonthAdapter;
    private List<CalendarInfo> calendarInfos;
    private List<EventGroup> eventGroups;
    private List<CalendarInfo> mDataList = new ArrayList<>();
    private String weekFirstStr;
    private String weekEndStr;
    private int monthDays;
    public static final String ACTION_REFRESH_SCHEDULE_LIST = "cc.emew.mobile.refresh.calendar";
    private MyBroadcastReceive receive;
    private EditText mSearchEt; // 搜索框
    private List<CalendarInfo> gSearchList = new ArrayList<>();  //搜索列表

    // 日程月视图缓存
    public static final int READCACHE = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case READCACHE:
                    List<EventGroup> list = (ArrayList<EventGroup>) msg.obj;
                    adapter.setGroupData(list);
                    adapter.notifyDataSetChanged();
                    getCalendar(CalendarFragment.currentId);
                    break;
            }
        }
    };


    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_SCHEDULE_LIST.equals(action)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    public WeekFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        initCacheData();
    }

    private void init() {
        receive = new MyBroadcastReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_SCHEDULE_LIST);
        getActivity().registerReceiver(receive, filter);
        mSearchEt = (EditText) getParentFragment().getView().findViewById(R.id.et_search_keyword_calendar);
        // header
        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(getActivity(), 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setToggleMenu(true);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        adapter = new CalendarWeekAdapter(getActivity());
        mListView.setDivider(null);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.elv_footview_item, null);
        mListView.addFooterView(inflate);
        mListView.setAdapter(adapter);

        scalingListViewMonthAdapter = new ScalingListViewMonthAdapter(gSearchList, getActivity());
        mSearchListView.setAdapter(scalingListViewMonthAdapter);

        refresh();
        mBlankLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPtrFrameLayout.autoRefresh(false);
            }
        });

        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                gSearchList.clear();
                if (!TextUtils.isEmpty(s)) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mDataList.size(); i++) {
                        CalendarInfo userInfo = mDataList.get(i);
                        String name = userInfo.Title;
                        name = name.replace(" ", "");
                        if (name != null) {
                            CharacterParser characterParser = CharacterParser.getInstance();
                            String selling = characterParser.getSelling(name.toLowerCase());
                            sb.delete(0, sb.length());
                            for (int j = 0; j < name.length(); j++) {
                                String substring = name.substring(j, j + 1);
                                substring = characterParser.convert(substring);
                                if (substring != null && substring.length() >= 1) {
                                    substring = substring.substring(0, 1);
                                    sb.append(substring);
                                }
                            }
                            if (name.contains(s) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
                                gSearchList.add(mDataList.get(i));
                            }
                        }
                    }
                    mPtrFrameLayout.setVisibility(View.GONE);
                    mSearchListView.setVisibility(View.VISIBLE);
                    scalingListViewMonthAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int before, int count) {
                // TODO Auto-generated method stub
                mPtrFrameLayout.setVisibility(View.VISIBLE);
                mSearchListView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                // HelpUtil.hideSoftInput(getActivity(), mSearchEt);
            }
        });
    }

    /**
     * 初始化日程缓存数据
     */
    private void initCacheData() {
        new Thread() {
            @Override
            public void run() {
                List<EventGroup> eventGroups = CacheUtils.readObjectFile(PrefsUtil.readUserInfo().ID + "", new TypeToken<List<EventGroup>>() {
                }.getType());
                if (eventGroups != null && eventGroups.size() > 0) {
                    Message message = mHandler.obtainMessage();
                    message.what = READCACHE;
                    message.obj = eventGroups;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    private void refresh() {

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getCalendar(CalendarFragment.currentId);
            }
        });
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

    private void getCalendar(int userid) {
        final SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CalendarUtil calUtil = new CalendarUtil();
        Calendar today = Calendar.getInstance();
        monthDays = CalendarUtils.daysInGregorianMonth(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1);
        weekFirstStr = calUtil.getFirstDayOfMonth();    //本月开始的时间
        weekEndStr = calUtil.getDefaultDay();    //本月结束的时间
        weekFirstStr = weekFirstStr + " " + "00:00:00";
        weekEndStr = weekEndStr + " " + "23:59:59";
        API.TalkerAPI.GetCalenderListByTimeSpan(weekFirstStr,
                weekEndStr, 1, userid, new RequestCallback<CalendarInfo>(
                        CalendarInfo.class) {
                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        ToastUtil.showToast(getActivity(), "服务器异常");
                        mPtrFrameLayout.refreshComplete();
                        if (arg0 instanceof ConnectException) {
                            mNetworkTipsLayout.setVisibility(View.VISIBLE);
                            ToastUtil.showToast(getActivity(), "网络连接异常");
                        }
                    }

                    @Override
                    public void onParseSuccess(List<CalendarInfo> respList) {
                        mDataList.clear();
                        mDataList.addAll(respList);
                        mPtrFrameLayout.refreshComplete();
                        mNetworkTipsLayout.setVisibility(View.GONE);
                        Collections.sort(respList);
                        Calendar calendar = Calendar.getInstance();
                        Calendar sCal = Calendar.getInstance();// 日程开始的时间
                        Calendar eCal = Calendar.getInstance();// 日程结束的时间
                        Calendar wScal = Calendar.getInstance();// 本周开始的时间
                        Calendar wEcal = Calendar.getInstance();
                        Calendar nextDay = Calendar.getInstance();
                        eventGroups = new ArrayList<>();
                        if (respList != null && respList.size() > 0) {
                            mBlankLayout.setVisibility(View.GONE);
                            try {
                                wScal.setTimeInMillis(time.parse(weekFirstStr).getTime());
                                wEcal.setTimeInMillis(time.parse(weekEndStr).getTime());
                                calendar.setTimeInMillis(time.parse(weekFirstStr).getTime());
                                nextDay.setTimeInMillis(time.parse(weekFirstStr).getTime());
                                for (int j = 0; j < monthDays; j++) {
                                    EventGroup eventGroup = new EventGroup();
                                    calendarInfos = new ArrayList<>();
                                    nextDay.add(Calendar.DAY_OF_MONTH, 1);
                                    for (int i = 0; i < respList.size(); i++) {
                                        CalendarInfo calendarInfo = respList.get(i);
                                        sCal.setTimeInMillis(time.parse(calendarInfo.StartTime).getTime());
                                        eCal.setTimeInMillis(time.parse(calendarInfo.OverTime).getTime());
                                        if (sCal.getTimeInMillis() <= (nextDay.getTimeInMillis() - 1000) && eCal.getTimeInMillis() >= (calendar.getTimeInMillis())) {
                                            if (calendarInfo.Color == CalendarFragment.tag) {
                                                calendarInfos.add(calendarInfo);
                                            } else if (CalendarFragment.tag == -1) {
                                                calendarInfos.add(calendarInfo);
                                            }
                                        }
                                    }
                                    if (calendarInfos != null && calendarInfos.size() > 0) {
                                        if (calendar == wScal) {
                                        } else {
                                            calendar.setTimeInMillis(calendar.getTimeInMillis() + 1000);
                                        }
                                        eventGroup.time = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1)
                                                + "/" + calendar.get(Calendar.DAY_OF_MONTH);
                                        eventGroup.num = calendarInfos.size();
                                        eventGroup.mDataList = (ArrayList<CalendarInfo>) calendarInfos;
                                        eventGroups.add(eventGroup);
                                    }
                                    calendar.setTimeInMillis(nextDay.getTimeInMillis() - 1000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Collections.reverse(eventGroups);
                            CacheUtils.writeObjectFile(PrefsUtil.readUserInfo().ID + "", eventGroups);
                            adapter.setGroupData(eventGroups);
                            for (int i = 0; i < eventGroups.size(); i++) {
                                mListView.expandGroup(i);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            // TODO 为空处理
                            mBlankLayout.setVisibility(View.VISIBLE);
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
