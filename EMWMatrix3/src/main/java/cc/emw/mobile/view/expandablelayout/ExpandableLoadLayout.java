package cc.emw.mobile.view.expandablelayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import cc.emw.mobile.R;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.view.autoload.PullLoadMoreRecyclerView;
import cc.emw.mobile.view.expandablelayout.util.FastOutSlowInInterpolator;

import static cc.emw.mobile.view.expandablelayout.ExpandableLayout.State.COLLAPSED;
import static cc.emw.mobile.view.expandablelayout.ExpandableLayout.State.COLLAPSING;
import static cc.emw.mobile.view.expandablelayout.ExpandableLayout.State.EXPANDED;
import static cc.emw.mobile.view.expandablelayout.ExpandableLayout.State.EXPANDING;

public class ExpandableLoadLayout extends FrameLayout {
    public interface State {
        int COLLAPSED = 0;
        int COLLAPSING = 1;
        int EXPANDING = 2;
        int EXPANDED = 3;
    }

    public static final String KEY_SUPER_STATE = "super_state";
    public static final String KEY_EXPANSION = "expansion";

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final int DEFAULT_DURATION = 300;

    private int duration = DEFAULT_DURATION;
    private float parallax;
    private float expansion;
    private int orientation;
    private int state;

    private Interpolator interpolator = new FastOutSlowInInterpolator();
    private ValueAnimator animator;

    private OnExpansionUpdateListener listener;

    private ProgressBar progressBar;
//    private ListView listView;
    private PullLoadMoreRecyclerView pullLoadMoreRecyclerView;
    private RecyclerView recyclerView;
//    private ScrollView scrollView;
    private static final int MAX_HEIGHT_DP = 300;

    public ExpandableLoadLayout(Context context) {
        this(context, null);
    }

    public ExpandableLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
            duration = a.getInt(R.styleable.ExpandableLayout_el_duration, DEFAULT_DURATION);
            expansion = a.getBoolean(R.styleable.ExpandableLayout_el_expanded, false) ? 1 : 0;
            orientation = a.getInt(R.styleable.ExpandableLayout_android_orientation, VERTICAL);
            parallax = a.getFloat(R.styleable.ExpandableLayout_el_parallax, 1);
            a.recycle();

            state = expansion == 0 ? COLLAPSED : EXPANDED;
            setParallax(parallax);
        }

        /*listView = new ListView(context);
        listView.setDividerHeight(0);
        addView(listView);*/

//        recyclerView = new RecyclerView(context);
        pullLoadMoreRecyclerView = new PullLoadMoreRecyclerView(context);
        pullLoadMoreRecyclerView.setPullRefreshEnable(false);
        recyclerView = pullLoadMoreRecyclerView.getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setBackgroundColor(Color.parseColor("#EFF3F5"));
        addView(pullLoadMoreRecyclerView);

        progressBar = new ProgressBar(context);
        LayoutParams params = new LayoutParams(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        progressBar.setLayoutParams(params);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar2));
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(GONE);
        addView(progressBar);

        View topShadow = new View(context);
        topShadow.setBackgroundResource(R.drawable.up_down_shape);
        LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(context, 4));
        params1.gravity = Gravity.TOP;
        topShadow.setLayoutParams(params1);
        addView(topShadow);

        View bottomShadow = new View(context);
        bottomShadow.setBackgroundResource(R.drawable.up_down_shape2);
        LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(context, 4));
        params2.gravity = Gravity.BOTTOM;
        bottomShadow.setLayoutParams(params2);
        addView(bottomShadow);
    }

    public void setRefreshing(boolean refreshing) {
        progressBar.setVisibility(refreshing ? VISIBLE : GONE);
    }

//    public void setScrollView(final ScrollView scrollView) {
//        listView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        scrollView.requestDisallowInterceptTouchEvent(true);
//                        return false;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        scrollView.requestDisallowInterceptTouchEvent(false);
//                        return false;
//                }
//                return true;
//            }
//        });
//    }
//
//    public ListView getListView() {
//        return listView;
//    }

    public PullLoadMoreRecyclerView getPullLoadMoreRecyclerView() {
        return pullLoadMoreRecyclerView;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void updateHeight() {
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
            View itemView = recyclerView.getChildAt(0);
            if (itemView == null) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateHeight();
                    }
                }, 100);
                return;
            }
            int height = itemView.getHeight() * recyclerView.getAdapter().getItemCount();
            LayoutParams params;
            if (height > DisplayUtil.dip2px(getContext(), MAX_HEIGHT_DP)) {
                params = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(getContext(), MAX_HEIGHT_DP));
            } else {
                params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            }
