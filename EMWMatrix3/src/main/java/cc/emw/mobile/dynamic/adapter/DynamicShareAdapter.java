package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.view.CircleImageView;

/**
 * 滑动面板分享
 */
public class DynamicShareAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserNote> mDataList;
    private DisplayImageOptions options;

    public DynamicShareAdapter(Context context, List<UserNote> dataList) {
        this.mContext = context;
        this.mDataList = new ArrayList<>();
        this.mDataList.addAll(dataList);

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

    public void setData(ArrayList<UserNote> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).ID;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup parent) {
        ViewHolder vh;
        if (contentView == null) {
            vh = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamic_share, null);
            vh.headIv = (CircleImageView) contentView.findViewById(R.id.iv_dynamicshare_head);
            vh.nameTv = (TextView) contentView.findViewById(R.id.tv_dynamicshare_name);
            vh.timeTv = (TextView) contentView.findViewById(R.id.tv_dynamicshare_time);
            vh.contentTv = (TextView) contentView.findViewById(R.id.tv_dynamicshare_content);
            contentView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) contentView.getTag(R.id.tag_first);
        }

        final UserNote un = mDataList.get(position);
        if (un.UserIdInfo != null) {
            String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(un.UserIdInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : un.UserIdInfo.CompanyCode, un.UserIdInfo.Image);
            if (un.UserIdInfo.Image != null && un.UserIdInfo.Image.startsWith("/")) {
                uri = Const.DOWN_ICON_URL2 + un.UserIdInfo.Image;
            }
            vh.headIv.setTextBg(EMWApplication.getIconColor(un.UserIdInfo.ID), un.UserIdInfo.Name, 30);
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
            vh.nameTv.setText(un.UserIdInfo.Name);
        } else {
            vh.headIv.setTextBg(EMWApplication.getIconColor(un.UserID), "", 30);
            vh.nameTv.setText("");
        }

        vh.headIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EMWApplication.personMap != null && EMWApplication.personMap.get(un.UserID) != null) {
                    Intent intent = new Intent(mContext, PersonInfoActivity.class);
                    intent.putExtra("intoTag", 1);
                    intent.putExtra("UserInfo", EMWApplication.personMap.get(un.UserID));
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            }
        });
        String createTime = un.CreateTime;
        vh.timeTv.setText(StringUtils.friendly_time(createTime));
        String content = "";
        if (!TextUtils.isEmpty(un.Content)) {
            content = Html.fromHtml(un.Content).toString();
        }
        vh.contentTv.setText(content);

        return contentView;
    }

    class ViewHolder {
        CircleImageView headIv;
        TextView nameTv;
        TextView timeTv;
        TextView contentTv;

    }

}
