package cc.emw.mobile.project.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Form implements Serializable {
	public ArrayList<Elements> Elements;
	public ArrayList<Tools> Tools;
	public ArrayList<Navigations> Navigations;
	public int ID;
	public String Name;
	public int RecordID;
	public String VerifyTime = "";
}
