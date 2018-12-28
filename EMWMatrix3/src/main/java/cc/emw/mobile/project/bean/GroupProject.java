package cc.emw.mobile.project.bean;

import java.io.Serializable;
import java.util.ArrayList;

import cc.emw.mobile.net.ApiEntity;

/**
 * Created by jven.wu on 2016/6/25.
 */
public class GroupProject implements Serializable {
    public int GroupId;
    public int GroupCreator;
    public String GroupName;
    public String GroupImg;
    public ArrayList<Integer> UsersId = new ArrayList<>();
    public ArrayList<ProjectViewBean> projectViews = new ArrayList<>();
    public ApiEntity.ChatterGroup group;
    public ApiEntity.UserProject Project;

}
