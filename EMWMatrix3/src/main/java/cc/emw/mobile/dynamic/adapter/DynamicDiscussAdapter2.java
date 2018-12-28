package cc.emw.mobile.dynamic.adapter;

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
import android.text.style.RelativeSizeSpan;
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
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.squareup.picasso.Picasso;
import com.zf.iosdialog.widget.AlertDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.utils.QqFilter;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.file.FilePreviewActivity;
import cc.emw.mobile.main.contral.CommentContral;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserNote;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.fragment.TimeTrackingWebFragment;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.FlowLayout;
import sj.keyboard.utils.EmoticonsKeyboardUtils;

/**
 * 滑动面板讨论
 */
public class DynamicDiscussAdapter2 extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserNote> mAllList;
    private ArrayList<UserNote> mDataList;
    private DisplayImageOptions options;
    private EditText mContentEt;
    private Dialog mLoadingDialog;
    private int detailID;
    private int enterFlag;

    public DynamicDiscussAdapter2(Context context, List<UserNote> dataList, EditText editText) {
        this(context, dataList, editText, 0);
    }
    public DynamicDiscussAdapter2(Context context, List<UserNote> dataList, EditText editText, int enterFlag) {
        this.mContext = context;
        this.mAllList = new ArrayList<>();
        this.mDataList = new ArrayList<>();
        this.mDataList.addAll(dataList);
        this.mContentEt = editText;
        this.enterFlag = enterFlag;

        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setDetailID(int noteID) {
        detailID = noteID;
    }

    public void setData(ArrayList<UserNote> dataList) {
        this.mDataList = dataList;
    }

    public void setAllData(List<UserNote> dataList) {
        if (dataList != null) {
            mAllList.clear();
            mAllList.addAll(dataList);
        }
    }

    public ArrayList<UserNote> getAllData() {
        return mAllList;
    }

    private CommentContral mCommentContral;
    public void setCirclePublicCommentContral(CommentContral commentContral) {
        this.mCommentContral = commentContral;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).ID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamicdetail_discuss, null);
            vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_dynamicdetail_head);
            vh.timeTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_time);
            vh.nameTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_name);
            vh.contentTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_content);
            vh.delTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_del);
            vh.itemLayout = (FlowLayout) convertView.findViewById(R.id.fl_dynamicdetail_item);
            vh.replyLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamicdetail_reply);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) convertView.getTag(R.id.tag_first);
        }

        vh.itemLayout.removeAllViews();
        vh.replyLayout.removeAllViews();
        final UserNote revs = (UserNote) getItem(position);
        if (revs.UserIdInfo != null) {
            String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(revs.UserIdInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : revs.UserIdInfo.CompanyCode, revs.UserIdInfo.Image);
            if (revs.UserIdInfo.Image != null && revs.UserIdInfo.Image.startsWith("/")) {
                uri = Const.DOWN_ICON_URL2 + revs.UserIdInfo.Image;
            }
            vh.headIv.setTextBg(EMWApplication.getIconColor(revs.UserIdInfo.ID), revs.UserIdInfo.Name, 30);
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
            vh.nameTv.setText(revs.UserIdInfo.Name);
        } else {
            vh.headIv.setTextBg(EMWApplication.getIconColor(revs.UserID), "", 30);
            vh.nameTv.setText("");
        }

        vh.headIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EMWApplication.personMap != null && EMWApplication.personMap.get(revs.UserID) != null) {
                    Intent intent = new Intent(mContext, PersonInfoActivity.class);
                    intent.putExtra("intoTag", 1);
                    intent.putExtra("UserInfo", EMWApplication.personMap.get(revs.UserID));
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            }
        });
        String lastReply = revs.UserIdInfo != null && revs.UserIdInfo.ID != PrefsUtil.readUserInfo().ID && mContentEt != null? "    回复":"";
        vh.timeTv.setText(StringUtils.friendly_time(revs.CreateTime) + lastReply);
