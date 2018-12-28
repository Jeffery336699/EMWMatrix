package cc.emw.mobile.task.view;


import java.util.List;


import cc.emw.mobile.task.entity.UserLabelBean;

/**
 * Created by chengyong.liu on 2016/7/1.
 */
public interface ITaskLableView extends ITaskBaseView {
    void getUserLable(List<UserLabelBean> labels);

    void addUserLable(String s);

    void modifyUserLable(String s);

    void delUserLable(String s);
}
