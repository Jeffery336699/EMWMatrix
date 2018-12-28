package cc.emw.mobile.me.widget;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ListView;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.me.adapter.ScalePopupAdapter;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by 大灯泡 on 2016/1/15.
 * 普通的popup
 */
public class ScalePopup extends BasePopupWindow {

    private View popupView;
    private ListView mListView;
    private List<String> mDataList;
    private Context mContext;
    private OnScalePopupItemClickListener listener;

    public ScalePopup(Activity context, List<String> mDataList) {
        super(context);
        this.mContext = context;
        this.mDataList = mDataList;
        bindEvent();
    }

    @Override
    protected Animator initShowAnimator() {
        return getDefaultSlideFromBottomAnimationSet();
    }

    @Override
    protected Animation initShowAnimation() {
        return null;
    }

    public void setOnScaleItemClickListener(OnScalePopupItemClickListener listener) {
        this.listener = listener;
        if (mListView != null) {
            mListView.setOnItemClickListener(listener);
        }
    }

    @Override
    public View getClickToDismissView() {
        return popupView.findViewById(R.id.click_to_dismiss);
    }

    @Override
    public View onCreatePopupView() {
        popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_normal, null);
        return popupView;
    }

    @Override
    public View initAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    private void bindEvent() {
        if (popupView != null) {
            mListView = (ListView) popupView.findViewById(R.id.lv_scale_popup);
            if (mDataList != null) {
                ScalePopupAdapter adapter = new ScalePopupAdapter(mContext, mDataList);
                mListView.setAdapter(adapter);
            }
        }
    }
}
