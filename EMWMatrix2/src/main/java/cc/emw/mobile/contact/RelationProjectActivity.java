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
import cc.emw.mobile.me.adapter.WaitPlanAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.IWorkProjectView;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;

@ContentView(R.layout.activity_relations)
public class RelationProjectActivity extends BaseActivity implements
        IWorkProjectView {

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

    private ArrayList<UserProject> mDataList; // 列表数据
    private int page = PAGE_FIRST; // 第几页，第1页为1，每下一页+1
    private static final int PAGE_FIRST = 1; // 第1页
    private static final int PAGE_COUNT = 10; // 页数
    private WaitPlanAdapter adapter; // 项目适配器
    private UserInfo userInfo;
    private GroupInfo groupInfo;
    private Files fileInfo;

    public static final String CALENDER_WORK_PORJECT = "calender_porject";// 接收到日程界面的传过来的工作项目ID
    private TaskPresenter mTaskPresenter;
    private String mStringProjects;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskPresenter = new TaskPresenter(this);// 获取工作项目的代理
        mDataList = new ArrayList<UserProject>();
        adapter = new WaitPlanAdapter(this, false);
        if (getIntent().hasExtra("user_info")) {
            userInfo = (UserInfo) getIntent().getSerializableExtra("user_info");
        } else if (getIntent().hasExtra("group_info")) {
            groupInfo = (GroupInfo) getIntent().getSerializableExtra(
                    "group_info");
        } else if (getIntent().hasExtra("file_info")) {
            fileInfo = (Files) getIntent().getSerializableExtra("file_info");
        } else if (getIntent().hasExtra(CALENDER_WORK_PORJECT)) {
            mStringProjects = StringUtils.replaceBlank(getIntent().getStringExtra(CALENDER_WORK_PORJECT));
            getWorkProjects();
        }
        initView();
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    /**
     * 根据项目ID串获取工作项目
     *
     * @param stringExtra
     */
    private void getWorkProjects() {

        mTaskPresenter.getAllProjects();

    }

    @Override
    public void showFinish() {
        mPtrFrameLayout.refreshComplete();// 刷新完毕
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        mPtrFrameLayout.refreshComplete();
        if (ex instanceof ConnectException) {
            mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
        }
    }

    @Override
    public void showProjects(List<UserProject> projects) {
        // 解析数据
        //空视图展示
        mNetworkTipsLayout.setVisibility(View.GONE);
//        if (projects.size() == 0) {
//            mLlBlank.setVisibility(View.VISIBLE);
//            mListView.setVisibility(View.GONE);
//        } else {
//            mLlBlank.setVisibility(View.GONE);
//            mListView.setVisibility(View.VISIBLE);
//        }

        mDataList.clear();

        mPtrFrameLayout.refreshComplete();// 刷新完毕

        if (!mStringProjects.equals("[]")&&!mStringProjects.equals("\"\"")) {
            mLlBlank.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            String[] stringID = TaskUtils.getStringID(mStringProjects);// 解析出ID
            if (projects != null) {
                for (int i = 0; i < stringID.length; i++) {
                    for (int j = 0; j < projects.size(); j++) {
                        if (projects.get(j).ID == Integer.valueOf(stringID[i])) {
                            mDataList.add(projects.get(j));
                        }
                    }
                }
            }
        } else {
            mLlBlank.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }

        adapter.setData(mDataList);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.relationproject);

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
                if (getIntent().hasExtra(CALENDER_WORK_PORJECT)) {
                    getWorkProjects();
                } else {
                    if (userInfo != null) {
                        getProjectByUserId(userInfo.ID);
                    } else if (groupInfo != null) {
                        getProjectByGroupId(groupInfo.ID);
                    } else if (fileInfo != null) {
                        getProjectByFileId(fileInfo.ID);
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

    private void getProjectByUserId(int uid) {
        API.TalkerAPI.GetProjectByUserId(uid, new RequestCallback<UserProject>(
                UserProject.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationProjectActivity.this, R.string.relationproject_list_error);
            }

            @Override
            public void onParseSuccess(List<UserProject> respList) {
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

    private void getProjectByGroupId(int gid) {
        API.TalkerAPI.GetProjectByGroupId(gid,
                new RequestCallback<UserProject>(UserProject.class) {
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mPtrFrameLayout.refreshComplete();
                        ToastUtil.showToast(RelationProjectActivity.this, R.string.relationproject_list_error);
                    }

                    @Override
                    public void onParseSuccess(List<UserProject> respList) {
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

    private void getProjectByFileId(int fid) {
        API.TalkerAPI.GetProjectByFileId(fid, new RequestCallback<UserProject>(
                UserProject.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationProjectActivity.this, R.string.relationproject_list_error);
            }

            @Override
            public void onFinished() {
                mPtrFrameLayout.refreshComplete();
            }

            @Override
            public void onParseSuccess(List<UserProject> respList) {
                if (respList.size() > 0) {
                    // 有数据
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.notifyDataSetChanged();
                } else {
                    mListView.setVisibility(View.GONE);
                    mLlBlank.setVisibility(View.VISIBLE);
                }

            }

        });
    }

}
