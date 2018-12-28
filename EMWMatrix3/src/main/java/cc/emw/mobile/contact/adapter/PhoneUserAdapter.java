package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.contact.AddFriendGroupSelectActivity;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class PhoneUserAdapter extends RecyclerView.Adapter<PhoneUserAdapter.ViewHolder> {

    private Context mContext;
    private List<UserInfo> mDatas;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public PhoneUserAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setMData(List<UserInfo> mDatas) {
        this.mDatas = mDatas;
    }

    public ImageLoader getImageLoader() {
        return this.imageLoader;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.listitem_contact_user, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (EMWApplication.personMap != null && EMWApplication.personMap.get(mDatas.get(position).ID) != null) {

            final UserInfo user = EMWApplication.personMap.get(mDatas.get(position).ID);

            String uri = String.format(Const.DOWN_ICON_URL,
                    TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);

            Picasso.with(mContext)
                    .load(uri)
                    .resize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40))
                    .centerCrop()
                    .config(Bitmap.Config.ALPHA_8)
                    .placeholder(R.drawable.cm_img_head)
                    .error(R.drawable.cm_img_head)
                    .into(holder.headIv);

            holder.nameTv.setText(user.Name);

            if (user.IsFollow) {
                holder.iconTextView.setText("已添加");
                holder.iconTextView.setTextColor(Color.parseColor("#989898"));
            } else {
                holder.iconTextView.setText("添加");
                holder.iconTextView.setTextColor(Color.parseColor("#1DA1F3"));
            }

            holder.itemView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent intent = new Intent(mContext, PersonInfoActivity.class);
                    intent.putExtra("UserInfo", user);
                    intent.putExtra("intoTag", 1);
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.nameTv.setText(mDatas.get(position).Name);
        }

        holder.iconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addFriend(mDatas.get(position).ID);
                Intent intent = new Intent(mContext, AddFriendGroupSelectActivity.class);
                intent.putExtra(AddFriendGroupSelectActivity.USER_ID, mDatas.get(position).ID);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView headIv;
        View isOnline;
        TextView nameTv;
        TextView iconTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            headIv = (CircleImageView) itemView
                    .findViewById(R.id.iv_contactselect_icon);
            nameTv = (TextView) itemView
                    .findViewById(R.id.tv_contactselect_name);
            isOnline = itemView.findViewById(R.id.view_contact_is_online);
            iconTextView = (TextView) itemView.findViewById(R.id.itv_contact_item);
        }
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
