package cc.emw.mobile.contact;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.activity.ChatActivity;
import cc.emw.mobile.contact.adapter.ContactSelectAdapter;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.SideBar;
import cc.emw.mobile.view.SideBar.OnTouchingLetterChangedListener;

/**
 * 人员选择
 * 
 * @author shaobo.zhuang
 * 
 */
@ContentView(R.layout.activity_contact_select)
public class ContactSelectActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderOkTv;

	@ViewInject(R.id.et_contact_search)
	private EditText mSearchEt;
	@ViewInject(R.id.contact_elv_person)
	private ExpandableListView mListView;
	@ViewInject(R.id.contact_sidebar)
	private SideBar mSideBar;
	@ViewInject(R.id.contact_tv_letter)
	private TextView mTextDialog;

	public static final String EXTRA_SELECT_TYPE = "select_type";
	/** 单选 */
	public static final int RADIO_SELECT = 1;
	/** 多选 */
	public static final int MULTI_SELECT = 2;

	private Dialog mLoadingDialog; // 加载框
	private ContactSelectAdapter mSelectAdapter;
	private ArrayList<UserInfo> mDataList;
	private int mPosition, mSelectType;
	private boolean isSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getIntent().getIntExtra("position", 0);
		mSelectType = getIntent().getIntExtra(EXTRA_SELECT_TYPE, MULTI_SELECT);

		if (getIntent().hasExtra("isSend")) {
			isSend = getIntent().getBooleanExtra("isSend", false);
		}
		UserInfo user = (UserInfo) getIntent().getSerializableExtra(
				"select_user");
		ArrayList<UserInfo> selectList = (ArrayList<UserInfo>) getIntent()
				.getSerializableExtra("select_list");

		mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(R.string.contactselect);
		mHeaderOkTv.setText(R.string.ok);
		mHeaderOkTv.setVisibility(View.VISIBLE);

		mSideBar.setTextView(mTextDialog);
		mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mSelectAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					// elv.setSelection(position);
					mListView.setSelectedGroup(position);
				}
			}
		});
		mSearchEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				HelpUtil.hideSoftInput(ContactSelectActivity.this, mSearchEt);
				mSelectAdapter.setSearch(s.toString().toLowerCase().trim());
				// 展开所有
				for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
					mListView.expandGroup(i);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mDataList = new ArrayList<UserInfo>();
		mSelectAdapter = new ContactSelectAdapter(this);
		mSelectAdapter.setSelectType(mSelectType);
		if (mSelectType == RADIO_SELECT) {
			mSelectAdapter.setRadioUser(user);
		} else {
			mSelectAdapter.setSelectList(selectList);
		}
		mListView.setAdapter(mSelectAdapter);

		if (EMWApplication.personSortList != null && EMWApplication.personSortList.size() > 0) {
			getOnlineUID();
			mDataList.addAll(EMWApplication.personSortList);
			mSelectAdapter.setDataList(mDataList);
			mSelectAdapter.notifyDataSetChanged();
			for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
				mListView.expandGroup(i);
			}
		} else {
			getPersonList("");
		}

	}

	@Event({ R.id.cm_header_btn_left, R.id.cm_header_tv_right })
	private void onHeaderClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			HelpUtil.hideSoftInput(this, mSearchEt);
			onBackPressed();
			break;
		case R.id.cm_header_tv_right:
			Intent data = new Intent();
			if (mSelectType == RADIO_SELECT) {
				data.putExtra("position", mPosition);
				data.putExtra("select_user", mSelectAdapter.getSelectUser());
				if (mSelectAdapter.getSelectUser() == null) {
					ToastUtil.showToast(this, R.string.contactselect_empty);
					return;
				}
				if (isSend) {
					Intent intent = new Intent(this, ChatActivity.class);
					intent.putExtra("SenderID", mSelectAdapter.getSelectUser().ID);
					intent.putExtra("type", 1);
					intent.putExtra("name", mSelectAdapter.getSelectUser().Name);
					startActivity(intent);
				}
			} else {
				data.putExtra("position", mPosition);
				ArrayList<UserInfo> userInfos = new ArrayList<>();
				userInfos.addAll( mSelectAdapter.getSelectList());
//				data.putParcelableArrayListExtra("select_list",
//						(ArrayList<? extends Parcelable>) mSelectAdapter
//								.getSelectList());
				data.putExtra("select_list", userInfos);
				if (mSelectAdapter.getSelectList().size() == 0) {
					ToastUtil.showToast(this, R.string.contactselect_empty);
					return;
				}
			}
			setResult(Activity.RESULT_OK, data);
			finish();
			break;
		}
	}

	private void getPersonList(String keyword) {
		API.UserAPI.SearchUser(keyword, 0, false, new RequestCallback<UserInfo>(UserInfo.class, true) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(ContactSelectActivity.this, R.string.contactselect_list_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
				mLoadingDialog.show();
			}
			@Override
			public void onParseSuccess(List<UserInfo> respList) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				getOnlineUID();
				mDataList.addAll(respList);
				mSelectAdapter.setDataList(mDataList);
				mSelectAdapter.notifyDataSetChanged();
				// 展开所有
				for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
					mListView.expandGroup(i);
				}
				EMWApplication.personSortList = mDataList;
			}
		});
	}

	/**
	 * 获取在线人员ID
	 */
	private void getOnlineUID() {
		// /*String url = HttpConst.Url_Passport+"?a=onlines";
		// HttpUtils http = new HttpUtils();
		// http.configCookieStore(PrefsUtil.readLoginCookie());
		// http.configCurrentHttpCacheExpiry(1);
		// http.send(HttpMethod.POST, url, new
		// RequestListener<Integer>(Integer.class) {
		// @Override
		// public void onStart() {
		// mLoadingDialog = createLoadingDialog("正在加载...");
		// mLoadingDialog.show();
		// }
		// @Override
		// public void onSuccess(List<Integer> respList) {
		// mLoadingDialog.dismiss();
		// mSelectAdapter.setOnlineIdList(respList);
		// mSelectAdapter.setDataList(mDataList);
		// mSelectAdapter.notifyDataSetChanged();
		// // 展开所有
		// for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length;
		// i++) {
		// mListView.expandGroup(i);
		// }
		// }
		//
		// @Override
		// public void onFailure(HttpException error, String msg) {
		// mLoadingDialog.dismiss();
		// // Toast.makeText(ContactSelectActivity.this,
		// error.getExceptionCode()+" "+msg, Toast.LENGTH_SHORT).show();
		// }
		// });*/
	}
}
