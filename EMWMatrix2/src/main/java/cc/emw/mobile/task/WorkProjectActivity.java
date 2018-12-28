package cc.emw.mobile.task;

import java.net.ConnectException;
import java.util.List;

import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;

import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.task.adapter.WorkProjectAdapter;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.IWorkProjectView;

@ContentView(R.layout.activity_task_work_project)
public class WorkProjectActivity extends BaseActivity implements
		IWorkProjectView {
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.lv_task_work_project)
	private ListView mListView;

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderRightTv; // 顶部右标题
	
	@ViewInject(R.id.ll_task_blank)
	private LinearLayout mLlBlank;// 空视图
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout;// 无网络

	private TaskPresenter mTaskPresenter;
	private WorkProjectAdapter mAdapter;

	public static final String WORK_PROJECT = "work_project";
	private int mProjectID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProjectID = getIntent().getIntExtra(
				TaskModifyActivity.RELATION_PROJECT, 0);
		initView();
	}

	private void initView() {
		mAdapter = new WorkProjectAdapter(this, mProjectID);
		mTaskPresenter = new TaskPresenter(this);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderRightTv.setVisibility(View.VISIBLE);
		mHeaderRightTv.setText(R.string.finish);
		mHeaderTitleTv.setText(R.string.task_workproject_relation_project);

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
				mTaskPresenter.getAllProjects();
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
			UserProject selectProjecd = mAdapter.getSelectProjecd();
			Intent intent = new Intent();
			intent.putExtra(WORK_PROJECT, selectProjecd);
			setResult(Activity.RESULT_OK, intent);
			onBackPressed();
			break;

		default:
			break;
		}
	}

	@Override
	public void showProjects(List<UserProject> projects) {
		mNetworkTipsLayout.setVisibility(View.GONE);
		if (projects.size() == 0) {
			mLlBlank.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		} else {
			mLlBlank.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
		mAdapter.setData(projects);
		mAdapter.notifyDataSetChanged();
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void showFinish() {
		mPtrFrameLayout.refreshComplete();
	}

	@Override
	public void onError(Throwable ex, boolean isOnCallback) {
		mPtrFrameLayout.refreshComplete();
		if (ex instanceof ConnectException) {
			mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
			return;
		}
		
		//加载错误视图 TODO
		
	}

}
