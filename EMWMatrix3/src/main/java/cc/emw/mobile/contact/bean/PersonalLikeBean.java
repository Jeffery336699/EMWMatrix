package cc.emw.mobile.contact.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class PersonalLikeBean implements MultiItemEntity {

    private int itemType=0;

    public String getTitle() {
        return title1;
    }

    public void setTitle(String title) {
        this.title1 = title;
    }

    private String title1;
    private List<String> mList;

    public PersonalLikeBean(int itemType, List<String> list) {
        this.itemType = itemType;
        mList = list;
    }

    @Override
    public String toString() {
        return "PersonalLikeBean{" +
                "itemType=" + itemType +
                ", title='" + title1 + '\'' +
                ", mList=" + mList +
                '}';
    }

    public PersonalLikeBean(int itemType, String title) {
        this.itemType = itemType;
        title1 = title;
    }

    public List<String> getList() {
        return mList;
    }

    public void setList(List<String> list) {
        mList = list;
    }


    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
