package cc.emw.mobile.task.entity;


import cc.emw.mobile.net.ApiEntity;

/**
 * Created by chengyong.liu on 2016/7/26.
 */
public class UserLabelBean extends ApiEntity.UserLabel {
    private String sortLetters; //首字母

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
