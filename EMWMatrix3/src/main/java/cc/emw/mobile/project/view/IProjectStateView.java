package cc.emw.mobile.project.view;

import java.util.ArrayList;

import cc.emw.mobile.project.bean.StateTask;

/**
 * Created by jven.wu on 2016/6/25.
 */
public interface IProjectStateView extends IBaseProjectView {
    void renderProjectStateView(ArrayList<StateTask> stateTasks);
}
