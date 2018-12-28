package cc.emw.mobile.contact.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zf.iosdialog.widget.AlertDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.GroupInActivity;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;

public class GroupEditAdapter extends BaseAdapter {

    DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
    private Context mContext;
    private List<UserInfo> mDataList;
    private Dialog mLoadingDialog; // 加载框
    private boolean isEdit;
    private GroupInfo mGroupInfo;
    private Dialog dialog;
    private boolean flag;

    public GroupEditAdapter(Context context, Dialog loadingDialog) {
        mContext = context;
        mLoadingDialog = loadingDialog;
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public int getMemberCount() {
        return mDataList.size();
    }

    public void setData(List<UserInfo> dataList) {
        mDataList = dataList;
    }

    public void setEditEnabled(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        mGroupInfo = groupInfo;
    }

    @Override
    public int getCount() {

        if (mDataList == null)
            return 0;
        else if (mDataList.size() + 1 > 50)
            return 50;
        else
            return mDataList.size() + 1;

        // return mDataList != null ? mDataList.size() + 1 : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_group_edit_grid, null, false);
            vh.headIv = (CircleImageView) convertView
                    .findViewById(R.id.group_create_iv_head);
            vh.nameTv = (TextView) convertView
                    .findViewById(R.id.group_create_tv_name);
            vh.delTagImg = (ImageView) convertView
                    .findViewById(R.id.ico_del_red);
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (position == getCount() - 1) {
            vh.delTagImg.setVisibility(View.GONE);
            if (isEdit) {
                convertView.setVisibility(View.VISIBLE);
                vh.headIv.setImageResource(R.drawable.btn_add_people);
                vh.nameTv.setText("编辑");
                if (mGroupInfo.CreateUser == PrefsUtil.readUserInfo().ID)
                    convertView.setVisibility(View.VISIBLE);
                else
                    convertView.setVisibility(View.GONE);
            } else {
                convertView.setVisibility(View.INVISIBLE);
            }
        } else if (position < getCount() - 1) {
            final UserInfo role = mDataList.get(position);
            vh.headIv.setTextBg(EMWApplication.getIconColor(role.ID), role.Name, 40);
            String uri = String.format(Const.DOWN_ICON_URL,
                    TextUtils.isEmpty(role.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : role.CompanyCode, role.Image);
            ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
            vh.nameTv.setText(role.Name);
            if (isEdit) {
                if (role.ID != PrefsUtil.readUserInfo().ID) {
                    vh.delTagImg.setVisibility(View.VISIBLE);
                    flag = true;
                    convertView.setTag(R.id.tag_first, flag);
                } else {
                    vh.delTagImg.setVisibility(View.GONE);
                    flag = false;
                    convertView.setTag(R.id.tag_first, flag);
                }
            } else {
                vh.delTagImg.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        CircleImageView headIv;
        TextView nameTv;
        ImageView delTagImg;
    }

    private void delGroupRoles(int gid, int userid, final int postion) {
        API.TalkerAPI.DelGroupUser(gid, userid, new RequestCallback<String>(
                String.class) {

            @Override
            public void onStarted() {
                dialog = DialogUtil.createLoadingDialog(mContext, "正在处理...");
                dialog.show();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                dialog.dismiss();
                ToastUtil.showToast(mContext, "服务器异常");
            }

            @Override
            public void onSuccess(String arg0) {
                dialog.dismiss();
                if (Integer.valueOf(arg0) == 0) {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                } else if (Integer.valueOf(arg0) == 1) {
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    mDataList.remove(postion);
                    Intent intent = new Intent();
                    intent.setAction(GroupInActivity.ACTION_REFRESH_COUNT_GROUP);
                    mContext.sendBroadcast(intent);
                    notifyDataSetChanged();
                }
            }
        });
    }

}
