package cc.emw.mobile.contact;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.contact.adapter.ContactSelectAdapter;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SideBar;
import cc.emw.mobile.view.SideBar.OnTouchingLetterChangedListener;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 人员选择
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_contact_select)
public class ContactSelectActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left9)
    private IconTextView mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right9)
    private TextView mHeaderOkTv;

    @ViewInject(R.id.et_contact_search)
    private EditText mSearchEt;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.contact_elv_person)
    private ExpandableListView mListView;
    @ViewInject(R.id.contact_sidebar)
    private SideBar mSideBar;
    @ViewInject(R.id.contact_tv_letter)
    private TextView mTextDialog;

    public static final String EXTRA_SELECT_TYPE = "select_type";
    /**
     * 单选
     */
    public static final int RADIO_SELECT = 1;
    /**
     * 多选
     */
    public static final int MULTI_SELECT = 2;

    public static final String EXTRA_SELECT_USER = "select_user"; //默认选中的用户
    public static final String EXTRA_SELECT_LIST = "select_list"; //默认选中的用户集
    public static final String EXTRA_FILTER_LIST = "filter_list"; //显示的用户集
    public static final String EXTRA_CLICKABLE_UID = "clickable_uid"; //不处理点击事件的用户ID

    private Dialog mLoadingDialog; // 加载框
    private ContactSelectAdapter mSelectAdapter;
    private ArrayList<UserInfo> mDataList, mFilterList;
    private int mPosition, mSelectType;
    private boolean isSend;
    private boolean isShare; //是否从动态列表转发
    private boolean hasOneself; //是否显示自己
    private UserNote shareNote; //转发信息
    private int chatAddGroupUser;
    private GroupInfo chatGroupInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getIntent().getIntExtra("position", 0);
        mSelectType = getIntent().getIntExtra(EXTRA_SELECT_TYPE, MULTI_SELECT);

        if (getIntent().hasExtra("isSend")) {
            isSend = getIntent().getBooleanExtra("isSend", false);
        }
        isShare = getIntent().getBooleanExtra("is_share", false);
        hasOneself = getIntent().getBooleanExtra("has_oneself", true);
        shareNote = (UserNote) getIntent().getSerializableExtra("share_note");
        UserInfo user = (UserInfo) getIntent().getSerializableExtra(EXTRA_SELECT_USER);


        chatAddGroupUser = getIntent().getIntExtra("chat_add_group_user", -1);
        chatGroupInfo = (GroupInfo) getIntent().getSerializableExtra("chat_add_group_info");

        ArrayList<UserInfo> selectList = (ArrayList<UserInfo>) getIntent().getSerializableExtra(EXTRA_SELECT_LIST);
        mFilterList = (ArrayList<UserInfo>) getIntent().getSerializableExtra(EXTRA_FILTER_LIST);

        mHeaderBackBtn.setIconText("eb68");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.contactselect);
        mHeaderOkTv.setText(R.string.ok);
        mHeaderOkTv.setVisibility(View.VISIBLE);
        mPtrFrameLayout.setEnabled(false);

        mSideBar.setTextView(mTextDialog);
        mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mSelectAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    // elv.setSelection(position);
                    mListView.setSelectedGroup(position);
                }
            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                HelpUtil.hideSoftInput(ContactSelectActivity.this, mSearchEt);
                mSelectAdapter.setSearch(s.toString().toLowerCase().trim());
                // 展开所有
                for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
                    mListView.expandGroup(i);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDataList = new ArrayList<UserInfo>();
        mSelectAdapter = new ContactSelectAdapter(this);
        mSelectAdapter.setSelectType(mSelectType);
        int uid = getIntent().getIntExtra(EXTRA_CLICKABLE_UID, 0);
        mSelectAdapter.setClickableItemID(uid);
        if (mSelectType == RADIO_SELECT) {
            mSelectAdapter.setRadioUser(user);
        } else {
            mSelectAdapter.setSelectList(selectList);
        }
        mListView.setAdapter(mSelectAdapter);

        if (mFilterList != null) {
            mSelectAdapter.setDataList(mFilterList);
            mSelectAdapter.notifyDataSetChanged();
            for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
                mListView.expandGroup(i);
            }
        } else if (EMWApplication.personSortList != null && EMWApplication.personSortList.size() > 0) {
            mDataList.addAll(EMWApplication.personSortList);
            if (!hasOneself) {
                for (UserInfo userInfo : mDataList) {
                    if (userInfo.ID == PrefsUtil.readUserInfo().ID) {
                        mDataList.remove(userInfo);
                        break;
                    }
                }
            }
            mSelectAdapter.setDataList(mDataList);
            mSelectAdapter.notifyDataSetChanged();
            for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
                mListView.expandGroup(i);
            }
        } else {
            getPersonList("");
        }

    }

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                HelpUtil.hideSoftInput(this, mSearchEt);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                Intent data = new Intent();
                if (mSelectType == RADIO_SELECT) {
                    data.putExtra("position", mPosition);
                    data.putExtra(EXTRA_SELECT_USER, mSelectAdapter.getSelectUser());
                    if (mSelectAdapter.getSelectUser() == null) {
                        ToastUtil.showToast(this, R.string.contactselect_empty);
                        return;
                    }
                    if (isSend) {
                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra("SenderID", mSelectAdapter.getSelectUser().ID);
                        intent.putExtra("type", 1);
                        intent.putExtra("name", mSelectAdapter.getSelectUser().Name);
                        startActivity(intent);
                    } else if (isShare) {
                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra("SenderID", mSelectAdapter.getSelectUser().ID);
                        intent.putExtra("type", 1);
                        intent.putExtra("name", mSelectAdapter.getSelectUser().Name);
                        UserNote.UserNoteShareTo share = new UserNote.UserNoteShareTo();
                        share.NoteID = shareNote.ID;
                        share.UserName = shareNote.UserName;
                        share.Content = shareNote.Content;
                        intent.putExtra("share", new Gson().toJson(share));
                        startActivity(intent);
                    }
                } else {

                    data.putExtra("position", mPosition);
                    ArrayList<UserInfo> userInfos = new ArrayList<>();
                    userInfos.addAll(mSelectAdapter.getSelectList());
                    data.putExtra(EXTRA_SELECT_LIST, userInfos);
                /*if (mSelectAdapter.getSelectList().size() == 0) {
					ToastUtil.showToast(this, R.string.contactselect_empty);
					return;
				}*/
                }
                if (chatAddGroupUser == 1001) {
                    if (chatGroupInfo != null) {
                        List<Integer> userids =new LinkedList<>();
                        if(chatGroupInfo.Users != null && chatGroupInfo.Users.size() != 0){
                            for (ApiEntity.UserInfo userinfo:chatGroupInfo.Users ) {
                                userids.add(userinfo.ID);
                            }
                        }
                        if(mSelectAdapter.getSelectList() != null && mSelectAdapter.getSelectList().size() != 0){
                            for (ApiEntity.UserInfo userinfo:mSelectAdapter.getSelectList() ) {
                                userids.add(userinfo.ID);
                            }
                        }
                        saveGroupInfo(chatGroupInfo,userids);
                    } else {
                        ToastUtil.showToast(this, "网络异常，请稍后尝试");
                    }

                } else {
                    setResult(Activity.RESULT_OK, data);
                }
                onBackPressed();
                break;
        }
    }

    private void getPersonList(String keyword) {
        API.UserAPI.SearchUser(keyword, 0, false, new RequestCallback<UserInfo>(UserInfo.class, true) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(ContactSelectActivity.this, R.string.contactselect_list_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                mDataList.addAll(respList);
                if (!hasOneself) {
                    for (UserInfo userInfo : mDataList) {
                        if (userInfo.ID == PrefsUtil.readUserInfo().ID) {
                            mDataList.remove(userInfo);
                            break;
                        }
                    }
                }
                mSelectAdapter.setDataList(mDataList);
                mSelectAdapter.notifyDataSetChanged();
                // 展开所有
                for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
                    mListView.expandGroup(i);
                }
                EMWApplication.personSortList = mDataList;
            }
        });
    }


    /**
     * Chat模块群组聊天界面触发添加成员
     * 解决进程通信的问题，将添加成员接口触发时机调整到本界面触发。判断字段：chatAddGroupUser
     * 人员变动提交后台
     */
    public void saveGroupInfo(final GroupInfo mGroupInfo, List<Integer> userids) {
        API.TalkerAPI.SaveChatterGroup(mGroupInfo, userids, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(ContactSelectActivity.this, "编辑失败,服务器异常");
            }

            @Override
            public void onSuccess(String result) {
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(ContactSelectActivity.this, "添加成功");
                    //刷新缓存
                    EMWApplication.groupMap.put(mGroupInfo.ID, mGroupInfo);
                    Intent intent = new Intent();
                    intent.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                    ContactSelectActivity.this.sendBroadcast(intent);
                } else {
                    ToastUtil.showToast(ContactSelectActivity.this, "编辑失败,请稍候再试");
                }
            }
        });
    }

}
