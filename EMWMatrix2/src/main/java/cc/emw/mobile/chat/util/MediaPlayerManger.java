package cc.emw.mobile.chat.util;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.RoundProgressBar;

/**
 * @author xiang.peng 语音播放管理类
 */
public class MediaPlayerManger {

    public static MediaPlayerManger mInstance;
    private MediaPlayer mMediaPlayer;
    private boolean isPlay;
    private long time;
    private TextView tv;
    private int allTime, currentTime;
    private RoundProgressBar but;
//    private ImageView mImageView;
    private String times;

    private MediaPlayerManger() {
    }

    public static MediaPlayerManger getInstance() {

        if (mInstance == null) {
            synchronized (MediaPlayerManger.class) {
                if (mInstance == null) {
                    mInstance = new MediaPlayerManger();
                }
            }
        }
        return mInstance;
    }

    @SuppressLint({"HandlerLeak", "SimpleDateFormat"})
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            synchronized (MediaPlayerManger.class) {
                if (msg.what == 1) {
                    but.setProgress(0);
                } else {
                    try {
                        but.setProgress(currentTime);
                        if (time % 5 == 0) {
                            Format f0 = new SimpleDateFormat("ss");
                            SimpleDateFormat f1 = new SimpleDateFormat("mm:ss");
                            Date d;
//                            if (allTime > 4500) {
//                                d = (Date) f0.parseObject(time / 5 + 1 + "");
//                            } else {
                            d = (Date) f0.parseObject(time / 5 + "");
//                            }
                            tv.setText(f1.format(d));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    };

    /**
     * 语音播放
     *
     * @param path 语音文件路径
     * @param buts 语音播放按钮
     * @param tvs  播放时间显示
     * @param tm   播放时长
     * @param type 播放类型
     */
    public void playSound(final String path, final RoundProgressBar buts,
                          final TextView tvs,  String tm, final int type,
                          final Context mContext) {//, final ChatMainAdapter adapters
        if (null != path) {
            Log.i("px", "MediaPlayer url=" + path);
            tv = tvs;
            but = buts;
            times = tm;
//            mImageView = iv;
            buts.setClickable(false);
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnErrorListener(new OnErrorListener() {

                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        ToastUtil.showToast(mContext, R.string.resource_error);
                        mMediaPlayer.reset();
//                        iv.setImageResource(R.drawable.chat_btn_bofang_w);
                        but.setBackgroundResource(R.drawable.chat_btn_bofang_w);
                        mHandler.sendEmptyMessage(1);
                        reset();
                        return false;
                    }
                });
            }
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //语音播放准备完成回调
                mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer arg0) {
                        mMediaPlayer.start();
                        buts.setClickable(true);
                        if (type == 1) {
//                            iv.setImageResource(R.drawable.chat_btn_zanting_h);
                            but.setBackgroundResource(R.drawable.chat_btn_zanting_h);
                        } else {
//                            iv.setImageResource(R.drawable.chat_btn_zanting_w);
                            but.setBackgroundResource(R.drawable.chat_btn_zanting_w);
                        }
                        allTime = mMediaPlayer.getDuration();
                        Log.d("px", "daxiao " + allTime);
                        but.setMax(allTime);
                        isPlay = true;
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                while (isPlay) {
                                    try {
                                        Thread.sleep(200);
                                        time += 1;
                                        currentTime = mMediaPlayer
                                                .getCurrentPosition();
                                        mHandler.sendEmptyMessage(2);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    }
                });
                mMediaPlayer
                        .setOnCompletionListener(new OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
//                                iv.setImageResource(R.drawable.chat_btn_bofang_w);
                                but.setBackgroundResource(R.drawable.chat_btn_bofang_w);
//                                adapters.notifyDataSetChanged();
                                reset();
                                try {
                                    Thread.sleep(200);
                                    mHandler.sendEmptyMessage(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            reset();
//            mImageView.setImageResource(R.drawable.chat_btn_bofang_w);
            but.setBackgroundResource(R.drawable.chat_btn_bofang_w);
            tv.setText(times);
            try {
                Thread.sleep(200);
                mHandler.sendEmptyMessage(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void resume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void reset() {
        isPlay = false;
        time = 0;
        currentTime = 0;
        allTime = 0;
    }
}