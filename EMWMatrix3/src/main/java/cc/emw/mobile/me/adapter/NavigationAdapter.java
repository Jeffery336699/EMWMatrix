package cc.emw.mobile.me.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.NavGroup;
import cc.emw.mobile.me.UpdateAliasActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SwitchButton;

/**
 * 项目主列表适配器类
 * Created by jven.wu on 2016/6/22.
 */
public class NavigationAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<NavGroup> navGroupList;
    private Dialog mLoadingDialog;
    private boolean isUpdate;
    private int selectedGroup;
    private int selectedChild;

    public NavigationAdapter(Context context, boolean isUpdate) {
        this.mContext = context;
        this.isUpdate = isUpdate;
        navGroupList = new ArrayList<>();
    }

    public void onPick(int[] position) {
        selectedGroup = position[0];
        selectedChild = position[1];
    }

    public void onDrop(int[] from, int[] to) {
        if (to[0] > navGroupList.size() || to[0] < 0 || to[1] < 0
                || from[0] != to[0])// from[0]!=to[0]保证不会越组拖动
            return;
        ApiEntity.Navigation tValue = getValue(from);
        navGroupList.get(from[0]).NavList.remove(tValue);
        navGroupList.get(from[0]).NavList.add(to[1], tValue);
        selectedGroup = -1;
        selectedChild = -1;
        notifyDataSetChanged();
    }

    private ApiEntity.Navigation getValue(int[] id) {
        return (ApiEntity.Navigation) navGroupList.get(id[0]).NavList.get(
                id[1]);
    }

    /**
     * 设置数据
     * @param navGroupList
     */
    public void setData(List<NavGroup> navGroupList) {
        this.navGroupList.clear();
        if (navGroupList != null) {
            this.navGroupList.addAll(navGroupList);
        }
    }

    @Override
    public int getGroupCount() {
        return navGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return navGroupList.get(groupPosition).NavList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return navGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return navGroupList.get(groupPosition).NavList.get(childPosition);
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupViewHolder gvh;
        if (convertView == null) {
            gvh = new GroupViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.listitem_navigation_group, parent, false);
            gvh.groupIcon = (IconTextView) convertView.findViewById(R.id.tv_navigation_groupicon);
            gvh.groupName = (TextView) convertView.findViewById(R.id.tv_navigation_groupname);
            gvh.groupAlias = (TextView) convertView.findViewById(R.id.tv_navigation_groupalias);
            gvh.switchButton = (SwitchButton) convertView.findViewById(R.id.sb_navigation_group);
            convertView.setTag(gvh);
        } else {
            gvh = (GroupViewHolder) convertView.getTag();
        }

        final NavGroup navGroup = navGroupList.get(groupPosition);
        gvh.groupIcon.setIconText(IconTextView.getIconCode(navGroup.ICON));
        gvh.groupName.setText(navGroup.Name);
        gvh.groupAlias.setText(navGroup.AliasName);
        gvh.groupAlias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateAliasActivity.class);
                intent.putExtra("nav_id", navGroup.ID);
                intent.putExtra("nav_alias", navGroup.AliasName);
                intent.putExtra("group_position", groupPosition);
                intent.putExtra("start_anim", false);
                ((Activity)mContext).startActivityForResult(intent, 100);
            }
        });
        gvh.switchButton.setOnCheckedChangeListener(null);
        gvh.switchButton.setChecked(navGroup.Visible == 1 ? true : false);
        gvh.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                API.TemplateAPI.UpdateNavVisible(navGroup.ID, isChecked ? 1 : 0, new RequestCallback<String>(String.class) {

                    @Override
                    public void onStarted() {
                        mLoadingDialog = DialogUtil.createLoadingDialog(mContext, R.string.loading_dialog_tips3);
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(mContext, isChecked ? "开启成功":"隐藏成功");
                            isUpdate = true;
                        } else {
                            ToastUtil.showToast(mContext, isChecked ? "开启失败":"隐藏失败");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        ToastUtil.showToast(mContext, isChecked ? "开启失败":"隐藏失败");
                    }
                });
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder cvh;
        if (convertView == null) {
            cvh = new ChildViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.listitem_navigation_child, parent, false);
            cvh.childIcon = (IconTextView) convertView.findViewById(R.id.tv_navigation_childicon);
            cvh.childName = (TextView) convertView.findViewById(R.id.tv_navigation_childname);
            cvh.childAlias = (TextView) convertView.findViewById(R.id.tv_navigation_childalias);
            cvh.switchButton = (SwitchButton) convertView.findViewById(R.id.sb_navigation_child);
            convertView.setTag(cvh);
        } else {
            cvh = (ChildViewHolder) convertView.getTag();
        }
        final ApiEntity.Navigation navChild = navGroupList.get(groupPosition).NavList.get(childPosition);
        cvh.childIcon.setIconText(IconTextView.getIconCode(navChild.ICON));
        cvh.childName.setText(navChild.Name);
        cvh.childAlias.setText(navChild.AliasName);
        cvh.childAlias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateAliasActivity.class);
                intent.putExtra("nav_id", navChild.ID);
                intent.putExtra("nav_alias", navChild.AliasName);
                intent.putExtra("group_position", groupPosition);
                intent.putExtra("child_position", childPosition);
                intent.putExtra("start_anim", false);
                ((Activity)mContext).startActivityForResult(intent, 100);
            }
        });
        cvh.switchButton.setOnCheckedChangeListener(null);
        cvh.switchButton.setChecked(navChild.Visible == 1 ? true : false);
        cvh.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                API.TemplateAPI.UpdateNavVisible(navChild.ID, isChecked ? 1 : 0, new RequestCallback<String>(String.class) {

                    @Override
                    public void onStarted() {
                        mLoadingDialog = DialogUtil.createLoadingDialog(mContext, R.string.loading_dialog_tips3);
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(mContext, isChecked ? "开启成功":"隐藏成功", R.drawable.tishi_ico_gougou);
                            isUpdate = true;
                        } else {
                            ToastUtil.showToast(mContext, isChecked ? "开启失败":"隐藏失败");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        ToastUtil.showToast(mContext, isChecked ? "开启失败":"隐藏失败");
                    }
                });
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupViewHolder {
        IconTextView groupIcon;
        TextView groupName;
        TextView groupAlias;
        SwitchButton switchButton;
    }

    class ChildViewHolder {
        IconTextView childIcon;
        TextView childName;
        TextView childAlias;
        SwitchButton switchButton;
    }
}
