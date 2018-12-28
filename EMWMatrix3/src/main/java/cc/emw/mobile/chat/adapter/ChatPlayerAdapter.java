package cc.emw.mobile.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatTeamPlayerViewPagerActivity;
import cc.emw.mobile.chat.model.bean.Files;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.chat.utils.VideoPlayer;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;


/**********
 * Created by sunny.du on 2016/10/25.
 * / * 设置多媒体信息适配器
 * /
 *****************************************************************************************************************************/
public class ChatPlayerAdapter extends BaseAdapter {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ArrayList<Message> playerList;
    private List<Files> mPlayerFiles;
    Context mContext;
    boolean flag = false;
    int widthPixels;
    int meanWidth;

    public ChatPlayerAdapter(Context context, ArrayList<Message> imagelist) {
        this.mContext = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.friends_sends_pictures_no) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.friends_sends_pictures_no) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.friends_sends_pictures_no) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
        widthPixels = DisplayUtil.getDisplayWidth(mContext);
        //定义DisplayMetrics 对象
        DisplayMetrics metric = new DisplayMetrics();
        //获得窗口属性
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        widthPixels = metric.widthPixels;
        meanWidth = widthPixels / 4;
        this.playerList = imagelist;
        if (playerList != null && playerList.size() > 0) {
            flag = true;
        }
    }

    public void setmPlayerFiles(List<Files> mPlayerFiles) {
        this.mPlayerFiles = mPlayerFiles;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_chat_team_player, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d("zrjtsss", "meanWidth------------->" + meanWidth);
        ViewGroup.LayoutParams params = holder.ivChatPlayerImage.getLayoutParams();
        params.width = meanWidth;
        params.height = DisplayUtil.dip2px(mContext, 100);
        holder.ivChatPlayerImage.setLayoutParams(params);
        ViewGroup.LayoutParams params2 = holder.vpvChatPlayer.getLayoutParams();
        params2.width = meanWidth;
        params2.height = DisplayUtil.dip2px(mContext, 100);
        holder.vpvChatPlayer.setLayoutParams(params2);
        if (flag && position < 4) {
            if (playerList.get(position).getType() == 7) {//过滤信息  设置图片
                try {
                    final Files image = new Gson().fromJson(playerList.get(position).getContent(), Files.class);
                    Log.d("zrjtssss", playerList.get(position).getContent());
                    holder.ivChatPlayerImage.setVisibility(View.VISIBLE);
                    holder.rlChatPlayer.setVisibility(View.GONE);
                    String imageUri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image.Url);
                    imageLoader.displayImage(imageUri, holder.ivChatPlayerImage, options);
                } catch (Exception e) {

                }
            } else if (playerList.get(position).getType() == 9) {
                final Files video = new Gson().fromJson(playerList.get(position).getContent(), Files.class);
                final Videos videos = new Videos();
                videos.setThumbFileName(video.getName());
                videos.setLength(video.getLength() + "");
                videos.setUrl(video.getUrl());
                holder.vpvChatPlayer.setTag(videos.getUrl());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        VideoPlayer tempPlayer = new VideoPlayer(mContext);
                        tempPlayer.setVideo(videos);
                        final Bitmap bitmap = tempPlayer.getFirstVideoFrame(true);
                        holder.vpvChatPlayer.post(new Runnable() {
                            @Override
                            public void run() {
                                if (videos.getUrl().equals(holder.vpvChatPlayer.getTag())) {
                                    if (bitmap != null) {
                                        holder.vpvChatPlayer.setImageBitmap(bitmap);
                                    }
                                }
                            }
                        });
                    }
                }).start();
                holder.ivChatPlayerImage.setVisibility(View.GONE);
                holder.rlChatPlayer.setVisibility(View.VISIBLE);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatTeamPlayerViewPagerActivity.class);
                intent.putExtra("videos", playerList);
                intent.putExtra("count", position);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        if (playerList.size() >= 4) {
            return 4;
        } else {
            return playerList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (playerList.size() != 0 && playerList != null) {
            if (position < 4) {
                return playerList.get(position);
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

    public class ViewHolder {
        public ImageView ivChatPlayerImage;
        public RelativeLayout rlChatPlayer;
        public ImageView vpvChatPlayer;
        public ImageView ivChatPlayerButton;

        public ViewHolder(View view) {
            ivChatPlayerImage = (ImageView) view.findViewById(R.id.iv_group_new_image_player);
            rlChatPlayer = (RelativeLayout) view.findViewById(R.id.rl_chat_team_player);
            vpvChatPlayer = (ImageView) view.findViewById(R.id.vpv_chat_team_player);
            ivChatPlayerButton = (ImageView) view.findViewById(R.id.iv_chat_team_player_but);
        }
    }
}
