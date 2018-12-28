package cc.emw.mobile;

import java.io.File;
import java.util.ArrayList;

import org.xutils.x;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.SparseArray;

import com.githang.androidcrash.AndroidCrash;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.ErrorlogManager;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.BadgeView;

import com.githang.androidcrash.AndroidCrash;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

/**
 * User: Geek_Soledad(msdx.android@qq.com) Date: 2014-11-03 Time: 23:26 FIXME
 */
public class EMWApplication extends Application {
    /**
     * 存储异常日志路径
     */
    public String logPath = Environment.getExternalStorageDirectory()
            + File.separator + "ZKBRDir" + File.separator + "log";
    /**
     * 存储人员头图路径
     */
    public static String iconPath = Environment.getExternalStorageDirectory()
            + File.separator + "ZKBRDir" + File.separator + "img"
            + File.separator;
    /**
     * 存储文件路径
     */
    public static String filePath = Environment.getExternalStorageDirectory()
            + File.separator + "ZKBRDir" + File.separator + "file"
            + File.separator;
    /**
     * 存储临时文件路径
     */
    public static String tempPath = Environment.getExternalStorageDirectory()
            + File.separator + "ZKBRDir" + File.separator + "temp"
            + File.separator;
    /**
     * 存储下载图片路径
     */
    public static String imagePath = Environment.getExternalStorageDirectory()
            + File.separator + "ZKBRDir" + File.separator + "images"
            + File.separator;

    public static SparseArray<UserInfo> personMap = new SparseArray<UserInfo>(); //HashMap<Integer, UserInfo>
    public static SparseArray<GroupInfo> groupMap = new SparseArray<GroupInfo>(); //HashMap<Integer, GroupInfo>
    private static Map<String, String> iconMap;
    /**
     * 人员列表已排序
     */
    public static ArrayList<UserInfo> personSortList;
    /**
     * 当前聊天界面用户ID
     */
    public static int currentChatUid;
    /**
     * 主界面顶部条右上角标
     */
    public static BadgeView badgeView;

    @Override
    public void onCreate() {
        super.onCreate();
        createDirectory();
        AndroidCrash.getInstance().setLogFilePath(logPath);
        ErrorlogManager.initEmailReporter(this);// initHttpReporter(this);

        ImageLoader.getInstance().init(
                ImageLoaderConfiguration.createDefault(this)); // 全局初始化 ,默认的配置
        L.writeLogs(false); // 使ImageLoader不打印日志信息

        x.Ext.init(this);
        x.Ext.setDebug(false); // 是否输出debug日志

        PrefsUtil.init(this);

        initIcon();
    }

