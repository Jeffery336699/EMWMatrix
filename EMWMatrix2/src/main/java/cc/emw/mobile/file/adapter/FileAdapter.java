package cc.emw.mobile.file.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.util.FileUtil;

/**
 * 知识库列表Adapter
 * @author shaobo.zhuang
 */
public class FileAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<Files> mDataList;
	
	public FileAdapter(Context context,
			ArrayList<Files> dataList) {
		this.mContext = context;
		this.mDataList = dataList;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_file, null);
			vh.iconIv = (ImageView) convertView.findViewById(R.id.iv_file_icon);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_file_name);
			vh.timeTv = (TextView) convertView.findViewById(R.id.tv_file_time);
			
			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		Files file = mDataList.get(position);
		vh.iconIv.setImageResource(FileUtil.getResIconId(file.Name));
		vh.nameTv.setText(file.Name);
		vh.timeTv.setText(file.UpdateTime);
		convertView.setTag(R.id.tag_second, file);
		return convertView;
	}

	static class ViewHolder {
		ImageView iconIv;
		TextView nameTv;
		TextView timeTv;
	}
	
}
