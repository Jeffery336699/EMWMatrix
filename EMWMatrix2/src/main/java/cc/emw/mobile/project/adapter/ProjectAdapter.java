package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.entities.UserSprint;

public class ProjectAdapter extends BaseExpandableListAdapter {
	private static final String TAG = "ProjectAdapter";
	public static final String DATEFORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
	private Context mContext;
	private int projectNum = 0;
	private int sprintNum = 0;
	private ArrayList<UserProject> mProject;
	private ArrayList<UserSprint> mSprints;
	private Map<Integer, Integer> colorMap;

	public ProjectAdapter(Context context) {
		this.mContext = context;
		mProject = new ArrayList<UserProject>();
		mSprints = new ArrayList<UserSprint>();
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

	public void setArrayProjects(List<UserProject> projects) {
		if (projects != null) {
			this.mProject.clear();
			this.mProject.addAll(projects);
			projectNum = mProject.size();
		}
	}

	public void setArraySrpints(List<UserSprint> sprints) {
		if (sprints != null) {
			this.mSprints.addAll(sprints);
			sprintNum = mSprints.size();
		}
	}

	public ArrayList<UserProject> getArrayProjects(){
		return this.mProject;
	}
	
	public ArrayList<UserSprint> getArraySprints(){
		return this.mSprints;
	}
	
	@Override
	public int getGroupCount() {
		return projectNum + sprintNum;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition < sprintNum) {
			List<UserFenPai> sprintTasks = mSprints.get(groupPosition)
					.getTasks();
			return sprintTasks != null?sprintTasks.size():0;
		} else {
			List<UserFenPai> mTask = mProject.get(groupPosition - sprintNum)
					.Tasks;
			return mTask != null?mTask.size():0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (groupPosition < sprintNum) {
			return mSprints.get(groupPosition);
		} else {
			return mProject.get(groupPosition - sprintNum);
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (groupPosition < sprintNum) {
			return mSprints.get(groupPosition).getTasks().get(childPosition);
		} else {
			return mProject.get(groupPosition - sprintNum).Tasks
					.get(childPosition);
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded,
			View convertView, final ViewGroup parent) {
			ProjectGroupViewHolder pgvh;
			if (convertView == null) {
				pgvh = new ProjectGroupViewHolder();
				LayoutInflater inflater = LayoutInflater.from(parent
						.getContext());
				convertView = inflater.inflate(
						R.layout.listitem_project_summary, parent, false);
				pgvh.progress = (ProgressBar) convertView
						.findViewById(R.id.pb_summary_progress);
				pgvh.name = (TextView) convertView
						.findViewById(R.id.tv_summary_name);
				pgvh.responser = (TextView) convertView
						.findViewById(R.id.tv_summary_responser);
				pgvh.date = (TextView) convertView
						.findViewById(R.id.tv_summary_date);
				pgvh.taskNum = (TextView) convertView
						.findViewById(R.id.tv_summary_tasknum);
				pgvh.arrowIcon = (ImageView) convertView
						.findViewById(R.id.iv_summary_arrow);
				pgvh.divider = (ImageView)convertView
						.findViewById(R.id.iv_summary_divider);
				pgvh.contentLL = (LinearLayout)convertView
						.findViewById(R.id.ll_summary_arrow);

				convertView.setTag(pgvh);
			} else {
				pgvh = (ProjectGroupViewHolder) convertView.getTag();
			}
			if(groupPosition < sprintNum){
				UserSprint spr = mSprints.get(groupPosition);
				pgvh.progress.setVisibility(View.GONE);
				pgvh.name.setText("项目冲刺"+(groupPosition+1)+": "+spr.getName());
				pgvh.responser.setVisibility(View.GONE);
				pgvh.date.setVisibility(View.GONE);
				pgvh.taskNum.setText(String.format(
						"数量：%d", spr.getTasks() != null?spr.getTasks().size():0));
				if (spr.getTasks().size() > 0) {
					pgvh.arrowIcon
							.setImageResource(isExpanded ? R.drawable.list_up_jiantou
									: R.drawable.list_down_jiantou);
					pgvh.contentLL.setVisibility(View.VISIBLE);
				} else {
					pgvh.contentLL.setVisibility(View.INVISIBLE);
				}
			}else{
				UserProject project = mProject.get(groupPosition - sprintNum);
				pgvh.responser.setVisibility(View.VISIBLE);
				pgvh.date.setVisibility(View.VISIBLE);
				pgvh.progress.setVisibility(View.VISIBLE);
				pgvh.progress.setProgress(
								CommonUtil.getProgress(
								project.BeginTime,
								project.EndTime, 
								parent.getContext().getResources().getString(R.string.timeformat4)));
				pgvh.name.setText(project.Name);
				pgvh.responser.setText(
						EMWApplication.personMap.get(project.Creator).Name);
				SimpleDateFormat sdf = new SimpleDateFormat(
						parent.getContext().getResources().getString(R.string.timeformat17), Locale.CHINA);
				SimpleDateFormat sdf2 = new SimpleDateFormat(
						parent.getContext().getResources().getString(R.string.timeformat4),Locale.CHINA);
				Date date;
				try {
					date = sdf2.parse(project.CreateTime);
					pgvh.date.setText(sdf.format(date));
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
				pgvh.taskNum.setText(String.format("数量：%d", project.Tasks
						.size()));
				if (project.Tasks.size() > 0) {
					pgvh.arrowIcon
							.setImageResource(isExpanded ? R.drawable.list_up_jiantou
									: R.drawable.list_down_jiantou);
					pgvh.contentLL.setVisibility(View.VISIBLE);
				} else {
					pgvh.contentLL.setVisibility(View.INVISIBLE);
				}
			}
			pgvh.contentLL.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) { 
					if (!isExpanded) {
						((ExpandableListView) parent).expandGroup(groupPosition);
					}else{
						((ExpandableListView) parent).collapseGroup(groupPosition);
					}
				}
			});
			pgvh.divider.setVisibility(isExpanded?View.GONE:View.VISIBLE);
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
			cvh.topIcon = (ImageView)convertView
					.findViewById(R.id.iv_summarytask_arrow);
			cvh.name = (TextView) convertView
					.findViewById(R.id.tv_summarytask_name);
			cvh.description = (TextView) convertView
					.findViewById(R.id.tv_summarytask_description2);
			cvh.progress = (CircleProgressView) convertView
					.findViewById(R.id.circleView_summarytask_progress);
			cvh.divider = (ImageView)convertView
					.findViewById(R.id.iv_summarytask_divider);
			cvh.isProject = (TextView) convertView
					.findViewById(R.id.tv_summarytask_project);
			convertView.setTag(cvh);
		} else {
			cvh = (ChildViewHolder) convertView.getTag();
		}
		UserFenPai task;
		if(groupPosition < sprintNum){
			task = mSprints.get(groupPosition).getTasks().get(childPosition);
			if(task.ProjectId != 0){
				String projectNameString = "";
				for(UserProject p:mProject){
					if(p.ID == task.ProjectId){
						projectNameString = p.Name;
						cvh.isProject.setVisibility(View.VISIBLE);
						cvh.isProject.setText(projectNameString);
					}
				}
				
			}
		}else{
			task = mProject.get(groupPosition - sprintNum).Tasks
					.get(childPosition);
			cvh.isProject.setVisibility(View.GONE);
		}
			cvh.topIcon.setImageResource(colorMap.get(task.Yxj));
			cvh.name.setText(task.Title);
			cvh.description.setText(task.Mark);
			cvh.progress.setValue(CommonUtil.getProgress(
					task.StartTime, 
					task.FinishTime,
					parent.getResources().getString(R.string.timeformat6)));
		cvh.divider.setVisibility(isLastChild?View.VISIBLE:View.GONE);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	class ProjectGroupViewHolder {
		ProgressBar progress;
		TextView name;
		TextView responser;
		TextView date;
		TextView taskNum;
		LinearLayout contentLL;
		ImageView arrowIcon;
		ImageView divider;
	}

	class ChildViewHolder {
		ImageView topIcon;
		TextView name;
		TextView description;
		CircleProgressView progress;
		ImageView divider;
		TextView isProject;
	}
}
