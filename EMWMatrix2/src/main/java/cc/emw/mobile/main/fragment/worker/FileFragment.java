package cc.emw.mobile.main.fragment.worker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.file.FileListActivity;
import cc.emw.mobile.file.adapter.FileAdapter;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.RequestCallback;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 知识库Fragment
 * 
 * @author shaobo.zhuang
 * 
 */
@ContentView(R.layout.fragment_file)
public class FileFragment extends BaseFragment implements
		OnItemClickListener {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderMenuBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn; // 顶部条右菜单按钮
	
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView mListView; // 知识库列表

	private FileAdapter mFileAdapter; // 知识库adapter
	private ArrayList<Files> mDataList; // 知识库列表数据

	public static final String ACTION_REFRESH_HOME_LIST = "cc.emw.mobile.refresh_home_list"; // 刷新的action
	private MyBroadcastReceive mReceive;
	private String headerTitle;
	private int page = 1; // 第几页，第1页为1，每下一页+1
	private static final int PAGE_COUNT = 10; // 页数

	public static FileFragment newInstance(String title) {
		FileFragment fragment = new FileFragment();
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
		mHeaderMenuBtn.setImageResource(R.drawable.nav_btn_menu);
		mHeaderMenuBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(headerTitle);
		mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_share);
		mHeaderMoreBtn.setVisibility(View.INVISIBLE);
		
		view.findViewById(R.id.rl1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FileListActivity.class);
				intent.putExtra("file_type", 3);
				intent.putExtra("file_name", getString(R.string.file_myfile));
				startActivity(intent);
			}
		});
		view.findViewById(R.id.rl2).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), FileListActivity.class);
						intent.putExtra("file_type", 4);
						intent.putExtra("file_name", getString(R.string.file_sharefile));
						startActivity(intent);
					}
				});
		view.findViewById(R.id.rl3).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FileListActivity.class);
				intent.putExtra("file_type", 5);
				intent.putExtra("file_name", getString(R.string.file_cancelfile));
				startActivity(intent);
			}
		});
		
		/*initView();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_REFRESH_HOME_LIST);
		mReceive = new MyBroadcastReceive();
		getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
*/	}

	@Event({ R.id.cm_header_btn_left, R.id.cm_header_btn_right })
	private void onHeaderClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
			break;
		case R.id.cm_header_btn_right:
			break;
		}
	}
	
	private void initView() {
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
				getFoldList("");
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
		mDataList = new ArrayList<Files>();
		mFileAdapter = new FileAdapter(getActivity(), mDataList);
		mListView.setAdapter(mFileAdapter);
		mListView.setOnItemClickListener(this);
		loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
			@Override
			public void onLoadMore(LoadMoreContainer loadMoreContainer) {
				// 请求下一页数据
				page++;
				getFoldList("");
			}
		});
	}

	@Override
	public void onFirstUserVisible() {
		/*if (mPtrFrameLayout != null) {
			mPtrFrameLayout.postDelayed(new Runnable() {
				@Override
				public void run() {
					mPtrFrameLayout.autoRefresh(false);
				}
			}, 100);
		}*/
	}

	@Override
	public void onDestroy() {
//		getActivity().unregisterReceiver(mReceive); // 取消监听
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
	private void getFoldList(String keyword) {
		API.UserData.GetFoldList(keyword, new RequestCallback<Files>(Files.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mPtrFrameLayout.refreshComplete();
				if (page > 0) {
					page--;
				}
			}
			@Override
			public void onParseSuccess(List<Files> respList) {
				mPtrFrameLayout.refreshComplete();
				if (respList.size() < PAGE_COUNT)
					loadMoreListViewContainer.loadMoreFinish(false, false);// load more
				else
					loadMoreListViewContainer.loadMoreFinish(false, true);
				if (page == 1)
					mDataList.clear();
				mDataList.addAll(respList);
				mFileAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (v.getTag(R.id.tag_second) != null) {
			Intent intent = new Intent(getActivity(), FileListActivity.class);
			intent.putExtra("note_file", (Files) v.getTag(R.id.tag_second));
			startActivity(intent);
		}
	}

}
