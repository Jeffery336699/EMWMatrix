package cc.emw.mobile.project.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import java.io.Serializable;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.adapter.ProjectAdapter;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.AddSprintActivity;
import cc.emw.mobile.project.view.ISummaryView;
import cc.emw.mobile.project.view.ModifyProjectActivity;
import cc.emw.mobile.project.view.ProjectDetailsActivity;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.task.TaskModifyActivity;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 项目概览页面
 * @author jven.wu
 *
 */
@ContentView(R.layout.fragment_summary)
public class SummaryFragment extends BaseFragment implements ISummaryView {
	private static final String TAG = "SummaryFragment";
	public static final String PROJECT_REFRESH = "cc.emw.mobile.project.fragment";
    public static final int PAGE_SITE = 20;

	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.lv_summary_project)
	private ExpandableListView mListView;
	@ViewInject(R.id.ll_summary_blank)
	private LinearLayout mBlankImage;
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout;

	private ProjectAdapter adapter = new ProjectAdapter(this.getContext());
	private ProjectPresenter presenter;
	private ProjectUpdateBroadcastReceive updateReceive;
    private boolean isModify = false;
    private int page = 1;
	
	public static SummaryFragment newInstance(String content) {
		SummaryFragment fragment = new SummaryFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		presenter = new ProjectPresenter(this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}

	@Override
	public void onFirstUserVisible() {
		mPtrFrameLayout.postDelayed(new Runnable() {

			@Override
			public void run() {
				mPtrFrameLayout.autoRefresh(false);
			}
		}, 100);
		refresh();
	}
	
	@Override
	public void onUserVisible() {
		super.onUserVisible();
		if(adapter != null){
			ProjectPresenter.setGlobalProjects(adapter.getArrayProjects());
		}
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
                page = 1;
                presenter.showListProjects(page,getContext());
            }
        });
	}

	@Override
	public void loadProjectList(ProjectAdapter adapter) {
        int oldCount = 0;
        int count = 0;
        if(page == 1) {
            this.adapter = adapter;
            mListView.setAdapter(adapter);
        }else {
            oldCount = this.adapter.getArrayProjects().size();
            this.adapter.setArrayProjects(adapter.getArrayProjects());
            this.adapter.notifyDataSetChanged();
        }
		ProjectPresenter.setGlobalProjects(adapter.getArrayProjects());
		if(adapter.getArrayProjects().size()>0 
				|| adapter.getArraySprints().size()>0){
			mBlankImage.setVisibility(View.INVISIBLE);
		}else{
			mBlankImage.setVisibility(View.VISIBLE);
		}
		mNetworkTipsLayout.setVisibility(View.GONE);
//        if(isModify) {
            Intent intentBroadCast = new Intent();
            intentBroadCast.setAction(ScheduleFragment.PROJECT_REFRESH);
            getActivity().sendBroadcast(intentBroadCast);
            isModify = false;
//        }
        count = adapter.getArrayProjects().size() - oldCount;
        if (count < PAGE_SITE)
            loadMoreListViewContainer.loadMoreFinish(false, false);// load more
        else
            loadMoreListViewContainer.loadMoreFinish(false, true);
	}

	private void initView() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TaskModifyActivity.ACTION_CREATE_TASK);
		intentFilter.addAction(TaskModifyActivity.ACTION_MODIFY_TASK);
		intentFilter.addAction(AddSprintActivity.ADD_SPRINT);
		intentFilter.addAction(PROJECT_REFRESH);
		updateReceive = new ProjectUpdateBroadcastReceive();
		getActivity().registerReceiver(updateReceive, intentFilter);
//        mListView.setAdapter(this.adapter);
		mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(getActivity(),TaskDetailActivity.class);
				intent.putExtra(TaskDetailActivity.TASK_DETAIL, 
						(UserFenPai)adapter.getChild(groupPosition, childPosition));
				startActivity(intent);
				return true;
			}
		});
		mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if(adapter.getGroup(groupPosition) instanceof UserProject){
					if(((UserProject)adapter.getGroup(groupPosition)).ID == -1){
						return true;
					}
					Intent intent = new Intent(getActivity(),ProjectDetailsActivity.class);
					intent.putExtra(ProjectDetailsActivity.DETAILS_PROJECT,
                            (Serializable)adapter.getGroup(groupPosition));
					startActivity(intent);
				}
				return true;
			}
		});
		mPtrFrameLayout.setPinContent(false);
		mPtrFrameLayout.setLoadingMinTime(1000);
		// header
		final MaterialHeader header = new MaterialHeader(getActivity());
		int[] colors = getResources().getIntArray(R.array.google_colors);
		header.setColorSchemeColors(colors);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
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
                page++;
                Log.d(TAG,"initView()->page: " + page);
                presenter.showListProjects(page,getContext());
			}
		});
		mNetworkTipsLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPtrFrameLayout.autoRefresh(false);
			}
		});
	}

	@Override
	public void refreshComplete() {
        mPtrFrameLayout.refreshComplete();
	}

    @Override
    public void onGetProjectsError() {
        if(page>1){
            page--;
        }else {
            mPtrFrameLayout.refreshComplete();
        }
    }

    @Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(updateReceive);
	}

	class ProjectUpdateBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
            int i = intent.getIntExtra("modify",-1);
            if(i == ModifyProjectActivity.MODIFY){
                isModify = true;
            }
			if (TaskModifyActivity.ACTION_CREATE_TASK.equals(actionStr)
					|| TaskModifyActivity.ACTION_MODIFY_TASK.equals(actionStr)
					|| AddSprintActivity.ADD_SPRINT.equals(actionStr)
					|| PROJECT_REFRESH.equals(actionStr)) {
				mPtrFrameLayout.autoRefresh(false);
			}
		}
	}

	@Override
	public void onNetworkError() {
		refreshComplete();
		mNetworkTipsLayout.setVisibility(View.VISIBLE);
	}
}
