package cc.emw.mobile.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.ChatImportanceMessageAdapter;
import cc.emw.mobile.chat.model.bean.ImprotanceMessage;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.MyFixRecycleView;
import cc.emw.mobile.view.MyScrollView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.chat
 * @data on 2018/8/13  14:43
 * @describe TODO
 */
@ContentView(R.layout.activity_call_msg)
public class CallMsgActivity extends BaseActivity {
    @ViewInject(R.id.iv_important_msg_close)
    private ImageView mCloseIv; //关闭
    @ViewInject(R.id.scroll_chat_importance_messages)
    private MyScrollView mImportScrollView;
    @ViewInject(R.id.rv_chat_importance_messages)
    private MyFixRecycleView mMessageRv;

    private ChatImportanceMessageAdapter callMsgAdapter;
    private int groupId; //传值，圈子id

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        groupId = getIntent().getIntExtra("group_id", 0);
        initView();
        if (groupId != 0) {
            getCallMessagesGroup();
        }
    }

    private void initView() {

        callMsgAdapter = new ChatImportanceMessageAdapter(this);
        callMsgAdapter.setListener(new MyStarMsgActivity.GetMessageListener() {
            @Override
            public void GetMessageIndex(ImprotanceMessage message) {
                Intent intent = new Intent();
                intent.putExtra("message", message);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        callMsgAdapter.setOnSelectListener(new ChatImportanceMessageAdapter.OnSelectListener() {
            @Override
            public void onSelect(int size) {
                callMsgAdapter.setStartAnim(false);
            }
        });
        mMessageRv.setItemAnimator(new DefaultItemAnimator());
        mMessageRv.setLayoutManager(new LinearLayoutManager(this));
        mMessageRv.setAdapter(callMsgAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Event({R.id.root, R.id.iv_important_msg_close})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.root:
            case R.id.iv_important_msg_close:
                onBackPressed();
                break;
        }
    }

    private void getCallMessagesGroup() {
        API.Message.GetMyPinMessages(PrefsUtil.readUserInfo().Name, PrefsUtil.readUserInfo().ID, groupId, 1, 200, new RequestCallback<ImprotanceMessage>(ImprotanceMessage.class) {
            @Override
            public void onParseSuccess(List<ImprotanceMessage> respInfo) {
                callMsgAdapter.setData(respInfo);
                callMsgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(CallMsgActivity.this, "获取信息失败！");
            }
        });
    }
}
