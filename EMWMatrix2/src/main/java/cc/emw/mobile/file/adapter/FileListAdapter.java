package cc.emw.mobile.file.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;

import com.zf.iosdialog.widget.AlertDialog;

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
	private int mfileID = 0; //记录点击的文件ID
	private boolean isExpand; //是否可以展开显示操作条
	
	public FileListAdapter(Context context,
			ArrayList<Files> dataList) {
		this(context, dataList, null, true);
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
	}
	
	/**
	 * 获取需要分享的文件ID
	 * @return 返回文件ID
	 */
	public int getFileID(){
		return mfileID;
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
			vh.moreBtn = (ImageButton) convertView.findViewById(R.id.btn_filelist_more);
			vh.moreLayout = (LinearLayout) convertView.findViewById(R.id.ll_filelist_more);
			vh.deleteBtn = (Button) convertView.findViewById(R.id.btn_filelist_delete);
			vh.moveBtn = (Button) convertView.findViewById(R.id.btn_filelist_move);
			vh.shareBtn = (Button) convertView.findViewById(R.id.btn_filelist_share);
			vh.downloadBtn = (Button) convertView.findViewById(R.id.btn_filelist_download);
			vh.openBtn = (Button) convertView.findViewById(R.id.btn_filelist_open);
			
			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		Files file = mDataList.get(position);
		
		vh.nameTv.setText(file.Name);
		vh.timeTv.setText(file.UpdateTime);
		vh.sizeTv.setText(FileUtil.getReadableFileSize(file.Length));
		
		vh.moreBtn.setVisibility(isExpand ? View.VISIBLE : View.GONE);
		
		if (file.Type == 1) {
			vh.iconIv.setImageResource(R.drawable.list_ico_folder);
			vh.sizeTv.setVisibility(View.GONE);
			vh.projectTv.setVisibility(View.GONE);
			vh.moreBtn.setImageResource(R.drawable.list_btn_next);
			vh.moreBtn.setOnClickListener(null);
		} else {
			vh.iconIv.setImageResource(FileUtil.getResIconId(file.Name));
			vh.sizeTv.setVisibility(View.VISIBLE);
			vh.projectTv.setVisibility(View.GONE);
			vh.moreBtn.setImageResource(R.drawable.list_btn_sel_menu);
			vh.moreBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (lastPosition == position) {
						lastPosition = -1;
					} else {
						lastPosition = position;
					}
					notifyDataSetChanged();
				}
			});
		}
		
		vh.moreLayout.setVisibility(lastPosition == position? View.VISIBLE:View.GONE);
		vh.deleteBtn.setTag(file);
		vh.moveBtn.setTag(file);
		vh.shareBtn.setTag(file);
		vh.downloadBtn.setTag(file);
		vh.openBtn.setTag(file);
		vh.deleteBtn.setOnClickListener(this);
		vh.moveBtn.setOnClickListener(this);
		vh.shareBtn.setOnClickListener(this);
		vh.downloadBtn.setOnClickListener(this);
		vh.openBtn.setOnClickListener(this);
		convertView.setTag(R.id.tag_second, file);
		return convertView;
	}

	static class ViewHolder {
		ImageView iconIv;
		TextView nameTv;
		TextView timeTv;
		TextView sizeTv;
		TextView projectTv;
		ImageButton moreBtn;
		LinearLayout moreLayout;
		Button deleteBtn, moveBtn, shareBtn, downloadBtn, openBtn;
	}

	@Override
	public void onClick(View v) {
		final Files noteFile = (Files) v.getTag();
		switch (v.getId()) {
			case R.id.btn_filelist_delete:
				new AlertDialog(mContext).builder().setMsg(mContext.getString(R.string.deletefile_tips))
				.setPositiveButton(mContext.getString(R.string.ok), new OnClickListener() {
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
			case R.id.btn_filelist_move:
				break;
			case R.id.btn_filelist_share:
				mfileID = noteFile.ID;
				Intent intent = new Intent(mContext, ContactSelectActivity.class);
				intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
//				intent.putExtra("select_list", mSelectList);
				((Activity)mContext).startActivityForResult(intent, ContactSelectActivity.MULTI_SELECT);
				break;
			case R.id.btn_filelist_download:
				if (noteFile.Name != null) { // 通过服务下载文件
					if (!FileUtil.hasFile(EMWApplication.filePath + noteFile.Url)) {
						String fileUrl = String.format(Const.DOWN_FILE_URL,
								PrefsUtil.readUserInfo().CompanyCode,
								noteFile.Url);
						NotificationUtil.notificationForDLAPK(mContext, fileUrl, EMWApplication.filePath);
					} else {
						ToastUtil.showToast(mContext, R.string.download_fileexist_tips);
					}
				}
				break;
			case R.id.btn_filelist_open:
				FileUtil.openFile(mContext, EMWApplication.filePath + noteFile.Url);
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
    
}
