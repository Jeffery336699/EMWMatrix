package cc.emw.mobile.dynamic.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.dynamic.adapter.DynamicShareAdapter;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 动态·转发Fragment
 * 
 * @author shaobo.zhuang
 * 
 */
@ContentView(R.layout.fragment_dynamic_favour)
public class DynamicShareFragment extends BaseFragment {

	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView mListView; // 近期动态列表
//	@ViewInject(R.id.ll_dynamic_blank)
//	private LinearLayout mBlankLayout;
//	@ViewInject(R.id.ll_network_tips)
//	private LinearLayout mNetworkTipsLayout; //无网络

	private Dialog mLoadingDialog; //加载框
	private DynamicShareAdapter mDynamicShareAdapter; // 讨论adapter
	private ArrayList<UserNote> mDataList; // 回复列表数据
	private UserNote note; //列表传值

	public static DynamicShareFragment newInstance(UserNote un) {
		DynamicShareFragment fragment = new DynamicShareFragment();
		Bundle args = new Bundle();
		args.putSerializable("user_note", un);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		note = (UserNote)getArguments().getSerializable("user_note");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();

	}
	
	@Event(R.id.ll_network_tips)
	private void onNetworkTipsClick(View v) {
		mPtrFrameLayout.autoRefresh(false);
	}

	private void initView() {
		mPtrFrameLayout.setEnabled(false);
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
				getHomeList();
			}
		});

		// header
		final MaterialHeader header = new MaterialHeader(getActivity());
		int[] colors = getResources().getIntArray(R.array.google_colors);
		header.setColorSchemeColors(colors);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, 0, 0, DisplayUtil.dip2px(getActivity(), 15));
		header.setPtrFrameLayout(mPtrFrameLayout);

		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setDurationToCloseHeader(1500);
		mPtrFrameLayout.setHeaderView(header);
		mPtrFrameLayout.addPtrUIHandler(header);

		// binding view and data
		mDataList = new ArrayList<>();
		mDynamicShareAdapter = new DynamicShareAdapter(getActivity(), mDataList);
		mListView.setAdapter(mDynamicShareAdapter);

		/*View commentLayout = getActivity().findViewById(R.id.ll_dynamicbottom_discuss);
		EditText commentContentEt = (EditText) getActivity().findViewById(R.id.et_dynamicbottom_content);
		View commentSendItv = getActivity().findViewById(R.id.itv_dynamicbottom_send);
		mCommentContral = new CommentContral(getActivity(), commentLayout, commentContentEt, commentSendItv);
		mCommentContral.setListView(mListView);
		mHomeAdapter.setCommentContral(mCommentContral);

		setViewTreeObserver();*/
	}

	@Override
	public void onFirstUserVisible() {
		if (mPtrFrameLayout != null) {
			mPtrFrameLayout.postDelayed(new Runnable() {
				@Override
				public void run() {
					mPtrFrameLayout.autoRefresh(false);
				}
			}, 100);
		}
	}

	/**
	 * 获取近期动态列表
	 */
	private void getHomeList() {
		API.TalkerAPI.getTalkerByTypeId(note.ID, new RequestCallback<UserNote>(UserNote.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mPtrFrameLayout.refreshComplete();
//				mBlankLayout.setVisibility(View.GONE);
				if (ex instanceof ConnectException) {
//					mNetworkTipsLayout.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.showToast(getActivity(), R.string.dynamic_list_error);
				}
			}
			@Override
			public void onParseSuccess(List<UserNote> respList) {
				mPtrFrameLayout.refreshComplete();
//				mNetworkTipsLayout.setVisibility(View.GONE);
				if (respList != null && respList.size() > 0) {
//					mBlankLayout.setVisibility(View.GONE);
					mDataList.clear();
					mDataList.addAll(respList);
					mDynamicShareAdapter.setData(mDataList);
					mDynamicShareAdapter.notifyDataSetChanged();
				} else {
//					mBlankLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}
}
