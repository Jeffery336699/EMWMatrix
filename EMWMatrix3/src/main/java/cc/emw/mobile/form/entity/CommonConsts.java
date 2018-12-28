package cc.emw.mobile.form.entity;

/**
 * Created by jven.wu on 2016/5/13.
 */
public class CommonConsts {
    //元素类型
    public static class ElementType{
        public static final int TEXTBOX = 1;  //文本输入框控件
        public static final int DATETIMER = 2; //时间选择控件
        public static final int DROPDOWNLIST = 3; //下拉控件
        public static final int SEARCHER = 4; //搜索控件
        public static final int RADIOBUTTON = 5; //单选控件
        public static final int TIMESELECTOR = 6; //时间控件
        public static final int CHECKBOX = 7; //
        public static final int LABEL = 8; //文本控件
    }

    /**
     * 元素状态
     */
    public static class ElementStates{
        public static final int NoSet = 0;  //
        public static final int Normal = 1; //可编辑
        public static final int ReadOnly = 2; //只读
        public static final int Hidden = 3; //隐藏
    }

}
