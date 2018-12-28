package cc.emw.mobile.contact;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.util.AnimUtils;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.me.adapter.WaitScheduleAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

@ContentView(R.layout.activity_relations)
public class RelationCalendarActivity extends BaseActivity {

    @ViewInject(R.id.root_view)
    private LinearLayout mRootView;
    @ViewInject(R.id.root_view2)
    private LinearLayout mRootView2;
    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条发布

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 我相关的列表

    @ViewInject(R.id.ll_task_blank)
    private LinearLayout mLlBlank;// 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;// 无网络
    @ViewInject(R.id.img_relation_blank_tag)
    private ImageView imgBlankTips;
    @ViewInject(R.id.tv_relation_blank_tag)
    private TextView tvBlankTips;

    private WaitScheduleAdapter adapter;
    private ArrayList<CalendarInfo> mDataList; // 列表数据
    private int page = PAGE_FIRST; // 第几页，第1页为1，每下一页+1
    private static final int PAGE_FIRST = 1; // 第1页
    private static final int PAGE_COUNT = 10; // 页数
    private UserInfo userInfo;
    private GroupInfo groupInfo;
    private Files fileInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("user_info")) {
            userInfo = (UserInfo) getIntent().getSerializableExtra("user_info");
        } else if (getIntent().hasExtra("group_info")) {
            groupInfo = (GroupInfo) getIntent().getSerializableExtra("group_info");
        } else if (getIntent().hasExtra("file_info")) {
            fileInfo = (Files) getIntent().getSerializableExtra("file_info");
        }

        initView();

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    private void initView() {
        mHeaderTitleTv.setText(R.string.relationcalendar);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
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
        mDataList = new ArrayList<CalendarInfo>();
        adapter = new WaitScheduleAdapter(this, true);
        adapter.setData(mDataList);
        mListView.setAdapter(adapter);

        refresh();
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
                if (userInfo != null) {
                    getScheduleByUserId(userInfo.ID);
                } else if (groupInfo != null) {
                    getScheduleByGroupId(groupInfo.ID);
                } else if (fileInfo != null) {
                    getScheduleByFileId(fileInfo.ID);
                }
            }
        });
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

    private void getScheduleByUserId(int uid) {
        API.TalkerAPI.GetAllCalenderListByUserId(uid, new RequestCallback<CalendarInfo>(CalendarInfo.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationCalendarActivity.this, R.string.relationcalendar_list_error);
            }

            @Override
            public void onParseSuccess(List<CalendarInfo> respList) {
                if (respList.size() > 0) {
                    // 有数据
                    Collections.sort(respList);
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mPtrFrameLayout.refreshComplete();
                    mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.notifyDataSetChanged();
                } else {
                    mListView.setVisibility(View.GONE);
                    mLlBlank.setVisibility(View.VISIBLE);
                    imgBlankTips.setImageResource(R.drawable.blank_ico_richeng);
                    tvBlankTips.setText(getString(R.string.tv_calender_relation_tips));
                    mPtrFrameLayout.refreshComplete();
                }
            }
        });
    }

    private void getScheduleByGroupId(int gid) {
        API.TalkerAPI.GetScheduleByGroupId(gid, new RequestCallback<CalendarInfo>(CalendarInfo.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationCalendarActivity.this, R.string.relationcalendar_list_error);
            }

            @Override
            public void onParseSuccess(List<CalendarInfo> respList) {
                if (respList.size() > 0) {
                    // 有数据
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mPtrFrameLayout.refreshComplete();
                    mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.notifyDataSetChanged();
                } else {
                    mListView.setVisibility(View.GONE);
                    mLlBlank.setVisibility(View.VISIBLE);
                    imgBlankTips.setImageResource(R.drawable.blank_ico_richeng);
                    tvBlankTips.setText(getString(R.string.tv_calender_relation_tips));
                    mPtrFrameLayout.refreshComplete();
                }
            }
        });
    }

    private void getScheduleByFileId(int fid) {
        API.TalkerAPI.GetScheduleByFileId(fid, new RequestCallback<CalendarInfo>(CalendarInfo.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationCalendarActivity.this, R.string.relationcalendar_list_error);
            }

            @Override
            public void onParseSuccess(List<CalendarInfo> respList) {
                mPtrFrameLayout.refreshComplete();
                mDataList.clear();
                mDataList.addAll(respList);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
