package cc.emw.mobile.project.view;

import cc.emw.mobile.project.adapter.ProjectAdapter;

public interface IProjectBaseView {
	/**
	 * 加载项目列表
	 * @param adapter 项目适配器
	 */
	void loadProjectList(ProjectAdapter adapter);
	
	/**
	 * 网络错误时，或无网络时处理
	 */
	void onNetworkError();
}
