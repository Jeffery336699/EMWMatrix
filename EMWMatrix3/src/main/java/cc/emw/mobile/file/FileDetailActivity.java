package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brucetoo.imagebrowse.ImageBrowseFragment;
import com.brucetoo.imagebrowse.widget.ImageInfo;
import com.brucetoo.imagebrowse.widget.PhotoView;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.file.adapter.FileDetailAdapter;
import cc.emw.mobile.file.fragment.FileOtherFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.FileDown;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cn.aigestudio.downloader.bizs.DLManager;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 知识库项详情
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_detail3)
public class FileDetailActivity extends BaseActivity {
	
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderNoticeBtn; //

	@ViewInject(R.id.pb_filedetail_download)
	private ProgressBar mDownloadPb; //下载进度
	@ViewInject(R.id.iv_filedetail_icon)
	private PhotoView mIconTv; //文件图标
	@ViewInject(R.id.tv_filedetail_name)
	private TextView mNameTv; //文件名
	@ViewInject(R.id.tv_filedetail_size)
	private TextView mSizeTv; // 所有文件总大小
	@ViewInject(R.id.tv_filedetail_uploadtime)
	private TextView mUploadTimeTv; // 上传时间
	@ViewInject(R.id.tv_filedetail_project)
	private TextView mProjectTv; // 关联的项目(来源)

	@ViewInject(R.id.ll_filedetail_sharelook)
	private LinearLayout mShareLookLayout; //分享查看Layout
	@ViewInject(R.id.ll_filedetail_power)
	private LinearLayout mPowerLayout; //权限Layout
	@ViewInject(R.id.rl_filedetail_open)
	private RelativeLayout mOpenLayout; //查看Layout
	@ViewInject(R.id.itv_filedetail_open)
	private IconTextView mOpenItv;
	@ViewInject(R.id.rl_filedetail_download)
	private RelativeLayout mDownloadLayout; //下载Layout
	@ViewInject(R.id.itv_filedetail_down)
	private IconTextView mDownloadItv;
	@ViewInject(R.id.rl_filedetail_update)
	private RelativeLayout mUpdateLayout; //更新Layout
	@ViewInject(R.id.itv_filedetail_update)
	private IconTextView mUpdateItv;
	@ViewInject(R.id.rl_filedetail_share)
	private RelativeLayout mShareLayout; //分享Layout
	@ViewInject(R.id.itv_filedetail_share)
	private IconTextView mShareItv;
	@ViewInject(R.id.rl_filedetail_cancel)
	private RelativeLayout mCancelLayout; //作废Layout
	@ViewInject(R.id.itv_filedetail_cancel)
	private IconTextView mCancelItv;
	@ViewInject(R.id.rl_filedetail_delete)
	private RelativeLayout mDeleteLayout; //彻底删除Layout
	@ViewInject(R.id.itv_filedetail_delete)
	private IconTextView mDeleteItv;

	@ViewInject(R.id.tv_filedetail_desc)
	private TextView mDescTv; //文件描述
	@ViewInject(R.id.tv_filedetail_number)
	private TextView mDiscussNumberTv; //讨论数量
	@ViewInject(R.id.lv_filedetail_downlog)
	private ListView mDownLogLv; //下载记录
	@ViewInject(R.id.itv_filedetail_exist)
	private IconTextView mExistItv; //已下载；取消下载图标
	@ViewInject(R.id.tv_filedetail_down)
	private TextView mDownTv; //已下载；取消下载

	private ArrayList<FileDown> mDownLogList; // 下载记录列表数据
	private FileDetailAdapter mFileDetailAdapter; // 下载记录Adapter

	public static final String ACTION_REFRESH_FILE_DOWN = "cc.emw.mobile.refresh_file_down"; // 刷新的action
	public static final String ACTION_SAVE_RECORD_SUCCESS = "cc.emw.mobile.save_record_success"; //保存下载记录刷新
	public static final String ACTION_REFRESH_FILE_CANCEL = "cc.emw.mobile.refresh_file_cancel"; //取消下载或下载成功刷新文件列表
	private MyBroadcastReceive mReceive;

	private Dialog mLoadingDialog; //加载框
	private Files noteFile; //传值
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        noteFile = (Files) getIntent().getSerializableExtra("file_info");

