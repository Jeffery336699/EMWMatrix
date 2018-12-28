package cc.emw.mobile.project.view;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.project.adapter.ProjectAdapter;
import cc.emw.mobile.project.adapter.RelativeScheduleAdapter;
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
 * 相关日程页面
 * @author jven.wu
 *
 */
@ContentView(R.layout.activity_relative_task)
public class RelativeScheduleActivity extends BaseActivity 
	implements IRelativeScheduleView{
	public static final String SCHEDULE = "schedule";
	private static final String TAG = "RelativeScheduleActivity";

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderFinishBtn;
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.lv_project_relative_tasks)
	private ListView mListView;
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout;
	
	private String scheduleString;
	private ProjectPresenter presenter;
	private RelativeScheduleAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	private void initView(){
		scheduleString = getIntent().getStringExtra(SCHEDULE);
		presenter = new ProjectPresenter(this);
		OnClickListener onClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.cm_header_btn_left:
					onBackPressed();
					break;
				case R.id.cm_header_tv_right:
					onBackPressed();
					break;
				default:
					break;
				}
			}
		};
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(getResources().getString(R.string.relative_schedule));
		mHeaderFinishBtn.setText(getResources().getString(R.string.finish));
		mHeaderFinishBtn.setVisibility(View.VISIBLE);
		mHeaderBackBtn.setOnClickListener(onClickListener);
		mHeaderFinishBtn.setOnClickListener(onClickListener);		
		mHeaderFinishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                scheduleString = calendar2string(mAdapter.getSelSchedule());
                setResult(RESULT_OK, getIntent().putExtra(SCHEDULE, scheduleString));
				onBackPressed();
			}
		});
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
				presenter.showAllSchedule(RelativeScheduleActivity.this);
			}
		});
	}

	@Override
	public void setListView(RelativeScheduleAdapter adapter) {
		this.mAdapter = adapter;
		adapter.setSchedules(scheduleString);
		mListView.setAdapter(adapter);
		mNetworkTipsLayout.setVisibility(View.GONE);
	}

	@Override
	public void loadProjectList(ProjectAdapter adapter) {
	}
	
	@Override
	public void refreshComplete(String errMsg) {
		mPtrFrameLayout.refreshComplete();
	}
	
	//把日程集合id转成id字符串
	private String calendar2string(ArrayList<CalendarInfo> infos){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0;i<infos.size();i++){
			sb.append(infos.get(i).ID);
			if(i != infos.size()-1){
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString().trim();
	}

	@Override
	public void onNetworkError() {
		mNetworkTipsLayout.setVisibility(View.VISIBLE);
		refreshComplete("");
	}
}
