package cc.emw.mobile.chat.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sunny.du on 2017/4/20.
 */

public class LocationBean implements Parcelable {
    public String addressName;
    public boolean isChoose;
    public String title;
    public double longitude;
    public double latitude;
    public String url;
    public boolean isShareLocation;

    public LocationBean() {
    }

    public LocationBean(String addressName,
                        boolean isChoose,
                        String title,
                        double longitude,
                        double latitude,
                        String url) {
        this.addressName = addressName;
        this.isChoose = isChoose;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.url = url;
    }


    protected LocationBean(Parcel in) {
        addressName = in.readString();
        isChoose = in.readByte() != 0;
        title = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        url = in.readString();
        isShareLocation = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressName);
        dest.writeByte((byte) (isChoose ? 1 : 0));
        dest.writeString(title);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(url);
        dest.writeByte((byte)(isShareLocation ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocationBean> CREATOR = new Creator<LocationBean>() {
        @Override
        public LocationBean createFromParcel(Parcel in) {
            return new LocationBean(in);
        }

        @Override
        public LocationBean[] newArray(int size) {
            return new LocationBean[size];
        }
    };
}
