package cc.emw.mobile.contact;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.mingle.headsUp.HeadsUpManager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;

import cc.emw.mobile.chat.model.bean.GroupMessage;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 申请加入圈子通知
 */
@ContentView(R.layout.activity_group_join)
public class GroupJoinActivity extends BaseActivity {

    @ViewInject(R.id.tv_groupjoin_content)
    private TextView mContentTv;

    private Dialog mLoadingDialog; // 加载框
    private Message message;
    private GroupMessage groupMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        message = (Message)getIntent().getSerializableExtra("message");
        groupMsg = new Gson().fromJson(message.getContent(), GroupMessage.class);
        mContentTv.setText(groupMsg.UserName + " 申请加入圈子【" + groupMsg.GroupName + "】。");

        HeadsUpManager.getInstant(this).cancel(message.getID());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(message.getID());
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event(value = {R.id.btn_groupjoin_left, R.id.btn_groupjoin_right})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.btn_groupjoin_left:
                join(2);
                break;
            case R.id.btn_groupjoin_right:
                join(1);
                break;
        }
    }

    private void join(int type){
        API.TalkerAPI.DoJoinGroupUser(groupMsg.GroupID, groupMsg.UserID, type, message.getContent(), message.getID(), groupMsg.GroupName, new RequestCallback<String>(String.class) {
            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                Toast.makeText(GroupJoinActivity.this, "处理成功!", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(GroupFragment.ACTION_REFRESH_GROUP));

                Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                intent.putExtra(MainActivity.MESSAGE_ID, message.getID());
                intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
                sendBroadcast(intent);

                finish();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                Toast.makeText(GroupJoinActivity.this, "处理失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }
        });
    }
}
