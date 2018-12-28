package cc.emw.mobile.project.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.adapter.TaskAdapter;
import cc.emw.mobile.project.adapter.TeamMemberAdapter;
import cc.emw.mobile.project.adapter.TeamMemberAdapter2;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.task.activity.TaskCreateActivity;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by jven.wu on 2016/6/23.
 * 查看团队
 */
@ContentView(R.layout.activity_observe_project3)
public class ObserveProjectActivity extends BaseActivity implements IObserveProjectActivity {

    public static final String EXTRA_PROJECT = "project";
    public static final String EXTRA_PROJECT_ID = "projectid";
    public static final String EXTRA_PROJECT_NAME = "Projectname";
    public static final String EXTRA_GROUP_NAME = "groupName";
    public static final String EXTRA_GROUP_MEMBERS = "groupmembers";
    public static final String BROADCAST_PROJECT_TASK_REFRESH = "broadcast_project_task_refresh";

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;    //顶部标题
    @ViewInject(R.id.cm_header_btn_more)
    private ImageButton mHeaderMoreBtn; // 顶部条编辑按钮

    @ViewInject(R.id.ll_teamdetail_newtask) private LinearLayout mNewTaskLayout; //新建任务Layout
    @ViewInject(R.id.load_more_list_view_ptr_frame) private PtrFrameLayout mPtrFrameLayout; //下拉刷新
    @ViewInject(R.id.scrollview) private ScrollView scrollview;
    @ViewInject(R.id.tv_teamdetail_tasknum) private TextView mTaskNumTv; //任务数量
    @ViewInject(R.id.lv_teamdetail_task) private ListView mTaskListView; //任务列表
    @ViewInject(R.id.civ_teamdetail_creatorhead) private CircleImageView mCreatorHeadIv; //创建者头像
    @ViewInject(R.id.tv_teamdetail_creatorname) private TextView mCreatorNameTv; //创建者姓名
    @ViewInject(R.id.ll_teamdetail_member) private LinearLayout mMemberLayout;
    @ViewInject(R.id.tv_teamdetail_membernum) private TextView mMemberNumTv; //成员数量
    @ViewInject(R.id.itv_teamdetail_memberarrow) private IconTextView mMoreMemberBtn; //查看更多成员按钮
    @ViewInject(R.id.gv_teamdetail_member) private GridView gridView;// 团队成员头像列表
    @ViewInject(R.id.ll_network_tips) private LinearLayout mNetworkErrorPage; //网络错误显示

    private DisplayImageOptions options;
    private TaskAdapter adapter;
    private TeamMemberAdapter2 memberAdapter;
    private MyBroadcastReceive receive;
    private ProjectPresenter presenter = new ProjectPresenter(this);
    private int projectId;
    private String projectName;
    private ApiEntity.UserProject project;
    private ArrayList<ApiEntity.UserInfo> memberList = new ArrayList<>(); //团队人员列表集
    private ArrayList<Integer> groupmembers; //圈子人员id集
    private String groupName; //圈子名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        initView();

    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_btn_more, R.id.ll_network_tips,
            R.id.ll_teamdetail_newtask, R.id.ll_teamdetail_member, R.id.ll_teamdetail_chat})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_btn_more:
                Intent modifyProject = new Intent(this, NewProjectActivity.class);
                modifyProject.putExtra("project", project);
                modifyProject.putExtra("groupName", groupName);
                modifyProject.putIntegerArrayListExtra("groupmembers", groupmembers);
                modifyProject.putExtra("start_anim", false);
                startActivity(modifyProject);
                break;
            case R.id.ll_network_tips:
                mPtrFrameLayout.autoRefresh(false);
                break;
            case R.id.ll_teamdetail_newtask:
                Intent taskIntent = new Intent(this, TaskCreateActivity.class);
                taskIntent.putExtra(TaskCreateActivity.TEAM_USERPROJECT,project);
                taskIntent.putExtra("start_anim", false);
                startActivity(taskIntent);
                break;
            case R.id.ll_teamdetail_member:
                Intent intent = new Intent(this, TeamMemberActivity.class);
