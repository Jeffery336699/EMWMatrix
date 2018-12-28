package cc.emw.mobile.main.fragment.talker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarCreateActivitys;
import cc.emw.mobile.dynamic.AllotActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.NoticeActivity;
import cc.emw.mobile.dynamic.PlanActivity;
import cc.emw.mobile.dynamic.ShareActivity;
import cc.emw.mobile.dynamic.adapter.DynamicAdapter;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 动态Fragment
 * 
 * @author shaobo.zhuang
 * 
 */
@ContentView(R.layout.fragment_dynamic)
public class DynamicFragment extends BaseFragment implements OnItemClickListener {

	@ViewInject(R.id.cm_header_btn_left1)
	private ImageButton mHeaderMenuBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title1)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right1)
	private ImageButton mHeaderMoreBtn; // 顶部条右菜单按钮
	
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView mListView; // 近期动态列表
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout;

	private DynamicAdapter mHomeAdapter; // 近期动态adapter
	private ArrayList<UserNote> mDataList; // 近期动态列表数据

	public static final String ACTION_REFRESH_HOME_LIST = "cc.emw.mobile.refresh_home_list"; // 刷新的action
	private MyBroadcastReceive mReceive;
	private String headerTitle;
	private int page = 1; // 第几页，第1页为1，每下一页+1
	private static final int PAGE_COUNT = 10; // 页数

	public static DynamicFragment newInstance(String title) {
		DynamicFragment fragment = new DynamicFragment();
		Bundle args = new Bundle();
		args.putString("header_title", title);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		headerTitle = getArguments().getString("header_title");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
		initMenu();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_REFRESH_HOME_LIST);
		mReceive = new MyBroadcastReceive();
		getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
	}
	
	@Event({ R.id.cm_header_btn_left1, R.id.cm_header_btn_right1 })
	private void onHeaderClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left1:
			getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
			break;
		case R.id.cm_header_btn_right1:
			mMenu.showAsDropDown(v);
			break;
		}
	}
	
	@Event(R.id.ll_network_tips)
	private void onNetworkTipsClick(View v) {
		mPtrFrameLayout.autoRefresh(false);
	}

	private void initView() {
		mHeaderMenuBtn.setImageResource(R.drawable.nav_btn_menu);
		mHeaderMenuBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(headerTitle);
		mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_share);
		mHeaderMoreBtn.setVisibility(View.VISIBLE);
		
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
				page = 1;
				mHomeAdapter.clearVoteMap();
				getHomeList();
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

		// binding view and data
		mDataList = new ArrayList<UserNote>();
		mHomeAdapter = new DynamicAdapter(getActivity(), mDataList);
		mListView.setAdapter(mHomeAdapter);
		mListView.setOnItemClickListener(this);
		loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
			@Override
			public void onLoadMore(LoadMoreContainer loadMoreContainer) {
				// 请求下一页数据
				page++;
				getHomeList();
			}
		});
	}

	private RightMenu mMenu;
    private void initMenu() {  
        mMenu = new RightMenu(getActivity());  
        mMenu.addItem(R.string.dynamic_more_share, 1);  
        mMenu.addItem(R.string.dynamic_more_notice, 2);
        mMenu.addItem(R.string.dynamic_more_schedule, 3);
        mMenu.addItem(R.string.dynamic_more_allot, 4);
        mMenu.addItem(R.string.dynamic_more_plan, 5);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {  
            @Override  
            public void selected(View view, PopMenu.Item item, int position) {
                switch (item.id) {
	                case 1:
	                	Intent shareIntent = new Intent(getActivity(), ShareActivity.class);
	    				startActivity(shareIntent);
	                    break;
                    case 2:
                    	Intent noticeIntent = new Intent(getActivity(), NoticeActivity.class);
	    				startActivity(noticeIntent);
                        break;
                    case 3:
                    	Intent scheduleIntent = new Intent(getActivity(), CalendarCreateActivitys.class);
                    	scheduleIntent.putExtra("enter_flag", 1);
	    				startActivity(scheduleIntent);
                        break;
                    case 4:
                    	Intent allotIntent = new Intent(getActivity(), AllotActivity.class);
	    				startActivity(allotIntent);
                        break;
                    case 5:
                    	Intent planIntent = new Intent(getActivity(), PlanActivity.class);
	    				startActivity(planIntent);
                        break;
                }
            }  
        });
    }
	
	@Override
	public void onFirstUserVisible() {
		if (mPtrFrameLayout != null) {
			mPtrFrameLayout.postDelayed(new Runnable() {
				@Override
				public void run() {
					mPtrFrameLayout.autoRefresh(false);
				}
			}, 300);
		}
	}
	
	@Override
	public void onUserVisible() {
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
			if (ACTION_REFRESH_HOME_LIST.equals(action)) {
				mPtrFrameLayout.autoRefresh(false);
			} 
		}
	}

	/**
	 * 获取近期动态列表
	 */
	private void getHomeList() {
		API.TalkerAPI.LoadTalker(0, 0, page, PAGE_COUNT, new RequestCallback<UserNote>(UserNote.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mPtrFrameLayout.refreshComplete();
				if (page > 0) {
					page--;
				}
				
				if (ex instanceof ConnectException) {
					mNetworkTipsLayout.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.showToast(getActivity(), R.string.dynamic_list_error);
				}
			}
			@Override
			public void onParseSuccess(List<UserNote> respList) {
				mPtrFrameLayout.refreshComplete();
				mNetworkTipsLayout.setVisibility(View.GONE);
				if (respList.size() < PAGE_COUNT)
					loadMoreListViewContainer.loadMoreFinish(false, false);// load more
				else
					loadMoreListViewContainer.loadMoreFinish(false, true);
				if (page == 1)
					mDataList.clear();
				mDataList.addAll(respList);
				mHomeAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (v.getTag(R.id.tag_second) != null) {
			Intent intent = new Intent(getActivity(), DynamicDetailActivity.class);
			intent.putExtra("user_note", (UserNote) v.getTag(R.id.tag_second));
			startActivity(intent);
		}
	}


}
