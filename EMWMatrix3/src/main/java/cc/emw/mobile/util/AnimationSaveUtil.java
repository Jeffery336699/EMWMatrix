package cc.emw.mobile.util;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by sunny.du on 2016/12/12.
 * 被替换下来的动画保留类，暂无引用
 */
public class AnimationSaveUtil {
    /**
     * 从左往右滚动出来的动作条    原用于聊天对话框的功能栏的显示效果，已被替换，暂时保留动画，后续确定无需求的时候可清理掉
     */
    private static AnimatorSet showToolbar(final Context context, final ViewGroup toolbarLayout, long startDelay) {
        AnimatorSet showAnimSet = new AnimatorSet();
        ValueAnimator widthAnim = ValueAnimator.ofInt(DisplayUtil.dip2px(context, 49), DisplayUtil.getDisplayWidth(context) - DisplayUtil.dip2px(context, 20));
        widthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int width = (int) animation.getAnimatedValue();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, DisplayUtil.dip2px(context, 49));
                toolbarLayout.setLayoutParams(params);
                toolbarLayout.setLayoutParams(params);
            }
        });
        showAnimSet.play(widthAnim);
        showAnimSet.setDuration(500);

        showAnimSet.setStartDelay(startDelay);
        //showAnimSet.start();
        return showAnimSet;
    }

    /**
     * 从右往左滚动出来的动作条    原用于聊天对话框的功能栏的显示效果，已被替换，暂时保留动画，后续确定无需求的时候可清理掉
     */
    private static AnimatorSet hideToolbar(final Context context, final ViewGroup toolbarLayout) {
        AnimatorSet hideAnimSet = new AnimatorSet();
        ValueAnimator widthAnim = ValueAnimator.ofInt(DisplayUtil.getDisplayWidth(context) - DisplayUtil.dip2px(context, 20), DisplayUtil.dip2px(context, 49));
        widthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int width = (int) animation.getAnimatedValue();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, DisplayUtil.dip2px(context, 49));
                toolbarLayout.setLayoutParams(params);
            }
        });
        hideAnimSet.play(widthAnim);
        hideAnimSet.setDuration(300);

        //hideAnimSet.start();
        return hideAnimSet;
    }


    /**
     * 监听事件的用法
     * showAnimSet.addListener(new AnimatorListenerAdapter() {
     *
     * @Override public void onAnimationEnd(Animator animation) {
     * mRlChatShowEdit.setVisibility(View.GONE);
     * mItvChatAddVoice.setVisibility(View.GONE);
     * }
     * @Override public void onAnimationStart(Animator animation) {
     * toolbarLayout.setVisibility(View.VISIBLE);
     * hidePhoto(mRlLine, 0, 1);
     * }
     * });
     */
    /**
     * 设置相册显示和隐藏的工具类，    参数2、3只能是1或者0，否则无法正常显示
     */
//    boolean isVisibilityEndFlag = true;//true   为还没有结束动画   false 为结束动画
//
//    private void hidePhoto(final ViewGroup toolbarLayout, final int start, final int end) {
//        final AlphaAnimation aa = new AlphaAnimation(start, end);//设置透明度；设置动画终止的透明度
//        aa.setDuration(600);//设置动画效果
//        aa.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (start == 1 && end == 0) {
//                    toolbarLayout.setVisibility(View.GONE);
//                } else if (start == 0 && end == 1) {
//                    toolbarLayout.setVisibility(View.VISIBLE);
//                }
//                isVisibilityEndFlag = false;
//            }
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        ScaleAnimation sa = null;
//        if (start == 1 && end == 0) {
//            sa = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
//        } else if (start == 0 && end == 1) {
//            sa = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
//        }
//        assert sa != null;
//        sa.setDuration(300);//设置动画效果
//        AnimationSet set = new AnimationSet(true);
//        set.addAnimation(aa);
//        set.addAnimation(sa);
//        toolbarLayout.startAnimation(set);
//    }
//
//    private void showToolBar(final ViewGroup toolbarLayout) {
//        AlphaAnimation aa = new AlphaAnimation(0.5f, 1);//设置透明度；设置动画终止的透明度
//        aa.setDuration(500);//设置动画效果
//        aa.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (mRlExpressionRoot.getVisibility() == View.VISIBLE) {
////                    mRlExpressionRoot.setVisibility(View.GONE);
//                    colseRootView(mRlExpressionRoot);
//                    modifyListViewHight(mRlListView, 1);
//                }
//                mRlChatShowEdit.setVisibility(View.GONE);
//                mItvChatAddVoice.setVisibility(View.GONE);
//                toolbarLayout.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        toolbarLayout.startAnimation(aa);
//    }
//
//    private void hideToolbar(final ViewGroup toolbarLayout) {
//        AlphaAnimation aa = new AlphaAnimation(1, 0);//设置透明度；设置动画终止的透明度
//        aa.setDuration(500);//设置动画效果
//        aa.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                mRlChatShowEdit.setVisibility(View.VISIBLE);
//                if (TextUtils.isEmpty(mEetChatAddText.getText().toString())) {
//                    mItvChatAddVoice.setVisibility(View.VISIBLE);
//                    mItvChatSendMsg.setVisibility(View.GONE);
//                } else {
//                    mItvChatAddVoice.setVisibility(View.GONE);
//                    mItvChatSendMsg.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                toolbarLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        toolbarLayout.startAnimation(aa);
//    }


/**
 * ChatActivity中的底部画布关闭弹性动画      已经撤除，暂时保留代码，后续稳定后删除
 */
    //    private void closeRootView(final RelativeLayout view1) {
//        SpringConfig defaultConfig = SpringConfig.fromOrigamiTensionAndFriction(70, 7);
//        SpringSystem mSpringSystem = SpringSystem.create();
//        Spring mSpring = mSpringSystem.createSpring();
//        mSpring.setSpringConfig(defaultConfig);
//        mSpring.addListener(new SpringListener() {
//            @Override
//            public void onSpringUpdate(Spring spring) {
//                float value = (float) spring.getCurrentValue();
//                float moveTo = DisplayUtil.dip2px(ChatActivity.this, 220);
//                float scale2 = 0 - (value * moveTo);
//                FrameLayout.LayoutParams llParams1 = (FrameLayout.LayoutParams) view1.getLayoutParams();
//                llParams1.bottomMargin = (int) scale2;
//                view1.setLayoutParams(llParams1);
//            }
//
//            @Override
//            public void onSpringAtRest(Spring spring) {
////                if (keybFlag) {
////                    if (isCloseKeybFlag) {
////                        keybordStateFlag = KeyBoardUtil.closeKeyboard(ChatActivity.this);//关闭键盘
////                    } else {
////                        if (!keybordStateFlag) {
////                            keybordStateFlag = KeyBoardUtil.openOrCloseSoftInput(ChatActivity.this);
////                        }
////                    }
////                }
//            }
//
//            @Override
//            public void onSpringActivate(Spring spring) {
//                view1.setVisibility(View.GONE);
//                if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
//                    mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
//                }
//            }
//
//            @Override
//            public void onSpringEndStateChange(Spring spring) {
//            }
//        });
//        mSpring.setEndValue(1);
//    }
}
