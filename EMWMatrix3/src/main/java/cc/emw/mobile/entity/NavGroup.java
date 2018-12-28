package cc.emw.mobile.entity;

import java.io.Serializable;
import java.util.List;

import cc.emw.mobile.net.ApiEntity;

/**
 * 导航设置
 */
public class NavGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	public boolean IsBase;
	public int Price;
	public String ICON;
	public boolean IsPublish;
	public String Images;
	public String VideoName;
	public String VideoURL;
	public int Folder;
	public String LGICON;

	public int ID;
	public String Name;
	public String Memo;
	public int Language;
	public int Creator;
	public String CreateTime;
	public String CreatorName;
	public int Modifier;
	public String ModifyTime;
	public int VER;
	public int Sort;
	public int Visible;
	public String AliasName;
	public List<ApiEntity.Navigation> NavList;
}
