package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brucetoo.imagebrowse.widget.PhotoView;
import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatTeamPlayerViewPagerActivity;
import cc.emw.mobile.chat.model.bean.Files;
import cc.emw.mobile.chat.factory.ImageLoadFactory;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.VideoPlayerView;

/**
 * Created by sunny.du on 2016/10/20.
 * 设置多媒体信息适配器
 */
public class ChatTeamPlayerGrideAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Message> imagelist;
    boolean flag = false;
    int widthPixels;
    int meanWidth = widthPixels / 4;

    public ChatTeamPlayerGrideAdapter(Context context, ArrayList<Message> imagelist) {
        this.mContext = context;
        this.imagelist = imagelist;
        widthPixels = DisplayUtil.getDisplayWidth(mContext);
        if (imagelist != null && imagelist.size() != 0) {
            flag = true;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_chat_team_player, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ViewGroup.LayoutParams params = holder.ivChatPlayerImage.getLayoutParams();
        params.width = meanWidth;
        holder.ivChatPlayerImage.setLayoutParams(params);
        ViewGroup.LayoutParams params2 = holder.vpvChatPlayer.getLayoutParams();
        params2.width = meanWidth;
        holder.vpvChatPlayer.setLayoutParams(params2);
        if (flag && position < 4) {
            if (imagelist.get(position).getType() == 7) {//过滤信息  设置图片
                final Files image = new Gson().fromJson(imagelist.get(position).getContent(), Files.class);
                holder.ivChatPlayerImage.setVisibility(View.VISIBLE);
                holder.rlChatPlayer.setVisibility(View.GONE);
                String imageuri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image.Url);
                DisplayImageOptions optiones = ImageLoadFactory.getChatPlayerOption();//图片load
                ImageLoader.getInstance().displayImage(imageuri, holder.ivChatPlayerImage, optiones);
                holder.ivChatPlayerImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ChatTeamPlayerViewPagerActivity.class);
                        intent.putExtra("videos", imagelist);
                        intent.putExtra("count", position);
                        mContext.startActivity(intent);
                    }
                });
            } else if (imagelist.get(position).getType() == 9) {
                final Files video = new Gson().fromJson(imagelist.get(position).getContent(), Files.class);
                holder.ivChatPlayerImage.setVisibility(View.GONE);
                holder.rlChatPlayer.setVisibility(View.VISIBLE);
                //跳转视频播放页面
                holder.rlChatPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ChatTeamPlayerViewPagerActivity.class);
                        intent.putExtra("videos", imagelist);
                        intent.putExtra("count", position);
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        if (imagelist.size() != 0 && imagelist != null) {
            if (position < 4) {
                return imagelist.get(position);
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (position < 4) {
            return position;
        }
        return 0;
    }

    private class ViewHolder {
        public PhotoView ivChatPlayerImage;
        public RelativeLayout rlChatPlayer;
        public VideoPlayerView vpvChatPlayer;
        public ImageView ivChatPlayerButton;

        public ViewHolder(View view) {
            ivChatPlayerImage = (PhotoView) view.findViewById(R.id.iv_group_new_image);
            rlChatPlayer = (RelativeLayout) view.findViewById(R.id.rl_chat_team_player);
            vpvChatPlayer = (VideoPlayerView) view.findViewById(R.id.vpv_chat_team_player);
            ivChatPlayerButton = (ImageView) view.findViewById(R.id.iv_chat_team_player_but);
        }
    }
}
