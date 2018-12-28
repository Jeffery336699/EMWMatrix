package cc.emw.mobile.main.contral;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;

/**
 * @Description: 控制EdittextView的显示和隐藏，以及发布动作，根据回复的位置调整listview的位置
 *
 */
public class CommentContral {
	private static final String TAG = CommentContral.class.getSimpleName();
	private View mInputLayout;
	private EditText mEditText;
	private View mSendBtn;
	private int mRootPosition;
	private int mCommentType;
	private int mCommentPosition;
	private ListView mListView;
	private Context mContext;

	private Dialog mLoadingDialog;
	private int mScreenHeight;
	/**
	 * 选择动态条目的高
	 */
	private int mSelectCircleItemH;
	/**
	 * 选择的commentItem距选择的CircleItem底部的距离
	 */
	private int mSelectCommentItemBottom;
	
	public ListView getListView() {
		return mListView;
	}

	public void setListView(ListView mListView) {
		this.mListView = mListView;
	}

	public CommentContral(Context context, View inputLayout, EditText editText, View sendBtn){
		mContext = context;
		mInputLayout = inputLayout;
		mEditText = editText;
		mSendBtn = sendBtn;
		mSendBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				editTextBodyVisible(View.GONE);
				String content = mEditText.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {
					ToastUtil.showToast(mContext, R.string.empty_content_tips);
				} else {
					reply(content, Integer.valueOf(v.getTag(R.id.tag_first).toString()), Integer.valueOf(v.getTag(R.id.tag_second).toString()));
				}
			}
		});
	}

	public void setScreenHeight(int screenHeight) {
		mScreenHeight = screenHeight;
	}

	/**
	 * 
	* @Title: editTextBodyVisible 
	* @Description: 评论时显示发布布局，评论完隐藏，根据不同位置调节listview的滑动
	* @param  visibility
	* @param  commentType  0:发布评论   1：回复评论
	* @param  rootPosition 外层ListView的item位置
	* @param  commentPosition 嵌套评论的ListView的item位置
	* @param  noteID
	* @param  un 评论数据
	* @return void    返回类型
	* @throws
	 */
	public void editTextBodyVisible(int visibility, int commentType, int rootPosition, int commentPosition, int noteID, ApiEntity.UserNote un) {
		this.mRootPosition = rootPosition;
		this.mCommentType = commentType;
		this.mCommentPosition = commentPosition;

		mEditText.setTag(un);
		mSendBtn.setTag(R.id.tag_first, noteID);
		mSendBtn.setTag(R.id.tag_second, commentType);
		if (un != null && commentType == 1) {
			String name = "";
			if (EMWApplication.personMap != null && EMWApplication.personMap.get(un.UserID) != null) {
				name = EMWApplication.personMap.get(un.UserID).Name;
			}
			mEditText.setHint("回复 " + name);
		} else {
			mEditText.setHint(R.string.dynamicdetail_edittext_hint);
		}

		editTextBodyVisible(visibility);

		measure(rootPosition, commentType);
	}

	private void measure(int rootPosition, int commentType) {
		if (mListView != null) {
			int firstPosition = mListView.getFirstVisiblePosition();
			View selectCircleItem = mListView.getChildAt(rootPosition - firstPosition);
			mSelectCircleItemH = selectCircleItem.getHeight();

			if (commentType == 1) {//回复评论的情况
				ExListView commentLv = (ExListView) selectCircleItem.findViewById(R.id.lv_dynamic_discuss);
				if (commentLv != null) {
					int firstCommentPosition = commentLv.getFirstVisiblePosition();
					//找到要回复的评论view,计算出该view距离所属动态底部的距离
					View selectCommentItem = commentLv.getChildAt(mCommentPosition - firstCommentPosition);
					if (selectCommentItem != null) {
						mSelectCommentItemBottom = 0;
						View parentView = selectCommentItem;
						do {
							int subItemBottom = parentView.getBottom();
							parentView = (View) parentView.getParent();
							if (parentView != null) {
								mSelectCommentItemBottom += (parentView.getHeight() - subItemBottom);
							}
						} while (parentView != null && parentView != selectCircleItem);
					}
				}
			}
		}
	}

	public void handleListViewScroll() {
		int keyH = EMWApplication.mKeyBoardH; //键盘的高度
		int inputLayoutH = DisplayUtil.dip2px(mContext, 49); //整个输入条的高度
		int screenlH = mScreenHeight; //整个应用屏幕的高度
		int barH = DisplayUtil.dip2px(mContext, 70); //状态栏和顶部条高度
		int listviewOffset = screenlH - mSelectCircleItemH - keyH - inputLayoutH - barH;
		if(mCommentType == 1){
			listviewOffset = listviewOffset + mSelectCommentItemBottom;
		}
		Log.d(TAG, "offset="+listviewOffset + " &mSelectCommentItemBottom="+mSelectCommentItemBottom + " &mSelectCircleItemH="+mSelectCircleItemH + " &screenlH="+screenlH + " &keyH="+keyH + " &inputLayoutH="+inputLayoutH);
		if(mListView!=null){
			mListView.setSelectionFromTop(mRootPosition, listviewOffset);
		}
	}

	public void editTextBodyVisible(int visibility) {
		if (mInputLayout != null) {
			mInputLayout.setVisibility(visibility);
			if (View.VISIBLE == visibility) {
				mEditText.requestFocus();
				//弹出键盘
				HelpUtil.showSoftInput(mEditText.getContext(), mEditText);
			} else if (View.GONE == visibility) {
				//隐藏键盘
				HelpUtil.hideSoftInput(mEditText.getContext(), mEditText);
			}
		}
	}

	public String getEditTextString() {
		String result = "";
		if(mEditText!=null){
			result =  mEditText.getText().toString();
		}
		return result;
	}
	
	public void clearEditText(){
		if(mEditText!=null){
			mEditText.setText("");
		}
	}


	/**
	 * 回复
	 *
	 * @param content
	 */
	private void reply(String content, final int noteID, int commentType) {
		final UserNote rev = new UserNote();
		rev.ID = 0;
		rev.Content = content;
		if (mEditText != null && mEditText.getTag() instanceof ApiEntity.UserNote) {
			ApiEntity.UserNote subrev = (ApiEntity.UserNote) mEditText.getTag();
			rev.PID = subrev.ToUserId > 0 ? subrev.PID : subrev.ID;
			rev.TopId = subrev.ID;
			if (commentType == 1) {
				rev.ToUserId = subrev.UserID;
			}

			rev.ToUserIdInfo = new ApiEntity.TalkerUserInfo(); //回复的对方信息
			rev.ToUserIdInfo.ID = subrev.UserID;
			rev.ToUserIdInfo.Name = subrev.UserIdInfo != null ? subrev.UserIdInfo.Name : "";
		}

		API.TalkerAPI.SaveTalkerRev(rev, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(mContext, R.string.dynamicdetail_comment_error);
			}

			@Override
			public void onStarted() {
				mLoadingDialog = DialogUtil.createLoadingDialog(mContext, R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}

			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(mContext, R.string.dynamicdetail_comment_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
					intent.putExtra("note_id", noteID);
					rev.ID = Integer.valueOf(result);
					rev.UserID = PrefsUtil.readUserInfo().ID;
					rev.UserIdInfo = HelpUtil.userInfo2TalkerUserInfo(PrefsUtil.readUserInfo());
					rev.CreateTime = StringUtils.getCurTimeStr();
					Gson gson = new Gson();
					intent.putExtra("rev_note", gson.fromJson(gson.toJson(rev), ApiEntity.UserNote.class));
					mContext.sendBroadcast(intent); //刷新Talker列表
					mEditText.setText("");
					mEditText.setTag(null);
					mEditText.setHint(R.string.dynamicdetail_edittext_hint);
				} else {
					ToastUtil.showToast(mContext, R.string.dynamicdetail_comment_error);
				}
			}
		});
	}
}
