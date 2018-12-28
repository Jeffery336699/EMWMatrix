package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.project.entities.UserSprint;

public class SprintAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private ArrayList<UserSprint> mSprints;

	public SprintAdapter(Context context) {
		this.mContext = context;
		mSprints = new ArrayList<UserSprint>();
	}

	public void setArraySprints(List<UserSprint> sprints) {
		if (sprints != null) {
			this.mSprints.addAll(sprints);
		}
	}

	@Override
	public int getGroupCount() {
		return mSprints != null ? mSprints.size() : 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mSprints.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder gvh;
		if (convertView == null) {
			gvh = new GroupViewHolder();
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.listitem_project_sprint,
					parent, false);
			gvh.name = (TextView) convertView
					.findViewById(R.id.tv_summary_name);
			gvh.taskNum = (TextView) convertView
					.findViewById(R.id.tv_summary_tasknum);
			gvh.arrowIcon = (ImageView) convertView
					.findViewById(R.id.iv_summary_arrow);

			convertView.setTag(gvh);
		} else {
			gvh = (GroupViewHolder) convertView.getTag();
		}
		UserSprint sprint = mSprints.get(groupPosition);
		gvh.name.setText(sprint.getName());
		gvh.taskNum.setText(String.format(
				mContext.getResources().getString(R.string.quantity), 5));
		if(1>0){
			gvh.arrowIcon.setImageResource(
					isExpanded ? R.drawable.btn_calendar_month_rightarrow
							: R.drawable.btn_calendar_month_rightarrow);
		}else{
			gvh.arrowIcon.setImageResource(R.drawable.trans_bg);
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder cvh;
		if (convertView == null) {
			cvh = new ChildViewHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(
					R.layout.listitem_project_summarytask, parent, false);
			cvh.name = (TextView) convertView
					.findViewById(R.id.tv_summarytask_name);
			cvh.description = (TextView) convertView
					.findViewById(R.id.tv_summarytask_description);
			cvh.progress = (CircleProgressView) convertView
					.findViewById(R.id.circleView_summarytask_progress);
			convertView.setTag(cvh);
		} else {
			cvh = (ChildViewHolder) convertView.getTag();
		}
		UserFenPai task = new UserFenPai();
		cvh.name.setText(task.Title);
		cvh.description.setText("");
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	class GroupViewHolder {
		TextView name;
		TextView taskNum;
		ImageView arrowIcon;
	}
	
	class ChildViewHolder {
		TextView name;
		TextView description;
		CircleProgressView progress;
	}
}
