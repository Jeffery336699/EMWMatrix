package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.adapter.ShareVoteAdapter;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteSendTypes;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 动态·新建公告
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_share2)
public class NoticeActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderCancelBtn; // 顶部条取消
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderSendTv; // 顶部条发布

	@ViewInject(R.id.cm_input_et_content)
	private EditText mContentEt; // 内容
	@ViewInject(R.id.share_btn_file)
	private ImageButton mFileBtn; // 文件按钮
	@ViewInject(R.id.share_btn_img)
	private ImageButton mImgBtn; // 图片按钮
	@ViewInject(R.id.share_btn_link)
	private ImageButton mLinkBtn; // 链接按钮
	@ViewInject(R.id.share_btn_vote)
	private ImageButton mVoteBtn; // 投票按钮
	@ViewInject(R.id.ll_sharefile_root)
	private LinearLayout mFileRootLayout; // 文件根Layout
	@ViewInject(R.id.ll_sharefile_item)
	private LinearLayout mFileLayout; // 文件Layout
	@ViewInject(R.id.ll_shareimg_root)
	private LinearLayout mImgRootLayout; // 图片根Layout
	@ViewInject(R.id.fl_shareimg_item)
	private FlowLayout mImgFlowLayout; // 图片Layout
	@ViewInject(R.id.ll_sharelink_root)
	private LinearLayout mLinkRootLayout; // 链接根Layout
	@ViewInject(R.id.et_sharelink_url)
	private EditText mLinkUrlEt; // 链接URL
	@ViewInject(R.id.et_sharelink_desc)
	private EditText mLinkNameEt; // 链接名称
	@ViewInject(R.id.ll_sharevote_root)
	private LinearLayout mVoteRootLayout; // 投票根Layout
	@ViewInject(R.id.lv_sharevote_item)
	private ExListView mVoteLv; // 投票列表
	@ViewInject(R.id.cm_select_tv_name)
	private TextView mSelectTv; // 分享范围
	@ViewInject(R.id.cm_select_fl_select)
	private FlowLayout mSelectFlowLayout; // 分享人员Layout

	private static final int CHOSE_FILE_CODE = 1; //选择文件requestCode
	private static final int CHOSE_IMG_CODE = 2; //选择图片requestCode
	private static final int CHOSE_PERSON_CODE = 3; //选择人员requestCode

	private DisplayImageOptions options;
	private Dialog mLoadingDialog; //加载框
	private ArrayList<UserNote.UserNoteFile> fileList, imgList; // 文件、图片列表数据
	private ArrayList<UserNote.UserNoteVote> voteList; // 投票列表数据
	private ArrayList<UserInfo> selectList; // 分享人员列表数据
	private ShareVoteAdapter shareAdapter; // 投票adapter
	private int addType = 0; //当前选择的类型
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
		setBackTip(true);
		initView();

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.gray_1)
//		.showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
//		.showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.build(); // 创建配置过得DisplayImageOption对象

		/*try {
			findViewById(R.id.ll_share_root).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		} catch (Exception e) {

		}*/
	}
	private void initView() {
		findViewById(R.id.cm_header_bar).setBackgroundColor(Color.TRANSPARENT);
		mHeaderCancelBtn.setVisibility(View.GONE);
		mHeaderTitleTv.setText(R.string.notice);
		mHeaderSendTv.setText(R.string.publish);
		mHeaderSendTv.setVisibility(View.GONE);
		mContentEt.setHint(R.string.notice_content_hint);

		fileList = new ArrayList<UserNote.UserNoteFile>();
		imgList = new ArrayList<UserNote.UserNoteFile>();
		voteList = new ArrayList<UserNote.UserNoteVote>();
		UserNote.UserNoteVote vote1 = new UserNote.UserNoteVote();
		UserNote.UserNoteVote vote2 = new UserNote.UserNoteVote();
		voteList.add(vote1);
		voteList.add(vote2);
		shareAdapter = new ShareVoteAdapter(this, voteList);
		mVoteLv.setAdapter(shareAdapter);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
	private void onHeaderClick(View v) {
		switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				HelpUtil.hideSoftInput(this, mContentEt);
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:
				String content = mContentEt.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {
					ToastUtil.showToast(this, R.string.empty_content_tips);
				} else {
					if (addType == UserNoteAddTypes.Link) {
						String linkUrl = mLinkUrlEt.getText().toString().trim();
						String linkName = mLinkNameEt.getText().toString().trim();
						if (TextUtils.isEmpty(linkUrl)) {
							ToastUtil.showToast(this, R.string.share_empty_linkurl);
							return;
						}
						if (TextUtils.isEmpty(linkName)) {
							ToastUtil.showToast(this, R.string.share_empty_linkdesc);
							return;
						}
					} else if (addType == UserNoteAddTypes.Vote) {
						for (int i = 0, size = voteList.size(); i < size; i++) {
							UserNote.UserNoteVote vote = voteList.get(i);
							if (!validate(vote, i+1))
								return;
						}
					}
					send(content);
				}
				break;
		}
	}

	@Event({R.id.share_btn_file, R.id.share_btn_img, R.id.share_btn_link, R.id.share_btn_vote})
	private void onSwitchClick(View v) {
		HelpUtil.hideSoftInput(this, mContentEt);
		switch (v.getId()) {
			case R.id.share_btn_file:
				addType = UserNoteAddTypes.File;
//				choseFileFromSystemFile();
				mFileRootLayout.setVisibility(View.VISIBLE);
				mImgRootLayout.setVisibility(View.GONE);
				mLinkRootLayout.setVisibility(View.GONE);
				mVoteRootLayout.setVisibility(View.GONE);
				break;
			case R.id.share_btn_img:
				addType = UserNoteAddTypes.Image;
//				choseImageFromGallery();
				mFileRootLayout.setVisibility(View.GONE);
				mImgRootLayout.setVisibility(View.VISIBLE);
				mLinkRootLayout.setVisibility(View.GONE);
				mVoteRootLayout.setVisibility(View.GONE);
				break;
			case R.id.share_btn_link:
				addType = UserNoteAddTypes.Link;
				mFileRootLayout.setVisibility(View.GONE);
				mImgRootLayout.setVisibility(View.GONE);
				mLinkRootLayout.setVisibility(View.VISIBLE);
				mVoteRootLayout.setVisibility(View.GONE);
				break;
			case R.id.share_btn_vote:
				addType = UserNoteAddTypes.Vote;
				mFileRootLayout.setVisibility(View.GONE);
				mImgRootLayout.setVisibility(View.GONE);
				mLinkRootLayout.setVisibility(View.GONE);
				mVoteRootLayout.setVisibility(View.VISIBLE);
				break;
		}
	}

	@Event({R.id.btn_sharefile_add, R.id.btn_shareimg_add, R.id.btn_sharevote_add})
	private void onAddActionClick(View v) {
		switch (v.getId()) {
			case R.id.btn_sharefile_add: //添加文件
				Intent fileIntent = new Intent(this, FileSelectActivity.class);
				startActivityForResult(fileIntent, CHOSE_FILE_CODE);
				break;
			case R.id.btn_shareimg_add: //添加图片
				Intent imgIntent = new Intent(this, FileSelectActivity.class);
				startActivityForResult(imgIntent, CHOSE_IMG_CODE);
				break;
			case R.id.btn_sharevote_add: //添加投票
				UserNote.UserNoteVote vote = new UserNote.UserNoteVote();
				voteList.add(vote);
				shareAdapter.notifyDataSetChanged();
				break;
		}
	}

	@Event(R.id.cm_select_ll_select)
	private void onSelectClick(View v) {
		Intent intent = new Intent(this, ContactSelectActivity.class);
		intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
		intent.putExtra("select_list", selectList);
		startActivityForResult(intent, CHOSE_PERSON_CODE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mContentEt.clearFocus();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case CHOSE_FILE_CODE: //选择的文件
					ArrayList<ApiEntity.Files> fileList = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
					for (int i = 0; i < fileList.size(); i++) {
						UserNote.UserNoteFile file = HelpUtil.files2UserNoteFile(fileList.get(i));
						addFileItem(file);
					}
					break;
				case CHOSE_IMG_CODE: //选择的图片
					ArrayList<ApiEntity.Files> imgList = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
					for (int i = 0; i < imgList.size(); i++) {
						UserNote.UserNoteFile file = HelpUtil.files2UserNoteFile(imgList.get(i));
						addImgItem(file);
					}
					break;
				case CHOSE_PERSON_CODE: //选择的人员
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
	 * 从本地文件管理选取文件
	 */
	private void choseFileFromSystemFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, "请选择文件!"),
					CHOSE_FILE_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			ToastUtil.showToast(this, "请安装文件管理器");
		}
	}
	/**
	 * 从本地相册选取图片
	 */
	private void choseImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(intent, CHOSE_IMG_CODE);
	}
	/**
	 * 显示选择的文件
	 * @param file
	 */
	private void addFileItem(final UserNote.UserNoteFile file) {
		final View childView = LayoutInflater.from(this).inflate(R.layout.share_tab_file_item, null);
//    	ImageButton delBtn = (ImageButton) childView.findViewById(R.id.btn_sharefile_del);
		IconTextView delTv = (IconTextView) childView.findViewById(R.id.tv_sharefile_del);
		ImageView iconIv = (ImageView) childView.findViewById(R.id.iv_sharefile_icon);
		TextView nameTv = (TextView) childView.findViewById(R.id.tv_sharefile_name);
		TextView timeTv = (TextView) childView.findViewById(R.id.tv_sharefile_time);
		TextView sizeTv = (TextView) childView.findViewById(R.id.tv_sharefile_size);
		TextView projectTv = (TextView) childView.findViewById(R.id.tv_sharefile_project);

		delTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog(NoticeActivity.this).builder()
						.setMsg(getString(R.string.deletefile_tips))
						.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mFileLayout.removeView(childView);
								fileList.remove(file);
							}
						})
						.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
							@Override
							public void onClick(View v) {}
						}).show();

			}
		});
		iconIv.setImageResource(FileUtil.getResIconId(file.FileName));
		nameTv.setText(file.FileName);
		timeTv.setText("");
		sizeTv.setText(FileUtil.getReadableFileSize(file.Length));
		mFileLayout.addView(childView);
		fileList.add(file);
	}

	/**
	 * 显示选择的图片
	 * @param file
     */
	private void addImgItem(final UserNote.UserNoteFile file) {
		final View childView = LayoutInflater.from(this).inflate(R.layout.share_tab_image_item, null);
		ImageView iconIv = (ImageView) childView.findViewById(R.id.iv_shareimg_icon);
//    	ImageView delBtn = (ImageView) childView.findViewById(R.id.iv_shareimg_del);
		IconTextView delTv = (IconTextView) childView.findViewById(R.id.tv_shareimg_del);

		String url = HelpUtil.getFileURL(file);
		ImageLoader.getInstance().displayImage(url, iconIv, options);

		delTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog(NoticeActivity.this).builder()
						.setMsg(getString(R.string.deleteimage_tips))
						.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mImgFlowLayout.removeView(childView);
								imgList.remove(file);
							}
						})
						.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
							@Override
							public void onClick(View v) {}
						}).show();
			}
		});
		FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
		params.rightMargin = DisplayUtil.dip2px(this, 10);
		mImgFlowLayout.addView(childView, params);
		imgList.add(file);
	}

	/**
	 * 发布
	 * @param content
	 */
	private void send(String content) {
		UserNote un = new UserNote();
		un.Type = UserNoteAddTypes.Notice;
		un.AddType = addType;
		un.Content = content;
		un.UserID = PrefsUtil.readUserInfo().ID;
		un.Roles = new ArrayList<NoteRole>();
		switch (addType) {
			case UserNoteAddTypes.Image:
				un.AddProperty = new Gson().toJson(imgList);
				break;
			case UserNoteAddTypes.File:
				un.AddProperty = new Gson().toJson(fileList);
				break;
			case UserNoteAddTypes.Link:
				UserNote.UserNoteLink link = new UserNote.UserNoteLink();
				link.addr = mLinkUrlEt.getText().toString().trim();
				link.desc = mLinkNameEt.getText().toString().trim();
				un.AddProperty = new Gson().toJson(link);
				break;
			case UserNoteAddTypes.Vote:
				ArrayList<UserNote.UserRootVote> rootVoteList = new ArrayList<UserNote.UserRootVote>();
				UserNote.UserRootVote rootVote = new UserNote.UserRootVote();
				rootVote.Content = voteList;
				rootVote.Type = 1;
				rootVoteList.add(rootVote);
				un.AddProperty = new Gson().toJson(rootVoteList);
				break;
		}
		if (imgList.size() == 0 && fileList.size() == 0 && addType != UserNoteAddTypes.Link && addType != UserNoteAddTypes.Vote) {
			un.AddType = 0;
		}
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

		API.TalkerAPI.SaveTalker(un, new RequestCallback<ApiEntity.APIResult>(ApiEntity.APIResult.class) {
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
			/*@Override
			public void onParseSuccess(ApiEntity.APIResult respInfo) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (respInfo != null && respInfo.State == 1) {
					ToastUtil.showToast(NoticeActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
					sendBroadcast(intent); // 刷新首页列表
					finish();
				} else {
					ToastUtil.showToast(NoticeActivity.this, R.string.publish_error);
				}
			}*/
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(NoticeActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
					sendBroadcast(intent); // 刷新首页列表
					finish();
				} else {
					ToastUtil.showToast(NoticeActivity.this, R.string.publish_error);
				}
			}
		});
	}

	/**
	 * 非空验证
	 * @param vote
	 * @param position
	 * @return
	 */
	private boolean validate(UserNote.UserNoteVote vote, int position) {
		boolean isSuccess = false;
		String tip = "";
		if (TextUtils.isEmpty(vote.Text)) {
			tip = getString(R.string.share_empty_votename);
		} else {
			isSuccess = true;
		}
		if (!isSuccess) {
			ToastUtil.showToast(this, "【选项"+position+"】"+tip);
		}

		return isSuccess;
	}
}
