package cc.emw.mobile.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.constant.TaskConstant;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.adapter.ResponserAdapter;
import cc.emw.mobile.project.view.RelativeScheduleActivity;
import cc.emw.mobile.task.adapter.ColorAdapter;
import cc.emw.mobile.task.adapter.DevideAttachmentAdapter;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.OptionsPickerView.OnOptionsSelectListener;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;

@ContentView(R.layout.activity_modify_task)
public class TaskModifyActivity extends BaseActivity implements
		ITaskModifyView, OnClickListener {
	@ViewInject(R.id.cm_header_tv_left)
	private TextView mHeaderTvLeft;// 头部左边文字
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;// 头部左边按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;// 头部标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderTvRight;// 头部右边文字
	@ViewInject(R.id.et_taskmodify_name)
	private EditText mName;// 任务名称
	@ViewInject(R.id.et_taskmodify_description)
	private EditText mDescription;// 任务描述
	@ViewInject(R.id.tv_taskmodify_startTime)
	private TextView mStartTime;// 任务开始时间
	@ViewInject(R.id.tv_taskmodify_endTime)
	private TextView mEndTime;// 任务结束时间
	@ViewInject(R.id.lv_taskmodify_responser)
	private ListView mResponserListView;// 负责人listview
	@ViewInject(R.id.lv_taskmodify_executor)
	private ListView mExecutorListView;// 执行人listview
	@ViewInject(R.id.ll_taskmodify_add_responer)
	private LinearLayout mAddResponser;// 添加负责人 按钮
	@ViewInject(R.id.ll_taskmodify_add_executor)
	private LinearLayout mAddExecutor;// 添加执行人 按钮
	@ViewInject(R.id.tv_taskmodify_emergency)
	private TextView mEmergency;// 紧急程度

	@ViewInject(R.id.ll_taskmodify_work_project)
	private LinearLayout mWorkProject;// 工作项目
	@ViewInject(R.id.ll_taskmodify_active_schedule)
	private LinearLayout mActiveSchedule;// 活动日程
	@ViewInject(R.id.ll_taskmodify_repository)
	private LinearLayout mRepository;// 知识库
	@ViewInject(R.id.tv_taskmodify_projectCount)
	private TextView mProjectCount;// 工作项目数量
	@ViewInject(R.id.tv_taskmodify_scheduleCount)
	private TextView mScheduleCount;// 活动日程数量
	@ViewInject(R.id.tv_taskmodify_repositoryCount)
	private TextView mRepositoryCount;// 知识库数量

	@ViewInject(R.id.ll_taskmodify_modify_create)
	private LinearLayout mRelative;// 关联工作项目，知识库等视图容器
	@ViewInject(R.id.ll_taskmodify_attach)
	private LinearLayout mAttachContainer;// 附件视图容器
	@ViewInject(R.id.lv_taskmodify_attach)
	private ListView mAttachListView;// 附件listview
	@ViewInject(R.id.ll_taskmodify_add_attach)
	private LinearLayout mAddAttach;// 附件添加按钮

	private UserFenPai mData;// 任务分派
	private int flag;// 标记是哪个页面跳转过来; 编辑 、新建、分解任务。
	private static final int REQUESTCODERESPONSER = 1;// 负责人的请求码
	private static final int REQUESTCODEEXECUTOR = 2;// 执行人的请求码
	private static final int WORKPROJECT = 3;// 工作项目的请求
	private static final int REPOSITORY = 10;// 知识库的请求
	private static final int SCHEDUEL = 4;// 相关日程的请求
	private List<UserInfo> mainUserList;
	private List<UserInfo> moreUserList;
	private ResponserAdapter mResponserAdapter;
	private ResponserAdapter mExecutorAdapter;
	private List<UserInfo> users = new ArrayList<UserInfo>();// 储存添加用户
	public static final String ACTION_MODIFY_TASK = "cc.emw.mobile.modify_task";// 修改任务发送广播频段
	public static final String ACTION_CREATE_TASK = "cc.emw.mobile.create_task";// 创建任务发送广播频段
	public static final String SEND_USERFENPAI = "send_userfenpai";// 发送广播携带数据
	// private static final String TAG = "TaskModifyActivity";

	public static final String RELATION_PROJECT = "relation_project";// 相关项目

	private TimePickerView mStartPopupWindow;// 时间表弹窗
	private int date_type = 0;// 时间类型 0表示开始时间 1表示结束时间。
	private UserProject mUserProject;
	private DevideAttachmentAdapter mDevideAttachmentAdapter;
	// private ArrayList<Attachment> mEditTaskFiles;// 编辑界面获取的附件信息

	public static final String PROJECT_TO_TASK = "projectID";// 项目模块新建任务字段
	private int mProjectID;// 项目新建任务 传入的ID值

	private ListView colorListView;// 紧急程度选择ListView;
	private ColorAdapter colorAdapter;// 紧急程度Adapter;
	private PopupWindow mPw;
	private Drawable mDrawableRight;
	private String[] mScheduelIDs;
	private Drawable mDrawableLeft;// 紧急程度左边图片
	private Dialog mLoadingDialog;// 加载框
	private OptionsPickerView<String> mOptionsPickerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		flag = intent.getIntExtra(TaskConstant.TASK_FLAG,
				TaskConstant.CREATE_TASK);
		mProjectID = intent.getIntExtra(PROJECT_TO_TASK, 0);
		/**
		 * 是否携带任务数据
		 */
		mData = (UserFenPai) intent
				.getSerializableExtra(TaskConstant.TASK_MODIFY);
		if (mData == null) {
			mData = new UserFenPai();
		}
		initView();
		initTimeSelector();
		initOpionSelector();
		initClick();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		mUserProject = new UserProject();
		mResponserAdapter = new ResponserAdapter(TaskModifyActivity.this);
		mExecutorAdapter = new ResponserAdapter(TaskModifyActivity.this);
		mDrawableRight = getResources().getDrawable(
				R.drawable.btn_calendar_month_rightarrow);// 紧急右边箭头图标
		// 初始化popupWindow
		// initPopupWindow();
		switch (flag) {
		case TaskConstant.CREATE_TASK:
			mHeaderBackBtn.setVisibility(View.VISIBLE);
			mHeaderTitleTv.setText(R.string.task_modify_create_task);

			mResponserAdapter.setArrayUser(null, true);
			mExecutorAdapter.setArrayUser(null, true);
			if (mProjectID != 0) {
				mProjectCount.setVisibility(View.VISIBLE);
				mProjectCount.setText("1");
			}
			break;
		case TaskConstant.EDIT_TASK:
			// 修改任务
			mHeaderTvLeft.setVisibility(View.VISIBLE);
			mHeaderTvLeft.setText(R.string.cancel);
			mHeaderTitleTv.setText(R.string.task_modify_edit);
			// 编辑界面传来的数据
			mName.setText(mData.Title);
			// mName.setSelection(mData.getTitle().length());
			mDescription.setText(mData.Mark);// 目前测试任务描述为空
			// 紧急程度设置
			int yxj = mData.Yxj;
			mEmergency.setText(ColorAdapter.colorStr[yxj - 1]);
			mEmergency.setTextColor(getResources().getColor(R.color.cm_text));
			mDrawableLeft = getResources().getDrawable(
					ColorAdapter.colorIcon[yxj - 1]);
			mEmergency.setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft,
					null, mDrawableRight, null);

			mStartTime.setText(mData.StartTime);
			mStartTime.setTextColor(getResources().getColor(R.color.cm_text));
			mEndTime.setText(mData.FinishTime);
			mEndTime.setTextColor(getResources().getColor(R.color.cm_text));
			// 负责人展示
			String mainUser = mData.MainUser;
			mainUserList = TaskUtils.getUsers(mainUser);
			// 执行人展示
			String moreUser = mData.MoreUser;
			moreUserList = TaskUtils.getUsers(moreUser);
			// 遍历负责人 如果当前用户是负责人中的议员就具有添加负责人执行人的权限
			UserInfo currentUser = EMWApplication.personMap.get(PrefsUtil
					.readUserInfo().ID);
			if (mainUserList.contains(currentUser)) {
				mResponserAdapter.setArrayUser(mainUserList, true);
				mExecutorAdapter.setArrayUser(moreUserList, true);
			} else {
				// 不包含
				mResponserAdapter.setArrayUser(mainUserList, false);
				mExecutorAdapter.setArrayUser(moreUserList, false);
				mAddResponser.setVisibility(View.GONE);
				mAddExecutor.setVisibility(View.GONE);
			}

			// 展示工作项目、知识库、相关日程数量
			if (mData.ProjectId != 0) {
				mProjectCount.setVisibility(View.VISIBLE);
				mProjectCount.setText("1");
			}
			// 知识库数量的展示
			String[] fileString = TaskUtils.getStringID(mData.Files);
			if (fileString.length != 0) {
				mRepositoryCount.setVisibility(View.VISIBLE);
				mRepositoryCount.setText(fileString.length + "");
				// mTempFiles.addAll(mEditTaskFiles);// 编辑界面初始化先将已经存在的附件添加到临时容器中
			}
			mScheduelIDs = TaskUtils.getStringID(mData.Line_Schedule);
			if (mScheduelIDs.length != 0) {
				mScheduleCount.setText(mScheduelIDs.length + "");
				mScheduleCount.setVisibility(View.VISIBLE);
			} else {
				mScheduleCount.setVisibility(View.GONE);
			}

			break;
		case TaskConstant.DEVIDE_TASK:
			mRelative.setVisibility(View.GONE);
			mAttachContainer.setVisibility(View.VISIBLE);
			mHeaderTitleTv.setText(R.string.task_modify_devide);
			mHeaderBackBtn.setVisibility(View.VISIBLE);

			mResponserAdapter.setArrayUser(null, true);
			mExecutorAdapter.setArrayUser(null, true);
			mDevideAttachmentAdapter = new DevideAttachmentAdapter(
					TaskModifyActivity.this);
			mDevideAttachmentAdapter.setArrayData(null, true);
			mAttachListView.setAdapter(mDevideAttachmentAdapter);
			TaskUtils.setListViewHeightBasedOnChildren(mAttachListView);
			break;

		default:
			break;
		}
		mHeaderTvRight.setVisibility(View.VISIBLE);
		mHeaderTvRight.setText(R.string.finish);
		mResponserAdapter.notifyDataSetChanged();
		mResponserListView.setAdapter(mResponserAdapter);
		TaskUtils.setListViewHeightBasedOnChildren(mResponserListView);

		mExecutorAdapter.notifyDataSetChanged();
		mExecutorListView.setAdapter(mExecutorAdapter);
		TaskUtils.setListViewHeightBasedOnChildren(mExecutorListView);
	}

	/**
	 * 初始化popupWindow
	 */
	private void initPopupWindow() {
		colorListView = new ListView(this);
		colorAdapter = new ColorAdapter(this);
		colorListView.setAdapter(colorAdapter);

		mPw = new PopupWindow(colorListView, -2, 250);
		mPw.setOutsideTouchable(true);
		mPw.setFocusable(true);
		mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		colorListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				mData.Yxj = position + 1;// 对优先级进行赋值
				// 对紧急程度文本控件进行设值
				initMemergency(position);
				showPopupWindow();
			}
		});
	}

	/**
	 * 任务紧急程度展示成底部Dialog
	 */
	private void showEmergencyDialog() {
		ActionSheetDialog dialog = new ActionSheetDialog(this).builder()
				.setTitle(getString(R.string.task_modify_emergenct));

		dialog.addSheetItem("普通", null, new OnSheetItemClickListener() {

			@Override
			public void onClick(int which) {
				mData.Yxj = which;
				// Toast.makeText(TaskModifyActivity.this, ""+which,
				// Toast.LENGTH_SHORT).show();
				initMemergency(which - 1);
			}
		});
		dialog.addSheetItem("紧急", null, new OnSheetItemClickListener() {

			@Override
			public void onClick(int which) {
				mData.Yxj = which;
				// Toast.makeText(TaskModifyActivity.this, ""+which,
				// Toast.LENGTH_SHORT).show();
				initMemergency(which - 1);
			}
		});
		dialog.addSheetItem("非常紧急", null, new OnSheetItemClickListener() {

			@Override
			public void onClick(int which) {
				mData.Yxj = which;
				// Toast.makeText(TaskModifyActivity.this, ""+which,
				// Toast.LENGTH_SHORT).show();
				initMemergency(which - 1);
			}
		});
		dialog.show();
	}

	/**
	 * 根据条目选择紧急程度，并对紧急程度文本控件进行设值
	 * 
	 * @param position
	 *            条目 0 普通、1紧急、2非常紧急
	 */
	@SuppressWarnings("deprecation")
	private void initMemergency(int position) {
		mEmergency.setText(ColorAdapter.colorStr[position]);
		mEmergency.setTextColor(getResources().getColor(R.color.cm_text));
		mDrawableLeft = getResources().getDrawable(
				ColorAdapter.colorIcon[position]);
		mEmergency.setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, null,
				mDrawableRight, null);
	}

	private void initTimeSelector() {
		mStartPopupWindow = new TimePickerView(this, Type.ALL);// 时间选择器
		mStartPopupWindow.setTitle(getString(R.string.beg_time));
		mStartPopupWindow.setCancelable(true);
		mStartPopupWindow.setOnTimeSelectListener(new OnTimeSelectListener() { // 时间选择后回调
			@Override
			public void onTimeSelect(Date date) {
				updateDiaplay(date);
			}
		});

	}

	private void initOpionSelector() {
		// 紧急程度滚轴
		ArrayList<String> sorts = new ArrayList<String>();
		sorts.add(TaskConstant.TaskEmergencyState.NORMAL);
		sorts.add(TaskConstant.TaskEmergencyState.EMERGENCY);
		sorts.add(TaskConstant.TaskEmergencyState.VERY_EMERGENCY);
		mOptionsPickerView = new OptionsPickerView<String>(this);
		mOptionsPickerView.setPicker(sorts);
		mOptionsPickerView.setTitle(getString(R.string.task_modify_emergenct));
		mOptionsPickerView.setCancelable(true);
		mOptionsPickerView.setCyclic(false);// 无限循环

		if (mData.Yxj > 0) {
			mOptionsPickerView.setSelectOptions(mData.Yxj - 1);
		}
		mOptionsPickerView
				.setOnoptionsSelectListener(new OnOptionsSelectListener() {

					@Override
					public void onOptionsSelect(int options1, int option2,
												int options3) {
						// 一级联动
						// 将当前的条目信息返回
						mData.Yxj = options1 + 1;
						// 并且显示在紧急控件中
						initMemergency(options1);
					}
				});
	}

	private void initClick() {
		mHeaderBackBtn.setOnClickListener(this);
		mHeaderTvLeft.setOnClickListener(this);
		mHeaderTvRight.setOnClickListener(this);
		mAddResponser.setOnClickListener(this); // 添加更多负责人的点击事件
		mAddExecutor.setOnClickListener(this);// 添加更多执行人的点击事件
		mStartTime.setOnClickListener(this);// 开始时间的点击事件
		mEndTime.setOnClickListener(this);// 完成时间的点击事件

		mWorkProject.setOnClickListener(this);// 工作项目的点击事件
		mActiveSchedule.setOnClickListener(this);// 活动日程的点击事件
		mRepository.setOnClickListener(this);// 知识库的点击事件
		mAttachContainer.setOnClickListener(this);// 分解任务中 附件容器的点击事件
		mEmergency.setOnClickListener(this);// 紧急程度的点击事件

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
		case R.id.cm_header_tv_left:
			onBackPressed();
			break;
		case R.id.tv_taskmodify_emergency:
			// 紧急程度的选择 弹出popuwindow;
			HelpUtil.hideSoftInput(this, mEmergency);
			// showPopupWindow();
			// showEmergencyDialog();
			mOptionsPickerView.show();

			break;
		case R.id.cm_header_tv_right:
			TaskPresenter taskPresenter = new TaskPresenter(this);
			// 对任务对象进行赋值
			String name = mName.getText().toString().trim();
			if (TextUtils.isEmpty(name)) {
				Toast.makeText(TaskModifyActivity.this, R.string.allot_empty_taskname,
						Toast.LENGTH_SHORT).show();
				return;
			}
			mData.Title = name;
			String des = mDescription.getText().toString().trim();
			if (TextUtils.isEmpty(des)) {
				Toast.makeText(TaskModifyActivity.this, R.string.task_modify_empty_taskdes,
						Toast.LENGTH_SHORT).show();
				return;
			}
			mData.Mark = des;
			String statTime = mStartTime.getText().toString().trim();
			if (getString(R.string.plan_time_hint).equals(statTime)) {
				Toast.makeText(TaskModifyActivity.this, R.string.task_modify_beginTime,
						Toast.LENGTH_SHORT).show();
				return;
			}
			mData.StartTime = statTime;
			String endTime = mEndTime.getText().toString().trim();
			if (getString(R.string.task_detail_endtime).equals(endTime)) {
				Toast.makeText(TaskModifyActivity.this, R.string.task_modify_endtime,
						Toast.LENGTH_SHORT).show();
				return;
			}
			mData.FinishTime = endTime;
			// 获取紧急状态(通过任务实体中的yxj值更改)
			if (mEmergency.getText().equals(getString(R.string.task_detail_chose))) {
				Toast.makeText(this, R.string.task_modify_choose_emergency, Toast.LENGTH_SHORT).show();
				return;
			}
			// 负责人
			ArrayList<UserInfo> responserList = mResponserAdapter
					.getArrayUser();
			mData.MainUser = members2string(responserList);
			// 将负责人转换成字符串
			ArrayList<UserInfo> executorList = mExecutorAdapter.getArrayUser();
			mData.MoreUser = members2string(executorList);
			if ("".equals(mData.MainUser)) {
				Toast.makeText(TaskModifyActivity.this, R.string.task_modify_chose_responser_tips,
						Toast.LENGTH_SHORT).show();
				return;
			}
			switch (flag) {
			case TaskConstant.EDIT_TASK:
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
				mLoadingDialog.show();
				taskPresenter.modifyTask(mData);
				break;
			case TaskConstant.CREATE_TASK:

				if (mData.Files == null) {
					mData.Files = "[]";// 用户新建任务的时候，没有选择任何附件操作
				}
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
				mLoadingDialog.show();
				mData.FlowState = TaskConstant.FlowState.NORMAL;
				mData.Creator = PrefsUtil.readUserInfo().ID;
				mData.ID = 0;
				mData.ProjectId = mProjectID;
				mData.State = TaskConstant.TaskState.UNSTART;// 新建任务的状态为未完成
				taskPresenter.createTask(mData);
				break;
			case TaskConstant.DEVIDE_TASK:
				if (mData.Files == null) {
					mData.Files = "[]";// 用户分解任务的时候，没有选择任何附件操作
				}
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
				mLoadingDialog.show();
				mData.FlowState = TaskConstant.FlowState.NORMAL;
				mData.PID = mData.ID;// 任务的父ID是上一个任务的ID
				mData.ID = 0;
				mData.ProjectId = mProjectID;
				mData.State = TaskConstant.TaskState.UNSTART;
				mData.Creator = PrefsUtil.readUserInfo().ID;
				taskPresenter.createTask(mData);

				break;

			default:
				break;
			}

			break;
		case R.id.ll_taskmodify_add_responer:
			// 更多负责人的点击事件,跳转到选择人员列表中选择
			Intent addRespnser = new Intent(TaskModifyActivity.this,
					ContactSelectActivity.class);
			addRespnser.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE,
					ContactSelectActivity.MULTI_SELECT);
			startActivityForResult(addRespnser, REQUESTCODERESPONSER);
			break;
		case R.id.ll_taskmodify_add_executor:
			Intent addExcutor = new Intent(TaskModifyActivity.this,
					ContactSelectActivity.class);
			addExcutor.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE,
					ContactSelectActivity.MULTI_SELECT);
			startActivityForResult(addExcutor, REQUESTCODEEXECUTOR);
			break;
		case R.id.tv_taskmodify_startTime:
			// 开始时间的点击事件，底部弹出时间表
			date_type = 0;
			mStartPopupWindow.show();
			HelpUtil.hideSoftInput(TaskModifyActivity.this, mStartTime);
			break;
		case R.id.tv_taskmodify_endTime:
			date_type = 1;
			mStartPopupWindow.show();
			HelpUtil.hideSoftInput(TaskModifyActivity.this, mEndTime);
			break;
		case R.id.ll_taskmodify_work_project:
			// 跳转到相关项目列表
			Intent intent = new Intent(this, WorkProjectActivity.class);
			intent.putExtra(RELATION_PROJECT, mData.ProjectId);
			startActivityForResult(intent, WORKPROJECT);
			break;
		case R.id.ll_taskmodify_active_schedule:
			// 跳转到相关日程
			Intent scheduleIntent = new Intent(this,
					RelativeScheduleActivity.class);
			scheduleIntent.putExtra(RelativeScheduleActivity.SCHEDULE,
					mData.Line_Schedule);
			startActivityForResult(scheduleIntent, SCHEDUEL);
			break;
		case R.id.ll_taskmodify_repository:
			// 跳转到知识库
			Intent intentFile = new Intent(this, FileSelectActivity.class);
			intentFile.putExtra(FileSelectActivity.EXTRA_SELECT_IDS,
					mData.Files);
			startActivityForResult(intentFile, REPOSITORY);
			break;
		case R.id.ll_taskmodify_attach:
			// 跳转到知识库
			Intent intentFile2 = new Intent(this, FileSelectActivity.class);
			intentFile2.putExtra(FileSelectActivity.EXTRA_SELECT_LIST,
					mDevideAttachmentAdapter.getArrayData());
			startActivityForResult(intentFile2, REPOSITORY);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mName.clearFocus();
		mDescription.clearFocus();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODERESPONSER
				&& resultCode == Activity.RESULT_OK) {
			// 负责人请求添加联系人界面
			ArrayList<UserInfo> userRets = (ArrayList<UserInfo>) data
					.getSerializableExtra("select_list");
			users.clear();
			// 去除重复的负责人
			users.addAll(mResponserAdapter.getArrayUser());
			users = chekCopyUsers(userRets, users);
			mResponserAdapter.setArrayUser(users, true);
			mResponserAdapter.notifyDataSetChanged();
			TaskUtils.setListViewHeightBasedOnChildren(mResponserListView);
		} else if (requestCode == REQUESTCODEEXECUTOR
				&& resultCode == Activity.RESULT_OK) {
			// 执行人添加
			ArrayList<UserInfo> userRets = (ArrayList<UserInfo>) data
					.getSerializableExtra("select_list");
			users.clear();
			users.addAll(mExecutorAdapter.getArrayUser());
			users = chekCopyUsers(userRets, users);
			mExecutorAdapter.setArrayUser(users, true);
			mExecutorAdapter.notifyDataSetChanged();
			TaskUtils.setListViewHeightBasedOnChildren(mExecutorListView);
		} else if (requestCode == WORKPROJECT
				&& resultCode == Activity.RESULT_OK) {

			mUserProject = (UserProject) data
					.getSerializableExtra(WorkProjectActivity.WORK_PROJECT);
			// 工作项目
			if (mUserProject.ID != 0) {
				mProjectCount.setVisibility(View.VISIBLE);
				mProjectCount.setText("1");
			} else {
				mProjectCount.setVisibility(View.GONE);
				mProjectCount.setText("");
			}
			mProjectID = mUserProject.ID;
			mData.ProjectId = mUserProject.ID;
			Toast.makeText(this, mUserProject.ID + "", Toast.LENGTH_SHORT)
					.show();

		} else if (requestCode == REPOSITORY
				&& resultCode == Activity.RESULT_OK) {
			ArrayList<Files> list = (ArrayList<Files>) data
					.getSerializableExtra("select_list");

			if (flag == TaskConstant.DEVIDE_TASK) {
				// 当前界面是分解页面
				mData.Files = TaskUtils.getRepositoryArray(list);// 转换成"[1,2,3]"
				mDevideAttachmentAdapter.setArrayData(list, true);
				mDevideAttachmentAdapter.notifyDataSetChanged();
				TaskUtils.setListViewHeightBasedOnChildren(mAttachListView);
			} else if (flag == TaskConstant.EDIT_TASK) {
				// 编辑界面
				mData.Files = TaskUtils.getRepositoryArray(list);
				// UI展示
				if (list.size() != 0) {
					// 显示出数量
					mRepositoryCount.setVisibility(View.VISIBLE);
					mRepositoryCount.setText(list.size() + "");
				} else {
					mRepositoryCount.setVisibility(View.INVISIBLE);
				}

			} else {
				// 新建任务界面
				// 编辑界面
				mData.Files = TaskUtils.getRepositoryArray(list);
				// UI展示
				if (list.size() != 0) {
					// 显示出数量
					mRepositoryCount.setVisibility(View.VISIBLE);
					mRepositoryCount.setText(list.size() + "");
				} else {
					mRepositoryCount.setVisibility(View.INVISIBLE);
				}
			}
		} else if (requestCode == SCHEDUEL && resultCode == Activity.RESULT_OK) {
			String scheduel = data
					.getStringExtra(RelativeScheduleActivity.SCHEDULE);
			mData.Line_Schedule = scheduel;
			// 展示到视图上
			// 拆分字符串
			mScheduelIDs = TaskUtils.getStringID(scheduel);
			if (mScheduelIDs.length != 0) {
				mScheduleCount.setText(mScheduelIDs.length + "");
				mScheduleCount.setVisibility(View.VISIBLE);
			} else {
				mScheduleCount.setVisibility(View.GONE);
			}

		}

	}

	/**
	 * 添加不重复人员
	 * 
	 * @param userRets
	 *            添加的人员列表
	 * @param users
	 *            目的人员列表
	 * @return 返回添加后的目的人员列表
	 */
	private List<UserInfo> chekCopyUsers(List<UserInfo> userRets,
			List<UserInfo> users) {
		for (int i = 0; i < userRets.size(); i++) {
			boolean haveFlag = false;// 返回列表中是否有重复
			for (int j = 0; j < users.size(); j++) {
				if (userRets.get(i).ID == users.get(j).ID) {
					haveFlag = true;
					break;
				}
			}
			if (!haveFlag) {
				users.add(userRets.get(i));
			}
		}
		return users;
	}

	private String members2string(List<UserInfo> users) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < users.size(); i++) {
			sb.append(users.get(i).ID);
			if (i != users.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString().trim();
	}

	@Override
	public void createTask(String s) {
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		if (Integer.parseInt(s) > 0) {
			switch (flag) {
			case TaskConstant.CREATE_TASK:
				ToastUtil.showToast(this, R.string.task_modify_create_task_success,
						R.drawable.tishi_ico_gougou);
				break;
			case TaskConstant.DEVIDE_TASK:
				ToastUtil.showToast(this, R.string.task_modify_devide_task_success,
						R.drawable.tishi_ico_gougou);
				break;
			default:
				break;
			}
			Intent intent = new Intent();
			intent.setAction(ACTION_CREATE_TASK);
			sendBroadcast(intent);
			onBackPressed();
		} else {
			switch (flag) {
			case TaskConstant.CREATE_TASK:
				ToastUtil.showToast(this, R.string.task_modify_create_task_error);
				break;
			case TaskConstant.DEVIDE_TASK:
				ToastUtil.showToast(this, R.string.task_modify_devide_task_error);
				break;
			default:
				break;
			}

		}
	}

	@Override
	public void modifyTask(String s) {
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		if ("1".equals(s)) {
			ToastUtil.showToast(this, R.string.task_modify_edit_task_success, R.drawable.tishi_ico_gougou);
			Intent intent = new Intent();
			intent.setAction(ACTION_MODIFY_TASK);
			intent.putExtra(SEND_USERFENPAI, mData);
			sendBroadcast(intent);
			onBackPressed();
		} else {
			ToastUtil.showToast(this, R.string.task_modify_edit_task_error);
		}
	}

	@Override
	public void onError(Throwable ex, boolean isOnCallback) {
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		switch (flag) {
		case TaskConstant.CREATE_TASK:
			ToastUtil.showToast(this, R.string.task_modify_create_task_error);
			break;
		case TaskConstant.DEVIDE_TASK:
			ToastUtil.showToast(this, R.string.task_modify_devide_task_error);
			break;
		case TaskConstant.EDIT_TASK:
			ToastUtil.showToast(this, R.string.task_modify_edit_task_error);
			break;
		default:
			break;
		}

	}

	private void updateDiaplay(Date date) {
		String formatString = getResources().getString(R.string.timeformat6);
		SimpleDateFormat f = new SimpleDateFormat(formatString, Locale.CHINA);
		try {
			if (date_type == 0) {
				String endTime = mEndTime.getText().toString().trim();
				if (getString(R.string.task_detail_endtime).equals(endTime)) {
					mStartTime.setText(f.format(date));
					mStartTime.setTextColor(getResources().getColor(
							R.color.cm_text));
				} else {
					Date d = f.parse(endTime);
					if (d.getTime() > date.getTime()) {
						mStartTime.setText(f.format(date));
						mStartTime.setTextColor(getResources().getColor(
								R.color.cm_text));
					} else {
						Toast.makeText(this, R.string.task_modify_endtime_compare_starttime, Toast.LENGTH_SHORT)
								.show();
					}
				}
			} else {
				String startTime = mStartTime.getText().toString().trim();
				if (getString(R.string.plan_time_hint).equals(startTime)) {
					mEndTime.setText(f.format(date));
					mEndTime.setTextColor(getResources().getColor(
							R.color.cm_text));
				} else {
					Date d = f.parse(mStartTime.getText().toString());
					if (d.getTime() < date.getTime()) {
						mEndTime.setText(f.format(date));
						mEndTime.setTextColor(getResources().getColor(
								R.color.cm_text));
					} else {
						Toast.makeText(this, R.string.task_modify_endtime_compare_starttime, Toast.LENGTH_SHORT)
								.show();
					}
				}

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 控制颜色PopupWindow的显示隐藏
	 */
	private void showPopupWindow() {
		if (mPw != null && mPw.isShowing()) {
			mPw.dismiss();
		} else {
			mEmergency.measure(0, 0);
			mPw.setWidth(mEmergency.getWidth());
			mPw.showAsDropDown(mEmergency);
		}
	}

	@Override
	public void showFinish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getFileList(List<Files> files) {
		// TODO Auto-generated method stub

	}

}
