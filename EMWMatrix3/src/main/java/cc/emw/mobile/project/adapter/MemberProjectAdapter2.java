package cc.emw.mobile.project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.bean.GroupProject;
import cc.emw.mobile.project.bean.MemberProject;
import cc.emw.mobile.project.bean.ProjectViewBean;
import cc.emw.mobile.project.view.ObserveProjectActivity;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * 项目按成员显示适配器
 * Created by jven.wu on 2016/6/24.
 */
public class MemberProjectAdapter2 extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<MemberProject> memberProjects = new ArrayList<>(); //成员和项目数据
    private DisplayImageOptions options; //图片显示选项
    private HomeProjectAdapter2 adapter; //项目主列表适配器

    public MemberProjectAdapter2(Context context) {
        mContext = context;
        adapter = new HomeProjectAdapter2(mContext);
    }

    /**
     * 设置数据
     * @param mbs
     */
    public void setData(ArrayList<MemberProject> mbs){
        this.memberProjects.clear();
        this.memberProjects.addAll(mbs);
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

    @Override
    public int getGroupCount() {
        return memberProjects.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
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
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, final ViewGroup parent) {
        ProjectGroupViewHolder pgvh;
        if (convertView == null) {
            pgvh = new ProjectGroupViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent
                    .getContext());
            convertView = inflater.inflate(
                    R.layout.listitem_project_member, parent, false);
            pgvh.headerImg = (CircleImageView)convertView.findViewById(R.id.portrait_civ);
            pgvh.name = (TextView)convertView.findViewById(R.id.response_name_tv) ;
            pgvh.projectNum = (TextView)convertView.findViewById(R.id.project_num);
            pgvh.taskNum = (TextView)convertView.findViewById(R.id.task_num);
            pgvh.arrowIcon = (ImageView) convertView.findViewById(R.id.arrow_icon);
            pgvh.divider = (ImageView) convertView.findViewById(R.id.iv_summary_divider);
            pgvh.contentLL = (LinearLayout) convertView.findViewById(R.id.arrow_ll);

            convertView.setTag(pgvh);
        } else {
            pgvh = (ProjectGroupViewHolder) convertView.getTag();
        }
        MemberProject memberProject = memberProjects.get(groupPosition);

        //通过UserId从全局获取相应的人员信息，并设置人名和头像显示
        if (EMWApplication.personMap != null && EMWApplication.personMap.get(memberProject.UserId) != null) {
            String image = EMWApplication.personMap.get(memberProject.UserId).Image;
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
            ImageLoader.getInstance().displayImage(uri, pgvh.headerImg, options);
            UserInfo userInfo = EMWApplication.personMap.get(memberProject.UserId);
            pgvh.name.setText(userInfo.Name);
        }
        pgvh.projectNum.setText("项目： " + memberProject.ProjectNum);
        pgvh.taskNum.setText("任务： " + memberProject.TaskNum);
        if (1 > 0) {
            /*pgvh.arrowIcon
                    .setImageResource(isExpanded ? R.drawable.list_up_jiantou
                            : R.drawable.list_down_jiantou);
            pgvh.contentLL.setVisibility(View.VISIBLE);*/
        } else {
            pgvh.contentLL.setVisibility(View.INVISIBLE);
        }
        //设置item点击事件用于触发收缩和展开列表
        pgvh.contentLL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    ((ExpandableListView) parent).expandGroup(groupPosition);
                } else {
                    ((ExpandableListView) parent).collapseGroup(groupPosition);
                }
            }
        });
//        ((ExpandableListView) parent).expandGroup(groupPosition);
//        pgvh.divider.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
//        pgvh.divider.setVisibility(View.GONE);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder cvh;
        if (convertView == null) {
            cvh = new ChildViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(
                    R.layout.listitem_project_member_content, parent, false);
            cvh.mListView = (ExpandableListView)convertView.findViewById(R.id.project_lv);
            cvh.divider = (ImageView) convertView.findViewById(R.id.iv_summarytask_divider);
            convertView.setTag(cvh);
        } else {
            cvh = (ChildViewHolder) convertView.getTag();
        }
        adapter.setData(memberProjects.get(groupPosition).groupProjects);
        cvh.mListView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(cvh.mListView);
        cvh.mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                ProjectViewBean project =
                        (ProjectViewBean) adapter.getChild(groupPosition, childPosition);
                GroupProject groupProject = (GroupProject)adapter.getGroup(groupPosition);
                Intent intent = new Intent(mContext, ObserveProjectActivity.class);
                intent.putExtra(ObserveProjectActivity.EXTRA_PROJECT_ID, project.ProjectId);
                intent.putExtra(ObserveProjectActivity.EXTRA_PROJECT_NAME, project.ProjectName);
//                intent.putIntegerArrayListExtra("members",groupProject.UsersId);
                intent.putIntegerArrayListExtra("groupmembers", groupProject.UsersId);
                intent.putExtra("groupName",groupProject.GroupName);
                intent.putExtra("project",project.Project);
                intent.putExtra("group",groupProject.group);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                mContext.startActivity(intent);
                return true;
            }
        });
        cvh.divider.setVisibility(isLastChild ? View.VISIBLE : View.GONE);
//        cvh.divider.setVisibility(View.GONE);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ProjectGroupViewHolder {
        CircleImageView headerImg; //头像
        TextView name;             //名称
        TextView projectNum;       //项目数量
        TextView taskNum;          //任务数量
        LinearLayout contentLL;
        ImageView arrowIcon;
        ImageView divider;
    }

    class ChildViewHolder {
        ImageView divider; //分割线
        ExpandableListView mListView; //项目列表
    }

    /**
     * 计算列表高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ExpandableListView listView) {
        // 获取ListView对应的Adapter
        ExpandableListAdapter exAdapter = listView.getExpandableListAdapter();
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null || exAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for(int i = 0;i<exAdapter.getGroupCount();i++){
            View listItem = exAdapter.getGroupView(i, true,null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            for(int j = 0;j<exAdapter.getChildrenCount(i);j++){
                boolean islastChild = j == exAdapter.getChildrenCount(i)-1 ? true:false;
                listItem = exAdapter.getChildView(i,j,islastChild,null, listView);
                listItem.measure(0,0);
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
        }
//        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0); // 计算子项View 的宽高
//            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
//        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
