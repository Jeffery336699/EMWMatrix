package cc.emw.mobile.chat;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.ChatTeamPagerAdapter;
import cc.emw.mobile.chat.model.bean.Files;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by sunny.du on 2016/10/19.
 * 所有媒体点击放大展示页面。
 */
public class ChatTeamPlayerViewPagerActivity extends BaseActivity {
    ViewPager mVpChatPlayerShow;
    IconTextView itvChatPlayerBg;
    List<Message> palyers;
    int pagerCount;
    List<View> viewList;
    ChatTeamPagerAdapter mMyPagerAdapter;
    List<Files> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_team_palyer_viewpager);
        mVpChatPlayerShow = (ViewPager) findViewById(R.id.vp_chat_player_show);
        itvChatPlayerBg = (IconTextView) findViewById(R.id.itv_player_bg);
        initData();
        initView();
    }

    private void initData() {
        palyers = (List<Message>) getIntent().getSerializableExtra("videos");
        playerList = new ArrayList<>();
        pagerCount = getIntent().getIntExtra("count", 1);
        viewList = new ArrayList<>();
        for (int i = 0; i < palyers.size(); i++) {
            try {
                Files image = new Gson().fromJson(palyers.get(i).getContent(), Files.class);
                playerList.add(image);
                View view = View.inflate(this, R.layout.item_chat_team_player_video, null);
                viewList.add(view);
            } catch (Exception e) {

            }
        }
        mMyPagerAdapter = new ChatTeamPagerAdapter(this, viewList, playerList, palyers);
        mVpChatPlayerShow.setAdapter(mMyPagerAdapter);
    }

    private void initView() {
        /**
         * 跳转指定页面
         */
        try {
            Field field = mVpChatPlayerShow.getClass().getField("mCurltem");
            field.setInt(mVpChatPlayerShow, pagerCount);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mMyPagerAdapter.notifyDataSetChanged();
        mVpChatPlayerShow.setCurrentItem(pagerCount);
        /**
         *设置返回按键监听
         */
        itvChatPlayerBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}


//private class MyPagerAdapter extends PagerAdapter {
//        List<Files> palyers;
//        List<View> viewList;
//        List<Message> msgList;
//        VideoPlayer player;
//
//        public MyPagerAdapter(List<View> list, List<Files> palyerlist, List<Message> msgList) {
//            this.viewList = list;
//            this.palyers = palyerlist;
//            this.msgList = msgList;
//            player = new VideoPlayer(ChatTeamPlayerViewPagerActivity.this);
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, final int position) {
//            ImageView ivChatPlayerImage = (ImageView) viewList.get(position).findViewById(R.id.iv_chat_player_image);
//            final RelativeLayout rlChatPlayerVioce = (RelativeLayout) viewList.get(position).findViewById(R.id.rl_chat_player_voice);
//            final VideoPlayerView vpvChatPlayerVideo = (VideoPlayerView) viewList.get(position).findViewById(R.id.video_player_view);
//            final ProgressBar pbChatLoad = (ProgressBar) viewList.get(position).findViewById(R.id.pb_chat_palyer);
//            final ImageView ivChatPlay = (ImageView) viewList.get(position).findViewById(R.id.chat_iv_play);
//            TextView tvChatNumPage = (TextView) viewList.get(position).findViewById(R.id.tv_num_page);
//            tvChatNumPage.setText(position+1+"/"+viewList.size());
//            if (msgList.get(position).getType() == 7) {
//                ivChatPlayerImage.setVisibility(View.VISIBLE);
//                rlChatPlayerVioce.setVisibility(View.GONE);
//                String imageuri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, palyers.get(position).Url);
//                DisplayImageOptions optiones = ImageLoadFactory.getChatOptiones();//图片load
//                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageuri, optiones);
//                BitmapDrawable bd = new BitmapDrawable(bitmap);
//                bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//                bd.setDither(true);
//                ivChatPlayerImage.setImageBitmap(bitmap);
//            } else if (msgList.get(position).getType() == 9) {
//                /**
//                 * 视频RL点击监听
//                 */
//                rlChatPlayerVioce.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        pbChatLoad.setVisibility(View.VISIBLE);
//                        ivChatPlay.setVisibility(View.GONE);
//                        player.init(EMWApplication.videoPath, vpvChatPlayerVideo, rlChatPlayerVioce, pbChatLoad, true);
//                        Videos videos = new Videos();
//                        videos.setUrl(palyers.get(position).Url);
//                        videos.setLength(palyers.get(position).Length + "");
//                        player.setVideo(videos);
//                        player.downLoadVideo();
//                    }
//                });
//            }
//
//            container.addView(viewList.get(position));
//            return viewList.get(position);
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(viewList.get(position));
//        }
//
//        @Override
//        public int getCount() {
//            return viewList.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//    }