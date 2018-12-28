package cc.emw.mobile.contact;

import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CharacterParser;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.adapter.GroupMemberAdapters;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author zrjt
 */
@ContentView(R.layout.activity_group_member)
public class GroupMemberListActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeadRight;
    // @ViewInject(R.id.load_more_list_view_ptr_frame)
    // private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 成员列表
    @ViewInject(R.id.et_contact_search)
    private EditText mSearchEt; // 搜索框
    private ArrayList<UserInfo> noteRoles; // 所有的群成员列表
    private ArrayList<UserInfo> mSearchList = new ArrayList<UserInfo>(); // 我搜索的群成员列表
    private GroupMemberAdapters adapter;
    public static final String ACTION_REFRESH_GROUP = "cc.emw.mobile.refresh.group";
    private MyBroadcastReceiver receicer;
    private GroupInfo info;

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction() == ACTION_REFRESH_GROUP) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
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
                getGroupMember(info.ID);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = (GroupInfo) getIntent().getSerializableExtra("group_info");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.groupmember);

        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.load_more_list_view_ptr_frame);

        getGroupMember(info.ID);

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
        refresh();

        init();
    }

    @Event(value = {R.id.cm_header_btn_left})
    private void onChick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void init() {
        adapter = new GroupMemberAdapters(this, info);
        mListView.setAdapter(adapter);
        IntentFilter intentFilter = new IntentFilter(ACTION_REFRESH_GROUP);
        receicer = new MyBroadcastReceiver();
        registerReceiver(receicer, intentFilter); // 注册监听
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mSearchList.clear();
                StringBuilder sb = new StringBuilder();
                for (int i = 0, size = noteRoles.size(); i < size; i++) {
                    String name = noteRoles.get(i).Name;
                    if (name != null) {
                        CharacterParser characterParser = CharacterParser.getInstance();
                        String selling = characterParser.getSelling(noteRoles.get(i).Name);
                        sb.delete(0,sb.length());
                        for (int j = 0; j < name.length(); j++) {
                            String firstName = name.substring(j, j + 1);
                            String convert = characterParser.convert(firstName);
                            String singleConvert = convert.substring(0, 1);
                            sb.append(singleConvert);
                        }
                        if (noteRoles.get(i).Name.contains(s) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
                            mSearchList.add(noteRoles.get(i));
                        }
                    }
                }
                if (mSearchList != null) {
                    adapter.setData(mSearchList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int before, int count) {
                adapter.setData(noteRoles);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // HelpUtil.hideSoftInput(getActivity(), mSearchEt);
            }
        });
    }

    /**
     * 获取群组成员信息
     *
     * @param gid
     */
    private void getGroupMember(int gid) {
        API.TalkerAPI.LoadGroupUsersByGid(gid, new RequestCallback<String>(
                String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(GroupMemberListActivity.this, R.string.groupmember_list_error);
            }

            @Override
            public void onSuccess(String result) {
                mPtrFrameLayout.refreshComplete();
                mSearchEt.setVisibility(View.VISIBLE);
                try {
                    Gson gson = new Gson();
                    noteRoles = new ArrayList<UserInfo>();
                    JsonArray array = new JsonParser().parse(result)
                            .getAsJsonArray();
                    UserInfo owner = new UserInfo();
                    for (JsonElement jsonElement : array) {
                        UserInfo noteRole = gson.fromJson(jsonElement,
                                UserInfo.class);
                        if (noteRole.ID == info.CreateUser) {
                            owner = noteRole;
                            owner.isTag = true;
                        } else {
                            noteRoles.add(noteRole);
                        }
                    }
                    noteRoles.add(0, owner);
                    adapter.setData(noteRoles);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("zrjt", e.toString());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receicer != null)
            unregisterReceiver(receicer);
    }
}
