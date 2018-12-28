package cc.emw.mobile.project.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.project.adapter.StateProjectAdapter2;
import cc.emw.mobile.project.bean.StateTask;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.IProjectStateView;
import cc.emw.mobile.project.view.NewProjectActivity;
import cc.emw.mobile.project.view.NewTeamActivity;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 任务按状态展示碎片
 * Created by jven.wu on 2016/6/24.
 */
@ContentView(R.layout.fragment_project_statelist)
public class ProjectStateListFragment2 extends BaseFragment implements IProjectStateView {
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.project_lv)
    private ExpandableListView mListView; //具可展开功能的列表
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkErrorPage;  //网络错误显示
    @ViewInject(R.id.empty_ll)
    private LinearLayout mEmptyPage; //空数据页面

    private ProjectFragment parentFragment;
    private StateProjectAdapter2 adapter; //任务根据状态显示适配器
    private ProjectPresenter presenter = new ProjectPresenter(this);
    private MyBroadcastReceive receive;

    private ArrayList<StateTask> mDataList; //记录当前页获取的数据列表
    private ArrayList<StateTask> mSearchList = new ArrayList<>(); //用于记录搜索列表数据

    public void setParentFragment(ProjectFragment fragment) {
        this.parentFragment = fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onUserVisible() {
        Log.d("liu", "projectStateListFragment-->onUserVisible");
        initListener();
    }

    @Override
    public void onFirstUserVisible() {
        super.onFirstUserVisible();
        //第一次加载下拉刷新时，下拉控件初始化需要时间，故这里做个延迟调用
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 300);
//        mPtrFrameLayout.autoRefresh(false);
        Log.d("liu", "projectStateListFragment-->onFirstUserVisible");
        initListener();
    }

    private void initListener() {
        ProjectFragment.mEtSearch.setText("");
        /**添加搜富功能*/
        ProjectFragment.mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(mDataList == null) return;
                adapter.setData(mDataList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mDataList == null) return;
                mSearchList.clear();
                StringBuilder sb = new StringBuilder();
                if (TextUtils.isEmpty(s)) {
                    refreshView(mDataList);
                } else {
                    for (int i = 0, size = mDataList.size(); i < size; i++) {
                        ArrayList<ApiEntity.UserFenPai> tasks = mDataList.get(i).TaskViewBean;
                        if (tasks != null && tasks.size() != 0) {
                            //如果每个状态下的项目不为空
                            for (int z = 0; z < tasks.size(); z++) {
                                ApiEntity.UserFenPai task = tasks.get(z);
                                if (task.Title != null) {
                                    CharacterParser characterParser = CharacterParser.getInstance();
                                    String selling = characterParser.getSelling(task.Title.toLowerCase());
                                    sb.delete(0, sb.length());
                                    for (int j = 0; j < task.Title.length(); j++) {
                                        String substring = task.Title.substring(j, j + 1);
                                        substring = characterParser.convert(substring);
                                        substring = substring.substring(0, 1);
                                        sb.append(substring);
                                    }
                                    if (task.Title.contains(s.toString().trim()) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
                                        //项目组下面可能有多个关键字相同的项目  因此需要过滤相同的状态项目组条目数据
                                        if (!mSearchList.contains(mDataList.get(i))) {
                                            mSearchList.add(mDataList.get(i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    refreshView(mSearchList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    mPtrFrameLayout.setEnabled(true);
                } else {
                    mPtrFrameLayout.setEnabled(false);
                }
            }
        });
    }

    @Event({R.id.ll_network_tips})
    private void onClick(View v) {
        mPtrFrameLayout.autoRefresh(false);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setRefresh();
        adapter = new StateProjectAdapter2(getActivity());
        mListView.setAdapter(adapter);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                ProjectViewBean project =
//                        (ProjectViewBean) adapter.getChild(groupPosition, childPosition);
//                Intent intent = new Intent(getActivity(), ObserveProjectActivity.class);
//                intent.putExtra(ObserveProjectActivity.PROJECTID, project.ProjectId + "");
//                intent.putExtra(ObserveProjectActivity.PROJECTNAME, project.ProjectName);
////                intent.putIntegerArrayListExtra("members",new ArrayList<Integer>());
//                intent.putExtra("groupName", project.ProjectName);
//                intent.putExtra("project", project.Project);
//                startActivity(intent);
//               getActivity(). overridePendingTransition(R.anim.card_activity_open, R.anim.activity_out);
                Intent taskDetailIntent = new Intent(getActivity(), TaskDetailActivity.class);
                taskDetailIntent.putExtra(TaskDetailActivity.TASK_DETAIL,(ApiEntity.UserFenPai)adapter.getChild(groupPosition,childPosition));
                taskDetailIntent.putExtra("start_anim", false);
                startActivity(taskDetailIntent);
                return true;
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NewProjectActivity.BROADCAST_PROJECT_REFRESH);
        intentFilter.addAction(NewTeamActivity.BROADCAST_TEAM_REFRESH);
        receive = new MyBroadcastReceive();
        getActivity().registerReceiver(receive, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receive);
    }

    /**
     * 设置【下拉刷新】和【加载更多】功能
     */
    private void setRefresh() {
        mPtrFrameLayout.setToggleMenu(true);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
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
        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(true);
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {

            }
        });
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                presenter.getProjects(false, false);
//                presenter.getTaskByUserId(PrefsUtil.readUserInfo().ID);
                presenter.getTaskByState(0);
            }
        });
    }

    /**
     * 网络请求数据后返回在页面上的回调
     * @param stateTasks
     */
    @Override
    public void renderProjectStateView(ArrayList<StateTask> stateTasks) {
        mDataList = stateTasks;
        refreshView(stateTasks);
    }

    private void refreshView(ArrayList<StateTask> stateTasks) {
        if (stateTasks != null && stateTasks.size() < 1) {
            mEmptyPage.setVisibility(View.VISIBLE);
        } else {
            mEmptyPage.setVisibility(View.INVISIBLE);
        }
        mNetworkErrorPage.setVisibility(View.INVISIBLE);
        adapter.setData(stateTasks);
        adapter.notifyDataSetChanged();
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void onError(Throwable ex) {
        if (ex instanceof ConnectException) {
            mNetworkErrorPage.setVisibility(View.VISIBLE);
        } else {
            ToastUtil.showToast(getActivity(), ex.getMessage());
        }
        mPtrFrameLayout.refreshComplete();
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();

            if (NewProjectActivity.BROADCAST_PROJECT_REFRESH.equals(actionStr)
                    || NewTeamActivity.BROADCAST_TEAM_REFRESH.equals(actionStr)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }
}
