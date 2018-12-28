package cc.emw.mobile.task.view;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;
/**
 * Task视图层接口
 * @author chengyong.liu
 *
 */
public interface ITaskView extends ITaskBaseView {
	void showTask(List<ApiEntity.UserFenPai> taskList);//任务列表展示
}
