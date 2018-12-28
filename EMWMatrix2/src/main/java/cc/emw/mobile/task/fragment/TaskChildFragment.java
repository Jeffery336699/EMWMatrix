package cc.emw.mobile.task.fragment;

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
import org.xutils.view.annotation.ViewInject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.constant.TaskConstant;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.task.TaskModifyActivity;
import cc.emw.mobile.task.adapter.TaskAdapter;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskView;

/**
 * 任务分流 将任务分成 进行中、未开始、已完成Fragment
 * 
 * @author chengyong.liu
 * 
 */
@ContentView(R.layout.fragment_task_child)
public class TaskChildFragment extends BaseFragment implements ITaskView {

	protected static final String TAG = "TaskChildFragment";
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.lv_task_child_fragment)
	private ExpandableListView mListView; // 进行中、已开始、未完成列表
	
	@ViewInject(R.id.ll_task_blank)
	private LinearLayout mBlankLayout;
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout;

	public int mType = TaskConstant.TaskState.PROCESSING;// 默认是进行的中任务展示,根据类型请求不同的列表数据。

	private TaskAdapter mTaskAdapter;

	private TaskPresenter mTaskPresenter;

	private List<UserProject> mProjectList;
	private TaskChildBroadcastReceive mReceive;

	/**
	 * 根据传入的Type类型生产出相应的Fragment
	 * 
	 * @param type
	 *            任务类型
	 * @return 返回Fragment的类型
	 */
	public static TaskChildFragment newInstance(int type) {
		TaskChildFragment fragment = new TaskChildFragment();
		Bundle b = new Bundle();
		b.putInt("type", type);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTaskPresenter = new TaskPresenter(this);// 任务代理创建
		mType = getArguments().getInt("type");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();

		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TaskModifyActivity.ACTION_CREATE_TASK);
		intentFilter.addAction(TaskModifyActivity.ACTION_MODIFY_TASK);
		mReceive = new TaskChildBroadcastReceive();
		getActivity().registerReceiver(mReceive, intentFilter);

	}

	private void initView() {
		if (mTaskAdapter == null) {
			mTaskAdapter = new TaskAdapter(getActivity());
		}
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
				mTaskPresenter.getProjectByTaskState(mType);
			}
		});

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
		loadMoreListViewContainer.setAutoLoadMore(false);

		mListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {

				return true;
			}
		});
		// 设置子条目的点击事件
		mListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(getActivity(),
						TaskDetailActivity.class);
				intent.putExtra(TaskDetailActivity.TASK_DETAIL, mProjectList
						.get(groupPosition).Tasks.get(childPosition));
				startActivity(intent);
				return false;
			}
		});
		
	}

	@Override
	public void onFirstUserVisible() {
		mPtrFrameLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPtrFrameLayout.autoRefresh(false);
			}
		}, 100);
	}

	/**
	 * 任务列表数据的获取回调
	 */
	@Override
	public void showTask(List<UserProject> projectList) {
		mNetworkTipsLayout.setVisibility(View.GONE);
		// Gson gson = new Gson();
		// String json = gson.toJson(projectList);
		// Log.d(TAG, "类型"+mType+json);

		// 将数据进行处理 空数据不展示
		ArrayList<UserProject> arrayList = new ArrayList<UserProject>();
		for (int i = 0; i < projectList.size(); i++) {
			List<UserFenPai> tasks = projectList.get(i).Tasks;
			if (tasks.size() != 0) {
				arrayList.add(projectList.get(i));
			}
		}
		if (arrayList.size()==0) {
			//展示空视图
			mBlankLayout.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}else{
			mBlankLayout.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
		mProjectList = arrayList;
		mTaskAdapter.setData(arrayList);
		mTaskAdapter.notifyDataSetChanged();
		mListView.setAdapter(mTaskAdapter);
		// 默认展开所有条目
		int groupCount = mTaskAdapter.getGroupCount();
		for (int i = 0; i < groupCount; i++) {
			mListView.expandGroup(i);
		}

	}

	class TaskChildBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TaskModifyActivity.ACTION_MODIFY_TASK
					.equals(intent.getAction())
					|| TaskModifyActivity.ACTION_CREATE_TASK.equals(intent
							.getAction())) {
				mPtrFrameLayout.autoRefresh(false);
			}
		}
	}

	/**
	 * 下拉刷新结束
	 */
	@Override
	public void showFinish() {
		mPtrFrameLayout.refreshComplete();
	}


	@Override
	public void onDestroy() {
		if (mReceive != null)
			getActivity().unregisterReceiver(mReceive);
		super.onDestroy();
	}

	@Override
	public void onError(Throwable ex, boolean isOnCallback) {
		mPtrFrameLayout.refreshComplete();
		if ("返回的数据格式错误".equals(ex.getMessage())) {
			//加载什么视图？ TODO
			
		}
		
		if (ex instanceof ConnectException) {
			mNetworkTipsLayout.setVisibility(View.VISIBLE);//无网络状态 展示视图
		}
	}
}
