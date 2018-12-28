package cc.emw.mobile.chat.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.adapter.ChatMainAdapter;
import cc.emw.mobile.chat.view.MySeekBar;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;

/**
 * @author xiang.peng 语音播放管理类
 */
public class MediaPlayerManger {

    public static MediaPlayerManger mInstance;
    private MediaPlayer mMediaPlayer;
    private boolean isPlay;
    private long time;
    private TextView tv;
    private String times;

    private MySeekBar mMySeekBar;//设置播放进度
    private ChatMainAdapter mAdapter;
    private IconTextView mItvVoiceOpen;//播放中
    private IconTextView mItvVoiceColse;//没有播放

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
    /**
     * 语音播放
     *
     * @param path 语音文件路径
     * @param tvs  播放时间显示
     * @param tm   播放时长
     * @param type 播放类型
     */
    public void playSound(IconTextView itvVoiceOpen,
                          IconTextView itvVoiceColse, MySeekBar mySeekBar,
                          final String path, final TextView tvs, String tm,
                          final int type, final View view, final Context mContext,
                          final ChatMainAdapter adapter) {
        mMySeekBar = mySeekBar;
        mAdapter = adapter;
        mItvVoiceOpen = itvVoiceOpen;
        mItvVoiceColse = itvVoiceColse;
        if (null != path) {
            tv = tvs;
            times = tm;
            view.setClickable(false);
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        ToastUtil.showToast(mContext, R.string.resource_error);
                        mMediaPlayer.reset();
                        adapter.notifyDataSetChanged();
                        //  mHandler.sendEmptyMessage(1);
                        handler.sendEmptyMessage(0);
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
                        view.setClickable(true);
                        mMediaPlayer.start();
                        handler.sendEmptyMessage(3);
                        isPlay = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (isPlay) {
                                    SystemClock.sleep(200);
                                    time += 1;
                                    handler.sendEmptyMessage(1);//设置进度条的播放进度
                                }
                            }
                        }).start();
                    }
                });
                mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {// 播放完毕接口
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        adapter.notifyDataSetChanged();
                        reset();
                        SystemClock.sleep(200);
                        handler.sendEmptyMessage(0);//设置进度条的播放进度
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
     * 语音播放
     *
     * @param path 语音文件路径
     * @param tvs  播放时间显示
     * @param tm   播放时长
     *             重写方法，废弃之前的无效引用。
     */
    public void playSound(IconTextView itvVoiceOpen,
                          IconTextView itvVoiceColse, MySeekBar mySeekBar,
                          final String path, final TextView tvs, String tm,
                          final View view, final Context mContext,
                          final ChatMainAdapter adapter) {
        mMySeekBar = mySeekBar;
        mAdapter = adapter;
        mItvVoiceOpen = itvVoiceOpen;
        mItvVoiceColse = itvVoiceColse;
        if (null != path) {
            tv = tvs;
            times = tm;
            view.setClickable(false);
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        ToastUtil.showToast(mContext, R.string.resource_error);
                        mMediaPlayer.reset();
                        adapter.notifyDataSetChanged();
                        //  mHandler.sendEmptyMessage(1);
                        handler.sendEmptyMessage(0);
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
                        view.setClickable(true);
                        mMediaPlayer.start();
                        handler.sendEmptyMessage(3);
                        isPlay = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (isPlay) {
                                    SystemClock.sleep(200);
                                    time += 1;
                                    handler.sendEmptyMessage(1);//设置进度条的播放进度
                                }
                            }
                        }).start();
                    }
                });
                mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {// 播放完毕接口
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        adapter.notifyDataSetChanged();
                        reset();
                        SystemClock.sleep(200);
                        handler.sendEmptyMessage(0);//设置进度条的播放进度
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
            isPlay = false;
            time = 0;
            tv.setText(times);
            mItvVoiceOpen.setVisibility(View.GONE);
            mItvVoiceColse.setVisibility(View.VISIBLE);
            mMySeekBar.setProgress(0);
        }
    }
    public boolean getIsPlay(){
        return isPlay;
    }
    public void release() {
        if (mMediaPlayer != null) {
//            handler.sendEmptyMessage(2);
            mItvVoiceOpen.setVisibility(View.GONE);
            mItvVoiceColse.setVisibility(View.VISIBLE);
            mMySeekBar.setProgress(0);
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
        mItvVoiceOpen.setVisibility(View.GONE);
        mItvVoiceColse.setVisibility(View.VISIBLE);
        mMySeekBar.setProgress(0);
        isPlay = false;
        time = 0;

    }

    int i = 0;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mItvVoiceOpen.setVisibility(View.GONE);
                    mItvVoiceColse.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    if (isPlay) {
                        try {
                            if (mMediaPlayer.getCurrentPosition() < mMySeekBar.getMax() - 100) {//开始播放
                                try {
                                    if (time % 5 == 0) {
                                        Format f0 = new SimpleDateFormat("ss");
                                        SimpleDateFormat f1 = new SimpleDateFormat("mm:ss");
                                        Date d = (Date) f0.parseObject(time / 5 + "");
                                        tv.setText(f1.format(d));
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mMySeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                                mItvVoiceOpen.setVisibility(View.VISIBLE);
                                mItvVoiceColse.setVisibility(View.GONE);
                            } else if (mMediaPlayer.getCurrentPosition() >= mMySeekBar.getMax() - 100) {//停止播放
                                mMySeekBar.setProgress(0);
                                reset();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessageDelayed(1, 500);
                    }
                    break;
                case 2:
                    mItvVoiceOpen.setVisibility(View.GONE);
                    mItvVoiceColse.setVisibility(View.VISIBLE);
                    mMySeekBar.setProgress(0);
                    break;
                case 3:
                    mMySeekBar.setMax(mMediaPlayer.getDuration());
            }
        }
    };
}


/********
 * 12.9修复语音播放的一系列bUG   版本预留 稳定后删除  suunnydu
 * <p/>
 * TODO 原版本语音播放存在严重BUG，重新实现，暂时保留原版代码，后续稳定后删除   sunnydu
 ********/
//package cc.emw.mobile.chat.utils;
//
//        import android.annotation.SuppressLint;
//        import android.content.Context;
//        import android.graphics.drawable.AnimationDrawable;
//        import android.hardware.SensorManager;
//        import android.media.AudioManager;
//        import android.media.MediaPlayer;
//        import android.media.MediaPlayer.OnCompletionListener;
//        import android.media.MediaPlayer.OnErrorListener;
//        import android.media.MediaPlayer.OnPreparedListener;
//        import android.os.Handler;
//        import android.os.Message;
//        import android.util.Log;
//        import android.view.View;
//        import android.widget.ImageView;
//        import android.widget.TextView;
//
//        import java.text.Format;
//        import java.text.ParseException;
//        import java.text.SimpleDateFormat;
//        import java.util.Date;
//
//        import cc.emw.mobile.R;
//        import cc.emw.mobile.chat.adapter.ChatMainAdapter;
//        import cc.emw.mobile.chat.view.MySeekBar;
//        import cc.emw.mobile.util.ToastUtil;
//        import cc.emw.mobile.view.IconTextView;
//
///**
// * @author xiang.peng 语音播放管理类
// */
//public class MediaPlayerManger {
//
//    public static MediaPlayerManger mInstance;
//    private MediaPlayer mMediaPlayer;
//    private boolean isPlay;
//    private long time;
//    private TextView tv;
//    private String times;
//
//    private MySeekBar mMySeekBar;//设置播放进度
//    private ChatMainAdapter mAdapter;
//    private IconTextView mItvVoiceOpen;//播放中
//    private IconTextView mItvVoiceColse;//没有播放
//    private MediaPlayerManger() {
//    }
//    public static MediaPlayerManger getInstance() {
//        if (mInstance == null) {
//            synchronized (MediaPlayerManger.class) {
//                if (mInstance == null) {
//                    mInstance = new MediaPlayerManger();
//                }
//            }
//        }
//        return mInstance;
//    }
//
//    @SuppressLint({"HandlerLeak", "SimpleDateFormat"})
//    private Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            synchronized (MediaPlayerManger.class) {
//                if (msg.what == 1) {
//                } else {
//                    try {
//                        if (time % 5 == 0) {
//                            Format f0 = new SimpleDateFormat("ss");
//                            SimpleDateFormat f1 = new SimpleDateFormat("mm:ss");
//                            Date d = (Date) f0.parseObject(time / 5 + "");
//                            tv.setText(f1.format(d));
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//    };
//
//    /**
//     * 语音播放
//     *
//     * @param path 语音文件路径
//     * @param tvs  播放时间显示
//     * @param tm   播放时长
//     * @param type 播放类型
//     */
//    public void playSound(IconTextView itvVoiceOpen, IconTextView itvVoiceColse, MySeekBar mySeekBar, final String path, final TextView tvs, String tm, final int type, final View view, final Context mContext, final ChatMainAdapter adapter) {//, final ChatMainAdapter adapters
//        mMySeekBar = mySeekBar;
//        mAdapter=adapter;
//        mItvVoiceOpen=itvVoiceOpen;
//        mItvVoiceColse=itvVoiceColse;
//        if (null != path) {
//            tv = tvs;
//            times = tm;
//            view.setClickable(false);
//            if (mMediaPlayer == null) {
//                mMediaPlayer = new MediaPlayer();
//                mMediaPlayer.setOnErrorListener(new OnErrorListener() {
//
//                    @Override
//                    public boolean onError(MediaPlayer mp, int what, int extra) {
//                        ToastUtil.showToast(mContext, R.string.resource_error);
//                        mMediaPlayer.reset();
//                        adapter.notifyDataSetChanged();
//                        mHandler.sendEmptyMessage(1);
////                        handler.sendEmptyMessage(0);
//                        reset();
//                        return false;
//                    }
//                });
//            }
//            try {
//                mMediaPlayer.reset();
//                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                //语音播放准备完成回调
//                mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
//
//                    @Override
//                    public void onPrepared(MediaPlayer arg0) {
//                        view.setClickable(true);
//                        mMediaPlayer.start();
//                        handler.sendEmptyMessage(3);
//                        isPlay = true;
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                while (isPlay) {
//                                    try {
//                                        Thread.sleep(200);
//                                        time += 1;
//                                        mHandler.sendEmptyMessage(2);
//                                        handler.sendEmptyMessage(1);//设置进度条的播放进度
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }).start();
//                    }
//                });
//                mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        adapter.notifyDataSetChanged();
//                        reset();
//                        try {
//                            Thread.sleep(200);
//                            mHandler.sendEmptyMessage(1);
//                            handler.sendEmptyMessage(0);//设置进度条的播放进度
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                mMediaPlayer.setDataSource(path);
//                mMediaPlayer.prepareAsync();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 暂停播放
//     */
//    public void pause() {
//        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//            mMediaPlayer.pause();
//            reset();
//            tv.setText(times);
//            try {
//                Thread.sleep(200);
//                mHandler.sendEmptyMessage(1);
//                handler.sendEmptyMessage(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void release() {
//        if (mMediaPlayer != null) {
//            handler.sendEmptyMessage(2);
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//    }
//
//    public void resume() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.start();
//        }
//    }
//
//    private void reset() {
//        mItvVoiceOpen.setVisibility(View.GONE);
//        mItvVoiceColse.setVisibility(View.VISIBLE);
//        mMySeekBar.setProgress(0);
//        isPlay = false;
//        time = 0;
//
//    }
//
//    int i = 0;
//    private Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    mItvVoiceOpen.setVisibility(View.GONE);
//                    mItvVoiceColse.setVisibility(View.VISIBLE);
//                    break;
//                case 1:
//                    try {
//                        if (mMediaPlayer.getCurrentPosition() < mMySeekBar.getMax() - 100) {//开始播放
//                            mMySeekBar.setProgress(mMediaPlayer.getCurrentPosition());
//                            mItvVoiceOpen.setVisibility(View.VISIBLE);
//                            mItvVoiceColse.setVisibility(View.GONE);
//                        } else if (mMediaPlayer.getCurrentPosition() >= mMySeekBar.getMax() - 100) {//停止播放
//                            mMySeekBar.setProgress(0);
//                            reset();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    handler.sendEmptyMessageDelayed(1, 500);
//                    break;
//                case 2:
//                    mItvVoiceOpen.setVisibility(View.GONE);
//                    mItvVoiceColse.setVisibility(View.VISIBLE);
//                    mMySeekBar.setProgress(0);
//                    break;
//                case 3:
//                    mMySeekBar.setMax(mMediaPlayer.getDuration());
//            }
//        }
//    };
//}

