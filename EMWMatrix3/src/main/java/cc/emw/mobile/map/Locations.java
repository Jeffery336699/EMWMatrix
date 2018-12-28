package cc.emw.mobile.map;

import java.io.Serializable;

//经纬度bean
public class Locations implements Serializable {

    private String Axis;// 经纬度
    private int ID; // 用户ID
    private String Name;// 用户名字
    private String Image;
    public String address;
    public String AxisTime;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAxis() {
        return Axis;
    }

    public void setAxis(String axis) {
        Axis = axis;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
