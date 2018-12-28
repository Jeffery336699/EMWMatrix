package cc.emw.mobile.contact;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.contact.adapter.ChatSelectAdapter;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SideBar;

/**
 * 发起聊天
 */
@ContentView(R.layout.activity_chat_select)
public class ChatSelectActivity extends BaseActivity {

    @ViewInject(R.id.itv_group_create_title_back)
    private IconTextView mHeaderBackBtn;
    @ViewInject(R.id.et_group_create_title_name)
    private EditText mSearchEt;

    @ViewInject(R.id.ll_chatselect_creategroup)
    private LinearLayout mCreateLayout;
    @ViewInject(R.id.lv_personselect)
    private ListView mListView;
    @ViewInject(R.id.contact_sidebar)
    private SideBar mSideBar;
    @ViewInject(R.id.contact_tv_letter)
    private TextView mTextDialog;

    private ChatSelectAdapter mSelectAdapter;
    private ArrayList<UserInfo> mDataList = new ArrayList<>();
    private ArrayList<UserInfo> mSearchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Event(value = {R.id.itv_group_create_title_back})
    private void onHeaderClick(View view) {
        switch (view.getId()) {
            case R.id.itv_group_create_title_back:
                onBackPressed();
                break;
        }
    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    private void initView() {
        mSideBar.setTextView(mTextDialog);
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mSelectAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });

        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchList.clear();
                if (!TextUtils.isEmpty(s)) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mDataList.size(); i++) {
                        UserInfo userInfo = mDataList.get(i);
                        String name = userInfo.Name;
                        if (name != null) {
                            name = name.replace(" ", "");
                            CharacterParser characterParser = CharacterParser.getInstance();
                            String selling = characterParser.getSelling(name.toLowerCase());
                            sb.delete(0, sb.length());
                            for (int j = 0; j < name.length(); j++) {
                                String substring = name.substring(j, j + 1);
                                substring = characterParser.convert(substring);
                                if (substring != null && substring.length() >= 1) {
                                    substring = substring.substring(0, 1);
                                    sb.append(substring);
                                }
                            }
                            if (name.contains(s) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
                                mSearchList.add(mDataList.get(i));
                            }
                        }
                    }
                    mSelectAdapter.setData(mSearchList);
                    mSelectAdapter.notifyDataSetChanged();
                } else {
                    mSelectAdapter.setData(mDataList);
                    mSelectAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        mCreateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatSelectActivity.this, GroupsCreateActivitys.class);
                intent.putExtra("start_anim", false);
                startActivity(intent);
                finish();
            }
        });

        mSelectAdapter = new ChatSelectAdapter(this);
        mListView.setAdapter(mSelectAdapter);
        if (EMWApplication.personSortList != null && EMWApplication.personSortList.size() > 0) {
            mDataList.addAll(EMWApplication.personSortList);
            mSelectAdapter.setData(mDataList);
            mSelectAdapter.notifyDataSetChanged();
        } else {
            getPersonList("");
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getTag(R.id.tag_second) != null) {
                    UserInfo user = (UserInfo) view.getTag(R.id.tag_second);
                    Intent intent = new Intent(ChatSelectActivity.this, ChatActivity.class);
                    intent.putExtra("SenderID", user.ID);
                    intent.putExtra("type", 1);
                    intent.putExtra("name", user.Name);
                    intent.putExtra("start_anim", false);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void getPersonList(String keyword) {
        API.UserAPI.SearchUser(keyword, 0, false, new RequestCallback<UserInfo>(UserInfo.class, true) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(ChatSelectActivity.this, R.string.contactselect_list_error);
            }

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                mDataList.addAll(respList);
                mSelectAdapter.setData(mDataList);
                mSelectAdapter.notifyDataSetChanged();
                EMWApplication.personSortList = mDataList;
            }
        });
    }

}
