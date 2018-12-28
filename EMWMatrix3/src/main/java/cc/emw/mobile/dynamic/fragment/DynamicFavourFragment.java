package cc.emw.mobile.dynamic.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Date;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.dynamic.adapter.DynamicDiscussAdapter2;
import cc.emw.mobile.dynamic.adapter.DynamicFavourAdapter;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.contral.CommentContral;
import cc.emw.mobile.main.fragment.TalkerFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 动态·赞Fragment
 * 
 * @author shaobo.zhuang
 * 
 */
@ContentView(R.layout.fragment_dynamic_favour)
public class DynamicFavourFragment extends BaseFragment {

	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView mListView; // 近期动态列表
	@ViewInject(R.id.ll_dynamic_blank)
	private LinearLayout mBlankLayout;
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout; //无网络

	private Dialog mLoadingDialog; //加载框
	private DynamicFavourAdapter mDynamicDiscussAdapter; // 讨论adapter
	private ArrayList<ApiEntity.UserInfo> mDataList; // 回复列表数据
	private UserNote note; //列表传值

	public static DynamicFavourFragment newInstance(UserNote un) {
		DynamicFavourFragment fragment = new DynamicFavourFragment();
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
//		mDataList = new ArrayList<>();
//		mDataList.addAll(note.EnjoyList);
		mDynamicDiscussAdapter = new DynamicFavourAdapter(getActivity(), note);
		mListView.setAdapter(mDynamicDiscussAdapter);

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
		/*if (mPtrFrameLayout != null) {
			mPtrFrameLayout.postDelayed(new Runnable() {
				@Override
				public void run() {
					mPtrFrameLayout.autoRefresh(false);
				}
			}, 100);
		}*/
	}

}
