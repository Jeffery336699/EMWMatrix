package cc.emw.mobile.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.adapter.PersonAllAdapter;
import cc.emw.mobile.entity.UserAllGroup;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PersonnelComparator;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

@ContentView(R.layout.activity_all_person)
public class AllPersonActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.elv_all_person)
    private ExpandableListView mListView;

    private List<UserAllGroup> mDataList;
    private PersonAllAdapter allAdapter;
    public static final String ACTION_REFRESH_CONTACT_LIST = "cc.emw.mobile.refresh_all_list"; // 刷新的action
    private MyBroadcastReceive mReceive;

    class MyBroadcastReceive extends BroadcastReceiver {
        // TODO
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_CONTACT_LIST.equals(action)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    private void init() {
        mTitle.setText("查找人员");
        mReceive = new MyBroadcastReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_CONTACT_LIST);
        registerReceiver(mReceive, filter);
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
                mDataList.clear();
                getAllUser("");
            }
        });

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mDataList = new ArrayList<>();
        allAdapter = new PersonAllAdapter(this);
        mListView.setAdapter(allAdapter);
    }

    @Event(value = {R.id.cm_header_btn_left})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
        }
    }

    private void getAllUser(String keyword) {
        API.UserAPI.SearchUser(keyword, 0, false,
                new RequestCallback<UserInfo>(UserInfo.class) {

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mPtrFrameLayout.refreshComplete();
                        ToastUtil.showToast(AllPersonActivity.this, "获取所有人员列表失败");
                    }

                    @Override
                    public void onParseSuccess(List<UserInfo> respList) {
                        mPtrFrameLayout.refreshComplete();
                        if (respList != null && respList.size() > 0) {
                            UserInfo lastUser = new UserInfo();
                            lastUser.DeptID = Integer.MAX_VALUE;
                            respList.add(lastUser);
                            PersonnelComparator<UserInfo> personnelComparator = new PersonnelComparator<UserInfo>();
                            Collections.sort(respList, personnelComparator);
                            List<UserInfo> groupUserInfos = new ArrayList<UserInfo>();
                            UserAllGroup userAllGroup = new UserAllGroup();
                            for (int i = 0; i < respList.size(); i++) {
                                UserInfo userInfo = respList.get(i);
                                if (i > 0 && respList.get(i - 1).DeptID == userInfo.DeptID) {
                                    groupUserInfos.add(userInfo);
                                } else {
                                    if (i > 0) {
                                        groupUserInfos.add(respList.get(i - groupUserInfos.size() - 1));
                                        userAllGroup.gName = respList.get(i - 1).DeptName;
                                        if (TextUtils.isEmpty(userAllGroup.gName)) {
                                            userAllGroup.gName = "其他";
                                        }
                                        /*Set<UserInfo> userInfoSet = new HashSet<UserInfo>();
                                        userInfoSet.addAll(groupUserInfos);
                                        groupUserInfos.clear();
                                        groupUserInfos.addAll(userInfoSet);*/
                                        userAllGroup.userInfos = groupUserInfos;
                                        userAllGroup.num = groupUserInfos.size();
                                        mDataList.add(userAllGroup);
                                        userAllGroup = new UserAllGroup();
                                        groupUserInfos = new ArrayList<>();
                                    }
                                }
                            }
                            allAdapter.setData(mDataList);
                            allAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceive != null) {
            unregisterReceiver(mReceive);
        }
    }
}
