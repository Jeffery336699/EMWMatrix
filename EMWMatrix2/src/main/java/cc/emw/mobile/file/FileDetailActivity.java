package cc.emw.mobile.file;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.RelationCalendarActivity;
import cc.emw.mobile.contact.RelationProjectActivity;
import cc.emw.mobile.contact.RelationTaskActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserFilePowerType;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;

import com.zf.iosdialog.widget.AlertDialog;

/**
 * 知识库项详情
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_detail)
public class FileDetailActivity extends BaseActivity {
	
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn; // 
	
	@ViewInject(R.id.iv_filedetail_icon)
	private TextView mIconIv; // 
	@ViewInject(R.id.tv_filedetail_name)
	private TextView mNameTv; // 
	@ViewInject(R.id.tv_filedetail_size)
	private TextView mSizeTv; // 所有文件总大小
	@ViewInject(R.id.tv_filedetail_uploadtime)
	private TextView mUploadTimeTv; // 上传时间
	@ViewInject(R.id.tv_filedetail_sharescope)
	private TextView mShareScopeTv; // 分享范围
	
	@ViewInject(R.id.tv_filedetail_projectcount)
	private TextView mProjectCountTv; // 工作项目数量
	@ViewInject(R.id.tv_filedetail_taskcount)
	private TextView mTaskCountTv; // 相关任务数量
	@ViewInject(R.id.tv_filedetail_schedulecount)
	private TextView mScheduleCountTv; // 活动日程数量
	
	@ViewInject(R.id.btn_filedetail_delete)
	private TextView mDeleteBtn; // 删除
	@ViewInject(R.id.btn_filedetail_share)
	private TextView mShareBtn; // 分享
	@ViewInject(R.id.btn_filedetail_download)
	private TextView mDownloadBtn; // 下载
	@ViewInject(R.id.btn_filedetail_open)
	private TextView mOpenBtn; // 查看
	
	private ArrayList<UserFilePower> mPowerList; // 提交分享人员列表数据
	private ArrayList<UserInfo> mSelectList; // 分享人员列表数据
	
	private Dialog mLoadingDialog; //加载框
	private Files noteFile;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteFile = (Files) getIntent().getSerializableExtra("note_file");
        
        initView();
        
        if (noteFile != null) {
        	loadFilePower(noteFile.ID);
            getMyTalkerCountByFileId(noteFile.ID);
        }
    }
    
    private void initView() {
    	mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.filedetail);
    	mHeaderMoreBtn.setImageResource(R.drawable.cm_header_btn_more);
        mHeaderMoreBtn.setVisibility(View.GONE);
        
        mNameTv.setText(noteFile.Name);
        mSizeTv.setText(FileUtil.getReadableFileSize(noteFile.Length));
        mUploadTimeTv.setText(noteFile.UpdateTime);
        
        mPowerList = new ArrayList<UserFilePower>();
        mSelectList = new ArrayList<UserInfo>();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left:
				onBackPressed();
				break;
			case R.id.cm_header_btn_right:
				break;
    	}
    }
    @Event({R.id.ll_filedetail_project, R.id.ll_filedetail_task, R.id.ll_filedetail_schedul})
    private void onRelationClick(View v) {
    	Intent intent = null;
    	switch (v.getId()) {
			case R.id.ll_filedetail_project:
				intent = new Intent(this, RelationProjectActivity.class);
				break;
			case R.id.ll_filedetail_task:
				intent = new Intent(this, RelationTaskActivity.class);
				break;
			case R.id.ll_filedetail_schedul:
				intent = new Intent(this, RelationCalendarActivity.class);
				break;
    	}
    	if (intent != null) {
    		intent.putExtra("file_info", noteFile);
    		startActivity(intent);
    	}
    }   
    @Event({R.id.btn_filedetail_delete, R.id.btn_filedetail_share, R.id.btn_filedetail_download, R.id.btn_filedetail_open})
    private void onActionClick(View v) {
    	switch (v.getId()) {
			case R.id.btn_filedetail_download:
				// 通过服务下载文件
				if (noteFile.Name != null) {
					if (!FileUtil.hasFile(EMWApplication.filePath + noteFile.Url)) {
						String fileUrl = String.format(Const.DOWN_FILE_URL,
								PrefsUtil.readUserInfo().CompanyCode,
								noteFile.Url);
						NotificationUtil.notificationForDLAPK(this, fileUrl, EMWApplication.filePath);
					} else {
						ToastUtil.showToast(this, R.string.download_fileexist_tips);
					}
				}
				break;
			case R.id.btn_filedetail_share: //分享文件 跳转到人员选择列表
				Intent intent = new Intent(this, ContactSelectActivity.class);
				intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
				intent.putExtra("select_list", mSelectList);
				startActivityForResult(intent, ContactSelectActivity.MULTI_SELECT);
				break;
			case R.id.btn_filedetail_open:
				FileUtil.openFile(this, EMWApplication.filePath + noteFile.Url);
				break;
			case R.id.btn_filedetail_delete:
				/*if (noteFile.CreateUser != PrefsUtil.readUserInfo().getID()) {
					Toast.makeText(FileDetailActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
					return;
				}*/
				new AlertDialog(FileDetailActivity.this).builder().setMsg(getString(R.string.deletefile_tips))
				.setPositiveButton(getString(R.string.ok), new OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteFile(noteFile.ID);
					}
				}).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
				break;
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
    			case ContactSelectActivity.MULTI_SELECT:
    				mSelectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
    				// 用户集合
    				ArrayList<UserFilePower> upList = new ArrayList<UserFilePower>();
    				for (UserInfo simpleUser : mSelectList) {
    					UserFilePower filePower = new UserFilePower();
    					filePower.ID = simpleUser.ID;
    					filePower.Power = UserFilePowerType.View;
    					filePower.Type = NoteRoleTypes.User;
    					upList.add(filePower);
    				}
    				shareFile(upList, noteFile.ID);
    				break;
    		}
		}
	}
   
    private void shareFile(List<UserFilePower> upList, int fileID) {
    	API.UserData.DoFilePower(upList, fileID, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileDetailActivity.this, R.string.file_share_error);
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
					ToastUtil.showToast(FileDetailActivity.this, R.string.file_share_success, R.drawable.tishi_ico_gougou);
					loadFilePower(noteFile.ID);
				} else {
					ToastUtil.showToast(FileDetailActivity.this, R.string.file_share_error);
				}
			}
		});
    }
    
    /*
    @OnClick(R.id.cm_select_ll_select)
    private void onSelectClick(View v) {
    	Intent intent = new Intent(this, ContactSelectActivity.class);
		intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
    	intent.putExtra("select_list", mSelectList);
    	startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
				case 1:
					mSelectFlowLayout.removeAllViews();
					mSelectList = (ArrayList<SimpleUser>) data.getSerializableExtra("select_list");
					if (mSelectList.size() > 0) {
						mSelectTv.setHint("");
					} else {
						mSelectTv.setHint(R.string.useropr_share_range); 
					}
					for (SimpleUser su : mSelectList) {
						boolean flag = false;
						for (UserFilePower power : mPowerList) {
							if (su.getID() == power.ID) {
								flag = true;
								// 重复人员不需添加
								break;
							}
						}
						if (!flag) {
							UserFilePower power = new UserFilePower();
							power.ID = su.getID();
							power.name = su.getName();
							power.Power = 1;
							power.Type = 1;
							mPowerList.add(power);
							addPersonItem(power);
						}
					}
					saveSharePerson(mPowerList);
					break;
			}
    	}
    }
    
    *//**
     * 显示选择的分享人员
     *//*
    private void addPersonItem(UserFilePower power) {
		PersonTextView childView = new PersonTextView(this);
		childView.setTag(power);
		childView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectFlowLayout.removeView(v);
				changeSharePerson((UserFilePower) v.getTag());
				mPowerList.remove((UserFilePower) v.getTag());
				if (mSelectList.size() == 0) {
					mSelectTv.setHint(R.string.useropr_share_range);
				}
			}
		});
		childView.setText(power.name);
		mSelectFlowLayout.addView(childView);
    }
    
    *//**
     * 获取分享人员列表
     *//*
    private void getSharePersonList() {
    	HttpUtils http = new HttpUtils();
        http.configCookieStore(PrefsUtil.readLoginCookie());
		RequestParam param = new RequestParam();
		param.setStringParams(String.valueOf(noteFile.ID));
		http.send(HttpMethod.POST, HttpConst.Url_Client+"?t=Chatter.NoteAccess&m=LoadFilePower", param, new RequestListener<UserFilePower>(UserFilePower.class) {

			@Override
			public void onStart() {
				mLoadingDialog = createLoadingDialog("正在加载...");
				mLoadingDialog.show();
			}
			
			@Override
			public void onSuccess(List<UserFilePower> respList) {
				mLoadingDialog.dismiss();
				mPowerList.addAll(respList);
				for (UserFilePower power : mPowerList) {
					if (power.ID == 0) {
						power.name = "全体人员";
					} else {
						if (EMWApplication.personList != null && EMWApplication.personList.size() > 0) {
							for (SimpleUser su : EMWApplication.personList) {
								if (su.getID() == power.ID) {
									power.name = su.getName();
									break;
								}
							}
						}
					}
					addPersonItem(power);// 显示拥有权限的人
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mLoadingDialog.dismiss();
				Toast.makeText(FileDetailActivity.this, error.getExceptionCode()+" "+msg, Toast.LENGTH_SHORT).show();
			}
		});
    }
    *//**
     * 删除某个分享人员
     * @param power
     *//*
    private void changeSharePerson(UserFilePower power) {
    	HttpUtils http = new HttpUtils();
        http.configCookieStore(PrefsUtil.readLoginCookie());
		RequestParam param = new RequestParam();
		param.setStringParams(power, "0", String.valueOf(noteFile.ID));
		http.send(HttpMethod.POST, HttpConst.Url_Client+"?t=Chatter.NoteAccess&m=ChangePower", param, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				mLoadingDialog = createLoadingDialog("正在处理...");
				mLoadingDialog.show();
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				mLoadingDialog.dismiss();
				if (responseInfo.result != null && Integer.valueOf(responseInfo.result) > 0) {
					Toast.makeText(FileDetailActivity.this, R.string.file_tip5, Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(FileDetailActivity.this, "文件共享设置失败!", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mLoadingDialog.dismiss();
				Toast.makeText(FileDetailActivity.this, error.getExceptionCode()+" "+msg, Toast.LENGTH_SHORT).show();
			}
		});
    }
    *//**
     * 保存选择的分享人员列表
     * @param powerList
     *//*
    private void saveSharePerson(List<UserFilePower> powerList) {
    	HttpUtils http = new HttpUtils();
        http.configCookieStore(PrefsUtil.readLoginCookie());
		RequestParam param = new RequestParam();
		param.setStringParams(powerList, "0", String.valueOf(noteFile.ID));
		http.send(HttpMethod.POST, HttpConst.Url_Client+"?t=Chatter.NoteAccess&m=ChangePower", param, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				mLoadingDialog = createLoadingDialog("正在处理...");
				mLoadingDialog.show();
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				mLoadingDialog.dismiss();
				if (responseInfo.result != null && Integer.valueOf(responseInfo.result) > 0) {
					Toast.makeText(FileDetailActivity.this, R.string.file_tip5, Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(FileDetailActivity.this, "文件共享设置失败!", Toast.LENGTH_SHORT).show();
				} 
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mLoadingDialog.dismiss();
				Toast.makeText(FileDetailActivity.this, error.getExceptionCode()+" "+msg, Toast.LENGTH_SHORT).show();
			}
		});
    }*/
    /**
     * 删除分享文件
     */
    private void deleteFile(int fileId) {
    	API.UserData.delFile(fileId, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileDetailActivity.this, R.string.deletefile_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips5);
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(FileDetailActivity.this, R.string.deletefile_success, R.drawable.tishi_ico_gougou);
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					ToastUtil.showToast(FileDetailActivity.this, R.string.deletefile_error);
				} 
			}
		});
    }
    
    private void loadFilePower(int fileId) {
    	API.UserData.LoadFilePower(fileId, new RequestCallback<UserFilePower>(UserFilePower.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileDetailActivity.this, R.string.filedetail_sharerange_error);
			}
			/*@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog("正在加载...");
				mLoadingDialog.show();
			}*/
			@Override
			public void onParseSuccess(List<UserFilePower> respList) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (respList != null && respList.size() > 0) {
					StringBuilder shareBuffer = new StringBuilder();
					for (UserFilePower filePower : respList) {
						if (!TextUtils.isEmpty(filePower.Name)) {
							shareBuffer.append(filePower.Name);
							shareBuffer.append("、");
						}
					}
					if (shareBuffer.length() > 0) {
						shareBuffer.deleteCharAt(shareBuffer.length()-1);
						mShareScopeTv.setText(shareBuffer.toString());
					}
				}
			}
		});
    }
    
    private void getMyTalkerCountByFileId(int fileId) {
    	API.TalkerAPI.GetMyTalkerCountByFileId(fileId, new RequestCallback<Integer>(Integer.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileDetailActivity.this, R.string.filedetail_relationcount_error);
			}
			/*@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog("正在加载...");
				mLoadingDialog.show();
			}*/
			@Override
			public void onParseSuccess(List<Integer> respList) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (respList != null && respList.size() >= 3) {
					int projectCount = respList.get(0);
					int taskCount = respList.get(1);
					int scheduleCount = respList.get(2);
					mProjectCountTv.setText(String.valueOf(projectCount));
					mProjectCountTv.setVisibility(projectCount > 0 ? View.VISIBLE : View.GONE);
					
					mTaskCountTv.setText(String.valueOf(taskCount));
					mTaskCountTv.setVisibility(taskCount > 0 ? View.VISIBLE : View.GONE);
					
					mScheduleCountTv.setText(String.valueOf(scheduleCount));
					mScheduleCountTv.setVisibility(scheduleCount > 0 ? View.VISIBLE : View.GONE);
				}
			}
		});
    }
}
