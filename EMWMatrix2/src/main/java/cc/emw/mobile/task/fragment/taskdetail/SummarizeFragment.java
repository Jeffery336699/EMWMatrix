package cc.emw.mobile.task.fragment.taskdetail;

import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.constant.TaskConstant;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.adapter.ResponserAdapter;
import cc.emw.mobile.task.SprintActivity;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.task.TaskModifyActivity;
import cc.emw.mobile.task.adapter.ColorAdapter;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;

import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.zf.iosdialog.widget.ActionSheetDialog.SheetItemColor;
import com.zf.iosdialog.widget.AlertDialog;

@ContentView(R.layout.fragment_task_detail_summarize)
public class SummarizeFragment extends BaseFragment implements ITaskModifyView,
		OnClickListener {
	@ViewInject(R.id.tv_task_summarize_title)
	private TextView mSummarizeTitle;// 任务标题
	@ViewInject(R.id.tv_task_summarize_state)
	private TextView mSummarizeState;
	@ViewInject(R.id.lv_task_summarize_mainuser)
	private ListView mLvMainUser;// 负责人
	@ViewInject(R.id.lv_task_summarize_moreuser)
	private ListView mLvMoreUser;// 执行人
	@ViewInject(R.id.tv_task_summarize_des)
	private TextView mSummarizeDes;// 任务描述
	@ViewInject(R.id.tv_task_summarize_emergency)
	private TextView mSummarizeEmergencyTv;// 紧急文本设置
	@ViewInject(R.id.iv_task_summarize_emergency)
	private ImageView mSummarizeEmergencyIv;// 紧急任务图标更改
	@ViewInject(R.id.tv_task_summarize_end)
	private TextView mSummarizeEnd;// 结束时间
	@ViewInject(R.id.tv_task_summarize_start)
	private TextView mSummarizeStart;// 开始时间
	@ViewInject(R.id.tv_task_summarize_link)
	private TextView mSummarizeLink;// 关联
	@ViewInject(R.id.tv_task_summarize_progress)
	private TextView mSummarizeProgress;// 文本进度设置
	@ViewInject(R.id.pb_task_summarize_progress)
	private ProgressBar mSummarizeProgressPb;// 进度条
	@ViewInject(R.id.tv_task_summarize_devide)
	private TextView mSummarizeDevide;// 任务分解
	@ViewInject(R.id.tv_task_summarize_operation)
	private TextView mSummarizeOperation;// 任务操作

	@ViewInject(R.id.sv_task_summarize)
	private ScrollView mScrollView;

	@ViewInject(R.id.ll_task_summarize_devide_operation)
	private LinearLayout mDevieOperation;// 任务分解任务操作的容器

	private TextView mHeaderRightTv;
	private ImageButton mHeaderRightBtn;
	private final static String[] stateText = { "",
			TaskConstant.TaskStateString.UNSTART,
			TaskConstant.TaskStateString.PROCESSING,
			TaskConstant.TaskStateString.FINISHED };// 任务状态

	private SummarizeBroadcastReceive mReceive;
	private UserFenPai mUserFenPai;
	private int mState;// 任务状态值
	private UserInfo mCurrentUser;
	public static boolean ChargeLimitFlag = false;// 任务操作权限问题 默认为执行人权限
	public static boolean ExecutorLimitFlag = false;// 当前用户是否是执行人队列 默认不是

	private Dialog mLoadingDialog; // 加载框

	private static final int TASK_EXECUTE = 1;
	private static final int TASK_RETURN_WORK = 2;
	private static final int TASK_CHECK = 3;
	private static final int TASK_FINISHED = 4;
	private static final String TAG = "SummarizeFragment";
	private int mOperateType;

	private int mTempState;
	private int mTempFlowState;

	// private UserFenPai mTempUserFenPai = new UserFenPai();// 临时任务实体 任务操作

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// mParams = new RequestParams(HttpConstant.GET_PROJECT);
		// mParams.addQueryStringParameter("userid", PrefsUtil.readUserInfo().ID
		// + "");
		// 注册广播用于接收编辑页面传来的数据并进行更改
		mHeaderRightTv = (TextView) getActivity().findViewById(
				R.id.cm_header_tv_right);
		mHeaderRightBtn = (ImageButton) getActivity().findViewById(
				R.id.cm_header_btn_right);
		TaskDetailActivity ta = (TaskDetailActivity) getActivity();
		mUserFenPai = ta.getUserFenPai();
		mCurrentUser = EMWApplication.personMap
				.get(PrefsUtil.readUserInfo().ID);
		initView();
		initClick();
		// 注册广播监听任务编辑界面传来的数据
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TaskModifyActivity.ACTION_MODIFY_TASK);
		mReceive = new SummarizeBroadcastReceive();
		getActivity().registerReceiver(mReceive, intentFilter);
	}

	private void initView() {
		// 展示相关项目
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// 更改关联项目的名称
				final int projectId = mUserFenPai.ProjectId;
				if (projectId == 0) {
					mSummarizeLink.setText(getString(R.string.task_other));
					return;
				}
				API.TalkerAPI.GetProjectById(projectId,
						new RequestCallback<UserProject>(UserProject.class) {

							@Override
							public void onError(Throwable ex,
									boolean isOnCallback) {
								// TODO
							}

							@Override
							public void onParseSuccess(UserProject projects) {

								mSummarizeLink.setText(projects.Name);
							}
						});
				// x.http().post(mParams,
				// new RequestListener<UserProject>(UserProject.class) {
				//
				// @Override
				// public void onCancelled(CancelledException cex) {
				//
				// }
				//
				// @Override
				// public void onError(Throwable ex,
				// boolean isOnCallback) {
				//
				// }
				//
				// @Override
				// public void onFinished() {
				//
				// }
				//
				// @Override
				// public void onParseSuccess(
				// List<UserProject> projects) {
				// for (int i = 0; i < projects.size(); i++) {
				// if (projectId == projects.get(i).ID) {
				// mSummarizeLink.setText(projects.get(i).Name);
				// return;
				// }
				// }
				// }
				// });
			}
		});
		mSummarizeTitle.setText(mUserFenPai.Title);
		mState = mUserFenPai.State;
		// 设置任务状态
		mSummarizeState.setText(stateText[mState]);
		// 设置开始时间
		String startTime = mUserFenPai.StartTime;
		mSummarizeStart.setText(startTime);
		// 设置结束时间
		mSummarizeEnd.setText(mUserFenPai.FinishTime);
		// 设置百分比
		String format = getActivity().getResources().getString(
				R.string.timeformat6);
		int progress = CommonUtil.getProgress(mUserFenPai.StartTime,
				mUserFenPai.FinishTime, format);
		mSummarizeProgress.setText(progress + "%");
		mSummarizeProgressPb.setProgress(progress);
		// 设置项目描述
		mSummarizeDes.setText(mUserFenPai.Mark);
		// 设置任务紧急状态
		int yxj = mUserFenPai.Yxj;
		if (yxj != 0) {
			mSummarizeEmergencyIv
					.setImageResource(ColorAdapter.colorIcon[yxj - 1]);
			mSummarizeEmergencyTv.setText(ColorAdapter.colorStr[yxj - 1]);
		}
		// 展示主用户
		String mainUser = mUserFenPai.MainUser;
		List<UserInfo> mainUsers = TaskUtils.getUsers(mainUser);
		ResponserAdapter responserAdapter = new ResponserAdapter(getActivity());
		responserAdapter.setArrayUser(mainUsers, false);
		responserAdapter.notifyDataSetChanged();
		mLvMainUser.setAdapter(responserAdapter);
		TaskUtils.setListViewHeightBasedOnChildren(mLvMainUser);
		// 展示更多用户
		String moreUser = mUserFenPai.MoreUser;
		List<UserInfo> moreUserList = TaskUtils.getUsers(moreUser);
		ResponserAdapter executorAdapter = new ResponserAdapter(getActivity());
		executorAdapter.setArrayUser(moreUserList, false);
		executorAdapter.notifyDataSetChanged();
		mLvMoreUser.setAdapter(executorAdapter);
		TaskUtils.setListViewHeightBasedOnChildren(mLvMoreUser);

		if (!mainUsers.contains(mCurrentUser)) {
			// 如果当前用户不属于主要负责人队列
			// 不具有分解任务权限
			mSummarizeDevide.setTextColor(Color.GRAY);
			mSummarizeDevide.setEnabled(false);
			mSummarizeDevide.setClickable(false);
			ChargeLimitFlag = false;
		} else {
			// 权限为主要负责人权限
			ChargeLimitFlag = true;
		}

		if (!moreUserList.contains(mCurrentUser)) {
			// 如果当前用户不属于执行人队列
			ExecutorLimitFlag = false;
		} else {
			// 权限为执行人权限
			ExecutorLimitFlag = true;
		}
		if (!ChargeLimitFlag && !ExecutorLimitFlag) {
			mDevieOperation.setVisibility(View.GONE);
		}
	}

	private void initClick() {
		// 点击事件
		mSummarizeDevide.setOnClickListener(this);
		mSummarizeOperation.setOnClickListener(this);
	}

	@Override
	public void onFirstUserVisible() {
		mHeaderRightBtn.setVisibility(View.GONE);
		if (ChargeLimitFlag) {// 如果是负责人 才有任务编辑的权限
			mHeaderRightTv.setText(getString(R.string.edit));
			mHeaderRightTv.setVisibility(View.VISIBLE);
			mHeaderRightTv.setOnClickListener(this);
		}
		mScrollView.smoothScrollTo(0, 0);
	}

	@Override
	public void onUserVisible() {
		mHeaderRightBtn.setVisibility(View.GONE);
		if (ChargeLimitFlag) {// 如果是负责人 才有任务编辑的权限
			mHeaderRightTv.setText(getString(R.string.edit));
			mHeaderRightTv.setVisibility(View.VISIBLE);
			mHeaderRightTv.setOnClickListener(this);
		}
		mScrollView.smoothScrollTo(0, 0);
	}

	@Override
	public void onClick(View v) {
		final TaskPresenter taskPresenter = new TaskPresenter(this);
		switch (v.getId()) {
		case R.id.cm_header_tv_right:
			// 跳转到编辑界面
			Intent intent = new Intent(getActivity(), TaskModifyActivity.class);
			// 携带数据
			intent.putExtra(TaskConstant.TASK_FLAG, TaskConstant.EDIT_TASK);
			intent.putExtra(TaskConstant.TASK_MODIFY, mUserFenPai);
			startActivity(intent);
			break;
		case R.id.tv_task_summarize_devide:
			Intent newTask = new Intent(getActivity(), TaskModifyActivity.class);
			newTask.putExtra(TaskConstant.TASK_FLAG, TaskConstant.DEVIDE_TASK);
			newTask.putExtra(TaskModifyActivity.PROJECT_TO_TASK,
					mUserFenPai.ProjectId);
			startActivity(newTask);
			break;
		case R.id.tv_task_summarize_operation:
			ActionSheetDialog dialog = new ActionSheetDialog(getActivity())
					.builder().setTitle(getString(R.string.task_operate));
			mTempState = mUserFenPai.State;
			mTempFlowState = mUserFenPai.FlowState;
			Log.d(TAG, "" + mTempFlowState);
			if (mTempState == TaskConstant.TaskState.UNSTART
					&& mTempFlowState == TaskConstant.FlowState.NORMAL) {
				// 如果任务状态为未开始,权限状态为普通 才有执行中
				dialog.addSheetItem(TaskConstant.TaskStateString.PROCESSING,
						null, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								new AlertDialog(getActivity())
										.builder()
										.setMsg(getString(R.string.task_summarize_operate_tips))
										.setPositiveButton("确定",
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
														mLoadingDialog.show();
														mOperateType = TASK_EXECUTE;
														mUserFenPai.State = TaskConstant.TaskState.PROCESSING;
														taskPresenter
																.modifyTask(mUserFenPai);
													}
												})
										.setNegativeButton("取消",
												new OnClickListener() {

													@Override
													public void onClick(View v) {

													}
												}).show();
							}
						});
			}
			if (mTempState != TaskConstant.TaskState.FINISHED
					&& ChargeLimitFlag
					&& (mTempFlowState == TaskConstant.FlowState.NORMAL || mTempFlowState == TaskConstant.FlowState.RETURNWOEK)) {
				// 如果任务状态为进行中或者未开始 并且是负责人 权限状态为 普通或者返工
				dialog.addSheetItem(
						getString(R.string.task_summarize_operate_sprints),
						null, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent sprintIntent = new Intent(getActivity(),
										SprintActivity.class);
								sprintIntent.putExtra(
										SprintActivity.SPRINT_TASKID,
										mUserFenPai.ID);
								startActivity(sprintIntent);
							}
						});
			}

			if (ChargeLimitFlag
					&& (mTempState == TaskConstant.TaskState.FINISHED || mTempState == TaskConstant.TaskState.PROCESSING)
					&& (mTempFlowState == TaskConstant.FlowState.CHECK || mTempFlowState == TaskConstant.FlowState.FINISHED)) {
				// 如果任务状态为已完成或者进行中，权限状态为提交审核或者已完成的， 负责人 才有返工权限。
				dialog.addSheetItem(
						getString(R.string.task_summarize_operate_return_work),
						SheetItemColor.Red, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								new AlertDialog(getActivity())
										.builder()
										.setMsg(getString(R.string.task_summarize_operate_tips))
										.setPositiveButton("确定",
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
														mLoadingDialog.show();
														mOperateType = TASK_RETURN_WORK;
														mUserFenPai.State = TaskConstant.TaskState.PROCESSING;
														mUserFenPai.FlowState = TaskConstant.FlowState.RETURNWOEK;
														taskPresenter
																.modifyTask(mUserFenPai);
													}
												})
										.setNegativeButton("取消",
												new OnClickListener() {

													@Override
													public void onClick(View v) {

													}
												}).show();

							}
						});
			}
			if (ExecutorLimitFlag
					&& mTempState == TaskConstant.TaskState.PROCESSING
					&& (mTempFlowState == TaskConstant.FlowState.NORMAL || mTempFlowState == TaskConstant.FlowState.RETURNWOEK)) {
				// 如果是执行人 任务状态为进行中,权限状态为普通或者返工。
				dialog.addSheetItem(
						getString(R.string.task_summarize_operate_check), null,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

								new AlertDialog(getActivity())
										.builder()
										.setMsg(getString(R.string.task_summarize_operate_tips))
										.setPositiveButton("确定",
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
														mLoadingDialog.show();
														mOperateType = TASK_CHECK;
														mUserFenPai.FlowState = TaskConstant.FlowState.CHECK;
														taskPresenter
																.modifyTask(mUserFenPai);
													}
												})
										.setNegativeButton("取消",
												new OnClickListener() {

													@Override
													public void onClick(View v) {

													}
												}).show();
							}
						});
			}
			if (ChargeLimitFlag
					&& mTempFlowState == TaskConstant.FlowState.CHECK
					&& mTempState == TaskConstant.TaskState.PROCESSING) {
				// 如果是负责人 任务状态为进行中,权限状态为提交审核 具有已完成 并且将任务状态更改为完成
				dialog.addSheetItem(
						getString(R.string.task_summarize_operate_finish),
						null, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

								new AlertDialog(getActivity())
										.builder()
										.setMsg(getString(R.string.task_summarize_operate_tips))
										.setPositiveButton("确定",
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
														mLoadingDialog.show();
														mOperateType = TASK_FINISHED;// 更改为完成中
														mUserFenPai.State = TaskConstant.TaskState.FINISHED;
														mUserFenPai.FlowState = TaskConstant.FlowState.FINISHED;
														taskPresenter
																.modifyTask(mUserFenPai);
													}
												})
										.setNegativeButton("取消",
												new OnClickListener() {

													@Override
													public void onClick(View v) {

													}
												}).show();
							}
						});
			}

			dialog.show();
			break;

		default:
			break;
		}
	}

	class SummarizeBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TaskModifyActivity.ACTION_MODIFY_TASK
					.equals(intent.getAction())) {
				// 接收广播
				mUserFenPai = (UserFenPai) intent
						.getSerializableExtra(TaskModifyActivity.SEND_USERFENPAI);
				initView();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mReceive);
	}

	@Override
	public void modifyTask(String s) {
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		if ("1".equals(s)) {
			switch (mOperateType) {
			case TASK_EXECUTE:
				ToastUtil.showToast(getActivity(),
						getString(R.string.task_state_process_success),
						R.drawable.tishi_ico_gougou);
				break;
			case TASK_RETURN_WORK:
				ToastUtil.showToast(getActivity(),
						getString(R.string.task_state_return_work_success),
						R.drawable.tishi_ico_gougou);
				break;
			case TASK_CHECK:
				ToastUtil.showToast(getActivity(),
						getString(R.string.task_state_check_success),
						R.drawable.tishi_ico_gougou);
				break;
			case TASK_FINISHED:
				ToastUtil.showToast(getActivity(),
						getString(R.string.task_state_finish_success),
						R.drawable.tishi_ico_gougou);
				break;
			default:
				break;
			}
			Intent intent = new Intent();
			intent.setAction(TaskModifyActivity.ACTION_MODIFY_TASK);
			intent.putExtra(TaskModifyActivity.SEND_USERFENPAI, mUserFenPai);
			getActivity().sendBroadcast(intent);
		} else {
			switch (mOperateType) {
			case TASK_EXECUTE:
				// 修改失败后，改成先前的状态
				mUserFenPai.State = mTempState;
				ToastUtil.showToast(getActivity(),
						getString(R.string.task_state_process_error));
				break;
			case TASK_RETURN_WORK:
				mUserFenPai.State = mTempState;
				mUserFenPai.FlowState = mTempFlowState;
				ToastUtil.showToast(getActivity(),
						getString(R.string.task_state_return_work_error));
				break;
			case TASK_CHECK:
				mUserFenPai.FlowState = mTempFlowState;
				ToastUtil.showToast(getActivity(),
						getString(R.string.task_state_check_error));
				break;
			case TASK_FINISHED:
				mUserFenPai.State = mTempState;
				mUserFenPai.FlowState = mTempFlowState;
				ToastUtil.showToast(getActivity(),
						getString(R.string.task_state_finish_error));
				break;

			default:
				break;
			}
		}
		mOperateType = -1;// 置空操作状态
	}

	@Override
	public void onError(Throwable ex, boolean isOnCallback) {
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		switch (mOperateType) {
		case TASK_EXECUTE:
			mUserFenPai.State = mTempState;
			ToastUtil.showToast(getActivity(),
					getString(R.string.task_state_process_error));
			break;
		case TASK_RETURN_WORK:
			mUserFenPai.State = mTempState;
			mUserFenPai.FlowState = mTempFlowState;
			ToastUtil.showToast(getActivity(),
					getString(R.string.task_state_return_work_error));
			break;
		case TASK_CHECK:
			mUserFenPai.FlowState = mTempFlowState;
			ToastUtil.showToast(getActivity(),
					getString(R.string.task_state_check_error));
			break;
		case TASK_FINISHED:
			mUserFenPai.State = mTempState;
			mUserFenPai.FlowState = mTempFlowState;
			ToastUtil.showToast(getActivity(),
					getString(R.string.task_state_finish_error));
			break;
		default:
			break;
		}
		mOperateType = -1;// 置空操作状态
	}

	@Override
	public void createTask(String s) {
		// TODO Auto-generated method stub

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
