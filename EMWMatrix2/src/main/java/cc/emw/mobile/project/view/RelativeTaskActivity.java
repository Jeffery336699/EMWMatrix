package cc.emw.mobile.project.view;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.project.adapter.ProjectAdapter;
import cc.emw.mobile.project.adapter.RelativeTaskAdapter;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 相关任务页面
 * @author jven.wu
 *
 */
@ContentView(R.layout.activity_relative_task)
public class RelativeTaskActivity extends BaseActivity
	implements IRelativeTaskView{
	public static final int FROM_PROJECT = 0;//来自项目
	public static final int FROM_SCHEDULE = 1;//来自日程
	public static final int TASKS = 0;

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;
	@ViewInject(R.id.cm_header_tv_right)	//完成控钮
	private TextView mHeaderFinishBtn;
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.lv_project_relative_tasks)
	private ListView mListView;
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout;

	private String taskIds = "";
	private int from;
	private ProjectPresenter presenter;
	private RelativeTaskAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	private void initView(){
		taskIds = getIntent().getStringExtra("task_ids");
		from = getIntent().getIntExtra("from",FROM_PROJECT);
		presenter = new ProjectPresenter(this);
		OnClickListener onClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.cm_header_btn_left:
					onBackPressed();
					break;
				case R.id.cm_header_tv_right:
                    ArrayList<UserFenPai> tasks = new ArrayList<UserFenPai>();
                    tasks.addAll(mAdapter.getSelTasks());
                    setResult(RESULT_OK, getIntent().putExtra("select_list", tasks));
					onBackPressed();
					break;
				default:
					break;
				}
			}
		};
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(getResources().getString(R.string.relative_task));
		mHeaderFinishBtn.setText(getResources().getString(R.string.finish));
		mHeaderFinishBtn.setVisibility(View.VISIBLE);
		mHeaderBackBtn.setOnClickListener(onClickListener);
		mHeaderFinishBtn.setOnClickListener(onClickListener);
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
		loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
			@Override
			public void onLoadMore(LoadMoreContainer loadMoreContainer) {
				
			}
		});
		mPtrFrameLayout.postDelayed(new Runnable() {

			@Override
			public void run() {
				mPtrFrameLayout.autoRefresh(false);
			}
		}, 100);
		refresh();
		mNetworkTipsLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPtrFrameLayout.autoRefresh(false);
			}
		});
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
				presenter.showAllTasks(RelativeTaskActivity.this);
			}
		});
	}

	@Override
	public void setListView(RelativeTaskAdapter adapter) {
		this.mAdapter = adapter;
		if(taskIds == null || "".equals(taskIds)){
			adapter.setTasks(new String[]{}, from);
		}else {
            adapter.setTasks(taskIds.split(","), from);
        }
		mListView.setAdapter(adapter);
		mNetworkTipsLayout.setVisibility(View.GONE);
	}

	@Override
	public void loadProjectList(ProjectAdapter adapter) {
	}

	@Override
	public void refreshComplete() {
		mPtrFrameLayout.refreshComplete();
	}

	@Override
	public void displayError(String errMsg) {
		Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onNetworkError() {
		mNetworkTipsLayout.setVisibility(View.VISIBLE);
		refreshComplete();
	}
}
