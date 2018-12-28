package cc.emw.mobile.form.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 表单详情数据
 */
public class Form implements Serializable {
	public ArrayList<Elements> Elements; //表单元素
	public ArrayList<Tools> Tools; //
	public ArrayList<Navigations> Navigations; //内容项标题
	public int ID; //表单id
	public String Name; //名
	public int RecordID; //记录id
	public String VerifyTime = ""; //验证时间戳
    public OuterFlow Flow; //流程


	/**
	 * 分组下的数据
	 */
	public static class Elements implements Serializable {
		public int Columns; //列数据量
		public boolean ShowRowNumber;
		public ArrayList<GridControl.ToolInfo> Tools;
		public DataTable Data; //
		public ArrayList<Elements2> Elements; //元素内容
		public String Name; //名
		public String Title; //标题
		public int State; //状态
		public String Type; //类型
		public String ID;
	}

	public static class Navigations implements Serializable {
		public String Name; //导航条名称
		public String Element;
	}

	public static class Tools implements Serializable{
		public String Name;
		public String ID;
	}

	public static class OuterFlow implements Serializable {
		public String Name; //名称
		public String Memo;
		public ArrayList<Flow> Flows; //流程集合
	}
}
