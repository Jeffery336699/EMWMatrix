package cc.emw.mobile.task.util;

import java.util.ArrayList;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.util.TaskUtils;

/**
 * Created by sunny.du on 2016/9/6.
 */
public class FiterUtil {
    /**
     * 获取关联项目的相关人员集合
     *
     * @return
     */
    public static ArrayList<ApiEntity.UserInfo> getFiterTeamList(ApiEntity.UserProject userProject) {
        ArrayList<ApiEntity.UserInfo> users = new ArrayList<>();
        if (userProject.ID != 0) {
            users.addAll(TaskUtils.getUsers(userProject.MainUser));
            users.addAll(TaskUtils.getUsers(userProject.Users));
        }
        return users;
    }
}
