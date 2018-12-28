package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.AddFriendGroupSelectActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * Created by tao.zhou on 2017/6/24.
 */

public class PersonFriendAdapter extends BaseAdapter {

    private Context mContext;

    private List<UserInfo> mDataList;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public PersonFriendAdapter(Context mContext) {
        this.mContext = mContext;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setMDataList(List<UserInfo> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_add_friend_search, null);
            vh.mSearchUserImg = (CircleImageView) convertView.findViewById(R.id.civ_add_friend_search);
            vh.mSearchName = (TextView) convertView.findViewById(R.id.tv_add_friend_name);
            vh.mSearchSameNum = (TextView) convertView.findViewById(R.id.tv_same_friend_num);
            vh.mTvAdd = (TextView) convertView.findViewById(R.id.tv_add_friend);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final UserInfo user = mDataList.get(position);

        vh.mSearchUserImg.setTvBg(EMWApplication.getIconColor(user.ID), user.Name, 40);

        String uri = String.format(Const.DOWN_ICON_URL,
                TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);

        imageLoader.displayImage(uri, new ImageViewAware(vh.mSearchUserImg), options,
                new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);

//        Picasso.with(mContext)
//                .load(uri)
//                .resize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40))
//                .centerCrop()
//                .config(Bitmap.Config.ALPHA_8)
//                .placeholder(R.drawable.cm_img_head)
//                .error(R.drawable.cm_img_head)
//                .into(vh.mSearchUserImg);

        vh.mSearchName.setText(user.Name);

        if (user.IsFollow) {
            vh.mTvAdd.setText("已添加");
            vh.mTvAdd.setTextColor(Color.parseColor("#989898"));
        } else {
            vh.mTvAdd.setText("添加");
            vh.mTvAdd.setTextColor(Color.parseColor("#1DA1F3"));
        }

        vh.mSearchSameNum.setText(user.SameUserList != null && user.SameUserList.size() > 0 ? "共同好友:" + user.SameUserList.size() + "人" : "共同好友: 无");

        vh.mTvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addFriend(user.ID);
                Intent intent = new Intent(mContext, AddFriendGroupSelectActivity.class);
                intent.putExtra(AddFriendGroupSelectActivity.USER_ID, user.ID);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        CircleImageView mSearchUserImg;
        TextView mSearchName, mSearchSameNum, mTvAdd;
    }

    /**
     * 添加好友
     */
    private void addFriend(int conID) {
        API.UserPubAPI.AddPubConApply(conID, new RequestCallback<String>(String.class) {

            @Override
            public void onSuccess(String result) {
                if (result != null && Integer.valueOf(result) > 0) {
                    Toast.makeText(mContext, "好友申请已发出,请耐心等待", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