        initView();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_REFRESH_FILE_DOWN);
		intentFilter.addAction(ACTION_SAVE_RECORD_SUCCESS);
		mReceive = new MyBroadcastReceive();
		registerReceiver(mReceive, intentFilter); // 注册监听

        if (noteFile != null) {
			getFileDownLog(noteFile.ID);
			/*if (noteFile.ProjectId > 0) {
				getProjectName(noteFile.ProjectId);
			}*/
        }

		try {
			findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);
		} catch (Exception e) {

		}
    }
    
    private void initView() {
        /*mHeaderBackBtn.setVisibility(View.GONE);
		mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
		mHeaderNoticeBtn.setVisibility(View.VISIBLE);*/
		mHeaderTitleTv.setText(R.string.filedetail);

		mIconTv.disenable();
		mIconTv.setImageResource(FileUtil.getResIconId(noteFile.Name));
        mNameTv.setText(noteFile.Name);
//		mDescTv.setText("");
        mSizeTv.setText(FileUtil.getReadableFileSize(noteFile.Length));
        mUploadTimeTv.setText(noteFile.UpdateTime);
//		mProjectTv.setText("");
        
		mDownLogList = new ArrayList<>();
		mFileDetailAdapter = new FileDetailAdapter(this, mDownLogList);
		mDownLogLv.setAdapter(mFileDetailAdapter);

		String localPath = EMWApplication.filePath + FileUtil.getFileName(noteFile.Url);
		if (FileUtil.hasFile(localPath, noteFile.Length)) {
			mExistItv.setTextColor(Color.parseColor("#3CC56D"));
			mExistItv.setIconText("e924");
			mExistItv.setVisibility(View.VISIBLE);
			mDownTv.setText("已下载");
		} else {
			mExistItv.setVisibility(View.GONE);
			mDownTv.setText("下载");
		}

		if (PrefsUtil.readUserInfo().ID != noteFile.Creator) {
			mShareLookLayout.setVisibility(View.GONE);
			mPowerLayout.setVisibility(View.GONE);

			mOpenItv.setTextColor(Color.parseColor("#7f4a90e2"));
			mDownloadItv.setTextColor(Color.parseColor("#7f4a90e2"));
			mUpdateItv.setTextColor(Color.parseColor("#7f4a90e2"));
			mShareItv.setTextColor(Color.parseColor("#7f4a90e2"));
			mCancelItv.setTextColor(Color.parseColor("#7f4a90e2"));
			mDeleteItv.setTextColor(Color.parseColor("#7f4a90e2"));
			mOpenLayout.setEnabled(false);
			mDownloadLayout.setEnabled(false);
			mUpdateLayout.setEnabled(false);
			mShareLayout.setEnabled(false);
			mCancelLayout.setEnabled(false);
			mDeleteLayout.setEnabled(false);
			if (noteFile.FilePower > 0) {
				char[] chars = Integer.toBinaryString(noteFile.FilePower).toCharArray();
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] == '1') {
						if (i == 0) {
							mOpenItv.setTextColor(Color.parseColor("#4a90e2"));//查看
							mOpenLayout.setEnabled(true);
						} else if (i == 1) {
							//编辑
						} else if (i == 2) {
							mDownloadItv.setTextColor(Color.parseColor("#4a90e2"));//下载
							mDownloadLayout.setEnabled(true);
						} else if (i == 3) {
							mShareItv.setTextColor(Color.parseColor("#4a90e2"));//共享
							mShareLayout.setEnabled(true);
						} else if (i == 4) {
							mDeleteItv.setTextColor(Color.parseColor("#4a90e2"));//删除
							mDeleteLayout.setEnabled(true);
						}
					}
				}
			}
		}
    }

	@Override
	protected void onResume() {
		super.onResume();
		/*if (noteFile != null) {
			getTaskReplyByFileId(noteFile.ID);
		}*/
	}

	@Override
	protected void onDestroy() {
		if (mReceive != null) {
			unregisterReceiver(mReceive);
		}
		super.onDestroy();
	}

	class MyBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String url = intent.getStringExtra("url");
			String fileUrl = HelpUtil.getFileURL(noteFile);

			if (ACTION_REFRESH_FILE_DOWN.equals(action) && fileUrl.equals(url)) {
				int progress = intent.getIntExtra("progress", 0);
				int length = intent.getIntExtra("length", 0);
				mDownloadPb.setMax(length);
				if (progress == length) {
					mDownloadPb.setProgress(0);
					mDownloadPb.setVisibility(View.GONE);
					mExistItv.setTextColor(Color.parseColor("#3CC56D"));
					mExistItv.setIconText("e924");
					mExistItv.setVisibility(View.VISIBLE);
					mDownTv.setText("已下载");
					ToastUtil.showToast(FileDetailActivity.this, "下载成功", R.drawable.tishi_ico_gougou);
					sendBroadcast(new Intent(ACTION_REFRESH_FILE_CANCEL).putExtra("fid", noteFile.ID));
				} else {
					mDownloadPb.setProgress(progress);
					mDownloadPb.setVisibility(View.VISIBLE);
					mExistItv.setTextColor(Color.parseColor("#e2604e"));
					mExistItv.setIconText("ec71");
					mExistItv.setVisibility(View.VISIBLE);
					mDownTv.setText("取消下载");
				}
			} else if (ACTION_SAVE_RECORD_SUCCESS.equals(action)) {
				if (noteFile != null) {
					getFileDownLog(noteFile.ID);
				}
			}
		}
	}

	/*@Override
	public void onBackPressed() {
		finish();
	}*/

	@Event({R.id.cm_header_btn_left9, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				onBackPressed();
				break;
			case R.id.cm_header_btn_right:
				Intent noticeIntent = new Intent(this, TestActivity.class);
				startActivity(noticeIntent);
				break;
    	}
    }
    @Event({R.id.ll_filedetail_discuss, R.id.ll_filedetail_sharelook, R.id.ll_filedetail_history, R.id.ll_filedetail_power})
    private void onRelationClick(View v) {
    	Intent intent = null;
    	switch (v.getId()) {
			case R.id.ll_filedetail_discuss: //讨论
				intent = new Intent(this, FileDiscussActivity.class);
				break;
			case R.id.ll_filedetail_sharelook: //分享查看
				intent = new Intent(this, FileLookActivity.class);
				break;
			case R.id.ll_filedetail_history: //历史版本
				intent = new Intent(this, FileHistoryActivity.class);
				break;
			case R.id.ll_filedetail_power: //权限
				intent = new Intent(this, FileShareActivity.class);
				break;
    	}
    	if (intent != null) {
    		intent.putExtra("file_info", noteFile);
			intent.putExtra("start_anim", false);
			int[] location = new int[2];
			v.getLocationOnScreen(location);
			intent.putExtra("click_pos_y", location[1]);
			startActivityForResult(intent, 10);
    	}
    }
    @Event({R.id.rl_filedetail_delete, R.id.rl_filedetail_share, R.id.rl_filedetail_download, R.id.rl_filedetail_open, R.id.rl_filedetail_cancel, R.id.rl_filedetail_update})
    private void onActionClick(View v) {
    	switch (v.getId()) {
			case R.id.rl_filedetail_download: //下载；取消下载
				if (!TextUtils.isEmpty(noteFile.Url)) {
					String fileUrl = HelpUtil.getFileURL(noteFile);
					if ("取消下载".equals(mDownTv.getText().toString())) {
						DLManager.getInstance(this).dlCancel(fileUrl);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								mDownloadPb.setProgress(0);
								mDownloadPb.setVisibility(View.GONE);
								mExistItv.setVisibility(View.GONE);
								mDownTv.setText("下载");
								sendBroadcast(new Intent(ACTION_REFRESH_FILE_CANCEL).putExtra("fid", noteFile.ID));
							}
						}, 1000);
					} else {
						String localPath = EMWApplication.filePath + FileUtil.getFileName(noteFile.Url);
						if (!FileUtil.hasFile(localPath, noteFile.Length)) { // 通过服务下载文件
							NotificationUtil.notificationForDLAPK(this, fileUrl, EMWApplication.filePath, noteFile.ID, false);
						} else {
							ToastUtil.showToast(this, R.string.download_fileexist_tips);
						}
					}
				}
				break;
			case R.id.rl_filedetail_share: //分享文件
