package cc.emw.mobile.me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;

@ContentView(R.layout.activity_intimate)
public class IntimateActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;

    @ViewInject(R.id.cb_self_all)
    private IconTextView allIconText;
    @ViewInject(R.id.cb_self_concern)
    private IconTextView concernText;
    @ViewInject(R.id.cb_self_group)
    private IconTextView groupText;
    @ViewInject(R.id.cb_self_complete)
    private IconTextView completeText;
    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mLeftBtn;

    private int type;

    private int value = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText("隐私");

        type = getIntent().getIntExtra("intimate", 0);
        switch (type) {
            case 0:
                allIconText.setVisibility(View.VISIBLE);
                concernText.setVisibility(View.GONE);
                completeText.setVisibility(View.GONE);
                groupText.setVisibility(View.GONE);
                break;
            case 1:
                allIconText.setVisibility(View.GONE);
                concernText.setVisibility(View.VISIBLE);
                completeText.setVisibility(View.GONE);
                groupText.setVisibility(View.GONE);
                break;
            case 2:
                allIconText.setVisibility(View.GONE);
                concernText.setVisibility(View.GONE);
                completeText.setVisibility(View.GONE);
                groupText.setVisibility(View.VISIBLE);
                break;
            case 3:
                allIconText.setVisibility(View.GONE);
                concernText.setVisibility(View.GONE);
                completeText.setVisibility(View.VISIBLE);
                groupText.setVisibility(View.GONE);
                break;
        }
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.ll_self_all, R.id.ll_self_concern, R.id.ll_self_group, R.id.ll_self_complete})
    private void onHeadClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                intent.putExtra("value", type);
                setResult(Activity.RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.ll_self_all:
                allIconText.setVisibility(View.VISIBLE);
                concernText.setVisibility(View.GONE);
                completeText.setVisibility(View.GONE);
                groupText.setVisibility(View.GONE);
                value = 0;
                intent.putExtra("value", value);
                setResult(Activity.RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.ll_self_concern:
                allIconText.setVisibility(View.GONE);
                concernText.setVisibility(View.VISIBLE);
                completeText.setVisibility(View.GONE);
                groupText.setVisibility(View.GONE);
                value = 1;
                intent.putExtra("value", value);
                setResult(Activity.RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.ll_self_group:
                allIconText.setVisibility(View.GONE);
                concernText.setVisibility(View.GONE);
                completeText.setVisibility(View.GONE);
                groupText.setVisibility(View.VISIBLE);
                value = 2;
                intent.putExtra("value", value);
                setResult(Activity.RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.ll_self_complete:
                allIconText.setVisibility(View.GONE);
                concernText.setVisibility(View.GONE);
                completeText.setVisibility(View.VISIBLE);
                groupText.setVisibility(View.GONE);
                value = 3;
                intent.putExtra("value", value);
                setResult(Activity.RESULT_OK, intent);
                onBackPressed();
                break;
        }
    }

}
