package cc.emw.mobile.login.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.text.TextUtils;
import android.util.SparseArray;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.XmlParseUtil;

public class LoginModelImple implements LoginModel {

	@Override
	public void doCheckVersion(final int curVersion, final OnLoginListener listener) {
		if (PrefsUtil.isLoginCheckver()) {
			RequestParams params = new RequestParams("http://www.emw.cc:1000/client/apkversion.xml");
			/*SocketAddress sa = new InetSocketAddress("10.0.10.80", 8001);
			Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, sa);
			params.setProxy(proxy);*/
			Callback.Cancelable cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
				@Override
				public void onCancelled(CancelledException cex) {
				}
				@Override
				public void onError(Throwable ex, boolean isOnCallback) {
				}
				@Override
				public void onFinished() {
				}
				@Override
				public void onSuccess(String result) {
					Version ver = XmlParseUtil.parser(result);
					if (ver != null && ver.versionCode > curVersion) { // 版本比较
						listener.onVersionSuccess(ver);
					}
				}
			});
		}
	}
	
	@Override
	public void doLogin(String username, String password, String comcode,
			final OnLoginListener listener) {
		RequestParams params = new RequestParams(Const.LOGIN_URL);
		params.addQueryStringParameter("code", username);
		params.addQueryStringParameter("companyCode", comcode);
		params.addQueryStringParameter("password", password);
		Callback.Cancelable cancelable = x.http().post(params, new RequestCallback<LoginResp>(LoginResp.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (!TextUtils.isEmpty(ex.getMessage())
						&& TextUtils.isDigitsOnly(ex.getMessage())
						&& Integer.valueOf(ex.getMessage()) == 0) {
					listener.onNameOrPwdError();
				} else {
					listener.onFailure();
				}
			}
			@Override
			public void onParseSuccess(LoginResp respInfo) {
				listener.onSuccess(respInfo);
			}
		});
	}

	@Override
	public void getPersonList(final OnLoginListener listener) {
		API.UserAPI.SearchUser("", 0, false, new RequestCallback<UserInfo>(UserInfo.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				listener.onPersonSuccess();
			}
			@Override
			public void onParseSuccess(final List<UserInfo> respList) {
				if (EMWApplication.personMap == null) {
					EMWApplication.personMap = new SparseArray<UserInfo>();
				}
				for (UserInfo simpleUser : respList) {
					EMWApplication.personMap.put(simpleUser.ID, simpleUser);
				}
				listener.onPersonSuccess();
				new Thread(){
					@Override
					public void run() {
						sortData(respList);
					}
				}.start();
			}
		});
		/*RequestParam params = new RequestParam(HttpConstant.CONTACT_URL);
		Callback.Cancelable cancelable = x.http().post(params, new RequestListener<UserInfo>(UserInfo.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				listener.onPersonSuccess();
			}
			@Override
			public void onParseSuccess(List<UserInfo> respList) {
				if (EMWApplication.personList == null) {
					EMWApplication.personList = new ArrayList<UserInfo>();
				}
				EMWApplication.personList.clear();
				EMWApplication.personList.addAll(respList);
				if (EMWApplication.personMap == null) {
					EMWApplication.personMap = new HashMap<Integer, UserInfo>();
				}
				for (UserInfo simpleUser : respList) {
					EMWApplication.personMap.put(simpleUser.ID, simpleUser);
				}
				listener.onPersonSuccess();
			}
		});*/
	}

	
	private void sortData(List<UserInfo> respList) {
		CharacterParser characterParser = CharacterParser.getInstance();
		for (int i = 0; i < respList.size(); i++) {
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(respList.get(i).Name);
			if (!TextUtils.isEmpty(pinyin)) {
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					respList.get(i).setSortLetters(sortString.toUpperCase());
				} else {
					respList.get(i).setSortLetters("#");
				}
			} else {
				respList.get(i).setSortLetters("#");
			}
		}
		PinyinComparator pinyinComparator = new PinyinComparator();
		Collections.sort(respList, pinyinComparator);// 根据a-z进行排序源数据
		if (EMWApplication.personSortList == null) {
			EMWApplication.personSortList = new ArrayList<UserInfo>();
		}
		EMWApplication.personSortList.clear();
		EMWApplication.personSortList.addAll(respList);
	}
}
