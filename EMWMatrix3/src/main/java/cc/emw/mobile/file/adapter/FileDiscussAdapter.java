package cc.emw.mobile.file.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brucetoo.imagebrowse.ImageBrowseFragment;
import com.brucetoo.imagebrowse.widget.ImageInfo;
import com.brucetoo.imagebrowse.widget.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zf.iosdialog.widget.AlertDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.utils.QqFilter;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.file.FilePreviewActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.TaskReply;
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
import cc.emw.mobile.view.FlowLayout;
import sj.keyboard.utils.EmoticonsKeyboardUtils;

/**
 * 文件讨论Adapter
 * @author shaobo.zhuang
 */
public class FileDiscussAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<TaskReply> mDataList;
	private DisplayImageOptions options;
	private EditText mContentEt;
	private Dialog mLoadingDialog;

	public FileDiscussAdapter(Context context,
							  ArrayList<TaskReply> dataList, EditText contentEt) {
		this.mContext = context;
		this.mDataList = dataList;
		mContentEt = contentEt;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
	}
	
	@Override
	public int getCount() {
		return mDataList != null ? mDataList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_filediscuss, null);
			vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_filediscuss_head);
			vh.timeTv = (TextView) convertView.findViewById(R.id.tv_filediscuss_time);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_filediscuss_name);
			vh.contentTv = (TextView) convertView.findViewById(R.id.tv_filediscuss_content);
			vh.delTv = (TextView) convertView.findViewById(R.id.tv_filediscuss_del);
			vh.itemLayout = (FlowLayout) convertView.findViewById(R.id.fl_filediscuss_item);
			vh.replyLayout = (LinearLayout) convertView.findViewById(R.id.ll_filediscuss_reply);

			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}

		vh.replyLayout.removeAllViews();
		final TaskReply revs = (TaskReply) getItem(position);
		String name = "";
		String image = "";
		String companyCode = PrefsUtil.readUserInfo().CompanyCode;
		if (revs.CreatorInfo != null && revs.CreatorInfo.size() > 0) {
			ApiEntity.UserInfo user = revs.CreatorInfo.get(0);
			name = user.Name;
			image = user.Image;
			companyCode = user.CompanyCode;
		}
		vh.nameTv.setText(name);
//		vh.timeTv.setText(HelpUtil.time2String(mContext.getString(R.string.timeformat6), revs.CreateTime));
		vh.timeTv.setText(revs.CreateTime);
