package cc.emw.mobile.task.adapter;

import java.util.ArrayList;
import java.util.List;

import com.zf.iosdialog.widget.AlertDialog;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.task.fragment.taskdetail.AttachmentFragment;
import cc.emw.mobile.task.fragment.taskdetail.SummarizeFragment;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.ToastUtil;

/**
 * 任务附件Adapter
 * 
 * @author chengyong.liu
 * 
 */
public class AttachmentAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;
	private ArrayList<Files> mDataList;
	private int lastPosition = -1;
	// private Dialog mLoadingDialog; // 加载框
	private boolean isDelete = false;// 默认展示不删除
	private AttachmentFragment mFragment;
	private int mfileID = 0;// 记录点击的文件ID
	private ArrayList<Files> mTempList = new ArrayList<Files>();

	public AttachmentAdapter(Context context, AttachmentFragment fragment) {
		mFragment = fragment;
		mContext = context;
		mDataList = new ArrayList<Files>();
	}

	public boolean getFlag() {
		return isDelete;
	}

	public void setFlag(boolean flag) {
		this.isDelete = flag;
	}

	/**
	 * 获取需要分享的文件ID
	 * 
	 * @return 返回文件ID
	 */
	public int getFileID() {
		return mfileID;
	}

	public void setDataList(List<Files> dataList) {
		if (dataList != null) {
			mDataList.clear();
			mDataList.addAll(dataList);
		}
	}

	public List<Files> getDataList() {
		return mDataList;
	}

	@Override
	public int getCount() {
		return mDataList != null ? mDataList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mDataList != null ? mDataList.get(position) : null;

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.listitem_task_filelist, null);
			vh.iconIv = (ImageView) convertView
					.findViewById(R.id.iv_task_filelist_icon);
			vh.nameTv = (TextView) convertView
					.findViewById(R.id.tv_task_filelist_name);
			vh.sizeTv = (TextView) convertView
					.findViewById(R.id.tv_task_filelist_time);// 附件大小
			vh.moreBtn = (ImageButton) convertView
					.findViewById(R.id.btn_task_filelist_more);
			vh.moreLayout = (LinearLayout) convertView
					.findViewById(R.id.ll_task_filelist_more);
			vh.deleteBtn = (Button) convertView
					.findViewById(R.id.btn_task_filelist_delete);
			// vh.moveBtn = (Button)
			// convertView.findViewById(R.id.btn_task_filelist_move);
			vh.shareBtn = (Button) convertView
					.findViewById(R.id.btn_task_filelist_share);
			vh.downloadBtn = (Button) convertView
					.findViewById(R.id.btn_task_filelist_download);
			vh.openBtn = (Button) convertView
					.findViewById(R.id.btn_task_filelist_open);

			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		Files file = mDataList.get(position);

		vh.nameTv.setText(file.Name);
		vh.sizeTv.setText(FileUtil.getReadableFileSize(file.Length));// 将Long转换成文件大小
		vh.iconIv.setImageResource(FileUtil.getResIconId(file.Name));// 读取文件后缀名
		vh.moreLayout.setVisibility(lastPosition == position ? View.VISIBLE
				: View.GONE);
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
		vh.deleteBtn.setTag(file);
		vh.shareBtn.setTag(file);
		vh.downloadBtn.setTag(file);
		vh.openBtn.setTag(file);
		vh.deleteBtn.setOnClickListener(this);
		vh.shareBtn.setOnClickListener(this);
		vh.downloadBtn.setOnClickListener(this);
		vh.openBtn.setOnClickListener(this);
		convertView.setTag(R.id.tag_second, file);
		return convertView;
	}

	static class ViewHolder {
		ImageView iconIv;// 附件图标
		TextView nameTv;// 附件名称
		TextView sizeTv;// 文件大小
		ImageButton moreBtn;// 的
		LinearLayout moreLayout;// 按钮容器
		Button deleteBtn, moveBtn, shareBtn, downloadBtn, openBtn;
	}

	@Override
	public void onClick(View v) {
		final Files file = (Files) v.getTag();
		switch (v.getId()) {
		case R.id.btn_task_filelist_delete:
			if (SummarizeFragment.ChargeLimitFlag||SummarizeFragment.ExecutorLimitFlag) {
				new AlertDialog(mContext).builder().setMsg(mContext.getString(R.string.deletefile_tips))
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						mTempList.clear();
						for (Files attachment : mDataList) {
							if (attachment.ID != file.ID) {
								mTempList.add(attachment);
							}
						}
						deleteFile(file, mTempList);
					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				}).show();
			}else{
				ToastUtil.showToast(mContext,mContext.getString(R.string.task_attachment_delete_tips));
			}
			

			break;
		case R.id.btn_task_filelist_share:
			// 将文件ID传送给 Fragment
			mfileID = file.ID;
			// 分享附件 跳转到人员选择列表
			Intent intent = new Intent(mContext, ContactSelectActivity.class);
			intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE,
					ContactSelectActivity.MULTI_SELECT);
			mFragment.startActivityForResult(intent,
					AttachmentFragment.SHARE_ATTACHMENT);
			break;
		case R.id.btn_task_filelist_download:
			new AlertDialog(mContext).builder().setMsg(mContext.getString(R.string.download_tips))
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(View v) {
							downLoad(file);
						}
					}).setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(View v) {

						}
					}).show();

			break;
		case R.id.btn_task_filelist_open:
			// Toast.makeText(mContext, "查看", Toast.LENGTH_SHORT).show();
			String name = file.ID + FileUtil.getExtension(file.Name);
			FileUtil.openFile(mContext, EMWApplication.filePath + name);
			break;
		}
	}

	private void downLoad(final Files file) {
		// 根据文件ID 下载文件
		String fileName = file.ID + FileUtil.getExtension(file.Name);
		if (!FileUtil.hasFile(EMWApplication.filePath + fileName)) {
			downLoadFile(file.ID, fileName);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.download_fileexist_tips), Toast.LENGTH_SHORT).show();
		}
	}

	private void deleteFile(Files file, ArrayList<Files> list) {
		mFragment.deleteFile(file, list);
	}

	/**
	 * 根据文件ID下载附件
	 * 
	 * @param fileID
	 *            附件ID
	 */
	private void downLoadFile(int fileID, String name) {
		// String url = HttpConstant.DOWNLOAD_FILE + fileID;
		String url = Const.DOWNLOAD_FILE + fileID;
		NotificationUtil.notificationForDLAPK(mContext, url,
				EMWApplication.filePath, name);
	}

	/**
	 * 添加任务的时候设置setLastPostion为-1
	 */
	public void setLastPosition() {
		lastPosition = -1;
	}

	public int getLastPosition() {
		return lastPosition;
	}

}
