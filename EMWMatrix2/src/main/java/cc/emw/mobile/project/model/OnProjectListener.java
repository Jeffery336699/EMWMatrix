package cc.emw.mobile.project.model;

import java.util.List;

import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.entities.UserSprint;
import cc.emw.mobile.net.ApiEntity.UserFenPai;

public interface OnProjectListener {
	/**
	 * 获取项目成功
	 * @param projects 项目集合
	 * @param sprints 冲刺集合
	 */
	void onGetProjectsSuccess(List<UserProject> projects,List<UserSprint> sprints);
	/**
	 * 获取项目失败
	 */
	void onGetProjectsError();
	/**
	 * 获取冲刺成功
	 * @param sprints 冲刺集合
	 */
	void onGetSprintsSuccess(List<UserSprint> sprints);
	/**
	 * 获取冲刺失败
	 */
	void onGetSprintsError();
	/**
	 * 获取所有任务成功
	 * @param tasks 任务
	 */
	void onGetAllTasksSuccess(List<UserFenPai> tasks);
	/**
	 * 获取所有任务失败
	 */
	void onGetAllTasksError(String errMsg);
	/**
	 * 修改或添加项目成功
	 * @param ret 成功时返回项目
	 */
	void onModifyProjectSuccess(String ret);
	/**
	 * 获取所有日程成功
	 * @param shcedules 日程
	 */
	void onGetAllSchedulesSuccess(List<CalendarInfo> shcedules);
	/**
	 * 获取所有日程失败
	 */
	void onGetAllSchedulesError(String errMsg);
	/**
	 * 根据任务id获取任务成功
	 * @param tasks
	 */
	void onGetTasksByIdSuccess(List<UserFenPai> tasks);
	/**
	 * 根据任务id获取任务失败
	 */
	void onGetTasksByIdError();
	/**
	 * 网络错误或元网络
	 */
	void onNetworkError();
}
