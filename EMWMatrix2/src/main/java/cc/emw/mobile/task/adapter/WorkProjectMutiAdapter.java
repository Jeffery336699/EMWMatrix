package cc.emw.mobile.task.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cc.emw.mobile.util.TaskUtils;

public class WorkProjectMutiAdapter extends BaseAdapter {

	private Context mContext;
	private List<UserProject> mDataList;
	private String mProjectIDs;// 项目IDs字符串[1,2,3]
	private Map<Integer, Boolean> selMap;// 储存右边图标是否展示的值
	private ArrayList<UserProject> mTempList;// 临时储存项目冲刺的集合

	public WorkProjectMutiAdapter(Context context, String projectIDs) {
		mProjectIDs = projectIDs;
		this.mContext = context;
		mDataList = new ArrayList<UserProject>();
		mTempList=new ArrayList<UserProject>();
	}

	public void setData(List<UserProject> dataList) {
		if (dataList != null) {
			mDataList.clear();
			mDataList.addAll(dataList);
			selMap = new HashMap<Integer, Boolean>();
			// 遍历项目 核对ID值
			String[] stringID = TaskUtils.getStringID(mProjectIDs);
			for (int i = 0; i < dataList.size(); i++) {
				selMap.put(i, false);
				for (int j = 0; j < stringID.length; j++) {
					if (dataList.get(i).ID == Integer.valueOf(stringID[j])) {
						selMap.put(i, true);
					}
				}
			}
		}
	}
	
	public ArrayList<UserProject> getUserProjectList() {
		mTempList.clear();
		for (int i = 0; i < mDataList.size(); i++) {
			if (selMap.get(i)) {
				mTempList.add(mDataList.get(i));
			}
		}
		return mTempList;
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
		vh.createTime.setText(data.CreateTime);
		vh.ivSelect.setBackgroundResource(selMap.get(position) ? R.drawable.ico_duoxuan
						: R.drawable.btn_nor_duoxuan);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selMap.put(position, selMap.get(position) ? false : true);
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
