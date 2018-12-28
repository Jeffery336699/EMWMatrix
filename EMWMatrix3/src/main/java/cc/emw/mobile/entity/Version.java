package cc.emw.mobile.entity;

import java.io.Serializable;

/**
 * 版本升级类
 * @author tengfei.li
 *
 */
public class Version implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public String name;
	public long size;
	public String url;
	public String version;
	public int versionCode;
	public String versionName;
	public String explainUrl;
	public String explainContent;
	public boolean isStartGuide;
	
}
