package cc.emw.mobile.entity;

import java.util.List;

/**
 * Created by ${zrjt} on 2016/9/19.
 */
public class NoticeComment {
    public int ID;
    public int isNew;
    public int userId;
    public int topId;
    public int sendID;
    public String oldContent;
    public List<CommentInfo> commentInfos;
    public List<EnjoyInfo> enjoyInfos;

    public static class CommentInfo {
        public int userID;
        public String userName;
        public int isNew;
        public String urls;
        public String newContent;
    }

    public static class EnjoyInfo {
        public String urls;
    }

}
