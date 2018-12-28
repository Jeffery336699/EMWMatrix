package cc.emw.mobile.task.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserSprint;

public class SprintsAdapter extends BaseAdapter {

	private Context mContext;
	private List<UserSprint> mDataList;
	private int mTaskID;
	// private Map<Integer, Boolean> selMap;// 储存右边图标是否展示的值
	private int mClicPosition = -1;// 记录当前点击的位置,默认为-1什么也没选中

	public SprintsAdapter(Context context, int taskID) {
		mTaskID = taskID;
		this.mContext = context;
		mDataList = new ArrayList<UserSprint>();
	}

	@SuppressLint("UseSparseArrays")
	public void setData(List<UserSprint> dataList) {
		if (dataList != null) {
			mDataList.clear();
			mDataList.addAll(dataList);
			// selMap = new HashMap<Integer, Boolean>();
			it: for (int i = 0; i < dataList.size(); i++) {
				// selMap.put(i, false);
				String content = dataList.get(i).Content;
				if (content != null && !"".equals(content)
						&& !"[]".equals(content)) {
					String[] stringID = content.split(",");
					for (int j = 0; j < stringID.length; j++) {
						if (TextUtils.isDigitsOnly(stringID[j])) {
							if (mTaskID == Integer.valueOf(stringID[j])) {
								// selMap.put(i, true);
								mClicPosition = i;
								break it;
							}
						}

					}
				}

			}
		}
	}

	/**
	 * 获取选中的任务冲刺
	 * 
	 * @return
	 */
	public UserSprint getUserSprint() {
		if (mClicPosition != -1) {
			return mDataList.get(mClicPosition);
		}
		return new UserSprint();
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
		UserSprint data = mDataList.get(position);
		vh.tvName.setText(data.Name);
		vh.tvDes.setText(EMWApplication.personMap.get(data.Creator).Name);
		vh.createTime.setText(data.CreateTime);

		vh.ivSelect.setVisibility(mClicPosition == position ? View.VISIBLE
				: View.INVISIBLE);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mClicPosition == position) {
					mClicPosition = -1;
				} else {
					mClicPosition = position;
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