//				Intent intent = new Intent(this, FileShareActivity.class);
				Intent intent = new Intent(this, FileTalkerActivity.class);
				intent.putExtra("file_info", noteFile);
				intent.putExtra("start_anim", false);
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				intent.putExtra("click_pos_y", location[1]);
				startActivity(intent);
				break;
			case R.id.rl_filedetail_open: //查看
				if (FileUtil.isImage(noteFile.Name)) {
					//Use of ImageBrowseFragment
					ArrayList<String> imgList = new ArrayList<>();
					imgList.add(HelpUtil.getFileURL(noteFile));

					Bundle bundle = new Bundle();
					bundle.putStringArrayList(ImageInfo.INTENT_IMAGE_URLS, imgList);
					bundle.putParcelable(ImageInfo.INTENT_CLICK_IMAGE_INFO, mIconTv.getInfo());
					bundle.putInt(ImageInfo.INTENT_CLICK_IMAGE_POSITION, 0);
//							bundle.putParcelableArrayList(ImageInfo.INTENT_IMAGE_INFOS, imgImageInfos);
					getSupportFragmentManager().beginTransaction().replace(Window.ID_ANDROID_CONTENT, ImageBrowseFragment.newInstance(bundle), "ViewPagerFragment")
							.addToBackStack(null).commit();
				} else if (noteFile.Name.contains(".doc") || noteFile.Name.contains(".docx") || noteFile.Name.contains(".xls") || noteFile.Name.contains(".xlsx")) {
					Intent previewIntent = new Intent(this, FilePreviewActivity.class);
					previewIntent.putExtra(FilePreviewActivity.EXTENSION, noteFile.Name);
					previewIntent.putExtra(FilePreviewActivity.FILE_ID, noteFile.ID);
					previewIntent.putExtra(FilePreviewActivity.CREATOR, noteFile.Creator);
					startActivity(previewIntent);
				} else {
					String localPath = EMWApplication.filePath + FileUtil.getFileName(noteFile.Url);
					FileUtil.openFile(this, localPath);
				}
				break;
			case R.id.rl_filedetail_delete: //删除
				new AlertDialog(FileDetailActivity.this).builder().setMsg(getString(R.string.deletefile_tips))
						.setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
						.setPositiveButton(getString(R.string.confirm), new OnClickListener() {
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
			case R.id.rl_filedetail_cancel: //作废
				new AlertDialog(FileDetailActivity.this).builder().setMsg(getString(R.string.cancelfile_tips))
						.setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
						.setPositiveButton(getString(R.string.confirm), new OnClickListener() {
							@Override
							public void onClick(View v) {
								cancelFile(noteFile.ID);
							}
						}).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
				break;
			case R.id.rl_filedetail_update: //更新
				Intent updateIntent = new Intent(this, FileUpdateActivity.class);
				updateIntent.putExtra("file_info", noteFile);
				updateIntent.putExtra("start_anim", false);
				int[] location2 = new int[2];
				v.getLocationOnScreen(location2);
				updateIntent.putExtra("click_pos_y", location2[1]);
				startActivity(updateIntent);
				break;
    	}
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case 10:
					setResult(Activity.RESULT_OK);
					finish();
					break;
			}
		}
	}

	/**
	 * 获取下载记录
	 * @param fid
     */
	private void getFileDownLog(int fid) {
		API.UserData.FileDownLog(fid, new RequestCallback<FileDown>(FileDown.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
//				ToastUtil.showToast(FileDetailActivity.this, R.string.file_share_error);
			}
			/*@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}*/
			@Override
			public void onParseSuccess(List<FileDown> respList) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				mDownLogList.clear();
				mDownLogList.addAll(respList);
				mFileDetailAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 获取项目名称
	 * @param pid
     */
	private void getProjectName(int pid) {
		API.TalkerAPI.GetProjectById(pid, new RequestCallback<UserProject>(UserProject.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
//				ToastUtil.showToast(FileDetailActivity.this, R.string.file_share_error);
			}
			@Override
			public void onParseSuccess(UserProject respInfo) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				mProjectTv.setText(respInfo.Name);
			}
		});
	}

	/**
	 * 获取讨论数量
	 * @param fid
     */
	private void getTaskReplyByFileId(int fid) {
		API.TalkerAPI.GetTaskReplyByTaskId(fid, 1, new RequestCallback<TaskReply>(TaskReply.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
			}

			@Override
			public void onParseSuccess(List<TaskReply> respList) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (respList != null && respList.size() > 0 && respList.get(0).ReplyCount > 0) {
					mDiscussNumberTv.setText(String.valueOf(respList.get(0).ReplyCount));
					mDiscussNumberTv.setVisibility(View.VISIBLE);
				} else {
					mDiscussNumberTv.setVisibility(View.GONE);
				}
			}
		});
	}

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
					updateFragmentFileAction();
					finish();
				} else {
					ToastUtil.showToast(FileDetailActivity.this, R.string.deletefile_error);
				} 
			}
		});
    }

    //刷新文件
	private void updateFragmentFileAction(){
		Intent update = new Intent(FileOtherFragment.UPDATE_FILE);
		sendBroadcast(update);
	}
	/**
	 * 作废文件
	 */
	private void cancelFile(int fileId) {
		API.UserData.cancellFile(fileId, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if(mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileDetailActivity.this, R.string.cancelfile_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = DialogUtil.createLoadingDialog(FileDetailActivity.this, R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if(mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(FileDetailActivity.this, R.string.cancelfile_success, R.drawable.tishi_ico_gougou);
					updateFragmentFileAction();
					finish();
				} else {
					ToastUtil.showToast(FileDetailActivity.this, R.string.cancelfile_error);
				}
			}
		});
	}

}
