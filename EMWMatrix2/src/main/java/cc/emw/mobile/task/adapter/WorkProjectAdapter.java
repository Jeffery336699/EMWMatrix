package cc.emw.mobile.task.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserProject;

public class WorkProjectAdapter extends BaseAdapter {

	private Context mContext;
	private List<UserProject> mDataList;
	private int clickPosition = -1;// 点击的条目位置，默认为哪都没点
	private int mProjectID;

	public WorkProjectAdapter(Context context, int projectID) {
		mProjectID = projectID;
		this.mContext = context;
		mDataList = new ArrayList<UserProject>();
	}

	public void setData(List<UserProject> dataList) {
		if (dataList != null) {
			mDataList.clear();
			mDataList.addAll(dataList);
			for (int i = 0; i < mDataList.size(); i++) {
				if (mDataList.get(i).ID == mProjectID) {
					clickPosition = i;
				}
			}
		}
	}

	/**
	 * 获取选中的项目位置，-1表示都没选择。
	 * 
	 * @return
	 */
	public UserProject getSelectProjecd() {
		if (clickPosition != -1) {
			return mDataList.get(clickPosition);
		}
		return new UserProject();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.listitem_work_project, null);
			vh.tvName = (TextView) convertView
					.findViewById(R.id.tv_work_project_name);
			vh.tvDes = (TextView) convertView
					.findViewById(R.id.tv_work_project_description);
			vh.ivSelect = (ImageView) convertView
					.findViewById(R.id.iv_work_project_select);
			vh.createTime = (TextView) convertView
					.findViewById(R.id.tv_work_project_create_time);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		// 绑定数据
		UserProject data = mDataList.get(position);
		vh.tvName.setText(data.Name);
		vh.tvDes.setText(EMWApplication.personMap.get(data.Creator).Name);
		// vh.tvDes.setText(data.getMark());
		vh.createTime.setText(data.CreateTime);
		vh.ivSelect.setVisibility(position == clickPosition ? View.VISIBLE
				: View.INVISIBLE);
		// vh.ivSelect
		// .setBackgroundResource(position == clickPosition ?
		// R.drawable.ico_duoxuan
		// : R.drawable.cm_multi_select_nor);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (clickPosition == position) {
					clickPosition = -1;
				} else {
					clickPosition = position;
				}
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	static class ViewHolder {

		TextView tvName;
		TextView tvDes;
		TextView createTime;
		ImageView ivSelect;
	}

}
