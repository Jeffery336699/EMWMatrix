package cc.emw.mobile.chat.model.bean;

import java.io.Serializable;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.chat.model.bean
 * @data on 2018/10/8  16:07
 * @describe TODO
 */

public class PhoneBookBean implements Serializable {

    public String name;
    public String phone;
    private String sortLetters;

    public PhoneBookBean(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getSortLetters() {
        return sortLetters;
    }
    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