//		vh.contentTv.setText(revs.Content);
		ChatUtils.spannableEmoticonFilter(vh.contentTv, revs.Content);
		String uri = String.format(Const.DOWN_ICON_URL, companyCode, image);
		ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
		vh.delTv.setVisibility(revs.Creator == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.GONE);
		vh.delTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog(mContext).builder().setMsg("是否确定删除消息？")
						.setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
						.setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								delTaskReply(revs, false);
							}
						}).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
			}
		});
		convertView.setTag(R.id.tag_second, name);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mContentEt.setTag(revs);
				mContentEt.setHint("回复 " + v.getTag(R.id.tag_second));
			}
		});

		try {
			if (revs.ContentType == 1) { //图片
				addRevImage(revs, vh.itemLayout);
			} else if (revs.ContentType == 2) { //文件
				addRevFile(revs, vh.itemLayout);
			}
		} catch (Exception e) {

		}

		boolean hasSubreply = false;
		if (revs.Replys != null && revs.Replys.size() >0) {
			for (TaskReply subreply : revs.Replys) {
				hasSubreply = true;
				addSubReply(subreply, vh.replyLayout, revs.ID);
			}
		}

		vh.replyLayout.setVisibility(hasSubreply ? View.VISIBLE : View.GONE);

		return convertView;
	}

	private void addSubReply(final TaskReply subreply, final ViewGroup view, final int pid) {
		View child = LayoutInflater.from(mContext).inflate(R.layout.dynamic_detail_item_subreply, null);
		TextView contentTv = (TextView) child.findViewById(R.id.tv_dynamicreply_content);
		TextView delTv = (TextView) child.findViewById(R.id.tv_dynamicreply_del);
		delTv.setVisibility(subreply.Creator == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.GONE);
		delTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog(mContext).builder().setMsg("是否确定删除消息？")
						.setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
						.setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								delTaskReply(subreply, true);
							}
						}).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
			}
		});
		ApiEntity.UserInfo user = subreply.CreatorInfo != null && subreply.CreatorInfo.size() > 0 ? subreply.CreatorInfo.get(0) : null;
		ApiEntity.UserInfo touser = subreply.ToUserIdInfo != null && subreply.ToUserIdInfo.size() > 0 ? subreply.ToUserIdInfo.get(0) : null;
		String name = user != null? user.Name : "?";
		String reply = "\t回复\t";
		String toName = (touser != null? touser.Name : "?")+"：";
		if (subreply.Content == null) {
			subreply.Content = "";
		}
		subreply.Content = Html.fromHtml(subreply.Content).toString();
		SpannableString spanStr = new SpannableString(name + reply + toName + subreply.Content);
		ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamicreply_name_text));
		ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamicreply_name_text));
		spanStr.setSpan(colorSpan1, 0, name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		spanStr.setSpan(colorSpan2, name.length()+reply.length(), name.length()+reply.length()+toName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		Spannable spannable = QqFilter.spannableFilter(mContext,
				spanStr,
				name + reply + toName + subreply.Content,
				EmoticonsKeyboardUtils.getFontHeight(contentTv),
				null);
		contentTv.setText(spannable);
		child.setTag(name);
		child.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				subreply.ID = pid;
				mContentEt.setTag(subreply);
				mContentEt.setHint("回复 " + v.getTag());
			}
		});

		FlowLayout itemLayout = (FlowLayout) child.findViewById(R.id.fl_dynamicreply_item);
		try {
			if (subreply.ContentType == 1) { //图片
				addRevImage(subreply, itemLayout);
			} else if (subreply.ContentType == 2) { //文件
				addRevFile(subreply, itemLayout);
			}
		} catch (Exception e) {

		}

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		if (view.getChildCount() > 0) {
			params.topMargin = DisplayUtil.dip2px(mContext, 5);
		}
		view.addView(child, params);
	}

	//添加回复图片
	private void addRevImage(final TaskReply reply, ViewGroup imageLayout) {
		Type type = new TypeToken<List<UserNote.UserNoteFile>>() {}.getType();
		final ArrayList<UserNote.UserNoteFile> imageList = new Gson().fromJson(reply.AddProperty, type);
		if (imageList != null) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.color.gray_1)
					.delayBeforeLoading(100).bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true).resetViewBeforeLoading(true)
					.imageScaleType(ImageScaleType.EXACTLY) // 图像将完全按比例缩小的目标大小
					.showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
					.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
					.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
					.build(); // 创建配置过得DisplayImageOption对象
			final ArrayList<ImageInfo> imgImageInfos = new ArrayList<>();
			for (int i = 0, size = imageList.size(); i < size; i++) {
				final UserNote.UserNoteFile file = imageList.get(i);
				PhotoView imgview = new PhotoView(mContext);
				FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(DisplayUtil.dip2px(mContext, 120), DisplayUtil.dip2px(mContext, 100));
				imgview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imgview.disenable();
				imgview.setTag(i);
				imgview.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//Use of ImageBrowseFragment
						ArrayList<String> imgList = new ArrayList<>();
						for (int i = 0, size = imageList.size(); i < size; i++) {
							UserNote.UserNoteFile file = imageList.get(i);
							imgList.add(HelpUtil.getFileURL(file));
						}
						Bundle bundle = new Bundle();
						bundle.putStringArrayList(ImageInfo.INTENT_IMAGE_URLS, imgList);
						bundle.putParcelable(ImageInfo.INTENT_CLICK_IMAGE_INFO, ((PhotoView) v).getInfo());
						bundle.putInt(ImageInfo.INTENT_CLICK_IMAGE_POSITION, Integer.valueOf(v.getTag().toString()));
						bundle.putParcelableArrayList(ImageInfo.INTENT_IMAGE_INFOS, imgImageInfos);
						((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(Window.ID_ANDROID_CONTENT, ImageBrowseFragment.newInstance(bundle), "ViewPagerFragment")
								.addToBackStack(null).commit();

					}
				});
				String url = HelpUtil.getFileURL(file);
				ImageLoader.getInstance().displayImage(url, imgview, options);
				imgImageInfos.add(imgview.getInfo());
				params.bottomMargin = DisplayUtil.dip2px(mContext, 5);
				params.rightMargin = DisplayUtil.dip2px(mContext, 5);
				imageLayout.addView(imgview, params);
			}
		}
	}

	//添加回复文件
	public void addRevFile(TaskReply reply, ViewGroup otherLayout) {
		Type type = new TypeToken<List<UserNote.UserNoteFile>>() {}.getType();
		final ArrayList<UserNote.UserNoteFile> fileList = new Gson().fromJson(reply.AddProperty, type);
		if (fileList != null && fileList.size() > 0) {
			for (int i = 0, size = fileList.size(); i < size; i++) {
				final UserNote.UserNoteFile noteFile = fileList.get(i);
				View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_file_listitem, null, false);
				PhotoView iv_icon = (PhotoView) view.findViewById(R.id.iv_dynamicfile_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.iv_dynamicfile_name);
				TextView tv_size = (TextView) view.findViewById(R.id.iv_dynamicfile_size);
				iv_icon.disenable();
				iv_icon.setImageResource(FileUtil.getResIconId(noteFile.FileName));
				tv_name.setText(noteFile.FileName);
				tv_size.setText(FileUtil.getReadableFileSize(noteFile.Length));
				view.setTag(iv_icon);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (FileUtil.isImage(noteFile.FileName)) {
							//Use of ImageBrowseFragment
							ArrayList<String> imgList = new ArrayList<>();
							imgList.add(HelpUtil.getFileURL(noteFile));

							Bundle bundle = new Bundle();
							bundle.putStringArrayList(ImageInfo.INTENT_IMAGE_URLS, imgList);
							bundle.putParcelable(ImageInfo.INTENT_CLICK_IMAGE_INFO, ((PhotoView) v.getTag()).getInfo());
							bundle.putInt(ImageInfo.INTENT_CLICK_IMAGE_POSITION, 0);
//							bundle.putParcelableArrayList(ImageInfo.INTENT_IMAGE_INFOS, imgImageInfos);
							((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(Window.ID_ANDROID_CONTENT, ImageBrowseFragment.newInstance(bundle), "ViewPagerFragment")
									.addToBackStack(null).commit();
							return;
						}

						if (noteFile.FileName.contains(".doc") || noteFile.FileName.contains(".docx") || noteFile.FileName.contains(".xls") || noteFile.FileName.contains(".xlsx")) {
							Intent previewIntent = new Intent(mContext, FilePreviewActivity.class);
							previewIntent.putExtra(FilePreviewActivity.EXTENSION, noteFile.FileName);
							previewIntent.putExtra(FilePreviewActivity.FILE_ID, noteFile.FileId);
							previewIntent.putExtra(FilePreviewActivity.CREATOR, noteFile.CreateUser);
							mContext.startActivity(previewIntent);
							return;
						}

						String localPath = EMWApplication.filePath + FileUtil.getFileName(noteFile.Url);
						if (!FileUtil.hasFile(localPath)) {
							new AlertDialog(mContext).builder()
									.setMsg(mContext.getString(R.string.download_tips))
									.setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											if (!TextUtils.isEmpty(noteFile.Url)) {// 通过服务下载文件
												String fileUrl = HelpUtil.getFileURL(noteFile);
												NotificationUtil.notificationForDLAPK(mContext, fileUrl, EMWApplication.filePath, "", noteFile.FileId);
											}
										}
									})
									.setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
										}
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
				otherLayout.addView(view, params);
			}
			otherLayout.setVisibility(View.VISIBLE);
		} else {
			otherLayout.setVisibility(View.GONE);
		}
	}

	static class ViewHolder {
		TextView timeTv;
		CircleImageView headIv;
		TextView nameTv;
		TextView contentTv;
		TextView delTv;
		FlowLayout itemLayout;
		LinearLayout replyLayout;
	}

	private void delTaskReply(final TaskReply reply, final boolean isSubreply) {
		API.TalkerAPI.DelTaskReply(reply.ID, 0, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(mContext, R.string.deletedynamic_error);
			}

			@Override
			public void onStarted() {
				mLoadingDialog = DialogUtil.createLoadingDialog(mContext, R.string.loading_dialog_tips5);
				mLoadingDialog.show();
			}

			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(mContext, R.string.deletedynamic_success, R.drawable.tishi_ico_gougou);
					if (isSubreply) {
						for (TaskReply taskReply : mDataList) {
							if (taskReply.ID == reply.ParentID) {
								taskReply.Replys.remove(reply);
								break;
							}
						}
					} else {
						mDataList.remove(reply);
					}
					notifyDataSetChanged();
				} else {
					ToastUtil.showToast(mContext, R.string.deletedynamic_error);
				}
			}
		});
	}
}
