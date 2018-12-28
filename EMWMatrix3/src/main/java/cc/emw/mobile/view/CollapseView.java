package cc.emw.mobile.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.util.DisplayUtil;

/**
 * 折叠/展开ViewGroup
 */
public class CollapseView extends LinearLayout {
    private final Context mContext;
    private long duration = 400;//展开/折叠的时间(s)
    private TextView mTagName;
    //    private TextView mTagName1;
//    private TextView mTagName2;
//    private TextView mTagName3;
    private IconTextView mNumberTextView;
    private TextView mTitleTextView;
    private LinearLayout mTitleRelativeLayout;
    private ScrollView mContentScrollView;
    private RelativeLayout mContentRelativeLayout;
    private IconTextView mArrowImageView;
    private int parentWidthMeasureSpec;
    private int parentHeightMeasureSpec;
    private int measuredHeight;
    private int desireHeight = -1;
    private SwipeBackScrollView mSwipeScrollView;
    private ScrollView mScrollView;

    public CollapseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.collapse_layout, this);
        init();
    }

    public void setSwipeScrollView(SwipeBackScrollView swipeScrollView) {
        this.mSwipeScrollView = swipeScrollView;
    }

    public void setScrollView(ScrollView scrollView) {
        this.mScrollView = scrollView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        parentWidthMeasureSpec = widthMeasureSpec;
        parentHeightMeasureSpec = heightMeasureSpec;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mArrowImageView.setVisibility(isEnabled() ? VISIBLE : GONE);
    }

    private void init() {
        mNumberTextView = (IconTextView) findViewById(R.id.numberTextView);
        mTagName = (TextView) findViewById(R.id.tv_tag_name);
//        mTagName1 = (TextView) findViewById(R.id.tv_tag_name1);
//        mTagName2 = (TextView) findViewById(R.id.tv_tag_name2);
//        mTagName3 = (TextView) findViewById(R.id.tv_tag_name3);
        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mTitleRelativeLayout = (LinearLayout) findViewById(R.id.titleRelativeLayout);
        mContentScrollView = (ScrollView) findViewById(R.id.collapse_scroll);
        mContentRelativeLayout = (RelativeLayout) findViewById(R.id.contentRelativeLayout);
        mArrowImageView = (IconTextView) findViewById(R.id.arrowImageView);
        mTitleRelativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnabled()) {
                    final int[] location = new int[2];
                    v.getLocationInWindow(location);
                    rotateArrow();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mSwipeScrollView != null) {
                                mSwipeScrollView.smoothScrollBy(0, location[1] - DisplayUtil.dip2px(mContext, 74));
                            }
                            if (mScrollView != null) {
                                mScrollView.smoothScrollBy(0, location[1] - DisplayUtil.dip2px(mContext, 74));
                            }
                        }
                    }, 400);
                }
            }
        });
        collapse(mContentScrollView);
    }

    public void setTagNameVis(String icon, String tagName) {
        if (icon != null && TextUtils.isEmpty(icon)) {
            mNumberTextView.setVisibility(GONE);
        } else {
            mNumberTextView.setVisibility(VISIBLE);
            mNumberTextView.setIconText(icon);
        }
        mTagName.setText(tagName);
    }

    public void setTagNameStyle(int size, String color) {
        mTagName.setTextSize(size);
        mTagName.setTextColor(Color.parseColor(color));
    }

    public void setTitleNameStyle(int size, String color) {
        mTitleTextView.setTextSize(size);
        mTitleTextView.setTextColor(Color.parseColor(color));
    }

    public void setNumber(String number) {
        if (!TextUtils.isEmpty(number)) {
            mNumberTextView.setText(number);
        }
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }
    }

    public String getTitle() {
        return mTitleTextView.getText().toString();
    }

    public void setContent(int resID) {
        View view = LayoutInflater.from(mContext).inflate(resID, null);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        mContentRelativeLayout.addView(view);
    }

    public void setView(View view) {
        if (view != null) {
            mContentRelativeLayout.addView(view);
        }
    }

    public void rotateArrow() {
        int angle = 0;
        if (mArrowImageView.getTag() == null || mArrowImageView.getTag().equals(true)) {
            mArrowImageView.setTag(false);
            angle = 180;
            //TODO 展开
            expand(mContentScrollView);
        } else {
            angle = 0;
            mArrowImageView.setTag(true);
            //TODO 折叠
            collapse(mContentScrollView);
        }
        mArrowImageView.animate().setDuration(duration).rotation(angle);
    }

    /**
     * 折叠
     *
     * @param view 视图
     */
    public void collapse(final View view) {
        mTitleRelativeLayout.setPadding(DisplayUtil.dip2px(mContext, 12), DisplayUtil.dip2px(mContext, 14), DisplayUtil.dip2px(mContext, 12), DisplayUtil.dip2px(mContext, 14));
        measuredHeight = view.getMeasuredHeight();
//        final int measuredHeight = updateView(view);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                Log.e("TAG", "interpolatedTime = " + interpolatedTime);
                if (interpolatedTime == 1) {
                    mContentScrollView.setVisibility(GONE);
                } else {
                    view.getLayoutParams().height = measuredHeight - (int) (measuredHeight * interpolatedTime);
                    view.requestLayout();
                }
            }
        };
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 展开
     *
     * @param view 视图
     */
    public void expand(final View view) {
        view.measure(parentWidthMeasureSpec, parentHeightMeasureSpec);
        if (desireHeight != -1) {
            measuredHeight = desireHeight;
        } else {
            measuredHeight = view.getMeasuredHeight();
        }
//        final int measuredHeight = view.getMeasuredHeight();
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                Log.e("TAG", "interpolatedTime = " + interpolatedTime);
                if (interpolatedTime == 1) {
                    view.getLayoutParams().height = measuredHeight;
                } else {
                    view.getLayoutParams().height = (int) (measuredHeight * interpolatedTime);
                }
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    public void updateView(View view) {
        measuredHeight = measuredHeight + view.getMeasuredHeight();
    }

    public void setDesireHeight(int height) {
        desireHeight = height;
    }
}
