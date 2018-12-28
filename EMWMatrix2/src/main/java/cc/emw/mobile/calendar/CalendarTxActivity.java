package cc.emw.mobile.calendar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;

/**
 * @author zrjt
 * @version 创建时间：2016-4-21 上午9:16:49
 */
@ContentView(R.layout.activity_calendar_tixing)
public class CalendarTxActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(R.string.notify);
	}

	@Event(value = { R.id.cm_header_btn_left, R.id.ll_calendar_tx_none,
			R.id.ll_calendar_tx_0, R.id.ll_calendar_tx_1,
			R.id.ll_calendar_tx_2, R.id.ll_calendar_tx_3,
			R.id.ll_calendar_tx_4, R.id.ll_calendar_tx_5, R.id.ll_calendar_tx_6 })
	private void Onhlick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		case R.id.ll_calendar_tx_none:
			intent = new Intent();
			intent.putExtra("tixing", getString(R.string.nope));
			break;
		case R.id.ll_calendar_tx_0:
			intent = new Intent();
			intent.putExtra("tixing", getString(R.string.ontime));
			break;
		case R.id.ll_calendar_tx_1:
			intent = new Intent();
			intent.putExtra("tixing", getString(R.string.five_minute_per));
			break;
		case R.id.ll_calendar_tx_2:
			intent = new Intent();
			intent.putExtra("tixing", getString(R.string.one_hours));
			break;
		case R.id.ll_calendar_tx_3:
			intent = new Intent();
			intent.putExtra("tixing", getString(R.string.three_hours));
			break;
		case R.id.ll_calendar_tx_4:
			intent = new Intent();
			intent.putExtra("tixing", getString(R.string.one_day));
			break;
		case R.id.ll_calendar_tx_5:
			intent = new Intent();
			intent.putExtra("tixing", getString(R.string.three_day));
			break;
		case R.id.ll_calendar_tx_6:
			intent = new Intent();
			intent.putExtra("tixing", getString(R.string.one_week));
			break;
		}
		if (intent != null) {
			setResult(RESULT_OK, intent);
			onBackPressed();
		}
	}

}
