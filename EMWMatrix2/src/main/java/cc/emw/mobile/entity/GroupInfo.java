package cc.emw.mobile.entity;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.ChatterGroup;

public class GroupInfo extends ChatterGroup {

    public boolean tag;

    public GroupInfo(int count, String createTime, int createUser,
                     String createUserName, int iD, String image, boolean isAddIn,
                     String memo, String name, int type, List<ApiEntity.UserInfo> userids, boolean tag) {
        super();
        Count = count;
        CreateTime = createTime;
        CreateUser = createUser;
        CreateUserName = createUserName;
        ID = iD;
        Image = image;
        IsAddIn = isAddIn;
        Memo = memo;
        Name = name;
        Type = type;
        Users = userids;
        this.tag = tag;
    }

    public GroupInfo() {
        super();
    }
}