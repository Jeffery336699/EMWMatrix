package cc.emw.mobile.chat.model.bean;

/**
 * Created by jven.wu on 2016/7/29.
 */
public class Videos {
    private String Url;// 视频路径
    public String Length;// 视频大小
    public String ThumbFileName;//视频截图名称

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getLength() {
        return Length;
    }

    public void setLength(String length) {
        Length = length;
    }

    public String getThumbFileName() {
        return ThumbFileName;
    }

    public void setThumbFileName(String thumbFileName) {
        ThumbFileName = thumbFileName;
    }
}
