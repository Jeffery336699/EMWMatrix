package cc.emw.mobile.contact.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by ${zrjt} on 2016/6/27.
 */
public class PersonAllGroupAdapter extends BaseAdapter {

    private Context mContext;

    private List<UserInfo> userInfos;

    private DisplayImageOptions options;

    public PersonAllGroupAdapter(Context mContext) {

        this.mContext = mContext;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setData(List<UserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    @Override
    public int getCount() {
        return userInfos == null ? 0 : userInfos.size();
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_list_item_group_member, parent, false);

            holder.mImg = (CircleImageView) convertView.findViewById(R.id.iv_group_member);

            holder.mName = (TextView) convertView.findViewById(R.id.tv_group_member_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UserInfo user = userInfos.get(position);

        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode,
                user.Image);
        ImageLoader.getInstance().displayImage(uri, holder.mImg, options);

        holder.mName.setText(user.Name);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonInfoActivity.class);
                intent.putExtra("UserInfo", user);
                intent.putExtra("intoTag", 1);
                intent.putExtra("start_anim", false);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView mName;
        CircleImageView mImg;
    }
}
