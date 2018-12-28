package cc.emw.mobile.contact.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.SaoYiSaoActivity;
import cc.emw.mobile.contact.adapter.PersonFriendAdapter;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.view.MyListView;
import cc.emw.mobile.view.SearchEditText;
import cc.emw.mobile.view.SwipeBackScrollView;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_add_friend)
public class AddFriendFragment extends BaseFragment {

    public static final String ACTION_ADD_FRIEND_IS_BOTTOM = "action_add_friend_is_bottom";

    @ViewInject(R.id.cm_header_et_search)
    private SearchEditText mSearchEt;
    @ViewInject(R.id.swipe_scroll_add_friend)
    private SwipeBackScrollView mScroll;
    @ViewInject(R.id.mlv_maybe_know)
    private MyListView mListView;
    @ViewInject(R.id.tv_add_friend_tag_num)
    private TextView mTvNum;

    private MyBroadCastReceiver mReceiver;
    private PersonFriendAdapter mPersonAdapter;
    private List<UserInfo> mSearchList = new ArrayList<>();
    private List<UserInfo> mMaybeRemembers = new ArrayList<>();

    public AddFriendFragment() {
    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ADD_FRIEND_IS_BOTTOM)) {
                if (intent.hasExtra("enable")) {
                    mScroll.setEnableGesture(false);
                } else {
                    mScroll.setEnableGesture(true);
                    mScroll.updateSwipeBackState();
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mReceiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ADD_FRIEND_IS_BOTTOM);
        getActivity().registerReceiver(mReceiver, filter);
        mPersonAdapter = new PersonFriendAdapter(getActivity());
        mListView.setAdapter(mPersonAdapter);

        //人员搜索框
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    mPersonAdapter.setMDataList(mMaybeRemembers);
                    mPersonAdapter.notifyDataSetChanged();
                    if (mMaybeRemembers != null && mMaybeRemembers.size() > 0) {
                        mTvNum.setText("可能认识的人 (" + mMaybeRemembers.size() + ")");
                    } else {
                        mTvNum.setText("可能认识的人 (无)");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        mSearchEt.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void onSearchClick(View view) {
                if (TextUtils.isEmpty(mSearchEt.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入检索关键字", Toast.LENGTH_SHORT).show();
                } else {
//                    searchPubUser(mSearchEt.getText().toString());
                    addPubUser(mSearchEt.getText().toString());
                }
            }
        });
        getPossibleUser();
    }

    @Event(value = {R.id.ll_sao_yi_sao})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.ll_sao_yi_sao:
                Intent intent = new Intent(getActivity(), SaoYiSaoActivity.class);
                intent.putExtra("start_anim", false);
                intent.putExtra("mSelector", 1);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取我可能认识的人的列表
     */
    private void getPossibleUser() {
        API.UserPubAPI.GetPossibleContactsList(new RequestCallback<UserInfo>(UserInfo.class) {

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (respList != null && respList.size() > 0) {
                    mMaybeRemembers.clear();
                    mMaybeRemembers.addAll(respList);
                    mTvNum.setText("可能认识的人 (" + mMaybeRemembers.size() + ")");
                    mPersonAdapter.setMDataList(mMaybeRemembers);
                    mPersonAdapter.notifyDataSetChanged();
                } else {
                    mTvNum.setText("可能认识的人 (无)");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }
        });
    }

    /**
     * 搜索联系人
     *
     * @param key
     */
    private void searchUser(String key) {
        API.UserAPI.SearchUser(key, 0, false, new RequestCallback<UserInfo>(UserInfo.class) {
            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (respList != null && respList.size() > 0) {
                    mSearchList.addAll(respList);
                }
                if (mSearchList.size() == 0) {
                    mTvNum.setText("搜索结果 (无)");
                    Toast.makeText(getActivity(), "没有搜到相关信息", Toast.LENGTH_SHORT).show();
                } else {
                    mTvNum.setText("搜索结果 (" + mSearchList.size() + ")");
                    mPersonAdapter.setMDataList(mSearchList);
                    mPersonAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
        });
    }

    /**
     * 搜索公共库的好友
     * @param key
     */
    private void searchPubUser(final String key) {
        API.UserPubAPI.SearchUser(key, 0, false, new RequestCallback<UserInfo>(UserInfo.class) {
            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                mSearchList.clear();
                if (respList != null && respList.size() > 0) {
                    mSearchList.addAll(respList);
                }
                searchUser(key);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
        });
    }

    private void addPubUser(String key){
        API.UserPubAPI.GetSameUserContacts(key, new RequestCallback<UserInfo>(UserInfo.class) {

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                mSearchList.clear();
                if (respList != null && respList.size() > 0) {
                    mSearchList.addAll(respList);
                    mTvNum.setText("搜索结果 (" + mSearchList.size() + ")");
                    mPersonAdapter.setMDataList(mSearchList);
                    mPersonAdapter.notifyDataSetChanged();
                }else{
                    mTvNum.setText("搜索结果 (无)");
                    Toast.makeText(getActivity(), "没有搜到相关信息", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }
        });
    }

}
