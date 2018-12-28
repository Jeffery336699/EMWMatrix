package cc.emw.mobile.me;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.login.ScannerLoginActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SectorProgressView;

/**
 * 我·两步验证
 */
@ContentView(R.layout.activity_two_step)
public class TwoStepActivity extends BaseActivity {

    private static final String TAG = TwoStepActivity.class.getSimpleName();

    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_btn_right)
    private IconTextView mHeaderScannerBtn;

    @ViewInject(R.id.ll_twostep_code)
    private LinearLayout mCodeLayout;
    @ViewInject(R.id.tv_twostep_code)
    private TextView mCodeTv;
    @ViewInject(R.id.tv_twostep_countdown)
    private TextView mCountDownTv;
    @ViewInject(R.id.spv)
    private SectorProgressView mSectorProgressView;
    @ViewInject(R.id.tv_twostep_username)
    private TextView mUserNameTv;

    private Dialog mLoadingDialog;
    private Handler handler;
    private Timer timer;
    private TimerTask task;
    private int countDown = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
        mHeaderBackBtn.setIconText("eb68");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("两步验证");
        mHeaderScannerBtn.setIconText("ea5d");
        mHeaderScannerBtn.setVisibility(View.VISIBLE);

        mCodeLayout.setVisibility(View.GONE);
        mUserNameTv.setText(PrefsUtil.readUserInfo().Code);

        timer = new Timer();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                countDown++;
                countDown++;
                if (countDown == 100) {
                    getCode();
                    countDown = 0;
                }
                mSectorProgressView.setPercent(countDown);
            }
        };
        task = new MyTimerTask();

        getUserIMEI();
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    }

    @Override
    protected void onDestroy() {
        if (timer != null)
            timer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                String uid = data.getStringExtra("uid");
                if (!TextUtils.isEmpty(uid) && TextUtils.isDigitsOnly(uid)
                        && Integer.valueOf(uid) == PrefsUtil.readUserInfo().ID) {
                    updateUserIMEI();
                } else {
                    AlertDialog dialog = new AlertDialog(this).builder();
                    dialog.setCancelable(true).setMsg("与当前登录用户不匹配!");
                    dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }
            }
        }
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                Intent intent = new Intent(this, ScannerLoginActivity.class);
                startActivityForResult(intent, 101);
                break;
        }
    }

    /**
     * 获取当前用户IMEI
     * 从服务器获取返回信息为空
     *
     */
    private void getUserIMEI() {
        API.UserAPI.GetUserIMEI(PrefsUtil.readUserInfo().ID, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.e(TAG,"IMEI  throwable="+throwable.toString());
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(TwoStepActivity.this, "获取信息失败！");
            }

            @Override
            public void onSuccess(String result) {
                try {
                    String imei = new Gson().fromJson(result, String.class);
                    Logger.d(TAG, "imei son====:"+result);
                    if (imei.equals(getIMEI())) {
                        if (task != null) {
                            task.cancel();
                        }
                        task = new MyTimerTask();
                        timer.schedule(task, 0, 1000);
                        getCode();
                    } else {
                        //onError(new Throwable(""), false);
                        //重新获取用户的IMEI参数
                        updateUserIMEI();
                        Logger.d(TAG, "getimei:"+result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 更新用户IMEI
     */
    private void updateUserIMEI() {
        String imei = getIMEI();
        Logger.d(TAG, " updateUserIMEI imei:"+imei+"  getIMEI = "+getIMEI());
        API.UserAPI.UpdateUserIMEI(getIMEI(), PrefsUtil.readUserInfo().ID, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                AlertDialog dialog = new AlertDialog(TwoStepActivity.this).builder();
                dialog.setCancelable(true).setMsg("刷新动态码失败！");
                dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    if (task != null) {
                        task.cancel();
                    }
                    task = new MyTimerTask();
                    timer.schedule(task, 0, 1000);
                    getCode();
                } else {
                    onError(new Throwable(""), false);
                    Logger.d(TAG, "updateimei:"+result);
                }
            }
        });
    }

    /**
     * 更新动态码
     * @param code
     */
    private void addVerificationCode(final String code) {
        String key = code.replace(" ", "");
        Logger.d(TAG, "key:"+key);
        API.UserAPI.AddVerificationCode(key, PrefsUtil.readUserInfo().ID, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (timer != null) timer.cancel();
                countDown = 0;
                AlertDialog dialog = new AlertDialog(TwoStepActivity.this).builder();
                dialog.setCancelable(true).setMsg("刷新动态码失败！");
                dialog.setPositiveButton("关闭", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }).show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    mCodeTv.setText(code);
                    mCodeLayout.setVisibility(View.VISIBLE);
                    countDown = 0;
                } else {
                    onError(new Throwable(""), false);
                    Logger.d(TAG, "addVerificationCode:"+result);
                }
            }
        });
    }

    /**
     * 获取动态码，并更新到服务器
     */
    private void getCode() {
        mCodeTv.setText("--- ---");
        mCountDownTv.setText("");
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6 ; i++) {
            code.append((int)(Math.random()*10));
            if (i == 2)
                code.append(" ");
        }
        addVerificationCode(code.toString());
    }

    /**
     * 获取本设备IMEI
     * @return
     */
    private String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        boolean isImei = check(imei);
        if (!TextUtils.isEmpty(imei)) {
            isImei = check(imei);
            if (isImei)
                return imei;
        }

        if (TextUtils.isEmpty(imei) || !isImei) {
            // start get mac address
            WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiMan != null) {
                WifiInfo wifiInf = wifiMan.getConnectionInfo();
                if (wifiInf != null && wifiInf.getMacAddress() != null) {//48位，如FA:34:7C:6D:E4:D7
                    imei = wifiInf.getMacAddress().replaceAll(":", "");
                    return imei;
                }
            }
        }
        if (TextUtils.isEmpty(imei) || !isImei) {
            imei = UUID.randomUUID().toString().replaceAll("-", "");//UUID通用唯一识别码(Universally Unique Identifier)（128位，如3F2504E0-4F89-11D3-9A0C-0305E82C3301）
        }
        return imei;
    }

    private boolean check(String imei) {
        if (!TextUtils.isEmpty(imei) && TextUtils.isDigitsOnly(imei) && imei.length() == 15 && !"000000000000000".equals(imei)) {
            return true;
        } else {
            return false;
        }
    }
}
