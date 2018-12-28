package cc.emw.mobile.dynamic.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zf.iosdialog.widget.AlertDialog;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarEditActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.GroupSelectActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.ShareToActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.UserNoteFile;
import cc.emw.mobile.entity.UserNote.UserNoteLink;
import cc.emw.mobile.entity.UserNote.UserNoteShareTo;
import cc.emw.mobile.entity.UserNote.UserNoteVote;
import cc.emw.mobile.entity.UserNote.UserRootVote;
import cc.emw.mobile.main.PhotoActivity;
import cc.emw.mobile.main.fragment.talker.DynamicFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.net.ApiEntity.UserSchedule;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.FlowLayout;

/**
 * 首页项详情Adapter
 * @author shaobo.zhuang
 */
public class DynamicDetailAdapter extends BaseAdapter {
	
	private static final int TYPE_ITEM_COUNT = 3; //不同item的数量
	private static final int TYPE_ITEM_TOP = 0;   //顶部的item布局
	private static final int TYPE_ITEM_RECE = 1;  //接收的item布局
    private static final int TYPE_ITEM_SEND = 2;  //发送的item布局
	
	private Context mContext;
	private UserNote mUserNote;
	private ArrayList<UserNote> mAllList;
	private ArrayList<UserNote> mDataList;
	private Dialog mLoadingDialog;
	private DisplayImageOptions options;
	private SparseBooleanArray voteMap; //标记是否需要请求投票记录, 代替HashMap<Integer, Boolean>性能更优
	private EditText mContentEt;
	
	public DynamicDetailAdapter(Context context,
			ArrayList<UserNote> dataList, EditText contentEt) {
		this.mContext = context;
		this.mAllList = new ArrayList<UserNote>();
		this.mDataList = dataList;
		voteMap = new SparseBooleanArray();
		mContentEt = contentEt;
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//		.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
		.build(); // 创建配置过得DisplayImageOption对象
	}
	
	public void setTopData(UserNote un) {
		mUserNote = un;
	}
	
	public void setAllData(List<UserNote> dataList) {
		if (dataList != null) 
			mAllList.addAll(dataList);
	}
	
	public List<UserNote> getAllData() {
		return mAllList;
	}
	
	public void add(ArrayList<UserNote> dataList) {
		if (dataList != null) 
			mDataList.addAll(dataList);
		notifyDataSetChanged();
	}
	
	public SparseBooleanArray getVoteMap() {
		return voteMap;
	}
	public void clearVoteMap() {
        if (voteMap != null)
        	voteMap.clear();
    }
	
