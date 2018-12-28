package cc.emw.mobile.chat.function;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.TestVideoActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.view.ScrollableViewHelper;
import cc.emw.mobile.chat.view.SlidingUpPanelLayout;
import cc.emw.mobile.util.CommonVideo;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.MyButtonButton;
import cc.emw.mobile.view.MyLinearLayout;
import cc.emw.mobile.view.MyProgressBar;

/**
 * 预留类  后续稳定后删除
 */
@ContentView(R.layout.activity_chat_video_create)
public class ChatVideoCreateActivity extends BaseActivity {
    private static final int RECORD_TIME_MAX = 10000;
    @ViewInject(R.id.sliding_layout)
    private SlidingUpPanelLayout mSlidingLayout;
    @ViewInject(R.id.dragView)
    private LinearLayout mDragView;
    @ViewInject(R.id.record_progress)
    private MyProgressBar mProgressView;
    @ViewInject(R.id.btn)
    private MyButtonButton mBtn;
    @ViewInject(R.id.sf)
    private SurfaceView mSf;
    @ViewInject(R.id.myll)
    private MyLinearLayout myll;
    @ViewInject(R.id.bottom_layout)
    private RelativeLayout bottomLayout;
    @ViewInject(R.id.focus_tip)
    private TextView mFocusTip;
    @ViewInject(R.id.record_tip)
    private TextView mRecordTip;
    @ViewInject(R.id.release_tip)
    private TextView mReleaseTip;

