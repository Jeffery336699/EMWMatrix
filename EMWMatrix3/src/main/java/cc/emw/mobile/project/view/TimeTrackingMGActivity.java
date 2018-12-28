package cc.emw.mobile.project.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.task.activity.TaskCreateActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;

/**
 * TimeTracking Member Group Activity
 * Created by jven.wu on 2016/11/16.
 */
@ContentView(R.layout.activity_timetracking_mg)
public class TimeTrackingMGActivity extends BaseActivity {
    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderBackBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;    //顶部标题
    @ViewInject(R.id.cm_header_btn_right)
    private IconTextView mHeaderNoticeBtn; // 顶部条消息右菜单按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        initWidget();
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right, R.id.itv_tracking_add})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(this, TestActivity.class);
                startActivity(noticeIntent);
                break;
            case R.id.itv_tracking_add:
                Intent intent = new Intent(this, TaskCreateActivity.class);
                intent.putExtra("start_anim", false);
                startActivity(intent);
                break;
        }
    }

    private void initWidget(){
        setTopBar();
    }

    private void setTopBar() {
        mHeaderTitleTv.setText("成员视图");
        mHeaderBackBtn.setIconText("eb68");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderNoticeBtn.setIconText("ecd5");
    }

    /**
     * 设置头部标题栏按钮外观参数
     * @param ivs
     */
    private void setTopIcon(IconTextView... ivs) {
        for(IconTextView iv : ivs) {
            iv.setTextColor(getResources().getColor(R.color.white));
            iv.setTextSize(22);
            iv.setVisibility(View.VISIBLE);
        }
    }
}
