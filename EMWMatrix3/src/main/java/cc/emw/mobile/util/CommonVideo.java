package cc.emw.mobile.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.chat.function.utils.Utils;

/**
 * 拍摄视频的公共类
 *
 * @author jven
 */
@SuppressLint("NewApi")
public class CommonVideo implements SurfaceHolder.Callback {
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private final String TAG = this.getClass().getSimpleName();

    private MediaRecorder mediarecorder;// 录制视频的类
    // 用来显示视频
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private File mVideoFile;
    private Context mContext;
    private boolean recorrding = false;
    private SurfaceView mSurfaceView;
    private int screenWidth;
    /**
     * 构造方法
     * 初始化
     */
    public CommonVideo(Context context, SurfaceView surfaceview) {
        mContext = context;
        init(context, surfaceview);
    }

    /**
     * @des  初始化摄像头和视频相关信息 surfaceview 显示视频的控件
     * @cal   构造类的时候
     */
    private void init(Context context, SurfaceView surfaceview) {
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        ((Activity) context).getWindow().setFormat(PixelFormat.TRANSLUCENT);
        this.mSurfaceView = surfaceview;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels; //Find screen dimensions
        //设置surface的宽高
        RelativeLayout.LayoutParams layoutParam1 = new RelativeLayout.LayoutParams(screenWidth, (int) (screenWidth * (640 / (480 * 1f))));
        layoutParam1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        mSurfaceView.setLayoutParams(layoutParam1);
        SurfaceHolder holder = surfaceview.getHolder();// 取得holder
        holder.addCallback(this); // holder加入回调接口
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void startVideo(File videoFile) {
        try {
            mCamera.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //当videoFile不存在时先创建文件
        initFile(videoFile);
        mVideoFile = videoFile;
        if (mediarecorder == null) {
            mediarecorder = new MediaRecorder();
        } else {
            mediarecorder.reset();
        }

        mediarecorder.setCamera(mCamera);
        //初始化recorder参数
        initRecorder();


        // 设置视频文件输出的路径
        mediarecorder.setOutputFile(videoFile.getAbsolutePath());

        try {
            // 准备录制
            mediarecorder.prepare();
            // 开始录制
            mediarecorder.start();
            recorrding = true;
        } catch (Exception e) {
            if (SHOW_LOGS) Log.d(TAG, "startVideo()->Exception: " + e.getMessage());
        }

    }

    private void initRecorder() {
        // 设置从麦克风采集声音
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置录制视频源为Camera(相机)
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置视频文件的输出格式
        // 必须在设置声音编码格式、图像编码格式之前设置
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置声音编码格式
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // 设置录制的视频编码h263 h264
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoSize(640, 480);
        // 设置视频输出的格式和编码
        if (CamcorderProfile.hasProfile(FindBackCamera(), CamcorderProfile.QUALITY_QVGA)) {
            CamcorderProfile mProfile = CamcorderProfile
                    .get(FindBackCamera(), CamcorderProfile.QUALITY_QVGA);

            mediarecorder.setAudioEncodingBitRate(44100);
            if (mProfile.videoBitRate > 5 * 1024 * 1024) {
                // 很重要，提高视频清晰度
                mediarecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
            } else {
                mediarecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
            }
            // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
            // after setVideoSource(),after setOutFormat()
            mediarecorder.setVideoFrameRate(mProfile.videoFrameRate);
        }
        // 指定使用SurfaceView来预览视频
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
        // 设置录制视频的方向
        // 加了HTC的手机会有问题
        if(cameraPosition ==1) {
            mediarecorder.setOrientationHint(90);
        }else if(cameraPosition == 0){
            mediarecorder.setOrientationHint(270);
        }
    }

    /**
     * 结束拍摄视频
     *
     * @return 拍摄成功，返回true；拍摄失败，返回false
     */
    public boolean stopVideo() {
        recorrding = false;
        // 关闭预览并释放资源
        if (mediarecorder != null) {
            try {
                // 停止录制
                mediarecorder.stop();
                // 释放资源
                mediarecorder.release();
                mediarecorder = null;
                return true;
            } catch (Exception e) {
                // 当拍摄时间太短时，会报异常，在此删除不能看的视频文件
                return false;
            } finally {
                if (mCamera != null) {
                    mCamera.lock();
                }
            }

        }

        return false;
    }

    /**
     *返回camera实例  用于外部释放资源   用于Activity类中
     */
    public Camera getCamera(){
        return mCamera;
    }
    public void setCamera(Camera camera){
        this.mCamera=camera;
    }
    /**
     * 判断后置摄像头是否存在
     *
     * @return
     */
    @SuppressLint("NewApi")
    private int FindBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        mCamera = getCameraInstance();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "surfaceCreated()->msg " + e.getMessage());
            if (mCamera != null) {
                mCamera.release();
            }
        }
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // surfaceDestroyed的时候同时对象设置为null
        surfaceHolder = null;
        mediarecorder = null;
        if(mCamera !=null) {
            mCamera.release();
        }
        mCamera = null;
    }

    private Camera getCameraInstance() {
        // 设置摄像头以及摄像头的方向
        int CammeraIndex = FindBackCamera();
        Log.d("lcy", "CammeraIndex.." + CammeraIndex);
        Camera mCamera = null;
        try {
            // 获取Camera实例
            mCamera = Camera.open(CammeraIndex);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
            mCamera.setDisplayOrientation(90);
        } catch (Exception e) {
            // 摄像头不可用（正被占用或不存在）
            Log.d(TAG, "摄像头不可用");
        }
        // 不可用则返回null
        return mCamera;
    }

    /**
     * 初始化应用文件夹目录
     */
    public static void initDir(File targetDir) {
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }

    }

    /**
     * 对目标文件进行判断，不存在，创建（先创建目录，再创建文件）
     *
     * @param targetFile
     */
    public static void initFile(File targetFile) {
        if (targetFile.exists()) {
            return;
        } else {
            File dir = targetFile.getParentFile();
            initDir(dir);
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setZoom(int zoomValue) {
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.isZoomSupported()) {
            final int MAX = parameters.getMaxZoom();
            if (MAX == 0)
                return;
            if (zoomValue > MAX)
                zoomValue = MAX;
            parameters.setZoom(zoomValue); // value zoom value. The valid range
            // is 0 to getMaxZoom.
            mCamera.setParameters(parameters);
        } else
            return;
    }
    public void autoFocus(Camera.AutoFocusCallback cb) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(parameters);
            mCamera.autoFocus(cb);
        }
    }
    /***/
    public boolean isCameraBack = true;//用于判断前后摄像头的标记
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    private int viewWidth = 0;
    private int viewHeight = 0;
    /**
     * @des 切换前置后置摄像头
     * @cal 点击切换按钮
     */
    public void change() {
        if (isCameraBack) {
            isCameraBack = false;
        } else {
            isCameraBack = true;
        }
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        deal();
                        mCamera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        deal();
                        mCamera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }
        }
    }
    public Camera deal() {
        if (mCamera != null) {
            Camera.Parameters p = mCamera.getParameters();
            setParameters(p);
            try {
                mCamera.setParameters(p);
            } catch (Exception e) {
                Camera.Size previewSize = findPreviewSizeByScreen(p);
                p.setPreviewSize(previewSize.width, previewSize.height);
                p.setPictureSize(previewSize.width, previewSize.height);
                try {
                    mCamera.setParameters(p);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return mCamera;
    }
    public void setParameters(Camera.Parameters p) {
        List<String> focusModes = p.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        long time = new Date().getTime();
        p.setGpsTimestamp(time);
        p.setPictureFormat(PixelFormat.JPEG);
        Camera.Size previewSize = findPreviewSizeByScreen(p);
        p.setPreviewSize(previewSize.width, previewSize.height);
        p.setPictureSize(previewSize.width, previewSize.height);
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);
            p.setRotation(90);
        }
    }
    private Camera.Size findPreviewSizeByScreen(Camera.Parameters parameters) {
        if (viewWidth != 0 && viewHeight != 0) {
            return mCamera.new Size(Math.max(viewWidth, viewHeight),Math.min(viewWidth, viewHeight));
        } else {
            return mCamera.new Size(Utils.getScreenWH(mContext).heightPixels,Utils.getScreenWH(mContext).widthPixels);
        }
    }

    /***/
}


