package cc.emw.mobile.record.cameralibrary2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.record.cameralibrary2.util.AngleUtil;
import cc.emw.mobile.record.cameralibrary2.util.CameraParamUtil;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/4/25
 * 描    述：
 * =====================================
 */
@SuppressWarnings("deprecation")
public class CameraInterface {

    private static final String TAG = "CJT";

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;

    boolean isPreviewing() {
        return isPreviewing;
    }

    private static CameraInterface mCameraInterface;

    private int SELECTED_CAMERA = -1;
    private int CAMERA_POST_POSITION = -1;
    private int CAMERA_FRONT_POSITION = -1;

    private SurfaceHolder mHolder = null;
    private float screenProp = -1.0f;

    private boolean isRecorder = false;
    private MediaRecorder mediaRecorder;
    private String videoFileName;
    private String saveVideoPath;
    private String videoFileAbsPath;


    private int preview_width;
    private int preview_height;


    private int angle = 0;
    private SensorManager sm = null;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
                return;
            }
            float[] values = event.values;
            angle = AngleUtil.getSensorAngle(values[0], values[1]);
//            float ax = values[0];
//            float ay = values[1];
//            float az = values[2];
//            Log.i("CJT", "x = " + ax + " y = " + ay);
//            Log.i("CJT", "现在的角度为 = " + AngleUtil.getSensorAngle(ax, ay));
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    public void setSaveVideoPath(String saveVideoPath) {
        this.saveVideoPath = saveVideoPath;
        File file = new File(saveVideoPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    int nowScaleRate = 0;

    public void setZoom(float zoom) {
        if (!isRecorder || mCamera == null) {
            return;
        }
        if (mParams == null) {
            mParams = mCamera.getParameters();
        }
        if (!mParams.isZoomSupported()) {
            return;
        }
        if (zoom >= 0) {
            int scaleRate = (int) (zoom / 50);
            if (scaleRate < mParams.getMaxZoom() && scaleRate >= 0 && nowScaleRate != scaleRate) {
                mParams.setZoom(scaleRate);
                mCamera.setParameters(mParams);
                nowScaleRate = scaleRate;
//                Log.i("CJT", "zoom = " + nowScaleRate);
            }
        }
    }

    interface CamOpenOverCallback {
        void cameraHasOpened();
    }

    private CameraInterface() {
        findAvailableCameras();
        SELECTED_CAMERA = CAMERA_POST_POSITION;
        saveVideoPath = "";
    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    /**
     * open Camera
     */
    void doOpenCamera(CamOpenOverCallback callback) {
        if (mCamera == null) {
            openCamera(SELECTED_CAMERA);
        }
        callback.cameraHasOpened();
    }

    private void openCamera(int id) {
        mCamera = Camera.open(id);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mCamera.enableShutterSound(false);
        }
    }

    public synchronized void switchCamera() {
        if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
            SELECTED_CAMERA = CAMERA_FRONT_POSITION;
        } else {
            SELECTED_CAMERA = CAMERA_POST_POSITION;
        }
        doStopCamera();
        mCamera = Camera.open(SELECTED_CAMERA);
        doStartPreview(mHolder, screenProp);
    }

    /**
     * doStartPreview
     */
    void doStartPreview(SurfaceHolder holder, float screenProp) {
        if (this.screenProp < 0) {
            this.screenProp = screenProp;
        }
        if (holder == null) {
            return;
        }
        this.mHolder = holder;
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mParams = mCamera.getParameters();
                Camera.Size previewSize = CameraParamUtil.getInstance().getPreviewSize(mParams
                        .getSupportedPreviewSizes(), 1000, screenProp);
                Camera.Size pictureSize = CameraParamUtil.getInstance().getPictureSize(mParams
                        .getSupportedPictureSizes(), 1200, screenProp);

                mParams.setPreviewSize(previewSize.width, previewSize.height);

                preview_width = previewSize.width;
                preview_height = previewSize.height;

                mParams.setPictureSize(pictureSize.width, pictureSize.height);

                if (CameraParamUtil.getInstance().isSupportedFocusMode(mParams.getSupportedFocusModes(), Camera
                        .Parameters.FOCUS_MODE_AUTO)) {
                    mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                if (CameraParamUtil.getInstance().isSupportedPictureFormats(mParams.getSupportedPictureFormats(),
                        ImageFormat.JPEG)) {
                    mParams.setPictureFormat(ImageFormat.JPEG);
                    mParams.setJpegQuality(100);
                }
                mCamera.setParameters(mParams);
                mParams = mCamera.getParameters();

                /**
                 * SurfaceView
                 */
                mCamera.setPreviewDisplay(holder);

                /**
                 * TextureView
                 */
//                surface.setDefaultBufferSize(previewSize.width, previewSize.height);
//                mCamera.setPreviewTexture(holder);

                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
                isPreviewing = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "=== Start Preview ===");
    }

    /**
     * 停止预览，释放Camera
     */
    void doStopCamera() {
        if (null != mCamera) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewDisplay(null);
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
                Log.i(TAG, "=== Stop Camera ===");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 拍照
     */

    void takePicture(final TakePictureCallback callback) {
        final int nowAngle = (angle + 90) % 360;
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                    matrix.setRotate(nowAngle);
                } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                    matrix.setRotate(270);
                    matrix.postScale(-1, 1);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (callback != null) {
                    callback.captureResult(bitmap);
                }
            }
        });
    }

    void startRecord(Surface surface) {
        if (isRecorder) {
            return;
        }
        int nowAngle = (angle + 90) % 360;
        if (mCamera == null) {
            openCamera(SELECTED_CAMERA);
        }
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        if (mParams == null) {
            mParams = mCamera.getParameters();
        }
        List<String> focusModes = mParams.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(mParams);
        mCamera.unlock();
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        Camera.Size videoSize = null;
        if (mParams.getSupportedVideoSizes() == null) {
            videoSize = CameraParamUtil.getInstance().getPictureSize(mParams.getSupportedPreviewSizes(), 1000,
                    screenProp);
        } else {
            videoSize = CameraParamUtil.getInstance().getPictureSize(mParams.getSupportedVideoSizes(), 1000,
                    screenProp);
        }

//        Log.i(TAG, "setVideoSize    width = " + videoSize.width + "height = " + videoSize.height);
        if (videoSize.width == videoSize.height) {
            mediaRecorder.setVideoSize(preview_width, preview_height);
        } else {
            mediaRecorder.setVideoSize(videoSize.width, videoSize.height);
        }
        if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
            mediaRecorder.setOrientationHint(270);
        } else {
            mediaRecorder.setOrientationHint(nowAngle);
//            mediaRecorder.setOrientationHint(90);
        }

        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
