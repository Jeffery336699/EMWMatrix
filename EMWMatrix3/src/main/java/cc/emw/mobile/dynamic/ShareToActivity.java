package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.UserNoteShareTo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteSendTypes;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.FlowLayout;

/**
 * 动态详情·分享到Talker
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_share_to3)
public class ShareToActivity extends BaseActivity {

//	@ViewInject(R.id.cm_header_btn_left) private ImageButton mHeaderCancelBtn; // 顶部条取消
//	@ViewInject(R.id.cm_header_tv_title) private TextView mHeaderTitleTv; // 顶部条标题
//	@ViewInject(R.id.cm_header_tv_right) private TextView mHeaderSendTv; // 顶部条发布
	
	@ViewInject(R.id.cm_input_et_content) private EditText mContentEt; // 内容
	@ViewInject(R.id.tv_shareto_name) private TextView mShareNameTv; //分享原文的姓名
	@ViewInject(R.id.tv_shareto_content) private TextView mShareContentTv; //分享原文的内容
	@ViewInject(R.id.cm_select_ll_select) private LinearLayout mSelectRootLayout; // 选择分享人员Layout
	@ViewInject(R.id.cm_select_tv_name) private TextView mSelectTv; // 分享范围
	@ViewInject(R.id.cm_select_fl_select) private FlowLayout mSelectFlowLayout; // 分享人员Layout
	
	private Dialog mLoadingDialog; //加载框
	private ArrayList<UserInfo> selectList; // 分享人员列表数据
    private UserNote userNote; //传值

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userNote = (UserNote) getIntent().getSerializableExtra("user_note");
        initView();
    }
    
    private void initView() {
		/*mHeaderCancelBtn.setVisibility(View.GONE);
        mHeaderTitleTv.setText(R.string.shareto);
        mHeaderSendTv.setText(R.string.publish);
        mHeaderSendTv.setVisibility(View.GONE);*/
        mContentEt.setHint(R.string.content_hint);
        
        mShareNameTv.setText(userNote.UserName);
		ArrayList<ApiEntity.UserSchedule> scheduleList = userNote.info.schedule;
		if (scheduleList != null && scheduleList.size() > 0) {
			mShareContentTv.setText(scheduleList.get(0).Title);
		} else {
			mShareContentTv.setText(userNote.Content);
		}

    }

	/*@Override
	public void onBackPressed() {
		finish();
	}*/

	@Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:
				String content = mContentEt.getText().toString().trim();
		    	if (TextUtils.isEmpty(content)) {
		    		ToastUtil.showToast(this, R.string.empty_content_tips);
		    	} else {
		    		send(content);
		    	}
				break;
    	}
    }
    @Event(R.id.cm_select_ll_select)
    private void onSelectClick(View v) {
    	Intent intent = new Intent(this, ContactSelectActivity.class);
		intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
    	intent.putExtra("select_list", selectList);
		intent.putExtra("has_oneself", false);
		intent.putExtra("start_anim", false);
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		intent.putExtra("click_pos_y", location[1]);
    	startActivityForResult(intent, 1);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
				case 1: //选择的人员
					mSelectFlowLayout.removeAllViews();
					selectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < selectList.size(); i++) {
						if (i < 3) {
							UserInfo user = selectList.get(i);
							if (i != 0) {
								builder.append("、");
							}
							builder.append(user.Name);
						} else {
							builder.append("等" + selectList.size() + "人");
							break;
						}
					}
					mSelectTv.setText(builder);
					mSelectTv.setHint(selectList.size() > 0 ? "" : "公开");
					break;
			}
    	}
    }

    /**
     * 发布
     * @param content
     */
    private void send(String content) {
    	UserNote un = new UserNote();
		un.Type = UserNoteAddTypes.Share;
		un.Content = content;
		un.UserID = PrefsUtil.readUserInfo().ID;
		un.TypeId = userNote.ID;
		if (userNote.info != null && userNote.info.shareNote != null) {
			un.ShareId = userNote.info.shareNote.ID;
		} else {
			un.ShareId = userNote.ID;
		}

		ArrayList<UserNoteShareTo> shareToList = new ArrayList<>();
		UserNoteShareTo shareTo = new UserNoteShareTo();
		shareTo.NoteID = userNote.ID;
		shareTo.UserName = userNote.UserName;
		shareTo.Content = userNote.Content;
		shareToList.add(shareTo);
		un.Property = new Gson().toJson(shareToList);
		
		un.Roles = new ArrayList<>();
		ArrayList<NoteRole> nrList = new ArrayList<>();
		if (selectList != null && selectList.size() > 0) {
			un.SendType = UserNoteSendTypes.Private;  //0 公共 1 私有
			for (int i = 0, size = selectList.size(); i < size; i++) {
				NoteRole role = new NoteRole();
				role.ID = selectList.get(i).ID;
				role.Name = selectList.get(i).Name;
				role.Image = selectList.get(i).Image;
				role.Type = NoteRoleTypes.User;
				nrList.add(role);
			}
			un.Roles = nrList;
		} else {
			un.SendType = UserNoteSendTypes.Public;
		}
		
		API.TalkerAPI.SaveTalker(un, new RequestCallback<ApiEntity.APIResult>(ApiEntity.APIResult.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(ShareToActivity.this, R.string.publish_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}
			/*@Override
			public void onParseSuccess(ApiEntity.APIResult respInfo) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (respInfo != null && respInfo.State == 1) {
					ToastUtil.showToast(ShareToActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
					sendBroadcast(intent); // 刷新首页列表
					finish();
				} else {
					ToastUtil.showToast(ShareToActivity.this, R.string.publish_error);
				}
			}*/
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(ShareToActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
					sendBroadcast(intent); // 刷新首页列表
					finish();
				} else {
					ToastUtil.showToast(ShareToActivity.this, R.string.publish_error);
				} 
			}
		});
    }
}
