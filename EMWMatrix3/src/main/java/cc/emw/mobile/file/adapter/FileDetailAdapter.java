package cc.emw.mobile.file.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.FileDown;

/**
 * 文件下载记录Adapter
 * @author shaobo.zhuang
 */
public class FileDetailAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<FileDown> mDataList;

	public FileDetailAdapter(Context context,
							 ArrayList<FileDown> dataList) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_filedetail, null);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_filedetail_logname);
			vh.timeTv = (TextView) convertView.findViewById(R.id.tv_filedetail_logtime);

			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		FileDown downLog = mDataList.get(position);

		vh.nameTv.setText(downLog.UserName+"下载了此文件");
		vh.timeTv.setText(downLog.CreateTime);

		convertView.setTag(R.id.tag_second, downLog);
		return convertView;
	}

	static class ViewHolder {
		TextView nameTv;
		TextView timeTv;
	}
	
}
