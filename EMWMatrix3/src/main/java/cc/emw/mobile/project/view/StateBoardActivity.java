package cc.emw.mobile.project.view;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.DragItem;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Arrays;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.adapter.StateBoardAdapter;
import cc.emw.mobile.project.bean.GroupProject;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.task.activity.TaskCreateActivity;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 *	某团队协作中的任务
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_state_board)
public class StateBoardActivity extends BaseActivity implements IObserveProjectActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private IconTextView mHeaderMenuBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;    //顶部标题
	@ViewInject(R.id.cm_header_btn_right)
	private IconTextView mHeaderMemberProjectBtn; //

	@ViewInject(R.id.board_view)
	private BoardView mBoardView;
	@ViewInject(R.id.indicator_view)
	private PageIndicatorView mIndicatorView;
	@ViewInject(R.id.delete_view)
	private IconTextView mDeleteView;
	@ViewInject(R.id.add_view)
	private IconTextView mAddView;

	private Dialog mLoadingDialog; //加载框
    private ApiEntity.UserProject userProject; //传值
	private GroupProject groupProject; //传值
	private boolean disableMove; //true:不能拖拽改变状态
	private boolean canNotMove = true; //true:能拖拽到当前状态里
	private MyBroadcastReceive receive;
	private ProjectPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
		userProject = (ApiEntity.UserProject) getIntent().getSerializableExtra(TaskCreateActivity.TEAM_USERPROJECT);
		groupProject = (GroupProject) getIntent().getSerializableExtra("group_project");
        initView();

		presenter = new ProjectPresenter(this);
		if(!"".equals(TaskSpectacularActivity.currentProjectId)) {
			presenter.getTasksOfProject(TaskSpectacularActivity.currentProjectId + "");
		}else{
			presenter.getTasksOfProject(String.valueOf(0));
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NewProjectActivity.BROADCAST_PROJECT_REFRESH);
		intentFilter.addAction(TaskCreateActivity.ACTION_TASK_CREATE_SUCCESS);
		receive = new MyBroadcastReceive();
		registerReceiver(receive, intentFilter);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receive != null)
			unregisterReceiver(receive);
	}
    
    private void initView() {
		mHeaderMenuBtn.setIconText("eb68");
		mHeaderMenuBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(userProject != null ? userProject.Name : "任务看板");
		mHeaderMemberProjectBtn.setIconText("ecdc");
		mHeaderMemberProjectBtn.setVisibility(View.VISIBLE);

		mBoardView.setColumnWidth(getResources().getDisplayMetrics().widthPixels);
		mBoardView.setDeleteView(mDeleteView);
		mBoardView.setPageIndicatorView(mIndicatorView);
		mBoardView.setCustomDragItem(new DragItem(this, R.layout.listitem_state_board_child2));
		mBoardView.setSnapToColumnsWhenScrolling(true); //true:向左/右滑动切换下/上一页
		mBoardView.setSnapToColumnWhenDragging(true); //true:拖到左/右边缘会切换上/下一页
		mBoardView.setSnapDragItemToTouch(true); //true:dragview中心点会跳到手指长按的位置
		mBoardView.setBoardListener(new BoardView.BoardListener() {
			@Override
			public void onItemDragStarted(int column, int row, View dragView) {
				mAddView.setVisibility(View.GONE);
				dragView.getBackground().setAlpha(255);
				dragView.findViewById(R.id.sbc1_disablemove).setVisibility(View.GONE);
				ApiEntity.UserFenPai task = (ApiEntity.UserFenPai) mBoardView.getDragObject();
				// 任务的创建者或者负责人才可以拖拽改变任务状态或删除
				if (task.Creator != PrefsUtil.readUserInfo().ID &&
						task.MainUser != null && !Arrays.asList(task.MainUser.split(",")).contains(Integer.toString(PrefsUtil.readUserInfo().ID))) {
					dragView.getBackground().setAlpha(100);
					dragView.findViewById(R.id.sbc1_disablemove).setVisibility(View.VISIBLE);
					mDeleteView.setVisibility(View.GONE);
					disableMove = true;
					return;
				}
			}

			@Override
			public void onItemChangedColumn(int oldColumn, int newColumn, View dragView) {
				if (disableMove) {
					return;
				}

				ApiEntity.UserFenPai task = (ApiEntity.UserFenPai) mBoardView.getDragObject();
				if (task.State == TaskConstant.TaskState.DELAY) { //拖拽中任务为已延迟
					boolean isProcessing = getStateByColumn(newColumn) == TaskConstant.TaskState.PROCESSING; //是否进行中
					dragView.getBackground().setAlpha(isProcessing ? 100 : 255);
					dragView.findViewById(R.id.sbc1_disablemove).setVisibility(isProcessing ? View.VISIBLE : View.GONE);
					canNotMove = !isProcessing;
				} else {
					boolean isDelay = getStateByColumn(newColumn) == TaskConstant.TaskState.DELAY; //是否已延迟
					dragView.getBackground().setAlpha(isDelay ? 100 : 255);
					dragView.findViewById(R.id.sbc1_disablemove).setVisibility(isDelay ? View.VISIBLE : View.GONE);
					canNotMove = !isDelay;
				}
			}

			@Override
			public void onItemDragEnded(int fromColumn, int fromRow, final int toColumn, final int toRow) {
				mAddView.setVisibility(View.VISIBLE);
				if (fromColumn != toColumn || fromRow != toRow) {
					if (disableMove || !canNotMove) {
						mBoardView.cancelMove(toRow);
						disableMove = false;
						canNotMove = true;
					} else {
						final int state = getStateByColumn(toColumn);
						if (state == TaskConstant.TaskState.FINISHED) {
							new AlertDialog(StateBoardActivity.this).builder().setMsg("是否确认任务已经完成！如有子任务将一起更改为已完成！")
									.setPositiveButton(getString(R.string.confirm), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											updateTaskState(state, ((ApiEntity.UserFenPai)mBoardView.getDragObject()).ID, toColumn, toRow);
										}
									}).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									mBoardView.cancelMove(toRow);
									updateTopStateCount();
								}
							}).show();
						} else if (state != ((ApiEntity.UserFenPai)mBoardView.getDragObject()).State){
							updateTaskState(state, ((ApiEntity.UserFenPai)mBoardView.getDragObject()).ID, toColumn, toRow);
						}
					}
				}
			}

			@Override
			public void onItemDragging(boolean isDelete, int currentColumn, View dragView) {
				if (isDelete) {
					mDeleteView.setBackgroundResource(R.drawable.dialog_ok_bg_red);
					dragView.getBackground().setAlpha(255);
					dragView.findViewById(R.id.sbc1_disablemove).setVisibility(View.GONE);
				} else {
					mDeleteView.setBackgroundResource(R.drawable.dialog_ok_bg_red2);
					onItemChangedColumn(0, currentColumn, dragView);
				}
			}

			@Override
			public void onItemDragDelete(final int row) {
				mAddView.setVisibility(View.VISIBLE);
				new AlertDialog(StateBoardActivity.this).builder().setMsg("确认删除该任务？")
						.setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
						.setPositiveButton(getString(R.string.confirm), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								delTask(((ApiEntity.UserFenPai)mBoardView.getDragObject()).ID, row);
							}
						}).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mBoardView.cancelMove(row);
							}
				}).show();
			}
		});
    }

	@Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right, R.id.add_view})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left:
				onBackPressed();
				break;
			case R.id.cm_header_btn_right:
				Intent intent = new Intent(this, ObserveProjectActivity.class);
				intent.putIntegerArrayListExtra(ObserveProjectActivity.EXTRA_GROUP_MEMBERS, groupProject.UsersId);
				intent.putExtra(ObserveProjectActivity.EXTRA_GROUP_NAME, groupProject.GroupName);
				intent.putExtra(ObserveProjectActivity.EXTRA_PROJECT, userProject);
				intent.putExtra("start_anim", false);
				startActivity(intent);
				break;
			case R.id.add_view:
				Intent taskIntent = new Intent(this, TaskCreateActivity.class);
				taskIntent.putExtra(TaskCreateActivity.TEAM_USERPROJECT, userProject);
				taskIntent.putExtra("start_anim", false);
				startActivity(taskIntent);
				break;
    	}
    }

	@Override
	public void renderView(ArrayList<ApiEntity.UserFenPai> tasks) {
		ArrayList<ApiEntity.UserFenPai> unstartList = new ArrayList<>();
		ArrayList<ApiEntity.UserFenPai> processingList = new ArrayList<>();
		ArrayList<ApiEntity.UserFenPai> finishedList = new ArrayList<>();
		ArrayList<ApiEntity.UserFenPai> delayList = new ArrayList<>();
		if (tasks != null && tasks.size() >= 0) {
			for(ApiEntity.UserFenPai task : tasks){
				switch (task.State){
					case TaskConstant.TaskState.UNSTART:
						unstartList.add(task);
						break;
					case TaskConstant.TaskState.PROCESSING:
						processingList.add(task);
						break;
					case TaskConstant.TaskState.FINISHED:
						finishedList.add(task);
						break;
					case TaskConstant.TaskState.DELAY:
						delayList.add(task);
						break;
				}
			}
		}
		mBoardView.clearBoard();
		addColumnList(unstartList, "未开始");
		addColumnList(processingList, "进行中");
		addColumnList(finishedList, "已完成");
		addColumnList(delayList, "已延迟");


	}

	@Override
	public void onError(Throwable ex) {

	}

	private void addColumnList(ArrayList<ApiEntity.UserFenPai> tasks, String title) {
		final StateBoardAdapter listAdapter = new StateBoardAdapter(tasks, R.layout.listitem_state_board_child1, R.id.root_layout, true);
		final View header = View.inflate(this, R.layout.list_top_content2, null);
		((TextView) header.findViewById(R.id.task_state_title)).setText(title + " • ");
		((TextView) header.findViewById(R.id.task_state_num)).setText(Integer.toString(tasks.size()));
		header.findViewById(R.id.task_state_title).setVisibility(View.VISIBLE);
		header.findViewById(R.id.task_state_num).setVisibility(View.VISIBLE);

		mBoardView.addColumnList(listAdapter, header, false, "已完成".equals(title) ? false : true);
//		mBoardView.addColumnList(listAdapter, header, false, true);
	}

	private int getStateByColumn(int column) {
		int state = TaskConstant.TaskState.UNSTART;
		switch (column){
			case 0:
				state = TaskConstant.TaskState.UNSTART;
				break;
			case 1:
				state = TaskConstant.TaskState.PROCESSING;
				break;
			case 2:
				state = TaskConstant.TaskState.FINISHED;
				break;
			case 3:
				state = TaskConstant.TaskState.DELAY;
				break;
		}
		return state;
	}
	//刷新顶部条数量
	private void updateTopStateCount() {
		for (int i = 0; i < mBoardView.getColumnCount(); i++) {
			TextView itemCount = (TextView) mBoardView.getHeaderView(i).findViewById(R.id.task_state_num);
			itemCount.setText(Integer.toString(mBoardView.getAdapter(i).getItemCount()));
		}
	}

	/**
	 * 根据任务ID删除任务
	 */
	private void delTask(int taskID, final int row) {
		API.TalkerAPI.DelTaskById(taskID, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtil.showToast(StateBoardActivity.this, "删除任务失败！");
				mBoardView.cancelMove(row);
			}
			@Override
			public void onSuccess(String result) {
				Log.d("const", "result:"+result);
				if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					mBoardView.deleteItem(row);
					updateTopStateCount();
				} else {
					ToastUtil.showToast(StateBoardActivity.this, "删除任务失败！");
					mBoardView.cancelMove(row);
				}
			}
		});
	}

	private void updateTaskState(final int state, int taskID, final int toColumn, final int toRow) {
		API.TalkerAPI.UpdateTaskState(state, taskID, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtil.showToast(StateBoardActivity.this, "更改任务状态失败！");
				mBoardView.cancelMove(toRow);
				updateTopStateCount();
			}
			@Override
			public void onSuccess(String result) {
				Log.d("const", "result:"+result);
				if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					//任务拖拽到某状态中，需把任务状态更改为对应的状态
					final ApiEntity.UserFenPai task = (ApiEntity.UserFenPai) mBoardView.getAdapter(toColumn).removeItem(toRow);
					task.State = state;
					mBoardView.getAdapter(toColumn).addItem(toRow, task);
					updateTopStateCount();
				} else {
					ToastUtil.showToast(StateBoardActivity.this, "更改任务状态失败！");
					mBoardView.cancelMove(toRow);
					updateTopStateCount();
				}
			}
		});
	}

	class MyBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (NewProjectActivity.BROADCAST_PROJECT_REFRESH.equals(intent.getAction())) {
				ApiEntity.UserProject tempProject = (ApiEntity.UserProject)intent.getSerializableExtra("project");
				if(tempProject != null){
					userProject = tempProject;
					mHeaderTitleTv.setText(userProject.Name);
				}
			} else if (TaskCreateActivity.ACTION_TASK_CREATE_SUCCESS.equals(intent.getAction())) {
				Log.d("state_board", "ACTION_TASK_CREATE_SUCCESS");
				if(!"".equals(TaskSpectacularActivity.currentProjectId)) {
					presenter.getTasksOfProject(TaskSpectacularActivity.currentProjectId + "");
				}else{
					presenter.getTasksOfProject(String.valueOf(0));
				}
			}
		}
	}
}
