package cc.emw.mobile.login;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.model.bean.MessageInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;

/**
 * 扫码PC确认登录
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_scanner_pc)
public class ScannerPCActivity extends BaseActivity {
    
	@ViewInject(R.id.cm_header_btn_left)
	private IconTextView mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题

    private Dialog mLoadingDialog; //加载框
    private String token; //传值

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        setSwipeBackEnable(false);
        token = getIntent().getStringExtra("token");

        initView();
    }

    private void initView() {
        mHeaderBackBtn.setIconText("eb68");
        mHeaderBackBtn.setVisibility(View.GONE);
        mHeaderTitleTv.setText("扫码");
	}
    
    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left:
				onBackPressed();
				break;
			case R.id.cm_header_tv_right:
				
				break;
    	}
    }

    @Event(value = {R.id.btn_scannerpc_ok, R.id.btn_scannerpc_cancel})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scannerpc_ok:
                send(token);
                break;
            case R.id.btn_scannerpc_cancel:
                onBackPressed();
                break;
        }
    }


    private void send(String token) {
        ApiEntity.Message message = new MessageInfo();
        message.Type = 29;

        ScannerLogin scannerLogin = new ScannerLogin();
        scannerLogin.PublicKey = token;
        if (PrefsUtil.readLoginCookie() != null) {
            scannerLogin.c = PrefsUtil.readLoginCookie().c;
            scannerLogin.p = PrefsUtil.readLoginCookie().p;
            scannerLogin.s = PrefsUtil.readLoginCookie().s;
            scannerLogin.u = PrefsUtil.readLoginCookie().u;
            scannerLogin.un = PrefsUtil.readUserInfo().Code;
        }

        message.Content = new Gson().toJson(scannerLogin);
        Logger.d("pc", new Gson().toJson(message));
        API.Message.SendByToken(token, message, new RequestCallback<String>(
                String.class) {
//        API.Message.Send(message, true, new RequestCallback<String>(
//                String.class) {
            @Override
            public void onStarted() {
                mLoadingDialog = DialogUtil.createLoadingDialog(ScannerPCActivity.this, R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                ToastUtil.showToast(ScannerPCActivity.this, "登录失败!");
            }

            @Override
            public void onSuccess(String result) {
                Logger.d("pc", "result:"+result);
                //返回的是messageID
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                onBackPressed();
            }
        });
    }

    class ScannerLogin {
        public String PublicKey;
        public String c;
        public String p;
        public String s;
        public int u;
        public String un;
    }
}
