package cc.emw.mobile.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class DataTable implements Serializable{

	public ArrayList<String> Columns;
	public int MaxRow;
	public ArrayList<ArrayList<String>> Rows;
	
	public String getRowID() {
		if (Rows != null && Rows.size() > 0)
			return Rows.get(0).get(getColIndex(Columns, "ID"));
		return null;
	}

	public static int getColIndex(ArrayList<String> columns, String name) {
		String s = "";
		for (int i = 0; i < columns.size(); i++) {
			s = columns.get(i);
			if (name.equalsIgnoreCase(s)) {
				return i;
			}
		}
		return -1;
	}
}