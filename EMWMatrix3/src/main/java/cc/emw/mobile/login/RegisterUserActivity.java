package cc.emw.mobile.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.MaterialEditText;

/**
 * 注册用户
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_register_user)
public class RegisterUserActivity extends BaseActivity implements TextWatcher {

    @ViewInject(R.id.et_reguser_username)
    private MaterialEditText mUserNameEt;
    @ViewInject(R.id.et_reguser_password)
    private EditText mPasswordEt;
    @ViewInject(R.id.iv_reguser_visibility)
    private ImageView mVisibilityIv;
    @ViewInject(R.id.btn_reguser_register)
    private Button mRegisterBtn;

    private Dialog mLoadingDialog; //加载框
    private String regPhone; //传值
    private boolean showPwd = true;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        regPhone = getIntent().getStringExtra("reg_phone");

        mUserNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mUserNameEt.getText()) && !hasFocus) {
                    mUserNameEt.validateWith(new RegexpValidator("邮箱地址格式错误", StringUtils.EMAIL_REGEX));
                }
            }
        });
        mUserNameEt.addTextChangedListener(this);
        mPasswordEt.addTextChangedListener(this);
    }

    @Event(value = {R.id.itv_reguser_cancel, R.id.iv_reguser_visibility, R.id.btn_reguser_register})
    private void onActionClick(View v) {
        switch (v.getId()) {
            case R.id.itv_reguser_cancel:
                HelpUtil.hideSoftInput(this, mPasswordEt);
                onBackPressed();
                break;
            case R.id.iv_reguser_visibility:
                if (showPwd){
                    //设置EditText文本为可见的
                    mPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mVisibilityIv.setImageResource(R.drawable.ic_visibility_black);
                    showPwd = false;
                } else {
                    //设置EditText文本为隐藏的
                    mPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mVisibilityIv.setImageResource(R.drawable.ic_visibility_off_black);
                    showPwd = true;
                }
                if (mPasswordEt.hasFocus()) {
                    CharSequence text = mPasswordEt.getText();
                    if (text instanceof Spannable) {
                        Spannable spanText = (Spannable)text;
                        Selection.setSelection(spanText, text.length());
                    }
                }
                break;
            case R.id.btn_reguser_register:
                HelpUtil.hideSoftInput(this, mPasswordEt);
                String email = mUserNameEt.getText().toString().trim();
                String pwd = mPasswordEt.getText().toString().trim();
                addPubUser(email, pwd, regPhone);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String email = mUserNameEt.getText().toString().trim();
        String password = mPasswordEt.getText().toString();
        if (!TextUtils.isEmpty(email) && StringUtils.isEmail(email) && !TextUtils.isEmpty(password)
                && mPasswordEt.length() >= 8) {
            mRegisterBtn.setEnabled(true);
        } else {
            mRegisterBtn.setEnabled(false);
        }
    }
    @Override
    public void afterTextChanged(Editable s) {}


    private void addPubUser(String email, String pwd, String phone) {
        ApiEntity.UserInfo user = new ApiEntity.UserInfo();
        user.Name = "";
        user.CompanyCode = "";
        user.Email = email;
        user.Password = pwd;
        user.Phone = phone;
        API.UserPubAPI.JoinRegiter(user, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (throwable instanceof ConnectException) {
                    DialogUtil.showNetworkSetDialog(RegisterUserActivity.this);
                } else {
                    ToastUtil.showToast(RegisterUserActivity.this, "注册失败！");
                }
            }

            @Override
            public void onSuccess(String result) {
                Log.d("authcode", "register:"+result);
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    if (Integer.valueOf(result) > 0) {
                        /*Intent intent = new Intent(RegisterUserActivity.this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*/
                        ToastUtil.showToast(RegisterUserActivity.this, "注册成功！", R.drawable.tishi_ico_gougou);
                        finish();
                    } else if (Integer.valueOf(result) == -1) {
                        ToastUtil.showToast(RegisterUserActivity.this, "该邮箱已被注册！");
                        mUserNameEt.setError("该邮箱已被注册");
                        mUserNameEt.postInvalidate();
                    } else {
                        onError(null, false);
                    }
                } else {
                    onError(null, false);
                }
            }
        });
    }
}
