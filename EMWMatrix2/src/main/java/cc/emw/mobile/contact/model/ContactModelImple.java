package cc.emw.mobile.contact.model;

import java.util.ArrayList;
import java.util.List;

import org.xutils.common.util.LogUtil;

import android.util.SparseArray;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;

public class ContactModelImple implements ContactModel {

    /**
     * 得到全部联系人列表
     */
    @Override
    public void getPersonList(String keyword, final OnContactListener listener) {
        API.UserAPI.SearchUser(keyword, 0, false,
                new RequestCallback<UserInfo>(UserInfo.class) {

                    @Override
                    public void onStarted() {
                        listener.onStart();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        listener.onFailure();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        listener.onFailure();
                    }

                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onParseSuccess(List<UserInfo> respList) {
                        listener.onSuccess(respList);
                    }
                });
    }

    /**
     * 得到我关注的联系人列表
     */
    @Override
    public void getFollowList(final OnContactListener listener) {
        API.UserAPI.SearchUser("", 0, true, new RequestCallback<UserInfo>(
                UserInfo.class) {

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFailure();
                LogUtil.d("onError");
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                listener.onSuccess(respList);
                LogUtil.d("success");
            }
        });
        /*
		 * RequestParams params = new RequestParams(HttpConstant.CONTACT_URL);
		 * params.addQueryStringParameter("follow", "true"); Callback.Cancelable
		 * cancelable = x.http().post(params, new
		 * RequestListener<UserInfo>(UserInfo.class) {
		 * 
		 * @Override public void onCancelled(CancelledException cex) {
		 * 
		 * }
		 * 
		 * @Override public void onError(Throwable ex, boolean isOnCallback) {
		 * listener.onFailure(); LogUtil.d("onError"); }
		 * 
		 * @Override public void onFinished() {
		 * 
		 * }
		 * 
		 * @Override public void onParseSuccess(List<UserInfo> respList) {
		 * listener.onSuccess(respList); LogUtil.d("success"); } });
		 */
    }

    /**
     * 添加关注
     */
    @Override
    public void addFollow(UserInfo mUser, final OnContactListener listener) {
        // RequestParams params = new RequestParams(HttpConstant.DOFOLLOW_URL);
        // try {
        // StringBody stringBody = new StringBody("fuids=[" + mUser.getID()
        // + "]&type=1", "UTF-8");
        // stringBody.setContentType("application/x-www-form-urlencoded");
        // params.setRequestBody(stringBody);
        // } catch (UnsupportedEncodingException e) {
        // e.printStackTrace();
        // }
        List<Integer> fuids = new ArrayList<Integer>();
        fuids.add(mUser.ID);
        API.UserAPI.DoFollow(fuids, 1,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onStarted() {
                        listener.onStart();
                    }

                    @Override
                    public void onCancelled(CancelledException arg0) {
                        listener.onFailure();
                    }

                    @Override
                    public void onParseSuccess(List<String> respList) {
                        super.onParseSuccess(respList);
                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        listener.onFailure();
                    }

                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        listener.onSuccess(arg0);
                    }

                    @Override
                    public void onLoading(long arg0, long arg1, boolean arg2) {
                    }

                    @Override
                    public void onWaiting() {
                    }
                });
		/*
		 * Callback.Cancelable cancelable = x.http().post(params, new
		 * Callback.ProgressCallback<String>() {
		 * 
		 * @Override public void onStarted() { listener.onStart(); }
		 * 
		 * @Override public void onCancelled(CancelledException arg0) {
		 * listener.onFailure(); }
		 * 
		 * @Override public void onError(Throwable arg0, boolean arg1) {
		 * listener.onFailure(); }
		 * 
		 * @Override public void onFinished() { }
		 * 
		 * @Override public void onSuccess(String arg0) {
		 * listener.onSuccess(arg0); }
		 * 
		 * @Override public void onLoading(long arg0, long arg1, boolean arg2) {
		 * }
		 * 
		 * @Override public void onWaiting() { } });
		 */
    }

    /**
     * 取消关注
     */
    @Override
    public void delFollow(UserInfo mUser, final OnContactListener listener) {

        List<Integer> fuids = new ArrayList<Integer>();
        fuids.add(mUser.ID);
        API.UserAPI.DoFollow(fuids, 2,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onStarted() {
                        listener.onStart();
                    }

                    @Override
                    public void onCancelled(CancelledException arg0) {
                        listener.onFailure();
                    }

                    @Override
                    public void onParseSuccess(List<String> respList) {
                        super.onParseSuccess(respList);
                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        listener.onFailure();
                    }

                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        listener.onSuccess(arg0);
                    }

                    @Override
                    public void onLoading(long arg0, long arg1, boolean arg2) {
                    }

                    @Override
                    public void onWaiting() {
                    }
                });
		/*
		 * RequestParams params = new RequestParams(HttpConstant.DOFOLLOW_URL);
		 * try { StringBody stringBody = new StringBody("fuids=[" +
		 * mUser.getID() + "]&type=2", "UTF-8");
		 * stringBody.setContentType("application/x-www-form-urlencoded");
		 * params.setRequestBody(stringBody); } catch
		 * (UnsupportedEncodingException e) { e.printStackTrace(); }
		 * Callback.Cancelable cancelable = x.http().post(params, new
		 * Callback.CommonCallback<String>() {
		 * 
		 * @Override public void onCancelled(CancelledException arg0) {
		 * listener.onFailure(); }
		 * 
		 * @Override public void onError(Throwable arg0, boolean arg1) {
		 * listener.onFailure(); }
		 * 
		 * @Override public void onFinished() { }
		 * 
		 * @Override public void onSuccess(String arg0) {
		 * listener.onSuccess(arg0); }
		 * 
		 * });
		 */
    }

    @Override
    public void doFollow(UserInfo mUser, String tips, OnContactListener listener) {

    }

    /**
     * 得到群主列表
     */
    @Override
    public void getGroupList(final OnContactListener listener) {
        API.TalkerAPI.LoadGroups("", true, 0, new RequestCallback<GroupInfo>(
                GroupInfo.class) {

            @Override
            public void onStarted() {
                if (listener != null)
                    listener.onStart();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (listener != null)
                    listener.onFailure();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (listener != null)
                    listener.onFailure();
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onParseSuccess(List<GroupInfo> respList) {
                if (listener != null)
                    listener.onGroupSuccess(respList);

                if (respList != null && respList.size() > 0) {
                    if (EMWApplication.groupMap == null) {
                        EMWApplication.groupMap = new SparseArray<GroupInfo>();
                    }
                    EMWApplication.groupMap.clear();
                    for (GroupInfo groupInfo : respList) {
                        EMWApplication.groupMap.put(groupInfo.ID,
                                groupInfo);
                    }
                }
            }
        });
        // RequestParam params = new RequestParam(HttpConstant.GROUP_URL);
        // Callback.Cancelable cancelable = x.http().post(params,
        // new RequestListener<GroupInfo>(GroupInfo.class) {
        //
        // @Override
        // public void onStarted() {
        // if (listener != null)
        // listener.onStart();
        // }
        //
        // @Override
        // public void onCancelled(CancelledException cex) {
        // if (listener != null)
        // listener.onFailure();
        // }
        //
        // @Override
        // public void onError(Throwable ex, boolean isOnCallback) {
        // if (listener != null)
        // listener.onFailure();
        // }
        //
        // @Override
        // public void onFinished() {
        // }
        //
        // @Override
        // public void onParseSuccess(List<GroupInfo> respList) {
        // if (listener != null)
        // listener.onGroupSuccess(respList);
        //
        // if (respList != null && respList.size() > 0) {
        // if (EMWApplication.groupMap == null) {
        // EMWApplication.groupMap = new HashMap<Integer, GroupInfo>();
        // }
        // EMWApplication.groupMap.clear();
        // for (GroupInfo groupInfo : respList) {
        // EMWApplication.groupMap.put(groupInfo.getID(),
        // groupInfo);
        // }
        // }
        // }
        // });
    }

}
