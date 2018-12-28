package com.mingle.headsUp;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.emw.mobile.R;
import cc.emw.mobile.util.DisplayUtil;

/**
 *
 */
@SuppressLint("ViewConstructor")
public class FloatView extends LinearLayout {

    private float rawX = 0;
    private float rawY = 0;
    private float touchX = 0;
    private float startY = 0;
    public LinearLayout rootView;
    public int originalLeft;
    public int viewWidth;
    private float validWidth;
    private VelocityTracker velocityTracker;
    private int maxVelocity;
    private Distance distance;

    private ScrollOrientationEnum scrollOrientationEnum = ScrollOrientationEnum.NONE;

    public static WindowManager.LayoutParams winParams = new WindowManager.LayoutParams();

    public FloatView(final Context context, int i) {
        super(context);
        /*LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.notification_bg, null);
        rootView = (LinearLayout) view.findViewById(R.id.rootView);
        addView(view);*/
        rootView = new LinearLayout(context);
        rootView.setPadding(DisplayUtil.dip2px(context, 9), 0, DisplayUtil.dip2px(context, 9), 0);
        rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        rootView.setBackgroundResource(R.drawable.headsup_notification_bg);
        rootView.setOrientation(VERTICAL);
        addView(rootView);

        maxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        viewWidth = context.getResources().getDisplayMetrics().widthPixels;
        validWidth = viewWidth / 2.0f;
        originalLeft = 0;
        distance = new Distance(context);
    }

    public void setCustomView(View view) {
        rootView.addView(view);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    private HeadsUp headsUp;
    private long cutDown;
    private Handler mHandle = null;
    private CutDownTime cutDownTime;

    private class CutDownTime extends Thread {

        @Override
        public void run() {
            super.run();
            while (cutDown > 0) {
                try {
                    Thread.sleep(1000);
                    cutDown--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (cutDown == 0) {
                mHandle.sendEmptyMessage(0);
            }
        }
    }


    public HeadsUp getHeadsUp() {
        return headsUp;
    }

    private int pointerId;

    public boolean onTouchEvent(MotionEvent event) {
        rawX = event.getRawX();
        rawY = event.getRawY();
        acquireVelocityTracker(event);
        cutDown = headsUp.getDuration();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                startY = event.getRawY();
                pointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                switch (scrollOrientationEnum) {
                    case NONE:
                        if (Math.abs((rawX - touchX)) > 20) {
                            scrollOrientationEnum = ScrollOrientationEnum.HORIZONTAL;

                        } else if (startY - rawY > 20) {
                            scrollOrientationEnum = ScrollOrientationEnum.VERTICAL;

                        }

                        break;
                    case HORIZONTAL:
                        updatePosition((int) (rawX - touchX));
                        break;
                    case VERTICAL:
                        if (startY - rawY > 20) {
                            cancel();
                        }
                        break;
                }

                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000, maxVelocity);
                int dis = (int) velocityTracker.getYVelocity(pointerId);
                if (scrollOrientationEnum == ScrollOrientationEnum.NONE) {
                    if (headsUp.getNotification().contentIntent != null) {

                        try {
                            headsUp.getNotification().contentIntent.send();
                            cancel();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }


                int toX;
                if (preLeft > 0) {
                    toX = (int) (preLeft + Math.abs(dis));
                } else {
                    toX = (int) (preLeft - Math.abs(dis));
                }
                if (toX <= -validWidth) {
                    float preAlpha = 1 - Math.abs(preLeft) / validWidth;
                    preAlpha = preAlpha >= 0 ? preAlpha : 0;
                    translationX(preLeft, -(validWidth + 10), preAlpha, 0);
                } else if (toX <= validWidth) {
                    float preAlpha = 1 - Math.abs(preLeft) / validWidth;
                    preAlpha = preAlpha >= 0 ? preAlpha : 0;
                    translationX(preLeft, 0, preAlpha, 1);

                } else {
                    float preAlpha = 1 - Math.abs(preLeft) / validWidth;
                    preAlpha = preAlpha >= 0 ? preAlpha : 0;
                    translationX(preLeft, validWidth + 10, preAlpha, 0);
                }
                preLeft = 0;
                scrollOrientationEnum = ScrollOrientationEnum.NONE;
                break;
        }

        return super.onTouchEvent(event);

    }

    /**
     * @param event 向VelocityTracker添加MotionEvent
     * @see VelocityTracker#obtain()
     * @see VelocityTracker#addMovement(MotionEvent)
     */
    private void acquireVelocityTracker(MotionEvent event) {
        if (null == velocityTracker) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }


    private int preLeft;

    public void updatePosition(int left) {
        float preAlpha = 1 - Math.abs(preLeft) / validWidth;
        float leftAlpha = 1 - Math.abs(left) / validWidth;
        preAlpha = preAlpha >= 0 ? preAlpha : 0;
        leftAlpha = leftAlpha >= 0 ? leftAlpha : 0;
        translationX(preLeft, left, preAlpha, leftAlpha);

        preLeft = left;
    }


    public void translationX(float fromX, float toX, float formAlpha, final float toAlpha) {
        ObjectAnimator a1 = ObjectAnimator.ofFloat(rootView, "alpha", formAlpha, toAlpha);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(rootView, "translationX", fromX, toX);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(a1, a2);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (toAlpha == 0) {
                    HeadsUpManager.getInstant(getContext()).dismiss();

                    cutDown = -1;
                    if (velocityTracker != null) {
                        velocityTracker.clear();
                        try {
                            velocityTracker.recycle();
                        } catch (IllegalStateException e) {

                        }
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    public void setNotification(final HeadsUp headsUp) {
        this.headsUp = headsUp;

        mHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (headsUp.isActivateStatusBar()) {
                    HeadsUpManager.getInstant(getContext()).silencerNotify(headsUp);
                }
                HeadsUpManager.getInstant(getContext()).animDismiss(headsUp);
            }
        };

        cutDownTime = new CutDownTime();

        if (!headsUp.isSticky()) {
            cutDownTime.start();
        }

        cutDown = headsUp.getDuration();

        if (headsUp.getCustomView() == null) {
            View defaultView = LayoutInflater.from(getContext()).inflate(R.layout.headsup_notification, rootView, false);
            rootView.addView(defaultView);
            ImageView imageView = (ImageView) defaultView.findViewById(R.id.iconIM);
            TextView titleTV = (TextView) defaultView.findViewById(R.id.titleTV);
            TextView timeTV = (TextView) defaultView.findViewById(R.id.timeTV);
            TextView messageTV = (TextView) defaultView.findViewById(R.id.messageTV);
            imageView.setImageResource(headsUp.getIcon());
            titleTV.setText(headsUp.getTitleStr());
            messageTV.setText(headsUp.getMsgStr());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            timeTV.setText(simpleDateFormat.format(new Date()));
        } else {
            setCustomView(headsUp.getCustomView());
        }
    }

    protected void cancel() {
        HeadsUpManager.getInstant(getContext()).animDismiss();
        cutDown = -1;
        cutDownTime.interrupt();

        if (velocityTracker != null) {
            try {
                velocityTracker.clear();
                velocityTracker.recycle();
            } catch (IllegalStateException e) {

            }
        }
    }


    enum ScrollOrientationEnum {
        VERTICAL, HORIZONTAL, NONE
    }
}
