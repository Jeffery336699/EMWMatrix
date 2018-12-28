package cc.emw.mobile.contact.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;

/**
 * Created by tao.zhou on 2017/5/11.
 */

public class UserInfo implements Parcelable {

    public UserInfo() {
    }

    public int ID;
    public int IdentityID;
    public String Name;
    public String Image;
    public String BackImage;
    public String Job;
    public int Sex;
    public String Birthday;
    public int Age;
    public String DeptName;
    public boolean IsFollow;
    public int DeptID;
    public boolean IsOnline;
    public String Code;
    public String Phone;
    public String Password;
    public int UserType; //UserTypes
    public String CompanyCode;
    public String Email;
    public String VoipCode;
    public String VoipPwd;
    public String DeviceToken;
    public String EmailSignText;
    public String Axis;
    public int InitState; //UserInitState
    public String JobExperience;
    public String ClassSkill;
    public String College;
    public String HighSchool;
    public List<ApiEntity.UserSetting> UserSettings;

    protected UserInfo(Parcel in) {
        ID = in.readInt();
        IdentityID = in.readInt();
        Name = in.readString();
        Image = in.readString();
        BackImage = in.readString();
        Job = in.readString();
        Sex = in.readInt();
        Birthday = in.readString();
        Age = in.readInt();
        DeptName = in.readString();
        IsFollow = in.readByte() != 0;
        DeptID = in.readInt();
        IsOnline = in.readByte() != 0;
        Code = in.readString();
        Phone = in.readString();
        Password = in.readString();
        UserType = in.readInt();
        CompanyCode = in.readString();
        Email = in.readString();
        VoipCode = in.readString();
        VoipPwd = in.readString();
        DeviceToken = in.readString();
        EmailSignText = in.readString();
        Axis = in.readString();
        InitState = in.readInt();
        JobExperience = in.readString();
        ClassSkill = in.readString();
        College = in.readString();
        HighSchool = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeInt(IdentityID);
        parcel.writeString(Name);
        parcel.writeString(Image);
        parcel.writeString(BackImage);
        parcel.writeString(Job);
        parcel.writeInt(Sex);
        parcel.writeString(Birthday);
        parcel.writeInt(Age);
        parcel.writeString(DeptName);
        parcel.writeByte((byte) (IsFollow ? 1 : 0));
        parcel.writeInt(DeptID);
        parcel.writeByte((byte) (IsOnline ? 1 : 0));
        parcel.writeString(Code);
        parcel.writeString(Phone);
        parcel.writeString(Password);
        parcel.writeInt(UserType);
        parcel.writeString(CompanyCode);
        parcel.writeString(Email);
        parcel.writeString(VoipCode);
        parcel.writeString(VoipPwd);
        parcel.writeString(DeviceToken);
        parcel.writeString(EmailSignText);
        parcel.writeString(Axis);
        parcel.writeInt(InitState);
        parcel.writeString(JobExperience);
        parcel.writeString(ClassSkill);
        parcel.writeString(College);
        parcel.writeString(HighSchool);
    }
}
