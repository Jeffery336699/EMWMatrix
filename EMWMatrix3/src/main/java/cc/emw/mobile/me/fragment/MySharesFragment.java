package cc.emw.mobile.me.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.dynamic.adapter.DynamicAdapter;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;

@ContentView(R.layout.fragment_my_shares)
public class MySharesFragment extends BaseFragment {

    @ViewInject(R.id.mlv)
    private ListView mListView;
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多

    private DynamicAdapter dynamicAdapter;
    private List<UserNote> mDataList = new ArrayList<>();
    private List<UserNote> mSearchList = new ArrayList<>();
    public static final String ACTION_REFRESH_SHARE_LIST = "cc.emw.mobile.refresh_share_list";

    private MyBroadcastReceive mReceive;

    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_SHARE_LIST.equals(action)) {
                mPageCount = 1;
                getDataList("", mPageCount);
            } else if (MyInfoFragment4.ACTION_REFRESH_SEARCH_FRAGMENT4.equals(action)) {
                int currentItem = intent.getIntExtra("currentItem", -1);
                String key = intent.getStringExtra("keyword");
                if (currentItem == 2) {
                    getDataList(key, mPageCount);
                }
            } else if (MyInfoFragment4.ACTION_REFRESH_SEARCH_FRAGMENT4_BACK.equals(action)) {
                int currentItem = intent.getIntExtra("currentItem", -1);
                String key = intent.getStringExtra("keyword");
                if (TextUtils.isEmpty(key) && currentItem == 2) {
                    dynamicAdapter.setDataList((ArrayList<UserNote>) mDataList);
                    dynamicAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private int mPageCount = 1;

    public MySharesFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mReceive = new MyBroadcastReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_SHARE_LIST);
        filter.addAction(MyInfoFragment4.ACTION_REFRESH_SEARCH_FRAGMENT4);
        filter.addAction(MyInfoFragment4.ACTION_REFRESH_SEARCH_FRAGMENT4_BACK);
        getActivity().registerReceiver(mReceive, filter);

        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(true);
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mPageCount++;
                getDataList("", mPageCount);
            }
        });

        dynamicAdapter = new DynamicAdapter(getActivity(), (ArrayList<UserNote>) mDataList);
        mListView.setAdapter(dynamicAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mListView.setNestedScrollingEnabled(true);
        }

        getDataList("", mPageCount);

    }

    @Override
    public void onFirstUserVisible() {
//        getDataList(mPageCount);
    }

    private void getDataList(final String key, final int mPageCount) {
        API.TalkerAPI.getShareTalkerBySelf(key, mPageCount, 5, new RequestCallback<UserNote>(UserNote.class) {

            @Override
            public void onParseSuccess(List<UserNote> respList) {
                if (respList != null && respList.size() > 0) {
                    if (key != null && !TextUtils.isEmpty(key)) {
                        mSearchList.clear();
                        mSearchList.addAll(respList);
                        dynamicAdapter.setDataList((ArrayList<UserNote>) mSearchList);
                        dynamicAdapter.notifyDataSetChanged();
                    } else {
                        if (respList.size() < 5) {
                            loadMoreListViewContainer.loadMoreFinish(false, false);// load more
                        } else {
                            loadMoreListViewContainer.loadMoreFinish(false, true);
                        }
                        if (mPageCount == 1)
                            mDataList.clear();
                        mDataList.addAll(respList);
                        dynamicAdapter.setDataList((ArrayList<UserNote>) mDataList);
                        dynamicAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceive != null) {
            getActivity().unregisterReceiver(mReceive);
        }
    }
}
