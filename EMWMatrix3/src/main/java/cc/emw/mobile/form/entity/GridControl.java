package cc.emw.mobile.form.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class GridControl implements Serializable {
	
	public ArrayList<ColInfo> Columns;
	public ArrayList<Elements2> Searchers;
	public ArrayList<ToolInfo> Tools;
	public ArrayList<ViewInfo> Views;
	public ArrayList<ChartInfo> Charts;
	public int ID;
	public String Name;
	public int TID;
	
	
	public static class ColInfo implements Serializable {
		
		public int DBType;
		public String Name;
		public String Memo;
		public String Type;
		public String ID;
	}
	
	/*public static class SearchInfo implements Serializable {
		
		public int DBType;
		public String Name;
		public String Type;
		public String ID;
	}*/
	
	public static class ToolInfo implements Serializable {
		
		public String Icon;
		public int SelectType;
		public String ToolType;
		public int PageID;
		public int Key;
		public String Name;
		public String Type;
		public String ID;
		public String ParentTool;

		@Override
		public String toString() {
			return Name;
		}
	}
	
	public static class ViewInfo implements Serializable {
		
		public String Name;
		public String Memo;
		public String Type;
		public String ID;
	}
	
	public static class ChartInfo implements Serializable {
		
		public int ChartType;
		public int CountType;
		public boolean IsShowTitle;
		public String LegendLocation;
		public String Name;
		public String Type;
		public String ID;
	}
}