//        vh.contentTv.setText(revs.Content);
        ChatUtils.spannableEmoticonFilter(vh.contentTv, revs.Content);
        if (mContentEt != null) {
            vh.delTv.setVisibility(revs.UserID == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.GONE);
            vh.delTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog(mContext).builder().setMsg("是否确定删除消息？")
                            .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                            .setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteTalkerRev(revs, revs.PID);
                                }
                            }).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
            });
        } else {
            vh.delTv.setVisibility(View.GONE);
        }
        convertView.setTag(R.id.tag_second, vh.nameTv.getText().toString());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentEt != null) {
                    if (revs.UserID != PrefsUtil.readUserInfo().ID) {
                        mContentEt.setTag(revs);
                        mContentEt.setHint("回复 " + v.getTag(R.id.tag_second));
                    }
                } else {
                    Intent intent = new Intent(mContext, DynamicDetailActivity.class);
                    intent.putExtra("note_id", detailID);
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            }
        });

        try {
            if (ApiEnum.UserNoteAddTypes.Image == revs.AddType) { //图片
                addRevImage(revs, vh.itemLayout);
            } else if (ApiEnum.UserNoteAddTypes.File == revs.AddType) { //文件
                addRevFile(revs, vh.itemLayout);
            }
        } catch (Exception e) {

        }

        boolean hasSubreply = false;
        for (UserNote note : mAllList) {
            if (note.PID == revs.ID) {
                hasSubreply = true;
                addSubReply(note, vh.replyLayout, revs.ID, revs.PID);
            }
        }
        vh.replyLayout.setVisibility(hasSubreply ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private void addSubReply(final UserNote note, final ViewGroup view, final int pid, final int topID) {
        View child = LayoutInflater.from(mContext).inflate(R.layout.dynamic_detail_item_subreply, null);
        CircleImageView headIv = (CircleImageView) child.findViewById(R.id.iv_dynamicreply_head);
        TextView contentTv = (TextView) child.findViewById(R.id.tv_dynamicreply_content);
        TextView delTv = (TextView) child.findViewById(R.id.tv_dynamicreply_del);
        if (mContentEt != null) {
            delTv.setVisibility(note.UserID == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.GONE);
            delTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog(mContext).builder().setMsg("是否确定删除消息？")
                            .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                            .setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteTalkerRev(note, topID);
                                }
                            }).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
            });
        } else {
            delTv.setVisibility(View.GONE);
        }
        final ApiEntity.TalkerUserInfo user = note.UserIdInfo;
        ApiEntity.TalkerUserInfo touser = note.ToUserIdInfo;
        if (user != null) {
            String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);
            if (user.Image != null && user.Image.startsWith("/")) {
                uri = Const.DOWN_ICON_URL2 + user.Image;
            }
            headIv.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 24);
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
        } else {
            headIv.setTextBg(EMWApplication.getIconColor(note.UserID), "", 24);
        }

        headIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EMWApplication.personMap != null && EMWApplication.personMap.get(user.ID) != null) {
                    Intent intent = new Intent(mContext, PersonInfoActivity.class);
                    intent.putExtra("intoTag", 1);
                    intent.putExtra("UserInfo", EMWApplication.personMap.get(user.ID));
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            }
        });
        String name = user != null? user.Name : "?";
        String reply = "\t回复\t";
        String toName = (touser != null? touser.Name : "?")+"：";
        String lastReply = user != null && user.ID != PrefsUtil.readUserInfo().ID? "    回复":"";
        if (note.Content == null) {
            note.Content = "";
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            note.Content = Html.fromHtml(note.Content,Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            note.Content = Html.fromHtml(note.Content).toString();
        }
        note.Content = Html.fromHtml(note.Content).toString();
        SpannableString spanStr = new SpannableString(name + reply + toName + note.Content + lastReply);
//        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamicreply_name_text));
//        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamicreply_name_text));
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamic_name_text));
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamic_name_text));
        ForegroundColorSpan colorSpan3 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamic_time_text));
        spanStr.setSpan(colorSpan1, 0, name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spanStr.setSpan(colorSpan2, name.length()+reply.length(), name.length()+reply.length()+toName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spanStr.setSpan(colorSpan3, spanStr.length()-lastReply.length(), spanStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.9f);
        spanStr.setSpan(sizeSpan, name.length(), name.length()+reply.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        Spannable spannable = QqFilter.spannableFilter(mContext,
                spanStr,
                name + reply + toName + note.Content + lastReply,
                EmoticonsKeyboardUtils.getFontHeight(contentTv),
                null);
        contentTv.setText(spannable); //姓名颜色改为指定颜色，支持表情显示
        child.setTag(name);
        if (mContentEt != null) {
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user != null && user.ID != PrefsUtil.readUserInfo().ID) {
                        mContentEt.setTag(note);
                        mContentEt.setHint("回复 " + v.getTag());
                    }
                }
            });
        }
        FlowLayout itemLayout = (FlowLayout) child.findViewById(R.id.fl_dynamicreply_item);
        try {
            if (ApiEnum.UserNoteAddTypes.Image == note.AddType) { //图片
                addRevImage(note, itemLayout);
            } else if (ApiEnum.UserNoteAddTypes.File == note.AddType) { //文件
                addRevFile(note, itemLayout);
            }
        } catch (Exception e) {

        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (view.getChildCount() > 0) {
            params.topMargin = DisplayUtil.dip2px(mContext, 5);
        }
        view.addView(child, params);
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

    //添加图片
    private void addRevImage(final UserNote note, ViewGroup imageLayout) {
        Type type = new TypeToken<List<cc.emw.mobile.entity.UserNote.UserNoteFile>>() {}.getType();
        final ArrayList<cc.emw.mobile.entity.UserNote.UserNoteFile> imageList = new Gson().fromJson(note.AddProperty, type);
        if (imageList != null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.color.gray_1)
                    .delayBeforeLoading(300).bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true).resetViewBeforeLoading(true)
                    .imageScaleType(ImageScaleType.EXACTLY) // 图像将完全按比例缩小的目标大小
                    .showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    .build(); // 创建配置过得DisplayImageOption对象
            final ArrayList<ImageInfo> imgImageInfos = new ArrayList<>();
            for (int i = 0, size = imageList.size(); i < size; i++) {
                final cc.emw.mobile.entity.UserNote.UserNoteFile file = imageList.get(i);
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
                            cc.emw.mobile.entity.UserNote.UserNoteFile file = imageList.get(i);
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
                /*Picasso.with(mContext)
                        .load(url)
                        .resize(DisplayUtil.dip2px(mContext, 120), DisplayUtil.dip2px(mContext, 100))
                        .centerCrop()
                        .config(Bitmap.Config.ALPHA_8)
                        .placeholder(R.drawable.chat_jiazaishibai)
                        .error(R.drawable.chat_jiazaishibai)
                        .into(imgview);*/
//                ImageLoader.getInstance().displayImage(url, imgview, options);
                ImageLoader.getInstance().displayImage(url, new ImageViewAware(imgview), options, new ImageSize(DisplayUtil.dip2px(mContext, 120), DisplayUtil.dip2px(mContext, 100)), null, null);
                imgImageInfos.add(imgview.getInfo());
				params.bottomMargin = DisplayUtil.dip2px(mContext, 5);
				params.rightMargin = DisplayUtil.dip2px(mContext, 5);
				imageLayout.addView(imgview, params);
            }
        }
    }

    //添加文件
    public void addRevFile(UserNote note, ViewGroup otherLayout) {
        Type type = new TypeToken<List<cc.emw.mobile.entity.UserNote.UserNoteFile>>() {}.getType();
        final ArrayList<cc.emw.mobile.entity.UserNote.UserNoteFile> fileList = new Gson().fromJson(note.AddProperty, type);
        if (fileList != null && fileList.size() > 0) {
            for (int i = 0, size = fileList.size(); i < size; i++) {
                final cc.emw.mobile.entity.UserNote.UserNoteFile noteFile = fileList.get(i);
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

    private void deleteTalkerRev(final UserNote un, final int topID) {
        API.TalkerAPI.DeleteTalkerRev(un.ID, topID, new RequestCallback<String>(String.class) {
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
                    mDataList.remove(un);
                    mAllList.remove(un);
                    notifyDataSetChanged();

                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    if (enterFlag == 1) {
                        intent = new Intent(TimeTrackingWebFragment.ACTION_TIME_DELETEDISCUSS);
                    }
                    intent.putExtra("note_id", topID);
                    intent.putExtra("delrev_note", un);
                    mContext.sendBroadcast(intent); //刷新Talker列表
                } else {
                    ToastUtil.showToast(mContext, R.string.deletedynamic_error);
                }
            }
        });
    }
}
