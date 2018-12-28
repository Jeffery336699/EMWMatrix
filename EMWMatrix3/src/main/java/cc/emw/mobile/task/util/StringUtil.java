package cc.emw.mobile.task.util;

import android.text.Html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunny.du on 2016/9/5.
 */
public class StringUtil {
    /**
     * 定义script的正则表达式
     */
    private static final String REGEX_SCRIPT="<script[^>]*>[\\s\\S]*?<\\/script>";
    /**
     * 定义style的正则表达式
     */
    private static final String REGEX_STYLE="<STYLE[^>]*?>[\\S\\s]*?<\\/STYLE>";
    /**
     * 定义html标签的正则表达式
     */
    private static final String REGEX_HTML="<[^>]+>";
    /**
     * 定义空格回车换行符
     */
    private static final String REGEX_SPACE="\\s*|\t|\r|\n";

    private static final String REGEX_HTML2="&lt;[^&gt;]+&gt;";
    //private static final String REGEX_HTML3="&lt;[^]+&gt;";   &lt;[^>]+&gt;
    private static final String REGEX_HTML3="&lt;[^>]+&gt;";
    /**
     * @des  格式化文本，去除文本中的html标签和属性
     */
    public static String delHTMLTag(String htmlStr){
        htmlStr=Html.fromHtml(htmlStr).toString();
        Pattern patternScript=Pattern.compile(REGEX_SCRIPT,Pattern.CASE_INSENSITIVE);
        Matcher matcherScript=patternScript.matcher(htmlStr);
        htmlStr=matcherScript.replaceAll("");

        Pattern patternStyle=Pattern.compile(REGEX_STYLE,Pattern.CASE_INSENSITIVE);
        Matcher matcherStyle=patternStyle.matcher(htmlStr);
        htmlStr=matcherStyle.replaceAll("");

        Pattern patternHTML=Pattern.compile(REGEX_HTML,Pattern.CASE_INSENSITIVE);
        Matcher matcherHTML=patternHTML.matcher(htmlStr);
        htmlStr=matcherHTML.replaceAll("");

        Pattern patternSpace=Pattern.compile(REGEX_SPACE,Pattern.CASE_INSENSITIVE);
        Matcher matcherSpace=patternSpace.matcher(htmlStr);
        htmlStr=matcherSpace.replaceAll("");

        Pattern patternHTML2=Pattern.compile(REGEX_HTML2,Pattern.CASE_INSENSITIVE);
        Matcher matcherHTML2=patternHTML2.matcher(htmlStr);
        htmlStr=matcherHTML2.replaceAll("");

        Pattern patternHTML3=Pattern.compile(REGEX_HTML3,Pattern.CASE_INSENSITIVE);
        Matcher matcherHTML3=patternHTML3.matcher(htmlStr);
        htmlStr=matcherHTML3.replaceAll("");

        htmlStr=htmlStr.trim();
        return htmlStr;
    }

//    public static String getfromHtmlString(String str){
//        String s = "";
//        if (str != null) {
//            s = str.replace("&lt;b&gt", "");
//            s = s.replace("&lt;/b&gt", "");
//            s=s.replace(";","");
//        }
//        return Html.fromHtml(s).toString();
//    }
}
