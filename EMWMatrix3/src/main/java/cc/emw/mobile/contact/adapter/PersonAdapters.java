package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;

/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class PersonAdapters extends RecyclerView.Adapter<PersonAdapters.ViewHolder> {

    private Context mContext;
    private List<UserInfo> mDatas;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ListDialog mAddDialog;
    private List<Integer> onLineList;

    public PersonAdapters(Context mContext, List<UserInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
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

    private void initAddDialog(final UserInfo userInfo) {
        mAddDialog = new ListDialog(mContext, false);
        mAddDialog.addItem("语音通话", 1);
        mAddDialog.addItem("视频通话", 2);
        mAddDialog.addItem("拨打电话", 3);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case 1:
                        /*if (TextUtils.isEmpty(PrefsUtil.readUserInfo().VoipCode)) {
                            ToastUtil.showToast(mContext, "你暂未开通语音通话服务，请联系管理员申请开通。");
                            break;
                        }
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.Name) && !TextUtils.isEmpty(userInfo.VoipCode)) {
                            Intent intentVoice = new Intent(mContext, AudioConverseActivity.class);
                            intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intentVoice.putExtra("userName", userInfo.Name);
                            intentVoice.putExtra("userId", userInfo.VoipCode);
                            intentVoice.putExtra("call_phone", userInfo.Phone);
                            intentVoice.putExtra("call_type", 4);//1:免费电话 2:直拨 4:智能
                            intentVoice.putExtra("call_head", userInfo.Image);
                            mContext.startActivity(intentVoice);
                        } else {
                            ToastUtil.showToast(mContext, "对方暂未开通语音通话服务，请联系管理员申请开通。");
                        }*/
                        HelpUtil.startVoice(mContext, userInfo);
                        break;
                    case 2:
                        /*if (TextUtils.isEmpty(PrefsUtil.readUserInfo().VoipCode)) {
                            ToastUtil.showToast(mContext, "你暂未开通视频通话服务，请联系管理员申请开通。");
                            break;
                        }
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.Name) && !TextUtils.isEmpty(userInfo.VoipCode)) {
                            Intent intentVideo = new Intent(mContext, VideoConverseActivity.class);
                            intentVideo.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intentVideo.putExtra("userName", userInfo.Name);
                            intentVideo.putExtra("userId", userInfo.VoipCode);
                            intentVideo.putExtra("call_phone", userInfo.Phone);
                            intentVideo.putExtra("call_position", "");
                            mContext.startActivity(intentVideo);
                        } else {
                            ToastUtil.showToast(mContext, "对方暂未开通视频通话服务，请联系管理员申请开通。");
                        }*/
                        HelpUtil.startVideo(mContext, userInfo);
                        break;
                    case 3:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userInfo.Phone.toString()));
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
    }

    public void setOnLineList(List<Integer> onLineList) {
        this.onLineList = onLineList;
    }

    public ImageLoader getImageLoader() {
        return this.imageLoader;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.listitem_contact_child, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserInfo user = mDatas.get(position);
        if (!user.isFromPhone) {
            holder.nameTv.setText(user.Name);
            holder.nameTv.setTextColor(user.ID == PrefsUtil.readUserInfo().ID ? Color.parseColor("#85b2f6") : Color.parseColor("#101010"));
            holder.headIv.setTvBg(EMWApplication.getIconColor(user.ID), user.Name, 40);
            String uri = String.format(Const.DOWN_ICON_URL,
                    TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);
            imageLoader.displayImage(uri, new ImageViewAware(holder.headIv), options,
                    new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
//            Picasso.with(mContext)
//                    .load(uri)
//                    .resize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40))
//                    .centerCrop()
//                    .config(Bitmap.Config.ALPHA_8)
////                    .placeholder(R.drawable.cm_img_head)
////                    .error(R.drawable.cm_img_head)
//                    .into(holder.headIv);

            if (onLineList != null && onLineList.contains(user.ID))
                holder.isOnline.setBackgroundResource(R.drawable.circle_is_online);
            else
                holder.isOnline.setBackgroundResource(R.drawable.circle_is_not_online);

            holder.iconTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initAddDialog(user);
                    mAddDialog.show();
                }
            });
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
            holder.nameTv.setText(user.Name);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView headIv;
        View isOnline;
        TextView nameTv;
        IconTextView iconTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            headIv = (CircleImageView) itemView
                    .findViewById(R.id.iv_contactselect_icon);
            nameTv = (TextView) itemView
                    .findViewById(R.id.tv_contactselect_name);
            isOnline = itemView.findViewById(R.id.view_contact_is_online);
            iconTextView = (IconTextView) itemView.findViewById(R.id.itv_contact_item);
        }
    }

}
