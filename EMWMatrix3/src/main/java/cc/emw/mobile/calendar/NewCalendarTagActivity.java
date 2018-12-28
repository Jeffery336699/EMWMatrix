package cc.emw.mobile.calendar;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.fragment.CalendarFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_new_calendar_tag)
public class NewCalendarTagActivity extends BaseActivity {

    @ViewInject(R.id.cm_input_et_content)
    private EditText editContent;
    private int userId;
    private Dialog mLoadingDialog; // 加载框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event(value = {R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                if (!TextUtils.isEmpty(editContent.getText().toString()))
                    submitTag();
                else
                    ToastUtil.showToast(this, "标签不能为空");
                break;
            default:
                break;
        }
    }

    private void submitTag() {
        ApiEntity.UserLabel userLabel = new ApiEntity.UserLabel();
        userLabel.Name = editContent.getText().toString();
        API.TalkerAPI.AddUserLabel(userLabel, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(NewCalendarTagActivity.this, R.string.bzm_submit_error);
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(NewCalendarTagActivity.this, R.string.bzm_submit_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent();
                    intent.setAction(CalendarEditActivity.ACTION_REFRESH_CALENDAR_TAG);
                    sendBroadcast(intent);
//                    intent.setAction(CalendarCreateActivitys.ACTION_REFRESH_CALENDAR_TAG);
//                    intent.putExtra("tag",editContent.getText().toString());
//                    sendBroadcast(intent);
                    intent = new Intent();
                    intent.setAction(CalendarFragment.ACTION_REFRESH_CALENDAR_FRAGMENT);
                    sendBroadcast(intent);
                    if(mOnColseView !=null){
                        mOnColseView.onClolse();
                    }
                    finish();
                }
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }
        });
    }
    /*************************用于关闭下拉抽屉菜单的接口回调*******sunnydu*******该方法存在浪费内存开销，后续建议优化将新建标签内容调用接口放到创建日常的activity进行************/
    static OnColseView mOnColseView;
    public static void setOnColseView(OnColseView ocl){
        mOnColseView=ocl;
    }
    public interface OnColseView{
        void onClolse();
    }
}
