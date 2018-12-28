package cc.emw.mobile.me.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * @author zrjt
 * @version 2016-3-11 下午7:26:01
 */
public class ConcernAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserInfo> mDataList;
    private DisplayImageOptions options;

    public ConcernAdapter(Context context) {
        this.mContext = context;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setData(List<UserInfo> dataList) {
        this.mDataList = dataList;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.listitem_concern, null);
            vh.headIv = (CircleImageView) convertView
                    .findViewById(R.id.iv_concern_head);
            vh.nameTv = (TextView) convertView
                    .findViewById(R.id.tv_concern_name);
//            AnimationSet set = anmi();
//            vh.set = set;
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) convertView.getTag(R.id.tag_first);
        }
        final UserInfo user = mDataList.get(position);
        vh.nameTv.setText(user.Name);
        final String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil
                .readUserInfo().CompanyCode, user.Image);
        ImageLoader.getInstance().displayImage(uri, vh.headIv, options);

        convertView.setTag(R.id.tag_second, user);
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonInfoActivity.class);
                intent.putExtra("UserInfo", (UserInfo) v.getTag(R.id.tag_second));
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationInWindow(location);
                intent.putExtra("click_pos_y", location[1]);
                ((Activity) mContext).startActivityForResult(intent, 10);
            }
        });
//        convertView.startAnimation(vh.set);
        return convertView;
    }

    static class ViewHolder {
        CircleImageView headIv;
        TextView nameTv;
        AnimationSet set;
    }

    private AnimationSet anmi() {
        // 动画集合
        AnimationSet set = new AnimationSet(false);
        // 缩放动画
        ScaleAnimation scale = new ScaleAnimation(0.5f, 1, 0.5f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(800);// 动画时间
        scale.setFillAfter(true);// 保持动画状态
        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0.6f, 1);
        alpha.setDuration(800);// 动画时间
        alpha.setFillAfter(true);// 保持动画状态
        set.addAnimation(scale);
        set.addAnimation(alpha);
        return set;
    }

}
