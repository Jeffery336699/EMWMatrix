package cc.emw.mobile.task.presenter;

import java.util.List;

import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.ApiEntity.UserSprint;
import cc.emw.mobile.task.model.OnTaskListener;
import cc.emw.mobile.task.model.TaskModel;
import cc.emw.mobile.task.view.ITaskBaseView;
import cc.emw.mobile.task.view.ITaskCommentView;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.task.view.ITaskView;
import cc.emw.mobile.task.view.IWorkProjectView;
import cc.emw.mobile.task.view.ItaskShareAttachmentView;
import cc.emw.mobile.task.view.ItaskSprintsView;

/**
 * 连接Model与View 1、获取数据 2、调用view的方法 展示数据
 * 
 * @author chengyong.liu
 * 
 */
public class TaskPresenter implements OnTaskListener {

	private ITaskBaseView mITaskBaseView;
	private TaskModel mITaskModel;

	public TaskPresenter(ITaskBaseView view) {
		mITaskBaseView = view;
		mITaskModel = new TaskModel();
	}

	/**
	 * 根据任务状态获取项目列表
	 * 
	 * @param state
	 *            任务状态 1未开始 2进行中 3已完成
	 */
	public void getProjectByTaskState(int state) {
		mITaskModel.getProjectsByTaskState(state, this);
	}

	@Override
	public void onProjectGetByStateSuccess(List<UserProject> projectList) {
		// 获取任务项目成功
		((ITaskView) mITaskBaseView).showTask(projectList);
	}

	/**
	 * 获取任务评论列表
	 * 
	 * @param taskID
	 *            任务ID
	 */
	public void getTaskReply(int taskID) {
		// 获取任务评论
		mITaskModel.getTaskReply(taskID, this);
	}

	@Override
	public void onTaskCommentSuccess(List<TaskReply> replyList) {
		// 获取任务评论成功的回调
		((ITaskCommentView) mITaskBaseView).showReply(replyList);
	}

	/**
	 * 添加评论
	 * 
	 * @param reply
	 */
	public void saveTaskReply(TaskReply reply) {
		mITaskModel.saveTaskReply(reply, this);
	}

	@Override
	public void onSaveReplySuccess(String s) {
		// 保存评论成功
		((ITaskCommentView) mITaskBaseView).saveReply(s);
	}

	/**
	 * 删除评论
	 * 
	 * @param ID
	 */
	public void deleteTaskReply(int ID) {
		// 删除评论
		mITaskModel.deleteTaskReply(ID, this);
	}

	@Override
	public void onDelReplySuccess(String s) {
		((ITaskCommentView) mITaskBaseView).delReply(s);
	}

	/**
	 * 数据解析完毕的回调
	 */
	@Override
	public void onCompleteFresh() {
		mITaskBaseView.showFinish();
	}

	/**
	 * 修改任务
	 * 
	 * @param userFenPai
	 *            任务实体
	 */
	public void modifyTask(UserFenPai userFenPai) {
		mITaskModel.modifyTask(userFenPai, this);
	}

	@Override
	public void onModifyTaskSuccess(String s) {
		((ITaskModifyView) mITaskBaseView).modifyTask(s);
	}

	/**
	 * 创建任务
	 * 
	 * @param userFenPai
	 *            任务实体
	 */
	public void createTask(UserFenPai userFenPai) {
		mITaskModel.createTask(userFenPai, this);
	}

	@Override
	public void onCreateTaskSuccess(String s) {
		((ITaskModifyView) mITaskBaseView).createTask(s);
	}

	/**
	 * 获取所有的项目
	 */
	public void getAllProjects() {
		mITaskModel.getAllProjects(this);
	}

	@Override
	public void onGetAllProjectsSuccess(List<UserProject> projects) {
		// 获取所有的项目实例对象
		((IWorkProjectView) mITaskBaseView).showProjects(projects);
	}

	/**
	 * 附件分享
	 * 
	 * @param list
	 *            分享给用户的需求
	 * @param fileID
	 *            分享的附件ID
	 */
	public void shareAttachment(List<UserFilePower> list, int fileID) {
		mITaskModel.shareAttachment(list, fileID, this);
	}

	@Override
	public void onShareAttachmentSuccess(String s) {
		((ItaskShareAttachmentView) mITaskBaseView).shareAttachmentSuccess(s);

	}

	/**
	 * 添加冲刺任务
	 * 
	 * @param usList
	 * @param taskID
	 */
	public void doSprintTask(List<UserSprint> usList, UserSprint us, int taskID) {
		mITaskModel.doSprintTask(usList, us, taskID, this);
	}

	@Override
	public void onDoSprintTaskSuccess(String s) {
		((ItaskSprintsView) mITaskBaseView).doSprintTask(s);

	}

	/**
	 * 获取所有的项目冲刺
	 */
	public void getAllSprints() {
		mITaskModel.getAllSprints(this);
	}

	@Override
	public void onGetAllSprintsSuccess(List<UserSprint> sprints) {
		((ItaskSprintsView) mITaskBaseView).showSprints(sprints);
	}

	@Override
	public void onError(Throwable ex, boolean isOnCallback) {
		mITaskBaseView.onError(ex, isOnCallback);
	}

	/**
	 * 通过Ids字符串获取所有的附件
	 * 
	 * @param ids
	 */
	public void getFileListByIds(String ids) {
		mITaskModel.getFileListByIds(ids, this);
	}

	@Override
	public void onGetFileListByIdsSuccess(List<Files> files) {
		((ITaskModifyView) mITaskBaseView).getFileList(files);

	}

}