/********
 * 原版本代码暂时保存，后续稳定后删除****sunnydu**11.21
 ****************************************************/
//
//package cc.emw.mobile.util;
//
//        import android.annotation.SuppressLint;
//        import android.app.Activity;
//        import android.content.Context;
//        import android.graphics.PixelFormat;
//        import android.hardware.Camera;
//        import android.media.CamcorderProfile;
//        import android.media.MediaRecorder;
//        import android.util.DisplayMetrics;
//        import android.util.Log;
//        import android.view.SurfaceHolder;
//        import android.view.SurfaceView;
//        import android.widget.RelativeLayout;
//
//        import java.io.File;
//        import java.io.IOException;
//        import java.util.List;
//
///**
// * 拍摄视频的公共类
// *
// * @author jven
// */
//@SuppressLint("NewApi")
//public class CommonVideo implements SurfaceHolder.Callback {
//    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
//    private final String TAG = this.getClass().getSimpleName();
//
//    private MediaRecorder mediarecorder;// 录制视频的类
//    // 用来显示视频
//    private SurfaceHolder surfaceHolder;
//    private Camera mCamera;
//    private File mVideoFile;
//    private Context mContext;
//    private boolean recorrding = false;
//    private SurfaceView mSurfaceView;
//    private int screenWidth;
//
//    public CommonVideo(Context context, SurfaceView surfaceview) {
//        mContext = context;
//        init(context, surfaceview);
//    }
//
//    // surfaceview 显示视频的控件
//    private void init(Context context, SurfaceView surfaceview) {
//        // 选择支持半透明模式,在有surfaceview的activity中使用。
//        ((Activity) context).getWindow().setFormat(PixelFormat.TRANSLUCENT);
//
//        this.mSurfaceView = surfaceview;
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        //Find screen dimensions
//        screenWidth = displaymetrics.widthPixels;
//
//
//        //设置surface的宽高
//        RelativeLayout.LayoutParams layoutParam1 = new RelativeLayout.LayoutParams(screenWidth, (int) (screenWidth * (640 / (480 * 1f))));
//        Log.d(TAG, "init()->lpWidth: " + layoutParam1.width + " lpHeight: " + layoutParam1.height);
//        layoutParam1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//        mSurfaceView.setLayoutParams(layoutParam1);
//        SurfaceHolder holder = surfaceview.getHolder();// 取得holder
//        holder.addCallback(this); // holder加入回调接口
//        // setType必须设置，要不出错.
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//    }
//
//    public void startVideo(File videoFile) {
//        try {
//            mCamera.unlock();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //当videoFile不存在时先创建文件
//        initFile(videoFile);
//        mVideoFile = videoFile;
//
//        if (mediarecorder == null) {
//            mediarecorder = new MediaRecorder();
//        } else {
//            mediarecorder.reset();
//        }
//
//        mediarecorder.setCamera(mCamera);
//        //初始化recorder参数
//        initRecorder();
//
//
//        // 设置视频文件输出的路径
//        mediarecorder.setOutputFile(videoFile.getAbsolutePath());
//
//        try {
//            // 准备录制
//            mediarecorder.prepare();
//            // 开始录制
//            mediarecorder.start();
//            recorrding = true;
//        } catch (Exception e) {
//            if (SHOW_LOGS) Log.d(TAG, "startVideo()->Exception: " + e.getMessage());
//        }
//
//    }
//
//    private void initRecorder() {
//        // 设置从麦克风采集声音
//        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        // 设置录制视频源为Camera(相机)
//        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        // 设置视频文件的输出格式
//        // 必须在设置声音编码格式、图像编码格式之前设置
//        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
//        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        // 设置声音编码格式
//        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        // 设置录制的视频编码h263 h264
//        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
//        // after setVideoSource(),after setOutFormat()
////        mediarecorder.setVideoSize(640, 480);
//        mediarecorder.setVideoSize(640, 480);
//        // 设置视频输出的格式和编码
//        if (CamcorderProfile.hasProfile(FindBackCamera(), CamcorderProfile.QUALITY_QVGA)) {
//            CamcorderProfile mProfile = CamcorderProfile
//                    .get(FindBackCamera(), CamcorderProfile.QUALITY_QVGA);
//
//            mediarecorder.setAudioEncodingBitRate(44100);
//            if (mProfile.videoBitRate > 5 * 1024 * 1024) {
//                // 很重要，提高视频清晰度
//                mediarecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
//            } else {
//                mediarecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
//            }
//            // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
//            // after setVideoSource(),after setOutFormat()
//            mediarecorder.setVideoFrameRate(mProfile.videoFrameRate);
//        }
//        // 指定使用SurfaceView来预览视频
//        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
//        // 设置录制视频的方向
//        // 加了HTC的手机会有问题
//        mediarecorder.setOrientationHint(90);
//    }
//
//    /**
//     * 结束拍摄视频
//     *
//     * @return 拍摄成功，返回true；拍摄失败，返回false
//     */
//    public boolean stopVideo() {
//        recorrding = false;
//        // 关闭预览并释放资源
//        if (mediarecorder != null) {
//            // 设置后不会崩（不管用）
//            // mediarecorder.setOnErrorListener(null);
//            // mediarecorder.setPreviewDisplay(null);
//
//            try {
//                // 停止录制
//                mediarecorder.stop();
//                // 释放资源
//                mediarecorder.release();
//                mediarecorder = null;
//                return true;
//            } catch (Exception e) {
////				LogUtil.i("wpc", "Exception");
//                // 当拍摄时间太短时，会报异常，在此删除不能看的视频文件
////				FileAccessor.delFile(mVideoFile.getAbsolutePath());
////				ToastUtil.showMessage("拍摄时间太短，请重新拍摄");
//                return false;
//            } finally {
//                if (mCamera != null) {
//                    mCamera.lock();
////                    mCamera.stopPreview();
////                    mCamera.release();
////                    mCamera = null;
//                }
//            }
//
//        }
//
//        return false;
//    }
//
//    /**
//     * 判断前置摄像头是否存在
//     *
//     * @return
//     */
//    @SuppressLint("NewApi")
//    private int FindFrontCamera() {
//        int cameraCount = 0;
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        cameraCount = Camera.getNumberOfCameras(); // get cameras number
//
//        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
//            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
//            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
//                return camIdx;
//            }
//        }
//        return -1;
//    }
//
//    /**
//     * 判断后置摄像头是否存在
//     *
//     * @return
//     */
//    @SuppressLint("NewApi")
//    private int FindBackCamera() {
//        int cameraCount = 0;
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        cameraCount = Camera.getNumberOfCameras(); // get cameras number
//
//        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
//            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
//            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
//                return camIdx;
//            }
//        }
//        return -1;
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width,
//                               int height) {
//        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
//        surfaceHolder = holder;
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
//        mCamera = getCameraInstance();
////        mCamera = Camera.open();
//        try {
//            mCamera.setPreviewDisplay(holder);
//            mCamera.startPreview();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d(TAG, "surfaceCreated()->msg " + e.getMessage());
//            if (mCamera != null) {
//                mCamera.release();
//            }
//        }
//        surfaceHolder = holder;
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        // surfaceDestroyed的时候同时对象设置为null
////        mediarecorder.release();
//        surfaceHolder = null;
//        mediarecorder = null;
//        mCamera.release();
//        mCamera = null;
//    }
//
//    private Camera getCameraInstance() {
//        // 设置摄像头以及摄像头的方向
//        int CammeraIndex = FindBackCamera();
//        Log.d("lcy", "CammeraIndex.." + CammeraIndex);
//        Camera mCamera = null;
//        try {
//            // 获取Camera实例
//            mCamera = Camera.open(CammeraIndex);
//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
//
////            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();  //获取系统的size集合
////            Camera.Size optimalSize = getOptimalPreviewSize(sizes, mSurfaceView.getHeight(), mSurfaceView.getWidth()); //根据surfaceview控件的比例选择size
////            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
////            mCamera.setParameters(parameters);
//
//            mCamera.setDisplayOrientation(90);
//
////            mCamera.unlock();
//        } catch (Exception e) {
//            // 摄像头不可用（正被占用或不存在）
////			Toast.makeText(mContext,"摄像头不可用",Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "摄像头不可用");
//        }
//        // 不可用则返回null
//        return mCamera;
//    }
//
//    /**
//     * 初始化应用文件夹目录
//     */
//    public static void initDir(File targetDir) {
//        if (!targetDir.exists()) {
//            targetDir.mkdir();
//        }
//
//    }
//
//    /**
//     * 对目标文件进行判断，不存在，创建（先创建目录，再创建文件）
//     *
//     * @param targetFile
//     */
//    public static void initFile(File targetFile) {
//        if (targetFile.exists()) {
//            return;
//        } else {
//            File dir = targetFile.getParentFile();
//            initDir(dir);
//            try {
//                targetFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void setZoom(int zoomValue) {
//        Camera.Parameters parameters = mCamera.getParameters();
//        if (parameters.isZoomSupported()) {
//            final int MAX = parameters.getMaxZoom();
//            if (MAX == 0)
//                return;
//            if (zoomValue > MAX)
//                zoomValue = MAX;
//            parameters.setZoom(zoomValue); // value zoom value. The valid range
//            // is 0 to getMaxZoom.
//            mCamera.setParameters(parameters);
//        } else
//            return;
//    }
//
//    public boolean manualFocus(Camera.AutoFocusCallback cb, List<Camera.Area> focusAreas) {
//        if (mCamera != null && focusAreas != null) {
//            try {
//                mCamera.cancelAutoFocus();
//                Camera.Parameters parameters = mCamera.getParameters();
//                if (parameters != null) {
//                    // getMaxNumFocusAreas检测设备是否支持
//                    if (parameters.getMaxNumFocusAreas() > 0) {
//                        parameters.setFocusAreas(focusAreas);
//                    }
//                    // getMaxNumMeteringAreas检测设备是否支持
//                    if (parameters.getMaxNumMeteringAreas() > 0)
//                        parameters.setMeteringAreas(focusAreas);
//                    parameters.setFocusMode("macro");
//                    mCamera.setParameters(parameters);
//                    mCamera.autoFocus(cb);
//                    return true;
//                }
//            } catch (Exception e) {
////                if (mOnErrorListener != null) {
////                    mOnErrorListener.onVideoError(
////                            MEDIA_ERROR_CAMERA_AUTO_FOCUS, 0);
////                }
//                if (e != null)
//                    Log.e(" ", "autoFocus", e);
//            }
//        }
//        return false;
//    }
//
//    public void autoFocus(Camera.AutoFocusCallback cb) {
//        if (mCamera != null) {
//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            mCamera.setParameters(parameters);
//            mCamera.autoFocus(cb);
//        }
//    }
//
//    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
//        final double ASPECT_TOLERANCE = 0.1;
//        double targetRatio = (double) w / h;
//        if (sizes == null) return null;
//
//        Camera.Size optimalSize = null;
//        double minDiff = Double.MAX_VALUE;
//
//        int targetHeight = h;
//
//        // Try to find an size match aspect ratio and size
//        for (Camera.Size size : sizes) {
//            double ratio = (double) size.width / size.height;
//            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
//            if (Math.abs(size.height - targetHeight) < minDiff) {
//                optimalSize = size;
//                minDiff = Math.abs(size.height - targetHeight);
//            }
//        }
//
//        // Cannot find the one match the aspect ratio, ignore the requirement
//        if (optimalSize == null) {
//            minDiff = Double.MAX_VALUE;
//            for (Camera.Size size : sizes) {
//                if (Math.abs(size.height - targetHeight) < minDiff) {
//                    optimalSize = size;
//                    minDiff = Math.abs(size.height - targetHeight);
//                }
//            }
//        }
//        return optimalSize;
//    }
//}
//
