package cc.emw.mobile.me;

import android.content.Intent;
import android.graphics.Color;
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
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.contact.adapter.ConcernAdapter;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 我·关注
 *
 * @author zrjt
 * @version 2016-3-14 下午3:12:09
 */
@ContentView(R.layout.activity_concern)
public class ConcernActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 关注列表

    @ViewInject(R.id.ll_concern_blank)
    private LinearLayout mBlankLayout;// 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;// 无网络
    private ConcernAdapter concernAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
        initView();
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    private void initView() {
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("粉丝列表");
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
                getMyFenSi(PrefsUtil.readUserInfo().ID);
                getOnlineList();
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
                Intent noticeIntent = new Intent(this, TestActivity.class);
                this.startActivity(noticeIntent);
                break;
        }
    }

    private void getMyFenSi(int userId) {
        API.TalkerAPI.GetMyFans(userId, new RequestCallback<UserInfo>(UserInfo.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(ConcernActivity.this, "加载粉丝列表失败");
            }

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                mPtrFrameLayout.refreshComplete();
                if (respList != null && respList.size() > 0) {
                    concernAdapter.setDataList((ArrayList<UserInfo>) respList);
                    concernAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 获取在线人员列表
     */
    private void getOnlineList() {
        API.Message.GetOnlines(new RequestCallback<Integer>(Integer.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                mPtrFrameLayout.refreshComplete();
//                ToastUtil.showToast(getActivity(), "获取在线人员失败");
            }

            @Override
            public void onParseSuccess(List<Integer> respInfo) {
                mPtrFrameLayout.refreshComplete();
                if (respInfo != null) {
                    concernAdapter.setOnLineList(respInfo);
                    concernAdapter.setOnLineList(respInfo);
                }
            }
        });
    }
}
