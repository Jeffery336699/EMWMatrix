package cc.emw.mobile.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.adapter.DeptSelectAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Dept;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 选择部门
 */
@ContentView(R.layout.activity_dept_select)
public class DeptSelectActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left9)
    private IconTextView mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right9)
    private TextView mHeaderSubmitTv;

    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mGroupLv;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;

    private DeptSelectAdapter adapter;
    private ArrayList<Dept> mDataList = new ArrayList<>();

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

    @Event(value = {R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                Intent intent = new Intent();
                intent.putExtra("select_dept", adapter.getTargetG());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void initView() {
        mHeaderTitleTv.setText("选择部门");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setText(R.string.ok);
        adapter = new DeptSelectAdapter(this);
        mGroupLv.setAdapter(adapter);

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
                getAllDept();
            }
        });
    }

    /**
     * 获取部门列表
     */
    private void getAllDept() {
        API.UserAPI.GetAllDpt(new RequestCallback<Dept>(Dept.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(DeptSelectActivity.this, "获取数据失败！");
            }
            @Override
            public void onParseSuccess(List<Dept> respList) {
                mPtrFrameLayout.refreshComplete();
                if (respList != null && respList.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.setData(mDataList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
