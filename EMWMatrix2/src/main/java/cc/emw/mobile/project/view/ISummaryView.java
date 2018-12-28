package cc.emw.mobile.project.view;


public interface ISummaryView extends IProjectBaseView {
	/**
	 * 完成刷新
	 */
	void refreshComplete();

    /**
     * 获取项目失败
     */
    void onGetProjectsError();
}
