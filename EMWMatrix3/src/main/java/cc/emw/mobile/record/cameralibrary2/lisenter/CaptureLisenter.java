package cc.emw.mobile.record.cameralibrary2.lisenter;

/**
 * create by CJT2325
 * 445263848@qq.com.
 */

public interface CaptureLisenter {
    void takePictures();

    void recordShort(long time);

    void recordStart();

    void recordEnd(long time);

    void recordZoom(float zoom);
}
