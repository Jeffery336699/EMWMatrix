package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.bean.StateTask;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

/**
 * 任务根据状态显示适配器
 * Created by jven.wu on 2016/6/24.
 */
public class StateProjectAdapter2 extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<StateTask> stateTasks = new ArrayList<>(); //务务数据
    private DisplayImageOptions options; //图片显示选项
    private String oldFormat; //原始时间格式
    private String newFormat; //目标时间格式



    public StateProjectAdapter2(Context context) {
        mContext = context;
        //初始化图片显示选项
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
        oldFormat = mContext.getResources().getString(
                R.string.timeformat6);
        newFormat = mContext.getResources().getString(
                R.string.timeformat7);
    }

    /**
     * 设置数据
     * @param stateTasks
     */
    public void setData(ArrayList<StateTask> stateTasks) {
        this.stateTasks.clear();
        this.stateTasks.addAll(stateTasks);
    }

    @Override
    public int getGroupCount() {
        return stateTasks.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return stateTasks.get(groupPosition).TaskViewBean.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return stateTasks.get(groupPosition).TaskViewBean.get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ProjectGroupViewHolder pgvh;
        if (convertView == null) {
            pgvh = new ProjectGroupViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent
                    .getContext());
            convertView = inflater.inflate(
                    R.layout.listitem_project_state_title, parent, false);
            pgvh.projectState = (TextView) convertView.findViewById(R.id.project_state_tv);
            pgvh.bottomStripe = convertView.findViewById(R.id.bottom_stripe);
            convertView.setTag(pgvh);
        } else {
            pgvh = (ProjectGroupViewHolder) convertView.getTag();
        }
        StateTask stateTask = stateTasks.get(groupPosition);
        pgvh.projectState.setText(stateTask.TaskState + ":  "
                + stateTask.TaskViewBean.size());
        pgvh.projectState.setTextColor(mContext.getResources().getColor(
                CommonUtil.getTaskStateColor(groupPosition)));
        pgvh.bottomStripe.setBackgroundResource(CommonUtil.getTaskStateColor(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TaskViewHolder tvh;
        if (convertView == null) {
            tvh = new TaskViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent
                    .getContext());
            convertView = inflater.inflate(
                    R.layout.listitem_task_state, parent, false);
            tvh.taskTitle = (TextView) convertView.findViewById(R.id.tv_task_child_title);
            tvh.taskDateDur = (TextView) convertView.findViewById(R.id.tv_task_child_time);
            tvh.potrait = (CircleImageView)convertView.findViewById(R.id.civ_task_child_head_image);
            tvh.flag = (IconTextView)convertView.findViewById(R.id.itv_task_child_icon);
            convertView.setTag(tvh);
        } else {
            tvh = (TaskViewHolder) convertView.getTag();
        }
        ApiEntity.UserFenPai task =
                stateTasks.get(groupPosition).TaskViewBean.get(childPosition);
        tvh.taskTitle.setText(task.Title);
        String startTime = TaskUtils.parseToNewStringTime(oldFormat, newFormat, task.StartTime);
        String finishTime = TaskUtils.parseToNewStringTime(oldFormat, newFormat, task.FinishTime);
        tvh.taskDateDur.setText(startTime + "-" + finishTime);
        if(groupPosition == 2){
            tvh.taskDateDur.setTextColor(mContext.getResources().getColor(R.color.delay_time_color));
        }else{
            tvh.taskDateDur.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        //根据任务优先级，设置相应的时间展示
        switch (task.Yxj) {
            case TaskConstant.TaskEmergency.EMERGENCY:
                tvh.flag.setTextColor(mContext.getResources().getColor(R.color.task_emergency));
                break;
            case TaskConstant.TaskEmergency.VERY_EMERGENCY:
                tvh.flag.setTextColor(mContext.getResources().getColor(R.color.task_very_emergency));
                break;
            case TaskConstant.TaskEmergency.NORMAL:
            default:
                tvh.flag.setTextColor(mContext.getResources().getColor(R.color.task_normal));
                break;
        }

        if (EMWApplication.personMap != null && EMWApplication.personMap.get(task.Creator) != null) {
            String image = EMWApplication.personMap.get(task.Creator).Image;
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
            ImageLoader.getInstance().displayImage(uri, tvh.potrait, options);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ProjectGroupViewHolder {
        TextView projectState; //任务状态
        View bottomStripe;
    }

    class TaskViewHolder {
        TextView taskTitle;   //任务标题
        TextView taskDateDur; //任务时间
        CircleImageView potrait; //任务负责人头像
        IconTextView flag;    //优先级标识
    }
}
