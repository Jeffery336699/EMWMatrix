package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.base.BaseListAdapter2;
import cc.emw.mobile.project.base.BaseViewHolder;
import cc.emw.mobile.project.bean.GroupProject;
import cc.emw.mobile.project.bean.ProjectViewBean;
import cc.emw.mobile.project.view.TeamActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;

/**
 * 项目主列表适配器类
 * Created by jven.wu on 2016/6/22.
 */
public class HomeProjectAdapter extends BaseListAdapter2<GroupProject, ProjectViewBean> {
    public HomeProjectAdapter(Context context) {
        super(context);
    }

    @Override
    protected void convertChild(BaseViewHolder vh, ProjectViewBean item,int groupPosition, int position, boolean isLastChild) {
        vh.setText(R.id.project_name_tv,item.ProjectName);
        vh.setText(R.id.right_icon,String.valueOf(item.Project.TaskCount));
        //根据ProjectColor设置相关view的颜色
        View leftStripe = vh.getView(R.id.left_stripe);
        if (item.ProjectColor == 0) {
            vh.setTextColor(R.id.project_name_tv, R.color.cm_text);
            leftStripe.setVisibility(View.INVISIBLE);
        } else {
            vh.setTextColor(R.id.project_name_tv,
                    CommonUtil.getProjectColor(item.ProjectColor));
            leftStripe.setVisibility(View.VISIBLE);
            leftStripe.setBackgroundResource(CommonUtil.getProjectColor(item.ProjectColor));
        }

        //当不是第一个时，隐藏相应UI
        if(position != 0){
            vh.setViewsGone(R.id.project_group_stripe1,R.id.project_group_stripe2);
        }else {
            vh.setViewsVisible(R.id.project_group_stripe1,R.id.project_group_stripe2);
        }

        //当不是最后一个时，隐藏相应UI
        if(!isLastChild){
            vh.setGone(R.id.project_group_stripe3);
            vh.setVisibility(R.id.project_item_stripe);
        }else{
            vh.setVisibility(R.id.project_group_stripe3);
            vh.setGone(R.id.project_item_stripe);
        }

        View bgView = vh.getView(R.id.project_group_projects_ll);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = DisplayUtil.dip2px(mContext, 16);
        params.rightMargin = DisplayUtil.dip2px(mContext, 16);
        params.topMargin = position == 0 ? DisplayUtil.dip2px(mContext, 2) : 0;
        bgView.setLayoutParams(params);
        int size = getRealChildrenCount(groupPosition);
        if (size == 1) {
            bgView.setBackgroundResource(R.drawable.pj_bg);
        } else {
            if (position == 0) {
                bgView.setBackgroundResource(R.drawable.pj_bg_top);
            } else if (position < size - 1) {
                bgView.setBackgroundResource(R.drawable.pj_bg_center);
            } else {
                bgView.setBackgroundResource(R.drawable.pj_bg_bottom);
            }
        }

        vh.getConvertView().setTag(R.id.tag_first, getGroup(groupPosition));
        vh.getConvertView().setTag(R.id.tag_second, item);
    }

    @Override
    protected int getChildLayoutId(int position, ProjectViewBean item) {
        return R.layout.listitem_project_child;
    }

    @Override
    protected List<ProjectViewBean> getChildDatas(GroupProject groupItem) {
        return groupItem.projectViews;
    }

    @Override
    protected void convertGroup(BaseViewHolder vh, final GroupProject item, int position, boolean isExpanded) {
        vh.setText(R.id.project_group_name, item.GroupName);
        vh.setTextColor(R.id.project_group_indicate,
                isExpanded ? R.color.project_color_335B9D : R.color.project_color_CBCBCB);
        vh.setIconCode(R.id.project_group_indicate, isExpanded ? "eb6a" : "eb67");
        if (isExpanded && getRealChildrenCount(position) > 0) {
            vh.setInVisibility(R.id.project_group_line);
        } else {
            vh.setVisibility(R.id.project_group_line);
        }
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, item.GroupImg);
        vh.setGroupImageForNet(R.id.project_group_portrait, uri);
        vh.setOnClick(R.id.project_group_portrait, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TeamActivity.class);
                intent.putExtra("groupName", item.GroupName);
                intent.putExtra("groupImg", item.GroupImg);
                intent.putIntegerArrayListExtra("members", item.UsersId);
                intent.putExtra("group", item.group);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                mContext.startActivity(intent);
            }
        });
        vh.getConvertView().setTag(R.id.tag_second, item);
    }

    @Override
    protected int getGroupLayoutId(int position, GroupProject item) {
        return R.layout.listitem_project_group2;
    }

}
