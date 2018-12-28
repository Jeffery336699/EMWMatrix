package cc.emw.mobile.record;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.record.cameralibrary.JCameraView;
import cc.emw.mobile.record.cameralibrary.lisenter.ErrorLisenter;
import cc.emw.mobile.record.cameralibrary.lisenter.JCameraLisenter;
import cc.emw.mobile.record.cameralibrary.util.DeviceUtil;
import cc.emw.mobile.record.cameralibrary.util.FileUtil;

public class CameraActivity extends AppCompatActivity {
    private JCameraView jCameraView;

    public static final String EXTRA_INTO_FLAG = "into_tag";
    public static final int FLAG_NORMAL = 0; //默认
    public static final int FLAG_CHAT = 1; //从聊天进入
    public static final int FLAG_CONTACT = 2; //从圈子进入

    public static final String EXTRA_CAMERA_TYPE = "camera_type";
    public static final int TYPE_TAKE_RECORD = 0; //默认，点击拍照，长按录制
    public static final int TYPE_TAKE = 1; //仅能点击拍照
    public static final int TYPE_RECORD = 2; //仅能长按录制
    public static final String RESULT_DATA = "filePath";
    private int flag, type;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        flag = getIntent().getIntExtra(EXTRA_INTO_FLAG, 0);
        type = getIntent().getIntExtra(EXTRA_CAMERA_TYPE, 0);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(CameraActivity.this, "请检查相机权限~", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        jCameraView = (JCameraView) findViewById(R.id.jcameraview);

        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        if (type == TYPE_TAKE) {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);
            jCameraView.setTip("点击拍照");
        } else if (type == TYPE_RECORD) {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
            jCameraView.setTip("长按摄像");
        } else {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);
            jCameraView.setTip("轻触拍照，长按摄像");
        }
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new ErrorLisenter() {
            @Override
            public void onError() {
                //错误监听
                Log.i("CJT", "camera error");
                /*Intent intent = new Intent();
                setResult(103, intent);
                finish();*/
                handler.sendEmptyMessage(1);
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(CameraActivity.this, "给点录音权限可以?", Toast.LENGTH_SHORT).show();
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraLisenter() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
//                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
                /*String path = FileUtil.saveBitmap("JCamera", bitmap);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(101, intent);
                finish();*/
                /*long dataTake = System.currentTimeMillis();
                String jpegName = EMWApplication.tempPath + dataTake + ".jpg";
                cc.emw.mobile.record.cameralibrary2.util.FileUtil.saveBitmap(jpegName, bitmap);*/
                String jpegName = FileUtil.saveBitmap("ZKBRDir/.temp", bitmap);
                if (flag == FLAG_CHAT) { //sunnydu chatActivity
                    Intent intent = new Intent();
                    intent.putExtra("filePath", jpegName);
                    intent.setAction(ChatContent.REFRESH_CHAT_CAMARE_NEW_INFO);
                    sendBroadcast(intent);
                    onBackPressed();
                } else if (flag == FLAG_CONTACT) {
                    Intent intent = new Intent(CameraActivity.this, CameraCreateActivity.class);
                    intent.putExtra("filePath", jpegName);
                    intent.putExtra("jpegName", "file://" + jpegName);
                    startActivity(intent);
                    onBackPressed();
                } else {
                    Intent data = new Intent();
                    data.putExtra(RESULT_DATA, jpegName);
                    setResult(Activity.RESULT_OK, data);
                    onBackPressed();
                }
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                /*String path = FileUtil.saveBitmap("JCamera", firstFrame);
                Log.i("CJT", "url = " + url + ", Bitmap = " + path);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(101, intent);
                finish();*/
                if (flag == FLAG_CHAT) { //sunnydu chatActivity
                    Intent intent = new Intent();
                    intent.putExtra("video_url", url);
                    intent.setAction(ChatContent.REFRESH_CHAT_VIDEO_NEW_INFO);
                    sendBroadcast(intent);
                    onBackPressed();
                } else if (flag == FLAG_CONTACT) {
                    Intent intent = new Intent(CameraActivity.this, CameraCreateActivity.class);
                    intent.putExtra("start_anim", false);
                    intent.putExtra("video_url", url);
                    intent.putExtra("start_anim", false);
                    startActivity(intent);
                    onBackPressed();
                } else {
                    Intent data = new Intent();
                    data.putExtra(RESULT_DATA, url);
                    setResult(Activity.RESULT_OK, data);
                    onBackPressed();
                }
            }

            @Override
            public void quit() {
                //退出按钮
                CameraActivity.this.finish();
            }
        });
        getPermissions();
        Log.i("CJT", DeviceUtil.getDeviceModel());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        Log.i("CJT", "onResume");
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("CJT", "onPause");
        super.onPause();
        jCameraView.onPause();
    }

    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager
                            .PERMISSION_GRANTED) {

            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
            }
        } else {

        }
    }
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!recordPermissionGranted) {
                    size++;
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!cameraPermissionGranted) {
                    size++;
                }
                if (size == 0) {

                } else {
                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
