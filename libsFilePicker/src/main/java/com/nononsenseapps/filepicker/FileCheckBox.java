package com.nononsenseapps.filepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.gc.materialdesign.views.CheckBox;

/**
 * Created by sunny.du on 2017/4/12.
 */
public class FileCheckBox extends CheckBox {
    public FileCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }
}
