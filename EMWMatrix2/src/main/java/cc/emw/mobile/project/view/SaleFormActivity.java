package cc.emw.mobile.project.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.ArrayList;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.project.adapter.SaleFormAdapter;
import cc.emw.mobile.project.entities.Elements;
import cc.emw.mobile.project.entities.Elements2;
import cc.emw.mobile.project.entities.Form;
import cc.emw.mobile.project.entities.Navigations;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.RightMenu;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 表单详情页面
 */
@ContentView(R.layout.activity_sale_form)
public class SaleFormActivity extends BaseActivity {
    private static final String TAG = "SaleFormActivity";
    public static final int SEARCH_REQUEST_CODE = 0;
    public static final int CHECKBOX_REQUEST_CODE = 1;

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderFinishBtn; //顶部完成按钮
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.name)
    private TextView mName;
    @ViewInject(R.id.lv_saleform)
    private FloatingGroupExpandableListView mListView;

    private Form mForm;
    private SaleFormAdapter adapter;
    private String pageID, rowID;
    private RightMenu mMenu;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageID = getIntent().getStringExtra("page_id");
        rowID = getIntent().getStringExtra("row_id");
        initView();
        refresh();
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK &&
                (requestCode == SEARCH_REQUEST_CODE || requestCode == CHECKBOX_REQUEST_CODE)){
            Elements2 elements2 = (Elements2)data.getSerializableExtra("elem");
            int groupPosition = data.getIntExtra("group_position",-1);
            int childPosition = data.getIntExtra("child_position",-1);
            if(groupPosition == -1 || childPosition == -1){
                return;
            }
            mForm.Elements.get(groupPosition).Elements.set(childPosition,elements2);
            adapter.notifyDataSetChanged();
            Log.d(TAG,"onActivityResult()->element value: " + elements2.Value);
        }
    }

    private void initView() {
        setTopBar();
        request();
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                postForm();
                break;
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
                request();
            }
        });
    }

    private void setTopBar() {
        mHeaderTitleTv.setText(R.string.order_detail);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderFinishBtn.setVisibility(View.VISIBLE);
        mHeaderFinishBtn.setText(R.string.finish);
//        initMenu();
    }

    private void request() {
        String requestUrl;
        if(rowID == null){
            requestUrl = "http://10.0.10.80:8000/mpage/" + pageID;
        }else {
            requestUrl = "http://10.0.10.80:8000/mpage/" + pageID + "?rid=" + rowID;
        }
        Log.d(TAG, "request()->rul: " + requestUrl);
        RequestParams params = new RequestParams(requestUrl);
        Callback.Cancelable cancelable = x.http().post(params,
                new RequestCallback<Form>(Form.class) {
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtil.showToast(getApplicationContext(),ex.getMessage());
                        Log.d(TAG,"onError()->msg: " + ex.toString());
                    }

                    @Override
                    public void onFinished() {
                        mPtrFrameLayout.refreshComplete();
                    }

                    @Override
                    public void onParseSuccess(Form form) {
                        mForm = form;
                        if(rowID == null){
                            mForm.VerifyTime = "0";
                        }
                        if (mForm.Navigations == null || mForm.Navigations.size() == 0) {
                            mForm.Navigations = new ArrayList<Navigations>();
                            Navigations nav = new Navigations();
                            nav.ID = "Elem1";
                            nav.Name = "表单节";
                            mForm.Navigations.add(nav);
                        }
                        adapter = new SaleFormAdapter(SaleFormActivity.this, mForm);
                        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
                        mListView.setAdapter(wrapperAdapter);
                    }
                });
    }

    private void postForm(){
        if(mForm == null){
            return;
        }
        //用于标记是否要退出外层循环
        boolean flag = true;
        //内容为空的表单元素标题
        String title = "";
        String postUrl = "http://10.0.10.80:8000/Page/" + pageID + "/save";
        Log.d(TAG,postUrl);
        RequestParam params = new RequestParam(postUrl);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"RecordID\":" + mForm.RecordID + ","
            + "\"VerifyTime\":\"" + mForm.VerifyTime + "\"" );
        for(int i = 0;i<mForm.Elements.size();i++){
            if(mForm.Elements.get(i).Elements == null){
                continue;
            }
            if(!flag){
                break;
            }
            for (int j = 0;j<mForm.Elements.get(i).Elements.size();j++){
                Elements2 ele = mForm.Elements.get(i).Elements.get(j);
                if(!ele.IsAllowNull && TextUtils.isEmpty(ele.Value)){
                    flag = false;
                    title = ele.Title;
                }
                stringBuilder.append(",\"" + ele.ID + "\":");
                stringBuilder.append("\"" + ele.Value + "\"");
                if("Searcher".equals(ele.Type)){
                    stringBuilder.append(",\"t_" + ele.ID + "\":");
                    stringBuilder.append("\"" + ele.Text + "\"");
                }
            }
        }
        if(!flag){
            ToastUtil.showToast(this, title +
                    getResources().getString(R.string.is_not_null));
            return;
        }
        Log.d(TAG,"postForm()->if has RepeatTable add Elem45");
        for(Elements element:mForm.Elements){
            //判断是否有重复表
            if("重复表".equals(element.Title)){
                stringBuilder.append(",\"Elem45\":[]");
                break;
            }
        }
        stringBuilder.append("}");
        Log.d(TAG,"postForm()->url: " + stringBuilder.toString());
        String body = stringBuilder.toString();
        params.setStringBody(body);
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onStarted() {
                dialog = createLoadingDialog(
                        getResources().getString(R.string.progress_tip));
                dialog.show();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                if(rowID == null){
                    ToastUtil.showToast(SaleFormActivity.this,
                            getResources().getString(R.string.create_fail));
                }else {
                    ToastUtil.showToast(SaleFormActivity.this,
                            getResources().getString(R.string.modify_fail));
                }
                dialog.dismiss();
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String arg0) {
                if(rowID == null){
                    ToastUtil.showToast(SaleFormActivity.this, R.string.create_success);
                }else {
                    ToastUtil.showToast(SaleFormActivity.this, R.string.modify_success);
                }
                dialog.dismiss();
            }
        });
    }
}
