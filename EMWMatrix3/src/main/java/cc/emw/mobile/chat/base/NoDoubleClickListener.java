package cc.emw.mobile.chat.base;

import android.view.View;

import java.util.Calendar;

/**
 * Created by sunny.du on 2017/2/13.
 * 禁止短时间内相同的点击事件产生(1秒)
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {
    private long lastClickTime = 0;
    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > ChatContent.MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View v);
}
