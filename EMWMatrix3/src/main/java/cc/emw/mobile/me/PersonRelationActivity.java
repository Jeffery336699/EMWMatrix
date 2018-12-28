package cc.emw.mobile.me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.util.Prefs;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_person_relation)
public class PersonRelationActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;
    @ViewInject(R.id.tv_current_user)
    private TextView currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        currentUserName.setText(Prefs.getString(PrefsUtil.KEY_USERNAME, ""));
        init();
    }

    private void init() {
        mTitle.setText("个人相关");
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.ll_setting_secure})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.ll_setting_secure:
                Intent intent = new Intent(this, PwCheckingActivity.class);
                intent.putExtra("start_anim", false);
                startActivityForResult(intent, 101);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            new AlertDialog(this).builder()
                    .setMsg("密码输入错误，请重新输入")
                    .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
        }
    }
}
