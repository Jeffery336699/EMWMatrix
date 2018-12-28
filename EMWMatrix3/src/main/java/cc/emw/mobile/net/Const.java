package cc.emw.mobile.net;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cc.emw.mobile.LogLongUtil;

public class Const {

    private static final String TAG = "Const";
    //连接26
    /*private static final String BASE_HOST = "http://10.0.10.26";
    private static final int LOGIN_PORT = 8080; //59,61:8081  80:8001
    private static final int BASE_PORT = 8081; //59:8080  61:8082  80:8000*/

    //连接121
    /*private static final String BASE_HOST = "http://10.1.1.121";
    private static final int LOGIN_PORT = 8001;
    private static final int BASE_PORT = 8000;*/

    //连接52
    /*private static final String BASE_HOST = "http://10.0.10.52";
    private static final int LOGIN_PORT = 8001;
    private static final int BASE_PORT = 8000;*/

    //连接62
    /*private static final String BASE_HOST = "http://10.0.10.62";
    private static final int LOGIN_PORT = 8001;
    private static final int BASE_PORT = 80;*/

    //连接59
    /*private static final String BASE_HOST = "http://10.0.10.59";
    private static final int LOGIN_PORT = 8081;
    private static final int BASE_PORT = 8080;*/


    //IO Socket 测试
    //内网
    //public static final String SOCKET_URL = "http://10.0.10.25:8080";
    //外网
    //public static final String SOCKET_URL = "http://119.23.61.38:8088";
    public static final String SOCKET_URL = "https://www.aiemw.com:8088";
    //public static final String SOCKET_URL = "https://www.huazhicloud.net:8080";
    //Socket 账号注册
    //内网
    //public static final String REG_SOCKET_URL = "http://10.0.10.25:8080/register";
    //外网
    public static final String REG_SOCKET_URL = "https://www.aiemw.com:8088/register";
    //public static final String REG_SOCKET_URL = "http://119.23.61.38:8088/register";
    // public static final String REG_SOCKET_URL = "https://www.huazhicloud.net:8080/register";
    //连接内网测试
    //private static final String BASE_HOST = "http://10.0.10.25";
    //private static final String BASE_HOST ="ws://10.0.10.25";
    //private static final int BASE_PORT = 80;
    //private static final int BASE_PORT = 8999;

    //连接外网
    //private static final String BASE_HOST = "https://matrix.aiemw.com";
    //private static final String BASE_HOST = "https://120.79.59.130";

    private static final String BASE_HOST = "https://www.aiemw.com";
    private static final int BASE_PORT = 5678;

    public static final String BASE_URL = BASE_HOST + ":" + BASE_PORT;

    /**
     * 登录URL
     */
    //    public static final String LOGIN_URL = BASE_HOST + ":" + LOGIN_PORT + "/account/signin";
    //    public static final String LOGIN_URL = "https://www.aiemw.com/account/signin";
    public static final String LOGIN_URL = "http://120.79.59.130:8080/account/signin";
    /**
     * 最新版本信息URL
     */
    public static final String VERSION_UPDATE_URL = BASE_URL + "/Client/apkversion.xml";
    /**
     * 下载头像URL
     */
    public static final String DOWN_ICON_URL = BASE_URL + "/Resource/%s/UserImage/%s";
    public static final String DOWN_ICON_URL2 = BASE_URL;
    /**
     * 下载二维码URL
     */
    public static final String DOWN_RQCODE_URL = BASE_URL + "/Resource/%s/UploadRqCode/%s";
    /**
     * 下载文件URL
     */
    public static final String DOWN_FILE_URL = BASE_URL + "/Resource/%s/UserFile/%s";
    public static final String DOWN_FILE_URL2 = BASE_URL + "/Resource";
    /**
     * 下载任务附件URL
     */
    public static final String DOWNLOAD_FILE = BASE_URL + "/DownLoad/";
    /**
     * 上传文件URL
     */
    public static final String UPLOAD_FILE_URL = BASE_URL + "/upload";
    /**
     * 上传聊天图片URL
     */
    public static final String UPLOAD_IMAGE_URL = BASE_URL + "/ImageUpload";
    /**
     * 上传聊天音频URL
     */
    public static final String UPLOAD_AUDIO_URL = BASE_URL + "/UploadAudio";
    /**
     * 上传聊天视频URL
     */
    public static final String UPLOAD_VIDEO_URL = BASE_URL + "/UploadVideo";
    /**
     * 上传二维码
     */
    public static final String UPLOAD_CQ_CODE_URL = BASE_URL + "/UploadRqCode";
    // pay
    public static final String Host = "www.emw.cc:1000";
    public static final String HostAddress = "http://" + Host;
    public static final String Url_Client = HostAddress + "/Client.axd";


    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Callback.Cancelable get(String url, RequestCallback<?> cb) {
        return get(url, null, cb);
    }

