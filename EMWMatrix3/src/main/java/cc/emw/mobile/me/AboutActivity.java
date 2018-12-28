package cc.emw.mobile.me;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.sourceforge.simcpux.Util;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.login.presenter.LoginPresenter;
import cc.emw.mobile.login.view.LoginView;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.ToastUtil;
import io.socket.client.Socket;

/**
 * 我·关于
 */
@ContentView(R.layout.activity_about)
public class AboutActivity extends BaseActivity implements LoginView {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;

    @ViewInject(R.id.tv_about_version)
    private TextView mVersionTv;
    private LoginPresenter loginPresenter;
    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("关于");
        loginPresenter = new LoginPresenter(this);
        mLoadingDialog = createLoadingDialog("正在检测...");

        try {
            PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersionTv.setText(packInfo.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.tv_about_suggestion, R.id.tv_about_upgrade})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.tv_about_suggestion:
//                Intent intent = new Intent(this, SuggestionActivity.class);
//                intent.putExtra("start_anim", false);
//                int[] location = new int[2];
//                v.getLocationInWindow(location);
//                intent.putExtra("click_pos_y", location[1]);
//                startActivity(intent);

                IWXAPI api = WXAPIFactory.createWXAPI(this, "wx20910707a1e94327");
                api.registerApp("wx20910707a1e94327");

                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = "http://blog.csdn.net/yanzhenjie1003/article/details/44887341";
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = "WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
                msg.description = "WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
                bmp.recycle();
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("test");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                api.sendReq(req);
                break;
            case R.id.tv_about_upgrade:
                mLoadingDialog.show();
                try {
                    PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    loginPresenter.checkVersion(packInfo.versionCode);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void saveLoginInfo(LoginResp respInfo) {
    }

    @Override
    public void navigateToIndex() {
    }

    @Override
    public void showTipDialog(String tip) {
    }

    @Override
    public void showTipToast(String tip) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        ToastUtil.showToast(this, tip);
    }

    @Override
    public void showVersionDialog(Version ver) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        DialogUtil.showVersionDialog(this, ver);
    }

    @Override
    public Socket getIOSocket() {
        EMWApplication emw = (EMWApplication) getApplication();
        Socket mSocket = emw.getAppIOSocket();
        return mSocket;
    }
}
