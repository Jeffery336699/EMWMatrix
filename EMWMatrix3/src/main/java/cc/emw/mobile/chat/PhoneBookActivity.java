package cc.emw.mobile.chat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.PhoneBookAdapter;
import cc.emw.mobile.chat.util.CharacterParser;
import cc.emw.mobile.chat.util.PinyinComparator;
import cc.emw.mobile.contact.ContactFrament;
import cc.emw.mobile.contact.adapter.ConcernAdapter;
import cc.emw.mobile.contact.decoration.TitleItemDecoration;
import cc.emw.mobile.contact.widget.IndexBar;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.chat
 * @data on 2018/10/8  14:39
 * @describe TODO
 */
@ContentView(R.layout.activity_phone_book)
public class PhoneBookActivity extends BaseActivity {

    private List<UserInfo> datalistView = new ArrayList<>();//通讯录总数据
    private List<UserInfo> mSearchList = new ArrayList<>();//搜索框筛选后的数据

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.rv)
    private RecyclerView mRv;
    //    private AutoLoadRecyclerView mRv;
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mLvSearch;
    @ViewInject(R.id.tvSideBarHint)
    private TextView mTvSideBarHint;
    @ViewInject(R.id.indexBar)
    private IndexBar mIndexBar;
    @ViewInject(R.id.et_search_keywords)
    private EditText mSearchEt;
    @ViewInject(R.id.ic_et_del)
    private IconTextView mIcEtDel;

    private LinearLayoutManager mManager;
    private PhoneBookAdapter mAdapter;
    private ConcernAdapter mSearchAdapter;
    private TitleItemDecoration mDecoration;
    private DisplayImageOptions options;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    public static final String ACTION_CONTACT_SEARCH = "action_contact_search";
    public static final String ACTION_CONTACT_SEARCH_BACK = "action_contact_search_back";
    public static final String ACTION_CONTACT_SEARCH_USERINFO = "action_contact_search_user_info";

    private MyBroadcastReceive mReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ContactFrament.ACTION_CONTACT_SEARCH);
        intentFilter.addAction(ContactFrament.ACTION_CONTACT_SEARCH_BACK);
        mReceive = new MyBroadcastReceive();
        registerReceiver(mReceive, intentFilter); // 注册监听

        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setToggleMenu(true);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);

        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        mRv.setLayoutManager(mManager = new LinearLayoutManager(this));
        mAdapter = new PhoneBookAdapter(this, datalistView);
        mRv.setAdapter(mAdapter);
        mDecoration = new TitleItemDecoration(this, datalistView);
        mRv.addItemDecoration(mDecoration);

        //搜索
        mSearchAdapter = new ConcernAdapter(this);
        mLvSearch.setAdapter(mSearchAdapter);

        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIcEtDel.setVisibility(!TextUtils.isEmpty(mSearchEt.getText().toString()) ? View.VISIBLE : View.GONE);
                if (!TextUtils.isEmpty(s)) {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_CONTACT_SEARCH);
                    intent.putExtra("keyword", s.toString());
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_CONTACT_SEARCH_BACK);
                    intent.putExtra("keyword", s.toString());
                    sendBroadcast(intent);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
                    if (!TextUtils.isEmpty(mSearchEt.getText().toString())) {
                        HelpUtil.hideSoftInput(PhoneBookActivity.this, mSearchEt);
                        Intent intent = new Intent();
                        intent.setAction(ACTION_CONTACT_SEARCH_USERINFO);
                        intent.putExtra("keyword", mSearchEt.getText().toString());
                        sendBroadcast(intent);
                    }
                    return true;
                }
                return false;
            }
        });

        refresh();
        getPermissions();
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        // TODO
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ContactFrament.ACTION_CONTACT_SEARCH)) {
                String s = intent.getStringExtra("keyword");
                if (!TextUtils.isEmpty(s.trim())) {
                    mSearchList.clear();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < datalistView.size(); i++) {
                        UserInfo userInfo = datalistView.get(i);
                        String name = userInfo.Name;
                        name = name.replaceAll(" ", "").trim();
                        if (name != null) {
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
                                mSearchList.add(datalistView.get(i));
                            }
                        }
                    }
                    mRv.setVisibility(View.GONE);
                    mLvSearch.setVisibility(View.VISIBLE);
                    mSearchAdapter.setDataList((ArrayList<UserInfo>) mSearchList);
                    mSearchAdapter.notifyDataSetChanged();
                }
            } else if (intent.getAction().equals(ContactFrament.ACTION_CONTACT_SEARCH_BACK)) {
                String s = intent.getStringExtra("keyword");
                if (TextUtils.isEmpty(s)) {
                    mLvSearch.setVisibility(View.GONE);
                    mRv.setVisibility(View.VISIBLE);
                    mSearchList.clear();
                }
            }
        }
    }

    @Event(value = {R.id.ic_et_del})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.ic_et_del:
                mSearchEt.setText("");
                mSearchEt.clearFocus();
                break;
        }
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<UserInfo> filledData(List<UserInfo> date) {
        List<UserInfo> mSortList = new ArrayList<UserInfo>();

        for (int i = 0; i < date.size(); i++) {
            UserInfo sortModel = new UserInfo();
            sortModel.Name = date.get(i).Name;
            sortModel.Phone = date.get(i).Phone;
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date.get(i).Name);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    private void refresh() {

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mRv.getVisibility() == View.VISIBLE ? mRv : mLvSearch, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getPermissions();
            }
        });
    }

    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager
                    .PERMISSION_GRANTED) {
                getContacts();
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_CONTACTS}, GET_PERMISSION_REQUEST);
            }
        } else {
            getContacts();
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //通讯录权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                if (size == 0) {
                    getContacts();
                } else {
                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public final static String NUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
    public final static String NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    private Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    private void getContacts() {
        List<UserInfo> data = new ArrayList<>();
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(phoneUri, new String[]{NUM, NAME}, null, null, null);
        //向下移动光标
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    UserInfo phoneBook = new UserInfo();
                    phoneBook.Name = cursor.getString(cursor.getColumnIndex(NAME));
                    phoneBook.Phone = cursor.getString(cursor.getColumnIndex(NUM));
                    data.add(phoneBook);
                }
            } finally {
                cursor.close();
            }
        }
        mPtrFrameLayout.refreshComplete();
        datalistView = filledData(data);
        if (datalistView != null && datalistView.size() > 0) {
            // 根据a-z进行排序源数据
            Collections.sort(datalistView, pinyinComparator);
            mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                    .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                    .setNeedRealIndex(false)//设置需要真实的索引
                    .setmSourceDatas(datalistView);//设置数据源
            mAdapter.setmDatas(datalistView);
            mRv.removeItemDecorationAt(0);
            mDecoration = new TitleItemDecoration(this, datalistView);
            mRv.addItemDecoration(mDecoration);
            mAdapter.notifyDataSetChanged();
        }
    }

}