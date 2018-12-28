package cc.emw.mobile.project.view;

import java.util.ArrayList;

import cc.emw.mobile.project.bean.GroupProject;

/**
 * Created by jven.wu on 2016/6/25.
 */
public interface IProjectListView extends IBaseProjectView {
    void renderProjectListView(ArrayList<GroupProject> groupProjects);
}
