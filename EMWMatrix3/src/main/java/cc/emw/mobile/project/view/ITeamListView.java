package cc.emw.mobile.project.view;

import java.util.ArrayList;

import cc.emw.mobile.project.bean.GroupViewBean;

/**
 * Created by jven.wu on 2016/6/29.
 */
public interface ITeamListView extends IBaseProjectView {
    void renderView(ArrayList<GroupViewBean> groups);
}
