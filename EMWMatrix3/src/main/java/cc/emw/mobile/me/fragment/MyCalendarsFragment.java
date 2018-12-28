package cc.emw.mobile.me.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.me.adapter.RecyclerCalendarAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.autoload.PullLoadMoreRecyclerView;

@ContentView(R.layout.fragment_my_calendars)
public class MyCalendarsFragment extends BaseFragment implements PullLoadMoreRecyclerView.PullLoadMoreListener {

    @ViewInject(R.id.srv_chat_poi_list)
    private PullLoadMoreRecyclerView mSrvCalendarList;

    private RecyclerView mRecyclerView;
    private RecyclerCalendarAdapter mAdapter;
    private List<CalendarInfo> mDataList = new ArrayList<>();
    private List<CalendarInfo> mSearchList = new ArrayList<>();
    private int mPageCount = 1;

    public static final String ACTION_REFRESH_CALENDAR_LIST = "cc.emw.mobile.refresh_calendar_list";

    private MyBroadcastReceive mReceive;

    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_CALENDAR_LIST.equals(action)) {
                mPageCount = 1;
                getPersonsCalendar(mPageCount);
            } else if (MyInfoFragment4.ACTION_REFRESH_SEARCH_FRAGMENT4.equals(action)) {
                int currentItem = intent.getIntExtra("currentItem", -1);
                String key = intent.getStringExtra("keyword");
                if (currentItem == 0) {
                    getSearchData(key);
                }
            } else if (MyInfoFragment4.ACTION_REFRESH_SEARCH_FRAGMENT4_BACK.equals(action)) {
                int currentItem = intent.getIntExtra("currentItem", -1);
                String key = intent.getStringExtra("keyword");
                if (TextUtils.isEmpty(key) && currentItem == 0) {
                    mAdapter.setData(mDataList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public MyCalendarsFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mReceive = new MyBroadcastReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_CALENDAR_LIST);
        filter.addAction(MyInfoFragment4.ACTION_REFRESH_SEARCH_FRAGMENT4);
        filter.addAction(MyInfoFragment4.ACTION_REFRESH_SEARCH_FRAGMENT4_BACK);
        getActivity().registerReceiver(mReceive, filter);
        initRecyclerView();
        getPersonsCalendar(mPageCount);

    }

    private void initRecyclerView() {
        mRecyclerView = mSrvCalendarList.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        mRecyclerView.setVerticalScrollBarEnabled(true);
        //设置下拉刷新是否可见
        //mSrvCalendarList.setRefreshing(true);
        //设置是否可以上拉刷新
        //mSrvCalendarList.setPushRefreshEnable(false);
        //显示/隐藏下拉刷新
        mSrvCalendarList.setRefreshing(false);
        //设置是否可以下拉刷新
        mSrvCalendarList.setPullRefreshEnable(false);
        //设置上拉刷新文字
        mSrvCalendarList.setFooterViewText("加载中...");
        //设置上拉刷新文字颜色
        //mSrvCalendarList.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        mSrvCalendarList.setFooterViewBackgroundColor(R.color.white);
        mSrvCalendarList.setLinearLayout();
        mSrvCalendarList.setOnPullLoadMoreListener(this);
        mAdapter = new RecyclerCalendarAdapter(getActivity());
        mSrvCalendarList.setAdapter(mAdapter);
    }

    /**
     * 获取当前用户近两个月的日程
     *
     * @param mPageCount
     */
    private void getPersonsCalendar(final int mPageCount) {
        API.TalkerAPI.GetCalenderByPage(1, mPageCount, 10, new RequestCallback<CalendarInfo>(CalendarInfo.class) {

            @Override
            public void onError(Throwable throwable, boolean b) {
                mSrvCalendarList.setPullLoadMoreCompleted();
                ToastUtil.showToast(getActivity(), "获取用户日程失败!");
            }

            @Override
            public void onParseSuccess(final List<CalendarInfo> respList) {
                if (respList != null && respList.size() > 0) {
                    mSrvCalendarList.setPullLoadMoreCompleted();
                    if (mPageCount == 1)
                        mDataList.clear();
                    mDataList.addAll(respList);
                    mAdapter.setData(mDataList);
                    mAdapter.notifyDataSetChanged();
                    if (respList.size() < 10) {
                        mSrvCalendarList.setPushRefreshEnable(false);
                    } else {
                        mSrvCalendarList.setPushRefreshEnable(true);
                    }
                }
            }
        });
    }

    /**
     * 根据关键字搜索相关日程
     *
     * @param key
     */
    private void getSearchData(String key) {
        API.TalkerAPI.GetCalenderByKey(key, 1, new RequestCallback<CalendarInfo>(CalendarInfo.class) {

            @Override
            public void onParseSuccess(List<CalendarInfo> respList) {
                if (respList != null) {
                    mSearchList.clear();
                    mSearchList.addAll(respList);
                    mAdapter.setData(mSearchList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        mPageCount++;
        getPersonsCalendar(mPageCount);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceive != null) {
            getActivity().unregisterReceiver(mReceive);
        }
    }
}
