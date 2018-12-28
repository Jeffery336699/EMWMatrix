package cc.emw.mobile.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;

/**
 * @author zrjt
 * @version 创建时间：2016-4-21 上午9:16:49
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.activity_calendar_tixing_end)
public class CalendarTxEndActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderRightTv;
    @ViewInject(R.id.tv_calendar_end_repeat)
    private TextView endRepeatTime;
    private TimePickerView mStartPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.repeat_end);
        mHeaderRightTv.setVisibility(View.VISIBLE);
        mHeaderRightTv.setText(R.string.finish);
        final SimpleDateFormat format = new SimpleDateFormat(
                getString(R.string.timeformat6));
        mStartPopupWindow = new TimePickerView(this, Type.ALL);// 时间选择器
        mStartPopupWindow.setTitle(getString(R.string.repeat_end));
        mStartPopupWindow.setCancelable(true);

        mStartPopupWindow.setOnTimeSelectListener(new OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                String startTime = format.format(date);
                endRepeatTime.setText(startTime);
            }
        });

    }

    @Event(value = {R.id.cm_header_btn_left, R.id.ll_calendar_tx_none,
            R.id.ll_calendar_tx_0, R.id.cm_header_tv_right})
    private void Onhlick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.ll_calendar_tx_none:
                intent = new Intent();
                intent.putExtra("tixingEnd", getString(R.string.never));
                break;
            case R.id.ll_calendar_tx_0:
                mStartPopupWindow.show();
                break;
            case R.id.cm_header_tv_right:
                intent = new Intent();
                if (endRepeatTime.getText() != null && !endRepeatTime.getText().equals("")) {
                    intent.putExtra("tixingEnd", endRepeatTime.getText());
                } else {
                    intent.putExtra("tixingEnd", getString(R.string.never));
                }
                break;
        }
        if (intent != null) {
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mStartPopupWindow != null && mStartPopupWindow.isShowing()) {
            mStartPopupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

}
