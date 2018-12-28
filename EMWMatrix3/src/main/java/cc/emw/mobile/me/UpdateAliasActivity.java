package cc.emw.mobile.me;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 更改导航别名
 */
@ContentView(R.layout.activity_update_alias)
public class UpdateAliasActivity extends BaseActivity {

    @ViewInject(R.id.cm_input_et_content)
    private EditText editContent;

    private int navId;
    private String navAlias;
    private int groupPos;
    private int childPos;
    private Dialog mLoadingDialog; // 加载框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        navId = getIntent().getIntExtra("nav_id", 0);
        navAlias = getIntent().getStringExtra("nav_alias");
        groupPos = getIntent().getIntExtra("group_position", -1);
        childPos = getIntent().getIntExtra("child_position", -1);
        editContent.setText(navAlias);
        CharSequence text = editContent.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable)text;
            Selection.setSelection(spanText, text.length());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event(value = {R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                HelpUtil.hideSoftInput(this, editContent);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                String alias = editContent.getText().toString().trim();
                if (!TextUtils.isEmpty(alias))
                    updateAlias(alias);
                else
                    ToastUtil.showToast(this, "名称不能为空");
                break;
        }
    }

    private void updateAlias(final String alias) {
        API.TemplateAPI.UpdateNavName(navId, alias, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(UpdateAliasActivity.this, R.string.bzm_submit_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(UpdateAliasActivity.this, "修改成功！", R.drawable.tishi_ico_gougou);
                    Intent data = new Intent();
                    data.putExtra("nav_alias", alias);
                    data.putExtra("group_position", groupPos);
                    data.putExtra("child_position", childPos);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });
    }
}
