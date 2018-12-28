package cc.emw.mobile.project.bean;

import cc.emw.mobile.net.ApiEntity;

/**
 * Created by jven.wu on 2016/6/29.
 */
public class GroupViewBean {
    public int GroupId;
    public String GroupName;
    public String GroupImg;
    public ApiEntity.ChatterGroup group;

    private String sortLetters; //首字母

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

}
