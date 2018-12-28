package cc.emw.mobile.form.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jven.wu on 2016/9/28.
 */
public class Flow implements Serializable {
    public int ID;
    public String Node; //节点
    public String Title; //标题
    public String Remark; //描述
    public ArrayList<UserStatu> UserStatus;
    public ArrayList<History> History; //历吏流程
    public ArrayList<Line> Lines; //要审批流程
    public boolean AllowAppend; //是否有 转发
    public boolean AllowTrans; //是否有 加签
    public boolean AllowReturn; //是否有 回退上个环节

    /**
     * 用户状态
     */
    public static class UserStatu implements Serializable,IFlow {
        public int UserID;
        public String UserName; //用户名
        public String UserImage; //用户头像
        public int LineID; //流程id
    }

    /**
     * 历史流程实体
     */
    public static class History implements Serializable,IFlow {
        public String User; //用户
        public String Image; //图片
        public String Line; //
        public String Content; //内容
        public String Time; //时间
    }

    /**
     * 可审批的流程
     */
    public static class Line implements Serializable,IFlow {
        public int ID;
        public String Name; //名称
        public String Memo; //描述
        public boolean NoReceiver;
        public String Icon; //图片
    }
}
