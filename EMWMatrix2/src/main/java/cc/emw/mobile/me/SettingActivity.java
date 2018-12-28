package cc.emw.mobile.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.view.SwitchButton;

/**
 * 我·系统设置
 * @author zrjt
 */
@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity implements
		OnCheckedChangeListener {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;

	@ViewInject(R.id.sb_setting_message)
	private SwitchButton mReceiverSb;// 接受提醒
	@ViewInject(R.id.sb_setting_voice)
	private SwitchButton mVoiceSb;// 声音提醒

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();

	}

	private void initView() {
		mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(R.string.mesetting);
		mReceiverSb.setOnCheckedChangeListener(this);
	}

	@Event(value = { R.id.cm_header_btn_left, R.id.ll_setting_receiver_msg,
			R.id.ll_setting_receiver_voice, R.id.ll_setting_secure })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		case R.id.ll_setting_receiver_msg:
			mReceiverSb.toggle();
			break;
		case R.id.ll_setting_receiver_voice:
			if (mReceiverSb.isChecked()) {
				mVoiceSb.toggle();
			}
			break;
		case R.id.ll_setting_secure:
			setStartAnim(false);
			Intent intent = new Intent(this, PwCheckingActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			mVoiceSb.setEnabled(true);
		} else {
			mVoiceSb.setEnabled(false);
			mVoiceSb.setChecked(false);
		}
	}

}
