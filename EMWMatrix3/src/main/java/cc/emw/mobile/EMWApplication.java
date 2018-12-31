package cc.emw.mobile;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.githang.androidcrash.AndroidCrash;
import com.githang.androidcrash.util.AppManager;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.xutils.x;

import java.io.File;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cc.emw.mobile.base.MyOpenHelper;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.model.bean.DaoMaster;
import cc.emw.mobile.chat.model.bean.DaoSession;
import cc.emw.mobile.chat.model.bean.HistoryMessage;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.AuthImageDownloader;
import cc.emw.mobile.util.CacheUtils;
import cc.emw.mobile.util.ErrorlogManager;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.IconTextView;
import io.rong.imkit.RongIM;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

//import com.lzy.ninegrid.NineGridView;

/**
 * User: Geek_Soledad(msdx.android@qq.com) Date: 2014-11-03 Time: 23:26 FIXME
 */
public class EMWApplication extends MultiDexApplication {

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
            + File.separator + "ZKBRDir" + File.separator + ".temp"
            + File.separator;
    /**
     * 存储临时音频路径
     */
    public static String audioPath = Environment.getExternalStorageDirectory()
            + File.separator + "ZKBRDir" + File.separator + ".audio"
            + File.separator;
    /**
     * 存储下载图片路径
     */
    public static String imagePath = Environment.getExternalStorageDirectory()
            + File.separator + "ZKBRDir" + File.separator + ".images"
            + File.separator;

    /**
     * 存储临时视频路径
     */
    public static String videoPath = Environment.getExternalStorageDirectory()
            + File.separator + "ZKBRDir" + File.separator + ".video"
            + File.separator;

    public static SparseArray<UserInfo> personMap = new SparseArray<UserInfo>(); //HashMap<Integer, UserInfo>
    public static SparseArray<GroupInfo> groupMap = new SparseArray<GroupInfo>(); //HashMap<Integer, GroupInfo>
    public static SparseIntArray colorMap = new SparseIntArray(); //存放头像颜色
    public static List<HistoryMessage> mChatHistory = new ArrayList<>();
    public static String personAIDLStr = "";
    public static String groupAIDLStr = "";

    /**
     * 人员列表已排序
     */
    public static ArrayList<UserInfo> personSortList;
    /**
     * 当前聊天界面用户ID
     */
    public static int currentChatUid = -2;
    public static int onMessageReceive = 0;    //是否有新消息
    public static int mKeyBoardH = 0;
    public static StringBuilder tempMsg = new StringBuilder(); //拼接的消息完整内容，由于收到推送消息内容有可能会被截断


    /**
     * socket
     * key : "msg" 接收到聊天信息
     * key : "join" 加入圈子socket消息
     * key : "reg"  注册socket ID
     */
    public final static String SOCKET_EVENT_MSG = "msg";
    public final static String SOCKET_EVENT_JOIN = "join";
    public final static String SOCKET_EVENT_REG = "reg";
    /***
     * IO_socket 初始化工作 Application 定义Socket 唯一性
     */
    private Socket mSocket;

