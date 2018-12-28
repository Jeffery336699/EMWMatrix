package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.base.BaseListAdapter2;
import cc.emw.mobile.project.base.BaseViewHolder;
import cc.emw.mobile.project.bean.GroupProject;
import cc.emw.mobile.project.bean.MemberProject;
import cc.emw.mobile.project.view.StateBoardActivity;
import cc.emw.mobile.project.view.TaskSpectacularActivity;
import cc.emw.mobile.task.activity.TaskCreateActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.AnimatedExpandableListView;
import cc.emw.mobile.view.ExExpandableListView;

/**
 * 项目按成员显示适配器
 * Created by jven.wu on 2016/6/24.
 */
public class MemberProjectAdapter extends BaseListAdapter2<MemberProject,List<GroupProject>> {
    private final String UNSTART = "未开始：%s";
    private final String PROCESSING = "进行中：%s";
    private final String FINISHED = "已完成：%s";

    public MemberProjectAdapter(Context context) {
        super(context);
    }

    @Override
    protected void convertChild(BaseViewHolder vh, final List<GroupProject> item, int groupPosition, int position, boolean isLastChild) {
        final ExExpandableListView listView = vh.getView(R.id.listView);
        HomeProjectAdapter adapter = new HomeProjectAdapter(getContext());
        adapter.addItem(item);
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TaskSpectacularActivity.currentType = TaskSpectacularActivity.PROJECT_TASK;
                TaskSpectacularActivity.currentProjectId = item.get(groupPosition).projectViews.get(childPosition).ProjectId + "";
                Intent spectacularIntent = new Intent(getContext(),TaskSpectacularActivity.class);
                getContext().startActivity(spectacularIntent);

                /*TaskSpectacularActivity.currentType = TaskSpectacularActivity.PROJECT_TASK;
                TaskSpectacularActivity.currentProjectId = item.get(groupPosition).projectViews.get(childPosition).ProjectId + "";
                Intent spectacularIntent = new Intent(getContext(),StateBoardActivity.class);
                groupPos = groupPosition;
                childPos = childPosition;
                spectacularIntent.putExtra(TaskCreateActivity.TEAM_USERPROJECT, item.get(groupPosition).projectViews.get(childPosition).Project);
                spectacularIntent.putExtra("group_project", item.get(groupPosition));
                getContext().startActivity(spectacularIntent);*/
                return false;
            }
        });
    }

    @Override
    protected int getChildLayoutId(int position, List<GroupProject> item) {
        return R.layout.listitem_member_project_child;
    }

    @Override
    protected List<List<GroupProject>> getChildDatas(MemberProject groupItem) {
        List<List<GroupProject>> tmp = new ArrayList<>();
        tmp.add(groupItem.groupProjects);
        return tmp;
    }

    @Override
    protected void convertGroup(BaseViewHolder vh, MemberProject item, int position, boolean isExpanded) {
        //设置人员头像和名称
        if (EMWApplication.personMap != null && EMWApplication.personMap.get(item.UserId) != null) {
            String image = EMWApplication.personMap.get(item.UserId).Image;
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
            Logger.d("MemberProjectAdapter",uri);
            vh.setImageForNet(R.id.member_project_portrait,uri);
            UserInfo userInfo = EMWApplication.personMap.get(item.UserId);
            vh.setText(R.id.member_project_name,userInfo.Name);
        }

        vh.setText(R.id.member_project_unstart,String.format(UNSTART,item.ProjectNum));
        vh.setText(R.id.member_project_finished,String.format(FINISHED,item.TaskNum));

        setIndicate(vh,isExpanded);
    }

    @Override
    protected int getGroupLayoutId(int position, MemberProject item) {
        return R.layout.listitem_member_project_group;
    }

    /**
     * 设置展开和收缩的小图标
     * @param vh
     * @param isExpanded
     */
    private void setIndicate(BaseViewHolder vh, boolean isExpanded) {
        vh.setTextColor(R.id.member_project_indicate,
                isExpanded ? R.color.project_color_335B9D : R.color.project_color_CBCBCB);
        vh.setIconCode(R.id.member_project_indicate, isExpanded ? "eb6a" : "eb67");
    }
}
