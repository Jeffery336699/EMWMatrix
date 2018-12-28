package cc.emw.mobile.project.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jven.wu on 2016/7/6.
 */
public class MemberProject implements Serializable {
    public int UserId;
    public int ProjectNum;
    public int TaskNum;
    public ArrayList<GroupProject> groupProjects = new ArrayList<>();
}
