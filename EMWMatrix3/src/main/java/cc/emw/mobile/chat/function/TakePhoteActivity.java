package cc.emw.mobile.chat.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.ShowPhotoActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.function.camare.CameraPreview;
import cc.emw.mobile.chat.function.camare.FocusView;
import cc.emw.mobile.chat.function.utils.ImageFileUtil;
import cc.emw.mobile.util.UiUtils;
import cc.emw.mobile.view.IconTextView;

/**
 * 相机拍照类。内置切换视频
 */
public class TakePhoteActivity extends Activity implements CameraPreview.OnCameraStatusListener, SensorEventListener {
    private static final String TAG = "TakePhoteActivity";
    public static final String PATH = EMWApplication.tempPath;
    private final int GET_PIC_FORM_LOCAL = 100;
    private boolean cropperLayoutShow = false;
    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    private boolean mInitialized = false;
    private SensorManager mSensorManager;
    private Sensor mAccel;
    private CameraPreview mCameraPreview;
    private RelativeLayout mTakePhotoLayout;
    private IconTextView mItChatCameraButVideo;
    private LinearLayout mLlBackPhote;
    private IconTextView mItvChatCameraBig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camare_activity_take_phote);
        mLlBackPhote = (LinearLayout) findViewById(R.id.ll_back_phote);
        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        FocusView focusView = (FocusView) findViewById(R.id.view_focus);
        mTakePhotoLayout = (RelativeLayout) findViewById(R.id.take_photo_layout);
        mItChatCameraButVideo = (IconTextView) findViewById(R.id.itv_chat_camera_but_video);
        mItvChatCameraBig= (IconTextView) findViewById(R.id.itv_chat_camera_big);
        mCameraPreview.setFocusView(focusView);
        mCameraPreview.setOnCameraStatusListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        UiUtils.setTouchDelegate(mLlBackPhote,50);
        UiUtils.setTouchDelegate(mItvChatCameraBig,50);
        mLlBackPhote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraPreview.getCamera() != null) {
                    mCameraPreview.getCamera().release();//释放资源
                    mCameraPreview.setCamera(null);//取消原来摄像头
                }
                finish();
                SystemClock.sleep(500);
            }
        });

        /**+
         * 跳转视频
         */
        findViewById(R.id.itv_chat_camera_but_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraPreview.getCamera() != null) {
                    mCameraPreview.getCamera().stopPreview();//停掉原来摄像头的预览
                    mCameraPreview.getCamera().release();//释放资源
                    mCameraPreview.setCamera(null);//取消原来摄像头
                }
                SystemClock.sleep(500);
                Intent videoIntent = new Intent(TakePhoteActivity.this, VideoCaptureActivity2.class);
                startActivity(videoIntent);
                finish();
            }
        });
        /**
         * 跳转图片选择界面
         */
        findViewById(R.id.itv_chat_camera_but_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change();
            }
        });
        /**
         * 跳转半屏拍照
         */
        mItvChatCameraBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraPreview.getCamera() != null) {
                    mCameraPreview.getCamera().stopPreview();//停掉原来摄像头的预览
                    mCameraPreview.getCamera().release();//释放资源
                    mCameraPreview.setCamera(null);//取消原来摄像头
                }
                SystemClock.sleep(500);
                Intent intent = new Intent(ChatContent.REFRESH_CHAT_CAMARE_INFO);
                sendBroadcast(intent);
                finish();
            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }




    /**
     * 写图片流到本地 并且切换到图片剪去界面
     */
    @Override
    public void onCameraStopped(byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        try {
            long time = System.currentTimeMillis();
            writeImage(bitmap, time);
            if (mCameraPreview.getCamera() != null) {
                mCameraPreview.getCamera().release();//释放资源
                mCameraPreview.setCamera(null);//取消原来摄像头
            }
            Intent intentImage = new Intent(TakePhoteActivity.this, ShowPhotoActivity.class);
            intentImage.putExtra("photo_uri", PATH + time + ".png");
            startActivity(intentImage);
            finish();
            SystemClock.sleep(500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }
        float deltaX = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);
        if (deltaX > 0.8 || deltaY > 0.8 || deltaZ > 0.8) {
            mCameraPreview.setFocus();
        }
        mLastX = x;
        mLastY = y;
        mLastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCameraPreview.getCamera() != null) {
                mCameraPreview.getCamera().release();//释放资源
                mCameraPreview.setCamera(null);//取消原来摄像头
            }
            finish();
            SystemClock.sleep(500);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*********
     * Utils
     ****************************************************************************************/
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头

    public void change() {
        int cameraCount = 0;
        //切换前后摄像头
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCameraPreview.getCamera().stopPreview();//停掉原来摄像头的预览
                    mCameraPreview.getCamera().release();//释放资源
                    mCameraPreview.setCamera(null);//取消原来摄像头
                    mCameraPreview.setCamera(Camera.open(i));//打开当前选中的摄像头
                    mCameraPreview.updateCameraParameters();
                    try {
                        mCameraPreview.getCamera().setPreviewDisplay(mCameraPreview.getSurfaceHolder());//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCameraPreview.getCamera().startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCameraPreview.getCamera().stopPreview();//停掉原来摄像头的预览
                    mCameraPreview.getCamera().release();//释放资源
                    mCameraPreview.setCamera(null);//取消原来摄像头
                    mCameraPreview.setCamera(Camera.open(i));//打开当前选中的摄像头
                    mCameraPreview.updateCameraParameters();
                    try {
                        mCameraPreview.getCamera().setPreviewDisplay(mCameraPreview.getSurfaceHolder());//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCameraPreview.getCamera().startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }

        }
    }



    private void writeImage(Bitmap bitmap, long time) throws IOException {
        OutputStream outputStream = null;
        try {
            File dir = new File(PATH);
            if (ImageFileUtil.isFileExists(dir, 1)) {
                File file = new File(PATH, time + ".png");
                if(ImageFileUtil.isFileExists(file,2)) {
                    outputStream = new FileOutputStream(file);
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

/*********事件监听方法****************************************************************************************/
    /**
     * 拍照
     */
    public void takePhoto(View view) {
        if (mCameraPreview != null) {
            mCameraPreview.takePicture();
        }
    }

    /*********
     * 生命周期方法
     ****************************************************************************************/
    @Override
    protected void onDestroy() {
        if (mCameraPreview.getCamera() != null) {
            mCameraPreview.getCamera().stopPreview();//停掉原来摄像头的预览
            mCameraPreview.getCamera().release();//释放资源
            mCameraPreview.setCamera(null);//取消原来摄像头
            SystemClock.sleep(500);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        if (mCameraPreview.getCamera() != null) {
            mCameraPreview.getCamera().stopPreview();//停掉原来摄像头的预览
            mCameraPreview.getCamera().release();//释放资源
            mCameraPreview.setCamera(null);//取消原来摄像头
            SystemClock.sleep(500);
        }
        super.onStop();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}