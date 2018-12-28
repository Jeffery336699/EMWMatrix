package cc.emw.mobile.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import cc.emw.mobile.R;

/**
 * Created by sunny.du on 2017/7/7.
 */

public class ChatButtonRoundnessMenuLayout extends ViewGroup {
    /**
     * MenuItem的点击事件接口
     */
    public interface OnMenuItemClickListener {
        void itemClick(View view, int pos);
    }

    private OnMenuItemClickListener mOnMenuItemClickListener;

    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }


    private int mRadius;
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;//该容器内child item的默认尺寸
    private static final float RADIO_PADDING_LAYOUT = 0;//1 / 20f;//该容器的内边距,无视padding属性，如需边距请用该变量
    private float mPadding;//该容器的内边距,无视padding属性，如需边距请用该变量
    private double mStartAngle = 270;//布局时的开始角度
    private String[] mItemTexts;//菜单项的文本
    private String[] mItemImgs;//菜单项的图标
    private int mMenuItemCount;//菜单的个数
    private int mMenuItemLayoutId = R.layout.chat_smool_menu_item;

    public ChatButtonRoundnessMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMenuItemIconsAndTexts(new String[]{"多人语音", "单人语音", "多人视频", "单人视频"}
                , new String[]{"Multiplayer speech", "singleVoice", "Multiplayer video", "single Video"});
        setPadding(0, 0, 0, 0);  // 无视padding
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {//如果宽或者高的测量模式非精确值
            resWidth = getSuggestedMinimumWidth();// 主要设置为背景图的高度
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;// 如果未设置背景图片，则设置为屏幕宽高的默认值
            resHeight = getSuggestedMinimumHeight();
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;// 如果未设置背景图片，则设置为屏幕宽高的默认值
        } else {// 如果都设置为精确值，则直接取小值；
            resWidth = resHeight = Math.min(width, height);
        }
        setMeasuredDimension(resWidth, resHeight);
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());// 获得半径
        final int count = getChildCount();// menu item数量
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);// menu item尺寸
        int childMode = MeasureSpec.EXACTLY;// menu item测量模式
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            int makeMeasureSpec = -1;// 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            if (child.getId() != R.id.id_circle_menu_item_center) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
        mPadding = RADIO_PADDING_LAYOUT * mRadius;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutRadius = mRadius;
        final int childCount = getChildCount();
        int left, top;
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);//menu item 的尺寸
        float angleDelay = 360 / 12;//(getChildCount() - 1);//根据menu item的个数，计算角度
        for (int i = 0; i < childCount; i++) {//遍历去设置menuitem的位置
            final View child = getChildAt(i);
            mStartAngle %= 360;
            float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;// 计算，中心点到menu item中心的距离
            left = layoutRadius / 2 + (int) Math.round(tmp * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth);// tmp cosa 即menu item中心点的横坐标
            top = layoutRadius / 2 + (int) Math.round(tmp * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth);// tmp sina 即menu item的纵坐标
            child.layout(left, top, left + cWidth, top + cWidth);
            mStartAngle += angleDelay; // 叠加尺寸
        }
        mStartAngle=270;
    }















    /**
     * 设置菜单条目的图标和文本
     */
    public void setMenuItemIconsAndTexts(String[] resIds, String[] texts) {
        mItemImgs = resIds;
        mItemTexts = texts;
        mMenuItemCount = resIds.length;// 初始化mMenuCount
        if (resIds != null && texts != null) {
            mMenuItemCount = Math.min(resIds.length, texts.length);
        }
        addMenuItems();

    }

    /**
     * 设置MenuItem的布局文件，必须在setMenuItemIconsAndTexts之前调用
     */
    public void setMenuItemLayoutId(int mMenuItemLayoutId) {
        this.mMenuItemLayoutId = mMenuItemLayoutId;
    }

    /**
     * 添加菜单项
     */
    private void addMenuItems() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        for (int i = 0; i < mMenuItemCount; i++) {//根据用户设置的参数，初始化view
            final int j = i;
            View view = mInflater.inflate(mMenuItemLayoutId, this, false);
            TextView iv = (TextView) view.findViewById(R.id.id_menu_item_text2);
            TextView tv = (TextView) view.findViewById(R.id.id_menu_item_text1);
            tv.setText(mItemImgs[i]);
            iv.setText(mItemTexts[i]);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.itemClick(v, j);
                    }
                }
            });
            addView(view);// 添加view到容器中
        }
    }

    /**
     * 设置内边距的比例
     */
    public void setPadding(float mPadding) {
        this.mPadding = mPadding;
    }

    /**
     * 获得默认该layout的尺寸
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }
}
