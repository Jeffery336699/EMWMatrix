package cc.emw.mobile.chat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Type;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.chat.utils.VideoPlayer;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.view.VideoPlayerView;

/**
 * Created by jven.wu on 2016/7/28.
 */
@ContentView(R.layout.activity_testvideo)
public class TestVideoActivity extends BaseActivity implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener,
        SurfaceHolder.Callback, MediaController.MediaPlayerControl,
        MediaPlayer.OnBufferingUpdateListener {

    private final String TAG = this.getClass().getSimpleName();
    @ViewInject(R.id.main_ll)
    private RelativeLayout main_ll;
    @ViewInject(R.id.videoview)
    private VideoView vv;
    @ViewInject(R.id.surfaceview)
    private SurfaceView surfaceView;
    @ViewInject(R.id.video_player_view)
    private VideoPlayerView video_player_view;
    @ViewInject(R.id.tip)
    private TextView tip;
    @ViewInject(R.id.progress)
    private ProgressBar mProgressBar;

    private Display currentDisplay;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private int videoWidth = 0;
    private int videoHeight = 0;
    private boolean readyToPlay = false;
    private MediaController controller;
    private boolean isComplete = false;
    private String videoPath;
    private VideoPlayer videoPlayer;
    private Videos videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
//        x.view().inject(this);
        initView();
    }

    private void initView() {
        videoPlayer = new VideoPlayer(this);
        videoPlayer.init(EMWApplication.videoPath, video_player_view, main_ll, mProgressBar, false);

        if (0 == 1) {
            Intent intent = getIntent();
            videoPath = intent.getStringExtra("videoPath");
            Logger.d(TAG, "initView()->videoPath: " + videoPath);
            String fileName = "5B73812FDC9D45D6B020D8325F31EFC4.mp4";
            String filePath = EMWApplication.videoPath + fileName;
//        String fileName = "Test1.mp4";
            Uri videoUri = Uri.parse(filePath);
            Uri videoNet = Uri.parse(ChatContent.REMOTE_VIDEO_FOLDER + fileName);
            Log.d(TAG, filePath);
//        vv.setVideoURI(videoNet);
//        vv.start();
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(videoUri,"video/mp4");
//        startActivity(intent);
            controller = new MediaController(this);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);

            try {
//            mediaPlayer.setDataSource(this,videoUri);
                if (videoPath != null || true) {
                    mediaPlayer.setDataSource(this, videoUri);
                }
//            mediaPlayer.setDataSource("http://www.mobvcasting.com/android/video/" + fileName);
            } catch (Exception e) {
                Log.d(TAG, "Exception: " + e.getMessage());
                e.printStackTrace();
                finish();
            }

            currentDisplay = getWindowManager().getDefaultDisplay();
        }

        main_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        main_ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        Logger.d(TAG, "onResume()->");
        super.onResume();
//        String msgGetContent = "{\"Length\":\"3\",\"Url\":\"Resource/emw/UserVideo/5B73812FDC9D45D6B020D8325F31EFC4.mp4\"}";
        String msgGetContent = getIntent().getStringExtra("videos");
        Type types = new TypeToken<Videos>() {
        }.getType();
        videos = new Gson().fromJson(msgGetContent, types);
        main_ll.post(new Runnable() {
            @Override
            public void run() {
                videoPlayer.init(EMWApplication.videoPath, video_player_view, main_ll, mProgressBar, false);
                videoPlayer.setVideo(videos);
                videoPlayer.downLoadVideo();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy()->");
        super.onDestroy();
        videoPlayer.stopVideo();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (controller != null) {
            if (controller.isShowing()) {
                controller.hide();
            } else {
                controller.show();
            }
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Logger.d(TAG, "surfaceCreated()->");
        mediaPlayer.setDisplay(holder);
        tip.setText("MediaPlayer Display Surface set");
        try {
            mediaPlayer.prepare();
//            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Logger.d(TAG, "surfaceCreated()->Exception: " + e.getMessage());
            e.printStackTrace();
            finish();
        }
        tip.setText("Prepare");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.d(TAG, "surfaceChanged()->");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logger.d(TAG, "surfaceDestroyed()->");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Logger.d(TAG, "onCompletion()->");
        isComplete = true;
        tip.setText("Play Completion");
        mediaPlayer.start();
//        finish();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            Log.d(TAG, "onError()->Server died" + extra);
        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            Log.d(TAG, "onError()->Server unknown" + extra);
        }
        tip.setText("Media Error");
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {
            Log.d(TAG, "onInfo()->BAD_INTERLEAVING" + extra);
        } else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
            Log.d(TAG, "onInfo()->NOT_SEEKABLE" + extra);
        } else if (what == MediaPlayer.MEDIA_INFO_UNKNOWN) {
            Log.d(TAG, "onInfo()->UNKNOWN" + extra);
        } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
            Log.d(TAG, "onInfo()->VIDEO_TRACK_LAGGING" + extra);
        } else if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
            Log.d(TAG, "onInfo()->METADATA_UPDATE" + extra);
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Logger.d(TAG, "onPrepared()->");
//        videoWidth = mp.getVideoWidth();
        videoWidth = surfaceView.getWidth();
        videoHeight = mp.getVideoHeight();
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

        surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
        mp.start();
        tip.setText("Play Start");

        controller.setMediaPlayer(this);
        controller.setAnchorView(main_ll);
        controller.setEnabled(true);
        controller.show();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete()->");
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
            tip.setText("MediaPlay Buffering:" + percent + "%");
        }
    }
}
