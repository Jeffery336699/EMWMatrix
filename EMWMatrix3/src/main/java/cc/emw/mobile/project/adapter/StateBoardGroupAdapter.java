package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.base.BaseListAdapter2;
import cc.emw.mobile.project.base.BaseViewHolder;
import cc.emw.mobile.project.bean.GroupTask;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;

/**
 * Created by jven.wu on 2016/11/14.
 */
public class StateBoardGroupAdapter extends BaseListAdapter2<GroupTask, ApiEntity.UserFenPai> {
    private final String groupTextFormat = "%s • %s";

    public StateBoardGroupAdapter(Context context) {
        super(context);
    }

    @Override
    protected void convertChild(BaseViewHolder vh, ApiEntity.UserFenPai item, int groupPosition, int position, boolean isLastChild) {
        vh.setText(R.id.sbc1_project_name, item.ProjectElem.Name);
        if(item.State == TaskConstant.TaskState.FINISHED) {
            TextView tv =  vh.getView(R.id.sbc1_task_name);
            CommonUtil.addStrikeSpan(item.Title, tv);
            vh.setTextColor(R.id.sbc1_task_name,R.color.gray_3);
            ((CheckBox)vh.getView(R.id.sbc1_cb)).setChecked(true);
        }
        else {
            vh.setText(R.id.sbc1_task_name, item.Title);
            ((CheckBox)vh.getView(R.id.sbc1_cb)).setChecked(false);
        }
        if (EMWApplication.personMap != null && EMWApplication.personMap.get(item.Creator) != null) {
            String image = EMWApplication.personMap.get(item.Creator).Image;
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
            vh.setImageForNet(R.id.scb1_portrait, uri);
        }
        RelativeLayout contain = vh.getView(R.id.sbc1_container_ll);
        contain.setBackgroundResource(CommonUtil.getTaskBg(item.Yxj));
        contain.setPadding(0,0,0, DisplayUtil.dip2px(getContext(),10));
        vh.getView(R.id.sbc1_project_name).setBackgroundResource(CommonUtil.getProjectBg(item.ProjectElem.Color));

        CommonUtil.setTaskDeadLineTimeString((TextView) vh.getView(R.id.sbc1_time), item);
    }

    @Override
    protected int getChildLayoutId(int position, ApiEntity.UserFenPai item) {
        return R.layout.listitem_state_board_child1;
    }

    @Override
    protected List<ApiEntity.UserFenPai> getChildDatas(GroupTask groupItem) {
        return groupItem.Tasks;
    }

    @Override
    protected void convertGroup(BaseViewHolder vh, GroupTask item, int position, boolean isExpanded) {
        vh.setText(R.id.project_task_num, String.format(groupTextFormat,
                item.Name,
                item.Tasks != null ? item.Tasks.size() : 0));
        setIndicate(vh, isExpanded);
    }

    @Override
    protected int getGroupLayoutId(int position, GroupTask item) {
        return R.layout.listitem_state_board_group;
    }

    /**
     * 设置展开和收缩的小图标
     *
     * @param vh
     * @param isExpanded
     */
    private void setIndicate(BaseViewHolder vh, boolean isExpanded) {
        vh.setTextColor(R.id.project_group_indicate,
                isExpanded ? R.color.project_color_335B9D : R.color.project_color_CBCBCB);
        vh.setIconCode(R.id.project_group_indicate, isExpanded ? "eb6a" : "eb67");
    }
}
