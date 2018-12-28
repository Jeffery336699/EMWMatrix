package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import cc.emw.mobile.contact.base.BaseRecyclerViewAdapter;
import cc.emw.mobile.contact.base.RecyclerViewData;
import cc.emw.mobile.contact.holder.PersonalUserViewHolder;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.ListDialog;

/**
 * Created by tao.zhou on 2017/9/12.
 */

public class PersonalUserAdapter extends BaseRecyclerViewAdapter<String, UserInfo, PersonalUserViewHolder> {

    private Context ctx;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ListDialog mAddDialog;

    public PersonalUserAdapter(Context ctx, List<RecyclerViewData> datas) {
        super(ctx, datas);
        mInflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
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
        mAddDialog = new ListDialog(ctx, false);
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
                        HelpUtil.startVoice(ctx, userInfo);
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
                        HelpUtil.startVideo(ctx, userInfo);
                        break;
                    case 3:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userInfo.Phone.toString()));
                        ctx.startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    public void onBindGroupHolder(PersonalUserViewHolder holder, int groupPos, int position, String groupData) {
        holder.tvTitle.setText(groupData);
    }

    @Override
    public void onBindChildpHolder(PersonalUserViewHolder holder, int groupPos, int childPos, int position, final UserInfo user) {
            holder.nameTv.setText(user.Name);
            holder.nameTv.setTextColor(user.ID == PrefsUtil.readUserInfo().ID ? Color.parseColor("#85b2f6") : Color.parseColor("#101010"));
            holder.headIv.setTvBg(EMWApplication.getIconColor(user.ID), user.Name, 40);
            String uri = String.format(Const.DOWN_ICON_URL,
                    TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);
            imageLoader.getInstance().displayImage(uri, new ImageViewAware(holder.headIv), options,
                    new ImageSize(DisplayUtil.dip2px(ctx, 40), DisplayUtil.dip2px(ctx, 40)), null, null);
//            Picasso.with(mContext)
//                    .load(uri)
//                    .resize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40))
//                    .centerCrop()
//                    .config(Bitmap.Config.ALPHA_8)
////                    .placeholder(R.drawable.cm_img_head)
////                    .error(R.drawable.cm_img_head)
//                    .into(holder.headIv);

//            if (onLineList != null && onLineList.contains(user.ID))
//                holder.isOnline.setBackgroundResource(R.drawable.circle_is_online);
//            else
//                holder.isOnline.setBackgroundResource(R.drawable.circle_is_not_online);

            holder.iconTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initAddDialog(user);
                    mAddDialog.show();
                }
            });
//            holder.itemView.setOnClickListener(new NoDoubleClickListener() {
//                @Override
//                public void onNoDoubleClick(View v) {
//                    Intent intent = new Intent(ctx, PersonInfoActivity.class);
//                    intent.putExtra("UserInfo", user);
//                    intent.putExtra("intoTag", 1);
//                    intent.putExtra("start_anim", false);
//                    int[] location = new int[2];
//                    v.getLocationInWindow(location);
//                    intent.putExtra("click_pos_y", location[1]);
//                    ctx.startActivity(intent);
//                }
//            });
    }

    @Override
    public View getGroupView(ViewGroup parent) {
        return mInflater.inflate(R.layout.personal_title_item_layout, parent, false);
    }

    @Override
    public View getChildView(ViewGroup parent) {
        return mInflater.inflate(R.layout.personal_item_layout, parent, false);
    }

    @Override
    public PersonalUserViewHolder createRealViewHolder(Context ctx, View view, int viewType) {
        return new PersonalUserViewHolder(ctx, view, viewType);
    }

    /**
     * true 全部可展开
     * fasle  同一时间只能展开一个
     */
    @Override
    public boolean canExpandAll() {
        return true;
    }
}
