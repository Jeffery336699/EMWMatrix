package cc.emw.mobile.chat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;
import java.util.Set;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.ChatImportanceMessageAdapter;
import cc.emw.mobile.chat.model.bean.ImprotanceMessage;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.MyFixRecycleView;
import cc.emw.mobile.view.MyScrollView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 圈子重要消息
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_important_msg)
public class ImportantMsgActivity extends BaseActivity {
    @ViewInject(R.id.iv_important_msg_close)
    private ImageView mCloseIv; //关闭
    @ViewInject(R.id.iv_important_msg_edit)
    private ImageView mEditIv; //编辑
    @ViewInject(R.id.tv_important_msg_cancel)
    private TextView mCancelTv; //取消
    @ViewInject(R.id.scroll_chat_importance_messages)
    private MyScrollView mImportScrollView;
    @ViewInject(R.id.rv_chat_importance_messages)
    private MyFixRecycleView mMessageRv;
    @ViewInject(R.id.ll_important_msg_bottom)
    private LinearLayout mBottomLayout; //底部根布局
    @ViewInject(R.id.tv_important_msg_all)
    private TextView mBottomAllTv; //底部全选
    @ViewInject(R.id.tv_important_msg_del)
    private TextView mBottomDelTv; //底部删除

    private ChatImportanceMessageAdapter importantMsgAdapter;
    private Dialog mLoadingDialog; //加载框
    private int groupId; //传值，圈子id
    private int senderId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        groupId = getIntent().getIntExtra("group_id", 0);
        senderId = getIntent().getIntExtra("important_send_id", 0);
        initView();
        if (groupId != 0) {
            getImportanceMessages2Group();
        } else if (groupId == 0 && senderId != 0) {
            getImportanceMessage2User();
        }
//        mMessageRv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                mImportScrollView.requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
    }

    private void initView() {
        mBottomLayout.setVisibility(View.GONE);
        mCancelTv.setVisibility(View.GONE);

        importantMsgAdapter = new ChatImportanceMessageAdapter(this);
        importantMsgAdapter.setListener(new MyStarMsgActivity.GetMessageListener() {
            @Override
            public void GetMessageIndex(ImprotanceMessage message) {
                Intent intent=new Intent();
                intent.putExtra("message",message);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        importantMsgAdapter.setOnSelectListener(new ChatImportanceMessageAdapter.OnSelectListener() {
            @Override
            public void onSelect(int size) {
                importantMsgAdapter.setStartAnim(false);
                mBottomDelTv.setTag(size);
                if (size > 0) {
                    mBottomDelTv.setTextColor(Color.RED);
                } else {
                    mBottomDelTv.setTextColor(Color.parseColor("#C0C0C0"));
                }
            }
        });
        mMessageRv.setItemAnimator(new DefaultItemAnimator());
        mMessageRv.setLayoutManager(new LinearLayoutManager(this));
        mMessageRv.setAdapter(importantMsgAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Event({R.id.root, R.id.iv_important_msg_close, R.id.iv_important_msg_edit, R.id.tv_important_msg_cancel})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.root:
            case R.id.iv_important_msg_close:
                onBackPressed();
                break;
            case R.id.iv_important_msg_edit:
                mBottomLayout.setVisibility(View.VISIBLE);
//                ObjectAnimator animator = ObjectAnimator.ofFloat(mBottomLayout, "translationY", DisplayUtil.dip2px(this, 49), 0);
//                animator.setDuration(400);
//                animator.start();
                TranslateAnimation animation = new TranslateAnimation(0, 0, DisplayUtil.dip2px(this, 49), 0);
                animation.setDuration(400);//设置动画持续时间
                animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                mBottomLayout.setAnimation(animation);
                mEditIv.setVisibility(View.GONE);
                mCancelTv.setVisibility(View.VISIBLE);
                importantMsgAdapter.setIsChangeMsgFalg(true);
                importantMsgAdapter.setStartAnim(true);
                importantMsgAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_important_msg_cancel:
//                mBottomLayout.setVisibility(View.GONE);

                TranslateAnimation animations = new TranslateAnimation(0, 0, 0, DisplayUtil.dip2px(this, 49));
                animations.setDuration(400);//设置动画持续时间
                animations.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                mBottomLayout.setAnimation(animations);
                mBottomLayout.setVisibility(View.GONE);
//                ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBottomLayout, "translationY", 0, DisplayUtil.dip2px(this, 49));
//                animator2.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mBottomLayout.setVisibility(View.GONE);
//                    }
//                });
//                animator2.setDuration(400);
//                animator2.start();
                mEditIv.setVisibility(View.VISIBLE);
                mCancelTv.setVisibility(View.GONE);
                importantMsgAdapter.setIsChangeMsgFalg(false);
                importantMsgAdapter.setStartAnim(true);
                importantMsgAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Event({R.id.tv_important_msg_all, R.id.tv_important_msg_del})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.tv_important_msg_all:
                importantMsgAdapter.getAllSelector();
                break;
            case R.id.tv_important_msg_del:
                if (mBottomDelTv.getTag() == null || Integer.valueOf(mBottomDelTv.getTag().toString()) == 0) {
                    return;
                }
                Set<Integer> selectorMessageList = importantMsgAdapter.getSelectorMessageList();
                String delIDs = TextUtils.join(",", selectorMessageList);
                Log.d("ImportantMsgActivity", delIDs);
                updateImportanceMessage(delIDs, 0);
                break;
        }
    }

    private void getImportanceMessages2Group() {
        //PrefsUtil.readUserInfo().ID
        //第一个参数UsurId暂时定为0，原实际参数为PrefsUtil.readUserInfo().ID
        API.Message.GetGroupMessagesByState(0, groupId + "", 1, 200, 1, new RequestCallback<ImprotanceMessage>(ImprotanceMessage.class) {
            @Override
            public void onParseSuccess(List<ImprotanceMessage> respInfo) {
                importantMsgAdapter.setData(respInfo);
                importantMsgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(ImportantMsgActivity.this, "获取信息失败！");
            }
        });
    }

    private void getImportanceMessage2User() {
        API.Message.GetChatImportanceMessages(senderId, 1, 200, new RequestCallback<ImprotanceMessage>(ImprotanceMessage.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(ImportantMsgActivity.this, "获取信息失败！");
            }

            @Override
            public void onParseSuccess(List<ImprotanceMessage> respInfo) {
                importantMsgAdapter.setData(respInfo);
                importantMsgAdapter.notifyDataSetChanged();
            }
        });

    }

    private void updateImportanceMessage(String msgId, final int state) {
        API.Message.UpdateOptionsByID(msgId, state, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null && !isFinishing()) {
                    mLoadingDialog.dismiss();
                }
                ToastUtil.showToast(ImportantMsgActivity.this, "删除失败！");
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips5);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null && !isFinishing()) {
                    mLoadingDialog.dismiss();
                }
                if (state == 0) {
                    if (groupId != 0 && senderId == 0) {
                        getImportanceMessages2Group();
                    } else if (groupId == 0 && senderId != 0) {
                        getImportanceMessage2User();
                    }
                }
                mBottomDelTv.setTag(0);
                mBottomDelTv.setTextColor(Color.parseColor("#C0C0C0"));
                ToastUtil.showToast(ImportantMsgActivity.this, "删除成功！");
            }
        });
    }
}
