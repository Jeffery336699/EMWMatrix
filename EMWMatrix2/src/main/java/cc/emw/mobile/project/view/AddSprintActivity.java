package cc.emw.mobile.project.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.entities.UserSprint;
import cc.emw.mobile.util.ToastUtil;

/**
 * 添加冲刺页面
 * @author jven.wu
 */
@ContentView(R.layout.activity_add_sprint)
public class AddSprintActivity extends BaseActivity {
	private static final String TAG = "AddSprintActivity";
	public static final String ADD_SPRINT = "cc.emw.mobile.add_sprint";
	@ViewInject(R.id.et_sprintcreate_name)
	private EditText mNameEt;
	@ViewInject(R.id.et_sprintcreate_desc)
	private EditText mDescEt;
	@ViewInject(R.id.btn_sprintcreate_cancel)
	private Button mCancelBtn;
	@ViewInject(R.id.btn_sprintcreate_ok)
	private Button mOkBtn;
	private Dialog mLoadingDialog; //加载框
	private UserSprint userSprint;
	private Handler mHandler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

	@Override
	public void onBackPressed() {
		finish();
	}

	@Event({R.id.btn_sprintcreate_cancel, R.id.btn_sprintcreate_ok})
    private void onFooterClick(final View v) {
    	switch (v.getId()) {
			case R.id.btn_sprintcreate_cancel:
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						hideSoftInput(v);
					}
				},0);
				onBackPressed();
				break;
			case R.id.btn_sprintcreate_ok:
				String name = mNameEt.getText().toString();
		    	String desc = mDescEt.getText().toString();
		    	if (!TextUtils.isEmpty(name)) {
		    		userSprint = new UserSprint();
		    		userSprint.setName(name);
		    		saveSprint();
		    	} else {
					ToastUtil.showToast(this, getResources().getString(R.string.input_sprint_name));
		    	}
				break;
    	}
    }
    /**
     * 新建项目冲刺
     */
    private void saveSprint() {
    	API.TalkerAPI.AddSprint(userSprint, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mLoadingDialog.dismiss();
				ToastUtil.showToast(getApplicationContext(), 
						getResources().getString(R.string.add_sprint_fail));
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(
						getResources().getString(R.string.creating));
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(
							getApplicationContext(), 
							getResources().getString(R.string.add_sprint_success),
							R.drawable.tishi_ico_gougou);
					Intent intentBroadCast = new Intent();
					intentBroadCast.setAction(ADD_SPRINT);
					sendBroadcast(intentBroadCast);
					finish();
				} else {
					ToastUtil.showToast(getApplicationContext(), 
							getResources().getString(R.string.add_sprint_fail));
				}
			}
    	});
    }

    /**
     * 从控件所在的窗口中隐藏软键盘
     */
    private void hideSoftInput(View view) {
        Log.d(TAG,"hideSoftInput()->");
        InputMethodManager imm1 = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //从控件所在的窗口中隐藏
        imm1.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