    private CommonVideo commonVideo;
    private String lastPath = "";
    private File file;
    private Handler mHandler = new Handler();
    private int mTimeCount;
    private Timer mTimer;
    private boolean isRecording = false;
    private long tabSurfaceStartTime;
    private boolean atRemove = false;
    private int recordDuration = 0;
    private int zoomValue = 0;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorListener sensorListener;
    private boolean isAutoFocus = true;
    //初始化自动对焦回掉
    private Camera.AutoFocusCallback autoFocusCallback;
    private float mLastX;
    private float mLastY;
    private float mLastZ;
    private boolean initBool = false;
    private RelativeLayout.LayoutParams focusTipParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        initSlidingLaout();
        initSensor();
        //初始化视屏录像
        commonVideo = new CommonVideo(this, mSf);
        mSf.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (tabSurfaceStartTime == 0) {
                            tabSurfaceStartTime = System.currentTimeMillis();
                        } else {
                            if (System.currentTimeMillis() - tabSurfaceStartTime < 500) {
                                switch (zoomValue) {
                                    case 0:
                                        zoomValue = 4;
                                        break;
                                    case 4:
                                        zoomValue = 6;
                                        break;
                                    case 6:
                                        zoomValue = 8;
                                        break;
                                    case 8:
                                        zoomValue = 0;
                                        break;
                                }
                                commonVideo.setZoom(zoomValue);
                            }
                            tabSurfaceStartTime = 0;
                        }
                        break;
                }
                return false;
            }
        });
        myll.setOnEventUPListener(new MyLinearLayout.OnEventUPListener() {
            @Override
            public void onEventUp(MotionEvent event) {
                int action = event.getAction();
                int height = 0;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isRecording) {
                            height = myll.getHeight() - bottomLayout.getHeight();
                            if (event.getY() < height) {
                                atRemove = true;
                                mProgressView.setRemove(atRemove);
                                mRecordTip.setVisibility(View.INVISIBLE);
                                mReleaseTip.setVisibility(View.VISIBLE);
                                mReleaseTip.measure(0, 0);

                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mReleaseTip.getLayoutParams();
                                if (event.getY() < mReleaseTip.getMeasuredHeight() * 2) {
                                    params.bottomMargin = height - mReleaseTip.getMeasuredHeight();
                                } else {
                                    params.bottomMargin = height - (int) event.getY() + mReleaseTip.getMeasuredHeight();
                                }
                                mReleaseTip.setLayoutParams(params);
                            } else {
                                atRemove = false;
                                mProgressView.setRemove(atRemove);
                                mRecordTip.setVisibility(View.VISIBLE);
                                mReleaseTip.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isRecording) {
                            height = myll.getHeight() - bottomLayout.getHeight();
                            commonVideo.stopVideo();
                            isRecording = false;
                            mRecordTip.setVisibility(View.INVISIBLE);
                            mReleaseTip.setVisibility(View.INVISIBLE);
                            if (event.getY() < height) {
                                mProgressView.setVisibility(View.INVISIBLE);
                                resetTimer();
                            } else {
                                if (recordDuration < 1200) {
                                    mProgressView.setVisibility(View.INVISIBLE);
                                    resetTimer();
                                    if (recordDuration > 800)
                                        ToastUtil.showToast(getApplicationContext(), "视频时间太短");
                                } else {
                                    retVideo(lastPath, recordDuration / 1000);
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        mBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        file = new File(EMWApplication.videoPath + generateFileName());
                        lastPath = file.getAbsolutePath();
                        commonVideo.startVideo(file);
                        isRecording = true;
                        atRemove = false;
                        mProgressView.setRemove(atRemove);
                        mProgressView.setVisibility(View.VISIBLE);
                        mRecordTip.setVisibility(View.VISIBLE);
                        updateProgress();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        atRemove = true;
                        mProgressView.setRemove(atRemove);
                        break;
                }
                return false;//false的时候  setOnclickListener会响应，否则不响应
            }
        });
        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatVideoCreateActivity.this, TestVideoActivity.class);
                intent.putExtra("videoPath", lastPath);
                startActivity(intent);
            }
        });

        mProgressView.setMax(RECORD_TIME_MAX);

        autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                isAutoFocus = true;
                if (b) {
                    camera.cancelAutoFocus();
                    blinAnim();
                    mFocusTip.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFocusTip.setVisibility(View.INVISIBLE);
                        }
                    }, 1000);
                } else {
                }
            }
        };

        //注册传感器监听事件
        boolean result = sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        mSf.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                focusTipParams = (RelativeLayout.LayoutParams) mFocusTip.getLayoutParams();
                focusTipParams.leftMargin = mSf.getMeasuredWidth() / 2 - mFocusTip.getMeasuredWidth() / 2;
                focusTipParams.topMargin = mSf.getMeasuredHeight() / 2 - mFocusTip.getMeasuredHeight() / 2;
                mFocusTip.setLayoutParams(focusTipParams);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销传感器监听事件
        sensorManager.unregisterListener(sensorListener);
    }

    // 随机生成文件名字
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".mp4";
    }

    private void initSlidingLaout() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        ViewGroup.LayoutParams layoutParam1 = mDragView.getLayoutParams();
        layoutParam1.width = screenWidth;
        layoutParam1.height = (int) (screenWidth * (640 / (480 * 1f))) + DisplayUtil.dip2px(this, 95);
        mDragView.setLayoutParams(layoutParam1);
        mSlidingLayout.setScrollableViewHelper(new ScrollableViewHelper());
        mSlidingLayout.setDragView(R.id.dragView);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSlidingLayout.setAnchorPoint(1.0f);
                mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        }, 500);
        mSlidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel,
                                            SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    finish();
                }
            }
        });
        mSlidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }

    private void retVideo(String path, int time) {
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        isRecording = false;
        Intent intent = new Intent(ChatContent.REFRESH_CHAT_VIDEO_INFO);
        intent.putExtra("videoPath", path);
        intent.putExtra("videoTime", time);
        sendBroadcast(intent);
        finish();
    }

    private void updateProgress() {
        mTimeCount = 0;// 时间计数器重新赋值
        recordDuration = 0;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimeCount++;
                //当进度大于1/2时进度条变成红色
                recordDuration = mTimeCount * 10;
                mProgressView.setProgress(recordDuration);// 设置进度条
                mProgressView.postInvalidate();
                if (recordDuration >= RECORD_TIME_MAX) {// 达到指定时间
                    this.cancel();
                    commonVideo.stopVideo();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            retVideo(lastPath, recordDuration / 1000);
                        }
                    });

                }
            }
        }, 0, 10);
    }

    /**
     * 重置计时器
     */
    private void resetTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mProgressView.setProgress(0);
            mProgressView.setRemove(false);
        }
    }

    /**
     * 初始化加速传感器
     */
    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorListener = new SensorListener();
    }

    //加速传感器监听
    class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (commonVideo == null) {
                return;
            }
            float[] values = sensorEvent.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            if (!initBool) {
                mLastX = x;
                mLastY = y;
                mLastZ = z;
                initBool = !initBool;
            }
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);

            if (deltaX > .5 && isAutoFocus) {
                isAutoFocus = false;
                commonVideo.autoFocus(autoFocusCallback);
                mFocusTip.setVisibility(View.VISIBLE);
                scalAnim();
            }
            if (deltaY > .5 && isAutoFocus) {
                isAutoFocus = false;
                commonVideo.autoFocus(autoFocusCallback);
                mFocusTip.setVisibility(View.VISIBLE);
                scalAnim();
            }
            if (deltaZ > .5 && isAutoFocus) {
                isAutoFocus = false;
                commonVideo.autoFocus(autoFocusCallback);
                mFocusTip.setVisibility(View.VISIBLE);
                scalAnim();
            }
            mLastX = x;
            mLastY = y;
            mLastZ = z;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            //传感器精度变化时使用
        }
    }

    private void scalAnim() {
        //缩放动画
        /*
        2f表示动画的起始宽度是真实宽度的2倍
        1表示动画结束宽度是真实宽度的1倍
        2f表示动画的起始宽度是真实高度的2倍
        1表示动画结束宽度是真实高度的1倍
        后面参数表示缩放中心点的位置 默认是左上角
        */
        ScaleAnimation sa = new ScaleAnimation(2f, 1, 2f, 1, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(500);
        sa.setFillAfter(true);
        //透明动画
        //0表示完全透明 1表示不透明
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(500);
        //动画组
        //false 为使用默认的动画插补器
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(sa);
        set.addAnimation(aa);
        mFocusTip.startAnimation(set);
    }

    private void blinAnim() {
        //透明动画
        //0表示完全透明 1表示不透明
        AlphaAnimation aa = new AlphaAnimation(0, 0.7f);
        aa.setDuration(200);
        aa.setRepeatCount(2);
        mFocusTip.startAnimation(aa);
    }
}
