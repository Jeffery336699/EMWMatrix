package cc.emw.mobile.contact.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import cc.emw.mobile.chat.model.bean.DaoSession;

/**
 * Created by tao.zhou on 2017/5/11.
 */
@Entity
public class UserInfo {
    /**
     * 数据库主键  自增长 必须是long类型
     */
    @Id(autoincrement = true)
    private Long userDbId;
    private int ID;
    private int IdentityID;
    private String Name;
    private String Image;
    private String BackImage;
    private String Job;
    private int Sex;
    private String Birthday;
    private int Age;
    private String DeptName;
    private boolean IsFollow;
    private int DeptID;
    private boolean IsOnline;
    private String Code;
    private String Phone;
    private String Password;
    private int UserType; //UserTypes
    private String CompanyCode;
    private String Email;
    private String VoipCode;
    private String VoipPwd;
    private String DeviceToken;
    private String EmailSignText;
    private String Axis;
    private int InitState; //UserInitState
    private String JobExperience;
    private String ClassSkill;
    private String College;
    private String HighSchool;

    @ToMany(referencedJoinProperty = "userSettingDbId")
    public List<UserSetting> UserSettings;

    public String sortLetters; //名称的首字母
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 437952339)
    private transient UserInfoDao myDao;

    @Generated(hash = 1761372649)
    public UserInfo(Long userDbId, int ID, int IdentityID, String Name, String Image, String BackImage,
                    String Job, int Sex, String Birthday, int Age, String DeptName, boolean IsFollow,
                    int DeptID, boolean IsOnline, String Code, String Phone, String Password, int UserType,
                    String CompanyCode, String Email, String VoipCode, String VoipPwd, String DeviceToken,
                    String EmailSignText, String Axis, int InitState, String JobExperience, String ClassSkill,
                    String College, String HighSchool, String sortLetters) {
        this.userDbId = userDbId;
        this.ID = ID;
        this.IdentityID = IdentityID;
        this.Name = Name;
        this.Image = Image;
        this.BackImage = BackImage;
        this.Job = Job;
        this.Sex = Sex;
        this.Birthday = Birthday;
        this.Age = Age;
        this.DeptName = DeptName;
        this.IsFollow = IsFollow;
        this.DeptID = DeptID;
        this.IsOnline = IsOnline;
        this.Code = Code;
        this.Phone = Phone;
        this.Password = Password;
        this.UserType = UserType;
        this.CompanyCode = CompanyCode;
        this.Email = Email;
        this.VoipCode = VoipCode;
        this.VoipPwd = VoipPwd;
        this.DeviceToken = DeviceToken;
        this.EmailSignText = EmailSignText;
        this.Axis = Axis;
        this.InitState = InitState;
        this.JobExperience = JobExperience;
        this.ClassSkill = ClassSkill;
        this.College = College;
        this.HighSchool = HighSchool;
        this.sortLetters = sortLetters;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getIdentityID() {
        return this.IdentityID;
    }

    public void setIdentityID(int IdentityID) {
        this.IdentityID = IdentityID;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getImage() {
        return this.Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public String getBackImage() {
        return this.BackImage;
    }

    public void setBackImage(String BackImage) {
        this.BackImage = BackImage;
    }

    public String getJob() {
        return this.Job;
    }

    public void setJob(String Job) {
        this.Job = Job;
    }

    public int getSex() {
        return this.Sex;
    }

    public void setSex(int Sex) {
        this.Sex = Sex;
    }

    public String getBirthday() {
        return this.Birthday;
    }

    public void setBirthday(String Birthday) {
        this.Birthday = Birthday;
    }

    public int getAge() {
        return this.Age;
    }

    public void setAge(int Age) {
        this.Age = Age;
    }

    public String getDeptName() {
        return this.DeptName;
    }

    public void setDeptName(String DeptName) {
        this.DeptName = DeptName;
    }

    public boolean getIsFollow() {
        return this.IsFollow;
    }

    public void setIsFollow(boolean IsFollow) {
        this.IsFollow = IsFollow;
    }

    public int getDeptID() {
        return this.DeptID;
    }

    public void setDeptID(int DeptID) {
        this.DeptID = DeptID;
    }

    public boolean getIsOnline() {
        return this.IsOnline;
    }

    public void setIsOnline(boolean IsOnline) {
        this.IsOnline = IsOnline;
    }

    public String getCode() {
        return this.Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getPhone() {
        return this.Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getPassword() {
        return this.Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public int getUserType() {
        return this.UserType;
    }

    public void setUserType(int UserType) {
        this.UserType = UserType;
    }

    public String getCompanyCode() {
        return this.CompanyCode;
    }

    public void setCompanyCode(String CompanyCode) {
        this.CompanyCode = CompanyCode;
    }

    public String getEmail() {
        return this.Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getVoipCode() {
        return this.VoipCode;
    }

    public void setVoipCode(String VoipCode) {
        this.VoipCode = VoipCode;
    }

    public String getVoipPwd() {
        return this.VoipPwd;
    }

    public void setVoipPwd(String VoipPwd) {
        this.VoipPwd = VoipPwd;
    }

    public String getDeviceToken() {
        return this.DeviceToken;
    }

    public void setDeviceToken(String DeviceToken) {
        this.DeviceToken = DeviceToken;
    }

    public String getEmailSignText() {
        return this.EmailSignText;
    }

    public void setEmailSignText(String EmailSignText) {
        this.EmailSignText = EmailSignText;
    }

    public String getAxis() {
        return this.Axis;
    }

    public void setAxis(String Axis) {
        this.Axis = Axis;
    }

    public int getInitState() {
        return this.InitState;
    }

    public void setInitState(int InitState) {
        this.InitState = InitState;
    }

    public String getJobExperience() {
        return this.JobExperience;
    }

    public void setJobExperience(String JobExperience) {
        this.JobExperience = JobExperience;
    }

    public String getClassSkill() {
        return this.ClassSkill;
    }

    public void setClassSkill(String ClassSkill) {
        this.ClassSkill = ClassSkill;
    }

    public String getCollege() {
        return this.College;
    }

    public void setCollege(String College) {
        this.College = College;
    }

    public String getHighSchool() {
        return this.HighSchool;
    }

    public void setHighSchool(String HighSchool) {
        this.HighSchool = HighSchool;
    }

    public String getSortLetters() {
        return this.sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }


    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 351700933)
    public synchronized void resetUserSettings() {
        UserSettings = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 821180768)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserInfoDao() : null;
    }

    public Long getUserDbId() {
        return this.userDbId;
    }

    public void setUserDbId(Long userDbId) {
        this.userDbId = userDbId;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1938867022)
    public List<UserSetting> getUserSettings() {
        if (UserSettings == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserSettingDao targetDao = daoSession.getUserSettingDao();
            List<UserSetting> UserSettingsNew = targetDao._queryUserInfo_UserSettings(userDbId);
            synchronized (this) {
                if (UserSettings == null) {
                    UserSettings = UserSettingsNew;
                }
            }
        }
        return UserSettings;
    }

}
