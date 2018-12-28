package cc.emw.mobile.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

import cc.emw.mobile.util.Config;
import cc.emw.mobile.util.Logger;

/**
 * Created by jven.wu on 2016/8/23.
 */
public class VideoPlayerView extends TextureView
    implements TextureView.SurfaceTextureListener{

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private final String TAG = this.getClass().getSimpleName();

    public VideoPlayerView(Context context) {
        super(context);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (SHOW_LOGS)
            Logger.v(TAG, "onSurfaceTextureAvailable()-> width " + width + ", height " + height + ", this " + this);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (SHOW_LOGS) Logger.v(TAG, "onSurfaceTextureDestroyed()-> surface " + surface);
        surface.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