/********
 * TODO 原版本语音播放存在严重BUG，重新实现，暂时保留原版代码，后续稳定后删除   sunnydu
 ********/
//package cc.emw.mobile.chat.utils;
//
//        import android.annotation.SuppressLint;
//        import android.content.Context;
//        import android.graphics.drawable.AnimationDrawable;
//        import android.hardware.SensorManager;
//        import android.media.AudioManager;
//        import android.media.MediaPlayer;
//        import android.media.MediaPlayer.OnCompletionListener;
//        import android.media.MediaPlayer.OnErrorListener;
//        import android.media.MediaPlayer.OnPreparedListener;
//        import android.os.Handler;
//        import android.util.Log;
//        import android.view.View;
//        import android.widget.ImageView;
//        import android.widget.TextView;
//
//        import java.text.Format;
//        import java.text.ParseException;
//        import java.text.SimpleDateFormat;
//        import java.util.Date;
//
//        import cc.emw.mobile.R;
//        import cc.emw.mobile.chat.adapter.ChatMainAdapter;
//        import cc.emw.mobile.util.ToastUtil;
//
///**
// * @author xiang.peng 语音播放管理类
// */
//public class MediaPlayerManger {
//
//    public static MediaPlayerManger mInstance;
//    private MediaPlayer mMediaPlayer;
//    private boolean isPlay;
//    private long time;
//    private TextView tv;
//    private int allTime, currentTime;
//    private ImageView mImageView;
//    private String times;
//    private AnimationDrawable animation;
//
//    private MediaPlayerManger() {
//    }
//
//    public static MediaPlayerManger getInstance() {
//
//        if (mInstance == null) {
//            synchronized (MediaPlayerManger.class) {
//                if (mInstance == null) {
//                    mInstance = new MediaPlayerManger();
//                }
//            }
//        }
//        return mInstance;
//    }
//
//    @SuppressLint({"HandlerLeak", "SimpleDateFormat"})
//    private Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            synchronized (MediaPlayerManger.class) {
//                if (msg.what == 1) {
//                } else {
//                    try {
//                        if (time % 5 == 0) {
//                            Format f0 = new SimpleDateFormat("ss");
//                            SimpleDateFormat f1 = new SimpleDateFormat("mm:ss");
//                            Date d = (Date) f0.parseObject(time / 5 + "");
//                            tv.setText(f1.format(d));
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//    };
//
//    /**
//     * 语音播放
//     *
//     * @param path 语音文件路径
//     * @param tvs  播放时间显示
//     * @param tm   播放时长
//     * @param type 播放类型
//     */
//    public void playSound(final String path, final ImageView iv,
//                          final TextView tvs, String tm, final int type, final View view,
//                          final Context mContext, final ChatMainAdapter adapter) {//, final ChatMainAdapter adapters
//        if (null != path) {
//            SensorManager ASM = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
//            Log.i("px", "MediaPlayer url=" + path);
//            tv = tvs;
//            times = tm;
//            mImageView = iv;
//            view.setClickable(false);
//            if (mMediaPlayer == null) {
//                mMediaPlayer = new MediaPlayer();
//                mMediaPlayer.setOnErrorListener(new OnErrorListener() {
//
//                    @Override
//                    public boolean onError(MediaPlayer mp, int what, int extra) {
//                        ToastUtil.showToast(mContext, R.string.resource_error);
//                        mMediaPlayer.reset();
//                        if (animation != null) {
//                            animation.selectDrawable(0);
//                            animation.stop();
//                            adapter.notifyDataSetChanged();
//                        }
//                        mHandler.sendEmptyMessage(1);
//                        reset();
//                        return false;
//                    }
//                });
//            }
//            try {
//                mMediaPlayer.reset();
//                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                //语音播放准备完成回调
//                mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
//
//                    @Override
//                    public void onPrepared(MediaPlayer arg0) {
//                        view.setClickable(true);
//                        mMediaPlayer.start();
//                        AnimationDrawable anima = (AnimationDrawable) mImageView
//                                .getBackground();
//                        anima.selectDrawable(0);
//                        anima.start();
//                        animation = anima;
//                        allTime = mMediaPlayer.getDuration();
//                        isPlay = true;
//                        new Thread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                while (isPlay) {
//                                    try {
//                                        Thread.sleep(200);
//                                        time += 1;
//                                        currentTime = mMediaPlayer
//                                                .getCurrentPosition();
//                                        mHandler.sendEmptyMessage(2);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }).start();
//                    }
//                });
//                mMediaPlayer
//                        .setOnCompletionListener(new OnCompletionListener() {
//
//                            @Override
//                            public void onCompletion(MediaPlayer mp) {
//                                if (animation != null) {
//                                    animation.selectDrawable(0);
//                                    animation.stop();
//                                    adapter.notifyDataSetChanged();
//                                }
//                                reset();
//                                try {
//                                    Thread.sleep(200);
//                                    mHandler.sendEmptyMessage(1);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                mMediaPlayer.setDataSource(path);
//                mMediaPlayer.prepareAsync();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 暂停播放
//     */
//    public void pause() {
//        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//            mMediaPlayer.pause();
//            if (animation != null) {
//                animation.selectDrawable(0);
//                animation.stop();
//            }
//            reset();
//            tv.setText(times);
//            try {
//                Thread.sleep(200);
//                mHandler.sendEmptyMessage(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void release() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//    }
//
//    public void resume() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.start();
//        }
//    }
//
//    private void reset() {
//        isPlay = false;
//        time = 0;
//        currentTime = 0;
//        allTime = 0;
//    }
//}