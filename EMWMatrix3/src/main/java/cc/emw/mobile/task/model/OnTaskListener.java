package cc.emw.mobile.task.model;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.task.entity.UserLabelBean;

public interface OnTaskListener {
//	void onTaskGetByStateSuccess(List<UserFenPai> taskList);//获取任务数据列表
//	void onTaskCommentSuccess(List<TaskReply> replyList);//获取任务评论数据列表
//	void onCompleteFresh();//数据解析完毕的回调
//	void onSaveReplySuccess(String s);//评论成功
//	void onDelReplySuccess(String s);//删除评论成功
//	void onProjectGetByStateSuccess(List<UserProject> projectList);
//	void onModifyTaskSuccess(String s);//编辑任务成功
//	void onCreateTaskSuccess(String s);//创建任务成功
	void onGetAllProjectsSuccess(List<UserProject> projects);//获取所有的项目成功后的回调
//	void onGetAllSprintsSuccess(List<UserSprint> sprints);//获取所有的项目冲刺成功后的回调
//	void onShareAttachmentSuccess(String s);//文件分享成功的回调
//	void onDoSprintTaskSuccess(String s);//任务冲刺添加成功的回调
//	void onGetFileListByIdsSuccess(List<Files> files);//附件请求成功的回调

	void onError(Throwable ex, boolean isOnCallback);//获取数据失败的回调
	void onCompleteFresh();//刷新完毕的回调
	void onGetTaskByStateSuccess(List<ApiEntity.UserFenPai> taskList);//获取任务数据

	void onTaskCommentSuccess(List<TaskReply> replyList);//获取任务评论数据列表
	void onSaveReplySuccess(ApiEntity.APIResult respInfo);//评论成功
	void onDelReplySuccess(String s );//删除评论成功

	void onModifyTaskSuccess(String s);//编辑任务成功
	void onCreateTaskSuccess(String respInfo);//创建任务成功
    void onShareAttachmentSuccess(String s);//文件分享成功的回调
    void onGetFileListByIdsSuccess(List<Files> files);//附件请求成功的回调
	void onGetTaskOfProjectSuccess(List<ApiEntity.UserFenPai> tasks);//获取项目下所有的任务成功的回调

	void onGetUserLabelSuccess(List<UserLabelBean> labels);
	void onAddUsdrLabelSuccess(String s);
	void onModifyUserLabelSuccess(String s);
	void onDelUserLabelSuccess(String s);
}
