package cc.emw.mobile.project.bean;

import java.io.Serializable;

import cc.emw.mobile.net.ApiEntity;

/**
 * Created by jven.wu on 2016/6/25.
 */
public class ProjectViewBean implements Serializable {
    public int ProjectId;
    public String ProjectName;
    public int ProjectColor;
    public String Date;
    public int TaskNum;
    public ApiEntity.UserProject Project;
    public boolean isBelongProject;
}
