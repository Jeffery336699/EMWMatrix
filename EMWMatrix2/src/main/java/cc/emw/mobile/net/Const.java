package cc.emw.mobile.net;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xutils.x;
import org.xutils.common.Callback;

import android.net.Uri;
import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

public class Const {

	private static final String TAG = "Const";

	private static final String BASE_HOST = "http://10.0.10.59";
	private static final int LOGIN_PORT = 8081; //
	private static final int BASE_PORT = 8080; //59:8080  61:8082
	public static final String BASE_URL = BASE_HOST + ":" + BASE_PORT;
	
	/** 登录URL*/
	public static final String LOGIN_URL = BASE_HOST + ":" + LOGIN_PORT + "/account/signin";
	
	/** 下载头像URL*/
	public static final String DOWN_ICON_URL = BASE_URL + "/Resource/%s/UserImage/%s";
	/** 下载文件URL*/
	public static final String DOWN_FILE_URL = BASE_URL + "/Resource/%s/UserFile/%s";
	/** 下载任务附件URL*/
	public static final String DOWNLOAD_FILE = BASE_URL + "/DownLoad/";
	
	/** 上传文件URL*/
	public static final String UPLOAD_FILE_URL = BASE_URL + "/upload"; 
	/** 上传聊天图片URL*/
	public static final String UPLOAD_IMAGE_URL = BASE_URL + "/ImageUpload";
	/** 上传聊天音频URL*/
	public static final String UPLOAD_AUDIO_URL = BASE_URL + "/UploadAudio";
	// pay
	public static final String Host = "www.emw.cc:1000";
	public static final String HostAddress = "http://" + Host;
	public static final String Url_Client = HostAddress + "/Client.axd";
	
	public static Callback.Cancelable get(String url, RequestCallback<?> cb) {
		return get(url, null, cb);
	}

	public static Callback.Cancelable get(String url, Object arg, RequestCallback<?> cb) {
		if (arg != null) {
			if (arg instanceof Map) {
				url = url + "?" + joinMap((Map) arg, "&");
			} else if (arg instanceof List) {
//				url = url + "/" + joinList((ArrayList) arg, "/");
				url = url + "/" + TextUtils.join("/", (List)arg);
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
		Log.d(TAG, params.getUri());
		if ("GET".equalsIgnoreCase(method)) {
			cancelable = x.http().get(params, cb);
		} else if ("POST".equalsIgnoreCase(method)) {
			params.setStringBody(data);
			cancelable = x.http().post(params, cb);
		}
		return cancelable;
	}

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
				str = key + "=" + Uri.encode((String)obj, "UTF-8");
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
}
