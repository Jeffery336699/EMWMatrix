package cc.emw.mobile.file.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.file.FilePreviewActivity;
import cc.emw.mobile.file.FileTalkerActivity;
import cc.emw.mobile.file.FileUpdateActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cn.aigestudio.downloader.bizs.DLManager;

/**
 * 知识库列表Adapter
 * @author shaobo.zhuang
 */
public class FileListAdapter extends BaseAdapter implements OnClickListener {
	
	private Context mContext;
	private ArrayList<Files> mDataList;
	private int lastPosition = -1;
	private Dialog mLoadingDialog; //加载框
	private Handler handler;
	private boolean isExpand; //是否可以展开显示操作条
	private static SparseIntArray mProgressMap;
	private int power;
	
	public FileListAdapter(Context context,
						   ArrayList<Files> dataList) {
		this(context, dataList, null, true);
	}

	public FileListAdapter(Context context,
						   ArrayList<Files> dataList, int power) {
		this(context, dataList, null, true);
		this.power = power;
	}

	public FileListAdapter(Context context,
						   ArrayList<Files> dataList, boolean isExpand) {
		this(context, dataList, null, isExpand);
	}
	
	public FileListAdapter(Context context,
						   ArrayList<Files> dataList, Handler handler) {
		this(context, dataList, handler, true);
	}
	
	public FileListAdapter(Context context,
						   ArrayList<Files> dataList, Handler handler, boolean isExpand) {
		this.mContext = context;
		this.mDataList = dataList;
		this.handler = handler;
		this.isExpand = isExpand;
		this.mProgressMap = new SparseIntArray();
	}

