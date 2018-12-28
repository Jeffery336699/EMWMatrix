package io.rong.callkit;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.login.LoginActivity2;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.me.SettingActivity;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.statusbar.Eyes;

import com.farsunset.cim.client.android.CIMPushManager;
import com.githang.androidcrash.util.AppManager;
import com.zf.iosdialog.widget.AlertDialog;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class TerminalLoginDialogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.TRANSPARENT);
        setSwipeBackEnable(false);
        CIMPushManager.stop(this);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null)
            manager.cancelAll();
        int reson = getIntent().getIntExtra("reason", 0);
        AlertDialog dialog = new AlertDialog(this).builder();
        dialog.setMsg("您的账号在其它地方登录,或被服务器强制下线").setPositiveButton(getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppManager.AppExit(getApplicationContext());
                sendBroadcast(new Intent(MainActivity.ACTION_FINISH_ACTIVITY));
                Intent intent = new Intent(getApplicationContext(), LoginActivity2.class);
                startActivity(intent);
                AppManager.finishActivity(MainActivity.class);
                finish();
            }
        }).setCancelable(false).show();


    }


    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

