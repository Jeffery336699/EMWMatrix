package cc.emw.mobile.view;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import cc.emw.mobile.R;

/**
 * compile 'com.rengwuxian.materialedittext:library:2.1.4'
 * Created by shaobo.zhuang on 2017/7/5.
 */

public class MaterialEditText extends com.rengwuxian.materialedittext.MaterialEditText {

    OnFocusChangeListener innerListener;
    OnFocusChangeListener outerListener;

    public MaterialEditText(Context context) {
        super(context);
        init();
    }

    public MaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaterialEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init();
    }

    private void init() {
        innerListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (outerListener != null) {
                    outerListener.onFocusChange(v, hasFocus);
                }
                if (TextUtils.isEmpty(getError())) {
                    setFloatingLabelTextColor(hasFocus ? ContextCompat.getColor(getContext(), R.color.cm_main_text) : Color.parseColor("#61000000"));
                }
            }
        };
        super.setOnFocusChangeListener(innerListener);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        if (innerListener == null) {
            super.setOnFocusChangeListener(listener);
        } else {
            outerListener = listener;
        }
    }
}
