package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 *
 */
public class DynamicMemberAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ApiEntity.UserInfo> mDataList;
    private DisplayImageOptions options;

    public DynamicMemberAdapter(Context context, List<ApiEntity.UserInfo> dataList) {
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

    public void setData(ArrayList<ApiEntity.UserInfo> dataList) {
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
    public View getView(final int position, View contentView, ViewGroup parent) {
        ViewHolder vh;
        if (contentView == null) {
            vh = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamic_member, null);
            vh.headIv = (CircleImageView) contentView.findViewById(R.id.iv_dynamicmember_head);
            vh.nameTv = (TextView) contentView.findViewById(R.id.tv_dynamicmember_name);
            contentView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) contentView.getTag(R.id.tag_first);
        }

        final ApiEntity.UserInfo userInfo = mDataList.get(position);
        if (userInfo != null) {
            String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
            if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                uri = Const.DOWN_ICON_URL2 + userInfo.Image;
            }
            vh.headIv.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 24);
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
            vh.nameTv.setText(userInfo.Name);
        } else {
            vh.headIv.setTextBg(0, "", 24);
            vh.nameTv.setText("");
        }

        return contentView;
    }

    class ViewHolder {
        CircleImageView headIv;
        TextView nameTv;
    }

}
