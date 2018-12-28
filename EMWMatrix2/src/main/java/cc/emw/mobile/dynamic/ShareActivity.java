package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
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
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.UserNoteFile;
import cc.emw.mobile.entity.UserNote.UserNoteLink;
import cc.emw.mobile.entity.UserNote.UserNoteVote;
import cc.emw.mobile.entity.UserNote.UserRootVote;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.main.fragment.talker.DynamicFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteSendTypes;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.PersonTextView;

/**
 * 动态·新建分享(文件/图片/链接/投票)
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_share)
public class ShareActivity extends BaseActivity {
	
	@ViewInject(R.id.cm_header_tv_left)
	private TextView mHeaderCancelTv; // 顶部条取消
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
//	@ViewInject(R.id.share_btn_delallfile)
//	private Button mDelAllFileBtn; // 删除所有文件按钮
	@ViewInject(R.id.ll_shareimg_root)
	private LinearLayout mImgRootLayout; // 图片根Layout
	@ViewInject(R.id.fl_shareimg_item)
	private FlowLayout mImgFlowLayout; // 图片Layout
//	@ViewInject(R.id.share_btn_delallimg)
//	private Button mDelAllImgBtn; // 删除所有图片按钮
	@ViewInject(R.id.ll_sharelink_root)
	private LinearLayout mLinkRootLayout; // 链接根Layout
	@ViewInject(R.id.et_sharelink_url)
	private EditText mLinkUrlEt; // 链接URL
	@ViewInject(R.id.et_sharelink_desc)
	private EditText mLinkNameEt; // 链接名称
	@ViewInject(R.id.ll_sharevote_root)
	private LinearLayout mVoteRootLayout; // 投票根Layout
	@ViewInject(R.id.et_sharevote_content)
	private Button mVoteAddBtn; // 添加投票按钮
	@ViewInject(R.id.lv_sharevote_item)
	private ExListView mVoteLv; // 投票列表
	@ViewInject(R.id.cm_select_ll_select)
	private LinearLayout mSelectRootLayout; // 选择分享人员Layout
	@ViewInject(R.id.cm_select_tv_select)
	private TextView mSelectTv; // 分享范围
	@ViewInject(R.id.cm_select_fl_select)
	private FlowLayout mSelectFlowLayout; // 分享人员Layout
	
	private static final int CHOSE_FILE_CODE = 1;
	private static final int CHOSE_IMG_CODE = 2;
	private static final int CHOSE_PERSON_CODE = 3;
	
	private DisplayImageOptions options;
	private Dialog mLoadingDialog; //加载框
	private ArrayList<UserNoteFile> fileList, imgList; // 文件、图片列表数据
	private ArrayList<UserNoteVote> voteList; // 投票列表数据
	private ArrayList<UserInfo> selectList; // 分享人员列表数据
	private ShareVoteAdapter shareAdapter; // 投票adapter
	private int addType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackTip(true);
        initView();
        
        options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.color.gray_1)
		.showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
		.build(); // 创建配置过得DisplayImageOption对象
    }
    private void initView() {
        mHeaderCancelTv.setVisibility(View.VISIBLE);
        mHeaderCancelTv.setText(R.string.cancel);
        mHeaderTitleTv.setText(R.string.share);
        mHeaderSendTv.setText(R.string.finish);
        mHeaderSendTv.setVisibility(View.VISIBLE);
        mContentEt.setHint(R.string.content_hint);
        
        fileList = new ArrayList<UserNoteFile>();
        imgList = new ArrayList<UserNoteFile>();
        voteList = new ArrayList<UserNoteVote>();
        UserNoteVote vote1 = new UserNoteVote();
		UserNoteVote vote2 = new UserNoteVote();
		voteList.add(vote1);
		voteList.add(vote2);
        shareAdapter = new ShareVoteAdapter(this, voteList);
        mVoteLv.setAdapter(shareAdapter);
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
		    	    		UserNoteVote vote = voteList.get(i);
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
    
    /*@Event({R.id.share_btn_delallfile, R.id.share_btn_delallimg})
    private void onDelAllClick(View v) {
    	switch (v.getId()) {
			case R.id.share_btn_delallfile:
				mFileLayout.removeAllViews();
				mFileRootLayout.setVisibility(View.GONE);
				fileList.clear();
				break;
			case R.id.share_btn_delallimg:
				mImgFlowLayout.removeAllViews();
				mImgRootLayout.setVisibility(View.GONE);
				imgList.clear();
				break;
		}
    }*/
    
    @Event({R.id.btn_sharefile_add, R.id.btn_shareimg_add, R.id.btn_sharevote_add})
    private void onAddActionClick(View v) {
    	switch (v.getId()) {
		case R.id.btn_sharefile_add:
			Intent fileIntent = new Intent(this, FileSelectActivity.class);
			startActivityForResult(fileIntent, CHOSE_FILE_CODE);
			break;
		case R.id.btn_shareimg_add:
			Intent imgIntent = new Intent(this, FileSelectActivity.class);
			startActivityForResult(imgIntent, CHOSE_IMG_CODE);
			break;
		case R.id.btn_sharevote_add:
			UserNoteVote vote = new UserNoteVote();
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
				case CHOSE_FILE_CODE:
					ArrayList<Files> fileList = (ArrayList<Files>) data.getSerializableExtra("select_list");
					for (int i = 0; i < fileList.size(); i++) {
						UserNoteFile file = HelpUtil.files2UserNoteFile(fileList.get(i));
						addFileItem(file, i);
					}
					break;
				case CHOSE_IMG_CODE:
					ArrayList<Files> imgList = (ArrayList<Files>) data.getSerializableExtra("select_list");
					for (int i = 0; i < imgList.size(); i++) {
						UserNoteFile file = HelpUtil.files2UserNoteFile(imgList.get(i));
						addImgItem(file, i);
					}
					break;
				case CHOSE_PERSON_CODE:
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
    private void addFileItem(final UserNoteFile file, final int position) {
    	final View childView = LayoutInflater.from(this).inflate(R.layout.share_tab_file_item, null);
    	ImageButton delBtn = (ImageButton) childView.findViewById(R.id.btn_sharefile_del);
    	ImageView iconIv = (ImageView) childView.findViewById(R.id.iv_sharefile_icon);
    	TextView nameTv = (TextView) childView.findViewById(R.id.tv_sharefile_name);
    	TextView timeTv = (TextView) childView.findViewById(R.id.tv_sharefile_time);
    	TextView sizeTv = (TextView) childView.findViewById(R.id.tv_sharefile_size);
    	TextView projectTv = (TextView) childView.findViewById(R.id.tv_sharefile_project);
    	delBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog(ShareActivity.this).builder()
				.setMsg(getString(R.string.deletefile_tips))
				.setPositiveButton(getString(R.string.ok), new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFileLayout.removeView(childView);
//						fileList.remove(position);
						fileList.remove(file);
					}
				})
				.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
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
    
    private void addImgItem(final UserNoteFile file, final int s) {
    	final View childView = LayoutInflater.from(this).inflate(R.layout.share_tab_image_item, null);
    	ImageView iconIv = (ImageView) childView.findViewById(R.id.iv_shareimg_icon);
    	ImageView delBtn = (ImageView) childView.findViewById(R.id.iv_shareimg_del);
    	
    	String url = String.format(Const.DOWN_FILE_URL, PrefsUtil.readUserInfo().CompanyCode, file.Url);
		ImageLoader.getInstance().displayImage(url, iconIv, options);
    	
    	delBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog(ShareActivity.this).builder()
				.setMsg(getString(R.string.deleteimage_tips))
				.setPositiveButton(getString(R.string.ok), new OnClickListener() {
					@Override
					public void onClick(View v) {
						mImgFlowLayout.removeView(childView);
//						imgList.remove(position);
						imgList.remove(file);
					}
				})
				.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
					@Override
					public void onClick(View v) {}
				}).show();
			}
		});
    	LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mImgFlowLayout.addView(childView, params);
		imgList.add(file);
    }
    
    /**
     * 显示选择的分享人员
     * @param user
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
		un.Type = UserNoteAddTypes.Normal;
		un.AddType = addType;
		un.Content = content;
		un.UserID = PrefsUtil.readUserInfo().ID;
		un.Roles = new ArrayList<NoteRole>();
		switch (addType) {
			case UserNoteAddTypes.Image:
				un.Property = new Gson().toJson(imgList);
				break;
			case UserNoteAddTypes.File:
				un.Property = new Gson().toJson(fileList);
				break;
			case UserNoteAddTypes.Link:
				UserNoteLink link = new UserNoteLink();
				link.addr = mLinkUrlEt.getText().toString().trim();
				link.desc = mLinkNameEt.getText().toString().trim();
				un.Property = new Gson().toJson(link);
				break;
			case UserNoteAddTypes.Vote:
				ArrayList<UserRootVote> rootVoteList = new ArrayList<UserRootVote>();
				UserRootVote rootVote = new UserRootVote();
				rootVote.Content = voteList;
				rootVote.Type = 1;
				rootVoteList.add(rootVote);
				un.Property = new Gson().toJson(rootVoteList);
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
		
		API.TalkerAPI.SaveTalker(un, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(ShareActivity.this, R.string.publish_error);
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
					ToastUtil.showToast(ShareActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
					sendBroadcast(intent); // 刷新首页列表
					finish();
				} else {
					ToastUtil.showToast(ShareActivity.this, R.string.publish_error);
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
    private boolean validate(UserNoteVote vote, int position) {
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
