package cc.emw.mobile.record;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.base.ChatContent;

import cc.emw.mobile.record.cameralibrary2.JCameraView;
import cc.emw.mobile.record.cameralibrary2.lisenter.JCameraLisenter;
import cc.emw.mobile.record.cameralibrary2.util.FileUtil;

public class CameraActivity2 extends BaseActivity {

    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码

    JCameraView jCameraView;

    public static final String EXTRA_INTO_FLAG = "into_tag";
    public static final int FLAG_NORMAL = 0; //默认
    public static final int FLAG_CHAT = 1; //从聊天进入
    public static final int FLAG_CONTACT = 2; //从圈子进入

    public static final String EXTRA_CAMERA_TYPE = "camera_type";
    public static final int TYPE_TAKE_RECORD = 0; //默认，点击拍照，长按录制
    public static final int TYPE_TAKE = 1; //仅能点击拍照
    public static final int TYPE_RECORD = 2; //仅能长按录制
    private int flag, type;
    public static final String RESULT_DATA = "filePath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        flag = getIntent().getIntExtra(EXTRA_INTO_FLAG, 0);
        type = getIntent().getIntExtra(EXTRA_CAMERA_TYPE, 0);
        jCameraView = (JCameraView) findViewById(R.id.jcameraview);

        if (type == TYPE_TAKE) {
            jCameraView.setRecordEnabled(false);
        } else if (type == TYPE_RECORD) {
            jCameraView.setTakePictureEnabled(false);
        }

        /**
         * 关闭声音
         */
        jCameraView.forbiddenAudio(true);
        /**
         * 设置视频保存路径
         */
//        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        jCameraView.setSaveVideoPath(EMWApplication.tempPath);
        /**
         * JCameraView监听
         */
        jCameraView.setJCameraLisenter(new JCameraLisenter() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                long dataTake = System.currentTimeMillis();
                String jpegName = EMWApplication.tempPath + dataTake + ".jpg";
                FileUtil.saveBitmap(jpegName, bitmap);
                if (flag == FLAG_CHAT) { //sunnydu chatActivity
                    Intent intent = new Intent();
                    intent.putExtra("filePath", jpegName);
                    intent.setAction(ChatContent.REFRESH_CHAT_CAMARE_NEW_INFO);
                    sendBroadcast(intent);
                    onBackPressed();
                } else if (flag == FLAG_CONTACT) {
                    Intent intent = new Intent(CameraActivity2.this, CameraCreateActivity.class);
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
            public void recordSuccess(String url) {
                if (flag == FLAG_CHAT) { //sunnydu chatActivity
                    Intent intent = new Intent();
                    intent.putExtra("video_url", url);
                    intent.setAction(ChatContent.REFRESH_CHAT_VIDEO_NEW_INFO);
                    sendBroadcast(intent);
                    onBackPressed();
                } else if (flag == FLAG_CONTACT) {
                    Intent intent = new Intent(CameraActivity2.this, CameraCreateActivity.class);
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
                /**
                 * 退出按钮
                 */
                CameraActivity2.this.finish();
            }
        });
        /**
         * 6.0动态权限获取
         */
        getPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 全屏显示
         */
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
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(CameraActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CameraActivity2.this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat
                    .checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //具有权限
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(CameraActivity2.this, new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA},
                        GET_PERMISSION_REQUEST);
            }

        } else {
            //具有权限
        }
    }

    /**
     * 获取内存权限回调
     */
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            if (grantResults.length >= 1) {
                //获取读写内存的权限
                int writeResult = grantResults[0];//读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (writeGranted) {
                    //具备权限
                } else {

                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    }
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (recordPermissionGranted) {
                    //具备权限
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    }
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (cameraPermissionGranted) {
                    //具备权限
                } else {
                    //不具有相关权限
                    Toast.makeText(this, "拍照被禁止，部分功能将失效，请到设置中开启。", Toast.LENGTH_SHORT).show();

                    if (!shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                        //如果用户勾选了不再提醒，则返回false
                        Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
