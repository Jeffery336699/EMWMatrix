package cc.emw.mobile.contact;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.githang.androidcrash.util.AppManager;
import com.google.gson.Gson;

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
import cc.emw.mobile.chat.ChatActivity;
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
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SideBar;
import cc.emw.mobile.view.edittag.EditTag;

/**
 * 选择人员 标签
 */
@ContentView(R.layout.activity_person_select)
public class PersonSelectActivity extends BaseActivity {

    @ViewInject(R.id.ic_tv_group_create_title_back)
    private IconTextView mHeaderBackBtn;
    @ViewInject(R.id.tv_group_create_title_name)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.tv_group_create_title_right)
    private TextView mHeaderSubmitTv;

    @ViewInject(R.id.ll_tag_root)
    private LinearLayout mTagLayout;
    @ViewInject(R.id.edit_tag_view)
    private EditTag mEditTag;
    @ViewInject(R.id.lv_personselect)
    private ListView mListView;
    @ViewInject(R.id.contact_sidebar)
    private SideBar mSideBar;
    @ViewInject(R.id.contact_tv_letter)
    private TextView mTextDialog;

    private PersonSelectAdapter mSelectAdapter;
    private ArrayList<UserInfo> mDataList = new ArrayList<>();
    private ArrayList<UserInfo> mSearchList = new ArrayList<>();
    private Dialog mLoadingDialog;
    private String imgName;
    private Uri uri;
    private GroupInfo groupInfo;
    private List<Integer> userIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event(value = {R.id.ic_tv_group_create_title_back, R.id.tv_group_create_title_right})
    private void onHeaderClick(View view) {
        switch (view.getId()) {
            case R.id.ic_tv_group_create_title_back:
                onBackPressed();
                break;
            case R.id.tv_group_create_title_right:
                if (mEditTag.getTagList() != null && mEditTag.getTagList().size() > 0) {
                    List<UserInfo> userInfos = (ArrayList) mEditTag.getTagList();
                    userIds.clear();
                    userIds.add(PrefsUtil.readUserInfo().ID);
                    for (int i = 0; i < userInfos.size(); i++) {
                        userIds.add(userInfos.get(i).ID);
                    }
                    if (uri != null)
                        uploadImage(uri.getPath(), uri);
                    else
                        addGroup(userIds);
//                    Intent intent = new Intent();
//                    intent.putExtra("select_list", (ArrayList)mEditTag.getTagList());
//                    setResult(RESULT_OK, intent);
//                    finish();
                } else {
                    if (uri != null)
                        uploadImage(uri.getPath(), uri);
                    else
                        addGroup(userIds);
                }
                break;
        }
    }

    private void initData() {
        String uris = getIntent().getStringExtra("groupImg");
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("groupInfo");
        if (uris != null && !TextUtils.isEmpty(uris))
            uri = Uri.parse(uris);
    }

    private void initView() {
        mHeaderTitleTv.setText("添加成员");
        /*mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setText(R.string.ok);*/

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

        mKeyBoradView = mTagLayout;
        mEditTag.setOnRemoveSelectTagListener(new EditTag.OnRemoveSelectTagListener() {
            @Override
            public void onRemoveSelect() {
                mSelectAdapter.setData(mDataList);
                mSelectAdapter.notifyDataSetChanged();
            }
        });
        mEditTag.addTextChangedListener(new TextWatcher() {

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
        if (groupInfo != null && groupInfo.Users != null) {
            for (ApiEntity.UserInfo user : groupInfo.Users) {
                mEditTag.addTag((UserInfo) user);
            }
        }
        mSelectAdapter = new PersonSelectAdapter(this, mEditTag);
        mListView.setAdapter(mSelectAdapter);
        if (EMWApplication.personSortList != null && EMWApplication.personSortList.size() > 0) {
            mDataList.addAll(EMWApplication.personSortList);
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).ID == PrefsUtil.readUserInfo().ID)
                    mDataList.remove(i);
            }
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
                    if (mEditTag.hasTag(user)) {
                        mEditTag.delTag(user);
                    } else {
                        mEditTag.addTag(user);
                    }
                    mSelectAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getPersonList(String keyword) {
        API.UserAPI.SearchUser(keyword, 0, false, new RequestCallback<UserInfo>(UserInfo.class, true) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(PersonSelectActivity.this, R.string.contactselect_list_error);
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

    /**
     * 创建圈子
     */
    private void addGroup(List<Integer> userids) {
        if (groupInfo.BackImageIndex == 0)
            groupInfo.Image = imgName;
        API.TalkerAPI.SaveChatterGroup(groupInfo, userids,
                new RequestCallback<String>(String.class) {
                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        Log.e("Const","PersonSelectActivity Throwable = "+arg0.toString());
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        ToastUtil.showToast(PersonSelectActivity.this, R.string.groupcreate_error);
                    }

                    @Override
                    public void onStarted() {
                        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips6);
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        if (!TextUtils.isEmpty(result)
                                && TextUtils.isDigitsOnly(result)
                                && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(PersonSelectActivity.this, R.string.groupcreate_success, R.drawable.tishi_ico_gougou);
                            groupInfo.ID = Integer.valueOf(result);
                            EMWApplication.groupMap.put(groupInfo.ID, groupInfo);
                            if (!TextUtils.isEmpty(EMWApplication.groupAIDLStr) && !EMWApplication.groupAIDLStr.equals("[{}]")) {
                                String str = EMWApplication.groupAIDLStr.substring(0, EMWApplication.groupAIDLStr.length()-1)+","+new Gson().toJson(groupInfo)+"]";
                                EMWApplication.groupAIDLStr = str;
                            }
                            Intent intent = new Intent(GroupFragment.ACTION_REFRESH_GROUP);
                            sendBroadcast(intent); // 刷群主列表
                            intent = new Intent(PersonSelectActivity.this, ChatActivity.class);
                            intent.putExtra("GroupID", groupInfo.ID);
                            intent.putExtra("type", 2);
                            intent.putExtra("start_anim", false);
                            startActivity(intent);
                            onBackPressed();
                            try {
                                AppManager.finishActivity(GroupsCreateActivitys.class);
                            } catch (Exception e) {
                            }
                        } else {
                            ToastUtil.showToast(PersonSelectActivity.this, R.string.groupcreate_error);
                        }
                    }
                });
    }

    /**
     * 群组头像上传
     *
     * @param path
     * @param uri
     */
    private void uploadImage(String path, final Uri uri) {
        RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.addBodyParameter(file.getName(), file);
        x.http().post(params, new RequestCallback<String>(String.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(PersonSelectActivity.this,
                        R.string.headupload_error);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onParseSuccess(List<String> result) {
                if (result != null && result.size() > 0) {
                    String imgTargetStr = result.toString();
                    int i = imgTargetStr.lastIndexOf("/");
                    imgName = imgTargetStr.substring(i + 1,
                            imgTargetStr.length() - 1);
                    addGroup(userIds);
                } else {
                    ToastUtil.showToast(PersonSelectActivity.this,
                            R.string.headupload_error);
                }
            }
        });
    }
}
