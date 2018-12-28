package cc.emw.mobile.task.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.task.entity.UserLabelBean;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by chengyong.liu on 2016/7/1.
 * 新建标签
 */
@ContentView(R.layout.activity_create_task_label_2)
public class TaskCreateLabelActivity extends BaseActivity {
    @ViewInject(R.id.et_label_name)
    private EditText mEtName;
    @ViewInject(R.id.tv_lable_title)
    private TextView mLableTitle;
    private String mLabelName;
    private UserLabelBean mUserLabel;

    public static final String TASK_LABEL_NAME = "task_label_name";
    public static final String TASK_LABLE = "task_lable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        mUserLabel = (UserLabelBean) getIntent().getSerializableExtra(TASK_LABLE);
        if (mUserLabel != null) {
            mEtName.setText(mUserLabel.Name);
            mLableTitle.setText("修改标签");
            mEtName.setSelection(mUserLabel.Name.length());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event({R.id.btn_ok, R.id.btn_cancel, R.id.finish})
    private void click(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_ok:
                if (TextUtils.isEmpty(mEtName.getText().toString().trim())) {
                    ToastUtil.showToast(TaskCreateLabelActivity.this, "请输入标签名字!");
                    return;
                }
                mLabelName = mEtName.getText().toString().trim();
                Intent intent = new Intent();
                if (mUserLabel != null) {
                    mUserLabel.Name = mLabelName;
                    intent.putExtra(TASK_LABLE, mUserLabel);
                } else {
                    intent.putExtra(TASK_LABEL_NAME, mLabelName);
                }
                setResult(Activity.RESULT_OK, intent);
                onBackPressed();
                break;
            /*case R.id.finish:
                finish();
                break;*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        HelpUtil.hideSoftInput(this, mEtName);
    }
}
