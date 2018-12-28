package cc.emw.mobile.calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.adapter.CalendarTagAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.MyListView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_calendar_tag)
public class CalendarTagActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left9)
    private IconTextView mBack;
    @ViewInject(R.id.lv_calendar_tag)
    private MyListView myListView;
    @ViewInject(R.id.tv_calendar_new_tag)
    private LinearLayout mNewTag;

    private List<ApiEntity.UserLabel> mDataList;
    private CalendarTagAdapter adapter;
    private ApiEntity.UserLabel tagSelect;
    private int select; //选中标签的下标
    private Dialog mLoadingDialog;
    private MyBroadCastReceicer receicer;
    public static final String ACTION_REFRESH_CALENDAR_TAG = "action_refresh_calendar_tag";

    private class MyBroadCastReceicer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getCalendarTag();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        init();
    }

    private void init() {
        IntentFilter filter = new IntentFilter();
        receicer = new MyBroadCastReceicer();
        filter.addAction(ACTION_REFRESH_CALENDAR_TAG);
        registerReceiver(receicer, filter);
        mLoadingDialog = createLoadingDialog("正在加载...");
        mDataList = new ArrayList<>();
        adapter = new CalendarTagAdapter(this);
        adapter.setData(mDataList);
        myListView.setAdapter(adapter);
        getCalendarTag();
    }

    private void initList() {
        ApiEntity.UserLabel tag0 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag1 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag2 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag3 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag4 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag5 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag6 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag7 = new ApiEntity.UserLabel();
        tag1.Name = "无";
        tag1.Name = "#会议";
        tag2.Name = "#拜访";
        tag3.Name = "#电话";
        tag4.Name = "#邮件";
        tag5.Name = "#报告";
        tag6.Name = "#其他";
        tag7.Name = "#事件";
        mDataList.add(tag0);
        mDataList.add(tag1);
        mDataList.add(tag2);
        mDataList.add(tag3);
        mDataList.add(tag4);
        mDataList.add(tag5);
        mDataList.add(tag6);
        mDataList.add(tag7);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event(value = {R.id.cm_header_btn_left9, R.id.cm_header_tv_right9, R.id.tv_calendar_new_tag})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                setStartAnim(false);
                Intent intent = new Intent();
                if (mDataList != null && mDataList.size() > 0) {
                    tagSelect = adapter.getSelectTag();
                    select = adapter.getSelect();
                    intent.putExtra("selectTag", tagSelect);
                    intent.putExtra("select", select);
                    setResult(Activity.RESULT_OK, intent);
                }
                setStartAnim(true);
                finish();
                break;
            case R.id.tv_calendar_new_tag:
                setStartAnim(false);
                Intent newTagIntent = new Intent(this, NewCalendarTagActivity.class);
                startActivityForResult(newTagIntent, 111);
                setStartAnim(true);
                break;
        }
    }

    private void getCalendarTag() {
        API.TalkerAPI.GetUserLabel(PrefsUtil.readUserInfo().ID, new RequestCallback<ApiEntity.UserLabel>(ApiEntity.UserLabel.class) {

            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserLabel> respList) {
                mLoadingDialog.dismiss();
                mDataList.clear();
                if (respList.size() > 0 && respList != null) {
                    initList();
                    mDataList.addAll(respList);
                } else {
                    initList();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receicer != null)
            unregisterReceiver(receicer);
    }
}
