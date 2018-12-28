package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.project.Util.CommonUtil;

public class TaskAdapter extends BaseAdapter {
	private ArrayList<UserFenPai> mTasks;
	private Context mContext;
	private Map<Integer, Integer> colorMap;
	
	public TaskAdapter(Context context) {
		this.mContext = context;
		mTasks = new ArrayList<UserFenPai>();
		colorMap = new HashMap<Integer, Integer>();
		colorMap.put(0, R.drawable.shape_ico_blue);
		colorMap.put(1, R.drawable.shape_ico_blue);
		colorMap.put(2, R.drawable.shape_ico_orange);
		colorMap.put(3, R.drawable.shape_ico_red);
		colorMap.put(4, R.drawable.shape_ico_red);
		colorMap.put(5, R.drawable.shape_ico_red);
		colorMap.put(6, R.drawable.shape_ico_red);
		colorMap.put(7, R.drawable.shape_ico_red);
	}
	
	public void setArrayTasks(List<UserFenPai> tasks) {
		if (tasks != null) {
			this.mTasks.clear();
			this.mTasks.addAll(tasks);
		}
	}
	
	@Override
	public int getCount() {
		return mTasks != null ? mTasks.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mTasks != null ? mTasks.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(
					R.layout.listitem_project_summarytask, parent, false);
			vh.arrowIcon = (ImageView)convertView
					.findViewById(R.id.iv_summarytask_arrow);
			vh.name = (TextView) convertView
					.findViewById(R.id.tv_summarytask_name);
			vh.description = (TextView) convertView
					.findViewById(R.id.tv_summarytask_description2);
			vh.progress = (CircleProgressView) convertView
					.findViewById(R.id.circleView_summarytask_progress);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		UserFenPai task = mTasks.get(position);
		vh.arrowIcon.setImageResource(colorMap.get(task.Yxj));
		vh.name.setText(task.Title);
		vh.description.setText(task.Mark);
		vh.progress.setValue(
				CommonUtil.getProgress(
						task.StartTime,
						task.FinishTime,
						parent.getContext().getResources().getString(R.string.timeformat6)));
		return convertView;
	}
	class ViewHolder {
		ImageView arrowIcon;
		TextView name;
		TextView description;
		CircleProgressView progress;
	}
}
