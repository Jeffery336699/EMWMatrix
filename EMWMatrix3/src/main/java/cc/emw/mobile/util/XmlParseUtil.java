package cc.emw.mobile.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.XMLFormatter;

import cc.emw.mobile.entity.Version;

/**
 * XML解析工具
 * @author shaobo.zhuang
 *
 */
public class XmlParseUtil {
	/**
	 * pull解析版本信息xml
	 * @param xml
	 * @return
	 */
	public static Version parser(String xml) { 
		Version version = null; 
        InputStream stream = null; 
        XmlPullParser parser = Xml.newPullParser();//创建XmlPullparser实例 
        try { 
            stream = new ByteArrayInputStream(xml.getBytes());//初始化输入流 
            parser.setInput(stream, "utf-8");//设置输入流 并指明编码方式 
            int evnType = parser.getEventType(); // get event type 
            String tag = "", flag = "";
            while (evnType != XmlPullParser.END_DOCUMENT) { // continue to end document 
                switch (evnType) { 
                case XmlPullParser.START_DOCUMENT://表示XML文档开始
                	version = new Version();//创建Version对象
                	break;
                case XmlPullParser.START_TAG: //表示XML开始读取标签
                    tag = parser.getName(); //获得解析标记名 
                    if ("file".equalsIgnoreCase(tag)) { //标记是file的情况 
                    	flag = "file";
                    } else if ("explain".equalsIgnoreCase(tag)) { 
                    	flag = "explain";
                    } 
                    break;
                case XmlPullParser.TEXT: //表示所读的是文本内容
                	if ("name".equalsIgnoreCase(tag)) {
						version.name = parser.getText();
					} else if ("size".equalsIgnoreCase(tag)) {
						version.size = Long.parseLong(parser.getText());
					} else if ("url".equalsIgnoreCase(tag)) {
						if ("file".equalsIgnoreCase(flag)) {
							version.url = parser.getText();
						} else if ("explain".equalsIgnoreCase(flag)) {
							version.explainUrl = parser.getText();
						}
					} else if ("version".equalsIgnoreCase(tag)) {
						version.version = parser.getText();
					} else if ("versionCode".equalsIgnoreCase(tag)) {
						version.versionCode = Integer.parseInt(parser.getText());
					} else if ("versionName".equalsIgnoreCase(tag)) {
						version.versionName = parser.getText();
					} else if ("content".equalsIgnoreCase(tag)) {
						version.explainContent = parser.getText();
					} else if ("isStartGuide".equalsIgnoreCase(tag)) {
						version.isStartGuide = Boolean.parseBoolean(parser.getText());
					}
                    break;     
                case XmlPullParser.END_TAG: 
                	tag = "";
                    break; 
                default: 
                    break; 
                } 
                evnType = parser.next(); 
            } 
        } catch (Exception e) { 
        	e.printStackTrace();
        } 
        return version; 
    } 
} 
