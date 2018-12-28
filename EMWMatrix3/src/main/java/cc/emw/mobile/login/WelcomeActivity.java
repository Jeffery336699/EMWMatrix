package cc.emw.mobile.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.xutils.view.annotation.Event;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.login.presenter.LoginPresenter;
import cc.emw.mobile.login.view.LoginView;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.util.PrefsUtil;
import io.socket.client.Socket;

import static cc.emw.mobile.util.PrefsUtil.readUserInfo;

/**
 * 登录或注册
 *
 * @author shaobo.zhuang
 */
//@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity implements LoginView {

    /*@ViewInject(R.id.btn_welcome_login)
    private Button mBtnLogin;
    @ViewInject(R.id.btn_welcome_register)
    private Button mBtnRegister;*/

    private LoginPresenter loginPresenter;
    public static final String ACTION_WELCOME_FINISH = "cc.emw.mobile.welcome_finish"; //
    private MyBroadcastReceive mReceive;

    @Override
    public void onCreate(Bundle state) {
        getIntent().putExtra("expand_anim", false);
        super.onCreate(state);

        Log.d("login_info", "WelcomeActivity onCreate");
        setContentView(R.layout.activity_welcome);
        if (PrefsUtil.isFirst()) { // 第一次启动APP，进入引导界面
            PrefsUtil.setFirst(false);
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        } else if (readUserInfo() != null && PrefsUtil.readLoginCookie() != null && !PrefsUtil.isSwitch()) {//
            UserInfo info=PrefsUtil.readUserInfo();
//            mBtnLogin.setVisibility(View.INVISIBLE);
//            mBtnRegister.setVisibility(View.INVISIBLE);
            loginPresenter = new LoginPresenter(WelcomeActivity.this);
            loginPresenter.getPersonList();
            boolean isLoginRY = false;
            if (!TextUtils.isEmpty(PrefsUtil.readUserInfo().RongYunToken)) {
                isLoginRY = true;
            }
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.putExtra("isLoginRY", isLoginRY);
            startActivity(intent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        } else {
            Intent loginIntent = new Intent(this, LoginActivity2.class);
            startActivity(loginIntent);
            //解决三星手机会多次显示
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    finish();
//                }
//            }, 1000);
        }

        setSwipeBackEnable(false);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_WELCOME_FINISH);
        mReceive = new MyBroadcastReceive();
        registerReceiver(mReceive, intentFilter); // 注册监听
    }

    @Event(value = {R.id.btn_welcome_login, R.id.btn_welcome_register})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.btn_welcome_login:
                Intent loginIntent = new Intent(this, LoginActivity2.class);
                startActivity(loginIntent);
                finish();
                break;
            case R.id.btn_welcome_register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mReceive != null)
            unregisterReceiver(mReceive);
        super.onDestroy();
        Log.d("login_info", "welcome finish");
    }

    @Override
    public void saveLoginInfo(LoginResp respInfo) {

    }

    @Override
    public void navigateToIndex() {
        finish();
    }

    @Override
    public void showTipDialog(String tip) {

    }

    @Override
    public void showTipToast(String tip) {

    }

    @Override
    public void showVersionDialog(Version ver) {

    }

    @Override
    public Socket getIOSocket() {
        EMWApplication emw = (EMWApplication) getApplication();
        Socket mSocket = emw.getAppIOSocket();
        return mSocket;
    }


    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_WELCOME_FINISH.equals(action)) {
                //finish();
            }
        }
    }
}
