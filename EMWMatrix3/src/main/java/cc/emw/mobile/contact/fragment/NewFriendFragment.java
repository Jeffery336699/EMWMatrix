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

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.adapter.PersonNewFriendAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.view.MyListView;
import cc.emw.mobile.view.SearchEditText;
import cc.emw.mobile.view.SwipeBackScrollView;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_new_friend)
public class NewFriendFragment extends BaseFragment {

    public static final String ACTION_NEW_FRIEND_IS_BOTTOM = "action_new_friend_is_bottom";

    @ViewInject(R.id.cm_header_et_search)
    private SearchEditText mSearchEt;
    @ViewInject(R.id.swipe_scroll_new_friend)
    private SwipeBackScrollView mScroll;
    @ViewInject(R.id.mlv_maybe_know)
    private MyListView mListView;

    private MyBroadCastReceiver mReceiver;
    private PersonNewFriendAdapter mNewFriendAdapter;
    private List<ApiEntity.PubConApply> mDataList = new ArrayList<>();
    private List<ApiEntity.PubConApply> mSearchList = new ArrayList<>();

    public NewFriendFragment() {
    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_NEW_FRIEND_IS_BOTTOM)) {
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
        filter.addAction(ACTION_NEW_FRIEND_IS_BOTTOM);
        getActivity().registerReceiver(mReceiver, filter);
        mNewFriendAdapter = new PersonNewFriendAdapter(getActivity());
        mListView.setAdapter(mNewFriendAdapter);
        getNewFriendRecord();

        //人员搜索框
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mSearchList.clear();
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mDataList.size(); i++) {
                        ApiEntity.PubConApply userInfo = mDataList.get(i);
                        String name = userInfo.Name;
                        name = name.replaceAll(" ", "").trim();
                        if (name != null) {
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
                            if (name.contains(s.toString().trim()) || selling.contains(s.toString().trim().toLowerCase())
                                    || sb.toString().contains(s.toString().trim().toLowerCase())) {
                                mSearchList.add(mDataList.get(i));
                            }
                        }
                    }
                    mNewFriendAdapter.setMDataList(mSearchList);
                    mNewFriendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int before, int count) {
                mNewFriendAdapter.setMDataList(mDataList);
                mNewFriendAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                // HelpUtil.hideSoftInput(getActivity(), mSearchEt);
            }
        });
    }

    private void getNewFriendRecord() {
        API.UserPubAPI.GetPubConApplyByType(new RequestCallback<ApiEntity.PubConApply>(ApiEntity.PubConApply.class) {

            @Override
            public void onParseSuccess(List<ApiEntity.PubConApply> respList) {
                if (respList != null && respList.size() > 0) {
                    mDataList.addAll(respList);
                    mNewFriendAdapter.setMDataList(mDataList);
                    mNewFriendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
            }
        });
    }
}