	@Override
	public int getCount() {
		return mDataList != null ? mDataList.size() + 1 : 1;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return mUserNote;
		} else {
			return mDataList.get(position -1);
		}
		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getViewTypeCount() {
		return TYPE_ITEM_COUNT;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_ITEM_TOP;
		} else {
			UserInfo user = PrefsUtil.readUserInfo();
			return mDataList.get(position-1).UserID==user.ID?TYPE_ITEM_SEND:TYPE_ITEM_RECE;
		}
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TopViewHolder tvh = null;
		ViewHolder vh = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
				case TYPE_ITEM_TOP:
					tvh = new TopViewHolder();
					convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamicdetail_top, null);
					tvh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_dynamicdetail_tophead);
					tvh.nameTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_topname);
					tvh.contentTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_topcontent);
					tvh.circleProgress = (CircleProgressView) convertView.findViewById(R.id.circleView);
					tvh.timeTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_toptime);
					//文件、链接、投票、日程、工作分派、工作计划根Layout
					tvh.otherLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamicdetail_topother);
					//图片集的根Layout
					tvh.imageLayout = (FlowLayout) convertView.findViewById(R.id.ll_dynamicdetail_topimage);
					tvh.discussTv = (TextView) convertView.findViewById(R.id.tv_dynamic_discuss);
					tvh.collectTv = (TextView) convertView.findViewById(R.id.tv_dynamic_collect);
					tvh.shareTv = (TextView) convertView.findViewById(R.id.tv_dynamic_share);
					convertView.setTag(tvh);
					break;
				case TYPE_ITEM_RECE:
				case TYPE_ITEM_SEND:
					vh = new ViewHolder();
					convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamicdetail_discuss, null);
					vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_dynamicdetail_head);
					vh.timeTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_time);
					vh.nameTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_name);
					vh.contentTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_content);
					vh.replyBtn = (Button) convertView.findViewById(R.id.btn_dynamicdetail_reply);
					vh.replyLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamicdetail_reply);
					convertView.setTag(vh);
					break;
			}
			
		} else {
			switch (type) {
				case TYPE_ITEM_TOP:
					tvh = (TopViewHolder) convertView.getTag();
					break;
				case TYPE_ITEM_RECE:
				case TYPE_ITEM_SEND:
					vh = (ViewHolder) convertView.getTag();
					break;
			}
		}
		
		switch (type) {
			case TYPE_ITEM_TOP:
				tvh.otherLayout.removeAllViews();
				tvh.imageLayout.removeAllViews();
				tvh.circleProgress.setVisibility(View.GONE);
				if (mUserNote != null) {
					if (EMWApplication.personMap != null && EMWApplication.personMap.get(mUserNote.UserID) != null) {
						String image = EMWApplication.personMap.get(mUserNote.UserID).Image;
						String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
						ImageLoader.getInstance().displayImage(uri, tvh.headIv, options);
					}
					tvh.nameTv.setText(mUserNote.UserName);
					String createTime = HelpUtil.time2String(mContext.getString(R.string.timeformat6), mUserNote.CreateTime);
					tvh.timeTv.setText(createTime);
					
					String content = "";
					if (!TextUtils.isEmpty(mUserNote.Content)) {
						content = Html.fromHtml(mUserNote.Content).toString();
					}
					tvh.contentTv.setText(content);
					tvh.discussTv.setText(String.valueOf(mUserNote.RevCount));
					tvh.collectTv.setText(String.valueOf(mUserNote.EnjoyCount));
					tvh.shareTv.setText(String.valueOf(mUserNote.ShareCount));
					
					Drawable left = mContext.getResources().getDrawable(
							mUserNote.IsEnjoy ? R.drawable.index_ico_sel_shoucang : R.drawable.index_ico_dianzan);
					left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
					tvh.collectTv.setCompoundDrawables(left, null, null, null);
					tvh.collectTv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							/*if (mUserNote.IsEnjoy) {
								delCollect(mUserNote.ID);
							} else {
								addCollect(mUserNote.ID);
							}*/
							doCollect(mUserNote.ID, mUserNote.IsEnjoy ? 1 : 0);
						}
					});
					tvh.shareTv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							openBottomSheet();
						}
					});
					
					if (mUserNote.info != null) {
						switch (mUserNote.Type) {
							case UserNoteAddTypes.Normal: //分享
								if (mUserNote.AddType == 2) { //图片
									addImage(mUserNote, tvh, position);
								} else if (mUserNote.AddType == 3) { //文件
									addFile(mUserNote, tvh);
								} else if (mUserNote.AddType == 4) { //链接
									addLink(mUserNote, tvh);
								} else if (mUserNote.AddType == 5) { //投票
									addSelect(mUserNote, tvh, position);
								} else { //普通
									
								}
								break;
							case UserNoteAddTypes.Schedule: //日程
								addSchedule(mUserNote, tvh);
								break;
							case UserNoteAddTypes.Task: //工作分派
								addWorkAllot(mUserNote, tvh);
								break;
							case UserNoteAddTypes.Plan: //工作计划
								addWorkPlan(mUserNote, tvh);
								break;
							case UserNoteAddTypes.Share: //转发
								addShareTo(mUserNote, tvh);
								break;
							default:
								break;
						}
					}
				}
				break;
			case TYPE_ITEM_RECE:
			case TYPE_ITEM_SEND:
				vh.replyLayout.removeAllViews();
				final UserNote revs = (UserNote) getItem(position);
				String name = "";
				String image = "";
				if (EMWApplication.personMap != null && EMWApplication.personMap.get(revs.UserID) != null) {
					UserInfo user = EMWApplication.personMap.get(revs.UserID);
					name = user.Name;
					image = user.Image;
				}
				vh.nameTv.setText(name+"：");
				vh.timeTv.setText(HelpUtil.time2String(mContext.getString(R.string.timeformat6), revs.CreateTime));
				vh.contentTv.setText(revs.Content);
				String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
				ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
				vh.replyBtn.setTag(name);
				vh.replyBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mContentEt.setTag(revs);
						mContentEt.setHint("回复 " + v.getTag());
					}
				});
				boolean hasSubreply = false;
				for (UserNote note : mAllList) {
					if (note.PID == revs.ID) {
						hasSubreply = true;
						addSubReply(note, vh.replyLayout, revs.ID);
					}
				}
				vh.replyLayout.setVisibility(hasSubreply ? View.VISIBLE : View.GONE);
