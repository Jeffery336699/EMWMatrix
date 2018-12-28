package cc.emw.mobile.project.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.adapter.HomeProjectAdapter2;
import cc.emw.mobile.project.bean.GroupProject;
import cc.emw.mobile.project.bean.ProjectViewBean;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.IProjectListView;
import cc.emw.mobile.project.view.NewProjectActivity;
import cc.emw.mobile.project.view.NewTeamActivity;
import cc.emw.mobile.project.view.ObserveProjectActivity;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 项目主列表碎片(旧版布局)
 * Created by jven.wu on 2016/6/22.
 */
@ContentView(R.layout.fragment_project_list)
public class ProjectListFragment2 extends BaseFragment implements IProjectListView {
    public static final String TAG = "ProjectListFragment";
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
    private HomeProjectAdapter2 adapter; //项目主列表适配器类
    private ProjectPresenter presenter; //MVP模式中的主导器
    private MyBroadcastReceive receive; //广播接收类

    private ArrayList<GroupProject> mDataList; //记录当前页获取的数据列表
    private ArrayList<GroupProject> mSearchList = new ArrayList<>(); //用于记录搜索列表数据
    private boolean isTopBottomToggle = false;
    private Dialog dialog; //弹出框
    private boolean canDel;

    public ProjectListFragment2() {
        presenter = new ProjectPresenter(this);
    }

    public void setParentFragment(ProjectFragment fragment) {
        this.parentFragment = fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mListView.setClipToPadding(false);
        Log.d(TAG, "onViewCreated()->");
    }

    @Override
    public void onUserVisible() {
        super.onUserVisible();
        Log.d("liu", "ProjectListFragment-->onUserVisible");
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
        Log.d(TAG, "onFirstUserVisible()->");
        Log.d("liu", "ProjectListFragment-->onFirstUserVisible");
        initListener();
    }

