package cc.emw.mobile.task.model;

import java.util.List;

import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.ApiEntity.UserSprint;

public interface OnTaskListener {
//	void onTaskGetByStateSuccess(List<UserFenPai> taskList);//获取任务数据列表
	void onTaskCommentSuccess(List<TaskReply> replyList);//获取任务评论数据列表
	void onCompleteFresh();//数据解析完毕的回调
	void onSaveReplySuccess(String s);//评论成功
	void onDelReplySuccess(String s );//删除评论成功
	void onProjectGetByStateSuccess(List<UserProject> projectList);
	void onModifyTaskSuccess(String s);//编辑任务成功
	void onCreateTaskSuccess(String s);//创建任务成功
	void onGetAllProjectsSuccess(List<UserProject> projects);//获取所有的项目成功后的回调
	void onGetAllSprintsSuccess(List<UserSprint> sprints);//获取所有的项目冲刺成功后的回调
	void onShareAttachmentSuccess(String s);//文件分享成功的回调
	void onDoSprintTaskSuccess(String s);//任务冲刺添加成功的回调
	void onError(Throwable ex,boolean isOnCallback);//获取数据失败的回调
	void onGetFileListByIdsSuccess(List<Files> files);//附件请求成功的回调
}
