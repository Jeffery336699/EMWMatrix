package cc.emw.mobile.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.util.Prefs;
import cc.emw.mobile.util.ToastUtil;

/**
 * 我·系统设置》修改密码检查
 */
@ContentView(R.layout.activity_pw_checking)
public class PwCheckingActivity extends BaseActivity {

	@ViewInject(R.id.et_checking_password)
	private EditText mPassWordEt; //
	@ViewInject(R.id.btn_checking_cancel)
	private Button mCancelBtn; //
	@ViewInject(R.id.btn_checking_ok)
	private Button mOkBtn; //

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Event({ R.id.btn_checking_cancel, R.id.btn_checking_ok })
	private void onFooterClick(View v) {
		switch (v.getId()) {
		case R.id.btn_checking_cancel:
			onBackPressed();
			break;
		case R.id.btn_checking_ok:
			if (Prefs.getString("password", "").equals(
					mPassWordEt.getText().toString())) {
				Intent intent = new Intent(this, PwChangeActivity.class);
				startActivity(intent);
				finish();
			} else {
				ToastUtil.showToast(this, R.string.mechecking_password_error);
			}
			break;
		}
	}
}
