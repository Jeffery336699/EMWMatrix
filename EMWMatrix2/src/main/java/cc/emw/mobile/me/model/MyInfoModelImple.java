package cc.emw.mobile.me.model;

import java.util.List;

import org.xutils.common.util.LogUtil;

import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.me.presenter.MyInfoListener;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;

/**
 * @author zrjt
 * @version 2016-3-12 下午4:42:03
 */
public class MyInfoModelImple implements MyInfoModel {

	@Override
	public void showCollect() {
		// TODO Auto-generated method stub
	}

	/**
	 * 显示联系人信息
	 */
	@Override
	public void showConcernInfo(String key, final MyInfoListener listener) {
		API.UserAPI.SearchUser(key, 0, false, new RequestCallback<UserInfo>(
				UserInfo.class) {

			@Override
			public void onCancelled(CancelledException arg0) {
				listener.onFailure(arg0.toString());
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				listener.onFailure(arg0.toString());
			}

			@Override
			public void onParseSuccess(List<UserInfo> respList) {
				super.onParseSuccess(respList);
				listener.onSuccess(respList);
			}

			@Override
			public void onFinished() {
			}

		});
		// RequestParams params = new RequestParams(HttpConstant.CONTACT_URL);
		// params.addBodyParameter("key", key);
		// Callback.Cancelable cancelable = x.http().post(params,
		// new RequestListener<UserInfo>(UserInfo.class) {
		//
		// @Override
		// public void onCancelled(CancelledException arg0) {
		// listener.onFailure(arg0.toString());
		// }
		//
		// @Override
		// public void onError(Throwable arg0, boolean arg1) {
		// listener.onFailure(arg0.toString());
		// }
		//
		// @Override
		// public void onParseSuccess(List<UserInfo> respList) {
		// super.onParseSuccess(respList);
		// listener.onSuccess(respList);
		// }
		//
		// @Override
		// public void onFinished() {
		// }
		//
		// });
	}

	// 获取关注的列表
	@Override
	public void getFollowList(final MyInfoListener listener) {
		API.UserAPI.SearchUser("", 0, true, new RequestCallback<UserInfo>(
				UserInfo.class) {

			@Override
			public void onCancelled(CancelledException cex) {

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				listener.onFailure(ex.toString());
				LogUtil.d("onError");
			}

			@Override
			public void onFinished() {
				listener.onFinish();
			}

			@Override
			public void onParseSuccess(List<UserInfo> respList) {
				listener.onSuccess(respList);
				LogUtil.d("success");
			}
		});
		// RequestParams params = new RequestParams(HttpConstant.CONTACT_URL);
		// params.addQueryStringParameter("follow", "true");
		// Callback.Cancelable cancelable = x.http().post(params,
		// new RequestListener<UserInfo>(UserInfo.class) {
		//
		// @Override
		// public void onCancelled(CancelledException cex) {
		//
		// }
		//
		// @Override
		// public void onError(Throwable ex, boolean isOnCallback) {
		// listener.onFailure(ex.toString());
		// LogUtil.d("onError");
		// }
		//
		// @Override
		// public void onFinished() {
		// listener.onFinish();
		// }
		//
		// @Override
		// public void onParseSuccess(List<UserInfo> respList) {
		// listener.onSuccess(respList);
		// LogUtil.d("success");
		// }
		// });
	}

	/**
	 * 获取我发布的、我收藏的、我关注的、相关到我的 数量
	 */
	@Override
	public void getMyInfoConut(final MyInfoListener listener) {
		API.TalkerAPI
				.GetMyTalkCount(new RequestCallback<Integer>(Integer.class) {

					@Override
					public void onCancelled(CancelledException arg0) {
						listener.onFailure(arg0.toString());
					}

					@Override
					public void onError(Throwable arg0, boolean arg1) {
						listener.onFailure(arg0.toString());
					}

					@Override
					public void onFinished() {
						listener.onFinish();
					}

					@Override
					public void onParseSuccess(List<Integer> respInfo) {
						super.onParseSuccess(respInfo);
						listener.onMyInfoCountSuccess(respInfo);
					}

				});
		// RequestParams params = new RequestParams(HttpConstant.MYINFO_COUNT);
		// Callback.Cancelable cancelable = x.http().get(params,
		// new RequestListener<Integer>(Integer.class) {
		//
		// @Override
		// public void onCancelled(CancelledException arg0) {
		// listener.onFailure(arg0.toString());
		// }
		//
		// @Override
		// public void onError(Throwable arg0, boolean arg1) {
		// listener.onFailure(arg0.toString());
		// }
		//
		// @Override
		// public void onFinished() {
		// listener.onFinish();
		// }
		//
		// @Override
		// public void onParseSuccess(List<Integer> respInfo) {
		// super.onParseSuccess(respInfo);
		// listener.onMyInfoCountSuccess(respInfo);
		// }
		//
		// });
	}

	/**
	 * 获取所有UserNote的信息
	 */
	@Override
	public void getMyReleaseInfo(int type, int id, int pages, int size,
			final MyInfoListener listener) {
		API.TalkerAPI.LoadTalker(type, id, pages, size,
				new RequestCallback<UserNote>(UserNote.class) {
					@Override
					public void onCancelled(CancelledException arg0) {
						listener.onFailure(arg0.toString());
					}

					@Override
					public void onError(Throwable arg0, boolean arg1) {
						listener.onFailure(arg0.toString());
					}

					@Override
					public void onFinished() {
						listener.onFinish();
					}

					@Override
					public void onParseSuccess(List<UserNote> respList) {
						super.onParseSuccess(respList);
						listener.onMyReleaseInfoSuccess(respList);
					}
				});
		// RequestParam params = new RequestParam(
		// HttpConstant.ALLRECORD_TALKER_USER);
		// try {
		// StringBody stringBody = new StringBody("loadtype=" + type
		// + "&id=0&s=" + pages + "&size=10&userid=" + uid, "UTF_8");
		// stringBody.setContentType("application/x-www-form-urlencoded");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		/*
		 * params.addBodyParameter("loadtype", type);
		 * params.addBodyParameter("id", id); params.addBodyParameter("s",
		 * pages); params.addBodyParameter("size", size);
		 * params.addBodyParameter("userid", uid); Callback.Cancelable
		 * cancelable = x.http().get(params, new
		 * RequestListener<UserNote>(UserNote.class) {
		 * 
		 * @Override public void onCancelled(CancelledException arg0) {
		 * listener.onFailure(arg0.toString()); }
		 * 
		 * @Override public void onError(Throwable arg0, boolean arg1) {
		 * listener.onFailure(arg0.toString()); }
		 * 
		 * @Override public void onFinished() { listener.onFinish(); }
		 * 
		 * @Override public void onParseSuccess(List<UserNote> respList) {
		 * super.onParseSuccess(respList);
		 * listener.onMyReleaseInfoSuccess(respList); } });
		 */
	}
}
