package cc.emw.mobile.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.view.IconTextView;


@ContentView(R.layout.activity_chat_web_view)
public class ChatWebActivity extends BaseActivity {
    @ViewInject(R.id.webview_chat)
    private WebView mWebView;
    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mCmHeaderBtnLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWeb();
    }

    private void initWeb() {
        Intent intent = getIntent();
        String loadMoreWebUrl = intent.getStringExtra("load_more_web_url");
        mWebView.loadUrl(loadMoreWebUrl);
        mCmHeaderBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
