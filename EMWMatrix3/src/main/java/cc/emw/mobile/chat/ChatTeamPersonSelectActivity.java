package cc.emw.mobile.chat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.githang.androidcrash.util.AppManager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.ChatTeamPersonSelectAdapter;
import cc.emw.mobile.contact.GroupsCreateActivitys;
import cc.emw.mobile.contact.adapter.PersonSelectAdapter;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SideBar;
import cc.emw.mobile.view.edittag.EditTag;

/**
 * 选择人员 标签
 */
@ContentView(R.layout.activity_chat_team_person_select)
public class ChatTeamPersonSelectActivity extends BaseActivity {
    @ViewInject(R.id.ic_tv_group_create_title_back)
    private IconTextView mHeaderBackBtn;
    @ViewInject(R.id.tv_group_create_title_name)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.tv_group_create_title_right)
    private TextView mHeaderSubmitTv;
    @ViewInject(R.id.lv_personselect)
    private ListView mListView;
    @ViewInject(R.id.contact_sidebar)
    private SideBar mSideBar;
    @ViewInject(R.id.contact_tv_letter)
    private TextView mTextDialog;
    private GroupInfo groupInfo;//团队实体
    private ArrayList<UserInfo> sUsers;//团队成员实体列表
    private ChatTeamPersonSelectAdapter mSelectAdapter;//团队成员适配器

    private Dialog mLoadingDialog;
    private String imgName;
    private Uri uri;
    private List<Integer> userIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        sUsers = (ArrayList<UserInfo>) getIntent().getSerializableExtra("userinfo");
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("groupInfo");
    }


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
        mSelectAdapter = new ChatTeamPersonSelectAdapter(this);
        mListView.setAdapter(mSelectAdapter);
        if (EMWApplication.personSortList != null && EMWApplication.personSortList.size() > 0) {
            mSelectAdapter.setData(sUsers);
            mSelectAdapter.notifyDataSetChanged();
        } else {
            getPersonList("");
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectTeamUserPosition=position;
                if(selectTeamUserPosition != -1){
                    mSelectAdapter.setListCount(selectTeamUserPosition);
                    mSelectAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    private int selectTeamUserPosition=-1;
    @Event(value = {R.id.ic_tv_group_create_title_back, R.id.tv_group_create_title_right})
    private void onHeaderClick(View view) {
        switch (view.getId()) {
            case R.id.ic_tv_group_create_title_back:
                onBackPressed();
                break;
            case R.id.tv_group_create_title_right:
                if(selectTeamUserPosition <0){
                    ToastUtil.showToast(this,"请选择一名群成员转移管理权限");
                }else{
                    Intent okIntent = new Intent();
                    okIntent.putExtra("count", selectTeamUserPosition);
                    setResult(Activity.RESULT_OK, okIntent);
                    finish();
                }
                break;
        }
    }

    /**
     * 获取成员列表信息
     *
     * @param keyword
     */
    private void getPersonList(String keyword) {
        API.UserAPI.SearchUser(keyword, 0, false, new RequestCallback<UserInfo>(UserInfo.class, true) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(ChatTeamPersonSelectActivity.this, R.string.contactselect_list_error);
            }

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                sUsers.addAll(respList);
                mSelectAdapter.setData(sUsers);
                mSelectAdapter.notifyDataSetChanged();
                EMWApplication.personSortList = sUsers;
            }
        });
    }
}
