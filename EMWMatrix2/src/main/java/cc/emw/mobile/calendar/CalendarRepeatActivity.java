package cc.emw.mobile.calendar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.githang.androidcrash.util.AppManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;

/**
 * @author zrjt
 * @version 创建时间：2016-4-21 上午9:15:32
 */
@ContentView(R.layout.activity_calendar_repeat)
public class CalendarRepeatActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView tvSubmit;
    private int enterTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.repeat);
        enterTag = getIntent().getIntExtra("enterTag", 0);
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.ll_never_repeat,
            R.id.ll_day_repeat, R.id.ll_week_repeat, R.id.ll_week_twice_repeat,
            R.id.ll_month_repeat, R.id.ll_year_repeat, R.id.ll_free_repeat})
    private void Onhlick(View v) {
        Intent intent = null;
        if (enterTag == 1) {
            intent = new Intent(this, CalendarEditActivity.class);
        } else {
            intent = new Intent(this, CalendarCreateActivitys.class);
        }
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                return;
            case R.id.ll_never_repeat:
                intent.putExtra("fixRepeat", "无");
                intent.putExtra("fixpv", 0);
                intent.putExtra("fixType", -1);
                break;
            case R.id.ll_day_repeat:
                intent.putExtra("fixRepeat", "每1天");
                intent.putExtra("fixpv", 1);
                intent.putExtra("fixType", 0);
                break;
            case R.id.ll_week_repeat:
                intent.putExtra("fixRepeat", "每1周");
                intent.putExtra("fixpv", 1);
                intent.putExtra("fixType", 1);
                break;
            case R.id.ll_week_twice_repeat:
                intent.putExtra("fixRepeat", "每2周");
                intent.putExtra("fixpv", 2);
                intent.putExtra("fixType", 1);
                break;
            case R.id.ll_month_repeat:
                intent.putExtra("fixRepeat", "每1月");
                intent.putExtra("fixpv", 1);
                intent.putExtra("fixType", 2);
                break;
            case R.id.ll_year_repeat:
                intent.putExtra("fixRepeat", "每1年");
                intent.putExtra("fixpv", 1);
                intent.putExtra("fixType", 3);
                break;
            case R.id.ll_free_repeat:
                intent = null;
                Intent freeIntent = new Intent(this, CalendarFreeActivity.class);
                if (enterTag == 1) {
                    freeIntent.putExtra("enterTag", 1);
                }
                startActivity(freeIntent);
//                new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        onBackPressed();
//                    }
//                }.start();
                finish();
                break;
        }
        if (intent != null) {
            startActivity(intent);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onBackPressed();
                }
            }.start();
        }
    }
}
