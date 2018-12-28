package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.factory.ImageLoadFactory;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;


/**
 * Created by sunny.du on 2017/7/18.
 */
public class ChatGroupSelectAdapter extends RecyclerView.Adapter<ChatGroupSelectAdapter.MyHolder> {
    private List<UserInfo> userList;
    private Context mContext;
    private DisplayImageOptions optiones;//头像

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        UserInfo userInfo = userList.get(position);
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, userInfo.Image);
        holder.mCivChatGroupSelectItemHead.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
        ImageLoader.getInstance().displayImage(uri, holder.mCivChatGroupSelectItemHead, optiones);
        holder.mTvChatGroupSelectItemName.setText(userInfo.Name);
        holder.mRlChatGroupSelectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.itemClick(holder.mTvChatGroupSelectItemName);
                }
            }
        });
    }

    private OnItemCilckListener listener;

    public void setOnItemCilckListener(OnItemCilckListener listener) {
        this.listener = listener;
    }

    public interface OnItemCilckListener {
        void itemClick(TextView tv);
    }

    class MyHolder extends RecyclerView.ViewHolder {
        View view;
        CircleImageView mCivChatGroupSelectItemHead;
        TextView mTvChatGroupSelectItemName;
        RelativeLayout mRlChatGroupSelectItem;

        public MyHolder(View itemView) {
            super(itemView);
            view = itemView;
            mCivChatGroupSelectItemHead = (CircleImageView) itemView.findViewById(R.id.civ_chat_group_select_item_head);
            mTvChatGroupSelectItemName = (TextView) itemView.findViewById(R.id.tv_chat_group_select_item_name);
            mRlChatGroupSelectItem= (RelativeLayout) itemView.findViewById(R.id.rl_chat_group_select_item);
        }
    }

    public void setData(List<UserInfo> userLists) {
        this.userList = userLists;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_group_select_user, parent, false));
    }

    public ChatGroupSelectAdapter(Context context) {
        this.mContext = context;
        optiones = ImageLoadFactory.getChatOptiones();//图片load
    }

    @Override
    public int getItemCount() {
        if (userList != null) {
            return userList.size();
        } else {
            return 0;
        }
    }

}
