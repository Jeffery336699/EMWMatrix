package cc.emw.mobile.project.adapter;

import android.content.Context;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.base.BaseListAdapter2;
import cc.emw.mobile.project.base.BaseViewHolder;
import cc.emw.mobile.project.bean.StateTask;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.view.CircleImageView;

/**
 * 项目主列表适配器类
 * Created by jven.wu on 2016/6/22.
 */
public class StateProjectAdapter extends BaseListAdapter2<StateTask,ApiEntity.UserFenPai> {
    private String oldFormat; //原始时间格式
    private String newFormat; //目标时间格式

    private boolean isExpanded = true;

    public StateProjectAdapter(Context context) {
        super(context);
        oldFormat = context.getResources().getString(
                R.string.timeformat6);
        newFormat = context.getResources().getString(
                R.string.timeformat7);
    }

    @Override
    protected void convertGroup(BaseViewHolder vh, StateTask item, int position,boolean isExpanded) {
        vh.setText(R.id.project_state_tv,item.TaskState + ":  " + item.TaskViewBean.size());
        vh.setTextColor(R.id.project_state_tv,CommonUtil.getTaskStateColor(position));
        vh.getView(R.id.bottom_stripe).setBackgroundResource(CommonUtil.getTaskStateColor(position));
    }

    @Override
    protected int getGroupLayoutId(int position, StateTask item) {
        return R.layout.listitem_project_state_title;
    }

    @Override
    protected void convertChild(BaseViewHolder vh, ApiEntity.UserFenPai item,int groupPosition, int position,boolean isLastChild) {

        vh.setText(R.id.tv_task_child_title,item.Title);
        String startTime = TaskUtils.parseToNewStringTime(oldFormat, newFormat, item.StartTime);
        String finishTime = TaskUtils.parseToNewStringTime(oldFormat, newFormat, item.FinishTime);
        vh.setText(R.id.tv_task_child_time,startTime + "-" + finishTime);

        CircleImageView potrait = (CircleImageView)vh.getView(R.id.civ_task_child_head_image);

        if(position == 2){
            vh.setTextColor(R.id.tv_task_child_time,R.color.delay_time_color);
        }else{
            vh.setTextColor(R.id.tv_task_child_time,R.color.black);
        }
        //根据任务优先级，设置相应的时间展示
        switch (item.Yxj) {
            case TaskConstant.TaskEmergency.EMERGENCY:
                vh.setTextColor(R.id.itv_task_child_icon,R.color.task_emergency);
                break;
            case TaskConstant.TaskEmergency.VERY_EMERGENCY:
                vh.setTextColor(R.id.itv_task_child_icon,R.color.task_very_emergency);
                break;
            case TaskConstant.TaskEmergency.NORMAL:
            default:
                vh.setTextColor(R.id.itv_task_child_icon,R.color.task_normal);
                break;
        }

        if (EMWApplication.personMap != null && EMWApplication.personMap.get(item.Creator) != null) {
            String image = EMWApplication.personMap.get(item.Creator).Image;
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
            vh.setImageForNet(potrait,uri);
        }
    }

    @Override
    protected int getChildLayoutId(int position, ApiEntity.UserFenPai item) {
        return R.layout.listitem_task_state;
    }

    @Override
    protected List<ApiEntity.UserFenPai> getChildDatas(StateTask groupItem) {
        return groupItem.TaskViewBean;
    }

}
