package cc.emw.mobile.contact;

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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.adapter.GroupSelectAdapter;
import cc.emw.mobile.contact.presenter.ContactPresenter;
import cc.emw.mobile.contact.view.ContactView;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.DisplayUtil;

@ContentView(R.layout.activity_group_select)
public class GroupSelectActivity extends BaseActivity implements ContactView {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;
	@ViewInject(R.id.cm_header_tv_right)
	private TextView tvSubmit;
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView gList; // 群组列表
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout;
	@ViewInject(R.id.rb_groups_select)
	private CheckBox checkBox;
	private ContactPresenter contactPresenter;
	private GroupSelectAdapter adapter;
	private ArrayList<GroupInfo> mDataList = new ArrayList<GroupInfo>();

	public static final String TargetG = "targetG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		mPtrFrameLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPtrFrameLayout.autoRefresh(false);
			}
		}, 100);
		refresh();
	}

	@Event(value = { R.id.cm_header_btn_left, R.id.cm_header_tv_right })
	private void onFootClick(View view) {
		switch (view.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		case R.id.cm_header_tv_right:
			Intent intent = new Intent();
			intent.putExtra(TargetG, adapter.getTargetG());
			setResult(RESULT_OK, intent);
			onBackPressed();
			break;
		}
	}

	private void refresh() {

		mPtrFrameLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
					View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame,
						gList, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				contactPresenter.getGroupList();
			}
		});
	}

	private void init() {
		mHeaderTitleTv.setText(R.string.groupselect);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		tvSubmit.setVisibility(View.VISIBLE);
		tvSubmit.setText(R.string.ok);
		contactPresenter = new ContactPresenter(this);
		adapter = new GroupSelectAdapter(this);
		gList.setAdapter(adapter);
		// header
		final MaterialHeader header = new MaterialHeader(this);
		int[] colors = getResources().getIntArray(R.array.google_colors);
		header.setColorSchemeColors(colors);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
		header.setPtrFrameLayout(mPtrFrameLayout);

		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setDurationToCloseHeader(1500);
		mPtrFrameLayout.setHeaderView(header);
		mPtrFrameLayout.addPtrUIHandler(header);
	}

	@Override
	public void disProgressDialog() {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshComplete() {
		mPtrFrameLayout.refreshComplete();
	}

	@Override
	public void showProgressDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showFollowResult(String result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showTipDialog(String tips) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showUserInfo(List<UserInfo> simpleUsers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showGroupInfo(List<GroupInfo> groupInfos) {
		mPtrFrameLayout.refreshComplete();
		mDataList.clear();
		mDataList.addAll(groupInfos);
		adapter.setData(mDataList);
		adapter.notifyDataSetChanged();
	}

}
