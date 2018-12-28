package cc.emw.mobile.task;

import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.net.ConnectException;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.ApiEntity.UserSprint;
import cc.emw.mobile.project.fragment.SummaryFragment;
import cc.emw.mobile.task.adapter.SprintsAdapter;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ItaskSprintsView;
import cc.emw.mobile.util.ToastUtil;

@ContentView(R.layout.activity_task_work_project)
public class SprintActivity extends BaseActivity implements ItaskSprintsView {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderRightTv; // 顶部右标题

	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.lv_task_work_project)
	private ListView mListView;

	@ViewInject(R.id.ll_task_blank)
	private LinearLayout mBlankLayout;// 空视图
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout;// 无网络
	private Dialog mLoadingDialog; // 加载框

	public static final int ADD_SPRITNS = 1;
	public static final int GET_SPRITNS = 2;
	private int mOperateSprints;

	public static final String SPRINT_TASKID = "sprint_taskid";// 任务冲刺接收任务
	private int mTaskID;// 需要更改的任务ID
	private TaskPresenter mTaskPresenter;
	private SprintsAdapter mSprintsAdapter;
	private List<UserSprint> mSprints;//所有的项目冲刺

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTaskPresenter = new TaskPresenter(this);
		mTaskID = getIntent().getIntExtra(SPRINT_TASKID, 0);
		initView();

	}

	private void initView() {
		mSprintsAdapter = new SprintsAdapter(this, mTaskID);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderRightTv.setVisibility(View.VISIBLE);
		mHeaderRightTv.setText(getString(R.string.finish));
		mHeaderTitleTv.setText(getString(R.string.task_relation_sprints));

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
				mOperateSprints=GET_SPRITNS;
				mTaskPresenter.getAllSprints();
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
					View content, View header) {
				// here check list view, not content.
				return PtrDefaultHandler.checkContentCanBePulledDown(frame,
						mListView, header);
			}
		});
	}

	@Event({ R.id.cm_header_btn_left, R.id.cm_header_tv_right })
	private void onHeaderClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		case R.id.cm_header_tv_right:
//			List<UserSprint> userSprintList = mSprintsAdapter
//					.getUserSprintList();
			UserSprint userSprint = mSprintsAdapter.getUserSprint();
			if (userSprint.Content==null) {
				Toast.makeText(this, getString(R.string.task_chose_sprint), Toast.LENGTH_SHORT).show();
				return;
			}
			mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
			mLoadingDialog.show();
			mOperateSprints=ADD_SPRITNS;
			mTaskPresenter.doSprintTask(mSprints,userSprint, mTaskID);
			break;

		default:
			break;
		}
	}

	@Override
	public void showSprints(List<UserSprint> sprints) {
		mSprints=sprints;
		mNetworkTipsLayout.setVisibility(View.GONE);
		if (sprints.size() == 0) {
			mBlankLayout.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		} else {
			mBlankLayout.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
		mSprintsAdapter.setData(sprints);
		mSprintsAdapter.notifyDataSetChanged();
		mListView.setAdapter(mSprintsAdapter);
		mOperateSprints=-1;
	}

	@Override
	public void doSprintTask(String s) {
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		if ("1".equals(s)) {
			// 发送广播到项目界面进行刷新
			Intent intentProject = new Intent();
			intentProject.setAction(SummaryFragment.PROJECT_REFRESH);
			sendBroadcast(intentProject);
			ToastUtil.showToast(this, getString(R.string.task_add_sprint_success), R.drawable.tishi_ico_gougou);
			onBackPressed();
		} else {
			ToastUtil.showToast(this,  getString(R.string.task_add_sprint_error));
		}
		mOperateSprints=-1;		
	}

	@Override
	public void showFinish() {
		mPtrFrameLayout.refreshComplete();
	}

	@Override
	public void onError(Throwable ex, boolean isOnCallback) {
		mPtrFrameLayout.refreshComplete();
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		switch (mOperateSprints) {
		case GET_SPRITNS:
			if (ex instanceof ConnectException) {
				mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
			}
			break;
		case ADD_SPRITNS:
			ToastUtil.showToast(this, getString(R.string.task_add_sprint_error));
			break;

		default:
			break;
		}
		mOperateSprints=-1;
	}

}
