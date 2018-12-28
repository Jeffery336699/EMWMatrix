package cc.emw.mobile.me.entity;

import java.io.Serializable;

/**
 * Created by tao.zhou on 2017/6/28.
 */

public class JobExperience implements Serializable {
    public String userCompany;
    public String userJob;
    public String userCounty;
    public String userIntro;
    public String jobStartTime;
    public String jobEndtTime;
    public int jobsPrivacy;  //查看权限
}
