package cc.emw.mobile.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.percent.PercentRelativeLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.me.MyTaskDetailActivity;
import cc.emw.mobile.me.entity.PendingEvents;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.AnimatedExpandableListView;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.me.adapter
 * @data on 2018/8/29  15:35
 * @describe TODO
 */

public class PendingEventAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter implements ITaskModifyView {

    private List<PendingEvents> templateList;
    private Context context;
    private TaskPresenter mTaskPresenter = new TaskPresenter(this);
    private boolean limit = false;//负责人权限 当前用户属于负责人，具有权限 为true
    private ApiEntity.UserInfo mCurrentUser;//当前用户
    public int groupPosition, childPosition;

    public PendingEventAdapter(Context context) {
        this.context = context;
        templateList = new ArrayList<>();
    }

    @Override
    public void createTask(String respInfo) {
    }

    @Override
    public void modifyTask(String s) {
        if ("1".equals(s)) {
            ToastUtil.showToast(context, "提交任务成功!",
                    R.drawable.tishi_ico_gougou);
            templateList.get(groupPosition).getNavigations().get(childPosition).State = 4;
            notifyDataSetChanged();

        } else {
            ToastUtil.showToast(context, "提交任务失败!");
        }

    }

    @Override
    public void getFileList(List<ApiEntity.Files> files) {

    }

    @Override
    public void completeFresh() {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        ToastUtil.showToast(context, "提交任务失败!");
    }

    //提供外面接口
    public void setData(List<ApiEntity.WeekInfo> templates, List<List<ApiEntity.UserFenPai>> respList) {
        templateList.clear();
        //组织数据
        templateList.add(new PendingEvents(templates.get(0), respList.get(0)));
        templateList.add(new PendingEvents(templates.get(1), respList.get(1)));
        templateList.add(new PendingEvents(templates.get(2), respList.get(2)));
        templateList.add(new PendingEvents(templates.get(3), respList.get(3)));
        templateList.add(new PendingEvents(templates.get(4), respList.get(4)));
        templateList.add(new PendingEvents(templates.get(5), respList.get(5)));
        templateList.add(new PendingEvents(templates.get(6), respList.get(6)));
    }

