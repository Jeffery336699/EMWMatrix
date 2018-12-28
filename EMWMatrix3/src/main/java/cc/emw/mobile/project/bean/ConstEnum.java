package cc.emw.mobile.project.bean;

/**
 * Created by jven.wu on 2016/6/29.
 */
public class ConstEnum {
    public class ProjectColor{
        public static final int None = 0;
        public static final int LightBlue = 1;
        public static final int Orange = LightBlue + 1;
        public static final int Red = Orange + 1;
        public static final int DarkBlue = Red + 1;
    }

    public class Create{
        public static final int CreateTeam = 1;
        public static final int CreateProject = 2;
    }
}
