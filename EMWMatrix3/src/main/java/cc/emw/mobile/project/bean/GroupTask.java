package cc.emw.mobile.project.bean;

import java.io.Serializable;
import java.util.ArrayList;

import cc.emw.mobile.net.ApiEntity;

/**
 * Created by jven.wu on 2016/11/22.
 */
public class GroupTask implements Serializable {
    public int ID;
    public String Name;
    public ArrayList<ApiEntity.UserFenPai> Tasks;
}