//				getRevsList(revs.ID, vh.replyLayout);
				break;
		}
		
		return convertView;
	}
    //添加日程
	private void addSchedule(final UserNote un, TopViewHolder tvh) {
		ArrayList<UserSchedule> scheduleList = un.info.schedule;
		if (scheduleList != null && scheduleList.size() > 0) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_schedule, null);
			TextView timeTv = (TextView) view.findViewById(R.id.tv_dynamicschedule_time);
			TextView startTv = (TextView) view.findViewById(R.id.tv_dynamicschedule_starttime);
			TextView endTv = (TextView) view.findViewById(R.id.tv_dynamicschedule_endtime);
			final UserSchedule schedule = scheduleList.get(0);
			if (schedule.StartTime.length() > 10) {
				timeTv.setText(schedule.StartTime.substring(0, 10));
				startTv.setText(schedule.StartTime.substring(11)+" "+schedule.Title);
			} else {
				timeTv.setText(schedule.StartTime);
				startTv.setText(schedule.StartTime + " " + schedule.Title);
			}
//			endTv.setText(schedule.OverTime);
			tvh.otherLayout.addView(view);
			tvh.otherLayout.setVisibility(View.VISIBLE);
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, CalendarEditActivity.class);
					intent.putExtra(CalendarEditActivity.CALENDARID, un.TypeId);
					mContext.startActivity(intent);
				}
			});
		} else {
			tvh.otherLayout.setVisibility(View.GONE);
		}
	}
	//添加工作分派
	private void addWorkAllot(UserNote un, TopViewHolder tvh) {
//		vh.circleProgress.setVisibility(View.VISIBLE);
		ArrayList<UserFenPai> fenPaiList = un.info.task;
		if (fenPaiList != null && fenPaiList.size() > 0) {
			/*int sum = 0;
		    for (Task task : un.info.task) {
		    	sum += task.schedule;
		    }
		    vh.circleProgress.setValue(sum/un.info.task.size());*/
			View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_allot, null);
			ExListView listView = (ExListView) view.findViewById(R.id.lv_dynamicallot_list);
			DynamicAllotAdapter adapter = new DynamicAllotAdapter(mContext, un);
			listView.setAdapter(adapter);
			tvh.otherLayout.addView(view);
			tvh.otherLayout.setVisibility(View.VISIBLE);
		} else {
			tvh.otherLayout.setVisibility(View.GONE);
		}
	}
	//添加工作计划
	private void addWorkPlan(UserNote un, TopViewHolder tvh) {
		ArrayList<UserPlan> planList = un.info.log;
		if (planList != null && planList.size() > 0) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_plan, null);
			TextView typeTv = (TextView) view.findViewById(R.id.tv_dynamicplan_type);
			ExListView listView = (ExListView) view.findViewById(R.id.lv_dynamicplan_list);
			DynamicPlanAdapter adapter = new DynamicPlanAdapter(mContext, un);
			listView.setAdapter(adapter);
//			String text = "";
//			if (vh.timeTv.getTag() != null) {
//				textView.setText(vh.timeTv.getTag().toString());
//			} else {
//				CalendarUtil calendarUtil = new CalendarUtil();
//				Date date = new Date(getTime(createTime));
				int type = planList.get(0).Type;
				if (type == 1) {
					typeTv.setText(R.string.plan_type_day);
//					text = planList.get(0).OverTime;
				}else if (type == 2) {
					typeTv.setText(R.string.plan_type_week);
//					text = calendarUtil.getMondayOFWeek(date)+" ~ "+calendarUtil.getCurrentWeekday(date);
				}else if (type == 3) {
					typeTv.setText(R.string.plan_type_month);
//					text = calendarUtil.getFirstDayOfMonth(date)+" ~ "+calendarUtil.getDefaultDay(date);
				}
