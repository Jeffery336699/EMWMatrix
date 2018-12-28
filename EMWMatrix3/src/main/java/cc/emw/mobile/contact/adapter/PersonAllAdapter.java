package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserAllGroup;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.MyGridView;

/**
 * Created by ${zrjt} on 2016/6/28.
 */
public class PersonAllAdapter extends BaseExpandableListAdapter {

    public List<UserAllGroup> mDataList;

    private Context mContext;

    public PersonAllAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<UserAllGroup> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getGroupCount() {
        return mDataList == null ? 0 : mDataList.size();
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
        GroupHolder groupHolder = null;
        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.elv_list_item_group_person, null);
            groupHolder.gName = (TextView) convertView.findViewById(R.id.tv_depart_name);
            groupHolder.iconTextView = (IconTextView) convertView.findViewById(R.id.ic_tv_dir);
            groupHolder.num = (TextView) convertView.findViewById(R.id.tv_person_group_num);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.iconTextView.setIconText(isExpanded ? "eb67" : "eb6a");
        UserAllGroup userAllGroup = mDataList.get(groupPosition);
        groupHolder.gName.setText(userAllGroup.gName);
        groupHolder.num.setText(userAllGroup.num + "");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if (convertView == null) {
            childHolder = new ChildHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.elv_list_item_child_person, null);
            childHolder.myGridView = (MyGridView) convertView.findViewById(R.id.group_grid_view_member);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        List<UserInfo> userInfos = mDataList.get(groupPosition).userInfos;
        PersonAllGroupAdapter adapter = new PersonAllGroupAdapter(mContext);
        adapter.setData(userInfos);
        childHolder.myGridView.setAdapter(adapter);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class GroupHolder {
        TextView gName;
        IconTextView iconTextView;
        TextView num;
    }

    private class ChildHolder {
        MyGridView myGridView;
    }

}
