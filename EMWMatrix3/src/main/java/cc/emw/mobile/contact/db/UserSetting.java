package cc.emw.mobile.contact.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by tao.zhou on 2017/5/11.
 */
@Entity
public class UserSetting {
    /**
     * 数据库主键  自增长 必须是long类型
     */
    @Id(autoincrement = true)
    private Long userSettingDbId;
    private int UserID;
    private String Key;
    private String Value;
    private int UType; //UserSettingTypes
    private String UpdateTime_;

    @Generated(hash = 1344105053)
    public UserSetting(Long userSettingDbId, int UserID, String Key, String Value,
                       int UType, String UpdateTime_) {
        this.userSettingDbId = userSettingDbId;
        this.UserID = UserID;
        this.Key = Key;
        this.Value = Value;
        this.UType = UType;
        this.UpdateTime_ = UpdateTime_;
    }

    @Generated(hash = 990984667)
    public UserSetting() {
    }

    public int getUserID() {
        return this.UserID;
    }

    public void setUserID(int UserID) {
        this.UserID = UserID;
    }

    public String getKey() {
        return this.Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public String getValue() {
        return this.Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }

    public int getUType() {
        return this.UType;
    }

    public void setUType(int UType) {
        this.UType = UType;
    }

    public String getUpdateTime_() {
        return this.UpdateTime_;
    }

    public void setUpdateTime_(String UpdateTime_) {
        this.UpdateTime_ = UpdateTime_;
    }

    public Long getUserSettingDbId() {
        return this.userSettingDbId;
    }

    public void setUserSettingDbId(Long userSettingDbId) {
        this.userSettingDbId = userSettingDbId;
    }
}
