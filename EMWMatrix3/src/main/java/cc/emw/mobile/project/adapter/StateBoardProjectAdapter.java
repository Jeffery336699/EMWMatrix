package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.base.BaseListAdapter;
import cc.emw.mobile.project.base.BaseViewHolder;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;

/**
 * Created by jven.wu on 2016/11/21.
 */
public class StateBoardProjectAdapter extends BaseListAdapter<ApiEntity.UserFenPai> {

    public StateBoardProjectAdapter(Context context) {
        super(context);
    }

    @Override
    protected void convert(BaseViewHolder vh, ApiEntity.UserFenPai item, int position) {
        vh.setGone(R.id.sbc1_project_name);
        if (item.State == TaskConstant.TaskState.FINISHED) {
            TextView tv = vh.getView(R.id.sbc1_task_name);
            CommonUtil.addStrikeSpan(item.Title, tv);
            vh.setTextColor(R.id.sbc1_task_name, R.color.gray_3);
        } else {
            vh.setText(R.id.sbc1_task_name, item.Title);
        }
        //根据任务优先级设置背景色
        RelativeLayout contain = vh.getView(R.id.sbc1_container_ll);
        contain.setBackgroundResource(CommonUtil.getTaskBg(item.Yxj));
        contain.setPadding(0, 0, 0, DisplayUtil.dip2px(getContext(), 10));

        CommonUtil.setTaskDeadLineTimeString((TextView) vh.getView(R.id.sbc1_time), item);

        if (EMWApplication.personMap != null && EMWApplication.personMap.get(item.Creator) != null) {
            String image = EMWApplication.personMap.get(item.Creator).Image;
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
            vh.setImageForNet(R.id.scb1_portrait, uri);
        }
    }

    @Override
    protected int getLayoutId(int position, ApiEntity.UserFenPai item) {
        return R.layout.listitem_state_board_child1;
    }
}
