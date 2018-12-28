package cc.emw.mobile.file.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.utils.OnItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.file.FileSelectActivity2;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;

/**
 * 选择知识库文件(无文件夹)Adapter
 * @author shaobo.zhuang
 */
public class FileSelectAdapter2 extends BaseAdapter {

	private Context mContext;
	private ArrayList<Files> mDataList;
	private ArrayList<Files> mSelectList;
	private Files mSelectFile;
	private int mSelectType;
	private int mFileType;
	private DisplayImageOptions options;
	private SparseBooleanArray mSelectMap;

	public FileSelectAdapter2(Context context,
							  ArrayList<Files> dataList) {
		this.mContext = context;
		this.mDataList = dataList;
		this.mSelectList = new ArrayList<>();
		mSelectMap = new SparseBooleanArray();

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ico_tupian) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ico_tupian) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ico_tupian) // 设置图片加载或解码过程中发生错误显示的图片
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY)
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.build(); // 创建配置过得DisplayImageOption对象
	}
	
	public Files getSelectFile() {
		return mSelectFile;
	}

	public void setSelectFile(Files file) {
		this.mSelectFile = file;
	}

	public void setSelectType(int selectType) {
		this.mSelectType = selectType;
	}

	public void setFileType(int fileType) {
		this.mFileType = fileType;
	}

	public ArrayList<Files> getSelectList() {
		return mSelectList;
	}
	public void setSelectList(ArrayList<Files> selectList) {
		if (selectList != null) {
			this.mSelectList = selectList;
			for (Files file : mSelectList) {
				mSelectMap.put(file.ID, true);
			}
		}
	}

	public SparseBooleanArray getSelectMap() {
		return mSelectMap;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_fileselect2, null);
			vh.iconIv = (ImageView) convertView.findViewById(R.id.iv_filelist_icon);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_filelist_name);
			vh.timeTv = (TextView) convertView.findViewById(R.id.tv_filelist_time);
			vh.sizeTv = (TextView) convertView.findViewById(R.id.tv_filelist_size);
			vh.projectTv = (TextView) convertView.findViewById(R.id.tv_filelist_project);
			vh.selectCb = (CheckBox) convertView.findViewById(R.id.cb_fileselect);
			
			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		Files file = mDataList.get(position);
		
		vh.nameTv.setText(file.Name);
		vh.timeTv.setText(file.UpdateTime);
		vh.timeTv.setVisibility(TextUtils.isEmpty(file.UpdateTime)? View.GONE:View.VISIBLE);
		vh.sizeTv.setText(FileUtil.getReadableFileSize(file.Length));
		vh.projectTv.setVisibility(View.GONE);

		if (mFileType == 2) { //图片
			String uri = HelpUtil.getFileURL(file);
			ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.iconIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
		} else {
			vh.iconIv.setImageResource(FileUtil.getResIconId(file.Name));
		}
		vh.sizeTv.setVisibility(View.VISIBLE);
		vh.projectTv.setVisibility(View.GONE);

		switch (mSelectType) {
			case FileSelectActivity2.RADIO_SELECT:
				vh.selectCb.setButtonDrawable(R.drawable.cm_radio_select);
				if (mSelectFile != null && mSelectFile.ID == file.ID) {
					vh.selectCb.setChecked(true);
				} else {
					vh.selectCb.setChecked(false);
				}
				break;
			case FileSelectActivity2.MULTI_SELECT:
				vh.selectCb.setButtonDrawable(R.drawable.cm_multi_select3);
				Boolean isSelect = mSelectMap.get(file.ID);
				if (isSelect != null && isSelect) {
					vh.selectCb.setChecked(true);
				} else {
					vh.selectCb.setChecked(false);
				}
				break;
		}

		convertView.setTag(R.id.tag_second, file);
		return convertView;
	}

	static class ViewHolder {
		ImageView iconIv;
		TextView nameTv;
		TextView timeTv;
		TextView sizeTv;
		TextView projectTv;
		CheckBox selectCb;
	}
	
}
