package cc.emw.mobile.contact.fragment;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.GroupsCreateActivity;
import cc.emw.mobile.contact.adapter.GroupAdapters;
import cc.emw.mobile.contact.presenter.ContactPresenter;
import cc.emw.mobile.contact.view.ContactView;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.DisplayUtil;

/**
 * @author zrjt
 * @version 2016-3-8 下午3:42:47
 */
@ContentView(R.layout.fragment_group)
public class GroupFragment extends BaseFragment implements ContactView, OnClickListener {

    // @ViewInject(R.id.load_more_list_view_ptr_frame)
    // private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 小组列表
    private ArrayList<GroupInfo> mDataList;
    private GroupAdapters groupAdapter;
    private ContactPresenter contactPresenter;
    public static final String ACTION_REFRESH_GROUP = "cc.emw.mobile.refresh.group";
    private MyBroadcastReceiver receicer;

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction() == ACTION_REFRESH_GROUP) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactPresenter = new ContactPresenter(this);

        mPtrFrameLayout = (PtrFrameLayout) view
                .findViewById(R.id.load_more_list_view_ptr_frame);

        mDataList = new ArrayList<GroupInfo>();

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
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);

        refresh();
        init();
    }

    private void init() {
        IntentFilter intentFilter = new IntentFilter(ACTION_REFRESH_GROUP);
        receicer = new MyBroadcastReceiver();
        getActivity().registerReceiver(receicer, intentFilter); // 注册监听
    }

    @Override
    public void onFirstUserVisible() {
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(true);
            }
        }, 100);
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
                contactPresenter.getGroupList();
            }
        });
    }

    @Override
    public void disProgressDialog() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void refreshComplete() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void showFollowResult(String result) {

    }

    @Override
    public void showTipDialog(String tips) {

    }

    @Override
    public void showUserInfo(List<UserInfo> simpleUsers) {

    }

    @Override
    public void showGroupInfo(List<GroupInfo> groupInfos) {
        mDataList.clear();
        ArrayList<GroupInfo> groupInfos2 = new ArrayList<GroupInfo>();
        for (int i = 0; i < groupInfos.size(); i++) {
            if (groupInfos.get(i).IsAddIn) {
                GroupInfo info = groupInfos.get(i);
                GroupInfo gInfo = new GroupInfo(info.Count,
                        info.CreateTime, info.CreateUser,
                        info.CreateUserName, info.ID,
                        info.Image, info.IsAddIn, info.Memo,
                        info.Name, info.Type, info.Users, true);
                groupInfos2.add(gInfo);
            }
        }
        mDataList.addAll(groupInfos2);
        mDataList.addAll(groupInfos);
        groupAdapter = new GroupAdapters(getActivity());
        groupAdapter.setData(mDataList);
        groupAdapter.notifyDataSetChanged();
        mListView.setAdapter(groupAdapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(),
                GroupsCreateActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        if (receicer != null)
            getActivity().unregisterReceiver(receicer); // 取消监听
        super.onDestroy();
    }
}
