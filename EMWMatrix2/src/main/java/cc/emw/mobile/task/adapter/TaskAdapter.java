package cc.emw.mobile.task.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.grabner.circleprogress.CircleProgressView;
import cc.emw.mobile.R;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.util.TaskUtils;

public class TaskAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private List<UserProject> mDataList;

	public TaskAdapter(Context context) {
		mContext = context;
	}

	public void setData(List<UserProject> projectList) {
		this.mDataList = projectList;
	}

	@Override
	public int getGroupCount() {
		if (mDataList != null) {
			return mDataList.size();
		}
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (mDataList != null) {
			List<UserFenPai> tasks = mDataList.get(groupPosition).Tasks;
			return tasks != null ? tasks.size() : 0;
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mDataList != null ? mDataList.get(groupPosition) : null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (mDataList != null) {
			UserProject userProject = mDataList.get(groupPosition);
			List<UserFenPai> tasks = userProject.Tasks;
			return tasks != null ? tasks.get(childPosition) : null;
		}
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder groupHolder;
		if (convertView == null) {
			groupHolder = new GroupViewHolder();
			// 加载父容器的布局
			convertView = View.inflate(mContext, R.layout.listitem_task_group,
					null);
			// 找控件
			groupHolder.tvName = (TextView) convertView
					.findViewById(R.id.tv_task_group_name);
			convertView.setTag(groupHolder);
		} else {
			groupHolder = (GroupViewHolder) convertView.getTag();
		}
		// 设置数据
		UserProject userProject = mDataList.get(groupPosition);
		groupHolder.tvName.setText(userProject.Name);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		ChildViewHolder childHolder;
		if (convertView == null) {
			childHolder = new ChildViewHolder();
			// 加载父容器的布局
			convertView = View.inflate(mContext, R.layout.listitem_task_child,
					null);
			// 找控件
			childHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_task_child_title);
			childHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_task_child_content);
			childHolder.progress = (CircleProgressView) convertView
					.findViewById(R.id.circleProgressVie_task_child_progress);
			childHolder.tvDelay = (TextView) convertView
					.findViewById(R.id.tv_task_child_isDelay);
			childHolder.container = (LinearLayout) convertView
					.findViewById(R.id.ll_task_child_container);
			childHolder.ivIcon = (ImageView) convertView
					.findViewById(R.id.iv_task_child_icon);
			convertView.setTag(childHolder);

		} else {
			childHolder = (ChildViewHolder) convertView.getTag();
		}
		childHolder.container.removeAllViews();
		// 设置孩子数据
		List<UserFenPai> tasks = mDataList.get(groupPosition).Tasks;
		if (tasks != null) {
			// 获取任务
			UserFenPai data = tasks.get(childPosition);
			// childHolder.tvTitle.setText(data.Title);
			// Title设置成主负责人
			List<UserInfo> users = TaskUtils.getUsers(data.MainUser);
			if (users!=null&&users.size() != 0&&users.get(0) instanceof UserInfo) {
				childHolder.tvTitle.setText(users.get(0).Name);
			}else{
				childHolder.tvTitle.setText("主负责人为空");
			}
			// childHolder.tvTitle.setText(EMWApplication.personMap.get(data.Creator).getName());
			childHolder.tvContent.setText(data.Title);
			String format = mContext.getResources().getString(
					R.string.timeformat6);
			float progress = CommonUtil.getProgress(data.StartTime,
					data.FinishTime, format);
			childHolder.progress.setValue(progress);
			// 设置是否超时 当前时间>完成事件就超时
			long finishTime = TaskUtils
					.parseStringTime(format, data.FinishTime);
			long currentTime = System.currentTimeMillis();
			childHolder.tvDelay
					.setVisibility(currentTime - finishTime > 0 ? View.VISIBLE
							: View.GONE);
			// 任务紧急程度展示
			if(data.Yxj>0){
				childHolder.ivIcon
						.setImageResource(ColorAdapter.colorIcon[data.Yxj - 1]);
			}

			// 添加子任务容器
			/*
			 * List<UserFenPai> secondTasks = data.Tasks; if (secondTasks !=
			 * null) { // 添加子任务 addSecondTasks(secondTasks,
			 * childHolder.container); }
			 */
		}

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * 添加子任务
	 * 
	 *
	 */
	/*
	 * private void addSecondTasks(List<UserFenPai> secondTasks, LinearLayout
	 * container) { int length = secondTasks.size(); for (int i = 0; i < length;
	 * i++) { final UserFenPai userFenPai = secondTasks.get(i); View view =
	 * View.inflate(mContext, R.layout.listitem_task_child, null);
	 * RelativeLayout rl = (RelativeLayout) view
	 * .findViewById(R.id.rl_task_child_container);
	 * rl.setBackgroundColor(Color.parseColor("#F3F6FA")); TextView tvTitle =
	 * (TextView) view .findViewById(R.id.tv_task_child_title); TextView
	 * tvContent = (TextView) view .findViewById(R.id.tv_task_child_content);
	 * CircleProgressView progress = (CircleProgressView) view
	 * .findViewById(R.id.circleProgressVie_task_child_progress); TextView
	 * tvDelay = (TextView) view .findViewById(R.id.tv_task_child_isDelay);
	 * tvTitle.setText(userFenPai.getTitle());
	 * tvContent.setText(userFenPai.getMark()); progress.setValue(90); //
	 * 子条目的点击事件 view.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { Intent intent = new
	 * Intent(mContext, TaskDetailActivity.class); intent.putExtra("userFenpai",
	 * userFenPai); mContext.startActivity(intent); } }); // TODO：延时判断是否超时
	 * container.addView(view, params); } }
	 */

	class GroupViewHolder {
		TextView tvName;
	}

	class ChildViewHolder {
		TextView tvTitle;
		TextView tvContent;
		CircleProgressView progress;
		LinearLayout container;
		ImageView ivIcon;
		TextView tvDelay;
	}

}
