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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.fragment.talker.DynamicFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteSendTypes;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.PersonTextView;
import cc.emw.mobile.view.SegmentedGroup;

/**
 * 动态·新建工作计划
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_plan)
public class PlanActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_tv_left)
	private TextView mHeaderCancelTv; // 顶部条取消
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderSendTv; // 顶部条发布
	
	@ViewInject(R.id.cm_input_et_content)
	private EditText mContentEt; // 内容
	@ViewInject(R.id.segmented_plan_type)
	private SegmentedGroup mRadioGroup; // 类型RadioGroup
	@ViewInject(R.id.lv_plan_planlist)
	private ExListView mListView; // 工作计划列表
	@ViewInject(R.id.btn_plan_additem)
	private Button mAddItemBtn; // 添加更多计划
	@ViewInject(R.id.ll_plan_radioselect)
	private LinearLayout mRadioSelectLayout; // 选择直属领导Layout
	@ViewInject(R.id.fl_plan_radioperson)
	private FlowLayout mRadioFlowLayout; // 直属领导Layout
	@ViewInject(R.id.tv_plan_mainselect)
	private TextView mRadioSelectTv; // 
	@ViewInject(R.id.cm_select_ll_select)
	private LinearLayout mSelectRootLayout; // 选择分享人员Layout
	@ViewInject(R.id.cm_select_tv_select)
	private TextView mSelectTv; // 分享范围
	@ViewInject(R.id.cm_select_fl_select)
	private FlowLayout mSelectFlowLayout; // 分享人员Layout
	
	private Dialog mLoadingDialog; //加载框
	private ArrayList<UserPlan> mDataList; // 工作计划列表
	private ArrayList<UserInfo> selectList; // 分享人员列表数据
	private UserInfo mSelectUser; // 直属领导信息
	private PlanAdapter mWorkPlanAdapter; // 工作计划列表adapter
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackTip(true);
        initView();
        
        onAddItemClick(null);
        onAddItemClick(null);
    }
    
    private void initView() {
    	mHeaderCancelTv.setVisibility(View.VISIBLE);
        mHeaderCancelTv.setText(R.string.cancel);
        mHeaderTitleTv.setText(R.string.plan);
        mHeaderSendTv.setText(R.string.finish);
        mHeaderSendTv.setVisibility(View.VISIBLE);
        mContentEt.setHint(R.string.content_hint);
        
        mDataList = new ArrayList<UserPlan>();
        mWorkPlanAdapter = new PlanAdapter(this, mDataList);
        mListView.setAdapter(mWorkPlanAdapter);
        
        /*CalendarUtil calendarUtil = new CalendarUtil();
        String nextDay = "日计划     "+calendarUtil.getNextDay();
        String nextWeek = "周计划     "+calendarUtil.getNextMonday()+" ~ "+calendarUtil.getNextSunday();
        String nextMonth = "月计划     "+calendarUtil.getNextMonthFirst()+" ~ "+calendarUtil.getNextMonthEnd();
        setRadioTipDate(nextDay, R.id.workplan_rb_day);
        setRadioTipDate(nextWeek, R.id.workplan_rb_week);
        setRadioTipDate(nextMonth, R.id.workplan_rb_month);*/
    }
    
    /**
     * 设置提示日期
     * @param date
     * @param radioId
     */
    private void setRadioTipDate(String date, int radioId) {
    	SpannableString spanStr = new SpannableString(date);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GRAY);
        spanStr.setSpan(colorSpan, 3, date.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.8f);
        spanStr.setSpan(sizeSpan, 3, date.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ((RadioButton)findViewById(radioId)).setText(spanStr);
    }
    
    @Event({R.id.cm_header_tv_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_tv_left:
				HelpUtil.hideSoftInput(this, mContentEt);
				onBackPressed();
				break;
			case R.id.cm_header_tv_right:
				String content = mContentEt.getText().toString().trim();
		    	if (TextUtils.isEmpty(content)) {
		    		ToastUtil.showToast(this, R.string.empty_content_tips);
		    	} else {
		    		send(content);
		    	}
				break;
    	}
    }
    @Event(R.id.btn_plan_additem)
    private void onAddItemClick(View v) {
    	UserPlan plan = new UserPlan();
    	/*plan.IsPlan = true;
    	plan.State = 1;*/
        mDataList.add(plan);
        mWorkPlanAdapter.notifyDataSetChanged(); 
    }
    @Event({R.id.ll_plan_radioselect, R.id.cm_select_ll_select})
    private void onSelectClick(View v) {
    	Intent intent = new Intent(this, ContactSelectActivity.class);
    	switch (v.getId()) {
	    	case R.id.ll_plan_radioselect:
	    		intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.RADIO_SELECT);
	        	intent.putExtra("select_user", mSelectUser);
	        	startActivityForResult(intent, 1);
				break;
	    	case R.id.cm_select_ll_select:
	    		intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
	        	intent.putExtra("select_list", selectList);
	        	startActivityForResult(intent, 2);
				break;
    	}
    }

	@Override
	protected void onPause() {
		super.onPause();
		mContentEt.clearFocus();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
				case 1:
					mRadioFlowLayout.removeAllViews();
					mSelectUser = (UserInfo) data.getSerializableExtra("select_user");
					addRadioPerson(mSelectUser);
					break;
				case 2:
					mSelectFlowLayout.removeAllViews();
					selectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
					for (UserInfo user : selectList) {
						addPersonItem(user);
					}
					if (selectList.size() > 0) {
						mSelectTv.setHint("");
					} else {
						mSelectTv.setHint(R.string.share_range_hint);
					}
					break;
			}
    	}
    }
    /**
     * 显示选择的直属领导人
     */
    private void addRadioPerson(UserInfo user) {
		PersonTextView childView = new PersonTextView(this);
		childView.setTag(user);
		childView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRadioFlowLayout.removeView(v);
				mSelectUser = null;
				mRadioSelectTv.setHint("请选择");
			}
		});
		childView.setText(user.Name);
		mRadioSelectTv.setHint("");
		mRadioFlowLayout.addView(childView);
    }
    /**
     * 显示选择的分享人员
     */
    private void addPersonItem(UserInfo user) {
		PersonTextView childView = new PersonTextView(this);
		childView.setTag(user);
		childView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectFlowLayout.removeView(v);
				selectList.remove((UserInfo) v.getTag());
				if (selectList.size() == 0) {
					mSelectTv.setHint(R.string.share_range_hint);
				}
			}
		});
		childView.setText(user.Name);
		mSelectFlowLayout.addView(childView);
    }
    
    /**
     * 发布
     * @param content
     */
    private void send(String content) {
    	for (int i = 0, size = mDataList.size(); i < size; i++) {
    		UserPlan plan = mDataList.get(i);
			if (!validate(plan, i+1))
				return;
		}
    	
    	UserNote un = new UserNote();
		un.Type = UserNoteAddTypes.Plan;
		un.Content = content;
		un.UserID = PrefsUtil.readUserInfo().ID;
		un.Roles = new ArrayList<NoteRole>();
		RadioButton selectRb = (RadioButton)findViewById(mRadioGroup.getCheckedRadioButtonId());
		for (UserPlan plan : mDataList) {
			plan.Type = Integer.valueOf(String.valueOf(selectRb.getTag()));
		}
		un.Property = new Gson().toJson(mDataList);
		
		ArrayList<NoteRole> nrList = new ArrayList<NoteRole>();
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
					Intent intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
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
     * @param plan
     * @param position
     * @return
     */
    private boolean validate(UserPlan plan, int position) {
    	boolean isSuccess = false;
    	String tip = "";
    	if (TextUtils.isEmpty(plan.Name)) {
    		tip = getString(R.string.plan_empty_name);
    	} else if (TextUtils.isEmpty(plan.EndTime)){
    		tip = getString(R.string.plan_empty_time);
    	} else {
    		isSuccess = true;
    	}
    	if (!isSuccess) {
    		ToastUtil.showToast(this, "【计划"+position+"】"+tip);
    	}
    	
    	return isSuccess;
    }
}