//                intent.putIntegerArrayListExtra("members", members);
                intent.putExtra("member_list", memberList);
                intent.putIntegerArrayListExtra("groupmembers",groupmembers);
                intent.putExtra("project",project);
                intent.putExtra("isFromProject",true);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivity(intent);
                break;
            case R.id.ll_teamdetail_chat:
                Intent chatIntent = new Intent(this, ChatActivity.class);
                chatIntent.putExtra("GroupID", project.TeamId);
                chatIntent.putExtra("name", groupName);
                chatIntent.putExtra("type", 2);
                startActivity(chatIntent);
                break;
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        projectId = getIntent().getIntExtra(EXTRA_PROJECT_ID, 0);
        project = (ApiEntity.UserProject) getIntent().getSerializableExtra(EXTRA_PROJECT);
        groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
        groupmembers = getIntent().getIntegerArrayListExtra(EXTRA_GROUP_MEMBERS);

        if (projectId == 0) {
            projectId = project.ID;
        }
        projectName = project.Name;

        mHeaderTitleTv.setText(projectName);
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        if (groupmembers != null && PrefsUtil.readUserInfo().ID == project.Creator) {
            mHeaderMoreBtn.setVisibility(View.VISIBLE);
            mNewTaskLayout.setVisibility(View.VISIBLE);
            mMoreMemberBtn.setVisibility(View.VISIBLE);
            mMemberLayout.setClickable(true);
        }else{
            mHeaderMoreBtn.setVisibility(View.INVISIBLE);
            mMoreMemberBtn.setVisibility(View.GONE);
            mMemberLayout.setClickable(false);
        }

        setRefresh();
        adapter = new TaskAdapter(this);
        mTaskListView.setAdapter(adapter);
        mTaskListView.setSelector(R.drawable.selector_transparent);
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApiEntity.UserFenPai task = (ApiEntity.UserFenPai) adapter.getItem(position);
                Intent taskDetailIntent = new Intent(ObserveProjectActivity.this, TaskDetailActivity.class);
                taskDetailIntent.putExtra(TaskDetailActivity.TASK_DETAIL,task);
                taskDetailIntent.putExtra(TaskCreateActivity.WHO_CAN_SEE,false);
                taskDetailIntent.putExtra(TaskCreateActivity.TEAM_USERPROJECT,project);
                taskDetailIntent.putExtra("start_anim", false);
                startActivity(taskDetailIntent);
            }
        });

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        if (EMWApplication.personMap != null && EMWApplication.personMap.get(project.Creator) != null) {
            UserInfo user = EMWApplication.personMap.get(project.Creator);
            mCreatorNameTv.setText(user.Name);
            String uri2 = String.format(Const.DOWN_ICON_URL, user.CompanyCode, user.Image);
            ImageLoader.getInstance().displayImage(uri2, mCreatorHeadIv, options);
        }

        memberAdapter = new TeamMemberAdapter2(this, TeamMemberAdapter.TYPE_GRID);

        if (project.MainUserList != null) {
            memberList.addAll(project.MainUserList);
        }
        if (project.UsersList != null) {
            memberList.addAll(project.UsersList);
        }
        memberAdapter.setData(memberList);
        gridView.setAdapter(memberAdapter);


        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 300);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ObserveProjectActivity.BROADCAST_PROJECT_TASK_REFRESH);
        intentFilter.addAction(NewProjectActivity.BROADCAST_PROJECT_REFRESH);
        receive = new MyBroadcastReceive();
        this.registerReceiver(receive,intentFilter);
    }

    @Override
    public void onError(Throwable ex) {
        if (ex instanceof ConnectException) {
            mNetworkErrorPage.setVisibility(View.VISIBLE);
        } else {
            ToastUtil.showToast(ObserveProjectActivity.this, ex.getMessage());
        }
        mPtrFrameLayout.refreshComplete();
    }

    /**
     * 设置【下拉刷新】和【加载更多】功能
     */
    private void setRefresh() {
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

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        scrollview, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                presenter.getTasksOfProject(projectId+"");
            }
        });
    }

    @Override
    public void renderView(ArrayList<ApiEntity.UserFenPai> tasks) {
        adapter.setData(tasks);
        adapter.notifyDataSetChanged();
        mTaskNumTv.setText(tasks.size() + "个任务");
        mPtrFrameLayout.refreshComplete();
    }

    //接收广播
    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();

            if (ObserveProjectActivity.BROADCAST_PROJECT_TASK_REFRESH.equals(actionStr)) {
                mPtrFrameLayout.autoRefresh(false);
            }

            if (NewProjectActivity.BROADCAST_PROJECT_REFRESH.equals(actionStr)) {

                ApiEntity.UserProject tempProject = (ApiEntity.UserProject)intent.getSerializableExtra("project");
                if(tempProject != null){
                    project = tempProject;
                    mHeaderTitleTv.setText(project.Name);
                    memberList.clear();
                    if (project.MainUserList != null) {
                        memberList.addAll(project.MainUserList);
                    }
                    if (project.UsersList != null) {
                        memberList.addAll(project.UsersList);
                    }
                    memberAdapter.setData(memberList);
                    memberAdapter.notifyDataSetChanged();
                    mMemberNumTv.setText(memberList.size() + "人");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receive != null)
            unregisterReceiver(receive);
    }
}
