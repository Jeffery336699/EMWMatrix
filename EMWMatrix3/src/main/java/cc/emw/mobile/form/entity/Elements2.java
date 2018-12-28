package cc.emw.mobile.form.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 行或列数据
 */
public class Elements2 implements Serializable {
	public ArrayList<Column> Columns; //列集合
	public String Text = "";
	public ArrayList<SelectItems> SelectItems; //选项
	public boolean IsAllowNull = true;
	public int DBType; //数据库类型
	public int Length; //长度
	public String Value = ""; //值
	public String DefaultValue = ""; //默认值
	public String Name = ""; //名称
	public String Title = ""; //标题
	public int State; //CommonConsts.ElementStates
	public String Type = ""; //类型
	public String ID = "";


	public static class Column implements Serializable {
		public String Header; //列头部
		public int Width; //列宽度
		public boolean IsText; //是否为文本
		public boolean IsValue; //是否有值
	}

	/**
	 * 选项实体
	 */
	public static class SelectItems implements Serializable {
		public String Text; //文本
		public String Value; //值
	}
}
