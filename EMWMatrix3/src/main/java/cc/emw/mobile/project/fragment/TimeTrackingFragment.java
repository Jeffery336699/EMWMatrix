package cc.emw.mobile.project.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.adapter.HomeProjectAdapter;
import cc.emw.mobile.project.base.PageBean;
import cc.emw.mobile.project.bean.ConstEnum;
import cc.emw.mobile.project.bean.GroupProject;
import cc.emw.mobile.project.bean.ProjectViewBean;
import cc.emw.mobile.project.bean.StateTask;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.IProjectListView;
import cc.emw.mobile.project.view.IProjectStateView;
import cc.emw.mobile.project.view.NewProjectActivity;
import cc.emw.mobile.project.view.NewTeamActivity;
import cc.emw.mobile.project.view.ObserveProjectActivity;
import cc.emw.mobile.project.view.StateBoardActivity;
import cc.emw.mobile.project.view.TaskSpectacularActivity;
import cc.emw.mobile.project.view.TeamActivity;
import cc.emw.mobile.project.view.TimeTrackingMGActivity;
import cc.emw.mobile.task.activity.TaskCreateActivity;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.AnimatedExpandableListView;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import q.rorbin.badgeview.Badge;

/**
 * TimeTracking首页
 * Created by shaobo.zhuang on 2016/11/30.
 */
@ContentView(R.layout.fragment_time_tracking)
public class TimeTrackingFragment extends BaseFragment implements IProjectListView,IProjectStateView {

    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderMenuBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;    //顶部标题
    @ViewInject(R.id.cm_header_btn_right1)
    private IconTextView mHeaderMemberProjectBtn; //项目人员列表
    @ViewInject(R.id.cm_header_btn_right)
    private IconTextView mHeaderNoticeBtn; // 顶部条消息右菜单按钮
    @ViewInject(R.id.cm_header_btn_notice)
    private ImageButton mHeaderNoticeIb;

//    @ViewInject(R.id.load_more_list_view_ptr_frame)
//    private PtrFrameLayout mPtrFrameLayout; //下拉刷新
    @ViewInject(R.id.swipe_refresh)
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @ViewInject(R.id.load_more_small_image_list_view)
    private AnimatedExpandableListView mListView; //
    @ViewInject(R.id.ll_dynamic_blank)
    private LinearLayout mBlankLayout; //空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout; //无网络

