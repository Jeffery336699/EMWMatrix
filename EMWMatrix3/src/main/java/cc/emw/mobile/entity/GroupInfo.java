package cc.emw.mobile.entity;

import cc.emw.mobile.net.ApiEntity.ChatterGroup;

public class GroupInfo extends ChatterGroup {

    public boolean tag;

    private String sortLetters; //名称的首字母

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public GroupInfo() {
        super();
    }
}