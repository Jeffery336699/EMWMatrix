package cc.emw.mobile.task.view;

import java.util.List;

import cc.emw.mobile.net.ApiEntity.UserSprint;


public interface ItaskSprintsView extends ITaskBaseView{
	/**
	 * 展示所有的项目冲刺
	 * @param sprints 
	 */
	void showSprints(List<UserSprint> sprints);
	/**
	 * 添加任务冲刺
	 */
	void doSprintTask(String s);
}
