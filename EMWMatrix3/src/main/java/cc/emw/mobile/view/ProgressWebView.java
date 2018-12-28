package cc.emw.mobile.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import cc.emw.mobile.util.DisplayUtil;

/**
 * 带进度条的WebView
 * @author shaobo.zhuang
 *
 */
public class ProgressWebView extends WebView {

    private Context context;
    private ProgressBar progressbar;
    private OnWebCallBack onWebCallBack;   //回调
    private boolean hasProgressbar = true;
    public void setShowProgressbar(boolean hasProgressbar) {
        this.hasProgressbar = hasProgressbar;
        progressbar.setVisibility(hasProgressbar ? VISIBLE : GONE);
    }

    public ProgressWebView(Context context) {
        this(context, null);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webTextViewStyle);
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        init();

        setWebViewClient(new MyWebViewClient());
        setWebChromeClient(new WebChromeClient());
    }

    /**
     * 设置ProgressBar
     */
    void init() {
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(getContext(), 5), 0, 0));
        addView(progressbar);
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if (hasProgressbar) {
                    progressbar.setVisibility(GONE);
                }
                if (onWebCallBack != null) {
                    onWebCallBack.loadFinish();
                }
            } else {
                if (hasProgressbar) {
                    progressbar.setVisibility(VISIBLE);
                    progressbar.setProgress(newProgress);
                }
            }
            super.onProgressChanged(view, newProgress);
        }


        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (onWebCallBack != null) {  //获取标题
                onWebCallBack.getTitle(title);
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.e("formweb", "onJsAlert:"+message);
            /*new com.zf.iosdialog.widget.AlertDialog(context).builder()
                    .setMsg(message)
                    .setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();*/
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            Log.e("formweb", "onJsConfirm:"+message);
            return super.onJsConfirm(view, url, message, result);
        }

    }

    /**
     * 不重写的话，会跳到手机浏览器中
     *
     * @author admin
     */
    public class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) { // Handle the
            goBack();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (onWebCallBack != null) { //获得WebView的地址
                onWebCallBack.getUrl(url);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); //忽略SSL证书错误，继续加载页面
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 设置WebView的回掉器
     *
     * @param onWebCallBack
     */
    public void setOnWebCallBack(OnWebCallBack onWebCallBack) {
        this.onWebCallBack = onWebCallBack;
    }

    public interface OnWebCallBack {
        /**
         * 获取标题
         *
         * @param title
         */
        void getTitle(String title);

        /**
         * 获得WebView的地址
         *
         * @param url
         */
        void getUrl(String url);

        void loadFinish();
    }


}