package cc.emw.mobile.calendar;

import java.util.ArrayList;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;

/**
 * @author zrjt
 * @version 创建时间：2016-4-21 上午10:18:01
 */
@ContentView(R.layout.activity_calendar_free)
public class CalendarFreeActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;
	@ViewInject(R.id.cm_header_tv_right)
	private TextView tvSubmit;
	@ViewInject(R.id.ll_calendar_repeat_pinlv)
	private LinearLayout plLayout;
	@ViewInject(R.id.ll_calendar_repeat_result)
	private LinearLayout rrLayout;
	@ViewInject(R.id.optionspicker_pinlv)
	private LinearLayout plInfoLayout; // 频率滚轴的Layout
	@ViewInject(R.id.optionspicker_result)
	private LinearLayout rrInfoLayout; // 重复结果的Layout
	@ViewInject(R.id.options1_type)
	private WheelView optionsType;
	@ViewInject(R.id.options1_num)
	private WheelView optionsNum;
	@ViewInject(R.id.options2_tv)
	private WheelView optionsTv;
	@ViewInject(R.id.tv_pl_tag)
	private TextView plTextView;
	@ViewInject(R.id.tv_pl_tags)
	private TextView plTextViews;
	@ViewInject(R.id.tv_rr_tag)
	private TextView rrTextView;
	@ViewInject(R.id.scroll)
	private ScrollView scrollView;
	@ViewInject(R.id.ll_week_select_day)
	private LinearLayout weekLayout; // 周选择视图
	@ViewInject(R.id.cb_week_select7)
	private CheckBox checkBox7; // 星期日
	@ViewInject(R.id.cb_week_select6)
	private CheckBox checkBox6;
	@ViewInject(R.id.cb_week_select5)
	private CheckBox checkBox5;
	@ViewInject(R.id.cb_week_select4)
	private CheckBox checkBox4;
	@ViewInject(R.id.cb_week_select3)
	private CheckBox checkBox3;
	@ViewInject(R.id.cb_week_select2)
	private CheckBox checkBox2;
	@ViewInject(R.id.cb_week_select1)
	private CheckBox checkBox1;
	private ArrayList<String> tags = new ArrayList<String>(); // 标签的时间滚轴
	private int n = 7; // 数量的时间滚轴
	private boolean isShowPl = true;
	private boolean isShowRr = true;
	private int enterTag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enterTag = getIntent().getIntExtra("enterTag", 0);
		init();
	}

	private void thingsCrash() {
		optionsType.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					scrollView.requestDisallowInterceptTouchEvent(true);
					return false;
				case MotionEvent.ACTION_DOWN:
					scrollView.requestDisallowInterceptTouchEvent(true);
					return false;
				case MotionEvent.ACTION_MOVE:
					scrollView.requestDisallowInterceptTouchEvent(true);
					return false;
				}
				return true;
			}
		});

		optionsNum.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					scrollView.requestDisallowInterceptTouchEvent(true);
					return false;
				case MotionEvent.ACTION_DOWN:
					scrollView.requestDisallowInterceptTouchEvent(true);
					return false;
				case MotionEvent.ACTION_MOVE:
					scrollView.requestDisallowInterceptTouchEvent(true);
					return false;
				}
				return true;
			}
		});

	}

	private void init() {
		mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(R.string.design_oneself);
		tvSubmit.setVisibility(View.VISIBLE);
		tvSubmit.setText(R.string.finish);
		// 分类的时间滚轴
		ArrayList<String> sorts = new ArrayList<String>();
		sorts.add("天");
		sorts.add("周");
		sorts.add("月");
		sorts.add("年");
		ArrayWheelAdapter<String> adapterSort = new ArrayWheelAdapter<String>(
				sorts);
		optionsType.setAdapter(adapterSort);
		optionsType.setCyclic(false);
		optionsType.setEnabled(true);
		optionsType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index) {
				tags.clear();
				switch (index) {
				case 0:
					tags.add("天");
					plTextView.setText("每天");
					plTextViews.setText("天");
					n = 10;
					weekLayout.setVisibility(View.GONE);
					break;
				case 1:
					plTextView.setText("每周");
					plTextViews.setText("周");
					tags.add("周");
					weekLayout.setVisibility(View.VISIBLE);
					n = 8;
					break;
				case 2:
					plTextView.setText("每月");
					plTextViews.setText("月");
					tags.add("月");
					n = 12;
					weekLayout.setVisibility(View.GONE);
					break;
				case 3:
					plTextView.setText("每年");
					plTextViews.setText("年");
					tags.add("年");
					n = 3;
					weekLayout.setVisibility(View.GONE);
					break;
				}
				ArrayWheelAdapter<String> adapter2 = new ArrayWheelAdapter<String>(
						tags);
				// 标签的时间滚轴
				optionsTv.setAdapter(adapter2);
				optionsTv.setCyclic(false);
				optionsTv.setEnabled(false);
				// 数量的时间滚轴
				NumericWheelAdapter adapter1 = new NumericWheelAdapter(1, n);
				optionsNum.setAdapter(adapter1);
			}
		});
		// 数量的时间滚轴
		NumericWheelAdapter adapter1 = new NumericWheelAdapter(1, n);
		optionsNum.setAdapter(adapter1);
		optionsNum.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index) {
				int i = optionsNum.getCurrentItem() + 1;
				rrTextView.setText(i + "");
			}
		});
		// 标签的时间滚轴
		tags = new ArrayList<String>();
		tags.add("天");
		ArrayWheelAdapter<String> adapter2 = new ArrayWheelAdapter<String>(tags);
		optionsTv.setAdapter(adapter2);
		optionsTv.setCyclic(false);
		optionsTv.setEnabled(false);
		// 设置默认值
		optionsType.setCurrentItem(0);
		optionsNum.setCurrentItem(0);
		thingsCrash();
	}

	@Event(value = { R.id.cm_header_btn_left, R.id.ll_calendar_repeat_result,
			R.id.ll_calendar_repeat_pinlv, R.id.cm_header_tv_right })
	private void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		case R.id.ll_calendar_repeat_pinlv:
			if (isShowPl) {
				plInfoLayout.setVisibility(View.VISIBLE);
				isShowPl = false;
			} else {
				plInfoLayout.setVisibility(View.GONE);
				isShowPl = true;
			}
			break;
		case R.id.ll_calendar_repeat_result:
			if (isShowRr) {
				rrInfoLayout.setVisibility(View.VISIBLE);
				isShowRr = false;
			} else {
				rrInfoLayout.setVisibility(View.GONE);
				isShowRr = true;
			}
			break;
		case R.id.cm_header_tv_right:
			Intent intent = null;
			if (enterTag == 1) {
				intent = new Intent(this, CalendarEditActivity.class);
			} else {
				intent = new Intent(this, CalendarCreateActivitys.class);
			}
			String weekStr = "";
			String weekNum = "";
			int type = optionsType.getCurrentItem();
			int pinlv = optionsNum.getCurrentItem();
			if (checkBox1.isChecked()) {
				weekStr = weekStr + "周一" + "/";
				weekNum = weekNum + "0" + ",";
			}
			if (checkBox2.isChecked()) {
				weekStr = weekStr + "周二" + "/";
				weekNum = weekNum + "1" + ",";
			}
			if (checkBox3.isChecked()) {
				weekStr = weekStr + "周三" + "/";
				weekNum = weekNum + "2" + ",";
			}
			if (checkBox4.isChecked()) {
				weekStr = weekStr + "周四" + "/";
				weekNum = weekNum + "3" + ",";
			}
			if (checkBox5.isChecked()) {
				weekStr = weekStr + "周五" + "/";
				weekNum = weekNum + "4" + ",";
			}
			if (checkBox6.isChecked()) {
				weekStr = weekStr + "周六" + "/";
				weekNum = weekNum + "5" + ",";
			}
			if (checkBox7.isChecked()) {
				weekStr = weekStr + "周日" + "/";
				weekNum = weekNum + "6" + ",";
			}
			intent.putExtra("type", type);
			intent.putExtra("pinlv", pinlv + 1);
			if (weekStr.length() > 0) {
				weekStr = weekStr.substring(0, weekStr.length() - 1);
			}
			intent.putExtra("week", weekStr);
			if (weekNum.length() > 0) {
				weekNum = weekNum.substring(0, weekNum.length() - 1);
			}
			intent.putExtra("weekNum", weekNum);
			startActivity(intent);
			finish();
			break;
		}
	}
}
