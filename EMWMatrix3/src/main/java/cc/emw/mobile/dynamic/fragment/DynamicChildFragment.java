package cc.emw.mobile.dynamic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.ListViewItemPositionGetter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.DynamicTypeActivity;
import cc.emw.mobile.dynamic.adapter.DynamicAdapter;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.contral.CommentContral;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.Clock;
import cc.emw.mobile.view.ExtendedListView;
import cc.emw.mobile.view.PinnedSectionListView;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;

/**
 * 动态(全部/关注)Fragment
 * 
 * @author shaobo.zhuang
 * 
 */
@ContentView(R.layout.fragment_dynamic_child)
public class DynamicChildFragment extends BaseFragment implements OnItemClickListener, ExtendedListView.OnPositionChangedListener {

	@ViewInject(R.id.swipe_refresh)
	private SwipeRefreshLayout mSwipeRefreshLayout;
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.load_more_small_image_list_view)
	private PinnedSectionListView mListView;
//	private ExtendedListView mListView; // 近期动态列表
	@ViewInject(R.id.ll_dynamic_blank)
	private LinearLayout mBlankLayout; //空视图
	@ViewInject(R.id.tv_dynamic_blank)
	private TextView mBlankTv; //搜索空提示
	@ViewInject(R.id.tv_dynamic_type)
	private TextView mTypeTv; //筛选类型
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout; //无网络

	private DynamicAdapter mHomeAdapter; // 近期动态adapter
	private ArrayList<UserNote> mDataList; // 近期动态列表数据

	public static final String ACTION_REFRESH_DYNAMIC_LIST = "cc.emw.mobile.refresh_dynamic_list"; // 刷新的action
	private MyBroadcastReceive mReceive;
	private int loadType; //0:所有；1:关注
	private int typeId; //UserNoteAddTypes
	private int page = 1; // 第几页，第1页为1，每下一页+1
	private static final int PAGE_COUNT = 10; // 页数
	private String keyword = ""; //搜索关键字

	private CommentContral mCommentContral;

	/*视频自动播放*/
	private ListItemsVisibilityCalculator mListItemVisibilityCalculator;
	private ItemsPositionGetter mItemsPositionGetter;
	private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

	public static DynamicChildFragment newInstance(int position) {
		DynamicChildFragment fragment = new DynamicChildFragment();
		Bundle args = new Bundle();
		args.putInt("position", position);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadType = getArguments().getInt("position", 0);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_REFRESH_DYNAMIC_LIST);
		mReceive = new MyBroadcastReceive();
		getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
	}

    @Event(R.id.ll_network_tips)
	private void onNetworkTipsClick(View v) {
		requestFirstPage();
	}

	private void initView() {
		loadMoreListViewContainer.useDefaultFooter();
		loadMoreListViewContainer.setAutoLoadMore(true);

		// binding view and data
		mDataList = new ArrayList<>();
		mHomeAdapter = new DynamicAdapter(getActivity(), mDataList, true);
		mTypeTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), DynamicTypeActivity.class);
				intent.putExtra("type_id", typeId);
				intent.putExtra("start_anim", false);
				startActivity(intent);
			}
		});
		mListView.setAdapter(mHomeAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnPositionChangedListener(this);
		mListView.setShadowVisible(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mListView.setNestedScrollingEnabled(true);
		}

		mSwipeRefreshLayout.setColorSchemeResources(R.color.ptr_blue, R.color.ptr_green, R.color.ptr_yellow, R.color.ptr_red);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// 请求第一页数据
				page = 1;
				mHomeAdapter.clearVoteMap();
				getHomeList(keyword, loadType, typeId);
			}
		});

		loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
			@Override
			public void onLoadMore(LoadMoreContainer loadMoreContainer) {
				// 请求下一页数据
				page++;
				getHomeList(keyword, loadType, typeId);
			}
		});
		/*视频自动播放*/
		mListItemVisibilityCalculator = new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mDataList);
		mItemsPositionGetter = new ListViewItemPositionGetter(mListView);
		// 设置滑动不加载 pauseOnScroll(滑动不加载) 传 true     pauseOnScroll(猛划不加载) 传true
		loadMoreListViewContainer.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true){
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				super.onScrollStateChanged(view, scrollState);
				mScrollState = scrollState;
				if (scrollState == SCROLL_STATE_IDLE && !mDataList.isEmpty()) {
					try {
						mListItemVisibilityCalculator.onScrollStateIdle(mItemsPositionGetter, view.getFirstVisiblePosition(), view.getLastVisiblePosition());
					} catch (Exception e) {
					}
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				if (!mDataList.isEmpty()) {
					// on each scroll event we need to call onScroll for mListItemVisibilityCalculator
					// in order to recalculate the items visibility
					try {
						mListItemVisibilityCalculator.onScroll(mItemsPositionGetter, firstVisibleItem, visibleItemCount, mScrollState);
					} catch (Exception e) {
					}
				}

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
					boolean enable = false;
					if(mListView != null && mListView.getChildCount() > 0){
						boolean firstItemVisible = mListView.getFirstVisiblePosition() == 0; // check if the first item of the list is visible
						boolean topOfFirstItemVisible = mListView.getChildAt(0).getTop() == 0; // check if the top of the first item is visible
						enable = firstItemVisible && topOfFirstItemVisible; // enabling or disabling the refresh layout
					}
					mSwipeRefreshLayout.setEnabled(enable);
				}
			}
		});

		/*View commentLayout = getActivity().findViewById(R.id.ll_dynamicbottom_discuss);
		EditText commentContentEt = (EditText) getActivity().findViewById(R.id.et_dynamicbottom_content);
		View commentSendItv = getActivity().findViewById(R.id.itv_dynamicbottom_send);
		mCommentContral = new CommentContral(getActivity(), commentLayout, commentContentEt, commentSendItv);
		mCommentContral.setListView(mListView);
		mHomeAdapter.setCommentContral(mCommentContral);

		setViewTreeObserver();*/
	}

	private void setViewTreeObserver() {
		final ViewTreeObserver swipeRefreshLayoutVTO = mListView.getViewTreeObserver();
		swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect r = new Rect();
				mListView.getWindowVisibleDisplayFrame(r);
				int screenH = mListView.getRootView().getHeight();
				int keyH = screenH - (r.bottom - r.top);
				if (keyH == EMWApplication.mKeyBoardH) {//有变化时才处理，否则会陷入死循环
					return;
				}
				EMWApplication.mKeyBoardH = keyH;
				mCommentContral.setScreenHeight(screenH);//应用屏幕的高度
				if (mCommentContral != null) {
					mCommentContral.handleListViewScroll();
				}
			}
		});
	}

	@Override
	public void onFirstUserVisible() {
		requestFirstPage();
	}

	@Override
	public void onDestroy() {
		if (mReceive != null)
			getActivity().unregisterReceiver(mReceive); // 取消监听
		super.onDestroy();
	}

	class MyBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_REFRESH_DYNAMIC_LIST.equals(action)) {
				if (intent.hasExtra("type_id")) { //筛选刷新
					typeId = intent.getIntExtra("type_id", 0);
					mTypeTv.setText(HelpUtil.getDynamicTypeText(typeId));
					requestFirstPage();
				} else if (intent.hasExtra("col_note")) { //收藏刷新
					ApiEntity.UserNote col = (ApiEntity.UserNote)intent.getSerializableExtra("col_note");
					for (UserNote un : mDataList) {
						if (un.ID == col.ID) {
							un.IsEnjoy = col.IsEnjoy;
							un.EnjoyCount = col.EnjoyCount;
							un.EnjoyList = col.EnjoyList;
							break;
						}
					}
					mHomeAdapter.notifyDataSetChanged();
				} else if (intent.hasExtra("push_note")){ //通知栏推送进入详情刷新
					UserNote push = (UserNote) intent.getSerializableExtra("push_note");
					for (int i = 0, count = mDataList.size(); i < count; i++) {
						UserNote un = mDataList.get(i);
						if (un.ID == push.ID) {
							mDataList.set(i, push);
							break;
						}
					}
					mHomeAdapter.notifyDataSetChanged();
				} else if (intent.hasExtra("note_id") && intent.hasExtra("rev_note")){ //评论增加刷新
					int noteID = intent.getIntExtra("note_id", 0);
					ApiEntity.UserNote rev = (ApiEntity.UserNote)intent.getSerializableExtra("rev_note");
					for (UserNote un : mDataList) {
						if (un.ID == noteID) {
							un.RevCount++;
							un.RevInfo.add(rev);
							break;
						}
					}
					mHomeAdapter.notifyDataSetChanged();
				} else if (intent.hasExtra("note_id") && intent.hasExtra("delrev_note")){ //评论删除刷新
					int noteID = intent.getIntExtra("note_id", 0);
					ApiEntity.UserNote rev = (ApiEntity.UserNote)intent.getSerializableExtra("delrev_note");
					for (UserNote un : mDataList) {
						if (un.ID == noteID) {
							un.RevCount--;
							for (ApiEntity.UserNote revnote : un.RevInfo) {
								if (revnote.ID == rev.ID) {
									un.RevInfo.remove(revnote);
									break;
								}
							}
							break;
						}
					}
					mHomeAdapter.notifyDataSetChanged();
				} else if (intent.hasExtra("keyword")) { //搜索刷新
					keyword = intent.getStringExtra("keyword");
					requestFirstPage();
				} else { //新建刷新
					page = 1;
					typeId = 0;
					mHomeAdapter.clearVoteMap();
					mTypeTv.setText(HelpUtil.getDynamicTypeText(typeId));
					getHomeList("", loadType, typeId);
					getActivity().sendBroadcast(new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST));
				}
			}
		}
	}

	// 请求第一页数据
	private void requestFirstPage() {
		mSwipeRefreshLayout.setRefreshing(true);
		page = 1;
		mHomeAdapter.clearVoteMap();
		getHomeList(keyword, loadType, typeId);
	}

	/**
	 * 获取近期动态列表
	 */
	private void getHomeList(final String keyword, int loadType, final int typeId) {
		API.TalkerAPI.LoadTalker(keyword, loadType, typeId, page, PAGE_COUNT, "", new RequestCallback<UserNote>(UserNote.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mSwipeRefreshLayout.setRefreshing(false);
				if (page > 0) {
					page--;
				}
				mBlankLayout.setVisibility(View.GONE);
				if (ex instanceof ConnectException) {
					mNetworkTipsLayout.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.showToast(getActivity(), R.string.dynamic_list_error);
				}
			}
			@Override
			public void onParseSuccess(List<UserNote> respList) {
				mSwipeRefreshLayout.setRefreshing(false);
				mNetworkTipsLayout.setVisibility(View.GONE);
				if (respList.size() < PAGE_COUNT)
					loadMoreListViewContainer.loadMoreFinish(false, false);// load more
				else
					loadMoreListViewContainer.loadMoreFinish(false, true);

				if (respList.size() > 0) {
					mBlankLayout.setVisibility(View.GONE);
					if (page == 1)
						mDataList.clear();
					mDataList.addAll(respList);
					mHomeAdapter.setData(mDataList);
					mHomeAdapter.notifyDataSetChanged();
					if (page == 1) {
						mListView.setSelection(0);
					}
				 } else {
					if (page == 1) {
						if (!TextUtils.isEmpty(keyword)) {
							mBlankTv.setCompoundDrawables(null, null, null, null);
							mBlankTv.setText("暂无搜索结果");
//							mTypeTv.setVisibility(View.GONE);
						} else {
							Drawable top = getResources().getDrawable(R.drawable.blank_ico_talker);
							top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 必须设置图片大小，否则不显示
							mBlankTv.setCompoundDrawables(null, top, null, null);
							mBlankTv.setText("");
							/*mTypeTv.setText(HelpUtil.getDynamicTypeText(typeId));
							mTypeTv.setVisibility(View.VISIBLE);
							mTypeTv.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									Intent intent = new Intent(getActivity(), DynamicTypeActivity.class);
									intent.putExtra("type_id", typeId);
									intent.putExtra("start_anim", false);
									startActivity(intent);
								}
							});*/
						}
						mBlankLayout.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		/*if (v.getTag(R.id.tag_second) != null) {
			Intent intent = new Intent(getActivity(), DynamicDetailActivity.class);
			intent.putExtra("user_note", (UserNote) v.getTag(R.id.tag_second));
			intent.putExtra("start_anim", false);
			startActivity(intent);
		}*/
	}

	@Override
	public void onPositionChanged(ExtendedListView listView, int position, View scrollBarPanel) {
		if (mDataList != null && position < mDataList.size()) { //更新滑块时间
			TextView tv = (TextView) scrollBarPanel.findViewById(R.id.timeTextView);
			String createTime = StringUtils.friendly_time5(mDataList.get(position).CreateTime);
			tv.setText(createTime);

			Clock analogClockObj = (Clock) scrollBarPanel.findViewById(R.id.analogClockScroller);
			Time timeObj = new Time();
			analogClockObj.setSecondHandVisibility(false);
			analogClockObj.setVisibility(View.VISIBLE);
			Date date = StringUtils.toDate(mDataList.get(position).CreateTime);
					Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			timeObj.set(calendar.get(Calendar.SECOND), calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR), 0, 0, 0);
			analogClockObj.onTimeChanged(timeObj);
		}
	}

}
