package cc.emw.mobile.contact;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.contact.adapter.GroupSelectAdapter;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 选择群组
 */
@ContentView(R.layout.activity_group_select)
public class GroupSelectActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left9)
    private IconTextView mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right9)
    private TextView mHeaderSubmitTv;

    @ViewInject(R.id.et_contact_search)
    private EditText mSearchEt;
    @ViewInject(R.id.load_more_small_image_list_view)
    private ExpandableListView mGroupLv;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;

    private GroupSelectAdapter adapter;
    private ArrayList<GroupInfo> mDataList = new ArrayList<GroupInfo>();

    public static final String TargetG = "targetG";
    private boolean isAll; //是否显示所有圈子
    private boolean isSend = false;
    private boolean isShare = false; //是否从动态列表转发
    private UserNote shareNote; //转发信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAll = getIntent().getBooleanExtra("is_all", true);
        isShare = getIntent().getBooleanExtra("is_share", false);
        shareNote = (UserNote) getIntent().getSerializableExtra("share_note");
        initView();

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    @Event(value = {R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                GroupInfo info = adapter.getTargetG();
                if (isSend) {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("GroupID", info.ID);
                    intent.putExtra("type", 2);
                    startActivity(intent);
                    finish();
                } else if (isShare) {
                    Intent intent2 = new Intent(this, ChatActivity.class);
                    intent2.putExtra("GroupID", info.ID);
                    intent2.putExtra("type", 2);
                    UserNote.UserNoteShareTo shareto = new UserNote.UserNoteShareTo();
                    shareto.NoteID = shareNote.ID;
                    shareto.UserName = shareNote.UserName;
                    shareto.Content = shareNote.Content;
                    intent2.putExtra("share", new Gson().toJson(shareto));
                    startActivity(intent2);
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(TargetG, info);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    private void initView() {
        if (getIntent().hasExtra("isSend")) {
            isSend = getIntent().getBooleanExtra("isSend", false);
        }
        mHeaderTitleTv.setText(R.string.groupselect);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setText(R.string.ok);
        adapter = new GroupSelectAdapter(this);
        mGroupLv.setAdapter(adapter);

        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                HelpUtil.hideSoftInput(GroupSelectActivity.this, mSearchEt);
                adapter.setSearch(s.toString().toLowerCase().trim());
                // 展开所有
                for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
                    mGroupLv.expandGroup(i);
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
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mGroupLv, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getAllGroupList();
            }
        });
    }

    /**
     * 获取圈子列表
     */
    private void getAllGroupList() {
        API.TalkerAPI.LoadGroups("", isAll, 0, false, new RequestCallback<GroupInfo>(GroupInfo.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(GroupSelectActivity.this, "获取数据失败！");
            }

            @Override
            public void onParseSuccess(List<GroupInfo> respList) {
                mPtrFrameLayout.refreshComplete();
                if (respList != null && respList.size() > 0) {
                    if (EMWApplication.groupMap == null) {
                        EMWApplication.groupMap = new SparseArray<GroupInfo>();
                    }
                    EMWApplication.groupMap.clear();
                    for (GroupInfo groupInfo : respList) {
                        EMWApplication.groupMap.put(groupInfo.ID,
                                groupInfo);
                    }
                    mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.setDataList(mDataList);
                    adapter.notifyDataSetChanged();
                    // 展开所有
                    for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
                        mGroupLv.expandGroup(i);
                    }
                }
            }
        });
    }
}