    @Override
    public ApiEntity.UserFenPai getChild(int groupPosition, int childPosition) {
        return templateList.get(groupPosition).getNavigations()
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent) {
        ChildViewHolder cvh;
        if (convertView == null) {
            cvh = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.pendingevent_chile_item, null);
            cvh.childNameTv = (TextView) convertView.findViewById(R.id.tv_leftmenu_childname);
            cvh.pending_view = (View) convertView.findViewById(R.id.pending_view);
            cvh.pending_item_back = (LinearLayout) convertView.findViewById(R.id.pending_item_back);
            cvh.week_button = (Button) convertView.findViewById(R.id.week_button);
            convertView.setTag(R.id.tag_first, cvh);
        } else {
            cvh = (ChildViewHolder) convertView.getTag(R.id.tag_first);
        }
        cvh.childNameTv.setPaintFlags(cvh.childNameTv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        cvh.week_button.setVisibility(View.GONE);
        final ApiEntity.UserFenPai nav = getChild(groupPosition, childPosition);
        cvh.childNameTv.setText(nav.Title);
        if (templateList.get(groupPosition).getWeekInfo().state == 1) {
            if (nav.State == 4) {
                cvh.childNameTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                cvh.childNameTv.setTextColor(Color.GRAY);
                cvh.week_button.setVisibility(View.GONE);
            } else {
                cvh.childNameTv.setTextColor(Color.BLACK);
                cvh.week_button.setVisibility(View.VISIBLE);
            }
        } else {
            cvh.childNameTv.setTextColor(Color.GRAY);
            cvh.week_button.setVisibility(View.GONE);
        }
        if (childPosition == (templateList.get(groupPosition).getNavigations().size() - 1)) {
            cvh.pending_item_back.setBackgroundResource(R.drawable.pendingevent_shape_white);
            cvh.pending_view.setVisibility(View.VISIBLE);
        } else {
            cvh.pending_view.setVisibility(View.GONE);
            cvh.pending_item_back.setBackgroundResource(R.color.white);
        }
        //ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1);
        /*Animation sa = new AlphaAnimation(0.1f,1.0f);
        sa.setDuration(500);
        LayoutAnimationController lac = new LayoutAnimationController(sa, 0.5f);
        cvh.pending_item_back.setLayoutAnimation(lac);*/
        //每个item监听事件
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent meIntent = new Intent(context, MyTaskDetailActivity.class);
                meIntent.putExtra("taskid", nav.ID);
                context.startActivity(meIntent); //待办工作详情
            }
        });
        cvh.week_button.setOnClickListener(new MyClick(groupPosition, childPosition, nav));
        return convertView;
    }

    public class MyClick implements View.OnClickListener {
        int groupPos;
        int childPos;
        ApiEntity.UserFenPai navInfon;

        public MyClick(int groupPositiosn, int childPosition, ApiEntity.UserFenPai nav) {
            groupPos = groupPositiosn;
            childPos = childPosition;
            navInfon = nav;
        }

        @Override
        public void onClick(View v) {
            //设置负责人
            mCurrentUser = EMWApplication.personMap.get(PrefsUtil.readUserInfo().ID);
            String mainUser = navInfon.MainUser;
            List<ApiEntity.UserInfo> mainUsers = TaskUtils.getUsers(mainUser);
            if (mainUsers.contains(mCurrentUser) || navInfon.Creator == PrefsUtil.readUserInfo().ID) {
                limit = true;
            } else {
                limit = false;
            }
            if (!limit) {
                ToastUtil.showToast(context, "你没有提交任务的权限!");
                return;
            }
            groupPosition = groupPos;
            childPosition = childPos;
            mTaskPresenter.modifyTask(navInfon);
        }
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return templateList.get(groupPosition).getNavigations().size();
    }

    @Override
    public ApiEntity.WeekInfo getGroup(int groupPosition) {
        return templateList.get(groupPosition).getWeekInfo();
    }

    @Override
    public int getGroupCount() {
        return templateList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder gvh;
        if (convertView == null) {
            gvh = new GroupViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.pendingevent_group_item, null);
            gvh.groupNameTv = (TextView) convertView.findViewById(R.id.tv_leftmenu_groupname);
            gvh.pending_view = (View) convertView.findViewById(R.id.pending_view);
            gvh.layout_groupname_min = (PercentRelativeLayout) convertView.findViewById(R.id.layout_groupname_min);
            gvh.root_view = (LinearLayout) convertView.findViewById(R.id.root_view);
            gvh.tv_groupname_min = (TextView) convertView.findViewById(R.id.tv_groupname_min);
            convertView.setTag(R.id.tag_first, gvh);
        } else {
            gvh = (GroupViewHolder) convertView.getTag(R.id.tag_first);
        }

        ApiEntity.WeekInfo temp = getGroup(groupPosition);
        gvh.groupNameTv.setText(temp.weekText);
        gvh.tv_groupname_min.setText(temp.weekText);
        if (temp.state == 0) {
            gvh.root_view.setBackgroundResource(R.drawable.pendingevent_shape);
        } else {
            gvh.root_view.setBackgroundResource(R.drawable.pendingevent_shape_red);
        }
        if (null == templateList.get(groupPosition).getNavigations() || templateList.get(groupPosition).getNavigations().size() == 0) {
            gvh.root_view.setVisibility(View.VISIBLE);
            gvh.pending_view.setVisibility(View.VISIBLE);
            gvh.layout_groupname_min.setVisibility(View.GONE);
        } else if (isExpanded) {
            gvh.root_view.setVisibility(View.GONE);
            gvh.pending_view.setVisibility(View.GONE);
            gvh.layout_groupname_min.setVisibility(View.VISIBLE);
            /*if (null != templateList.get(groupPosition).getNavigations() && templateList.get(groupPosition).getNavigations().size() > 0) {
                gvh.pending_view.setVisibility(View.GONE);
            }*/
        } else {
            gvh.root_view.setVisibility(View.VISIBLE);
            gvh.pending_view.setVisibility(View.VISIBLE);
            gvh.layout_groupname_min.setVisibility(View.GONE);
            /*if (null != templateList.get(groupPosition).getNavigations() && templateList.get(groupPosition).getNavigations().size() > 0) {
                gvh.pending_view.setVisibility(View.VISIBLE);
            }*/
        }

        convertView.setTag(R.id.tag_second, temp);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupViewHolder {
        TextView groupNameTv;
        View pending_view;
        PercentRelativeLayout layout_groupname_min;
        LinearLayout root_view;
        TextView tv_groupname_min;
    }

    public class ChildViewHolder {
        TextView childNameTv;
        View pending_view;
        LinearLayout pending_item_back;
        Button week_button;
    }
}
