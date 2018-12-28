package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;

/**
 * Created by ${zrjt} on 2016/7/27.
 */
public class ConcernAdapter extends BaseAdapter {

    private Context context;
    private List<UserInfo> mDataList;
    private DisplayImageOptions options;
    private List<Integer> onLineList;
    private ListDialog mAddDialog;

    private void initAddDialog(final UserInfo userInfo) {
        mAddDialog = new ListDialog(context, false);
        mAddDialog.addItem("语音通话", 1);
        mAddDialog.addItem("视频通话", 2);
        mAddDialog.addItem("拨打电话", 3);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case 1:
                        /*Intent intentVoice = new Intent(context, AudioConverseActivity.class);
                        intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intentVoice.putExtra("userName", userInfo.Name);
                        intentVoice.putExtra("userId", userInfo.VoipCode);
                        intentVoice.putExtra("call_phone", userInfo.Phone);
                        intentVoice.putExtra("call_type", 4);//1:免费电话 2:直拨 4:智能
                        intentVoice.putExtra("call_head", userInfo.Image);
                        context.startActivity(intentVoice);*/
                        HelpUtil.startVoice(context, userInfo);
                        break;
                    case 2:
                        /*Intent intentVideo = new Intent(context, VideoConverseActivity.class);
                        intentVideo.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intentVideo.putExtra("userName", userInfo.Name);
                        intentVideo.putExtra("userId", userInfo.VoipCode);
                        intentVideo.putExtra("call_phone", userInfo.Phone);
                        intentVideo.putExtra("call_position", "");
                        context.startActivity(intentVideo);*/
                        HelpUtil.startVideo(context, userInfo);
                        break;
                    case 3:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userInfo.Phone.toString()));
                        context.startActivity(intent);
                        break;
                }
            }
        });
    }

    public ConcernAdapter(Context context) {
        this.context = context;
        this.mDataList = new ArrayList<UserInfo>();
        this.onLineList = new ArrayList<>();
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(388))
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setOnLineList(List<Integer> onLineList) {
        this.onLineList = onLineList;
    }

    public void setDataList(ArrayList<UserInfo> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
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
        CViewHolder vh;
        if (convertView == null) {
            vh = new CViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.listitem_contact_child, null);
            vh.headIv = (CircleImageView) convertView
                    .findViewById(R.id.iv_contactselect_icon);
            vh.nameTv = (TextView) convertView
                    .findViewById(R.id.tv_contactselect_name);
            vh.iconTextView = (IconTextView) convertView.findViewById(R.id.itv_contact_item);
            vh.isOnline = convertView.findViewById(R.id.view_contact_is_online);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (CViewHolder) convertView.getTag(R.id.tag_first);
        }
        final UserInfo user = mDataList.get(position);
        vh.headIv.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 40);
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode, user.Image);
        if (onLineList.contains(user.ID))
            vh.isOnline.setBackgroundResource(R.drawable.circle_is_online);
        else
            vh.isOnline.setBackgroundResource(R.drawable.circle_is_not_online);
        ImageLoader.getInstance().displayImage(uri, vh.headIv, options);

        vh.iconTextView.setTag(user);
        vh.iconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo u = (UserInfo) v.getTag();
                initAddDialog(u);
                if (!TextUtils.isEmpty(u.Phone.toString()) && TextUtils.isDigitsOnly(u.Phone.toString())) {
                    if (u != null && !TextUtils.isEmpty(u.Name) && !TextUtils.isEmpty(u.RongYunToken)) {
                        mAddDialog.show();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + u.Phone.toString()));
                        context.startActivity(intent);
                    }
                } else {
                    ToastUtil.showToast(context, "号码为空");
                }
            }
        });

        vh.nameTv.setText(user.Name);
        convertView.setTag(R.id.tag_second, user);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonInfoActivity.class);
                intent.putExtra("UserInfo", user);
                intent.putExtra("intoTag", 1);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationInWindow(location);
                intent.putExtra("click_pos_y", location[1]);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    public class CViewHolder {
        CircleImageView headIv;
        TextView nameTv;
        IconTextView iconTextView;
        View isOnline;
    }
}