    private void initListener() {
        ProjectFragment.mEtSearch.setText("");
        /**增加搜索功能  */
        ProjectFragment.mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (mDataList == null) return;
                adapter.setData(mDataList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mDataList == null) return;
                mSearchList.clear();
                StringBuilder sb = new StringBuilder();
                for (int i = 0, size = mDataList.size(); i < size; i++) {
                    GroupProject groupProject = mDataList.get(i);
                    if (groupProject.GroupName != null) {
                        CharacterParser characterParser = CharacterParser.getInstance();
                        String selling = characterParser.getSelling(groupProject.GroupName.toLowerCase());
                        sb.delete(0, sb.length());
                        for (int j = 0; j < groupProject.GroupName.length(); j++) {
                            String substring = groupProject.GroupName.substring(j, j + 1);
                            substring = characterParser.convert(substring);
                            if (substring != null) {
                                substring = substring.substring(0, 1);
                                sb.append(substring);
                            }
                        }
                        if (groupProject.GroupName.contains(s.toString().trim()) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
                            mSearchList.add(mDataList.get(i));
                        }
                    }
                }
                refreshView(mSearchList);
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
        adapter = new HomeProjectAdapter2(getActivity());
        mListView.setAdapter(adapter);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                ProjectViewBean project =
                        (ProjectViewBean) adapter.getChild(groupPosition, childPosition);
                GroupProject groupProject = (GroupProject) adapter.getGroup(groupPosition);
                Intent intent = new Intent(getActivity(), ObserveProjectActivity.class);
                intent.putExtra(ObserveProjectActivity.EXTRA_PROJECT_ID, project.ProjectId);
                intent.putExtra(ObserveProjectActivity.EXTRA_PROJECT_NAME, project.ProjectName);
                intent.putIntegerArrayListExtra("groupmembers", groupProject.UsersId);
                intent.putExtra("groupName", groupProject.GroupName);
                intent.putExtra("project", project.Project);
                intent.putExtra("group", groupProject.group);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivity(intent);
                return true;
            }
        });

        //添加列表点击事件
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View childView, int flatPos, long id) {
                long packedPos = ((ExpandableListView) parent).getExpandableListPosition(flatPos);
                final int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                final int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
                int userId = PrefsUtil.readUserInfo().ID;
                String dialogTip = "";
                canDel = false;
                if (childPosition == -1) {
                    dialogTip = "确定解散该圈子？";
                    if (userId == mDataList.get(groupPosition).GroupCreator) {
                        canDel = true;
                    }
                } else {
                    dialogTip = "确定删除该项目？";
                    if (userId == mDataList.get(groupPosition).projectViews.get(childPosition).Project.ID) {
                        canDel = true;
                    }
                }

                new AlertDialog(getActivity()).builder().setMsg(dialogTip)
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //判断当前操作删除的是创建者才执行删除
                                if (!canDel) {
                                    ToastUtil.showToast(getActivity(), "你没有权限执行该操作！");
                                    return;
                                }
                                if (childPosition == -1) {
//                                    mDataList.get(groupPosition).projectViews.clear();
                                    DelGroupById(mDataList.get(groupPosition).GroupId, groupPosition);
                                    Log.d(TAG, "onItemLongClick()->g:" + groupPosition);
                                } else {
                                    int pid = mDataList.get(groupPosition).projectViews.get(childPosition).ProjectId;
                                    DelProjectById(pid, groupPosition, childPosition);
                                }
                                dialog = createLoadingDialog(getResources().getString(R.string.progress_tip));
                                dialog.show();
                            }
                        }).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                return true;
            }

        });

        //注册广播
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
                presenter.getProjects(true, false);
            }
        });
    }

    /**
     * 网络请求数据后在页面上的回调
     * @param groupProjects
     */
    @Override
    public void renderProjectListView(ArrayList<GroupProject> groupProjects) {
        mDataList = groupProjects;//记录列表数据
        refreshView(groupProjects);
        mPtrFrameLayout.refreshComplete();
    }

    /**
     * 根据数据展示数据视图或者空视图
     *
     * @param groupProjects
     */
    private void refreshView(ArrayList<GroupProject> groupProjects) {
        if (groupProjects != null && groupProjects.size() < 1) {
            mEmptyPage.setVisibility(View.VISIBLE);
        } else {
            mEmptyPage.setVisibility(View.INVISIBLE);
        }
        adapter.setData(groupProjects);
        adapter.notifyDataSetChanged();
        mNetworkErrorPage.setVisibility(View.INVISIBLE);
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

    /**
     * 删除项目
     *
     * @param pid      项目id
     * @param groupPos 圈子索引
     * @param childPos 项目索引
     */
    private void DelProjectById(int pid, final int groupPos, final int childPos) {
        API.TalkerAPI.DelProjectById(pid, new RequestCallback<String>(
                String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                delErrorMsg(ex.getMessage());
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && result.equals("1")) {
                    mDataList.get(groupPos).projectViews.remove(childPos);
                    delSuccess("删除项目成功！");
                } else {
                    delErrorMsg("删除项目失败" + result);
                }
            }
        });
    }

    /**
     * 删除圈子
     *
     * @param gid      圈子id
     * @param groupPos 圈子索引
     */
    private void DelGroupById(int gid, final int groupPos) {
        API.TalkerAPI.DelGroup(gid, new RequestCallback<String>(
                String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                delErrorMsg(ex.getMessage());
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && result.equals("1")) {
                    mDataList.remove(groupPos);
                    delSuccess("解散圈子成功！");
                } else {
                    delErrorMsg("解散圈子失败" + result);
                }
            }
        });
    }

    /**
     * 显示删除错误信息
     * @param msg
     */
    private void delErrorMsg(String msg) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 1000);
        ToastUtil.showToast(getActivity(), msg);
    }

    /**
     * 显示删除成功信息
     * @param msg
     */
    private void delSuccess(String msg) {
        adapter.setData(mDataList);
        adapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 1000);
        ToastUtil.showToast(getActivity(), msg, R.drawable.tishi_ico_gougou);
    }
}
