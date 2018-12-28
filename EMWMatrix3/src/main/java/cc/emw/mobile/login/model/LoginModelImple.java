package cc.emw.mobile.login.model;

import android.text.TextUtils;
import android.util.SparseArray;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.LogLongUtil;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.base.SocketRegistBean;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.XmlParseUtil;
import io.socket.client.Socket;

public class LoginModelImple implements LoginModel {

    @Override
    public void doCheckVersion(final int curVersion, final OnLoginListener listener) {
        if (PrefsUtil.isLoginCheckver()) {
            RequestParams params = new RequestParams(Const.VERSION_UPDATE_URL);
            /*SocketAddress sa = new InetSocketAddress("10.0.10.80", 8001);
            Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, sa);
			params.setProxy(proxy);*/
            Callback.Cancelable cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    listener.onVersionError();
                }

                @Override
                public void onFinished() {
                }

                @Override
                public void onSuccess(String result) {
                    Version ver = XmlParseUtil.parser(result);
                    if (ver != null && ver.versionCode > curVersion) { // 版本比较
                        listener.onVersionSuccess(ver);
                    } else {
                        listener.onVersionNewest();
                    }
                }
            });
        }
    }

    private Callback.Cancelable loginCancelable;

    @Override
    public void doLogin(final Socket mSocket, String username, String password, String comcode,
                        final OnLoginListener listener) {
        RequestParams params = new RequestParams(Const.LOGIN_URL);
        params.addBodyParameter("code", username);
        params.addBodyParameter("companyCode", comcode);
        params.addBodyParameter("password", password);
        loginCancelable = x.http().post(params, new RequestCallback<LoginResp>(LoginResp.class) {
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
                doRegSocket(mSocket, respInfo.User.ID);
                //内层bean的企业代码有错误，一直为zkbr，所以暂时使用外层的企业代码字段来赋值给内层CompanyCode
                respInfo.User.CompanyCode = respInfo.c;
                listener.onSuccess(respInfo);
            }
        });
    }

    //即使通讯注册方法,先http注册，再使用socket注册方法
    public void doRegSocket(final Socket mSocket, final int uid) {
        RequestParams params = new RequestParams(Const.REG_SOCKET_URL);
        params.addBodyParameter("uid", uid + "");
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogLongUtil.e("LoginModel", "---------------ex==" + ex.getMessage());
                ex.printStackTrace();
            }

            @Override
            public void onSuccess(String result) {
                // Socket socket = SingleIOSocket.getIoSocket();
                LogLongUtil.e("LoginModel", "-------" + result + "----------");
                SocketRegistBean registBean = new Gson().fromJson(result, SocketRegistBean.class);
                LogLongUtil.e("LoginModel", "-------------token---" + registBean.getToken());
                try {
                    if (mSocket != null) {
                        if (!mSocket.connected()) {
                            mSocket.connect();
                        }
                        if (mSocket != null) {
                            mSocket.emit(EMWApplication.SOCKET_EVENT_REG, registBean.getToken());
                        }
                    }
                } catch (Exception ex) {

                }
            }
        });
    }

    @Override
    public void cancelLogin() {
        if (loginCancelable != null && !loginCancelable.isCancelled()) {
            loginCancelable.cancel();
        }
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
                EMWApplication.personAIDLStr = new Gson().toJson(respList);
                for (UserInfo simpleUser : respList) {
                    EMWApplication.personMap.put(simpleUser.ID, simpleUser);
                }
                listener.onPersonSuccess();
                new Thread() {
                    @Override
                    public void run() {
                        sortData(respList);
                    }
                }.start();
            }
        });
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
