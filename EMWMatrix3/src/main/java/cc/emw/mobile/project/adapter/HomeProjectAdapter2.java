package cc.emw.mobile.project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.bean.GroupProject;
import cc.emw.mobile.project.bean.ProjectViewBean;
import cc.emw.mobile.project.view.TeamActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * 项目主列表适配器类
 * Created by jven.wu on 2016/6/22.
 */
public class HomeProjectAdapter2 extends BaseExpandableListAdapter {
    public static final String TAG = "HomeProjectAdapter";
    private Context mContext;
    private ArrayList<GroupProject> groupProjects; //项目及项目组数据
    private DisplayImageOptions options;           //图片显示选项

    public HomeProjectAdapter2(Context context) {
        this.mContext = context;
        groupProjects = new ArrayList<>();
        //初始化图片显示选项
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    /**
     * 设置数据
     * @param groupProjects
     */
    public void setData(ArrayList<GroupProject> groupProjects) {

        this.groupProjects.clear();
        if (groupProjects != null) {
            this.groupProjects.addAll(groupProjects);
        }

        Log.d(TAG, "setData()->count: " + this.groupProjects.size());
    }

    @Override
    public int getGroupCount() {
        return groupProjects.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupProjects.get(groupPosition).projectViews.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupProjects.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupProjects.get(groupPosition).projectViews.get(childPosition);
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
        final ProjectGroupViewHolder pgvh;
        if (convertView == null) {
            pgvh = new ProjectGroupViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent
                    .getContext());
            convertView = inflater.inflate(
                    R.layout.listitem_project_group, parent, false);
            pgvh.groupName = (TextView) convertView.findViewById(R.id.group_name_tv);
            pgvh.memberContent = (LinearLayout) convertView.findViewById(R.id.member_content_ll);
            pgvh.memberCollect = (LinearLayout) convertView.findViewById(R.id.member_collect_ll);
            pgvh.memberNum = (TextView) convertView.findViewById(R.id.member_num_tv);
            convertView.setTag(pgvh);
        } else {
            pgvh = (ProjectGroupViewHolder) convertView.getTag();
        }

        GroupProject groupProject = groupProjects.get(groupPosition);
        pgvh.groupName.setText(groupProject.GroupName);
        //移除上个列表item中旧的成员
        pgvh.memberCollect.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                DisplayUtil.dip2px(mContext, 24), DisplayUtil.dip2px(mContext, 24));
        params.setMargins(6, 0, 0, 0);

        int displayNum = 0;
        if (groupProject.UsersId.size() > 4) {
            pgvh.memberNum.setVisibility(View.VISIBLE);
            pgvh.memberNum.setText(groupProject.UsersId.size() + "");
            displayNum = 4;
        } else {
            pgvh.memberNum.setVisibility(View.GONE);
            displayNum = groupProject.UsersId.size();
        }
        //动态添加成员
        for (int i = 0; i < displayNum; i++) {
            CircleImageView c = new CircleImageView(mContext);
            c.setLayoutParams(params);
            int userId = groupProject.UsersId.get(i);
            if (EMWApplication.personMap != null && EMWApplication.personMap.get(userId) != null) {
                String image = EMWApplication.personMap.get(userId).Image;
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
                ImageLoader.getInstance().displayImage(uri, c, options);
            }
            pgvh.memberCollect.addView(c);
        }

//        pgvh.memberContent.setTag();
        pgvh.memberContent.setTag(groupProject);
        pgvh.memberContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupProject temGroupProject = (GroupProject) pgvh.memberContent.getTag();
                Intent intent = new Intent(mContext, TeamActivity.class);
                intent.putExtra("groupName", temGroupProject.GroupName);
                intent.putExtra("groupImg", temGroupProject.GroupImg);
                intent.putIntegerArrayListExtra("members", temGroupProject.UsersId);
                intent.putExtra("group", temGroupProject.group);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                mContext.startActivity(intent);
            }
        });
        ((ExpandableListView) parent).expandGroup(groupPosition);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ProjectViewHolder pvh;
        if (convertView == null) {
            pvh = new ProjectViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent
                    .getContext());
            convertView = inflater.inflate(
                    R.layout.listitem_project, parent, false);
            pvh.projectName = (TextView) convertView.findViewById(R.id.project_name_tv);
            pvh.leftStripe = convertView.findViewById(R.id.left_stripe);
            convertView.setTag(pvh);
        } else {
            pvh = (ProjectViewHolder) convertView.getTag();
        }
        ProjectViewBean projectView =
                groupProjects.get(groupPosition).projectViews.get(childPosition);
        pvh.projectName.setText(projectView.ProjectName);
        //根据ProjectColor设置相关view的颜色
        if (projectView.ProjectColor == 0) {
            pvh.projectName.setTextColor(
                    mContext.getResources().getColor(R.color.mainme_numbar_text));
            pvh.leftStripe.setVisibility(View.INVISIBLE);
        } else {
            pvh.projectName.setTextColor(mContext.getResources().getColor(
                    CommonUtil.getProjectColor(projectView.ProjectColor)));
            pvh.leftStripe.setVisibility(View.VISIBLE);
            pvh.leftStripe.setBackgroundResource(CommonUtil.getProjectColor(projectView.ProjectColor));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ProjectGroupViewHolder {
        TextView groupName; //组名
        TextView memberNum; //组成员数
        LinearLayout memberCollect;
        LinearLayout memberContent;
    }

    class ProjectViewHolder {
        TextView projectName; //项目名
        View leftStripe;      //左侧边的颜色标识
    }
}
