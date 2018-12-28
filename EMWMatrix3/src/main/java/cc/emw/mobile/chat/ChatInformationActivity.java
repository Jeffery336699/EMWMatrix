package cc.emw.mobile.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.ChatInformationAdapter;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;


/**
 * Created by sunny.du on 2017/5/16.
 */
@ContentView(R.layout.chat_information_layout)
public class ChatInformationActivity extends BaseActivity {
    private ChatInformationAdapter chatInformationAdapter;
    @ViewInject(R.id.rv_chat_information_list)
    private RecyclerView mRvChatInformationList;
    @ViewInject(R.id.but_chat_information_affirm)
    private ImageButton mAffirmBtn;
    @ViewInject(R.id.ll_information_root)
    private LinearLayout llInformationRoot;

    private List<UserNote> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvChatInformationList.setItemAnimator(new DefaultItemAnimator());
        mRvChatInformationList.setLayoutManager(mLayoutManager);
        chatInformationAdapter = new ChatInformationAdapter(this);
        chatInformationAdapter.setOnSelectListener(new ChatInformationAdapter.OnSelectListener() {
            @Override
            public void onSelect(int position) {
                mAffirmBtn.setImageResource(R.drawable.icon_chat_affirm);
            }
        });
        mRvChatInformationList.setAdapter(chatInformationAdapter);
        this.getAnyTalkerMsg();

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void getAnyTalkerMsg() {
        API.TalkerAPI.GetMyMessageNoteList(1, 200, new RequestCallback<UserNote>(UserNote.class) {
            @Override
            public void onParseSuccess(List<UserNote> respInfo) {
                if (respInfo != null && respInfo.size() > 0) {
                    mDataList.clear();
                    for (int i = 0; i < respInfo.size(); i++) {
                        if (respInfo.get(i).Type != 14)
                            mDataList.add(respInfo.get(i));
                    }
                    chatInformationAdapter.setmUserNoteList(mDataList);
                    chatInformationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d("sunny----->", "=1");
            }
        });
    }


    @Event({R.id.but_chat_information_back2
            , R.id.but_chat_information_affirm
            , R.id.ll_information_root})
    private void onclick(View v) {
        switch (v.getId()) {
            case R.id.ll_information_root:
                onBackPressed();
            case R.id.but_chat_information_affirm://选择确定
                UserNote userNote = chatInformationAdapter.getUserNote();
                if (userNote != null) {
                    Intent intent = new Intent();
                    userNote.info = null;
                    intent.putExtra("user_note", userNote);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.but_chat_information_back2:
                if (chatInformationAdapter.getIsEdit()) {
                    chatInformationAdapter.setIsEdit(false);
                    chatInformationAdapter.setSelectorPosition(-1);
                    chatInformationAdapter.setUserNote(null);
                    mAffirmBtn.setImageResource(R.drawable.icon_chat_unaffirm);
                    chatInformationAdapter.notifyDataSetChanged();
                } else {
                    onBackPressed();
                }
                break;
        }
    }
}
