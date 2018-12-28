package cc.emw.mobile.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.view.VideoPlayerView;
import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.SimpleDListener;

/**
 * Created by jven.wu on 2016/8/6.
 */
public class VideoPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener,
        SurfaceHolder.Callback, MediaController.MediaPlayerControl,
        MediaPlayer.OnBufferingUpdateListener, TextureView.SurfaceTextureListener {
    private final String TAG = this.getClass().getSimpleName();

    private View main_ll;
    private SurfaceView surfaceView;
    private ProgressBar progressBar;

    private Context mContext;
    private Display currentDisplay;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private int videoWidth = 0;
    private int videoHeight = 0;
    private boolean readyToPlay = false;
    private MediaController controller;
    private boolean isComplete = false;
    private String videoPath = EMWApplication.videoPath; //视频存放路径 EMWApplication.tempPath
    private Videos videos;
    private String fileName;
    private OnPlayListener onPlayListener;
    private boolean isSlient = false; //是否静音标志
    private String fileUrl;
    private float surfaceRatio = 9f / 16; //视频宽高比
    private VideoPlayerView textureView;
    private Surface mSurface;

    public VideoPlayer(Context context) {
        this.mContext = context;
    }

    public void init(String path, VideoPlayerView ttv, View anchorView, ProgressBar pb, boolean isSlient) {
        this.textureView = ttv;
        main_ll = anchorView;
        progressBar = pb;
        videoPath = path;
        this.isSlient = isSlient;
        Logger.d(TAG, "init()->videoPath: " + videoPath);
//        使用SurfaceView时设置
//        controller = new MediaController(mContext);
//        if(surfaceView != null) {
//            surfaceHolder = surfaceView.getHolder();
//            surfaceHolder.addCallback(this);
//        }
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        if (surfaceView != null && surfaceHolder.getSurface().isValid()) {
            mediaPlayer.setDisplay(surfaceHolder);
        } else if (textureView != null) {
            textureView.setSurfaceTextureListener(this);
            SurfaceTexture texture = textureView.getSurfaceTexture();
            Logger.v(TAG, "init()->texture " + texture);
            setSurfaceTexture(texture);
        }

        currentDisplay = ((Activity) mContext).getWindowManager().getDefaultDisplay();
    }

    public void setVideo(Videos videos) {
        this.videos = videos;
        fileName = videos.getUrl().substring(videos.getUrl().lastIndexOf("/") + 1);
        Logger.d(TAG, "setVideo()->fn: " + fileName);
        fileUrl = Const.BASE_URL + "/" + videos.getUrl();
//        fileUrl = HelpUtil.getFileURL(videos.getUrl());
    }
    private boolean mIsVideoLocationFlag=true;
    public void setVideo(Videos videos,boolean isVideoLocationFlag) {
        this.mIsVideoLocationFlag=isVideoLocationFlag;
        if(mIsVideoLocationFlag) {
            this.videos = videos;
            fileName = videos.getUrl().substring(videos.getUrl().lastIndexOf("/") + 1);
            Logger.d(TAG, "setVideo()->fn: " + fileName);
            fileUrl = Const.BASE_URL + "/" + videos.getUrl();
//        fileUrl = HelpUtil.getFileURL(videos.getUrl());
        }else{
            this.videos = videos;
            fileUrl=videos.getUrl();
        }
    }

    /**
     * 显示或隐藏视频控制条
     */
    private void toggleController() {
        if (controller.isShowing()) {
            controller.hide();
        } else {
            controller.show();
        }
    }

    /**
     * 播放视频，此时进入准备状态{@link #onPrepared(MediaPlayer)}
     */
    private void playVideo() {
        Logger.d(TAG, "playVideo()->");
        try {
            if (videoPath != null) {
                if (mediaPlayer == null) {
                    Log.d(TAG, "initView()->media is null");
                }
                if(mIsVideoLocationFlag) {
                    String filePath = videoPath + fileName;
                    mediaPlayer.setDataSource(filePath);
                    Logger.d(TAG, "playVideo()->filename: " + fileName);
                }else{
                    String filePath = fileUrl;
                    mediaPlayer.setDataSource(filePath);
                }
            }
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopVideo() {
        if (mediaPlayer != null) {
//            if(mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 异步下载视频下载后，或文件已存在的情况下，调用视频播放{@link #playVideo()}
     */
    public void downLoadVideo() {
        Logger.d(TAG,"downLoadVideo()->");
        if (!FileUtil.hasFile(videoPath + fileName)&&mIsVideoLocationFlag) {
            DLManager.getInstance(mContext).dlStart(fileUrl, videoPath, "", new SimpleDListener() {
                @Override
                public void onPrepare() {
                    Logger.d(TAG, "downLoadVideo()->DLManager.onPrepare()->");
                    main_ll.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setIndeterminate(false);
                        }
                    });
                }

                @Override
                public void onStart(String fileName, String realUrl, final int fileLength) {
                    super.onStart(fileName, realUrl, fileLength);
                    Logger.d(TAG, "downLoadVideo()->DLManager.onStart()->");
                    main_ll.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setMax(fileLength);
                        }
                    });
                }

                @Override
                public void onProgress(final int progress) {
                    main_ll.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                        }
                    });
                    Logger.d(TAG, "downLoadVideo()->DLManager.onProgress()->" + progress);
                }

                @Override
                public void onStop(int progress) {
                    Log.d(TAG, "downLoadVideo()->DLManager.onStop()->");
                }

                @Override
                public void onFinish(File file) {
                    main_ll.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                    playVideo();
                    Logger.d(TAG, "DLManager.onFinish()->");
                }

                @Override
                public void onError(int status, String error) {
                    Logger.d(TAG, "DLManager.onError()->error: " + error);
                }
            });
        } else {

            progressBar.setVisibility(View.INVISIBLE);
            playVideo();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Logger.d(TAG, "surfaceCreated()->");
        mediaPlayer.setDisplay(holder);
//        try{
//            mediaPlayer.prepare();
////            mediaPlayer.prepareAsync();
//        }catch (Exception e){
//            Log.d(TAG,"surfaceCreated()->Exception: " + e.getMessage());
//        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.d(TAG, "surfaceChanged()->");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logger.d(TAG, "surfaceDestroyed()->");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Logger.d(TAG, "onCompletion()->");
        isComplete = true;
        if(oc !=null){
            oc.showBut();
        }
//        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            Logger.d(TAG, "onError()->Server died" + extra);
        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            Logger.d(TAG, "onError()->Server unknown" + extra);
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {
            Logger.d(TAG, "onInfo()->BAD_INTERLEAVING" + extra);
        } else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
            Logger.d(TAG, "onInfo()->NOT_SEEKABLE" + extra);
        } else if (what == MediaPlayer.MEDIA_INFO_UNKNOWN) {
            Logger.d(TAG, "onInfo()->UNKNOWN" + extra);
        } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
            Logger.d(TAG, "onInfo()->VIDEO_TRACK_LAGGING" + extra);
        } else if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
            Logger.d(TAG, "onInfo()->METADATA_UPDATE" + extra);
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Logger.d(TAG, "onPrepared()->");
        if(textureView != null){
//        videoWidth = mp.getVideoWidth();
            videoWidth = textureView.getWidth();
            videoHeight = (int) (videoWidth * surfaceRatio);
            Logger.d(TAG, "onPrepared()->surfaceRatio: " + surfaceRatio + "videoHeight: " + videoHeight + " 9/11: " + 9f / 11 + " 9/16" + 9f / 16);
            if (videoWidth > currentDisplay.getWidth() ||
                    videoHeight > currentDisplay.getHeight()) {
                float widthRatio = (float) videoWidth / (float) currentDisplay.getWidth();
                float heightRatio = (float) videoHeight / (float) currentDisplay.getHeight();
                if (heightRatio > 1 || widthRatio > 1) {
                    if (heightRatio > widthRatio) {
                        videoWidth = (int) Math.ceil((float) videoWidth / (float) heightRatio);
                        videoHeight = (int) Math.ceil((float) videoHeight / (float) heightRatio);
                    } else {
                        videoWidth = (int) Math.ceil((float) videoWidth / (float) widthRatio);
                        videoHeight = (int) Math.ceil((float) videoHeight / (float) widthRatio);
                    }
                }
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textureView.getLayoutParams();
            params.width = videoWidth;
            params.height = videoHeight;
//            textureView.setLayoutParams(params);
            textureView.setVisibility(View.VISIBLE);
        }
        if (isSlient) {
            mp.setVolume(0f, 0f);
        }
        mp.start();
        if (onPlayListener != null) {
            onPlayListener.onPlay();
        }

//        controller.setMediaPlayer(this);
//        controller.setAnchorView(main_ll);
//        controller.setEnabled(true);
//        controller.show();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Logger.d(TAG, "onSeekComplete()->");
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Logger.d(TAG, "onVideoSizeChanged()->");
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (!isComplete && percent < 100 || true) {
        }
    }

    /**
     * 获取网络或本地视频的第一帧
     * @param isFromNetWork
     * @return bitmap
     */
    public Bitmap getFirstVideoFrame(boolean isFromNetWork) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        FileOutputStream outStream = null;
        try {
            if (isFromNetWork) {
                //获取网络视频
                retriever.setDataSource(fileUrl, new HashMap<String, String>());
            } else {
                //获取本地视频
                String filePath = videoPath + fileName;
                retriever.setDataSource(filePath);
            }
            bitmap = retriever.getFrameAtTime();
            if (bitmap != null) {
                outStream = new FileOutputStream(new File(EMWApplication.tempPath + fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg"));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    /**
     * 重载获取视频第一帧图片   适用于本地加载
     */
    public Bitmap getFirstVideoFrame(String file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        FileOutputStream outStream = null;
        try {
            //获取本地视频
            String filePath = file;
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
            if (bitmap != null) {
                outStream = new FileOutputStream(new File(EMWApplication.tempPath + fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg"));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    /**
     * 设置视频宽高比
     *
     * @param ratio
     */
    public void setSurfaceRatio(float ratio) {
        this.surfaceRatio = ratio;
        Logger.d(TAG, "setSurfaceRatio()->");
    }

    /**
     * 设置视频播放监听
     *
     * @param listener
     */
    public void setOnPlayListener(OnPlayListener listener) {
        this.onPlayListener = listener;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //在表面可用时，设置视频表面
        Logger.v(TAG, "onSurfaceTextureAvailable()->");
        if (textureView == null) {
            return;
        }
        SurfaceTexture texture = textureView.getSurfaceTexture();
        Logger.v(TAG, "onSurfaceTextureAvailable()->texture " + texture);
        setSurfaceTexture(texture);
        mediaPlayer.seekTo(0);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Logger.d(TAG, "onSurfaceTextureSizeChanged()->");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Logger.d(TAG, "onSurfaceTextureDestroyed()->");
//        textureView=null;
//        mSurface = null;
        if (mediaPlayer != null) {
//            mediaPlayer.stop();
            mediaPlayer.reset();
//            mediaPlayer.release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Logger.d(TAG, "onSurfaceTextureUpdated()->");
    }

    public interface OnPlayListener {
        void onPlay();
    }

    /**
     * 设置视频表面
     *
     * @param surfaceTexture
     */
    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        Logger.v(TAG, "setSurfaceTexture()->1 " + surfaceTexture);
        Logger.v(TAG, "setSurfaceTexture()->2mSurface " + mSurface);


        if (surfaceTexture != null) {
            mSurface = new Surface(surfaceTexture);
            mediaPlayer.setSurface(mSurface);
        } else {
            mediaPlayer.setSurface(null);
        }
        Logger.v(TAG, "setSurfaceTexture()->3 " + surfaceTexture);

    }

    private OnClose oc;

    public void setOnClose(OnClose oc) {
        this.oc = oc;
    }

    public interface OnClose {
        void showBut();
    }
}
