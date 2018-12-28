package cc.emw.mobile.me;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.UserFeedBack;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;

/**
 * 我·关于》意见反馈
 * @author zrjt
 */
@ContentView(R.layout.activity_about_suggestion)
public class SuggestionActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderSubmitTv;

    @ViewInject(R.id.et_suggestion_content)
    private EditText mContentEt;
    @ViewInject(R.id.et_suggestion_contact)
    private EditText mContactEt;

    private Dialog mLoadingDialog; // 加载框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.suggestion);
        mHeaderSubmitTv.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setText(R.string.submit);

    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                String content = mContentEt.getText().toString().trim();
                String contact = mContactEt.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    submitSuggest(content, contact);
                } else {
                    ToastUtil.showToast(SuggestionActivity.this, R.string.suggestion_empty_content);
                }
                break;
        }
    }

    /**
     * 提交意见反馈
     */
    private void submitSuggest(String content, String contact) {
        UserFeedBack ufb = new UserFeedBack();
        StringBuilder str = new StringBuilder(content);
        if (!TextUtils.isEmpty(contact)) {
            str.append("/").append(contact);
        }
        ufb.Content = str.toString();
        ufb.FeedType = 1;
        API.UserAPI.AddFeedBack(ufb, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                Toast.makeText(SuggestionActivity.this, ex.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(SuggestionActivity.this, R.string.suggestion_submit_success, R.drawable.tishi_ico_gougou);
                finish();
            }
        });
    }
}
