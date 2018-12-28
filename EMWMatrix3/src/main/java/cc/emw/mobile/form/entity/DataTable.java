package cc.emw.mobile.form.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class DataTable implements Serializable{

	public ArrayList<String> Columns;
	public int MaxRow;
	public ArrayList<ArrayList<String>> Rows;
	
	public int getRowID() {
		try {
			return Integer.valueOf(Rows.get(0).get(getColIndex(Columns, "ID")));
		} catch (Exception e) {
			return -1;
		}
	}

	public int getRowID(int index) {
		try {
			return Integer.valueOf(Rows.get(index).get(getColIndex(Columns, "ID")));
		} catch (Exception e) {
			return -1;
		}
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
