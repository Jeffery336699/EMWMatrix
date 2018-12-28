package cc.emw.mobile.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.statusbar.Eyes;

/**
 * 验证码，旧的
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_auth_code)
public class AuthCodeActivity extends BaseActivity {

    @ViewInject(R.id.et_authcode_code)
    private EditText mAuthCodeEt;
    @ViewInject(R.id.btn_authcode_next)
    private Button mNextBtn;

    private Dialog mLoadingDialog; //加载框
    private String regPhone; //传值

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        regPhone = getIntent().getStringExtra("reg_phone");

        mAuthCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(mAuthCodeEt.getText().toString().trim())) {
                    mNextBtn.setEnabled(true);
                } else {
                    mNextBtn.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Event(value = {R.id.itv_authcode_cancel, R.id.btn_authcode_next})
    private void onActionClick(View v) {
        switch (v.getId()) {
            case R.id.itv_authcode_cancel:
                HelpUtil.hideSoftInput(this, mAuthCodeEt);
                onBackPressed();
                break;
            case R.id.btn_authcode_next:
                HelpUtil.hideSoftInput(this, mAuthCodeEt);
                String inputCode = mAuthCodeEt.getText().toString().trim();
                checkRegisterSMS(regPhone, inputCode);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 10:
                    finish();
                    break;
            }
        }
    }


    private void checkRegisterSMS(final String phone, String code) {
        API.UserAPI.CheckRegisterSMS(phone, code, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                new AlertDialog(AuthCodeActivity.this).builder().setTitle("验证码不正确，请重新输入 ")
                        .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }

            @Override
            public void onSuccess(String result) {
                Log.d("authcode", "check:"+result);
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) == 1) {
                    Intent intent = new Intent(AuthCodeActivity.this, RegisterUserActivity.class);
                    intent.putExtra("reg_phone", phone);
                    startActivity(intent);
                } else {
                    onError(null, false);
                }
            }
        });
    }
}
