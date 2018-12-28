package cc.emw.mobile.net;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.x;

public class Page {
	
	public static Callback.Cancelable pageRequest(int pageID, String getdata, Object postdata, RequestCallback<?> cb) {
	    String url = "/Page/" + pageID + "/" + getdata;
	    String data = "";
	    if (postdata != null) {
	    	data = new Gson().toJson(postdata);
	    }
//	    return Const.request(url, data, cb, "POST");
	    return request(url, data, cb, "POST");
	}
	
	public static Callback.Cancelable request(String url, String data,
			final RequestCallback<?> cb, String method) {
		Callback.Cancelable cancelable = null;
		RequestParam params = new RequestParam(Const.BASE_URL + url);
		if ("GET".equalsIgnoreCase(method)) {
			cancelable = x.http().get(params, cb);
		} else if ("POST".equalsIgnoreCase(method)) {
			params.setStringBody(data);
			cancelable = x.http().post(params, cb);
		}
		return cancelable;
	}
}
