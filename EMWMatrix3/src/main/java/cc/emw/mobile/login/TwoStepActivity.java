package cc.emw.mobile.login;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.me.adapter.TwoStepAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;

/**
 * 登录·两步验证
 */
@ContentView(R.layout.activity_two_step2)
public class TwoStepActivity extends BaseActivity {

    private static final String TAG = TwoStepActivity.class.getSimpleName();

    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;

    @ViewInject(R.id.lv_twostep_user)
    private ListView mListView;
    @ViewInject(R.id.ll_twostep_blank)
    private LinearLayout mBlankLayout; // 空视图

    private Dialog mLoadingDialog;
    private TwoStepAdapter twoStepAdapter;
    private ArrayList<UserInfo> dataList;
    private Handler handler;
    private Timer timer;
    private TimerTask task;
    private int countDown = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        mHeaderBackBtn.setIconText("eb68");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("两步验证");

        timer = new Timer();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                countDown++;
                countDown++;
                if (countDown == 100) {
                    countDown = 0;
                }
                twoStepAdapter.setCountDown(countDown);
                twoStepAdapter.notifyDataSetChanged();
            }
        };
        task = new MyTimerTask();

        twoStepAdapter = new TwoStepAdapter(this);
        dataList = new ArrayList<>();
        twoStepAdapter.setData(dataList);
        mListView.setAdapter(twoStepAdapter);

        getUserInfoForIMEI();
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

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                break;
        }
    }

    /**
     * 获取本设备绑定的所有用户信息
     */
    private void getUserInfoForIMEI() {
        Log.e("TwoStepActivity","login getUserInfoForIME = "+getIMEI());
        API.UserAPI.GetUserInfoForIMEI(getIMEI(), new RequestCallback<UserInfo>(UserInfo.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                mBlankLayout.setVisibility(View.GONE);
                ToastUtil.showToast(TwoStepActivity.this, "获取信息失败！");
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    mBlankLayout.setVisibility(View.GONE);
                    dataList.clear();
                    dataList.addAll(respList);
                    twoStepAdapter.setData(dataList);
                    twoStepAdapter.notifyDataSetChanged();
                    timer.schedule(task, 0, 1000);
                } else {
                    mBlankLayout.setVisibility(View.VISIBLE);
                }
            }
        });
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
