package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;
import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.adapter.AllotAdapter;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.fragment.talker.DynamicFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserFenPaiFlowState;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.task.TaskModifyActivity;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.PersonTextView;
import cc.emw.mobile.view.SwitchButton;

/**
 * 动态·新建工作分派
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_allot)
public class AllotActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_tv_left)
	private TextView mHeaderCancelTv; // 顶部条取消
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderSendTv; // 顶部条发布
	
	@ViewInject(R.id.cm_input_et_content)
	private EditText mContentEt; // 内容
	@ViewInject(R.id.cm_select_ll_select)
	private LinearLayout mSelectRootLayout; // 选择分享人员Layout
	@ViewInject(R.id.cm_select_tv_select)
	private TextView mSelectTv; // 分享范围
	@ViewInject(R.id.cm_select_fl_select)
	private FlowLayout mSelectFlowLayout; // 分享人员Layout
	@ViewInject(R.id.et_allot_name)
	private EditText mTaskNameEt; //任务名称
	@ViewInject(R.id.tv_allot_finishtime)
	private TextView mFinishTimeTv; //完成时间
	@ViewInject(R.id.lv_allot_responser)
	private ExListView mResponserLv;
	@ViewInject(R.id.lv_allot_executor)
	private ExListView mExecutorLv;
	@ViewInject(R.id.cm_ll_worker)
	private LinearLayout mWorkerLayout;
	@ViewInject(R.id.cm_sb_worker)
	private SwitchButton mWorkerSb;
	
	private static final int CHOSE_SHAREPERSON_CODE = 1;
	private static final int CHOSE_RESPONSER_CODE = 2;
	private static final int CHOSE_EXECUTOR_CODE = 3;
	
	private Dialog mLoadingDialog; //加载框
	private ArrayList<UserInfo> selectList, responserList, executorList; // 分享人员，负责人，执行人列表数据
    private AllotAdapter responserAdapter, executorAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackTip(true);
        initView();
    }
    
    private void initView() {
    	mHeaderCancelTv.setVisibility(View.VISIBLE);
    	mHeaderCancelTv.setText(R.string.cancel);
        mHeaderTitleTv.setText(R.string.allot);
        mHeaderSendTv.setText(R.string.finish);
        mHeaderSendTv.setVisibility(View.VISIBLE);

        final TimePickerView pwTime = new TimePickerView(this, Type.ALL);//时间选择器
		pwTime.setTitle(getString(R.string.allot_finishtime));
		pwTime.setCancelable(true);
        pwTime.setOnTimeSelectListener(new OnTimeSelectListener() { //时间选择后回调

            @Override
            public void onTimeSelect(Date date) {
            	mFinishTimeTv.setTag(date.getTime());
            	mFinishTimeTv.setText(getTime(date));
            }
        });
        mFinishTimeTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HelpUtil.hideSoftInput(AllotActivity.this, mContentEt);
				pwTime.show();
			}
		});
        mWorkerLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mWorkerSb.toggle();
			}
		});
        
        responserAdapter = new AllotAdapter(this, responserList);
        mResponserLv.setAdapter(responserAdapter);
        executorAdapter = new AllotAdapter(this, executorList);
        mExecutorLv.setAdapter(executorAdapter);
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
		    	if (validate(content)) {
					send(content);
		    	}
				break;
    	}
    }
    @Event({R.id.btn_allot_addresponser, R.id.btn_allot_addexecutor})
    private void onAddClick(View v) {
    	switch (v.getId()) {
			case R.id.btn_allot_addresponser: //添加负责人
				Intent responserIntent = new Intent(this, ContactSelectActivity.class);
				responserIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
				responserIntent.putExtra("select_list", responserList);
		    	startActivityForResult(responserIntent, CHOSE_RESPONSER_CODE);
				break;
			case R.id.btn_allot_addexecutor: //添加执行人
				Intent executorIntent = new Intent(this, ContactSelectActivity.class);
				executorIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
				executorIntent.putExtra("select_list", executorList);
		    	startActivityForResult(executorIntent, CHOSE_EXECUTOR_CODE);
				break;
    	}
    }
    @Event(R.id.cm_select_ll_select)
    private void onSelectClick(View v) {
    	Intent intent = new Intent(this, ContactSelectActivity.class);
		intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
    	intent.putExtra("select_list", selectList);
    	startActivityForResult(intent, CHOSE_SHAREPERSON_CODE);
    }

	@Override
	protected void onPause() {
		super.onPause();
		mContentEt.clearFocus();
		mTaskNameEt.clearFocus();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
				case CHOSE_SHAREPERSON_CODE:
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
				case CHOSE_RESPONSER_CODE:
					responserList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
					responserAdapter.setDataList(responserList);
					responserAdapter.notifyDataSetChanged();
					break;
				case CHOSE_EXECUTOR_CODE:
					executorList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
					executorAdapter.setDataList(executorList);
					executorAdapter.notifyDataSetChanged();
					break;
			}
    	}
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
	 * 非空验证
	 * @param content
	 * @return
	 */
	private boolean validate(String content) {
		boolean isSuccess = false;
		String tip = "";
		if (TextUtils.isEmpty(content)) {
			tip = getString(R.string.empty_content_tips);
		} else if (TextUtils.isEmpty(mTaskNameEt.getText().toString().trim())) {
			tip = getString(R.string.allot_empty_taskname);
		} else if (TextUtils.isEmpty(mFinishTimeTv.getText())) {
			tip = getString(R.string.allot_empty_finishtime);
		} else if (responserAdapter.getDataList().size() == 0) {
			tip = getString(R.string.allot_empty_responser);
		} else {
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTimeInMillis(System.currentTimeMillis());
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTimeInMillis((Long)mFinishTimeTv.getTag());
			if (startCalendar.after(endCalendar)) {
				tip = getString(R.string.allot_finishtime_less_curtime);

			} else {
				isSuccess = true;
			}

		}
		if (!isSuccess) {
			ToastUtil.showToast(this, tip);
		}

		return isSuccess;
	}

    /**
     * 发布
     * @param content
     */
    private void send(String content) {
		ArrayList<NoteRole> nrList = new ArrayList<NoteRole>();
		if (selectList != null && selectList.size() > 0) {
			for (int i = 0, size = selectList.size(); i < size; i++) {
				NoteRole role = new NoteRole();
				role.ID = selectList.get(i).ID;
				role.Name = selectList.get(i).Name;
				role.Image = selectList.get(i).Image;
				role.Type = NoteRoleTypes.User;
				nrList.add(role);
			}
		}
		
		UserFenPai fenPai = new UserFenPai();
		fenPai.Type = UserNoteAddTypes.Task;
		fenPai.TaskType = 1;
		fenPai.State = 1;
		fenPai.Title = mTaskNameEt.getText().toString();
		fenPai.Mark = "";
		fenPai.StartTime = getTime(new Date(System.currentTimeMillis()));
		fenPai.FinishTime = mFinishTimeTv.getText().toString();
		fenPai.Creator = PrefsUtil.readUserInfo().ID;
		StringBuilder mainIds = new StringBuilder();
		StringBuilder moreIds = new StringBuilder();
		for (int i = 0, size = responserAdapter.getDataList().size(); i< size; i++) {
			UserInfo user = responserAdapter.getDataList().get(i);
			mainIds.append(user.ID);
			if (i < size - 1) {
				mainIds.append(",");
			}
		}
		for (int i = 0, size = executorAdapter.getDataList().size(); i< size; i++) {
			UserInfo user = executorAdapter.getDataList().get(i);
			moreIds.append(user.ID);
			if (i < size - 1) {
				moreIds.append(",");
			}
		}
		fenPai.MainUser = mainIds.toString();
		fenPai.MoreUser = moreIds.toString();
		fenPai.FlowState = UserFenPaiFlowState.Normal;
		fenPai.Yxj = 1;
		fenPai.Line_Schedule = "[]";
		fenPai.Files = "[]";
		fenPai.IsJobSync = mWorkerSb.isChecked() ? 1 : 0;
		Gson gson = new Gson();
		fenPai.NoteRoles = gson.toJson(nrList);
		fenPai.NoteContent = content;
		
		API.TalkerAPI.AddFenPai(fenPai, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(AllotActivity.this, R.string.publish_error);
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
					ToastUtil.showToast(AllotActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
					sendBroadcast(intent); // 刷新首页列表
					if (mWorkerSb.isChecked()) {
						intent = new Intent(TaskModifyActivity.ACTION_CREATE_TASK);
						sendBroadcast(intent); // 刷新任务列表
					}
					finish();
				} else {
					ToastUtil.showToast(AllotActivity.this, R.string.publish_error);
				} 
			}
		});
    }
    
    
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    } 
}
