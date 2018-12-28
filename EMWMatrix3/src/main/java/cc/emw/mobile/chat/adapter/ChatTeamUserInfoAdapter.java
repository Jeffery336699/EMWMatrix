package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.factory.ImageLoadFactory;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * Created by sunny.du on 2016/10/20.
 * 设置人员列表的适配器
 */
public class ChatTeamUserInfoAdapter extends BaseAdapter {
    private Context mContext;
    DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
    List<UserInfo> mUserInfos;

    public ChatTeamUserInfoAdapter(Context context) {
        this.mContext = context;
        options = ImageLoadFactory.getChatTeamUserImageOption();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        final UserInfo userInfo = mUserInfos.get(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_chat_team_userinfo, null);
            myHolder = new MyHolder(convertView);
            convertView.setTag(myHolder);
        } else {
            myHolder = (MyHolder) convertView.getTag();
        }
        myHolder.mCivChatUserImage.setTvBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 40);
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, userInfo.Image);
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(myHolder.mCivChatUserImage), options,
                new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
        myHolder.mTvChatTeamUserName.setText(userInfo.Name);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo != null) {
                    Intent intent = new Intent(mContext, PersonInfoActivity.class);
                    intent.putExtra("UserInfo", userInfo);
                    intent.putExtra("intoTag", 1);
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        if (null != mUserInfos) {
            if (mUserInfos.size() != 0) {
                return mUserInfos.size();
            }
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mUserInfos.size() != 0 && mUserInfos != null) {
            return mUserInfos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<UserInfo> data) {
        this.mUserInfos = data;
    }

    private class MyHolder {
        public CircleImageView mCivChatUserImage;
        public TextView mTvChatTeamUserName;
        private View mView;

        public MyHolder(View view) {
            this.mView = view;
            init();
        }

        private void init() {
            mCivChatUserImage = (CircleImageView) mView.findViewById(R.id.civ_chat_user_image);
            mTvChatTeamUserName = (TextView) mView.findViewById(R.id.tv_chat_team_user_name);
        }
    }

}
