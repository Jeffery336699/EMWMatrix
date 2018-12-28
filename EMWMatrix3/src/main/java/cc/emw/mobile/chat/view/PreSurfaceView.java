package cc.emw.mobile.chat.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;
import java.util.List;

import cc.emw.mobile.chat.utils.CameraUtil;
import cc.emw.mobile.util.DisplayUtil;

/**
 * Created by sunny.du on 2017/6/26.
 *
 */
public class PreSurfaceView extends TextureView implements TextureView.SurfaceTextureListener {
    private Camera mCamera;
    private Context mContext;
    private CameraUtil util;
    private int mScreenWidth;//预览画面的宽度
    private int mScreenHeight;//预览画面的高度
    private int cameraPosition = 0;//0代表前置摄像头，1代表后置摄像头
    public int getCameraPosition(){
        return cameraPosition;
    }
    public PreSurfaceView(Context context) {
        this(context,null);
    }

    public PreSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PreSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(this);
        initData(context);
        setAlpha(1);
    }


    /**
     * 关闭相机
     * photoAndCameraAdapter.getPreSurfaceView().closeCamera();
     */
    public void closeCamera() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                SystemClock.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开相机
     * photoAndCameraAdapter.getPreSurfaceView().openCamera();
     */
    public void openCamera() {
        if (mCamera == null) {
            mCamera = Camera.open(cameraPosition);//开启相机
        }
        try {
            setCameraParams(mScreenWidth,mScreenHeight);
            mCamera.setPreviewTexture(surface);//摄像头画面显示在Surface上
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private SurfaceTexture surface;
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface=surface;
        Log.d("sunny----->","onSurfaceTextureAvailable=");
        new Thread() {
            @Override
            public void run() {
                openCamera();
            }
        }.start();

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d("sunny----->","onSurfaceTextureSizeChanged=");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d("sunny----->","onSurfaceTextureDestroyed=");
        new Thread() {
            @Override
            public void run() {
                closeCamera();
            }
        }.start();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.d("sunny----->","onSurfaceTextureUpdated=");
    }
    private OnCameraStatusListener listener;

    public void setOnCameraStatusListener(OnCameraStatusListener listener) {
        this.listener = listener;
    }

    public interface OnCameraStatusListener {
        void onCameraStopped(byte[] data);
    }
    /**********************************************
     * 创建jpeg图片回调数据对象
     */
    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                camera.stopPreview();
            } catch (Exception e) {
            }
            if (null != listener) {
                listener.onCameraStopped(data);
            }
        }
    };
    public void takePicture() {//works
        try {
            // 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
            if (mCamera == null)
                mCamera= Camera.open(cameraPosition);
                //设置参数,并拍照
                setCameraParams(mScreenWidth, mScreenHeight);
                mCamera.takePicture(null, null, jpeg);

        } catch (Exception x) {

        }
    }


    /**
     * 设置Camera.Parameters~~~~~重要
     */
    private void setCameraParams(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = util.createCameraSize(parameters, width, height);
        /**根据选出的PictureSize重新设置SurfaceView大小*/
        parameters.setPictureSize(picSize.width, picSize.height);
        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Camera.Size preSize = util.getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            parameters.setPreviewSize(preSize.width, preSize.height);
        }
        // 设置照片质量
        parameters.setJpegQuality(30);
        //连续自动对焦模式
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            // 连续对焦模式
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        //自动对焦。
        mCamera.cancelAutoFocus();
        mCamera.setDisplayOrientation(util.getPreviewDegree(mContext));
        mCamera.setParameters(parameters);
    }
    private void initData(Context context) {
        mContext = context;
        mScreenWidth = DisplayUtil.dip2px(mContext, 146);
        mScreenHeight = DisplayUtil.dip2px(mContext, 220);
        util=CameraUtil.getCameraUtilInstance();
        SystemClock.sleep(50);
        mCamera = Camera.open(cameraPosition);
    }

    /**
     * 转换相机方向
     */
    public void changeOrientation() {
        Log.d("sunny----->","changeOrientation=start");
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 0) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    closeCamera();
                    cameraPosition = 1;
                    openCamera();
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    closeCamera();
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    cameraPosition = 0;
                    openCamera();
                    break;
                }
            }

        }
    }
}
