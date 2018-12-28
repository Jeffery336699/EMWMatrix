package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.text.TextUtils;
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

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.project.view.RelativeTaskActivity;

public class RelativeTaskAdapter extends BaseAdapter {
	private static final String TAG = "RelativeTaskAdapter";
	private Context mContext;
	private ArrayList<UserFenPai> mTasks;
	private ArrayList<UserFenPai> tempTasks;
	private Map<Integer, Integer> colorMap;
	private Map<Integer, Boolean> selMap;
	private ArrayList<UserFenPai> mTasksRet;

	public RelativeTaskAdapter(Context context) {
		this.mContext = context;
		mTasks = new ArrayList<UserFenPai>();
		tempTasks = new ArrayList<UserFenPai>();
		mTasksRet = new ArrayList<UserFenPai>();
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
			this.tempTasks.clear();
			this.tempTasks.addAll(tasks);
			selMap = new HashMap<Integer, Boolean>();
		}
	}

	/**
	 * 设置项目,若项目中有任务，则显示时让任务打勾
	 */
	public void setTasks(String[] taskIds, int from) {
		StringBuilder sBuilder = new StringBuilder();
		for (int i = 0; i < taskIds.length; i++) {
			sBuilder.append(taskIds[i]).append(",");
		}
//        mTasks.clear();
		if (from == RelativeTaskActivity.FROM_PROJECT) {
			for (int i = 0; i < tempTasks.size(); i++) {
				boolean flag = false;// 是否显示在列表中，projectId为0或存在taskIds中的任务则显示
				boolean isSel = false;// 是否显示已选
				for (int j = 0; j < taskIds.length; j++) {
					if (!TextUtils.isEmpty(taskIds[j])
							&& TextUtils.isDigitsOnly(taskIds[j])) {
						if (tempTasks.get(i).ID == Integer.valueOf(taskIds[j])) {
							flag = true;
							isSel = true;
							break;
						}
					}
				}
				if (tempTasks.get(i).ProjectId == 0) {
					flag = true;
				}
				if (flag) {
					mTasks.add(tempTasks.get(i));
					if (isSel) {
						selMap.put(mTasks.size() - 1, true);
					} else {
						selMap.put(mTasks.size() - 1, false);
					}
				}
			}
		} else if (from == RelativeTaskActivity.FROM_SCHEDULE) {
			for (int i = 0; i < tempTasks.size(); i++) {
				selMap.put(i, false);
				for (int j = 0; j < taskIds.length; j++) {
					if (!TextUtils.isEmpty(taskIds[j])
							&& TextUtils.isDigitsOnly(taskIds[j])) {
						if (tempTasks.get(i).ID == Integer.valueOf(taskIds[j])) {
							selMap.put(i, true);
							break;
						}
					}
				}
			}
			mTasks.addAll(tempTasks);
		}
	}

	public ArrayList<UserFenPai> getSelTasks() {
		for (int i = 0; i < mTasks.size(); i++) {
			if (selMap.get(i)) {
				mTasksRet.add(mTasks.get(i));
			}
		}
		return this.mTasksRet;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(
					R.layout.listitem_project_relativetask, parent, false);
			vh.name = (TextView) convertView
					.findViewById(R.id.tv_summarytask_name);
			vh.description = (TextView) convertView
					.findViewById(R.id.tv_summarytask_description);
			vh.selImg = (ImageView) convertView
					.findViewById(R.id.iv_relativetask_select);
			vh.arrowImg = (ImageView) convertView
					.findViewById(R.id.iv_summarytask_arrow);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		UserFenPai task = mTasks.get(position);
        vh.arrowImg.setImageResource(colorMap.get(task.Yxj));
		vh.name.setText(task.Title);
		vh.description.setText(task.Mark);
		vh.selImg
				.setImageResource(selMap.get(position) ? R.drawable.ico_duoxuan
						: R.drawable.cm_multi_select_nor);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selMap.put(position, selMap.get(position) ? false : true);
				notifyDataSetChanged();
			}
		});
		return convertView;
	}
	class ViewHolder {
		TextView name;
		TextView description;
		ImageView arrowImg;
		ImageView selImg;
	}

}
