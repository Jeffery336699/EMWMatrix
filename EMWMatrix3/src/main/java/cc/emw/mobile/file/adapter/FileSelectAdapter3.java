package cc.emw.mobile.file.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.util.FileUtil;

/**
 * 选择知识库文件Adapter
 * @author shaobo.zhuang
 */
public class FileSelectAdapter3 extends BaseAdapter {

	private Context mContext;
	private ArrayList<Files> mDataList;
	private ArrayList<Files> mSelectList;
	private SparseBooleanArray mSelectMap;

	public FileSelectAdapter3(Context context,
                              ArrayList<Files> dataList) {
		this.mContext = context;
		this.mDataList = dataList;
		mSelectList = new ArrayList<Files>();
		mSelectMap = new SparseBooleanArray();
	}
	
	public SparseBooleanArray getSelectMap() {
		return mSelectMap;
	}
	
	public List<Files> getSelectList() {
		return mSelectList;
	}

	public void setSelectList(List<Files> selectList) {
		if (mSelectList != null && selectList != null) {
			mSelectList.addAll(selectList);
			
			for (Files file : mSelectList) {
				mSelectMap.put(file.ID, true);
			}
		}
			
	}
	public void setSelectList() {
		if (mSelectList != null) {
			mSelectList.clear();
			mSelectMap.clear();
		}
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_fileselect, null);
			vh.iconIv = (ImageView) convertView.findViewById(R.id.iv_filelist_icon);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_filelist_name);
			vh.timeTv = (TextView) convertView.findViewById(R.id.tv_filelist_time);
			vh.sizeTv = (TextView) convertView.findViewById(R.id.tv_filelist_size);
			vh.projectTv = (TextView) convertView.findViewById(R.id.tv_filelist_project);
			vh.selectCb = (CheckBox) convertView.findViewById(R.id.cb_fileselect);
			vh.selectCb2= (ImageView) convertView.findViewById(R.id.cb_fileselect2);
			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		final Files file = mDataList.get(position);

		vh.nameTv.setText(file.Name);
		vh.timeTv.setText(file.UpdateTime);
		vh.timeTv.setVisibility(TextUtils.isEmpty(file.UpdateTime)? View.GONE:View.VISIBLE);
		vh.sizeTv.setText(FileUtil.getReadableFileSize(file.Length));
		vh.projectTv.setVisibility(View.GONE);

		Boolean isSelect = mSelectMap.get(file.ID);
		if (isSelect != null && isSelect) {
			vh.selectCb.setChecked(true);
		} else {
			vh.selectCb.setChecked(false);
		}

		if (file.Type == 1) {
			vh.iconIv.setImageResource(R.drawable.list_ico_folder);
			vh.sizeTv.setVisibility(View.GONE);
			vh.projectTv.setVisibility(View.GONE);
			vh.selectCb.setVisibility(View.GONE);
			vh.selectCb2.setVisibility(View.VISIBLE);
		} else {
			vh.iconIv.setImageResource(FileUtil.getResIconId(file.Name));
			vh.sizeTv.setVisibility(View.VISIBLE);
			vh.projectTv.setVisibility(View.GONE);
			vh.selectCb2.setVisibility(View.GONE);
			vh.selectCb.setVisibility(View.VISIBLE);
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
		ImageView selectCb2;
	}
}
