package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.view.MyGridView;

/**
 * Created by ${zrjt} on 2016/8/8.
 */
public class GroupAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<GroupInfo> mGroupInfos;

    private List<GroupInfo> unMGroupInfos;

    public GroupAdapter(Context context) {
        this.context = context;
    }

    public void setMGData(List<GroupInfo> mGroupInfos) {
        this.mGroupInfos = mGroupInfos;
    }

    public void setUGData(List<GroupInfo> unMGroupInfos) {
        this.unMGroupInfos = unMGroupInfos;
    }

    @Override
    public int getGroupCount() {
        return 2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder gHolder;
        if (convertView == null) {
            gHolder = new GroupViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_group_parent, null);
            gHolder.textView = (TextView) convertView.findViewById(R.id.personnel_tv_dept);
            convertView.setTag(gHolder);
        } else {
            gHolder = (GroupViewHolder) convertView.getTag();
        }
        if (groupPosition == 0) {
            gHolder.textView.setText("已加入");
        } else {
            gHolder.textView.setText("未加入");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder cHolder;
        if (convertView == null) {
            cHolder = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_group_child, null);
            cHolder.gridView = (MyGridView) convertView.findViewById(R.id.group_grid_view_member);
            convertView.setTag(cHolder);
        } else {
            cHolder = (ChildViewHolder) convertView.getTag();
        }
        GroupAdapters adapters = new GroupAdapters(context);
        if (groupPosition == 0) {
            adapters.setData(mGroupInfos);
        } else {
            adapters.setData(unMGroupInfos);
        }
        cHolder.gridView.setAdapter(adapters);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupViewHolder {
        TextView textView;
    }

    class ChildViewHolder {
        MyGridView gridView;
    }
}
