package cc.emw.mobile.project.view;

import java.util.ArrayList;

import cc.emw.mobile.net.ApiEntity;

/**
 * Created by jven.wu on 2016/7/13.
 */
public interface IObserveProjectActivity extends IBaseProjectView {
    void renderView(ArrayList<ApiEntity.UserFenPai> tasks);
}
