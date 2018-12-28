package cc.emw.mobile.task.view;

import java.util.List;

import cc.emw.mobile.net.ApiEntity.UserProject;

/**
 * Task视图层接口
 * @author chengyong.liu
 *
 */
public interface ITaskView extends ITaskBaseView {
	void showTask(List<UserProject> projectList);//任务项目列表的获取
}
