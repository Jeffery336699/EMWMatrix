package cc.emw.mobile.contact.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.adapter.PhoneUserAdapter;
import cc.emw.mobile.contact.decoration.TitleItemDecoration;
import cc.emw.mobile.contact.widget.IndexBar;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.SearchEditText;
import cc.emw.mobile.view.SwipeBackRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_phone_user)
public class PhoneUserFragment extends BaseFragment {

    public static final String ACTION_PHONE_FRIEND_IS_BOTTOM = "action_phone_friend_is_bottom";

    @ViewInject(R.id.cm_header_et_search)
    private SearchEditText mSearchEt;
    @ViewInject(R.id.rv)
    private SwipeBackRecyclerView mRv;
    @ViewInject(R.id.indexBar)
    private IndexBar mIndexBar;
    @ViewInject(R.id.tvSideBarHint)
    private TextView mTvSideBarHint;

    private MyBroadCastReceiver mReceiver;
    private LinearLayoutManager mManager;
    private PhoneUserAdapter mAdapter;
    private List<UserInfo> mPhoneUsers;     //手机通讯录中的好友
    private List<UserInfo> mDataList = new ArrayList<>();
    private List<UserInfo> mSearchList = new ArrayList<>();
    private List<String> mPhoneLists = new ArrayList<>();

    public PhoneUserFragment() {
    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_PHONE_FRIEND_IS_BOTTOM)) {
                if (intent.hasExtra("enable")) {
                    mRv.setEnableGesture(false);
                } else {
                    mRv.setEnableGesture(true);
                    mRv.updateSwipeBackState();
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mReceiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PHONE_FRIEND_IS_BOTTOM);
        getActivity().registerReceiver(mReceiver, filter);

        getPhoneNumberFromMobile(getActivity());

        mRv.setEnableGesture(false);

        mRv.setLayoutManager(mManager = new LinearLayoutManager(getActivity()));
        mAdapter = new PhoneUserAdapter(getActivity());
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(new TitleItemDecoration(getActivity(), mDataList));

        for (int i = 0; i < mPhoneUsers.size(); i++) {
            mPhoneLists.add(mPhoneUsers.get(i).Phone.replaceAll(" ", "") + "");
        }
        checkPhonePeople();

        //人员搜索框
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mSearchList.clear();
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mDataList.size(); i++) {
                        UserInfo userInfo = mDataList.get(i);
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
                    mIndexBar.setVisibility(View.GONE);
                    mAdapter.setMData(mSearchList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int before, int count) {
                mIndexBar.setVisibility(View.VISIBLE);
                mAdapter.setMData(mDataList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                // HelpUtil.hideSoftInput(getActivity(), mSearchEt);
            }
        });
    }

    public void getPhoneNumberFromMobile(Context context) {
        // TODO Auto-generated constructor stub
        mPhoneUsers = new ArrayList();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        //moveToNext方法返回的是一个boolean类型的数据
        while (cursor.moveToNext()) {
            //读取通讯录的姓名
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //读取通讯录的号码
            String number = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            UserInfo userInfo = new UserInfo();
            userInfo.Phone = number;
            userInfo.Name = name;
            userInfo.isFromPhone = true;
            mPhoneUsers.add(userInfo);
        }
        sortData(mPhoneUsers);
    }

    private void sortData(List<UserInfo> respList) {
        CharacterParser characterParser = CharacterParser.getInstance();
        for (int i = 0; i < respList.size(); i++) {
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(respList.get(i).Name);
            if (!TextUtils.isEmpty(pinyin)) {
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    respList.get(i).setSortLetters(sortString.toUpperCase());
                } else {
                    respList.get(i).setSortLetters("#");
                }
            } else {
                respList.get(i).setSortLetters("#");
            }
        }
        PinyinComparator pinyinComparator = new PinyinComparator();
        Collections.sort(respList, pinyinComparator);// 根据a-z进行排序源数据
    }

    private void checkPhonePeople() {
        API.UserPubAPI.GetPhoneStateByList(mPhoneLists, new RequestCallback<String>(String.class) {

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    mDataList.clear();
                    String[] mRealDatas = result.substring(1, result.length() - 1).split(",");
                    for (int i = 0; i < mRealDatas.length; i++) {
                        if (!mRealDatas[i].equals("0") &&
                                Integer.valueOf(mRealDatas[i]) != PrefsUtil.readUserInfo().ID) {
                            mPhoneUsers.get(i).ID = Integer.valueOf(mRealDatas[i]);
                            mDataList.add(mPhoneUsers.get(i));
                        }
                    }
                    mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                            .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                            .setNeedRealIndex(true)//设置需要真实的索引
                            .setmSourceDatas(mDataList);//设置数据源
                    mAdapter.setMData(mDataList);
                    mAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("zrjt", ex.toString());
            }
        });
    }
}
