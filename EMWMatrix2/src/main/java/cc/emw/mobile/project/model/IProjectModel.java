package cc.emw.mobile.project.model;

import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.ApiEntity.UserFenPai;

public interface IProjectModel {
	/**
	 * 获得所有任务
	 * @param listener
	 */
	void getProjects(OnProjectListener listener);

    /**
     * 获取项目(分页)
     * @param PageNo 页码
     * @param PageSize 记录数
     * @param listener
     */
    void GetProjectByPage(int PageNo,int PageSize,OnProjectListener listener);

    /**
	 * 通过项目id获得任务
	 * @param id 任务id
	 * @return 任务
	 */
	UserFenPai getTaskByProjectId(String id);
	
	/**
	 * 获得冲刺任务
	 * @param listener
	 */
	void getSprints(OnProjectListener listener);
	
	/**
	 * 获得所有任务
	 * @param listener
	 */
	void getAllTasks(OnProjectListener listener);
	
	/**
	 * 根据任务id字符串获得任务
	 * @param ids 任务id字符串
	 * @param listener
	 */
	void getTasksByIds(String ids,OnProjectListener listener);
	
	/**
	 * 获得所有日程
	 * @param listener
	 */
	void getAllSchedules(OnProjectListener listener);
	
	/**
	 * 添加/修改项目
	 * 传入0表示添加，传入项目id表示修改
	 * @param project 项目
	 * @param listener
	 */
	void modifyProject(UserProject project,OnProjectListener listener);
}