//				textView.setText(text);
//			}
			tvh.otherLayout.addView(view);
			tvh.otherLayout.setVisibility(View.VISIBLE);
		} else {
			tvh.otherLayout.setVisibility(View.GONE);
		}
	}
	//添加转发
	public void addShareTo(UserNote note, TopViewHolder tvh) {
		final UserNoteShareTo shareTo = note.info.shareTo;
		if (shareTo != null && shareTo.NoteID > 0) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_shareto, null, false);
			final TextView nameTv = (TextView) view.findViewById(R.id.tv_dynamicshareto_name);
			TextView contentTv = (TextView) view.findViewById(R.id.tv_dynamicshareto_content);
			nameTv.setText(shareTo.UserName);
			contentTv.setText(shareTo.Content);
			tvh.otherLayout.addView(view);
			tvh.otherLayout.setVisibility(View.VISIBLE);

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, DynamicDetailActivity.class);
					intent.putExtra("note_id", shareTo.NoteID);
					mContext.startActivity(intent);
				}
			});
		} else {
			tvh.otherLayout.setVisibility(View.GONE);
		}
	}
	//添加超链接
	public void addLink(UserNote note, TopViewHolder vh) {
		UserNoteLink link = note.info.link;
		if (link != null && !TextUtils.isEmpty(link.addr)) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_link, null, false);
			final TextView urlTv = (TextView) view.findViewById(R.id.tv_dynamiclink_url);
			TextView urlNameTv = (TextView) view.findViewById(R.id.tv_dynamiclink_urlname);
			urlTv.setText(link.addr);
			urlNameTv.setText(link.desc);
			vh.otherLayout.addView(view);
			vh.otherLayout.setVisibility(View.VISIBLE);
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = urlTv.getText().toString();
					if (!url.startsWith("http"))
						url = "http://" + url;
					Uri uri = Uri.parse(url);  
		            Intent intent = new Intent(Intent.ACTION_VIEW);
		            intent.setData(uri);
		            mContext.startActivity(intent);
				}
			});
		} else {
			vh.otherLayout.setVisibility(View.GONE);
		}
		/*
		copyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				copyText(mContext, urlTv.getText().toString());
			}
		});*/
		
	}
	//添加投票
	public void addSelect(UserNote note, TopViewHolder vh, int position) {
		ArrayList<UserRootVote> rootVoteList = note.info.vote; 
		if (rootVoteList != null && rootVoteList.size() > 0) {
			ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(
					R.layout.dynamic_item_vote, null, false);
			vh.otherLayout.addView(view);
			vh.otherLayout.setVisibility(View.VISIBLE);
			if (voteMap.get(note.ID)) {
				setRadioGroup(note, view);
			} else {
				getVoteRecsList(note, view);
			}
		} else {
			vh.otherLayout.setVisibility(View.GONE);
		}
	}
	//添加文件
	public void addFile(UserNote note, TopViewHolder vh) {
		ArrayList<UserNoteFile> fileList = note.info.File;
		if (fileList != null && fileList.size() > 0) {
			for (int i = 0, size = fileList.size(); i < size; i++) {
				final UserNoteFile noteFile = fileList.get(i);
				View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_file, null, false);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_dynamicfile_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.iv_dynamicfile_name);
				TextView tv_size = (TextView) view.findViewById(R.id.iv_dynamicfile_size);
				iv_icon.setImageResource(FileUtil.getResIconId(noteFile.Url));
				tv_name.setText(noteFile.FileName);
				tv_size.setText(FileUtil.getReadableFileSize(noteFile.Length));
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String localPath = EMWApplication.filePath + noteFile.Url;
						if (!FileUtil.hasFile(localPath)) {
							new AlertDialog(mContext).builder()
							.setMsg(mContext.getString(R.string.download_tips))
							.setPositiveButton(mContext.getString(R.string.ok), new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (noteFile.Url != null) {// 通过服务下载文件
										String fileUrl = String.format(Const.DOWN_FILE_URL,
												PrefsUtil.readUserInfo().CompanyCode,
												noteFile.Url);
										NotificationUtil.notificationForDLAPK(mContext, fileUrl, EMWApplication.filePath);
									}
								}
							})
							.setNegativeButton(mContext.getString(R.string.cancel), new OnClickListener() {
								@Override
								public void onClick(View v) {}
							}).show();
						} else {
							FileUtil.openFile(mContext, localPath);
						}
					}
				});
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				if (i > 0)
					params.topMargin = DisplayUtil.dip2px(mContext, 10);
				vh.otherLayout.addView(view, params);
			}
			vh.otherLayout.setVisibility(View.VISIBLE);
		} else {
			vh.otherLayout.setVisibility(View.GONE);
		}
	}
	//添加图片
	private void addImage(final UserNote note, TopViewHolder vh, int position) {
		final ArrayList<UserNoteFile> imageList = note.info.File;
		if (imageList != null) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.gray_1)
				.showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.build(); // 创建配置过得DisplayImageOption对象
			for (int i = 0, size = imageList.size(); i < size; i++) {
				UserNoteFile file = imageList.get(i);
				ImageView imgview = new ImageView(mContext);
				LayoutParams params = new LayoutParams(DisplayUtil.dip2px(mContext, 115), DisplayUtil.dip2px(mContext, 90));
				imgview.setLayoutParams(params);
				imgview.setPadding(0, 0, DisplayUtil.dip2px(mContext, 10), DisplayUtil.dip2px(mContext, 10));
				imgview.setScaleType(ScaleType.FIT_XY);
				imgview.setAdjustViewBounds(false);
				imgview.setTag(i);
				imgview.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						StringBuilder nameBuffer = new StringBuilder();
						for (int i = 0, size = imageList.size(); i < size; i++){
							UserNoteFile file = imageList.get(i);
							nameBuffer.append(file.Url);
							if (i < imageList.size() - 1) {
								nameBuffer.append(",");
							}
						}
						Intent intent=new Intent(mContext, PhotoActivity.class);
						intent.putExtra(PhotoActivity.INTENT_EXTRA_POSITION, Integer.valueOf(v.getTag().toString()));
						intent.putExtra(PhotoActivity.INTENT_EXTRA_IMGURLS, nameBuffer.toString());
						mContext.startActivity(intent);
					}
				});
				String url = String.format(Const.DOWN_FILE_URL, PrefsUtil.readUserInfo().CompanyCode, file.Url);
				ImageLoader.getInstance().displayImage(url, imgview, options);
				vh.imageLayout.addView(imgview, params);
			}
		}
	}
	
	public void setRadioGroup(final UserNote note, final ViewGroup view) {
		boolean flag = false; // 当前用户是否已投过
		int count = note.info.vote.get(0).Count;
		ArrayList<UserNoteVote> voteList = note.info.vote.get(0).Content;
		if (voteList != null) {
			for (UserNoteVote unv : voteList) {
				if (unv.IsSelected) {
					flag = true;
					break;
				}
			}
		}
		if (flag) {
			LinearLayout votedLayout = (LinearLayout) view.findViewById(R.id.ll_dynamicvote_voted);
			LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext, 40));
			for (int i = 0, size = voteList.size(); i < size; i++) {
				UserNoteVote unv = voteList.get(i);
				View childView = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_voted, null);
				childView.setBackgroundResource(unv.IsSelected ? R.drawable.voted_radio_bg:R.drawable.vote_radio_bg);
				TextView positionTv = (TextView) childView.findViewById(R.id.tv_dynamicvote_position);
				TextView nameTv = (TextView) childView.findViewById(R.id.tv_dynamicvote_name);
				TextView percentTv = (TextView) childView.findViewById(R.id.tv_dynamicvote_percent);
				positionTv.setText(i + 1 +".");
				nameTv.setText(unv.Text);
				percentTv.setText(Math.round(unv.Count*100.0/count)+"%");
				if (i > 0)
					params.topMargin = DisplayUtil.dip2px(mContext, 5);
				votedLayout.addView(childView, params);
			}
		} else {
			final RadioGroup group = (RadioGroup) view.findViewById(R.id.rg_dynamicvote_voting);
			group.removeAllViews();
			LayoutParams params = new RadioGroup.LayoutParams(
					RadioGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext, 40));
			for (int i = 0; i < voteList.size(); i++) {
				UserNoteVote unv = voteList.get(i);
				RadioButton rbutton = new RadioButton(mContext);
				rbutton.setLayoutParams(params);
				rbutton.setGravity(Gravity.CENTER_VERTICAL);
				rbutton.setPadding(DisplayUtil.dip2px(mContext, 10), 0, DisplayUtil.dip2px(mContext, 10), 0);
				rbutton.setBackgroundResource(R.drawable.vote_radio_bg);
//				rbutton.setTextAppearance(mContext, R.style.txt_normal);
				rbutton.setButtonDrawable(android.R.color.transparent);
				Drawable right = mContext.getResources().getDrawable(R.drawable.dynamic_vote_radio);
				right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
				rbutton.setCompoundDrawables(null, null, right, null);
				rbutton.setChecked(unv.IsSelected);
				rbutton.setEnabled(!flag);
				rbutton.setId(i+100);
				rbutton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				rbutton.setTextColor(mContext.getResources().getColor(R.color.cm_text));
				rbutton.setText(i+1+"."+unv.Text);
				rbutton.setTag(unv); //将选项的对象放到TAG
				if (i > 0)
					params.topMargin = DisplayUtil.dip2px(mContext, 5);
				group.addView(rbutton, params);
			}

			Button button = (Button) view.findViewById(R.id.btn_dynamicvote_vote);
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					View checkedRb = group.findViewById(group.getCheckedRadioButtonId());
					if (checkedRb == null) {
						ToastUtil.showToast(mContext, R.string.dynamic_select_vote);
						return;
					}
					selectVote(view, note, (UserNoteVote)checkedRb.getTag());
				}
			});
		}
	}
	
	/**
     * 获取投票记录
     * @param note
     * @param view
     */
	private void getVoteRecsList(final UserNote note, final ViewGroup view) {
		API.TalkerAPI.getVoteRevByPId(note.ID, new RequestCallback<UserNote>(UserNote.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
			}
			@Override
			public void onParseSuccess(List<UserNote> respList) {
				note.info.vote.get(0).Count = respList.size();
				((TextView) view.findViewById(R.id.tv_dynamicvote_count)).setText("投票："+respList.size()+"人");
				voteMap.put(note.ID, true);
				Gson gson = new Gson();
				ArrayList<UserNoteVote> voteList = note.info.vote.get(0).Content;
				if (voteList != null && voteList.size() > 0) {
					for (int i = 0, size = voteList.size(); i < size; i++) {
						UserNoteVote unv = voteList.get(i);
						int count = 0;
						boolean isSelect = false;
						for (UserNote un : respList) {
							try {
								Type typeOfT = new TypeToken<List<Integer>>(){}.getType();
								List<Integer> idList = gson.fromJson(un.Property, typeOfT);
								if (idList != null && idList.contains(unv.ID)) {
									count++;
									
									if (un.UserID == PrefsUtil.readUserInfo().ID) {
										isSelect = true;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						unv.Count = count;
						unv.IsSelected = isSelect;
						
//						TextView percentTv = (TextView) view.getChildAt(i+1).findViewById(R.id.tv_dynamicvote_percent);
//						if (respList != null) {
//							percentTv.setText(Math.round(unv.Count*100.0/respList.size())+"%");
//						}
					}
				}
				setRadioGroup(note, view);
			}
		});
    }
	
	/**
     * 投票
     */
    private void selectVote(final ViewGroup view, final UserNote note, UserNoteVote unv) {
    	UserNote rev = new UserNote();
    	rev.Content = "感谢您投票！";
    	rev.PID = note.ID;
    	rev.Property = "["+unv.ID+"]";
    	
    	API.TalkerAPI.SaveTalkerRev(rev, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null)
					mLoadingDialog.dismiss();
				ToastUtil.showToast(mContext, R.string.dynamic_vote_error);
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null)
					mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(mContext, R.string.dynamic_vote_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
					mContext.sendBroadcast(intent); // 刷新动态列表
					voteMap.put(note.ID, false);
					notifyDataSetChanged();
				} else {
					ToastUtil.showToast(mContext, R.string.dynamic_vote_error);
				}
			}
		});
    }
	
    /**
     * 收藏或取消收藏
     * @param noteID
     */
    private void doCollect(int noteID, final int flag) {
    	API.TalkerAPI.DoEnjoyTalker(noteID, flag, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null)
					mLoadingDialog.dismiss();
				ToastUtil.showToast(mContext, flag == 0 ? R.string.dynamicdetail_collect_error:R.string.dynamicdetail_cancelcollect_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = DialogUtil.createLoadingDialog(mContext, R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null)
					mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(mContext, flag == 0 ? R.string.dynamicdetail_collect_success:R.string.dynamicdetail_cancelcollect_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
					mContext.sendBroadcast(intent); // 刷新动态列表
					if (flag == 0) {
						mUserNote.IsEnjoy = true;
						mUserNote.EnjoyCount = mUserNote.EnjoyCount + 1;
					} else {
						mUserNote.IsEnjoy = false;
						mUserNote.EnjoyCount = mUserNote.EnjoyCount - 1;
					}
					notifyDataSetChanged();
				} else {
					ToastUtil.showToast(mContext, flag == 0 ? R.string.dynamicdetail_collect_error:R.string.dynamicdetail_cancelcollect_error);
				}
			}
		});
    }
    
    /**
     * 收藏
     * @param noteID
     */
    /*private void addCollect(int noteID) {
    	API.TalkerAPI.EnjoyTalker(noteID, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null)
					mLoadingDialog.dismiss();
				ToastUtil.showToast(mContext, "收藏失败!");
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog("正在处理...");
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null)
					mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(mContext, "收藏成功!", R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
					mContext.sendBroadcast(intent); // 刷新动态列表
					mUserNote.IsEnjoy = true;
					mUserNote.EnjoyCount = mUserNote.EnjoyCount + 1;
					notifyDataSetChanged();
				} else {
					ToastUtil.showToast(mContext, "收藏失败!");
				}
			}
		});
    }*/
    
    /**
     * 取消收藏
     * @param noteID
     */
    /*private void delCollect(int noteID) {
    	API.TalkerAPI.CancelEnjoyTalker(noteID, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null)
					mLoadingDialog.dismiss();
				ToastUtil.showToast(mContext, "取消收藏失败!");
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog("正在处理...");
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null)
					mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(mContext, "取消收藏成功!", R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
					mContext.sendBroadcast(intent); // 刷新动态列表
					mUserNote.IsEnjoy = false;
					mUserNote.EnjoyCount = mUserNote.EnjoyCount - 1;
					notifyDataSetChanged();
				} else {
					ToastUtil.showToast(mContext, "取消收藏失败!");
				}
			}
		});
    }*/
    
    /**
     * 获取回复列表
     */
    private void getRevsList(final int pid, final ViewGroup view) {
    	API.TalkerAPI.getTalkerRevByTalkerId(pid, new RequestCallback<UserNote>(UserNote.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
			}
			@Override
			public void onParseSuccess(List<UserNote> respList) {
				if (respList != null && respList.size() > 0) {
					for (final UserNote note : respList) {
						addSubReply(note, view, pid);
					}
				}
				view.setVisibility(View.VISIBLE);
			}
		});
    }
    private void addSubReply(final UserNote note, final ViewGroup view, final int pid) {
		View child = LayoutInflater.from(mContext).inflate(R.layout.dynamic_detail_item_subreply, null);
		TextView nameTv = (TextView) child.findViewById(R.id.tv_dynamicreply_name);
		TextView toNameTv = (TextView) child.findViewById(R.id.tv_dynamicreply_toname);
		TextView contentTv = (TextView) child.findViewById(R.id.tv_dynamicreply_content);
		Button replyBtn = (Button) child.findViewById(R.id.btn_dynamicreply_reply);
		UserInfo user = null;
		UserInfo touser = null;
		if (EMWApplication.personMap != null) {
			user = EMWApplication.personMap.get(note.UserID);
			touser = EMWApplication.personMap.get(note.ToUserId);
		}
		nameTv.setText(user != null? user.Name : "?");
		toNameTv.setText((touser != null? touser.Name : "?")+"：");
		contentTv.setText(note.Content);
		replyBtn.setTag(nameTv.getText());
		replyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				note.ID = pid;
				mContentEt.setTag(note);
				mContentEt.setHint("回复 " + v.getTag());
			}
		});
		view.addView(child);
	}
    /*private void getMyLogActivities(final TaskWorkPlanAdapter adapter) {
        final HttpUtils http = new HttpUtils();
        http.configCookieStore(PrefsUtil.readLoginCookie());
		RequestParam param = new RequestParam();
		param.setEmptyParams();
		http.send(HttpMethod.POST, HttpConst.Url_Client+"?t=Chatter.TalkerAccess&m=NewGetMyLogActivities", param, new RequestListener<WaitHandleInfo>(WaitHandleInfo.class, false, false) {
			@Override
			public void onStart() {
				mLoadingDialog = createLoadingDialog("正在加载...");
				mLoadingDialog.show();
			}
			
			@Override
			public void onSuccess(List<WaitHandleInfo> respList) {
//					mLoadingDialog.dismiss();
				adapter.setWaitList(respList);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
//					mLoadingDialog.dismiss();
				Toast.makeText(mContext, error.getExceptionCode()+" "+msg, Toast.LENGTH_SHORT).show();
			}
		});
    }*/
    
	// 将字符串转为时间戳
    private long getTime(String user_time) {
    	long re_time = 0; 
    	SimpleDateFormat sdf = new SimpleDateFormat(mContext.getString(R.string.timeformat6)); 
    	Date d; 
	    try { 
		    d = sdf.parse(user_time);
		    re_time = d.getTime(); 
	    }catch (ParseException e) { 
	    	e.printStackTrace();
	    } 
	    return re_time; 
    }
    
    private void copyText(Context context, String text) {
    	android.text.ClipboardManager cm = null;
		if (android.os.Build.VERSION.SDK_INT > 11) {
			cm = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);			
		} else {
			cm = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		}
		
		if (cm != null) {
			cm.setText(text);
			ToastUtil.showToast(mContext, "复制成功！");
		} else {
			ToastUtil.showToast(mContext, "复制失败！");
		}
    }
    
    private void openBottomSheet() {
        View view = LayoutInflater.from(mContext).inflate (R.layout.dynamic_detail_pop_share, null);
        Button talkerBtn = (Button)view.findViewById(R.id.btn_popshare_talker);
        Button personBtn = (Button)view.findViewById(R.id.btn_popshare_person);
        Button groupBtn = (Button)view.findViewById(R.id.btn_popshare_group);
 
        final Dialog bottomSheetDialog = new Dialog(mContext, R.style.MaterialDialogSheet);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
           DisplayUtil.dip2px(mContext, 160));
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();
 
        talkerBtn.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
            	bottomSheetDialog.dismiss();
                Intent intent = new Intent(mContext, ShareToActivity.class);
                intent.putExtra("user_note", mUserNote);
                mContext.startActivity(intent);
            }
        });
        personBtn.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
            	bottomSheetDialog.dismiss();
                Intent intent = new Intent(mContext, ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.RADIO_SELECT);
                ((Activity)mContext).startActivityForResult(intent, ContactSelectActivity.RADIO_SELECT);
            }
        });
        groupBtn.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
            	bottomSheetDialog.dismiss();
                Intent intent = new Intent(mContext, GroupSelectActivity.class);
                ((Activity)mContext).startActivityForResult(intent, 100);
            }
        });
    }

	static class TopViewHolder {
		CircleImageView headIv;
		CircleProgressView circleProgress;
		TextView nameTv;
		TextView timeTv;
		TextView contentTv;

		FlowLayout imageLayout;
		LinearLayout otherLayout;
		TextView discussTv;
		TextView collectTv;
		TextView shareTv;
	}

	static class ViewHolder {
		TextView timeTv;
		CircleImageView headIv;
		TextView nameTv;
		TextView contentTv;
		Button replyBtn;
		LinearLayout replyLayout;
	}
}