    private void createDirectory() {
        File logFile = new File(logPath);
        if (!logFile.exists()) {
            logFile.mkdirs();
        }
        File iconFile = new File(iconPath);
        if (!iconFile.exists()) {
            iconFile.mkdirs();
        }
        File downFile = new File(filePath);
        if (!downFile.exists()) {
            downFile.mkdirs();
        }
        File tempFile = new File(tempPath);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            imageFile.mkdirs();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static String getIcon(String icon) {
        StringBuilder str = new StringBuilder();
        if (iconMap != null && iconMap.get(icon) != null) {
            str.append("&#x").append(iconMap.get(icon)).append(";");
        } else {
            str.append("&#xe809;");
        }
        return str.toString();
    }

    private void initIcon() {
        iconMap = new HashMap<>();
        iconMap.put("search", "e800");
        iconMap.put("mail", "e801");
        iconMap.put("heart", "e802");
        iconMap.put("heart-empty", "e803");
        iconMap.put("star", "e804");
        iconMap.put("user", "e805");
        iconMap.put("video", "e806");
        iconMap.put("picture", "e807");
        iconMap.put("camera", "e808");
        iconMap.put("ok", "e809");
        iconMap.put("ok-circle", "e80a");
        iconMap.put("cancel", "e80b");
        iconMap.put("cancel-circle", "e80c");
        iconMap.put("plus", "e80d");
        iconMap.put("plus-circle", "e80e");
        iconMap.put("minus", "e80f");
        iconMap.put("minus-circle", "e810");
        iconMap.put("help", "e811");
        iconMap.put("info", "e812");
        iconMap.put("home", "e813");
        iconMap.put("link", "e814");
        iconMap.put("attach", "e815");
        iconMap.put("lock", "e816");
        iconMap.put("lock-empty", "e817");
        iconMap.put("lock-open", "e818");
        iconMap.put("lock-open-empty", "e819");
        iconMap.put("pin", "e81a");
        iconMap.put("eye", "e81b");
        iconMap.put("tag", "e81c");
        iconMap.put("tag-empty", "e81d");
        iconMap.put("download", "e81e");
        iconMap.put("upload", "e81f");
        iconMap.put("download-cloud", "e820");
        iconMap.put("upload-cloud", "e821");
        iconMap.put("quote-left", "e822");
        iconMap.put("quote-right", "e823");
        iconMap.put("quote-left-alt", "e824");
        iconMap.put("quote-right-alt", "e825");
        iconMap.put("pencil", "e826");
        iconMap.put("pencil-neg", "e827");
        iconMap.put("pencil-alt", "e828");
        iconMap.put("undo", "e829");
        iconMap.put("comment", "e82a");
        iconMap.put("comment-inv", "e82b");
        iconMap.put("comment-alt", "e82c");
        iconMap.put("comment-inv-alt", "e82d");
        iconMap.put("comment-alt2", "e82e");
        iconMap.put("comment-inv-alt2", "e82f");
        iconMap.put("chat", "e830");
        iconMap.put("chat-inv", "e831");
        iconMap.put("location", "e832");
        iconMap.put("location-inv", "e833");
        iconMap.put("location-alt", "e834");
        iconMap.put("compass", "e835");
        iconMap.put("trash", "e836");
        iconMap.put("trash-empty", "e837");
        iconMap.put("doc", "e838");
        iconMap.put("doc-inv", "e839");
        iconMap.put("doc-alt", "e83a");
        iconMap.put("doc-inv-alt", "e83b");
        iconMap.put("article", "e83c");
        iconMap.put("article-alt", "e83d");
        iconMap.put("book-open", "e83e");
        iconMap.put("folder", "e83f");
        iconMap.put("folder-empty", "e840");
        iconMap.put("box", "e841");
        iconMap.put("rss", "e842");
        iconMap.put("rss-alt", "e843");
        iconMap.put("cog", "e844");
        iconMap.put("wrench", "e845");
        iconMap.put("share", "e846");
        iconMap.put("calendar", "e847");
        iconMap.put("calendar-inv", "e848");
        iconMap.put("calendar-alt", "e849");
        iconMap.put("mic", "e84a");
        iconMap.put("volume-off", "e84b");
        iconMap.put("volume-up", "e84c");
        iconMap.put("headphones", "e84d");
        iconMap.put("clock", "e84e");
        iconMap.put("lamp", "e84f");
        iconMap.put("block", "e850");
        iconMap.put("resize-full", "e851");
        iconMap.put("resize-full-alt", "e852");
        iconMap.put("resize-small", "e853");
        iconMap.put("resize-small-alt", "e854");
        iconMap.put("resize-vertical", "e855");
        iconMap.put("resize-horizontal", "e856");
        iconMap.put("move", "e857");
        iconMap.put("popup", "e858");
        iconMap.put("down", "e859");
        iconMap.put("left", "e85a");
        iconMap.put("right", "e85b");
        iconMap.put("up", "e85c");
        iconMap.put("down-circle", "e85d");
        iconMap.put("left-circle", "e85e");
        iconMap.put("right-circle", "e85f");
        iconMap.put("up-circle", "e860");
        iconMap.put("cw", "e861");
        iconMap.put("loop", "e862");
        iconMap.put("loop-alt", "e863");
        iconMap.put("exchange", "e864");
        iconMap.put("split", "e865");
        iconMap.put("arrow-curved", "e866");
        iconMap.put("play", "e867");
        iconMap.put("play-circle2", "e868");
        iconMap.put("stop", "e869");
        iconMap.put("pause", "e86a");
        iconMap.put("to-start", "e86b");
        iconMap.put("to-end", "e86c");
        iconMap.put("eject", "e86d");
        iconMap.put("target", "e86e");
        iconMap.put("signal", "e86f");
        iconMap.put("award", "e870");
        iconMap.put("award-empty", "e871");
        iconMap.put("list", "e872");
        iconMap.put("list-nested", "e873");
        iconMap.put("bat-empty", "e874");
        iconMap.put("bat-half", "e875");
        iconMap.put("bat-full", "e876");
        iconMap.put("bat-charge", "e877");
        iconMap.put("mobile", "e878");
        iconMap.put("cd", "e879");
        iconMap.put("equalizer", "e87a");
        iconMap.put("cursor", "e87b");
        iconMap.put("aperture", "e87c");
        iconMap.put("aperture-alt", "e87d");
        iconMap.put("steering-wheel", "e87e");
        iconMap.put("book", "e87f");
        iconMap.put("book-alt", "e880");
        iconMap.put("brush", "e881");
        iconMap.put("brush-alt", "e882");
        iconMap.put("eyedropper", "e883");
        iconMap.put("layers", "e884");
        iconMap.put("layers-alt", "e885");
        iconMap.put("sun", "e886");
        iconMap.put("sun-inv", "e887");
        iconMap.put("cloud", "e888");
        iconMap.put("rain", "e889");
        iconMap.put("flash", "e88a");
        iconMap.put("moon", "e88b");
        iconMap.put("moon-inv", "e88c");
        iconMap.put("umbrella", "e88d");
        iconMap.put("chart-bar", "e88e");
        iconMap.put("chart-pie", "e88f");
        iconMap.put("chart-pie-alt", "e890");
        iconMap.put("key", "e891");
        iconMap.put("key-inv", "e892");
        iconMap.put("hash", "e893");
        iconMap.put("at", "e894");
        iconMap.put("pilcrow", "e895");
        iconMap.put("dial", "e896");
    }
}
