package cc.emw.mobile.project.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Elements2 implements Serializable {
	public ArrayList<Column> Columns;
	public String Text = "";
	public ArrayList<SelectItems> SelectItems;
	public boolean IsAllowNull = true;
	public int DBType;
	public int Length;
	public String Value = "";
	public String Name = "";
	public String Title = "";
	public String Type = "";
	public String ID = "";
}
