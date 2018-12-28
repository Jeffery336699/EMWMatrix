package cc.emw.mobile.task.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.view.NewProjectActivity;
import cc.emw.mobile.task.adapter.WorkProjectAdapter;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.IWorkProjectView;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 选择团队协作
 */
@ContentView(R.layout.activity_task_work_project_3)
public class WorkProjectActivity extends BaseActivity implements IWorkProjectView {
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.lv_task_work_project)
    private ListView mListView;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_bar)
    private LinearLayout cm_header_bar;
    @ViewInject(R.id.ll_task_blank)
    private LinearLayout mLlBlank;// 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;// 无网络

    public static final String WORK_PROJECT = "work_project";

    private TaskPresenter mTaskPresenter;
    private WorkProjectAdapter mAdapter;
    private int mProjectID;
    private UserInfo mCurrentUser;//当前登录用户
    private WorkProjectBroadCastReeciver mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        mProjectID = getIntent().getIntExtra(WORK_PROJECT, 0);
        initView();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NewProjectActivity.BROADCAST_PROJECT_REFRESH);
        mReceiver = new WorkProjectBroadCastReeciver();
        registerReceiver(mReceiver, intentFilter);
    }

    private class WorkProjectBroadCastReeciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NewProjectActivity.BROADCAST_PROJECT_REFRESH.equals(intent.getAction())) {
                mPtrFrameLayout.autoRefresh();
            }
        }
    }

    private void initView() {
        /*cm_header_bar.setBackgroundResource(R.drawable.trans_bg);
        mHeaderTitleTv.setText(R.string.task_workproject_relation_project);*/
        ((TextView)findViewById(R.id.cm_header_tv_right9)).setText("确认");

        mCurrentUser = PrefsUtil.readUserInfo();
        mAdapter = new WorkProjectAdapter(this, mProjectID);
        mTaskPresenter = new TaskPresenter(this);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);

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

        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(false);
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
        refresh();
    }

    private void refresh() {
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mTaskPresenter.getAllProjects();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }
        });
    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9,R.id.ll_network_tips,R.id.ll_task_work_project_create})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                if (mAdapter.getClickPosition() == -1) {
                    ToastUtil.showToast(this, "请选择一个团队协作!");
                    return;
                }
                UserProject selectProjecd = mAdapter.getSelectProjecd();
                Intent intent = new Intent();
                intent.putExtra(WORK_PROJECT, selectProjecd);
                setResult(Activity.RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.ll_network_tips:
                mPtrFrameLayout.autoRefresh();
                break;
            case R.id.ll_task_work_project_create:
                Intent createProject = new Intent(this, NewProjectActivity.class);
                createProject.putExtra("start_anim", false);
                startActivity(createProject);
                break;
            default:
                break;
        }
    }

    @Override
    public void showProjects(List<UserProject> projects) {
        ArrayList<UserProject> userProjects = new ArrayList<>();
        //过滤项目  创建者Or负责人的项目
        if (projects != null && projects.size() != 0) {
            for (UserProject up : projects) {
                if (mCurrentUser.ID == up.Creator || TaskUtils.getUsers(up.MainUser).contains(mCurrentUser)) {
                    //如果项目创建人或者执行人是当前用户
                    userProjects.add(up);
                }
            }
        }
        mNetworkTipsLayout.setVisibility(View.GONE);
        if (userProjects.size() == 0) {
            mLlBlank.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mLlBlank.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
        mAdapter.setData(userProjects);
        mAdapter.notifyDataSetChanged();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void completeFresh() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        mPtrFrameLayout.refreshComplete();
        if (ex instanceof ConnectException) {
            mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
            return;
        }
    }

}
