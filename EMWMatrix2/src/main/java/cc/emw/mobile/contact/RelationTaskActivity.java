package cc.emw.mobile.contact;

import cc.emw.mobile.net.RequestCallback;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.me.adapter.WaitTaskAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;

@ContentView(R.layout.activity_relations)
public class RelationTaskActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
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

    private ArrayList<UserFenPai> mDataList; // 列表数据
    private int page = PAGE_FIRST; // 第几页，第1页为1，每下一页+1
    private static final int PAGE_FIRST = 1; // 第1页
    private static final int PAGE_COUNT = 10; // 页数
    private WaitTaskAdapter adapter; // 任务适配器
    private UserInfo userInfo;
    private GroupInfo groupInfo;
    private Files fileInfo;

    public static final String CALENDER_TASK = "calender_task";// 接收到日程界面的传过来的任务ID
    private String mStringTasks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataList = new ArrayList<UserFenPai>();
        if (getIntent().hasExtra("user_info")) {
            userInfo = (UserInfo) getIntent().getSerializableExtra("user_info");
        } else if (getIntent().hasExtra("group_info")) {
            groupInfo = (GroupInfo) getIntent().getSerializableExtra(
                    "group_info");
        } else if (getIntent().hasExtra("file_info")) {
            fileInfo = (Files) getIntent().getSerializableExtra("file_info");
        } else if (getIntent().hasExtra(CALENDER_TASK)) {
            mStringTasks = StringUtils.replaceBlank(getIntent().getStringExtra(CALENDER_TASK));
            getTaskById();
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
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.relationtask);

        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
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
        adapter = new WaitTaskAdapter(this, true);
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
                if (getIntent().hasExtra(CALENDER_TASK)) {
                    getTaskById();
                } else {
                    if (userInfo != null) {
                        getTaskByUserId(userInfo.ID);
                    } else if (groupInfo != null) {
                        getTaskByGroupId(groupInfo.ID);
                    } else if (fileInfo != null) {
                        getTaskByFileId(fileInfo.ID);
                    }
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

    private void getTaskByUserId(int uid) {
        API.TalkerAPI.GetTaskByUserId(uid, new RequestCallback<UserFenPai>(
                UserFenPai.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationTaskActivity.this, R.string.relationtask_list_error);
            }

            @Override
            public void onParseSuccess(List<UserFenPai> respList) {
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
                    mPtrFrameLayout.refreshComplete();
                }
            }
        });
    }

    private void getTaskByGroupId(int gid) {
        API.TalkerAPI.GetTaskListByGroupId(gid,
                new RequestCallback<UserFenPai>(UserFenPai.class) {
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mPtrFrameLayout.refreshComplete();
                        ToastUtil.showToast(RelationTaskActivity.this, R.string.relationtask_list_error);
                    }

                    @Override
                    public void onParseSuccess(List<UserFenPai> respList) {
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
                            mPtrFrameLayout.refreshComplete();
                        }
                    }
                });
    }

    private void getTaskByFileId(int fid) {
        API.TalkerAPI.GetTaskByFileId(fid, new RequestCallback<UserFenPai>(
                UserFenPai.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationTaskActivity.this, R.string.relationtask_list_error);
            }

            @Override
            public void onParseSuccess(List<UserFenPai> respList) {
                mPtrFrameLayout.refreshComplete();
                mDataList.clear();
                mDataList.addAll(respList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 通过任务ID串获取任务实体
     */
    private void getTaskById() {
        if (mStringTasks != null && !mStringTasks.equals("")
                && !mStringTasks.equals("[]")) {
            // 有数据
            mLlBlank.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);

            String taskIds = mStringTasks.substring(1, mStringTasks.length() - 1);
            API.TalkerAPI.GetTaskByIds(taskIds,
                    new RequestCallback<UserFenPai>(UserFenPai.class) {
                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            mPtrFrameLayout.refreshComplete();
                            if (ex instanceof ConnectException) {
                                mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态
                                // 展示视图
                            }
                        }

                        @Override
                        public void onParseSuccess(List<UserFenPai> tasks) {
                            mPtrFrameLayout.refreshComplete();
                            mNetworkTipsLayout.setVisibility(View.GONE);// 隐藏无网络视图
                            mDataList.clear();
                            mDataList.addAll(tasks);
                            adapter.notifyDataSetChanged();
                        }
                    });
        } else {
            // 无数据 展示空视图
            mPtrFrameLayout.refreshComplete();
            mLlBlank.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }

    }
}
