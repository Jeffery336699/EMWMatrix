package cc.emw.mobile.me.widget;

import android.animation.Animator;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import cc.emw.mobile.R;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by 大灯泡 on 2016/1/22.
 * 菜单。
 */
public class MenuPopup extends BasePopupWindow {

    private TextView mText;
    private View view;

    private OnMenuItemClickListener listener;

    public MenuPopup(Activity context, View view) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.view = view;
    }

    public void setOnMenuClickListener(OnMenuItemClickListener listener) {
        this.listener = listener;
        findViewById(R.id.tx_1).setOnClickListener(listener);
        findViewById(R.id.tx_2).setOnClickListener(listener);
        findViewById(R.id.tx_3).setOnClickListener(listener);
        findViewById(R.id.tx_4).setOnClickListener(listener);
    }

    @Override
    protected Animation initShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(getScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0));
        set.addAnimation(getDefaultAlphaAnimation());
        return set;
        //return null;
    }

    @Override
    public Animator initShowAnimator() {
       /* AnimatorSet set=new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mAnimaView,"scaleX",0.0f,1.0f).setDuration(300),
                ObjectAnimator.ofFloat(mAnimaView,"scaleY",0.0f,1.0f).setDuration(300),
                ObjectAnimator.ofFloat(mAnimaView,"alpha",0.0f,1.0f).setDuration(300*3/2));*/
        return null;
    }

    @Override
    public void showPopupWindow(View v) {
        setOffsetX(2 * getWidth());
        setOffsetY(-v.getHeight());
        view.setVisibility(View.VISIBLE);
        super.showPopupWindow(v);
    }

    @Override
    public View getClickToDismissView() {
        return null;
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.popup_menu);
    }

    @Override
    public View initAnimaView() {
        return getPopupWindowView().findViewById(R.id.popup_contianer);
    }

    public void setText(TextView mText) {
        this.mText = mText;
    }

    public TextView getmText() {
        return this.mText;
    }

    @Override
    public void dismiss() {
        if (view != null)
            view.setVisibility(View.GONE);
        super.dismiss();
    }
}