    {
        try {
            SSLContext sc = null;
            TrustManager[] trustCerts = new TrustManager[]{new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] x509Certificates = new X509Certificate[0];
                    return x509Certificates;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {

                }
            }};
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustCerts, new SecureRandom());
            IO.Options options = new IO.Options();
            options.reconnection = true; //是否重新连接
            options.timeout = 20000; //连接超时时间
            options.reconnectionDelay = 20000;//重新连接间隔
            options.reconnectionAttempts = 5;//重连尝试次数
            options.sslContext = sc;
            options.hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            mSocket = IO.socket(Const.SOCKET_URL, options);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            LogLongUtil.e("EMWApplication", " mSocket =" + e.getMessage());
        }
    }

    public Socket getAppIOSocket() {
        return mSocket;
    }

    private Handler myHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showHostOnlineAlert((com.farsunset.cim.client.model.Message) msg.obj);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("EMWApplication", "EMWApplication >> onCreate():" + android.os.Process.myPid());

        createDirectory();
        AndroidCrash.getInstance().setLogFilePath(logPath);
        ErrorlogManager.initEmailReporter(this);// initHttpReporter(this);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .imageDownloader(new AuthImageDownloader(this)) //忽略证书
                .build();// 开始构建
        ImageLoader.getInstance().init(config);// 全局初始化此配置
        L.writeLogs(false); // 使ImageLoader不打印日志信息

        //        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        //                .setDefaultFontPath("iconfont/Roboto-Regular.ttf")
        //                .setFontAttrId(R.attr.fontPath)
        //                .disableCustomViewInflation()
        //                .build()
        //        ); //全局初始化，全部字体使用

        x.Ext.init(this);
        x.Ext.setDebug(false); // 是否输出debug日志
        //忽略证书信任问题
        x.Ext.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        PrefsUtil.init(this);
        IconTextView.initIcon();
        Const.init(this);
        // configure CacheUtilsLibrary
        CacheUtils.configureCache(this);
        setupDatabase();

        deleteDirectory();

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            Log.e("EMWApplication", "EMWApplication >> " + android.os.Process.myPid());

            RongIM.init(this);
        }

        //腾讯buly crash打印信息
        CrashReport.initCrashReport(getApplicationContext(), "89fb08b00d", true);
    }


    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    //socket
    public void doSocketOnAction() {
        Log.e("EMWApplication", "EMWApplication -----doSocketOnAction");
        if (!mSocket.connected()) {
            mSocket.connect();
        }
        mSocket.on(EMWApplication.SOCKET_EVENT_MSG, listener);
    }

    //事件监听处理事件
    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject object = (JSONObject) args[0];
            com.farsunset.cim.client.model.Message message = new Gson().fromJson(object.toString(), com.farsunset.cim.client.model.Message.class);
            Log.e("EMWApplication", "EMWApplication -----call-----");
            // Object o = new Gson().fromJson(object,)
            Message msg = myHandle.obtainMessage();
            msg.obj = message;
            msg.what = 0;
            myHandle.sendMessage(msg);
        }
    };

    public void doSocketOffAction() {
        Log.e("EMWApplication", "EMWApplication -----doSocketOffAction");
        if (mSocket != null) {
            mSocket.off(SOCKET_EVENT_MSG, listener);
        }
    }

    //显示提示对话框
    private void showHostOnlineAlert(com.farsunset.cim.client.model.Message msg) {
        //获取当前的Activity界面
        Activity activity = AppManager.currentActivity();
        if ((activity.getClass()).equals(ChatActivity.class)) {
            return;
        }
        //显示提示框
        //DialogSocket.createDialogSocket(activity, new DialogSocketBean("日志信息"));

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder3 = new Notification.Builder(activity);

        Intent intent3 = new Intent(activity, ChatActivity.class);
        if (msg.getGroupID() == 0) {
            intent3.putExtra("type", 1);
        } else {
            intent3.putExtra("type", 2);
        }
        intent3.putExtra("unReadNum", 1);
        intent3.putExtra("SenderID", msg.getSenderID());
        intent3.putExtra("start_anim", false);
        intent3.putExtra("GroupID", msg.getGroupID());
        //intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent3 = PendingIntent.getActivity(activity, 0, intent3, PendingIntent.FLAG_UPDATE_CURRENT);

        builder3.setContentIntent(pendingIntent3);
        builder3.setSmallIcon(R.drawable.ic_launcher);
        builder3.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        builder3.setAutoCancel(true);
        builder3.setContentTitle("消息通知");
        builder3.setContentText("您有新的消息，点击查看详情");
        builder3.setDefaults(Notification.DEFAULT_SOUND);
        //设置优先级
        //builder3.setVisibility(Notification.VISIBILITY_PUBLIC);
        builder3.setTicker("消息通知");
        builder3.setPriority(Notification.PRIORITY_MAX);

        //以下悬浮通知的方式，不支持几秒后悬浮效果自动消息，暂时弃用
        /*Intent XuanIntent = new Intent();
        XuanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        XuanIntent.setClass(activity, MainActivity.class);
        PendingIntent xuanpengdIntent = PendingIntent.getActivity(activity, 0, XuanIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder3.setFullScreenIntent(xuanpengdIntent, true);*/

        mNotificationManager.notify(2, builder3.build());
    }

    public static int getIconColor(int uid) {
        int color = colorMap.get(uid);
        if (color == 0) {
            Random random = new Random();
            color = random.nextInt(10) + 1;
            colorMap.put(uid, color);
        }
        return color;
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
        File audioFile = new File(audioPath);
        if (!audioFile.exists()) {
            audioFile.mkdirs();
        }
        File videoFile = new File(videoPath);
        if (!videoFile.exists()) {
            videoFile.mkdirs();
        }
    }

    //删除以前版本废弃的文件夹
    private void deleteDirectory() {
        try {
            String tempPath = Environment.getExternalStorageDirectory()
                    + File.separator + "ZKBRDir" + File.separator + "temp"
                    + File.separator;
            File tempFile = new File(tempPath);
            if (tempFile.exists()) {
                FileUtils.deleteDirectory(tempFile);
            }
            String audioPath = Environment.getExternalStorageDirectory()
                    + File.separator + "ZKBRDir" + File.separator + "audio"
                    + File.separator;
            File audioFile = new File(audioPath);
            if (audioFile.exists()) {
                FileUtils.deleteDirectory(audioFile);
            }
            String imagePath = Environment.getExternalStorageDirectory()
                    + File.separator + "ZKBRDir" + File.separator + "images"
                    + File.separator;
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                FileUtils.deleteDirectory(imageFile);
            }
            String videoPath = Environment.getExternalStorageDirectory()
                    + File.separator + "ZKBRDir" + File.separator + "video"
                    + File.separator;
            File videoFile = new File(videoPath);
            if (videoFile.exists()) {
                FileUtils.deleteDirectory(videoFile);
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    /**********************************************************************************************************************************************************************************
     *
     */
    /**
     * GreenDao chat模块数据库操作类    sunnydu  2017年3月3日
     */
    private static DaoSession daoSession;

    private void setupDatabase() {
        /**
         * 创建数据库
         */
        DaoMaster.DevOpenHelper helper = new MyOpenHelper(this, "EMWChat.db", null);

        /**
         * 获取可写数据库
         */
        SQLiteDatabase db = helper.getWritableDatabase();
        /**
         * 获取数据库对象
         */
        DaoMaster daoMaster = new DaoMaster(db);
        /**
         * 获取Dao对象管理者
         */
        daoSession = daoMaster.newSession();
    }

    /**
     * 获取数据库操作对象的公共方法
     */
    public static DaoSession getDaoInstant() {
        return daoSession;
    }

}