    public static Callback.Cancelable get(String url, Object arg, RequestCallback<?> cb) {
        if (arg != null) {
            if (arg instanceof Map) {
                url = url + "?" + joinMap((Map) arg, "&");
            } else if (arg instanceof List) {
                //				url = url + "/" + joinList((ArrayList) arg, "/");
                url = Uri.encode(url + "/" + TextUtils.join("/", (List) arg), "UTF-8");
            }
        }

        return request(url, null, cb, "GET");
    }

    public static Callback.Cancelable post(String url, RequestCallback<?> cb) {
        return post(url, null, cb);
    }

    public static Callback.Cancelable post(String url, Object arg, RequestCallback<?> cb) {
        String str = "";
        if (arg != null) {
            if (arg instanceof Map) {
                str = joinMap((Map) arg, "&");
            }
        }

        return request(url, str, cb, "POST");
    }

    public static Callback.Cancelable request(String url, String data,
                                              final RequestCallback<?> cb, String method) {
        Callback.Cancelable cancelable = null;
        RequestParam params = new RequestParam(BASE_URL + "/" + url);
        Log.d(TAG, "=" + params.getUri() + "  method=" + method);
        //取消ssl
       /* s_sSLContext = getSSLContext(mContext);
        Log.d(TAG, "s_sSLContext="+s_sSLContext.toString());
        if (s_sSLContext != null) {
            params.setSslSocketFactory(s_sSLContext.getSocketFactory());
            Log.d(TAG, s_sSLContext.getSocketFactory().toString());
        }
        */
        if ("GET".equalsIgnoreCase(method)) {
            cancelable = x.http().get(params, cb);
        } else if ("POST".equalsIgnoreCase(method)) {
            params.setStringBody(data);
            LogLongUtil.e(TAG, "body= " + data);
            //Log.e(TAG,"body data = "+data);
            cancelable = x.http().post(params, cb);
        }
        return cancelable;
    }

    //数据包body,进行UTF-8编码
    private static String joinMap(Map<String, Object> s, String delimiter) {

        if (s.isEmpty())
            return "";

        Gson gson = new Gson();
        Iterator<Map.Entry<String, Object>> iter = s.entrySet().iterator();
        StringBuilder buffer = new StringBuilder();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String str = "";
            String key = Uri.encode(entry.getKey(), "UTF-8");
            Object obj = entry.getValue();
            if (obj instanceof String) {
                str = key + "=" + Uri.encode((String) obj, "UTF-8");
            } else {
                str = key + "=" + Uri.encode(gson.toJson(obj), "UTF-8");
            }
            buffer.append(delimiter).append(str);
        }

        return buffer.toString().replaceFirst("&", "");
    }

    private static String joinList(AbstractCollection<Object> s,
                                   String delimiter) {

        if (s.isEmpty())
            return "";

        Iterator<Object> iter = s.iterator();
        StringBuilder buffer = new StringBuilder(String.valueOf(iter.next()));

        while (iter.hasNext())
            buffer.append(delimiter).append(iter.next());

        return buffer.toString();
    }

    /**
     * Https 证书验证对象
     */
    private static SSLContext s_sSLContext = null;

    /**
     * 获取Https的证书
     *
     * @param context Activity（fragment）的上下文
     * @return SSL的上下文对象
     */
    public static SSLContext getSSLContext(Context context) {
        if (null != s_sSLContext) {
            return s_sSLContext;
        }

        //以下代码来自百度 参见http://www.tuicool.com/articles/vmUZf2
        CertificateFactory certificateFactory = null;

        InputStream inputStream = null;
        KeyStore keystore = null;
        String tmfAlgorithm = null;
        TrustManagerFactory trustManagerFactory = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            inputStream = context.getAssets().open("ztbr.cer");//这里导入SSL证书文件
            //            inputStream = context.getAssets().open("51p2b_server_bs.pem");//这里导入SSL证书文件

            Certificate ca = certificateFactory.generateCertificate(inputStream);

            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", ca);

            tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
            trustManagerFactory.init(keystore);

            // Create an SSLContext that uses our TrustManager
            s_sSLContext = SSLContext.getInstance("TLS");
            //s_sSLContext.init(null, trustManagerFactory.getTrustManagers(), null);
            //信任所有证书 （官方不推荐使用）
            s_sSLContext.init(null, new TrustManager[]{new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {

                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {

                }
            }}, null);
            return s_sSLContext;
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
