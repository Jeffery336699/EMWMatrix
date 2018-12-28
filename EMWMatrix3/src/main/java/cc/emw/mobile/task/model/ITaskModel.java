package cc.emw.mobile.task.model;


import java.util.List;

import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.task.entity.UserLabelBean;

/**
 * Task的数据接口
 * @author chengyong.liu
 *
 */

public interface ITaskModel {
//	void getTestDatas(int type,OnTaskListener listener);//测试数据
//	void getTaskDatas(int uid,OnTaskListener listener);//根据用户ID类型获取任务
//	void getTaskReply(int taskID, OnTaskListener listener);//根据taskID获取任务评论数据
//	void saveTaskReply(TaskReply reply, OnTaskListener listener);//保存评论信息
//	void deleteTaskReply(int ID, OnTaskListener listener);//根据评论ID删除评论
//	void getProjectsByTaskState(int state, OnTaskListener listener);
	void modifyTask(UserFenPai userFenpai, OnTaskListener listener);//根据任务实例修改任务
	void createTask(UserFenPai userFenpai, OnTaskListener listener);//根据任务实例创建任务
	void getAllProjects(OnTaskListener listener);//获取所有的项目
//	void getAllSprints(OnTaskListener listener);//获取所有的项目冲刺
	void shareAttachment(List<UserFilePower> list, int fileID, OnTaskListener listener);//分享附件
//	void doSprintTask(List<UserSprint> usList, UserSprint us, int taskID, OnTaskListener listener);//提交任务冲刺
	void getFileListByIds(String ids, OnTaskListener listener);//通过ID字符串请求附件实体对象
	void getTaskByState(int type,OnTaskListener listener);//获取任务根据任务状态 0为获取所有的任务
	void getTaskReply(int taskID,OnTaskListener listener);//根据taskID获取任务评论数据
	void saveTaskReply(TaskReply reply,OnTaskListener listener);//保存评论信息
	void deleteTaskReply(int ID,int taskId,OnTaskListener listener);//根据评论ID删除评论

	void getTaskOfProject(String ids,OnTaskListener listener);//获取项目下所有的子任务

	void getUserLabel(int userId,OnTaskListener listener);
	void addUsdrLabel(UserLabelBean ul, OnTaskListener listener);
	void modifyUserLabel(UserLabelBean ul,OnTaskListener listener);
    void delUserLabel(int id,OnTaskListener listener);
}
