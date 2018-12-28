package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.fragment.talker.DynamicFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteSendTypes;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.PersonTextView;

/**
 * 动态·新建公告
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_notice)
public class NoticeActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_tv_left)
	private TextView mHeaderCancelTv; // 顶部条取消
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderSendTv; // 顶部条发布
	
	@ViewInject(R.id.cm_input_et_content)
	private EditText mContentEt; // 内容
	@ViewInject(R.id.cm_select_ll_select)
	private LinearLayout mSelectRootLayout; // 选择分享人员Layout
	@ViewInject(R.id.cm_select_tv_select)
	private TextView mSelectTv; // 分享范围
	@ViewInject(R.id.cm_select_fl_select)
	private FlowLayout mSelectFlowLayout; // 分享人员Layout
	
	private Dialog mLoadingDialog; //加载框
	private ArrayList<UserInfo> selectList; // 分享人员列表数据
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackTip(true);
        initView();
    }
    
    private void initView() {
    	mHeaderCancelTv.setVisibility(View.VISIBLE);
    	mHeaderCancelTv.setText(R.string.cancel);
        mHeaderTitleTv.setText(R.string.notice);
        mHeaderSendTv.setText(R.string.finish);
        mHeaderSendTv.setVisibility(View.VISIBLE);
        mContentEt.setHint(R.string.notice_content_hint);
    }
    
    @Event({R.id.cm_header_tv_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_tv_left:
				HelpUtil.hideSoftInput(this, mContentEt);
				onBackPressed();
				break;
			case R.id.cm_header_tv_right:
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
    	startActivityForResult(intent, 1);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
				case 1:
					mSelectFlowLayout.removeAllViews();
					selectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
					for (UserInfo user : selectList) {
						addPersonItem(user);
					}
					if (selectList.size() > 0) {
						mSelectTv.setHint("");
					} else {
						mSelectTv.setHint(R.string.share_range_hint);
					}
					break;
			}
    	}
    }
    /**
     * 显示选择的分享人员
     */
    private void addPersonItem(UserInfo user) {
		PersonTextView childView = new PersonTextView(this);
		childView.setTag(user);
		childView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectFlowLayout.removeView(v);
				selectList.remove((UserInfo) v.getTag());
				if (selectList.size() == 0) {
					mSelectTv.setHint(R.string.share_range_hint);
				}
			}
		});
		childView.setText(user.Name);
		mSelectFlowLayout.addView(childView);
    }
    
    /**
     * 发布
     * @param content
     */
    private void send(String content) {
    	UserNote un = new UserNote();
		un.Type = UserNoteAddTypes.Notice;
		un.Content = content;
		un.UserID = PrefsUtil.readUserInfo().ID;
		un.Roles = new ArrayList<NoteRole>();
		
		ArrayList<NoteRole> nrList = new ArrayList<NoteRole>();
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
		
		API.TalkerAPI.SaveTalker(un, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(NoticeActivity.this, R.string.publish_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(NoticeActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
					sendBroadcast(intent); // 刷新首页列表
					finish();
				} else {
					ToastUtil.showToast(NoticeActivity.this, R.string.publish_error);
				} 
			}
		});
    }
}
