package cc.emw.mobile.task.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;

import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.view.IconTextView;

/**
 * 无效类   暂无引用   后续考虑删除
 */
public class TaskAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<UserProject> mDataList;
    private String oldFormat;
    private String newFormat;

    public TaskAdapter(Context context) {
        mContext = context;
        oldFormat = mContext.getResources().getString(
                R.string.timeformat6);
        newFormat = mContext.getResources().getString(
                R.string.timeformat18);
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
            groupHolder.tvSize = (TextView) convertView
                    .findViewById(R.id.tv_task_group_size);
            groupHolder.ivIcon = (IconTextView) convertView.findViewById(R.id.iv_task_group_icon);
            groupHolder.devideLine = convertView.findViewById(R.id.view_task_group_line);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupViewHolder) convertView.getTag();
        }
        // 设置数据
        UserProject userProject = mDataList.get(groupPosition);
        groupHolder.tvName.setText(userProject.Name);
        groupHolder.tvSize.setText(userProject.Tasks.size() + "");
        if (userProject.Name.equals(TaskConstant.TaskStateString.PROCESSING)) {
            groupHolder.tvSize.setTextColor(mContext.getResources().getColor(R.color.task_group_processing_text));
            groupHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.task_group_processing_text));
//            groupHolder.ivIcon.setTextColor(mContext.getResources().getString(R.color.item_pressed_color));
            groupHolder.devideLine.setBackgroundColor(mContext.getResources().getColor(R.color.task_group_processing_text));
        } else if (userProject.Name.equals(TaskConstant.TaskStateString.UNSTART)) {
            groupHolder.tvSize.setTextColor(mContext.getResources().getColor(R.color.task_group_unstart_text));
            groupHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.task_group_unstart_text));
//            groupHolder.ivIcon.setImageResource(R.drawable.shape_ico_blue);
            groupHolder.devideLine.setBackgroundColor(mContext.getResources().getColor(R.color.task_group_unstart_text));
        } else if (userProject.Name.equals(TaskConstant.TaskStateString.FINISHED)) {
            groupHolder.tvSize.setTextColor(mContext.getResources().getColor(R.color.task_group_finish_text));
            groupHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.task_group_finish_text));
//            groupHolder.ivIcon.setImageResource(R.drawable.shape_ico_red);
            groupHolder.devideLine.setBackgroundColor(mContext.getResources().getColor(R.color.task_group_finish_text));
        }
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
            childHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.tv_task_child_time);
            AnimationSet set = anmi();
            childHolder.set = set;
            convertView.setTag(childHolder);

        } else {
            childHolder = (ChildViewHolder) convertView.getTag();
        }
        // 设置孩子数据
        List<UserFenPai> tasks = mDataList.get(groupPosition).Tasks;
        if (tasks != null) {
            // 获取任务
            UserFenPai data = tasks.get(childPosition);
            childHolder.tvTitle.setText(data.Title);
            childHolder.tvTime.setText(TaskUtils.parseToNewStringTime(oldFormat, newFormat, data.FinishTime));
        }
        convertView.startAnimation(childHolder.set);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    class GroupViewHolder {
        TextView tvName;
        View devideLine;
        TextView tvSize;
        IconTextView ivIcon;

    }

    class ChildViewHolder {
        TextView tvTitle;
        TextView tvTime;
        AnimationSet set;
    }

    private AnimationSet anmi() {
        AnimationSet set = new AnimationSet(false);
        ScaleAnimation scale = new ScaleAnimation(0.8f, 1, 0.8f, 1,
                Animation.RELATIVE_TO_SELF, 0.8f, Animation.RELATIVE_TO_SELF,
                0.8f);
        scale.setDuration(800);// 动画时间
        scale.setFillAfter(true);// 保持动画状态
        AlphaAnimation alpha = new AlphaAnimation(0.6f, 1);
        alpha.setDuration(300);// 动画时间
        alpha.setFillAfter(true);// 保持动画状态
        set.addAnimation(scale);
        set.addAnimation(alpha);
        return set;
    }
}
