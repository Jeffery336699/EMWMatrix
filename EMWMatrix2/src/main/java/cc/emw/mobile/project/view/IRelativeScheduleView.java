package cc.emw.mobile.project.view;

import cc.emw.mobile.project.adapter.RelativeScheduleAdapter;

public interface IRelativeScheduleView extends IProjectBaseView{
	/**
	 * 设置相关日程列表显示
	 * @param adapter 相关日程适配器
	 */
	void setListView(RelativeScheduleAdapter adapter);
	/**
	 * 完成刷新
	 */
	void refreshComplete(String errMsg);
}
