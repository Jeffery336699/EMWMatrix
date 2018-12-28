package cc.emw.mobile.me.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.me.adapter.WaitScheduleAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 我·待办工作》日程Fragment
 *
 * @author zrjt
 */
@ContentView(R.layout.fragment_calendar_handle)
public class WaitHandleCalendarFragment extends BaseFragment {

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 未处理工作列表

    @ViewInject(R.id.ll_calendar_blank)
    private LinearLayout mBlankLayout;

    private WaitScheduleAdapter adapter; // 未处理工作adapter
    private ArrayList<CalendarInfo> mDataList; // 未处理工作列表数据

    private static final int PAGE_FIRST = 1; // 第1页
    private int page = PAGE_FIRST;
    private static final int PAGE_COUNT = 10; // 页数
    public static final String ACTION_REFRESH_CALENDAR_HANDLE = "cc.emw.mobile.refresh.calendar.handle";
    private MyBroadcastReceive receive;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_CALENDAR_HANDLE);
        receive = new MyBroadcastReceive();
        getActivity().registerReceiver(receive, filter);

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    private void initView() {
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                // here check list view, not content.
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getWaitList(1, page, 10);
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

        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(false);

        mDataList = new ArrayList<CalendarInfo>();
        adapter = new WaitScheduleAdapter(getActivity(), true);
        adapter.setData(mDataList);
        mListView.setAdapter(adapter);
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                page++;
                getWaitList(1, page, 10);
            }
        });
    }

    class MyBroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_CALENDAR_HANDLE.equals(action)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    private void getWaitList(int state, int s, int size) {
        API.TalkerAPI.GetCalenderByPage(state, s, size,
                new RequestCallback<CalendarInfo>(CalendarInfo.class) {

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mPtrFrameLayout.refreshComplete();
                        if (page > 0) {
                            page--;
                        }
                    }

                    @Override
                    public void onParseSuccess(List<CalendarInfo> respList) {
                        mPtrFrameLayout.refreshComplete();
                        if (respList != null && respList.size() > 0) {
                            mBlankLayout.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                            if (respList.size() < PAGE_COUNT) {
                                loadMoreListViewContainer.loadMoreFinish(false, false);// load more
                            } else {
                                loadMoreListViewContainer.loadMoreFinish(false, true);
                            }
                            if (page == PAGE_FIRST)
                                mDataList.clear();
                            mDataList.addAll(respList);
                            adapter.setData(mDataList);
                            adapter.notifyDataSetChanged();
                        } else {
                            if (page == 1) {
                                mBlankLayout.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                            } else {
                                loadMoreListViewContainer.loadMoreFinish(false, false);
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        if (receive != null)
            getActivity().unregisterReceiver(receive);
        super.onDestroyView();

    }
}
