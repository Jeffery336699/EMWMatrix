package cc.emw.mobile.contact.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.ContactSideBar;
import cc.emw.mobile.contact.adapter.PersonnelAdapter;
import cc.emw.mobile.contact.presenter.ContactPresenter;
import cc.emw.mobile.contact.view.ContactView;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.me.adapter.ConcernAdapter;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PersonnelComparator;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * @author zrjt
 * @version 2016-3-8 下午3:42:37
 */
@SuppressLint("DefaultLocale")
@ContentView(R.layout.fragment_persons)
public class PersonFragments extends BaseFragment implements ContactView {

    // @ViewInject(R.id.load_more_list_view_ptr_frame)
    // private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 小组列表
    @ViewInject(R.id.et_contact_search)
    private EditText mSearchEt; // 搜索框
    @ViewInject(R.id.contact_sidebar)
    private ContactSideBar mSideBar;
    @ViewInject(R.id.contact_tv_letter)
    private TextView mTextDialog;
    private ContactPresenter contactPresenter;
    private ArrayList<UserInfo> mDataList; // 按部门分类后的数据集合
    private ArrayList<UserInfo> mSearchList = new ArrayList<>(); // 搜索后的数据集合
    private PersonnelAdapter personnelAdapter;
    public static final String ACTION_REFRESH_CONTACT_LIST = "cc.emw.mobile.refresh_follow_list"; // 刷新的action
    private MyBroadcastReceive mReceive;
    private List<String> sideBars = new ArrayList<>();
    private HashMap<String, Integer> postionsMap = new HashMap<>();   //用来储存用户位置的map集合

    class MyBroadcastReceive extends BroadcastReceiver {
        // TODO
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_CONTACT_LIST.equals(action)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    // Bundle savedInstanceState) {
    // View view = inflater.inflate(R.layout.fragment_person, null);
    // init(view);
    // return view;
    // }

    private void init(View view) {
        mPtrFrameLayout = (PtrFrameLayout) view
                .findViewById(R.id.load_more_list_view_ptr_frame);
        mDataList = new ArrayList<>();
        contactPresenter = new ContactPresenter(this);
        IntentFilter intentFilter = new IntentFilter(
                ACTION_REFRESH_CONTACT_LIST);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
        personnelAdapter = new PersonnelAdapter(getActivity());

        // header
        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(getActivity(), 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);

        refresh();

        //人员搜索框
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mSearchList.clear();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mDataList.size(); i++) {
                    UserInfo userInfo = mDataList.get(i);
                    if (userInfo.Name != null) {
                        CharacterParser characterParser = CharacterParser.getInstance();
                        String selling = characterParser.getSelling(userInfo.Name);
                        sb.delete(0, sb.length());
                        for (int j = 0; j < userInfo.Name.length(); j++) {
                            String substring = userInfo.Name.substring(j, j + 1);
                            substring = characterParser.convert(substring);
                            substring = substring.substring(0, 1);
                            sb.append(substring);
                        }
                        if (userInfo.Name.contains(s) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
                            mSearchList.add(mDataList.get(i));
                        }
                    }
                }
                if (mSearchList != null) {
//                    Set<UserInfo> set = new HashSet<>();
//                    set.addAll(mSearchList);
//                    mSearchList.clear();
//                    mSearchList.addAll(set);
                    personnelAdapter.setData(mSearchList);
                    personnelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int before, int count) {
                // TODO Auto-generated method stub
                personnelAdapter.setData(mDataList);
                personnelAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                // HelpUtil.hideSoftInput(getActivity(), mSearchEt);
            }
        });

    }

    @Override
    public void showTipDialog(String tip) {
    }

    @Override
    public void showUserInfo(List<UserInfo> simpleUsers) {
        mSearchEt.setVisibility(View.VISIBLE);
        mDataList.clear();
        ArrayList<UserInfo> followLists = new ArrayList<UserInfo>();
        ArrayList<UserInfo> allUsers = new ArrayList<UserInfo>();
        followLists.clear();
        allUsers.clear();
        mDataList.clear();
        // Collections.addAll(mDataList, new UserInfo[simpleUsers.size() *
        // 2]);
        // Collections.copy(mDataList, simpleUsers);
        // Collections.addAll(allUsers, new UserInfo[simpleUsers.size()]);
        // Collections.copy(allUsers, simpleUsers);
        allUsers.addAll(simpleUsers);
        mDataList.addAll(simpleUsers);
        for (UserInfo followUser : allUsers) {
            if (followUser.IsFollow == true) {
                UserInfo UserInfo = new UserInfo();
                UserInfo.DeptID = -1;
                UserInfo.DeptName = "关注的";
                UserInfo.Email = followUser.Email;
                UserInfo.IsOnline = followUser.IsOnline;
                UserInfo.ID = followUser.ID;
                UserInfo.Image = followUser.Image;
                UserInfo.Name = followUser.Name;
                UserInfo.Phone = followUser.Phone;
                UserInfo.IsFollow = true;
                followLists.add(UserInfo);
            }
        }
        mDataList.addAll(followLists);
        // textView.setVisibility(View.VISIBLE);
        // textView.setText(mDataList.get(0).toString());
        PersonnelComparator<UserInfo> personnelComparator = new PersonnelComparator<UserInfo>();
        Collections.sort(mDataList, personnelComparator);
        personnelAdapter.setData(mDataList);
        personnelAdapter.setType(0);
//        personnelAdapter.notifyDataSetChanged();
        mListView.setAdapter(personnelAdapter);
        sideBars.clear();
        for (int i = 0; i < mDataList.size(); i++) {
            final UserInfo user = mDataList.get(i);
            if (i > 0 && mDataList.get(i - 1).DeptID == user.DeptID) {
            } else {
                if (user.DeptName.length() > 1) {
                    String s = user.DeptName.substring(0, 1);
                    sideBars.add(s);
                    postionsMap.put(s, i);
                } else {
                    sideBars.add("其");
                    postionsMap.put("其", i);
                }
            }
        }
        sideBars.add("");
        postionsMap.put("", 0);
        mSideBar.setTextView(mTextDialog);
        mSideBar.setLists(sideBars);
        mSideBar.invalidate();
        mSideBar.setOnTouchingChangedListener(new ContactSideBar.OnTouchChangeListener() {
            @Override
            public void onTouchChanged(String s) {
                if (!s.equals(""))
                    mListView.setSelection(postionsMap.get(s));
            }
        });
    }

    @Override
    public void onUserInvisible() {
        mTextDialog.setVisibility(View.GONE);
    }

    private void refresh() {

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                contactPresenter.getPersonList("");
            }
        });
    }

    @Override
    public void onFirstUserVisible() {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrameLayout.autoRefresh(false);
                }
            }, 100);
        }
    }

    @Override
    public void onDestroy() {
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
    }

    @Override
    public void showProgressDialog() {
        // dialog = createLoadingDialog("正在加载");
        // dialog.show();
    }

    @Override
    public void disProgressDialog() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void refreshComplete() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void showFollowResult(String result) {
        // TODO Auto-generated method stub
    }

    @Override
    public void showGroupInfo(List<GroupInfo> groupInfos) {
        // TODO Auto-generated method stub
    }
}
