package cc.emw.mobile.me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.me.adapter.ConcernAdapter;
import cc.emw.mobile.me.presenter.MyInfoPresenter;
import cc.emw.mobile.me.view.MyInfoView;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 我·关注
 * @author zrjt
 * @version 2016-3-14 下午3:12:09
 */
@ContentView(R.layout.activity_concern)
public class ConcernActivity extends BaseActivity implements MyInfoView {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn;

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 关注列表

    @ViewInject(R.id.ll_concern_blank)
    private LinearLayout mBlankLayout;// 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;// 无网络

    public static final String ACTION_REFRESH_FOLLOW_LIST = "cc.emw.mobile.refresh_follow_list"; // 刷新的action
    private MyBroadcastReceive mReceive;
    private ArrayList<UserInfo> mDataList;
    private ConcernAdapter concernAdapter;
    private MyInfoPresenter presenter; // 我的模块P处理层

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        IntentFilter intentFilter = new IntentFilter(ACTION_REFRESH_FOLLOW_LIST);
        mReceive = new MyBroadcastReceive();
        registerReceiver(mReceive, intentFilter); // 注册监听

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    private void initView() {
        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("关注列表");
        mHeaderMoreBtn.setImageResource(R.drawable.cm_header_btn_more);
        mHeaderMoreBtn.setVisibility(View.GONE);

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
                presenter.getFollow();
            }
        });

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);

        // binding view and data
        presenter = new MyInfoPresenter(this);
        mDataList = new ArrayList<UserInfo>();
        concernAdapter = new ConcernAdapter(this);
        mListView.setAdapter(concernAdapter);
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                break;
        }
    }

    @Override
    public void showFailureInfo(String tips) {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void showFollowList(List<cc.emw.mobile.entity.UserInfo> simpleUsers) {
        mDataList.clear();
        mDataList.addAll(simpleUsers);
        if (mDataList.size() == 0) {
            mBlankLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mBlankLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
        concernAdapter.setData(mDataList);
        concernAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (mReceive != null)
            unregisterReceiver(mReceive);
        super.onDestroy();

    }

    class MyBroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_FOLLOW_LIST.equals(action)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    @Override
    public void finishRefresh() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void getMyInfoCount(List<Integer> str) {
        // TODO Auto-generated method stub
    }

    @Override
    public void getMyReleaseInfoList(List<UserNote> userNotes) {

    }
}
