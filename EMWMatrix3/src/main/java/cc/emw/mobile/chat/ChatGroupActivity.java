package cc.emw.mobile.chat;

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

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.adapter.GroupSelectAdapter;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by xiang.peng on 2016/7/15.
 */
@ContentView(R.layout.activity_chatgroup)
public class ChatGroupActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderSubmitTv;

    @ViewInject(R.id.et_contact_search)
    private EditText mSearchEt;
    @ViewInject(R.id.load_more_small_image_list_view)
    private ExpandableListView mListView;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    private GroupSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }


    private void initView() {
        mHeaderTitleTv.setText(R.string.groupselect);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setText(R.string.ok);
        adapter = new GroupSelectAdapter(this);
        mListView.setAdapter(adapter);

        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                HelpUtil.hideSoftInput(ChatGroupActivity.this, mSearchEt);
                adapter.setSearch(s.toString().toLowerCase().trim());
                // 展开所有
                for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
                    mListView.expandGroup(i);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
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
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getAddInGroup();
            }
        });
    }


    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})

    private void onHeaderClick(View view) {

        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                GroupInfo info = adapter.getTargetG();
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("GroupID", info.ID);
                intent.putExtra("type", 2);
                intent.putExtra("name", info.Name);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void getAddInGroup() {

        API.TalkerAPI.LoadGroups("", true, 0, false, new RequestCallback<GroupInfo>(
                GroupInfo.class) {

            @Override
            public void onStarted() {}


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(ChatGroupActivity.this, "加载群组列表失败");
            }

            @Override
            public void onParseSuccess(List<GroupInfo> respList) {
                mPtrFrameLayout.refreshComplete();
                if (respList != null && respList.size() > 0) {

                    ArrayList<GroupInfo> addInGroups = new ArrayList<>();
                    if (EMWApplication.groupMap == null) {
                        EMWApplication.groupMap = new SparseArray<>();
                    }
                    EMWApplication.groupMap.clear();
                    for (GroupInfo groupInfo : respList) {
                        EMWApplication.groupMap.put(groupInfo.ID,
                                groupInfo);
                        if (groupInfo.IsAddIn) {
                            addInGroups.add(groupInfo);
                        }
                    }
                    if (addInGroups != null && addInGroups.size() > 0) {
                        adapter.setDataList(addInGroups);
                        adapter.notifyDataSetChanged();
                        // 展开所有
                        for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
                            mListView.expandGroup(i);
                        }
                    }
                }
            }
        });
    }
}