//            recyclerView.setLayoutParams(params);
            pullLoadMoreRecyclerView.setLayoutParams(params);
        } /*else if (listView.getAdapter() != null && listView.getAdapter().getCount() > 0) {
            View itemView = listView.getChildAt(0);
            if (itemView == null) {
                listView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateHeight();
                    }
                }, 100);
                return;
            }
            int height = itemView.getHeight() * listView.getAdapter().getCount();
            LayoutParams params;
            if (height > DisplayUtil.dip2px(getContext(), MAX_HEIGHT_DP)) {
                params = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(getContext(), MAX_HEIGHT_DP));
            } else {
                params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            }
            listView.setLayoutParams(params);
        }*/
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();

        expansion = isExpanded() ? 1 : 0;

        bundle.putFloat(KEY_EXPANSION, expansion);
        bundle.putParcelable(KEY_SUPER_STATE, superState);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcelable) {
        Bundle bundle = (Bundle) parcelable;
        expansion = bundle.getFloat(KEY_EXPANSION);
        state = expansion == 1 ? EXPANDED : COLLAPSED;
        Parcelable superState = bundle.getParcelable(KEY_SUPER_STATE);

        super.onRestoreInstanceState(superState);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int size = orientation == LinearLayout.HORIZONTAL ? width : height;

        setVisibility(expansion == 0 && size == 0 ? GONE : VISIBLE);

        int expansionDelta = size - Math.round(size * expansion);
        if (parallax > 0) {
            float parallaxDelta = expansionDelta * parallax;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (orientation == HORIZONTAL) {
                    int direction = -1;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                        direction = 1;
                    }
                    child.setTranslationX(direction * parallaxDelta);
                } else {
                    child.setTranslationY(-parallaxDelta);
                }
            }
        }

        if (orientation == HORIZONTAL) {
            setMeasuredDimension(width - expansionDelta, height);
        } else {
            setMeasuredDimension(width, height - expansionDelta);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (animator != null) {
            animator.cancel();
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Get expansion state
     *
     * @return one of {@link State}
     */
    public int getState() {
        return state;
    }

    public boolean isExpanded() {
        return state == EXPANDING || state == EXPANDED;
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean animate) {
        if (isExpanded()) {
            collapse(animate);
        } else {
            expand(animate);
        }
    }

    public void expand() {
        expand(true);
    }

    public void expand(boolean animate) {
        setExpanded(true, animate);
    }

    public void collapse() {
        collapse(true);
    }

    public void collapse(boolean animate) {
        setExpanded(false, animate);
    }

    /**
     * Convenience method - same as calling setExpanded(expanded, true)
     */
    public void setExpanded(boolean expand) {
        setExpanded(expand, true);
    }

    public void setExpanded(boolean expand, boolean animate) {
        if (expand && isExpanded()) {
            return;
        }

        if (!expand && !isExpanded()) {
            return;
        }

        int targetExpansion = expand ? 1 : 0;
        if (animate) {
            animateSize(targetExpansion);
        } else {
            setExpansionInternal(targetExpansion);
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getExpansion() {
        return expansion;
    }

    public void setExpansion(float expansion) {
        if (this.expansion == expansion) {
            return;
        }

        // Infer state from previous value
        float delta = expansion - this.expansion;
        if (expansion == 0) {
            state = COLLAPSED;
        } else if (expansion == 1) {
            state = EXPANDED;
        } else if (delta < 0) {
            state = COLLAPSING;
        } else if (delta > 0) {
            state = EXPANDING;
        }
        setExpansionInternal(expansion);
    }

    private void setExpansionInternal(float expansion) {
        setVisibility(state == COLLAPSED ? GONE : VISIBLE);

        this.expansion = expansion;
        requestLayout();

        if (listener != null) {
            listener.onExpansionUpdate(expansion, state);
        }
    }

    public float getParallax() {
        return parallax;
    }

    public void setParallax(float parallax) {
        // Make sure parallax is between 0 and 1
        parallax = Math.min(1, Math.max(0, parallax));
        this.parallax = parallax;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        if (orientation < 0 || orientation > 1) {
            throw new IllegalArgumentException("Orientation must be either 0 (horizontal) or 1 (vertical)");
        }
        this.orientation = orientation;
    }

    public void setOnExpansionUpdateListener(OnExpansionUpdateListener listener) {
        this.listener = listener;
    }

    private void animateSize(int targetExpansion) {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        animator = ValueAnimator.ofFloat(expansion, targetExpansion);
        animator.setInterpolator(interpolator);
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setExpansionInternal((float) valueAnimator.getAnimatedValue());
            }
        });

        animator.addListener(new ExpansionListener(targetExpansion));

        animator.start();
    }

    public interface OnExpansionUpdateListener {
        /**
         * Callback for expansion updates
         *
         * @param expansionFraction Value between 0 (collapsed) and 1 (expanded) representing the the expansion progress
         * @param state             One of {@link State} repesenting the current expansion state
         */
        void onExpansionUpdate(float expansionFraction, int state);
    }

    private class ExpansionListener implements Animator.AnimatorListener {
        private int targetExpansion;
        private boolean canceled;

        public ExpansionListener(int targetExpansion) {
            this.targetExpansion = targetExpansion;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            state = targetExpansion == 0 ? COLLAPSING : EXPANDING;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!canceled) {
                state = targetExpansion == 0 ? COLLAPSED : EXPANDED;
                setExpansionInternal(targetExpansion);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            canceled = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }
}
