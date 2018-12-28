package cc.emw.mobile.project.view;

import java.util.ArrayList;

import cc.emw.mobile.project.bean.MemberProject;

/**
 * Created by jven.wu on 2016/6/25.
 */
public interface IProjectMemberView extends IBaseProjectView {
    void renderView(ArrayList<MemberProject> memberProjects);
}
