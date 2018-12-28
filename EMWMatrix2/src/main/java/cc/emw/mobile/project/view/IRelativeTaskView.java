package cc.emw.mobile.project.view;

import cc.emw.mobile.project.adapter.RelativeTaskAdapter;

public interface IRelativeTaskView extends IProjectBaseView {
	/**
	 * 设置相关任务显示
	 * @param adapter 相关任务适配器
	 */
	void setListView(RelativeTaskAdapter adapter);
	/**
	 * 完成刷新
	 */
	void refreshComplete();
	/**
	 * 现示错误信息
	 */
	void displayError(String errMsg);
}