	public void setProgressMap(int fid, int progress) {
		mProgressMap.put(fid, progress);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDataList != null ? mDataList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_filelist, null);
			vh.iconIv = (ImageView) convertView.findViewById(R.id.iv_filelist_icon);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_filelist_name);
			vh.timeTv = (TextView) convertView.findViewById(R.id.tv_filelist_time);
			vh.sizeTv = (TextView) convertView.findViewById(R.id.tv_filelist_size);
			vh.projectTv = (TextView) convertView.findViewById(R.id.tv_filelist_project);
			vh.moreBtn = (IconTextView) convertView.findViewById(R.id.itv_filelist_more);
			vh.moreLayout = (LinearLayout) convertView.findViewById(R.id.ll_filelist_more);
			vh.deleteLayout = (RelativeLayout) convertView.findViewById(R.id.rl_filelist_delete);
			vh.cancelLayout = (RelativeLayout) convertView.findViewById(R.id.rl_filelist_cancel);
			vh.shareLayout = (RelativeLayout) convertView.findViewById(R.id.rl_filelist_share);
			vh.downloadLayout = (RelativeLayout) convertView.findViewById(R.id.rl_filelist_download);
			vh.openLayout = (RelativeLayout) convertView.findViewById(R.id.rl_filelist_open);
			vh.updateLayout = (RelativeLayout) convertView.findViewById(R.id.rl_filelist_update);

			vh.openItv = (IconTextView) convertView.findViewById(R.id.itv_filelist_open);
			vh.downloadItv = (IconTextView) convertView.findViewById(R.id.itv_filelist_download);
			vh.updateItv = (IconTextView) convertView.findViewById(R.id.itv_filelist_update);
			vh.shareItv = (IconTextView) convertView.findViewById(R.id.itv_filelist_share);
			vh.cancelItv = (IconTextView) convertView.findViewById(R.id.itv_filelist_cancel);
			vh.deleteItv = (IconTextView) convertView.findViewById(R.id.itv_filelist_delete);

			vh.infoLayout = (RelativeLayout) convertView.findViewById(R.id.rl_filelist_info);
			vh.downLayout = (LinearLayout) convertView.findViewById(R.id.ll_filelist_down);
			vh.progressBar = (ProgressBar) convertView.findViewById(R.id.pb_filelist_download);
			vh.existItv = (IconTextView) convertView.findViewById(R.id.itv_filelist_exist);
			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		final Files file = mDataList.get(position);
		
		vh.nameTv.setText(file.Name);
		vh.timeTv.setText(file.UpdateTime);
		vh.sizeTv.setText(FileUtil.getReadableFileSize(file.Length));
		
		vh.moreBtn.setVisibility(isExpand ? View.VISIBLE : View.GONE);
		
		if (file.Type == 1) {
			vh.iconIv.setImageResource(R.drawable.list_ico_folder);
			vh.sizeTv.setVisibility(View.GONE);
			vh.projectTv.setVisibility(View.GONE);
//			vh.moreBtn.setImageResource(R.drawable.list_btn_next);
			vh.moreBtn.setIconText("eb69");
			vh.moreBtn.setTextColor(Color.parseColor("#CBCBCB"));
			vh.moreBtn.setOnClickListener(null);
			vh.moreBtn.setClickable(false);
			vh.moreBtn.setVisibility(View.VISIBLE);

			vh.infoLayout.setVisibility(View.VISIBLE);
			vh.downLayout.setVisibility(View.GONE);
			vh.existItv.setVisibility(View.GONE);
		} else {
			vh.iconIv.setImageResource(FileUtil.getResIconId(file.Name));
			vh.sizeTv.setVisibility(View.VISIBLE);
			vh.projectTv.setVisibility(View.GONE);
//			vh.moreBtn.setImageResource(R.drawable.list_btn_sel_menu);
			vh.moreBtn.setIconText("ec7e");
			vh.moreBtn.setTextColor(lastPosition == position ? Color.parseColor("#FF7E00"):Color.parseColor("#CBCBCB"));
			vh.moreBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (vh.downLayout.getVisibility() == View.VISIBLE) {
						String fileUrl = HelpUtil.getFileURL(file);
						DLManager.getInstance(mContext).dlCancel(fileUrl);
						mProgressMap.put(file.ID, 0);
					} else {
						if (lastPosition == position) {
							lastPosition = -1;
						} else {
							lastPosition = position;
						}
					}
					notifyDataSetChanged();
				}
			});
			vh.moreBtn.setClickable(true);

			vh.deleteLayout.setOnClickListener(null);
			vh.cancelLayout.setOnClickListener(null);
			vh.shareLayout.setOnClickListener(null);
			vh.downloadLayout.setOnClickListener(null);
			vh.openLayout.setOnClickListener(null);
			vh.updateLayout.setOnClickListener(null);
			if (PrefsUtil.readUserInfo().ID == file.Creator) {
				vh.openItv.setTextColor(Color.parseColor("#4a90e2"));
				vh.downloadItv.setTextColor(Color.parseColor("#4a90e2"));
				vh.updateItv.setTextColor(Color.parseColor("#4a90e2"));
				vh.shareItv.setTextColor(Color.parseColor("#4a90e2"));
				vh.cancelItv.setTextColor(Color.parseColor("#4a90e2"));
				vh.deleteItv.setTextColor(Color.parseColor("#4a90e2"));
				vh.deleteLayout.setOnClickListener(this);
				vh.cancelLayout.setOnClickListener(this);
				vh.shareLayout.setOnClickListener(this);
				vh.downloadLayout.setOnClickListener(this);
				vh.openLayout.setOnClickListener(this);
				vh.updateLayout.setOnClickListener(this);
			} else {
				vh.openItv.setTextColor(Color.parseColor("#7f4a90e2"));
				vh.downloadItv.setTextColor(Color.parseColor("#7f4a90e2"));
				vh.updateItv.setTextColor(Color.parseColor("#7f4a90e2"));
				vh.shareItv.setTextColor(Color.parseColor("#7f4a90e2"));
				vh.cancelItv.setTextColor(Color.parseColor("#7f4a90e2"));
				vh.deleteItv.setTextColor(Color.parseColor("#7f4a90e2"));
				if (file.FilePower > 0) {
					char[] chars = Integer.toBinaryString(file.FilePower).toCharArray();
					for (int i = 0; i < chars.length; i++) {
						if (chars[i] == '1') {
							if (i == 0) {
								vh.openItv.setTextColor(Color.parseColor("#4a90e2"));//查看
								vh.openLayout.setOnClickListener(this);
							} else if (i == 1) {
								//编辑
							} else if (i == 2) {
								vh.downloadItv.setTextColor(Color.parseColor("#4a90e2"));//下载
								vh.downloadLayout.setOnClickListener(this);
							} else if (i == 3) {
								vh.shareItv.setTextColor(Color.parseColor("#4a90e2"));//共享
								vh.shareLayout.setOnClickListener(this);
							} else if (i == 4) {
								vh.deleteItv.setTextColor(Color.parseColor("#4a90e2"));//删除
								vh.deleteLayout.setOnClickListener(this);
							}
						}
					}
				}
			}

			if (mProgressMap.get(file.ID) > 0) {
				vh.moreBtn.setIconText("ec71");
				vh.moreBtn.setTextColor(Color.parseColor("#E2604E"));
				vh.infoLayout.setVisibility(View.GONE);
				vh.downLayout.setVisibility(View.VISIBLE);
				vh.progressBar.setMax((int)file.Length);
				vh.progressBar.setProgress(mProgressMap.get(file.ID));
			} else {
				vh.moreBtn.setIconText("ec7e");
				vh.moreBtn.setTextColor(Color.parseColor("#CBCBCB"));
				vh.infoLayout.setVisibility(View.VISIBLE);
				vh.downLayout.setVisibility(View.GONE);
			}

			String localPath = EMWApplication.filePath + FileUtil.getFileName(file.Url);
			vh.existItv.setTextColor(FileUtil.hasFile(localPath, file.Length) ? Color.parseColor("#4A90E2"):Color.parseColor("#CBCBCB"));
			vh.existItv.setVisibility(View.VISIBLE);
		}
		
		vh.moreLayout.setVisibility(lastPosition == position? View.VISIBLE:View.GONE);
		vh.deleteLayout.setTag(file);
		vh.cancelLayout.setTag(file);
		vh.shareLayout.setTag(file);
		vh.downloadLayout.setTag(file);
		vh.openLayout.setTag(file);
		vh.updateLayout.setTag(file);

		convertView.setTag(R.id.tag_second, file);
		return convertView;
	}

	static class ViewHolder {
		ImageView iconIv;
		TextView nameTv;
		TextView timeTv;
		TextView sizeTv;
		TextView projectTv;
		IconTextView moreBtn;
		LinearLayout moreLayout;
		RelativeLayout deleteLayout, cancelLayout, shareLayout, downloadLayout, openLayout, updateLayout;
		IconTextView openItv, downloadItv, updateItv, shareItv, cancelItv, deleteItv;
		RelativeLayout infoLayout;
		LinearLayout downLayout;
		ProgressBar progressBar;
		IconTextView existItv;
	}

	@Override
	public void onClick(View v) {
		final Files noteFile = (Files) v.getTag();
		switch (v.getId()) {
			case R.id.rl_filelist_delete: //彻底删除
				new AlertDialog(mContext).builder().setMsg(mContext.getString(R.string.deletefile_tips))
						.setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
						.setPositiveButton(mContext.getString(R.string.confirm), new OnClickListener() {
							@Override
							public void onClick(View v) {
								deleteFile(noteFile.ID);
							}
						}).setNegativeButton(mContext.getString(R.string.cancel), new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
				break;
			case R.id.rl_filelist_cancel: //作废
				new AlertDialog(mContext).builder().setMsg(mContext.getString(R.string.cancelfile_tips))
						.setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
						.setPositiveButton(mContext.getString(R.string.confirm), new OnClickListener() {
							@Override
							public void onClick(View v) {
								cancelFile(noteFile.ID);
							}
						}).setNegativeButton(mContext.getString(R.string.cancel), new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
				break;
			case R.id.rl_filelist_share: //分享
				Intent intent = new Intent(mContext, FileTalkerActivity.class);
				intent.putExtra("file_info", noteFile);
				intent.putExtra("start_anim", false);
				mContext.startActivity(intent);
				break;
			case R.id.rl_filelist_download: //下载
				if (!TextUtils.isEmpty(noteFile.Url)) { // 通过服务下载文件
					String localPath = EMWApplication.filePath + FileUtil.getFileName(noteFile.Url);
					if (!FileUtil.hasFile(localPath, noteFile.Length)) {
						lastPosition = -1;
						String fileUrl = HelpUtil.getFileURL(noteFile);
						NotificationUtil.notificationForDLAPK(mContext, fileUrl, EMWApplication.filePath, noteFile.ID, false);
					} else {
						ToastUtil.showToast(mContext, R.string.download_fileexist_tips);
					}
				}
				break;
			case R.id.rl_filelist_open: //查看
				if (noteFile.Name.contains(".doc") || noteFile.Name.contains(".docx") || noteFile.Name.contains(".xls") || noteFile.Name.contains(".xlsx")) {
					Intent previewIntent = new Intent(mContext, FilePreviewActivity.class);
					previewIntent.putExtra(FilePreviewActivity.EXTENSION, noteFile.Name);
					previewIntent.putExtra(FilePreviewActivity.FILE_ID, noteFile.ID);
					previewIntent.putExtra(FilePreviewActivity.CREATOR, noteFile.Creator);
					mContext.startActivity(previewIntent);
				} else {
					String localPath = EMWApplication.filePath + FileUtil.getFileName(noteFile.Url);
					FileUtil.openFile(mContext, localPath);
				}
				break;
			case R.id.rl_filelist_update: //更新
				Intent updateIntent = new Intent(mContext, FileUpdateActivity.class);
				updateIntent.putExtra("file_info", noteFile);
				updateIntent.putExtra("start_anim", false);
				mContext.startActivity(updateIntent);
				break;
		}
	}
	
	/**
     * 删除文件
     */
    private void deleteFile(int fileId) {
    	API.UserData.delFile(fileId, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if(mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(mContext, R.string.deletefile_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = DialogUtil.createLoadingDialog(mContext, R.string.loading_dialog_tips5);
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if(mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(mContext, R.string.deletefile_success, R.drawable.tishi_ico_gougou);
					lastPosition = -1;
					if (handler != null)
						handler.sendEmptyMessage(10); // 刷新列表
				} else {
					ToastUtil.showToast(mContext, R.string.deletefile_error);
				} 
			}
		});
    }

	/**
	 * 作废文件
	 */
	private void cancelFile(int fileId) {
		API.UserData.cancellFile(fileId, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if(mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(mContext, R.string.cancelfile_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = DialogUtil.createLoadingDialog(mContext, R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if(mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(mContext, R.string.cancelfile_success, R.drawable.tishi_ico_gougou);
					lastPosition = -1;
					if (handler != null)
						handler.sendEmptyMessage(10); // 刷新列表
				} else {
					ToastUtil.showToast(mContext, R.string.cancelfile_error);
				}
			}
		});
	}

	public static String getFilePowerText(String power) {
		StringBuilder powerText = new StringBuilder();
		if (!TextUtils.isEmpty(power) && TextUtils.isDigitsOnly(power)) {
			if (Integer.valueOf(power) == 0) {
				powerText.append("无");
			} else {
				char[] chars = power.toCharArray();
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] == '1') {
						if (i == 0) {
							powerText.append("查看");
						} else if (i == 1) {
							if (!TextUtils.isEmpty(powerText))
								powerText.append("、");
							powerText.append("编辑");
						} else if (i == 2) {
							if (!TextUtils.isEmpty(powerText))
								powerText.append("、");
							powerText.append("下载");
						} else if (i == 3) {
							if (!TextUtils.isEmpty(powerText))
								powerText.append("、");
							powerText.append("共享");
						} else if (i == 4) {
							if (!TextUtils.isEmpty(powerText))
								powerText.append("、");
							powerText.append("删除");
						}
					}
				}
			}
		}
		return powerText.toString();
	}
}
