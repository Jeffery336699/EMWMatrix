package cc.emw.mobile.me;

import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.adapter.DynamicAdapter;
import cc.emw.mobile.dynamic.adapter.DynamicDetailAdapter;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.me.presenter.MyInfoPresenter;
import cc.emw.mobile.me.view.MyInfoView;

/**
 * 我·相关到我
 */
@ContentView(R.layout.activity_mention_me)
public class MentionMeActivity extends BaseActivity implements
		OnItemClickListener, MyInfoView {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn; // 顶部条发布

	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView mListView; //

	private DynamicAdapter mDynamicAdapter; // adapter
	private ArrayList<UserNote> mDataList; // 列表数据

	private MyInfoPresenter presenter;

	private int page = PAGE_FIRST; // 第几页，第1页为1，每下一页+1
	private static final int PAGE_FIRST = 1; // 第1页
	private static final int PAGE_COUNT = 10; // 页数

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();

		mPtrFrameLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPtrFrameLayout.autoRefresh(false);
			}
		}, 100);
	}

	private void initView() {
		mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText("相关到我");
		mHeaderMoreBtn.setImageResource(R.drawable.cm_header_btn_more);
		mHeaderMoreBtn.setVisibility(View.GONE);

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
				// 请求第一页数据
				page = PAGE_FIRST;
				presenter.getMyReleaseInfo(2, 0, page, PAGE_COUNT);
			}
		});

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

		// binding view and data
		presenter = new MyInfoPresenter(this);
		mDataList = new ArrayList<UserNote>();
		mDynamicAdapter = new DynamicAdapter(this, mDataList);
		mListView.setAdapter(mDynamicAdapter);
		mListView.setOnItemClickListener(this);
		loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
			@Override
			public void onLoadMore(LoadMoreContainer loadMoreContainer) {
				// 请求下一页数据
				page++;
				presenter.getMyReleaseInfo(2, 0, page, PAGE_COUNT);
			}
		});
	}


	@Event(value = { R.id.cm_header_btn_left, R.id.cm_header_btn_right })
	private void onHeaderClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		case R.id.cm_header_btn_right:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (v.getTag(R.id.tag_second) != null) {
			Intent intent = new Intent(this, DynamicDetailActivity.class);
			intent.putExtra("user_note", (UserNote) v.getTag(R.id.tag_second));
			startActivity(intent);
		}
	}

	@Override
	public void showFailureInfo(String tips) {
		mPtrFrameLayout.refreshComplete();
		if (page > 0) {
			page--;
		}
	}

	@Override
	public void showFollowList(List<cc.emw.mobile.entity.UserInfo> simpleUsers) {
		// TODO Auto-generated method stub
	}

	@Override
	public void finishRefresh() {
		mPtrFrameLayout.refreshComplete();
	}

	@Override
	public void getMyInfoCount(List<Integer> counts) {
		// TODO Auto-generated method stub
	}

	@Override
	public void getMyReleaseInfoList(List<UserNote> respList) {
		if (respList.size() < PAGE_COUNT) {
			loadMoreListViewContainer.loadMoreFinish(false, false);// load more
		} else {
			loadMoreListViewContainer.loadMoreFinish(false, true);
		}
		if (page == PAGE_FIRST)
			mDataList.clear();
		mDataList.addAll(respList);
		mDynamicAdapter.notifyDataSetChanged();
	}
}
