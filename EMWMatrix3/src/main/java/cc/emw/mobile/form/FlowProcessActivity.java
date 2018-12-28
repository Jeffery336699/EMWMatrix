package cc.emw.mobile.form;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.form.entity.DataTable;
import cc.emw.mobile.form.entity.Elements2;
import cc.emw.mobile.form.entity.Form;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 审批流程处理页面
 * Created by shaobo.zhuang on 2016/10/8.
 */
@ContentView(R.layout.activity_flow_process)
public class FlowProcessActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    @ViewInject(R.id.cm_header_btn_left9)
    private IconTextView mHeaderBackBtn; // 顶部返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_tv_right9)
    private TextView mHeaderFinishBtn; //顶部完成

    @ViewInject(R.id.ll_process_select)
    private LinearLayout mSelectLayout;
    @ViewInject(R.id.tv_process_name)
    private TextView mSelectNameTv;
    @ViewInject(R.id.et_process_description)
    private EditText mDescriptionEt;

    private Dialog mLoadingDialog; //加载框
    private UserInfo user;
    private String pageId;
    private String rowId;
    private int flowId;
    private int lineId;
    private Form mForm;
    private boolean noReceiver;
    private ArrayList<Integer> memberIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        getSwipeBackLayout().setEdgeSize(DisplayUtil.getDisplayHeight(this) / 2 - 10);
        initView();
    }

    private void initView() {
        pageId = getIntent().getStringExtra("page_id");
        rowId = getIntent().getStringExtra("row_id");
        flowId = getIntent().getIntExtra("flow_id", -1);
        lineId = getIntent().getIntExtra("line_id", -1);
        mForm = (Form) getIntent().getSerializableExtra("form");
        noReceiver = getIntent().getBooleanExtra("no_receiver", false);
        setTopBar();
        if (noReceiver) {
            mSelectLayout.setVisibility(View.GONE);
        }
        if (lineId > 0) {
            requestPMMember();
        }
    }

    private void setTopBar() {
        String title = getIntent().getStringExtra("process_type");
        mHeaderTitleTv.setText(TextUtils.isEmpty(title) ? "" : title);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderFinishBtn.setVisibility(View.VISIBLE);
        mHeaderFinishBtn.setText(R.string.confirm);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContactSelectActivity.RADIO_SELECT && resultCode == RESULT_OK) {
            user = (UserInfo) data.getSerializableExtra(ContactSelectActivity.EXTRA_SELECT_USER);
            mSelectNameTv.setText(user.Name);
        }
    }

    @Event({R.id.ll_process_select, R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_process_select:
                Intent intent = new Intent(this, ContactSelectActivity.class);
                if (lineId > 0) {
                    if (memberIds.size() == 0) {
                        ToastUtil.showToast(getApplicationContext(), "加载人员失败，请稍后重试！");
                        requestPMMember();
                        return;
                    }


                    ArrayList<ApiEntity.UserInfo> filterUserList = new ArrayList<>();
                    for (int i = 0; i < memberIds.size(); i++) {
                        if (EMWApplication.personMap != null && EMWApplication.personMap.get(memberIds.get(i)) != null) {
                            filterUserList.add(EMWApplication.personMap.get(memberIds.get(i)));
                        }
                    }
                    intent.putExtra(ContactSelectActivity.EXTRA_FILTER_LIST, filterUserList);
                }
                if (user != null)
                    intent.putExtra(ContactSelectActivity.EXTRA_SELECT_USER, user);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.RADIO_SELECT);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivityForResult(intent, ContactSelectActivity.RADIO_SELECT);
                break;
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                if (user == null && mSelectLayout.getVisibility() == View.VISIBLE) {
                    ToastUtil.showToast(this, "请选择下环节处理人员！");
                    return;
                }
                StringBuilder postUrlBuilder = new StringBuilder();
                postUrlBuilder.append(Const.BASE_URL)
                        .append("/Page/").append(pageId).append("/lineto?")
                        .append("Option=").append(Uri.encode(mDescriptionEt.getText().toString()))
                        .append("&Line=").append(lineId)
                        .append("&FlowID=").append(flowId)
                        .append("&rid=").append(rowId);
                if (user != null) {
                    postUrlBuilder.append("&NextUser=").append(user.ID);
                }
                postForm(postUrlBuilder.toString(), mForm);
                break;
        }
    }

    /**
     * 提交审批表单
     */
    private void postForm(String postUrl, Form mForm) {
        if (mForm == null) {
            return;
        }
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("RecordID", mForm.RecordID);
        dataMap.put("VerifyTime", mForm.VerifyTime);
        for (int i = 0; i < mForm.Elements.size(); i++) {
            Form.Elements elem = mForm.Elements.get(i);
            if ("RepeatTable".equalsIgnoreCase(elem.Type)) {
                Map<String, String> noNullMap = new HashMap<>(); //存放值不能为空的元素ID
                for (Elements2 ele : elem.Elements) {
                    if (!ele.IsAllowNull) {
                        noNullMap.put(ele.ID, ele.Title);
                    }
                }
                List<Map<String, Object>> repeatList = new ArrayList<>();
                for (int j = 0; j < elem.Data.Rows.size(); j++) {
                    Map<String, Object> repeatMap = new LinkedHashMap<>();
                    for (int k = 0; k < elem.Data.Rows.get(j).size(); k++) {
                        if (k < elem.Data.Columns.size()) {
                            if (noNullMap.containsKey(elem.Data.Columns.get(k)) && TextUtils.isEmpty(elem.Data.Rows.get(j).get(k))) {
                                ToastUtil.showToast(this, noNullMap.get(elem.Data.Columns.get(k)) + getResources().getString(R.string.is_not_null));
                                return;
                            }
                            repeatMap.put(elem.Data.Columns.get(k), elem.Data.Rows.get(j).get(k));
                        }
                    }
                    repeatList.add(repeatMap);
                }
                dataMap.put(elem.ID, repeatList);
                /*String delRowIDs = adapter.getDelRowIDMap().get(elem.ID);
                if (!TextUtils.isEmpty(delRowIDs)) {
                    dataMap.put(elem.ID + "_delete", delRowIDs);
                }*/
            } else {
                for (int j = 0; j < elem.Elements.size(); j++) {
                    Elements2 ele = elem.Elements.get(j);
                    if (TextUtils.isEmpty(ele.Value) && !ele.IsAllowNull) {
                        ToastUtil.showToast(this, ele.Title + getResources().getString(R.string.is_not_null));
                        return;
                    }
                    dataMap.put(ele.ID, ele.Value);
                    if ("Searcher".equals(ele.Type)) {
                        dataMap.put("t_"+ele.ID, ele.Text);
                    }
                }
            }
        }

        RequestParam params = new RequestParam(postUrl);
        String body = new Gson().toJson(dataMap);
        Log.d(TAG, "postForm()->data: " + body);
        params.setStringBody(body);
        x.http().post(params, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                ToastUtil.showToast(FlowProcessActivity.this, "流程提交失败！");
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                try {
                    Log.d(TAG, "postForm()->result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("Code") && jsonObject.getInt("Code") > 0) {
                        ToastUtil.showToast(FlowProcessActivity.this, "流程已提交！");
                        setResult(RESULT_OK,getIntent());
                        onBackPressed();
                    } else {
                        onError(null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void requestPMMember() {
        //请求流程下一步处理人员的url
        String requestPMUrl = Const.BASE_URL + "/Page/" + pageId + "/LineUser?rid=" + rowId + "&flowid=" + flowId + "&id=" + lineId;
        RequestParam params = new RequestParam(requestPMUrl);
        Callback.Cancelable cancelable = x.http().post(params, new RequestCallback<DataTable>(DataTable.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (TextUtils.isEmpty(ex.getMessage())) {
                    ToastUtil.showToast(getApplicationContext(), "请求数据错误！");
                } else {
                    ToastUtil.showToast(getApplicationContext(), ex.getMessage());
                }
            }

            @Override
            public void onParseSuccess(DataTable respInfo) {
                memberIds.clear();
                if (respInfo != null && respInfo.Rows != null) {
                    for (int i = 0; i < respInfo.Rows.size(); i++) {
                        memberIds.add(respInfo.getRowID(i));
                    }
                }
            }
        });
    }

}
