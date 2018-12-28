package cc.emw.mobile.net;

import com.google.gson.Gson;

import org.xutils.http.RequestParams;
import org.xutils.http.body.StringBody;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.util.PrefsUtil;

/**
 * 请求参数
 * @author shaobo.zhuang
 *
 */
public class RequestParam extends RequestParams {
	
	public RequestParam(String uri) {
		super(uri);
		LoginResp loginResp = PrefsUtil.readLoginCookie();
//		String cookie = "un=" + loginResp.User.Name + "; u=" + loginResp.u
//				+ "; c=" + loginResp.c + "; s=" + loginResp.s + "; p="
//				+ loginResp.p;
		if (loginResp != null) {
			StringBuilder cookie = new StringBuilder();
			cookie.append("un=").append(loginResp.User.Code).append("; ");
			cookie.append("u=").append(loginResp.u).append("; ");
			cookie.append("c=").append(loginResp.c).append("; ");
			cookie.append("s=").append(loginResp.s).append("; ");
			cookie.append("p=").append(loginResp.p);
			setHeader("Cookie", cookie.toString());
		}
	}

	/**
	 * 设置请求的参数
	 * @param body
	 */
	public void setStringBody(String body) {
		try {
			StringBody stringBody = new StringBody(body, "UTF-8");
			stringBody.setContentType("application/x-www-form-urlencoded");
			setRequestBody(stringBody);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置请求的参数
	 * @param params
	 */
	public void setStringParams(Object... params) {
		if (params != null) {
			Gson gson = new Gson();
			ArrayList<String> args = new ArrayList<String>();
			for (Object obj : params) {
//				if (obj instanceof String) {
//					args.add(String.valueOf(obj));
//				} else {
				args.add(gson.toJson(obj));
//				}
			}
			try {
				StringBody stringBody = new StringBody(gson.toJson(args), "UTF-8");
				stringBody.setContentType("application/x-www-form-urlencoded");
				setRequestBody(stringBody);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 设置空的参数：[]
	 * @param params
	 */
	/*public void setEmptyParams() {
		Gson gson = new Gson();
		ArrayList<String> args = new ArrayList<String>();
		setBodyContent(gson.toJson(args));
		LogUtil.d(gson.toJson(args));
//		try {
//			setBodyEntity(new StringEntity(gson.toJson(args), HTTP.UTF_8));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
	}*/
	
	/**
	 * 设置请求的参数
	 * @param params
	 */
	/*public void setStringParams(Object... params) {
		if (params != null) {
			Gson gson = new Gson();
			ArrayList<String> args = new ArrayList<String>();
			for (Object obj : params) {
//				if (obj instanceof String) {
//					args.add(String.valueOf(obj));
//				} else {
					args.add(gson.toJson(obj));
//				}
			}
			setBodyContent(gson.toJson(args));
			LogUtil.d(gson.toJson(args));
//			try {
//				setBodyEntity(new StringEntity(gson.toJson(args), HTTP.UTF_8));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
		}
	}*/
	
	/**
	 * 设置请求查询的SQL语句
	 * @param sql
	 */
	/*public void setSQLQueryParams(String sql) {
		if (sql != null) {
			setBodyContent(sql);
			LogUtil.d(sql);
//			try {
//				setBodyEntity(new StringEntity(sql, HTTP.UTF_8));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
		}
	}*/
	
	/**
	 * 设置请求执行的SQL语句
	 * @param sqls
	 */
	/*public void setSQLExcuteParams(ArrayList<String> sqls) {
		if (sqls != null && sqls.size() > 0) {
			Gson gson = new Gson();
			ArrayList<String> args = new ArrayList<String>();
			args.add(gson.toJson(sqls));
			setBodyContent(gson.toJson(args));
			LogUtil.d(gson.toJson(args));
//			try {
//				setBodyEntity(new StringEntity(gson.toJson(args), HTTP.UTF_8));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
		}
	}*/
}
