package cc.emw.mobile.project.bean;

import java.io.Serializable;
import java.util.ArrayList;

import cc.emw.mobile.net.ApiEntity;

/**
 * Created by jven.wu on 2016/8/25.
 */
public class StateTask implements Serializable{
    public String TaskState;
    public ArrayList<ApiEntity.UserFenPai> TaskViewBean = new ArrayList<>();
}
