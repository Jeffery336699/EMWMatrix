package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsunset.cim.client.model.Message;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.model.bean.Files;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.chat.factory.ImageLoadFactory;
import cc.emw.mobile.chat.utils.VideoPlayer;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.VideoPlayerView;

/**
 * Created by sunny.du on 2016/10/20.
 * 多媒体播放适配器
 */
public class ChatTeamPagerAdapter extends PagerAdapter {
    List<Files> mPalyers;
    List<View> viewList;
    List<Message> msgList;
    VideoPlayer mPlayer;
    Context mContext;

    public ChatTeamPagerAdapter(Context context, List<View> list, List<Files> palyerlist, List<Message> msgList) {
        this.mContext = context;
        this.viewList = list;
        this.mPalyers = palyerlist;
        this.msgList = msgList;
        mPlayer = new VideoPlayer(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        int displayHeight = DisplayUtil.getDisplayHeight(mContext);
        final ImageView ivChatPlayerImage = (ImageView) viewList.get(position).findViewById(R.id.iv_chat_player_image);
        final RelativeLayout rlChatPlayerVioce = (RelativeLayout) viewList.get(position).findViewById(R.id.rl_chat_player_voice);
        final VideoPlayerView vpvChatPlayerVideo = (VideoPlayerView) viewList.get(position).findViewById(R.id.video_player_view);
        final ProgressBar pbChatLoad = (ProgressBar) viewList.get(position).findViewById(R.id.pb_chat_palyer);
        final ImageView ivChatPlay = (ImageView) viewList.get(position).findViewById(R.id.chat_iv_play);
        final ImageView mIvItemVideoImg = (ImageView) viewList.get(position).findViewById(R.id.iv_item_video_img);
        TextView tvChatNumPage = (TextView) viewList.get(position).findViewById(R.id.tv_num_page);
        tvChatNumPage.setText(position + 1 + "/" + viewList.size());
        if (msgList.get(position).getType() == 7) {
            ivChatPlayerImage.setVisibility(View.VISIBLE);
            rlChatPlayerVioce.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = ivChatPlayerImage.getLayoutParams();
            layoutParams.height=displayHeight/2;
            ivChatPlayerImage.setLayoutParams(layoutParams);
            String imageuri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, mPalyers.get(position).Url);
            DisplayImageOptions optiones = ImageLoadFactory.getChatPageOptiones();//图片load
            ImageLoader.getInstance().displayImage(imageuri, ivChatPlayerImage, optiones);
        } else if (msgList.get(position).getType() == 9) {
            ivChatPlayerImage.setVisibility(View.GONE);
            rlChatPlayerVioce.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = rlChatPlayerVioce.getLayoutParams();
            layoutParams.height=displayHeight/2;
            rlChatPlayerVioce.setLayoutParams(layoutParams);
            /****************设置视频封面*************************************/
            final Files video = mPalyers.get(position);
            final Videos videos = new Videos();
            videos.setThumbFileName(video.getName());
            videos.setLength(video.getLength() + "");
            videos.setUrl(video.getUrl());
            mIvItemVideoImg.setTag(videos.getUrl());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    VideoPlayer tempPlayer = new VideoPlayer(mContext);
                    tempPlayer.setVideo(videos);
                    final Bitmap bitmap = tempPlayer.getFirstVideoFrame(true);
                    mIvItemVideoImg.post(new Runnable() {
                        @Override
                        public void run() {
                            if (videos.getUrl().equals(mIvItemVideoImg.getTag())) {
                                if (bitmap != null) {
                                    mIvItemVideoImg.setImageBitmap(bitmap);
                                }
                            }
                        }
                    });
                }
            }).start();
            /**
             * 视频RL点击监听
             */
            rlChatPlayerVioce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIvItemVideoImg.setVisibility(View.GONE);
                    pbChatLoad.setVisibility(View.VISIBLE);
                    ivChatPlay.setVisibility(View.GONE);
                    videos.setUrl(mPalyers.get(position).Url);
                    videos.setLength(mPalyers.get(position).Length + "");
                    mPlayer.setSurfaceRatio(9f / 11);
                    mPlayer.setVideo(videos);
                    mPlayer.init(EMWApplication.videoPath, vpvChatPlayerVideo, rlChatPlayerVioce, pbChatLoad, true);
                    mPlayer.downLoadVideo();
                    mPlayer.setOnClose(new VideoPlayer.OnClose() {
                        @Override
                        public void showBut() {
                            ivChatPlay.setVisibility(View.VISIBLE);
                            mPlayer.stopVideo();
                        }
                    });
                }
            });
        }

        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
