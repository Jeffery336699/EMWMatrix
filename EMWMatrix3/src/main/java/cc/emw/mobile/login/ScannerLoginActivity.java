package cc.emw.mobile.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.zxing.Result;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.sale.BeepManager;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * 扫码PC自动登录
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.scanner)
public class ScannerLoginActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    
	@ViewInject(R.id.cm_header_btn_left)
	private IconTextView mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	
	private ZXingScannerView mScannerView;
    private BeepManager beepManager;
    
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        setSwipeBackEnable(false);
        initView();
        beepManager = new BeepManager(this);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        /*mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };*/
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    private void initView() {
        mHeaderBackBtn.setIconText("eb68");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("扫一扫");
	}
    
    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left:
//				HelpUtil.hideSoftInput(this, mContentEt);
				finish();
				break;
			case R.id.cm_header_tv_right:
				
				break;
    	}
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
//    	Toast.makeText(this, "Contents = " + rawResult.getText() +
//                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
    	beepManager.playBeepSoundAndVibrate();

        if (rawResult != null && rawResult.getText() != null) {
            if (rawResult.getText().startsWith("Login_")) { //扫码登录
                Intent intent = new Intent(this, ScannerPCActivity.class);
                intent.putExtra("token", rawResult.getText());
                startActivity(intent);
            } else if (rawResult.getText().startsWith("OpenVer_")) { //开启两步验证
                Intent intent = new Intent();
                intent.putExtra("uid", rawResult.getText().substring(rawResult.getText().indexOf("_")+1));
                setResult(Activity.RESULT_OK, intent);
            } else {
                Intent intent = new Intent();
                intent.putExtra("result", rawResult.getText());
                setResult(Activity.RESULT_OK, intent);
            }
        } else {
            ToastUtil.showToast(ScannerLoginActivity.this, "扫码失败！");
        }
		finish();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScannerLoginActivity.this);
            }
        }, 2000);
    }

}
