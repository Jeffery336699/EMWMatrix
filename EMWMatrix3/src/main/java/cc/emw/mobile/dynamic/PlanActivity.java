package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.adapter.PlanAdapter;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteSendTypes;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.fragment.TimeTrackingWebFragment;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.SegmentedGroup;

/**
 * 动态·新建工作计划
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_plan3)
public class PlanActivity extends BaseActivity {

//	@ViewInject(R.id.cm_header_btn_left) private ImageButton mHeaderCancelBtn; // 顶部条取消
//	@ViewInject(R.id.cm_header_tv_title) private TextView mHeaderTitleTv; // 顶部条标题
//	@ViewInject(R.id.cm_header_tv_right) private TextView mHeaderSendTv; // 顶部条发布

    @ViewInject(R.id.cm_input_et_content)
    private EditText mContentEt; // 内容
    @ViewInject(R.id.segmented_plan_type)
    private SegmentedGroup mRadioGroup; // 类型RadioGroup
    @ViewInject(R.id.lv_plan_planlist)
    private ExListView mListView; // 工作计划列表
    @ViewInject(R.id.cm_select_tv_name)
    private TextView mSelectTv; // 分享范围
    @ViewInject(R.id.cm_select_fl_select)
    private FlowLayout mSelectFlowLayout; // 分享人员Layout

    private Dialog mLoadingDialog; //加载框
    private ArrayList<UserPlan> mDataList; // 工作计划列表
    private ArrayList<UserInfo> selectList; // 分享人员列表数据
    private PlanAdapter mWorkPlanAdapter; // 工作计划列表adapter

    private int groupID, projectID; //列表传值，来自TimeTracking中的动态

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupID = getIntent().getIntExtra("group_id", 0);
        projectID = getIntent().getIntExtra("project_id", 0);
        initView();
        onAddItemClick(null);
    }

    private void initView() {
        ((TextView) findViewById(R.id.cm_select_tv_select)).setTextColor(getResources().getColor(R.color.cm_text));
        /*mHeaderCancelBtn.setVisibility(View.GONE);
        mHeaderTitleTv.setText(R.string.plan);
        mHeaderSendTv.setText(R.string.publish);
        mHeaderSendTv.setVisibility(View.GONE);*/
        mContentEt.setHint(R.string.content_hint);

        mDataList = new ArrayList<>();
        mWorkPlanAdapter = new PlanAdapter(this, mDataList);
        mWorkPlanAdapter.setSwipeBackLayout(getSwipeBackLayout());
        mListView.setAdapter(mWorkPlanAdapter);
        
        /*CalendarUtils calendarUtil = new CalendarUtils();
        String nextDay = "日计划     "+calendarUtil.getNextDay();
        String nextWeek = "周计划     "+calendarUtil.getNextMonday()+" ~ "+calendarUtil.getNextSunday();
        String nextMonth = "月计划     "+calendarUtil.getNextMonthFirst()+" ~ "+calendarUtil.getNextMonthEnd();
        setRadioTipDate(nextDay, R.id.workplan_rb_day);
        setRadioTipDate(nextWeek, R.id.workplan_rb_week);
        setRadioTipDate(nextMonth, R.id.workplan_rb_month);*/
    }

    /**
     * 设置提示日期
     *
     * @param date
     * @param radioId
     */
    private void setRadioTipDate(String date, int radioId) {
        SpannableString spanStr = new SpannableString(date);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GRAY);
        spanStr.setSpan(colorSpan, 3, date.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.8f);
        spanStr.setSpan(sizeSpan, 3, date.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ((RadioButton) findViewById(radioId)).setText(spanStr);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                String content = mContentEt.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(this, R.string.empty_content_tips);
                } else {
                    send(content);
                }
                break;
        }
    }

    @Event(R.id.ll_plan_additem)
    private void onAddItemClick(View v) {
        UserPlan plan = new UserPlan();
        mDataList.add(plan);
        mWorkPlanAdapter.notifyDataSetChanged();
    }

    @Event({R.id.cm_select_ll_select})
    private void onSelectClick(View v) {
        Intent intent = new Intent(this, ContactSelectActivity.class);
        switch (v.getId()) {
            case R.id.cm_select_ll_select:
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("select_list", selectList);
                intent.putExtra("has_oneself", false);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivityForResult(intent, 2);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mContentEt.clearFocus(); //防止跳转其他页面后回到该页面滚动到mContentEt焦点位置
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 2: //选择的人员
                    mSelectFlowLayout.removeAllViews();
                    selectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < selectList.size(); i++) {
                        if (i < 3) {
                            UserInfo user = selectList.get(i);
                            if (i != 0) {
                                builder.append("、");
                            }
                            builder.append(user.Name);
                        } else {
                            builder.append("等" + selectList.size() + "人");
                            break;
                        }
                    }
                    mSelectTv.setText(builder);
                    mSelectTv.setHint(selectList.size() > 0 ? "" : "公开");
                    break;
            }
        }
    }

    /**
     * 发布
     *
     * @param content
     */
    private void send(String content) {
        for (int i = 0, size = mDataList.size(); i < size; i++) {
            UserPlan plan = mDataList.get(i);
            if (!validate(plan, i + 1))
                return;
        }

        UserNote un = new UserNote();
        if (groupID > 0) {
            un.GroupID = groupID;
        }
        if (projectID > 0) {
            un.ProjectID = projectID;
        }
        un.Type = UserNoteAddTypes.Plan;
        un.Content = content;
        un.UserID = PrefsUtil.readUserInfo().ID;
        un.Roles = new ArrayList<>();
        RadioButton selectRb = (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());
        for (UserPlan plan : mDataList) {
            plan.Type = Integer.valueOf(String.valueOf(selectRb.getTag()));
        }
        un.Property = new Gson().toJson(mDataList);

        ArrayList<NoteRole> nrList = new ArrayList<>();
        if (selectList != null && selectList.size() > 0) {
            un.SendType = UserNoteSendTypes.Private;  //0 公共 1 私有
            for (int i = 0, size = selectList.size(); i < size; i++) {
                NoteRole role = new NoteRole();
                role.ID = selectList.get(i).ID;
                role.Name = selectList.get(i).Name;
                role.Image = selectList.get(i).Image;
                role.Type = NoteRoleTypes.User;
                nrList.add(role);
            }
            un.Roles = nrList;
        } else {
            un.SendType = UserNoteSendTypes.Public;
        }

        API.TalkerAPI.AddTalkerPlan(mDataList, un, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(PlanActivity.this, R.string.publish_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(PlanActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    if (groupID > 0) {
                        intent = new Intent(TimeTrackingWebFragment.ACTION_REFRESH_TIMEDYNAMIC);
                    }
                    sendBroadcast(intent); // 刷新首页列表
                    finish();
                } else {
                    ToastUtil.showToast(PlanActivity.this, R.string.publish_error);
                }
            }
        });
    }

    /**
     * 非空验证
     *
     * @param plan
     * @param position
     * @return
     */
    private boolean validate(UserPlan plan, int position) {
        boolean isSuccess = false;
        String tip = "";
        if (TextUtils.isEmpty(plan.Name)) {
            tip = getString(R.string.plan_empty_name);
        } else if (TextUtils.isEmpty(plan.EndTime)) {
            tip = getString(R.string.plan_empty_time);
        } else {
            isSuccess = true;
        }
        if (!isSuccess) {
            ToastUtil.showToast(this, "【计划" + position + "】" + tip);
        }

        return isSuccess;
    }
}
