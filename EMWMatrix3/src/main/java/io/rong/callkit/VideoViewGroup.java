package io.rong.callkit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;


/**
 *
 */
public class VideoViewGroup extends ViewGroup {

    private int mWidth, mHeight;//viewgroup的宽高

    private DisplayImageOptions options;

    public VideoViewGroup(Context context) {
        this(context, null);
    }

    public VideoViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void addChildView(String uid, SurfaceView video) {
        View childView = LayoutInflater.from(getContext()).inflate(R.layout.rc_voip_viewlet_remote_user, null);
        childView.setTag(uid);
        ImageView imageView = (ImageView) childView.findViewById(R.id.user_portrait);
        UserInfo userInfo = EMWApplication.personMap.get(Integer.valueOf(uid));
        if (userInfo != null) {
            String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
            if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                url = Const.DOWN_ICON_URL2 + userInfo.Image;
            }
            ImageLoader.getInstance().displayImage(url, imageView, options);
        } else {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(R.drawable.cm_img_head);
        }
        if (video != null) {
            childView.findViewById(R.id.user_status).setVisibility(View.GONE);
            FrameLayout remoteVideoView = (FrameLayout) childView.findViewById(R.id.viewlet_remote_video_user);
            remoteVideoView.removeAllViews();
            if (video.getParent() != null) {
                ((ViewGroup) video.getParent()).removeView(video);
            }
            remoteVideoView.addView(video, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            remoteVideoView.setVisibility(View.VISIBLE);
            remoteVideoView.setTag(uid);
        } else {
            childView.findViewById(R.id.user_status).setVisibility(View.VISIBLE);
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(childView, "scaleX",0.5f, 1f).setDuration(300);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(childView, "scaleY",0.5f, 1f).setDuration(300);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
        addView(childView);
    }

    public void addChildView(String uid) {
        addChildView(uid, null);
    }

    public void removeChildView(String uid) {
        final View child = findViewWithTag(uid);
        if (child != null) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(child, "scaleX",1f, 0.5f).setDuration(300);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(child, "scaleY",1f, 0.5f).setDuration(300);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(scaleX).with(scaleY);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    removeView(child);
                }
            });
            animatorSet.start();
        }
    }

    public void updateChildView(String uid, SurfaceView video) {
        View child = findViewWithTag(uid);
        if (child != null) {
            child.findViewById(R.id.user_status).setVisibility(View.GONE);

            FrameLayout remoteVideoView = (FrameLayout) child.findViewById(R.id.viewlet_remote_video_user);
            remoteVideoView.removeAllViews();
            if (video.getParent() != null) {
                ((ViewGroup) video.getParent()).removeView(video);
            }
            remoteVideoView.addView(video, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            remoteVideoView.setVisibility(View.VISIBLE);
            remoteVideoView.setTag(uid);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
//        mWidth = mHeight = Math.min(mWidth, mHeight);
        //测量每个children
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int childCount = this.getChildCount();
        int childWidth = 0;
        int childHeight = 0;
        int childWidthMeasureSpec = 0;
        int childHeightMeasureSpec = 0;
        for (int i = 0; i < childCount; i++) {
            switch (childCount) {
                case 1:
                case 2:
                    childWidth = mWidth/2;
                    childHeight = DisplayUtil.dip2px(getContext(), 170);
                    break;
                case 3:
                    childWidth = mWidth/2;
                    if (i == 0) {
                        childHeight = DisplayUtil.dip2px(getContext(), 280);
                    } else {
                        childHeight = DisplayUtil.dip2px(getContext(), 140);
                    }
                    break;
                case 4:
                    childWidth = mWidth/2;
                    childHeight = DisplayUtil.dip2px(getContext(), 140);
                    break;
                case 5:
                    if (i < 3) {
                        childWidth = mWidth/3;
                    } else {
                        childWidth = mWidth/2;
                    }
                    childHeight = DisplayUtil.dip2px(getContext(), 140);
                    break;
                case 6:
                    childWidth = mWidth/3;
                    childHeight = DisplayUtil.dip2px(getContext(), 140);
                    break;
                case 7:
                    if (i == 0) {
                        childWidth = mWidth;
                        childHeight = DisplayUtil.dip2px(getContext(), 120);
                    } else {
                        childWidth = mWidth/3;
                        childHeight = DisplayUtil.dip2px(getContext(), 100);
                    }
                    break;
                case 8:
                    if (i < 6) {
                        childWidth = mWidth/3;
                        childHeight = DisplayUtil.dip2px(getContext(), 100);
                    } else {
                        childWidth = mWidth/2;
                        childHeight = DisplayUtil.dip2px(getContext(), 120);
                    }
                    break;
                case 9:
                    childWidth = mWidth/3;
                    childHeight = DisplayUtil.dip2px(getContext(), 100);
                    break;
            }
            
            View childView = getChildAt(i);
            // 如果不希望系统自动测量子View,我们用以下的方式:  
             childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth,MeasureSpec.EXACTLY);
             childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight,MeasureSpec.EXACTLY);
             childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);  
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (childCount <= 2) {
                    if (i == 0) {
                        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else {
                        child.layout(mWidth/2, 0, mWidth/2 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    }
                } else if (childCount == 3) {
                    if (i == 0) {
                        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 1){
                        child.layout(mWidth/2, 0, mWidth/2 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else {
                        child.layout(mWidth/2, child.getMeasuredHeight(), mWidth/2 + child.getMeasuredWidth(), child.getMeasuredHeight() + child.getMeasuredHeight());
                    }
                } else if (childCount == 4) {
                    if (i == 0) {
                        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 1){
                        child.layout(mWidth/2, 0, mWidth/2 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 2){
                        child.layout(0, child.getMeasuredHeight(), child.getMeasuredWidth(), child.getMeasuredHeight() + child.getMeasuredHeight());
                    } else {
                        child.layout(mWidth/2, child.getMeasuredHeight(), mWidth/2 + child.getMeasuredWidth(), child.getMeasuredHeight() + child.getMeasuredHeight());
                    }
                } else if (childCount == 5) {
                    if (i == 0) {
                        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 1){
                        child.layout(mWidth/3, 0, mWidth/3 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 2){
                        child.layout(mWidth/3*2, 0, mWidth/3*2 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 3){
                        child.layout(0, child.getMeasuredHeight(), child.getMeasuredWidth(), child.getMeasuredHeight() + child.getMeasuredHeight());
                    } else {
                        child.layout(mWidth/2, child.getMeasuredHeight(), mWidth/2 + child.getMeasuredWidth(), child.getMeasuredHeight() + child.getMeasuredHeight());
                    }
                } else if (childCount == 6) {
                    if (i == 0) {
                        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 1){
                        child.layout(mWidth/3, 0, mWidth/3 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 2){
                        child.layout(mWidth/3*2, 0, mWidth/3*2 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 3){
                        child.layout(0, child.getMeasuredHeight(), child.getMeasuredWidth(), child.getMeasuredHeight() + child.getMeasuredHeight());
                    } else if (i == 4){
                        child.layout(mWidth/3, child.getMeasuredHeight(), mWidth/3 + child.getMeasuredWidth(), child.getMeasuredHeight() + child.getMeasuredHeight());
                    } else {
                        child.layout(mWidth/3*2, child.getMeasuredHeight(), mWidth/3*2 + child.getMeasuredWidth(), child.getMeasuredHeight() + child.getMeasuredHeight());
                    }
                } else if (childCount == 7) {
                    if (i == 0) {
                        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 1){
                        child.layout(0, DisplayUtil.dip2px(getContext(), 120), child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 120) + child.getMeasuredHeight());
                    } else if (i == 2){
                        child.layout(mWidth/3, DisplayUtil.dip2px(getContext(), 120), mWidth/3 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 120) + child.getMeasuredHeight());
                    } else if (i == 3){
                        child.layout(mWidth/3*2, DisplayUtil.dip2px(getContext(), 120), mWidth/3*2 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 120) + child.getMeasuredHeight());
                    } else if (i == 4){
                        child.layout(0, DisplayUtil.dip2px(getContext(), 220), child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 220) + child.getMeasuredHeight());
                    } else if (i == 5){
                        child.layout(mWidth/3, DisplayUtil.dip2px(getContext(), 220), mWidth/3 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 220) + child.getMeasuredHeight());
                    } else {
                        child.layout(mWidth/3*2, DisplayUtil.dip2px(getContext(), 220), mWidth/3*2 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 220) + child.getMeasuredHeight());
                    }
                } else if (childCount == 8) {
                    if (i == 0) {
                        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 1){
                        child.layout(mWidth/3, 0, mWidth/3 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 2){
                        child.layout(mWidth/3*2, 0, mWidth/3*2 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 3){
                        child.layout(0, DisplayUtil.dip2px(getContext(), 100), child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 100) + child.getMeasuredHeight());
                    } else if (i == 4){
                        child.layout(mWidth/3, DisplayUtil.dip2px(getContext(), 100), mWidth/3 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 100) + child.getMeasuredHeight());
                    } else if (i == 5){
                        child.layout(mWidth/3*2, DisplayUtil.dip2px(getContext(), 100), mWidth/3*2 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 100) + child.getMeasuredHeight());
                    } else if (i == 6){
                        child.layout(0, DisplayUtil.dip2px(getContext(), 200), child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 200) + child.getMeasuredHeight());
                    } else {
                        child.layout(mWidth/2, DisplayUtil.dip2px(getContext(), 200), mWidth/2 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 200) + child.getMeasuredHeight());
                    }
                } else {
                    if (i == 0) {
                        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 1){
                        child.layout(mWidth/3, 0, mWidth/3 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 2){
                        child.layout(mWidth/3*2, 0, mWidth/3*2 + child.getMeasuredWidth(), child.getMeasuredHeight());
                    } else if (i == 3){
                        child.layout(0, DisplayUtil.dip2px(getContext(), 100), child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 100) + child.getMeasuredHeight());
                    } else if (i == 4){
                        child.layout(mWidth/3, DisplayUtil.dip2px(getContext(), 100), mWidth/3 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 100) + child.getMeasuredHeight());
                    } else if (i == 5){
                        child.layout(mWidth/3*2, DisplayUtil.dip2px(getContext(), 100), mWidth/3*2 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 100) + child.getMeasuredHeight());
                    } else if (i == 6){
                        child.layout(0, DisplayUtil.dip2px(getContext(), 200), child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 200) + child.getMeasuredHeight());
                    } else if (i == 7){
                        child.layout(mWidth/3, DisplayUtil.dip2px(getContext(), 200), mWidth/3 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 200) + child.getMeasuredHeight());
                    } else {
                        child.layout(mWidth/3*2, DisplayUtil.dip2px(getContext(), 200), mWidth/3*2 + child.getMeasuredWidth(), DisplayUtil.dip2px(getContext(), 200) + child.getMeasuredHeight());
                    }
                }
            }
        } catch (Exception e) {

        }

    }

    private int measureSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 300;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;

    }

}
