package cc.emw.mobile.dynamic.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.dynamic.PlanDetailActivity;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.WaitHandleInfo;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * 工作计划项计划列表Adapter
 * 
 * @author shaobo.zhuang
 */
public class DynamicPlanAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<UserPlan> mDataList;
	private UserNote mUserNote;
	private boolean isShowState; // 是否显示已完成复选框
	private ArrayList<WaitHandleInfo> mWaitList;
	private Dialog mLoadingDialog; // 加载框
	private DisplayImageOptions options;

	public DynamicPlanAdapter(Context context, UserNote un) {
		this(context, un, false);
	}

	public DynamicPlanAdapter(Context context, UserNote un, boolean isShowState) {
		this.mContext = context;
		this.mDataList = un.info.log;
		this.mUserNote = un;
		this.isShowState = isShowState;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
	}

	public void setWaitList(List<WaitHandleInfo> waitList) {
		if (mWaitList == null)
			mWaitList = new ArrayList<WaitHandleInfo>();
		mWaitList.clear();
		mWaitList.addAll(waitList);
	}

	@Override
	public int getCount() {
		return mDataList != null ? mDataList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.dynamic_item_plan_listitem, null);
			vh.colorCiv = (CircleImageView) convertView.findViewById(R.id.civ_dynamicplan_color);
			vh.taskTv = (TextView) convertView
					.findViewById(R.id.tv_dynamicplan_content);
			vh.timeTv = (TextView) convertView
					.findViewById(R.id.tv_dynamicplan_finishtime);
			vh.stateCb = (CheckBox) convertView
					.findViewById(R.id.cb_dynamicplan_state);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		final UserPlan plan = mDataList.get(position);
		final SpannableString spanString = new SpannableString(position + 1
				+ ". " + plan.Name);
		final StrikethroughSpan span = new StrikethroughSpan();
//		vh.taskTv.setText(spanString);
		vh.colorCiv.setImageResource(HelpUtil.getColorForPosition(position));
		vh.taskTv.setText(plan.Name);
		vh.timeTv.setText(plan.EndTime);

		if (isShowState) {
			if (mWaitList != null) {
				int j = 0;
				for (int i = 0; i < mWaitList.size(); i++) {
					WaitHandleInfo wait = mWaitList.get(i);
					if (wait.NoteID == mUserNote.ID) {
						if (position == j) { // 根据顺序对应
							plan.ID = wait.ID;
							plan.FinishState = wait.State;
							break;
						}
						j++;
					}
				}
			}
			if (plan.FinishState == 2) {
				spanString.setSpan(span, 0, spanString.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				vh.taskTv.setText(spanString); // 添加中划线
			} else {
				spanString.removeSpan(span);
				vh.taskTv.setText(spanString); // 取消中划线
			}
			vh.stateCb.setChecked(plan.FinishState == 2 ? true : false);
			vh.stateCb
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								spanString.setSpan(span, 0,
										spanString.length(),
										Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								vh.taskTv.setText(spanString); // 添加中划线
							} else {
								spanString.removeSpan(span);
								vh.taskTv.setText(spanString); // 取消中划线
							}
						}
					});
			vh.stateCb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finishWait(plan, vh.stateCb);
				}
			});
			vh.stateCb.setVisibility(View.VISIBLE);
		} else {
			vh.stateCb.setVisibility(View.GONE);
		}
		convertView.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View v) {
				Intent intent = new Intent(mContext, PlanDetailActivity.class);
				intent.putExtra("plan_list", mDataList);
				intent.putExtra("start_anim", false);
				int[] location = new int[2];
				v.getLocationInWindow(location);
				intent.putExtra("click_pos_y", location[1]);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		CircleImageView colorCiv;
		TextView taskTv;
		TextView timeTv;
		CheckBox stateCb;
	}

	/**
	 * 已完成或未完成
	 */
	private void finishWait(final UserPlan plan, final CheckBox checkBox) {
		/*
		 * HttpUtils http = new HttpUtils();
		 * http.configCookieStore(PrefsUtil.readLoginCookie()); RequestParam
		 * param = new RequestParam(); final int state = plan.State == 2? 1:2;
		 * param.setStringParams(String.valueOf(plan.ID), state);
		 * http.send(HttpMethod.POST,
		 * HttpConst.Url_Client+"?t=Chatter.TalkerAccess&m=SetMyActivitiesById",
		 * param, new RequestCallBack<String>() {
		 * 
		 * @Override public void onStart() { mLoadingDialog =
		 * createLoadingDialog("正在处理..."); mLoadingDialog.show(); }
		 * 
		 * @Override public void onSuccess(ResponseInfo<String> responseInfo) {
		 * LogUtils.d("send:"+responseInfo.result); mLoadingDialog.dismiss(); if
		 * (responseInfo.result != null && !"0".equals(responseInfo.result)) {
		 * Intent intent = new Intent(HomeFragment.ACTION_REFRESH_HOME_LIST);
		 * sendBroadcast(intent); // 刷新首页列表 Intent intent = new
		 * Intent(ScheduleFragment.ACTION_REFRESH_SCHEDULE_LIST);
		 * mContext.sendBroadcast(intent); // 刷新日程列表
		 * 
		 * plan.State = state; } else { Toast.makeText(mContext, "操作失败",
		 * Toast.LENGTH_SHORT).show(); } }
		 * 
		 * @Override public void onFailure(HttpException error, String msg) {
		 * mLoadingDialog.dismiss(); checkBox.setChecked(checkBox.isChecked() ?
		 * false : true); Toast.makeText(mContext,
		 * error.getExceptionCode()+" "+msg, Toast.LENGTH_SHORT).show(); } });
		 */
	}

	/**
	 * 加载对话框
	 * 
	 * @param msg
	 *            提示信息
	 * @return
	 */
	public Dialog createLoadingDialog(String msg) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.loading_dialog, null);
		TextView tipTextView = (TextView) v.findViewById(R.id.loading_tv_tip);
		tipTextView.setText(msg);
		Dialog loadingDialog = new Dialog(mContext, R.style.loading_dialog);
		loadingDialog.setCancelable(true);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return loadingDialog;
	}
}