    private ListDialog mAddDialog;
    private View mTopLayout;
    private ProjectPresenter presenter; //MVP模式中的主导器
    private ArrayList<StateTask> mStateTasks;
    private Handler mHandler;
    private HomeProjectAdapter adapter;
    private PageBean<GroupProject> mPageBean;
    private MyBroadcastReceive receive;
    private int groupPos, childPos;
    private Badge mBadgeView;
    private DisplayImageOptions options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ProjectPresenter(this);
        mHandler = new Handler();
        mPageBean = new PageBean<>();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NewTeamActivity.BROADCAST_TEAM_REFRESH);
        intentFilter.addAction(NewProjectActivity.BROADCAST_PROJECT_REFRESH);
        intentFilter.addAction(MainActivity.ACTION_UNREAD_COUNT);
        intentFilter.addAction(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
        receive = new MyBroadcastReceive();
        getActivity().registerReceiver(receive, intentFilter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBadgeView = HelpUtil.bindBadgeTarget(getActivity(), mHeaderNoticeIb);
        initView();
        initAddDialog();
    }

    @Override
    public void onDestroy() {
        if (receive != null)
            getActivity().unregisterReceiver(receive);
        super.onDestroy();
    }

    @Event(R.id.ll_network_tips)
    private void onNetworkTipsClick(View v) {
//        mPtrFrameLayout.autoRefresh(false);
        mSwipeRefreshLayout.setRefreshing(true);
        presenter.getProjects(true, false);
        presenter.getTaskByState(0);
    }

    private void initView(){
        mHeaderMenuBtn.setIconText("ec7c");
        mHeaderMenuBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("Time Tracking");
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderMemberProjectBtn.setIconText("ecdc");
        mHeaderMemberProjectBtn.setVisibility(View.VISIBLE);
        mHeaderNoticeBtn.setIconText("ecd5");
        mHeaderNoticeBtn.setVisibility(View.VISIBLE);

        /*mPtrFrameLayout.setToggleMenu(true);
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
                presenter.getProjects(true, false);
                presenter.getTaskByState(0);
            }
        });

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
        mPtrFrameLayout.addPtrUIHandler(header);*/
        mSwipeRefreshLayout.setColorSchemeResources(R.color.ptr_blue, R.color.ptr_green, R.color.ptr_yellow, R.color.ptr_red);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getProjects(true, false);
                presenter.getTaskByState(0);
            }
        });

        mTopLayout = LayoutInflater.from(getActivity()).inflate(R.layout.list_top_content1, null);
        setClickListenerToTopView(R.id.unstart_text, R.id.processing_text, R.id.finished_text, R.id.delay_text);
        mListView.addHeaderView(mTopLayout);

        adapter = new HomeProjectAdapter(getActivity());
        mListView.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mListView.setNestedScrollingEnabled(true);
        }
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (mListView.isGroupExpanded(groupPosition)) {
                    mListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    mListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                /*TaskSpectacularActivity.currentType = TaskSpectacularActivity.PROJECT_TASK;
                TaskSpectacularActivity.currentProjectId =
                        mPageBean.getItems().get(groupPosition).projectViews.get(childPosition).ProjectId + "";
                Intent spectacularIntent = new Intent(getActivity(),TaskSpectacularActivity.class);
                startActivity(spectacularIntent);*/
                TaskSpectacularActivity.currentType = TaskSpectacularActivity.PROJECT_TASK;
                TaskSpectacularActivity.currentProjectId =
                        mPageBean.getItems().get(groupPosition).projectViews.get(childPosition).ProjectId + "";
                Intent spectacularIntent = new Intent(getActivity(),StateBoardActivity.class);
                groupPos = groupPosition;
                childPos = childPosition;
                spectacularIntent.putExtra(TaskCreateActivity.TEAM_USERPROJECT, mPageBean.getItems().get(groupPosition).projectViews.get(childPosition).Project);
                spectacularIntent.putExtra("group_project", mPageBean.getItems().get(groupPosition));
                spectacularIntent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                spectacularIntent.putExtra("click_pos_y", location[1]);
                startActivity(spectacularIntent);
                return false;
            }
        });

        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware((CircleImageView)findView(R.id.cm_header_civ_head)), options, new ImageSize(DisplayUtil.dip2px(getActivity(), 40), DisplayUtil.dip2px(getActivity(), 40)), null, null);
    }

    /**
     * 初始化快捷操作按钮
     */
    private void initAddDialog() {
        mAddDialog = new ListDialog(getActivity(), false);
        mAddDialog.addItem("新增圈子与协作", ConstEnum.Create.CreateTeam);
        mAddDialog.addItem("新增团队协作", ConstEnum.Create.CreateProject);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case ConstEnum.Create.CreateTeam:
                        Intent teamIntent = new Intent(getActivity(), NewTeamActivity.class);
                        teamIntent.putExtra("start_anim", false);
                        startActivity(teamIntent);
                        break;
                    case ConstEnum.Create.CreateProject:
                        Intent projectIntent = new Intent(getActivity(), NewProjectActivity.class);
                        projectIntent.putExtra("start_anim", false);
                        startActivity(projectIntent);
                        break;
                }
            }
        });
    }

    @Override
    public void onFirstUserVisible() {
        /*if (mPtrFrameLayout != null) {
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrameLayout.autoRefresh(false);
                }
            }, 100);
        }*/
        mSwipeRefreshLayout.setRefreshing(true);
        presenter.getProjects(true, false);
        presenter.getTaskByState(0);
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right, R.id.cm_header_btn_right1, R.id.itv_timetrack_add,
            R.id.cm_header_civ_head, R.id.cm_header_btn_member, R.id.cm_header_btn_notice})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
            case R.id.cm_header_civ_head:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_right:
            case R.id.cm_header_btn_notice:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                break;
            case R.id.cm_header_btn_right1:
            case R.id.cm_header_btn_member:
                Intent timeTrackingIntent = new Intent(getActivity(), TimeTrackingMGActivity.class);
                getActivity().startActivity(timeTrackingIntent);
                break;
            case R.id.itv_timetrack_add:
                mAddDialog.show();
                break;
        }
    }

    @Override
    public void renderProjectListView(ArrayList<GroupProject> groupProjects) {
//        mPtrFrameLayout.refreshComplete();
        mSwipeRefreshLayout.setRefreshing(false);
        mNetworkTipsLayout.setVisibility(View.GONE);
        if (groupProjects != null && groupProjects.size() >= 0) {
            mPageBean.setItems(groupProjects);
            adapter.clear();
            adapter.addItem(groupProjects);
        }
    }

    @Override
    public void renderProjectStateView(ArrayList<StateTask> stateTasks) {
        if(stateTasks != null && stateTasks.size() != 0) {
            mStateTasks = new ArrayList<>();
            mStateTasks.addAll(stateTasks);
            setTaskNum(R.id.unstart_num, R.id.processing_num,
                    R.id.delay_num, R.id.finished_num);
        }
    }

    @Override
    public void onError(Throwable ex) {
//        mPtrFrameLayout.refreshComplete();
        mSwipeRefreshLayout.setRefreshing(false);
        if (ex instanceof ConnectException) {
            mNetworkTipsLayout.setVisibility(View.VISIBLE);
        } else {
            ToastUtil.showToast(getActivity(), R.string.dynamic_list_error);
        }
    }

    private void setTaskNum(int... viewIds) {
        for(int i = 0;i<viewIds.length;i++) {
            final TextView numTv = (TextView) mTopLayout.findViewById(viewIds[i]);
            final int finalI = i;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    numTv.setText(String.valueOf(mStateTasks.get(finalI).TaskViewBean.size()));
                }
            });
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String fragmentFlag = TaskConstant.TaskStateString.UNSTART;
            switch (v.getId()) {
                case R.id.unstart_text:
                    fragmentFlag = TaskConstant.TaskStateString.UNSTART;
                    break;
                case R.id.processing_text:
                    fragmentFlag = TaskConstant.TaskStateString.PROCESSING;
                    break;
                case R.id.finished_text:
                    fragmentFlag = TaskConstant.TaskStateString.FINISHED;
                    break;
                case R.id.delay_text:
                    fragmentFlag = TaskConstant.TaskStateString.DELAY;
                    break;
            }
            Intent spectacularIntent = new Intent(getActivity(), TaskSpectacularActivity.class);
            TaskSpectacularActivity.currentType = TaskSpectacularActivity.GROUP_TASK;
            TaskSpectacularActivity.currentState = fragmentFlag;
            spectacularIntent.putExtra("start_anim", false);
            startActivity(spectacularIntent);
        }
    };

    /**
     * 为topView中的控件设置clickListener
     *
     * @param viewIds
     */
    private void setClickListenerToTopView(int... viewIds) {
        for (int id : viewIds) {
            mTopLayout.findViewById(id).setOnClickListener(onClickListener);
        }
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (NewTeamActivity.BROADCAST_TEAM_REFRESH.equals(action)) {
                presenter.getProjects(true, false);
                presenter.getTaskByState(0);
            } else if (NewProjectActivity.BROADCAST_PROJECT_REFRESH.equals(action)) {
                ApiEntity.UserProject tempProject = (ApiEntity.UserProject)intent.getSerializableExtra("project");
                if(tempProject != null) { //编辑团队保存刷新
                    ProjectViewBean projectViewBean = mPageBean.getItems().get(groupPos).projectViews.get(childPos);
                    projectViewBean.Project = tempProject;
                    projectViewBean.ProjectColor = tempProject.Color;
                    projectViewBean.ProjectName = tempProject.Name;
                    adapter.clear();
                    adapter.addItem(mPageBean.getItems());
                    mListView.expandGroup(groupPos);
                }

                if (intent.hasExtra("state") && "delete".equals(intent.getStringExtra("state"))) { //团队删除刷新
                    mPageBean.getItems().get(groupPos).projectViews.remove(childPos);
                    adapter.clear();
                    adapter.addItem(mPageBean.getItems());
                    mListView.expandGroup(groupPos);
                } else if (intent.hasExtra("state") && "archive".equals(intent.getStringExtra("state"))) { //团队归档刷新
                    ProjectViewBean projectViewBean = mPageBean.getItems().get(groupPos).projectViews.get(childPos);
                    projectViewBean.Project.IsArchive = 1;
                    adapter.clear();
                    adapter.addItem(mPageBean.getItems());
                    mListView.expandGroup(groupPos);
                }
            } else if (MainActivity.ACTION_UNREAD_COUNT.equals(action)) {
                int count = intent.getIntExtra("unread_count", 0);
                mBadgeView.setBadgeNumber(count);
            } else if (DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST.equals(action)) {
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
                ImageLoader.getInstance().displayImage(uri, new ImageViewAware((CircleImageView)findView(R.id.cm_header_civ_head)), options, new ImageSize(DisplayUtil.dip2px(getActivity(), 40), DisplayUtil.dip2px(getActivity(), 40)), null, null);
            }
        }
    }
}
