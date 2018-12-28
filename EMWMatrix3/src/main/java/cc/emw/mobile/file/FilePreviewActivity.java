package cc.emw.mobile.file;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ProgressWebView;


/**
 * 文件预览
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_preview)
public class FilePreviewActivity extends BaseActivity {

	public static final String EXTENSION = "extension";
	public static final String FILE_ID = "file_id";
	public static final String CREATOR = "creator";

	@ViewInject(R.id.cm_header_btn_left)
	private IconTextView mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private IconTextView mHeaderNoticeBtn; // 顶部条更多按钮
	private ProgressWebView webView;

	private static String url = "http://10.0.10.80:8000/pages/editor/ExcelEditor.html?id=107308&creator=103614&IsPhone=1";

	private String extension;
	private int fid, creator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Eyes.setStatusBarLightMode(this, Color.WHITE);
		setSwipeBackEnable(false);
		extension = getIntent().getStringExtra(EXTENSION);
		fid = getIntent().getIntExtra(FILE_ID, 0);
		creator = getIntent().getIntExtra(CREATOR, 0);
		if (extension != null) {
			if (extension.contains(".doc") || extension.contains(".docx")) {
//				url = Const.BASE_URL + "/pages/editor/Editor.html?id=" + fid + "&creator=" + creator +"&IsPhone=1";
				url = Const.BASE_URL + "/pages/editor/Editor.html?id=" + fid + "&IsPhone=1";
			} else if (extension.contains(".xls") || extension.contains(".xlsx")) {
				url = Const.BASE_URL + "/pages/editor/ExcelEditor.html?id=" + fid + "&IsPhone=1";
			} else {
				AlertDialog dialog = new AlertDialog(this).builder();
				dialog.setCancelable(false).setMsg("暂不支持此类型！");
				dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						scrollToFinishActivity();
					}
				}).show();
			}
		} else {
			AlertDialog dialog = new AlertDialog(this).builder();
			dialog.setCancelable(false).setMsg("打开失败！");
			dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					scrollToFinishActivity();
				}
			}).show();
		}

		syncCookie(this, ".emw.cc");

		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (webView != null){
			webView.removeAllViews();
			webView.destroy();
			webView = null;
		}

	}

	@Event({R.id.cm_header_btn_left, R.id.cm_header_tv_right})
	private void onHeaderClick(View v) {
		switch (v.getId()) {
			case R.id.cm_header_btn_left:
				onBackPressed();
				break;
			case R.id.cm_header_tv_right:
				break;
		}
	}

	/**
	 * 将cookie同步到WebView
	 * @param url WebView要加载的url
	 * @return true 同步cookie成功，false同步cookie失败
	 */
	public static boolean syncCookie(Context context, String url) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.createInstance(context);
		}
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();//移除
		cookieManager.removeAllCookie();
		LoginResp loginResp = PrefsUtil.readLoginCookie();
		String cookieStr = "u="+loginResp.u+"; c="+loginResp.c+"; s="+loginResp.s+"; p="+loginResp.p;
		Log.e("file_preview", "cookie:"+cookieStr);
		cookieManager.setCookie(url, "u="+loginResp.u);
		cookieManager.setCookie(url, "c="+loginResp.c);
		cookieManager.setCookie(url, "s="+loginResp.s);
		cookieManager.setCookie(url, "p="+loginResp.p);
		CookieSyncManager.getInstance().sync();
		String newCookie = cookieManager.getCookie(url);
		Log.e("formweb", "newCookie:"+newCookie);
		return TextUtils.isEmpty(newCookie)?false:true;
	}

	private void initData() {
		mHeaderBackBtn.setIconText("eb68");
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderNoticeBtn.setVisibility(View.GONE);
		mHeaderTitleTv.setText("预览");
		webView = (ProgressWebView) findViewById(R.id.webview);
		WebSettings setting = webView.getSettings();
		setting.setJavaScriptEnabled(true);
		setting.setJavaScriptCanOpenWindowsAutomatically(true);
		setting.setAllowFileAccess(true);// 设置允许访问文件数据
		setting.setSupportZoom(true);
		setting.setBuiltInZoomControls(true);
		setting.setJavaScriptCanOpenWindowsAutomatically(true);
//		setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		setting.setCacheMode(WebSettings.LOAD_DEFAULT);
		setting.setDomStorageEnabled(true);
		setting.setDatabaseEnabled(true);
//		setting.setDefaultTextEncodingName("GBK");//设置字符编码
		webView.setOnWebCallBack(new ProgressWebView.OnWebCallBack() {
			@Override
			public void getTitle(String title) {
				if (title != null && !title.startsWith("http")) {
					mHeaderTitleTv.setText(title);
				}
			}

			@Override
			public void getUrl(String url) {

			}

			@Override
			public void loadFinish() {

			}
		});

		webView.loadUrl(url);
	}

}