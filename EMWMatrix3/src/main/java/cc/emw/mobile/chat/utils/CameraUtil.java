package cc.emw.mobile.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;

import java.util.List;

/**
 * Created by sunny.du on 2017/6/26.
 */

public class CameraUtil {
    private Camera mCamera;
    private CameraUtil(){
    }
    private static final CameraUtil util=new CameraUtil();
    public static final CameraUtil getCameraUtilInstance(){
        return util;
    }

    /** 安全获取Camera对象实例的方法 */
    public  Camera getCameraInstance(int cameraPosition) {
        Camera c = null;
        if(c != null) {
            try {
                c = Camera.open(cameraPosition); // 试图获取Camera实例
            } catch (Exception e) {
                // 摄像头不可用（正被占用或不存在）
            }
        }
        return c; // 不可用则返回null
    }

    /**
     * 设置Camera.Parameters~~~~~重要
     */
    public void setCameraParams(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = createCameraSize(parameters, width, height);
        /**根据选出的PictureSize重新设置SurfaceView大小*/
        parameters.setPictureSize(picSize.width, picSize.height);
        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            parameters.setPreviewSize(preSize.width, preSize.height);
        }
        // 设置照片质量
        parameters.setJpegQuality(100);
        //连续自动对焦模式
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            // 连续对焦模式
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        //自动对焦。
        mCamera.cancelAutoFocus();
        mCamera.setParameters(parameters);
    }
    /**
     * 获取当前手机的方向
     */
    public int getPreviewDegree(Context context) {
        int degree = 0;
        // 获得手机的方向
        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }
    /**
     * 从列表中选取合适的分辨率
     */
    public Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }
        if (null == result) {
            pictureSizeList.get(0);
//            for (Camera.Size size : pictureSizeList) {
//                float curRatio = ((float) size.width) / size.height;
//                if (curRatio == 4f / 3) {// 默认w:h = 4:3
//                    result = size;
//                    break;
//                }
//            }
        }
        return pictureSizeList.get(0);
    }
    /**
     * List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
     * size.width=176  size.height=144
     * size.width=320  size.height=240
     * size.width=352  size.height=288
     * size.width=480  size.height=320
     * size.width=480  size.height=368
     * size.width=640  size.height=480
     * size.width=720  size.height=480
     * size.width=800  size.height=480
     * size.width=800  size.height=600
     * size.width=864  size.height=480
     * size.width=960  size.height=540
     * size.width=1280  size.height=720
     * size.width=720  size.height=720
     */
    public Camera.Size createCameraSize(Camera.Parameters parameters, int width, int height) {
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            picSize = parameters.getPictureSize();
        }
        return picSize;
    }
}