//        mediaRecorder.setPreviewDisplay(new Surface(textureView.getSurfaceTexture()));
        mediaRecorder.setPreviewDisplay(surface);

        videoFileName = "video_" + System.currentTimeMillis() + ".mp4";
        if (saveVideoPath.equals("")) {
            saveVideoPath = Environment.getExternalStorageDirectory().getPath();
        }
        videoFileAbsPath = saveVideoPath + File.separator + videoFileName;
        mediaRecorder.setOutputFile(videoFileAbsPath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecorder = true;
        } catch (IOException e) {
            e.printStackTrace();
            mediaRecorder.stop();
        }
    }

    void stopRecord(boolean isShort, StopRecordCallback callback) {
        if (!isRecorder) {
            return;
        }
        if (mediaRecorder != null && isRecorder) {
            try {
                mediaRecorder.stop();
                isRecorder = false;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mediaRecorder.release();
            mediaRecorder = null;
            if (isShort) {
                /**
                 * delete video file
                 */
                File file = new File(videoFileAbsPath);
                if (file.exists()) {
                    file.delete();
                }
                callback.recordResult(null);
                return;
            }
            doStopCamera();
            String fileName = saveVideoPath + "/" + videoFileName;
            callback.recordResult(fileName);
        }
    }

    private void findAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            switch (info.facing) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    CAMERA_FRONT_POSITION = info.facing;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    CAMERA_POST_POSITION = info.facing;
                    break;
            }
        }
    }


    public void handleFocus(final float x, final float y, final FocusCallback callback) {
        if (mCamera == null) {
            return;
        }
        final Camera.Parameters params = mCamera.getParameters();
        Camera.Size previewSize = params.getPreviewSize();
        Rect focusRect = calculateTapArea(x, y, 1f, previewSize);
        mCamera.cancelAutoFocus();
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            Log.i(TAG, "focus areas not supported");
            callback.focusSuccess();
            return;
        }
        final String currentFocusMode = params.getFocusMode();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        Log.i(TAG, "width = " + params.getPreviewSize().width + "height = " + params.getPreviewSize().height);

        mCamera.setParameters(params);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                try {
                    if (success) {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(currentFocusMode);
                        camera.setParameters(params);
                        callback.focusSuccess();
                    } else {
                        handleFocus(x, y, callback);
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    private static Rect calculateTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / previewSize.width - 1000);
        int centerY = (int) (y / previewSize.height - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF
                .bottom));
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }


    interface StopRecordCallback {
        void recordResult(String url);
    }

    interface TakePictureCallback {
        void captureResult(Bitmap bitmap);
    }

    interface FocusCallback {
        void focusSuccess();
    }


    public void registerSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager
                .SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.unregisterListener(sensorEventListener);
    }

}
