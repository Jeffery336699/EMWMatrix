package io.rong.callkit;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


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
 * Created by Mr_immortalZ on 2016/5/2.
 * email : mr_immortalz@qq.com
 */
public class RadarViewGroup extends ViewGroup {
    private int mWidth, mHeight;//viewgroup的宽高
    private int mCenterX, mCenterY, mCircleR;
    private static float[] circleProportion = {1 / 9f, 2 / 9f, 3 / 9f, 4 / 9f};
    private static double[] circleAngdeg = {0,0, 0,90,180,270, 45,135,225,315, 0,90,180,270};
    private DisplayImageOptions options;

    public RadarViewGroup(Context context) {
        this(context, null);
    }

    public RadarViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        addCenterIcon();
    }

    private void addCenterIcon() {
        CircleImageView imageView = new CircleImageView(getContext());
        int imgWH = DisplayUtil.dip2px(getContext(), 60);
        imageView.setLayoutParams(new LayoutParams(imgWH, imgWH));
        imageView.setId(android.R.id.icon);
        imageView.setBorderWidth(DisplayUtil.dip2px(getContext(), 2));
        imageView.setBorderColor(Color.WHITE);
        imageView.setShadow(DisplayUtil.dip2px(getContext(), 5));
        UserInfo userInfo = PrefsUtil.readUserInfo();
        if (userInfo != null) {
            String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
            if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                url = Const.DOWN_ICON_URL2 + userInfo.Image;
            }
            imageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 60);
            ImageLoader.getInstance().displayImage(url, imageView, options);
        } else {
            imageView.setTextBg(0, "", 60);
        }
        addView(imageView);
    }

    public void addChildView(String uid) {
        CircleImageView imageView = new CircleImageView(getContext());
        int imgWH;
        if (getChildCount()-2 < 4) {
            imgWH = DisplayUtil.dip2px(getContext(), 40);
        } else if (getChildCount()-2 < 8) {
            imgWH = DisplayUtil.dip2px(getContext(), 34);
        } else if (getChildCount()-2 < 12){
            imgWH = DisplayUtil.dip2px(getContext(), 30);
        } else {
            return;
        }
        imageView.setLayoutParams(new LayoutParams(imgWH, imgWH));
        imageView.setTag(uid);
        imageView.setBorderWidth(1);
        imageView.setBorderColor(Color.WHITE);
        UserInfo userInfo = EMWApplication.personMap.get(Integer.valueOf(uid));
        if (userInfo != null) {
            String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
            if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                url = Const.DOWN_ICON_URL2 + userInfo.Image;
            }
            imageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, imgWH);
            ImageLoader.getInstance().displayImage(url, imageView, options);
        } else {
            imageView.setTextBg(EMWApplication.getIconColor(Integer.valueOf(uid)), "", imgWH);
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX",0.2f, 1f).setDuration(300);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY",0.2f, 1f).setDuration(300);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
        addView(imageView);
    }

    public void removeChildView(String uid) {
        final View child = findViewWithTag(uid);
        if (child != null) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(child, "scaleX",1f, 0.2f).setDuration(300);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(child, "scaleY",1f, 0.2f).setDuration(300);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidth = mHeight = Math.min(mWidth, mHeight);
        mCenterX = mCenterY = mWidth/2;
        //测量每个children
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            int childCount = getChildCount();
            //首先放置雷达扫描图
            View view = findViewById(R.id.id_scan_circle);
            if (view != null) {
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            }
            View centerView = findViewById(android.R.id.icon);
            if (centerView != null) {
                int x = mCenterX-centerView.getMeasuredWidth()/2;
                int y = mCenterY-centerView.getMeasuredHeight()/2;
                centerView.layout(x, y, x + centerView.getMeasuredWidth(), y + centerView.getMeasuredHeight());
            }
            //放置雷达图中需要展示的item圆点
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                if (child.getId() == R.id.id_scan_circle || child.getId() == android.R.id.icon) {
                    //如果不是Circleview跳过
                    continue;
                }
                //设置CircleView小圆点的坐标信息
                if (i-2 < 4) {
                    mCircleR = (int)(mWidth * circleProportion[1]);
                } else if (i-2 < 8) {
                    mCircleR = (int)(mWidth * circleProportion[2]);
                } else {
                    mCircleR = (int)(mWidth * circleProportion[3]);
                }
                int disX = (int) (mCenterX + mCircleR * Math.cos(Math.toRadians(circleAngdeg[i]))) - child.getMeasuredWidth()/2;
                int disY = (int) (mCenterY + mCircleR * Math.sin(Math.toRadians(circleAngdeg[i]))) - child.getMeasuredHeight()/2;
                child.layout(disX, disY, disX + child.getMeasuredWidth(), disY + child.getMeasuredHeight());
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
