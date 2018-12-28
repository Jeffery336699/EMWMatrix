package cc.emw.mobile.dynamic.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brucetoo.imagebrowse.ImageBrowseFragment;
import com.brucetoo.imagebrowse.widget.ImageInfo;
import com.brucetoo.imagebrowse.widget.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.squareup.picasso.Picasso;
import com.takeoffandroid.urllinkview.library.LinkSourceContent;
import com.takeoffandroid.urllinkview.library.LinkViewCallback;
import com.takeoffandroid.urllinkview.library.TextCrawler;
import com.zf.iosdialog.widget.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.chat.utils.QqFilter;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.GroupSelectActivity;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.dynamic.DateDetailActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.MailDetailActivity;
import cc.emw.mobile.dynamic.PhoneDetailActivity;
import cc.emw.mobile.dynamic.ServiceDetailActivity;
import cc.emw.mobile.dynamic.ShareToActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.UserNoteFile;
import cc.emw.mobile.entity.UserNote.UserNoteLink;
import cc.emw.mobile.entity.UserNote.UserNoteShareTo;
import cc.emw.mobile.entity.UserNote.UserNoteVote;
import cc.emw.mobile.entity.UserNote.UserRootVote;
import cc.emw.mobile.file.FilePreviewActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.net.ApiEntity.UserSchedule;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.record.jcvideoplayer_lib.JCVideoPlayer;
import cc.emw.mobile.record.jcvideoplayer_lib.JCVideoPlayerStandard;
import cc.emw.mobile.util.CalendarUtil;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;
import cc.emw.mobile.view.RoundedImageView;
import cc.emw.mobile.view.ThumbUpView;
import sj.keyboard.utils.EmoticonsKeyboardUtils;

/**
 * 首页项详情Adapter
 *
 * @author shaobo.zhuang
 */
public class DynamicDetailAdapter extends BaseAdapter {

    private final String TAG = this.getClass().getSimpleName();

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

    public DynamicDetailAdapter(Context context, ArrayList<UserNote> dataList, EditText contentEt) {
        this.mContext = context;
        this.mAllList = new ArrayList<>();
        this.mDataList = dataList;
        voteMap = new SparseBooleanArray();
        mContentEt = contentEt;

        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//		.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setTopData(UserNote un) {
        mUserNote = un;
    }

    public void setData(ArrayList<UserNote> dataList) {
        this.mDataList = dataList;
    }

    public void setAllData(List<UserNote> dataList) {
        if (dataList != null)
            mAllList.addAll(dataList);
    }

    public List<UserNote> getAllData() {
        return mAllList;
    }

    /*public void add(ArrayList<UserNote> dataList) {
        if (dataList != null)
            mDataList.addAll(dataList);
        notifyDataSetChanged();
    }*/

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
            return mDataList.get(position - 1);
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
            return mDataList.get(position - 1).UserID == user.ID ? TYPE_ITEM_SEND : TYPE_ITEM_RECE;
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
                    tvh.rolesTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_roles);
                    tvh.deptTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_topdept);
                    tvh.contentTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_topcontent);
                    tvh.timeTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_toptime);
                    //文件、链接、投票、日程、工作分派、工作计划根Layout
                    tvh.otherLayout = (CardView) convertView.findViewById(R.id.ll_dynamicdetail_topother);
                    //图片集的根Layout
                    tvh.imageLayout = (FlowLayout) convertView.findViewById(R.id.ll_dynamicdetail_topimage);
                    //点赞根Layout
                    tvh.favourLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamicdetail_topfavour);
                    tvh.discussTv = (TextView) convertView.findViewById(R.id.tv_dynamic_discuss);
                    tvh.collectLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamicdetail_topcollect);
                    tvh.collectTuv = (ThumbUpView) convertView.findViewById(R.id.tuv_dynamic_collect);
                    tvh.collectTv = (TextView) convertView.findViewById(R.id.tv_dynamic_collect);
                    tvh.shareTv = (TextView) convertView.findViewById(R.id.tv_dynamic_share);
                    tvh.moreItv = (IconTextView) convertView.findViewById(R.id.itv_dynamic_more);
                    tvh.personTv = (TextView) convertView.findViewById(R.id.tv_dynamic_person);
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
                    vh.delTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_del);
                    vh.itemLayout = (FlowLayout) convertView.findViewById(R.id.fl_dynamicdetail_item);
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
                tvh.favourLayout.removeAllViews();
                convertView.setOnClickListener(null);
                if (mUserNote != null) {
                    if (mUserNote.UserIdInfo != null) {
                        String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(mUserNote.UserIdInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : mUserNote.UserIdInfo.CompanyCode, mUserNote.UserIdInfo.Image);
                        if (mUserNote.UserIdInfo.Image != null && mUserNote.UserIdInfo.Image.startsWith("/")) {
                            uri = Const.DOWN_ICON_URL2 + mUserNote.UserIdInfo.Image;
                        }
                        tvh.headIv.setTextBg(EMWApplication.getIconColor(mUserNote.UserIdInfo.ID), mUserNote.UserIdInfo.Name);
                        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(tvh.headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                        tvh.nameTv.setText(mUserNote.UserIdInfo.Name);
                        tvh.deptTv.setText(mUserNote.UserIdInfo.Job);
                    } else {
                        tvh.headIv.setTextBg(EMWApplication.getIconColor(mUserNote.UserID), "");
                        tvh.nameTv.setText("");
                        tvh.deptTv.setText("");
                    }
                    tvh.headIv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (EMWApplication.personMap != null && EMWApplication.personMap.get(mUserNote.UserID) != null) {
                                Intent intent = new Intent(mContext, PersonInfoActivity.class);
                                intent.putExtra("intoTag", 1);
                                intent.putExtra("UserInfo", EMWApplication.personMap.get(mUserNote.UserID));
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                    /*if (mUserNote.Roles != null && mUserNote.Roles.size() > 0) {
                        StringBuilder roleStr = new StringBuilder();
                        for (int i = 0, count = mUserNote.Roles.size(); i < count; i++) {
                            roleStr.append(mUserNote.Roles.get(i).Name).append("、");
                        }
                        roleStr.deleteCharAt(roleStr.length() - 1);
                        tvh.rolesTv.setText(roleStr.toString());
                        tvh.nameTv.setMinWidth(0);
                        tvh.nameTv.setMinimumWidth(0);//必须同时设置这个
                    } else {
                        tvh.rolesTv.setText("");
                    }*/
                    String createTime = mUserNote.CreateTime;
                    tvh.timeTv.setText(createTime);

                    String content = "";
                    if (!TextUtils.isEmpty(mUserNote.Content)) {
                        content = Html.fromHtml(mUserNote.Content).toString();
                        tvh.contentTv.setText(content);
                        tvh.contentTv.setVisibility(View.VISIBLE);
                    } else {
                        tvh.contentTv.setVisibility(View.GONE);
                    }
                    tvh.contentTv.setText(content);
                    tvh.discussTv.setText(String.valueOf(mUserNote.RevCount));
                    tvh.collectTv.setText(String.valueOf(mUserNote.EnjoyCount));
                    tvh.shareTv.setText(String.valueOf(mUserNote.ShareCount));
                    tvh.moreItv.setVisibility(mUserNote.UserID == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.INVISIBLE);
                    tvh.moreItv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RightMenu mMenu = new RightMenu(mContext);
                            mMenu.addItem(R.string.dynamicdetail_more_delete, 1);
                            mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
                                @Override
                                public void selected(View view, PopMenu.Item item, int position) {
                                    switch (item.id) {
                                        case 1:
                                            new AlertDialog(mContext).builder().setMsg(mContext.getString(R.string.deletedynamic_tips))
                                                    .setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            delete(mUserNote.ID);
                                                        }
                                                    }).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                }
                                            }).show();
                                            break;
                                    }
                                }
                            });
                            mMenu.showAsDropDown(v);
                        }
                    });

//					Drawable left = mContext.getResources().getDrawable(mUserNote.IsEnjoy ? R.drawable.index_ico_sel_shoucang : R.drawable.index_ico_dianzan);
//					left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
//					tvh.collectTv.setCompoundDrawables(left, null, null, null);
                    if (mUserNote.IsEnjoy) {
                        tvh.collectTuv.Like();
                    } else {
                        tvh.collectTuv.UnLike();
                    }
                    tvh.collectLayout.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            doCollect(mUserNote.ID, mUserNote.IsEnjoy ? 1 : 0, v);
                        }
                    });
                    tvh.shareTv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            openBottomSheet();
                        }
                    });

                    if (mUserNote.EnjoyList != null && mUserNote.EnjoyList.size() > 0) {
                        tvh.favourLayout.setVisibility(View.VISIBLE);
                        for (int i = 0; i < mUserNote.EnjoyList.size(); i++) {
                            if (mUserNote.EnjoyList.size() - i - 5 > 0) //超过5个只需显示最后5个
                                continue;
                            ApiEntity.TalkerUserInfo user = mUserNote.EnjoyList.get(i);
                            final CircleImageView imgview = new CircleImageView(mContext);
                            imgview.setBorderWidth(DisplayUtil.dip2px(mContext, 1));
                            imgview.setBorderColorResource(R.color.cm_headimg_border);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 21), DisplayUtil.dip2px(mContext, 21));
                            String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);
                            imgview.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 21);
                            ImageLoader.getInstance().displayImage(url, new ImageViewAware(imgview), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                            if (tvh.favourLayout.getChildCount() != 0)
                                params.leftMargin = -DisplayUtil.dip2px(mContext, 4);
                            tvh.favourLayout.addView(imgview, params);
                        }
                        TextView countTv = new TextView(mContext);
                        countTv.setTextSize(12);
                        countTv.setTextColor(mContext.getResources().getColor(R.color.dynamic_count_text));
                        //countTv.setText(un.EnjoyList.size() +"人点赞");
                        countTv.setText("等觉得很赞");
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        params.leftMargin = DisplayUtil.dip2px(mContext, 10);
                        tvh.favourLayout.addView(countTv, params);
                    } else {
                        tvh.favourLayout.setVisibility(View.GONE);
                    }
                    /*if (mUserNote.EnjoyList != null && mUserNote.EnjoyList.size() > 0) {
						tvh.personTv.setVisibility(View.VISIBLE);
						StringBuilder builder = new StringBuilder();
						String endStr = "等"+ mUserNote.EnjoyList.size() +"人已收藏";
						for (int i = 0; i < mUserNote.EnjoyList.size(); i++) {
							ApiEntity.UserInfo user = mUserNote.EnjoyList.get(i);
							if (i != 0) {
								builder.append("、");
							}
							builder.append(user.Name);
						}
						builder.append(endStr);
						SpannableString spanStr = new SpannableString(builder);
						ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#595968"));
						spanStr.setSpan(colorSpan1, builder.length()-endStr.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						tvh.personTv.setText(spanStr);
					} else {
						tvh.personTv.setVisibility(View.GONE);
					}*/

                    addTypes(mUserNote, tvh.otherLayout, tvh.imageLayout, tvh.contentTv);
                }
                break;
            case TYPE_ITEM_RECE:
            case TYPE_ITEM_SEND:
                vh.itemLayout.removeAllViews();
                vh.replyLayout.removeAllViews();
                final UserNote revs = (UserNote) getItem(position);
                if (revs.UserIdInfo != null) {
                    String uri = String.format(Const.DOWN_ICON_URL, revs.UserIdInfo.CompanyCode, revs.UserIdInfo.Image);
                    vh.headIv.setTextBg(EMWApplication.getIconColor(revs.UserIdInfo.ID), revs.UserIdInfo.Name, 30);
                    ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                    vh.nameTv.setText(revs.UserIdInfo.Name);
                } else {
                    vh.headIv.setTextBg(EMWApplication.getIconColor(revs.UserID), "", 30);
                    vh.nameTv.setText("");
                }

                String lastReply = revs.UserIdInfo != null && revs.UserIdInfo.ID != PrefsUtil.readUserInfo().ID? "    回复":"";
                vh.timeTv.setText(StringUtils.friendly_time(revs.CreateTime) + lastReply);
                ChatUtils.spannableEmoticonFilter(vh.contentTv, revs.Content);
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
                convertView.setTag(R.id.tag_second, vh.nameTv.getText().toString());
                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (revs.UserID != PrefsUtil.readUserInfo().ID) {
                            mContentEt.setTag(revs);
                            mContentEt.setHint("回复 " + v.getTag(R.id.tag_second));
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
                break;
        }

        return convertView;
    }

    //添加回复图片
    private void addRevImage(final UserNote note, ViewGroup imageLayout) {
        Type type = new TypeToken<List<UserNoteFile>>() {
        }.getType();
        final ArrayList<UserNoteFile> imageList = new Gson().fromJson(note.AddProperty, type);
        if (imageList != null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.color.gray_1)
                    .delayBeforeLoading(100).bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true).resetViewBeforeLoading(true)
                    .imageScaleType(ImageScaleType.EXACTLY) // 图像将完全按比例缩小的目标大小
//					.showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
//					.showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    .build(); // 创建配置过得DisplayImageOption对象
            final ArrayList<ImageInfo> imgImageInfos = new ArrayList<>();
            for (int i = 0, size = imageList.size(); i < size; i++) {
                final UserNoteFile file = imageList.get(i);
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
                            UserNoteFile file = imageList.get(i);
                            imgList.add(String.format(Const.DOWN_FILE_URL,
                                    TextUtils.isEmpty(file.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : file.CompanyCode, file.Url));
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
    public void addRevFile(UserNote note, ViewGroup otherLayout) {
        Type type = new TypeToken<List<UserNoteFile>>() {
        }.getType();
        final ArrayList<UserNoteFile> fileList = new Gson().fromJson(note.AddProperty, type);
        if (fileList != null && fileList.size() > 0) {
            for (int i = 0, size = fileList.size(); i < size; i++) {
                final UserNoteFile noteFile = fileList.get(i);
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
                            imgList.add(String.format(Const.DOWN_FILE_URL,
                                    TextUtils.isEmpty(noteFile.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : noteFile.CompanyCode, noteFile.Url));

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

    /**
     * 根据不同类型展示布局
     */
    private void addTypes(UserNote un, ViewGroup otherLayout, ViewGroup imageLayout, TextView contentTv) {
        if (un.info != null) {
            switch (un.Type) {
                case UserNoteAddTypes.Normal: //分享
                case UserNoteAddTypes.Notice: //公告
                    if (UserNoteAddTypes.Image == un.AddType) { //图片
                        addImage(un, imageLayout);
                    } else if (UserNoteAddTypes.File == un.AddType) { //文件
                        addFile(un, otherLayout);
                    } else if (UserNoteAddTypes.Link == un.AddType) { //链接
                        addLink(un, otherLayout);
                    } else if (UserNoteAddTypes.Vote == un.AddType) { //投票
                        addSelect(un, otherLayout);
                    } else if (UserNoteAddTypes.Video == un.AddType) { //视频
                        addVideo(un, imageLayout);
                    } else { //普通

                    }
                    break;
                case UserNoteAddTypes.Schedule: //日程
                    addSchedule(un, otherLayout);
                    break;
                case UserNoteAddTypes.Task: //工作分派
                    addWorkAllot(un, otherLayout);
                    break;
                case UserNoteAddTypes.Plan: //工作计划
                    addWorkPlan(un, otherLayout);
                    break;
                case UserNoteAddTypes.Share: //转发
                    addShareTo(un, otherLayout, imageLayout);
                    break;
                case UserNoteAddTypes.Appoint: //约会
                case UserNoteAddTypes.Phone: //电话
                case UserNoteAddTypes.Email: //邮件
                case UserNoteAddTypes.SeviceActive: //服务活动
                    addDate(un, otherLayout, contentTv);
                    break;
                default:
                    break;
            }
        }
    }

    //添加日程
    private void addSchedule(final UserNote un, ViewGroup otherLayout) {
        ArrayList<UserSchedule> scheduleList = un.info.schedule;
        if (scheduleList != null && scheduleList.size() > 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_schedule, null);
            TextView timeTv = (TextView) view.findViewById(R.id.tv_dynamicschedule_time);
            TextView startTv = (TextView) view.findViewById(R.id.tv_dynamicschedule_starttime);
            TextView endTv = (TextView) view.findViewById(R.id.tv_dynamicschedule_endtime);
            final UserSchedule schedule = scheduleList.get(0);
            if (schedule.StartTime.length() > 10) {
                timeTv.setText(schedule.StartTime.substring(0, 10));
                startTv.setText(schedule.StartTime.substring(11) + " " + schedule.Title);
            } else {
                timeTv.setText(schedule.StartTime);
                startTv.setText(schedule.StartTime + " " + schedule.Title);
            }
//			endTv.setText(schedule.OverTime);
            otherLayout.addView(view);
            otherLayout.setVisibility(View.VISIBLE);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CalendarInfoActivity2.class);
                    intent.putExtra(CalendarInfoActivity2.CALENDARID, un.TypeId);
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            });
        } else {
            otherLayout.setVisibility(View.GONE);
        }
    }

    //添加工作分派
    private void addWorkAllot(UserNote un, ViewGroup otherLayout) {
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
            otherLayout.addView(view);
            otherLayout.setVisibility(View.VISIBLE);
        } else {
            otherLayout.setVisibility(View.GONE);
        }
    }

    //添加工作计划
    private void addWorkPlan(UserNote un, ViewGroup otherLayout) {
        ArrayList<UserPlan> planList = un.info.log;
        if (planList != null && planList.size() > 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_plan, null);
            TextView typeTv = (TextView) view.findViewById(R.id.tv_dynamicplan_type);
            ExListView listView = (ExListView) view.findViewById(R.id.lv_dynamicplan_list);
            DynamicPlanAdapter adapter = new DynamicPlanAdapter(mContext, un);
            listView.setAdapter(adapter);
            String text = "";
            CalendarUtil calendarUtil = new CalendarUtil();
            Date date = StringUtils.toDate(planList.get(0).EndTime);
            int type = planList.get(0).Type;
            if (type == 1) {
                typeTv.setText(R.string.plan_type_day);
            } else if (type == 2) {
                if (date != null)
                    text = " " + calendarUtil.getMondayOFWeek2(date) + "-" + calendarUtil.getCurrentWeekday2(date); //在哪个周范围内
                typeTv.setText(mContext.getResources().getString(R.string.plan_type_week) + text);
            } else if (type == 3) {
                if (date != null)
                    text = " " + calendarUtil.getFirstDayOfMonth2(date) + "-" + calendarUtil.getDefaultDay2(date); //在哪个月范围内
                typeTv.setText(mContext.getResources().getString(R.string.plan_type_month) + text);
            }
            otherLayout.addView(view);
            otherLayout.setVisibility(View.VISIBLE);
        } else {
            otherLayout.setVisibility(View.GONE);
        }
    }

    //添加转发
    public void addShareTo(UserNote note, ViewGroup otherLayout, ViewGroup imgLayout) {
        UserNote shareNote = note.info.shareNote;
        UserNoteShareTo shareTo = null;
        ArrayList<UserNoteShareTo> shareToList = note.info.shareTo;
        if (shareToList != null && shareToList.size() > 0) {
            shareTo = shareToList.get(0);
        }
        if (shareNote != null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_sharenote, null, false);
            final TextView contentTv = (TextView) view.findViewById(R.id.tv_dynamicshare_content);
            CardView shareLayout = (CardView) view.findViewById(R.id.ll_dynamicshare_other);
            FlowLayout imageLayout = (FlowLayout) view.findViewById(R.id.fl_dynamicshare_image);
            addTypes(shareNote, shareLayout, imageLayout, null);
            String name = shareNote.UserName + "：";
            String content = !TextUtils.isEmpty(shareNote.Content) ? Html.fromHtml(shareNote.Content).toString() : "";
            ArrayList<UserSchedule> scheduleList = shareNote.info.schedule;
            if (scheduleList != null && scheduleList.size() > 0) {
                content = scheduleList.get(0).Title;
            }
            SpannableString spanStr = new SpannableString(name + content);
            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamicshareto_name_text));
            spanStr.setSpan(colorSpan1, 0, name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            contentTv.setText(spanStr);
            imgLayout.addView(view);
            imgLayout.setVisibility(View.VISIBLE);

            view.setTag(shareNote.ID);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DynamicDetailActivity.class);
                    intent.putExtra("note_id", Integer.valueOf(v.getTag().toString()));
                    intent.putExtra("start_anim", false);
                    mContext.startActivity(intent);
                }
            });
        } else if (shareTo != null && shareTo.NoteID > 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_shareto, null, false);
            final TextView nameTv = (TextView) view.findViewById(R.id.tv_dynamicshareto_name);
            TextView contentTv = (TextView) view.findViewById(R.id.tv_dynamicshareto_content);
//			nameTv.setText(shareTo.UserName);
//			contentTv.setText(!TextUtils.isEmpty(shareTo.Content)? Html.fromHtml(shareTo.Content):"");
            ImageView iconIv = (ImageView) view.findViewById(R.id.iv_dynamicshareto_icon);
            iconIv.setVisibility(View.GONE);
            nameTv.setVisibility(View.GONE);
            contentTv.setText("原内容已被删除");
            otherLayout.addView(view);
            otherLayout.setVisibility(View.VISIBLE);

			/*view.setTag(shareTo.NoteID);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, DynamicDetailActivity.class);
					intent.putExtra("note_id", Integer.valueOf(v.getTag().toString()));
					mContext.startActivity(intent);
				}
			});*/
        } else {
            otherLayout.setVisibility(View.GONE);
            imgLayout.setVisibility(View.GONE);
        }
    }

    //添加超链接
    public void addLink(UserNote note, ViewGroup otherLayout) {
        final UserNoteLink link = note.info.link;
        if (link != null && !TextUtils.isEmpty(link.addr)) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_link, null, false);
            final TextView urlTv = (TextView) view.findViewById(R.id.tv_dynamiclink_url);
            TextView urlNameTv = (TextView) view.findViewById(R.id.tv_dynamiclink_urlname);
            urlTv.setText(link.addr);
            urlNameTv.setText(link.desc);
            otherLayout.addView(view);
            otherLayout.setVisibility(View.VISIBLE);

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

            if (!TextUtils.isEmpty(link.title)) {
                urlTv.setText(link.title);
            } else {
                TextCrawler textCrawler = new TextCrawler();
                textCrawler.makePreview(new LinkViewCallback() {
                    @Override
                    public void onBeforeLoading() {
                    }

                    @Override
                    public void onAfterLoading(LinkSourceContent linkSourceContent, boolean isNull) {
                        if (!TextUtils.isEmpty(linkSourceContent.getTitle())) {
                            link.title = linkSourceContent.getTitle();
                            urlTv.setText(linkSourceContent.getTitle());
                        }
                    }
                }, link.addr);
            }
        } else {
            otherLayout.setVisibility(View.GONE);
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
    public void addSelect(UserNote note, ViewGroup otherLayout) {
        ArrayList<UserRootVote> rootVoteList = note.info.vote;
        if (rootVoteList != null && rootVoteList.size() > 0) {
            ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(
                    R.layout.dynamic_item_vote2, null, false);
            otherLayout.addView(view);
            otherLayout.setVisibility(View.VISIBLE);
            if (voteMap.get(note.ID)) {
                setRadioGroup(note, view);
            } else {
                getVoteRecsList(note, view);
            }
        } else {
            otherLayout.setVisibility(View.GONE);
        }
    }

    //添加文件
    public void addFile(UserNote note, ViewGroup otherLayout) {
        ArrayList<UserNoteFile> fileList = note.info.File;
        if (fileList != null && fileList.size() > 0) {
            View rootview = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_file, null, false);
            LinearLayout itemRootLayout = (LinearLayout) rootview.findViewById(R.id.ll_dynamicfile_item);
            otherLayout.addView(rootview);
            addFileItem(note, itemRootLayout);
            otherLayout.setVisibility(View.VISIBLE);
        } else {
            otherLayout.setVisibility(View.GONE);
        }
    }
    private void addFileItem(UserNote note, ViewGroup otherLayout) {
        ArrayList<UserNoteFile> fileList = note.info.File;
        if (fileList != null && fileList.size() > 0) {
            for (int i = 0, size = fileList.size(); i < size; i++) {
                final UserNoteFile noteFile = fileList.get(i);
                View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_file_listitem, null, false);
                PhotoView iv_icon = (PhotoView) view.findViewById(R.id.iv_dynamicfile_icon);
                TextView tv_name = (TextView) view.findViewById(R.id.iv_dynamicfile_name);
                TextView tv_size = (TextView) view.findViewById(R.id.iv_dynamicfile_size);
                iv_icon.disenable();
                iv_icon.setImageResource(FileUtil.getResIconId(noteFile.FileName));
                tv_name.setText(noteFile.FileName);
                tv_size.setText(FileUtil.getReadableFileSize(noteFile.Length));
                view.setTag(iv_icon);
                view.setOnClickListener(new OnClickListener() {
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
                                    .setPositiveButton(mContext.getString(R.string.ok), new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!TextUtils.isEmpty(noteFile.Url)) { // 通过服务下载文件
                                                String fileUrl = HelpUtil.getFileURL(noteFile);
                                                NotificationUtil.notificationForDLAPK(mContext, fileUrl, EMWApplication.filePath, "", noteFile.FileId);
                                            }
                                        }
                                    })
                                    .setNegativeButton(mContext.getString(R.string.cancel), new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).show();
                        } else {
                            FileUtil.openFile(mContext, localPath);
                        }
                    }
                });
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                if (i > 0)
                    params.topMargin = DisplayUtil.dip2px(mContext, 10);
                otherLayout.addView(view, params);
            }
            otherLayout.setVisibility(View.VISIBLE);
        } else {
            otherLayout.setVisibility(View.GONE);
        }
    }

    //添加图片
    private void addImage(final UserNote note, ViewGroup imageLayout) {
        final ArrayList<UserNoteFile> imageList = note.info.File;
        if (imageList != null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.color.gray_1)
                    .delayBeforeLoading(100).bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true).resetViewBeforeLoading(true)
                    .imageScaleType(ImageScaleType.EXACTLY) // 图像将完全按比例缩小的目标大小
//					.showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
//					.showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    .build(); // 创建配置过得DisplayImageOption对象
            int count = imageList.size();
            View view = null;
            switch (count) {
                case 0:
                    return;
                case 1:
                    view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_image1, null, false);
                    break;
                case 2:
                    view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_image2, null, false);
                    break;
                case 3:
                    view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_image3, null, false);
                    break;
                case 4:
                    view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_image4, null, false);
                    break;
                case 5:
                    view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_image5, null, false);
                    break;
                case 6:
                    view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_image6, null, false);
                    break;
                default:
                    view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_image7, null, false);
                    TextView countTv = (TextView) view.findViewById(R.id.tv_dynamicimage_count);
                    /*SpannableString spanStr = new SpannableString(count + " 张");
                    RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.6F);
                    spanStr.setSpan(sizeSpan, 0, String.valueOf(count).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    countTv.setText(spanStr);*/
                    countTv.setText("+"+(count-6));
                    break;
            }
            final ArrayList<ImageInfo> imgImageInfos = new ArrayList<>();
            for (int i = 0, size = imageList.size(); i < size; i++) {
                final UserNoteFile file = imageList.get(i);
                PhotoView imgview = null;
                if (i == 0) {
                    imgview = (PhotoView) view.findViewById(R.id.iv_dynamicimage_img1);
                } else if (i == 1) {
                    imgview = (PhotoView) view.findViewById(R.id.iv_dynamicimage_img2);
                } else if (i == 2) {
                    imgview = (PhotoView) view.findViewById(R.id.iv_dynamicimage_img3);
                } else if (i == 3) {
                    imgview = (PhotoView) view.findViewById(R.id.iv_dynamicimage_img4);
                } else if (i == 4) {
                    imgview = (PhotoView) view.findViewById(R.id.iv_dynamicimage_img5);
                } else if (i == 5) {
                    imgview = (PhotoView) view.findViewById(R.id.iv_dynamicimage_img6);
                } else {
                    break;
                }
                imgview.disenable();
                imgview.setTag(i);
                imgview.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
						/*StringBuilder nameBuffer = new StringBuilder();
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
						mContext.startActivity(intent);*/

                        //Use of ImageBrowseFragment
                        ArrayList<String> imgList = new ArrayList<>();
                        for (int i = 0, size = imageList.size(); i < size; i++) {
                            UserNoteFile file = imageList.get(i);
                            imgList.add(HelpUtil.getFileURL(file));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList(ImageInfo.INTENT_IMAGE_URLS, imgList);
                        bundle.putParcelable(ImageInfo.INTENT_CLICK_IMAGE_INFO, ((PhotoView) v).getInfo());
                        bundle.putInt(ImageInfo.INTENT_CLICK_IMAGE_POSITION, Integer.valueOf(v.getTag().toString()));
                        bundle.putParcelableArrayList(ImageInfo.INTENT_IMAGE_INFOS, imgImageInfos);
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(Window.ID_ANDROID_CONTENT, ImageBrowseFragment.newInstance(bundle), "ViewPagerFragment")
                                .addToBackStack(null).commit();

                        mContext.sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_COUNT).putExtra("is_show", false));

                    }
                });
                String url = HelpUtil.getFileURL(file);
                ImageLoader.getInstance().displayImage(url, imgview, options);
                imgImageInfos.add(imgview.getInfo());
				/*params.bottomMargin = DisplayUtil.dip2px(mContext, 10);
				params.rightMargin = DisplayUtil.dip2px(mContext, 10);
				imageLayout.addView(imgview, params);*/
            }
            imageLayout.addView(view);
            imageLayout.setVisibility(View.VISIBLE);
        }
    }

    //添加视频
    private void addVideo(final UserNote note, final ViewGroup imageLayout) {
        ArrayList<UserNoteFile> fileList = note.info.File;
        if (fileList != null && fileList.size() > 0) {
            final JCVideoPlayerStandard jcVideoPlayer = (JCVideoPlayerStandard) LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_video2, null);
            final UserNoteFile file = fileList.get(0);
            String companyCode;
            if (TextUtils.isEmpty(file.CompanyCode)) {
                companyCode = PrefsUtil.readUserInfo().CompanyCode;
            } else {
                companyCode = file.CompanyCode;
            }
            final String fileUrl;
            if (file.Url.startsWith("/")) {
                fileUrl = Const.BASE_URL + "/" + "Resource" + file.Url;
            } else if (file.Url.startsWith("\\")) {
                fileUrl = Const.BASE_URL + "/" + "Resource" + file.Url.replaceAll("\\\\", "/");
            } else {
                fileUrl = Const.BASE_URL + "/" + "Resource/" + companyCode + "/UserFile/" + file.Url;
            }
            final String fileName = file.Url.substring(file.Url.lastIndexOf("/") + 1);
            jcVideoPlayer.setUp(fileUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "");
            String videoThumbPath = EMWApplication.tempPath + fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg";
            if (FileUtil.hasFile(videoThumbPath)) {
                Picasso.with(mContext).load("file://" + videoThumbPath).error(R.drawable.trans_bg)
                        .config(Bitmap.Config.ALPHA_8)
                        .into(jcVideoPlayer.thumbImageView);
            } else {
                jcVideoPlayer.thumbImageView.setImageResource(R.drawable.trans_bg);
                jcVideoPlayer.thumbImageView.setTag(fileUrl);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = getFirstVideoFrame(true, fileUrl, fileName);
                        jcVideoPlayer.thumbImageView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (fileUrl.equals(jcVideoPlayer.thumbImageView.getTag())) {
                                    if (bitmap != null) {
                                        jcVideoPlayer.thumbImageView.setImageBitmap(bitmap);
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
            imageLayout.addView(jcVideoPlayer);
            imageLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取网络或本地视频的第一帧
     *
     * @param isFromNetWork
     * @return bitmap
     */
    public Bitmap getFirstVideoFrame(boolean isFromNetWork, String fileUrl, String fileName) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        FileOutputStream outStream = null;
        try {
            if (isFromNetWork) {
                //获取网络视频
                retriever.setDataSource(fileUrl, new HashMap<String, String>());
            } else {
                //获取本地视频
                String filePath = EMWApplication.tempPath + fileName;
                retriever.setDataSource(filePath);
            }
            bitmap = retriever.getFrameAtTime();
            if (bitmap != null) {
                outStream = new FileOutputStream(new File(EMWApplication.tempPath + fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg"));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                outStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    //添加约会、电话、邮件或服务活动
    private void addDate(final UserNote note, ViewGroup otherLayout, TextView contentTv) {
        ArrayList<UserSchedule> scheduleList = note.info.schedule;
        if (scheduleList != null && scheduleList.size() > 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_date, null);
            View typeView = view.findViewById(R.id.view_dynamicdate_type);
            TextView typeTv = (TextView) view.findViewById(R.id.tv_dynamicdate_type);
            TextView timeTv = (TextView) view.findViewById(R.id.tv_dynamicdate_time);
            TextView memberTv = (TextView) view.findViewById(R.id.tv_dynamicdate_member);
            CircleImageView headIv = (CircleImageView) view.findViewById(R.id.iv_dynamicdate_head);
            TextView nameTv = (TextView) view.findViewById(R.id.tv_dynamicdate_name);
            TextView addressTv = (TextView) view.findViewById(R.id.tv_dynamicdate_address);
            TextView descTv = (TextView) view.findViewById(R.id.tv_dynamicdate_desc);
            LinearLayout memberRootLayout = (LinearLayout) view.findViewById(R.id.ll_dynamicdate_memberroot);
            LinearLayout memberLayout = (LinearLayout) view.findViewById(R.id.ll_dynamicdate_member);
            TextView memberNumTv = (TextView) view.findViewById(R.id.ll_dynamicdate_number);
            LinearLayout busDataLayout = (LinearLayout) view.findViewById(R.id.ll_dynamicdate_busdata);
            TextView busDataTv = (TextView) view.findViewById(R.id.tv_dynamicdate_busdata);
            LinearLayout fileLayout = (LinearLayout) view.findViewById(R.id.ll_dynamicdate_file);
            memberTv.setVisibility(View.GONE);
            memberRootLayout.setVisibility(View.GONE);
            final UserSchedule schedule = scheduleList.get(0);
            String title = "";
            final Intent intent = new Intent();
            if (note.Type == UserNoteAddTypes.Appoint) {
//                title = "约会:  ";
                title = "约会";
                typeView.setBackgroundColor(Color.parseColor("#EE5483"));
                intent.setClass(mContext, DateDetailActivity.class);
                if (schedule != null && !TextUtils.isEmpty(schedule.MustActor)) {
                    List<ApiEntity.UserInfo> userList = TaskUtils.getUsers(schedule.MustActor);
                    if (userList.size() > 0) {
                        memberRootLayout.setVisibility(View.VISIBLE);
                        if (userList.size() > 7) {
                            memberNumTv.setText("+"+(userList.size()-7));
                            memberNumTv.setVisibility(View.VISIBLE);
                        } else {
                            memberNumTv.setVisibility(View.GONE);
                        }
                        for (int i = 0,count = userList.size(); i < count; i++) {
                            ApiEntity.UserInfo userInfo = userList.get(i);
                            CircleImageView headCiv = new CircleImageView(mContext);
                            headCiv.setBorderWidth(DisplayUtil.dip2px(mContext, 1));
                            headCiv.setBorderColorResource(R.color.cm_headimg_border);
                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 31), DisplayUtil.dip2px(mContext, 31));
                            if (userInfo != null) {
                                String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
                                if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                                    uri = Const.DOWN_ICON_URL2 + userInfo.Image;
                                }
                                headCiv.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 31);
                                ImageLoader.getInstance().displayImage(uri, new ImageViewAware(headCiv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                            } else {
                                headCiv.setTextBg(0, "", 31);
                            }
                            if (memberLayout.getChildCount() != 0) {
                                param.leftMargin = -DisplayUtil.dip2px(mContext, 10);
                            }
                            memberLayout.addView(headCiv, param);
                            if (i == 6) {
                                break;
                            }
                        }
                    }
                }
                /*if (schedule != null && !TextUtils.isEmpty(schedule.MustActor)) {
                    memberTv.setText("("+schedule.MustActor.split(",").length+")");
                    memberTv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showMemberMenu(view, schedule.MustActor);
                        }
                    });
                } else {
                    memberTv.setText("(0)");
                }
                memberTv.setVisibility(View.VISIBLE);*/
            } else if (note.Type == UserNoteAddTypes.Phone) {
                title = "电话往来";
                typeView.setBackgroundColor(Color.parseColor("#428DC4"));
                intent.setClass(mContext, PhoneDetailActivity.class);
            } else if (note.Type == UserNoteAddTypes.Email) {
                title = "邮件往来";
                typeView.setBackgroundColor(Color.parseColor("#4AC2A4"));
                intent.setClass(mContext, MailDetailActivity.class);
            } else if (note.Type == UserNoteAddTypes.SeviceActive) {
                title = "服务活动";
                typeView.setBackgroundColor(Color.parseColor("#00C4E0"));
                intent.setClass(mContext, ServiceDetailActivity.class);
            }
            view.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    try {
                        intent.putExtra("user_schedule", schedule);
                        intent.putExtra("start_anim", false);
                        int[] location = new int[2];
                        v.getLocationInWindow(location);
                        intent.putExtra("click_pos_y", location[1]);
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {

                    }
                }
            });

            /*if (schedule.StartTime.length() > 10) {
                String time = schedule.StartTime.substring(0, 10);
                timeTv.setText(title + time + "  " + StringUtils.getWeekStr(time));
            } else {
                timeTv.setText(title + schedule.StartTime + "  " + StringUtils.getWeekStr(schedule.StartTime));
            }*/
            if (contentTv != null && TextUtils.isEmpty(note.Content)) {
                contentTv.setText(schedule.Title);
            }
//            nameTv.setText(schedule.T_MainUser);
            typeTv.setText(title);
            timeTv.setText(schedule.OverTime);
            if (!TextUtils.isEmpty(schedule.MainUser) && TextUtils.isDigitsOnly(schedule.MainUser.split(",")[0])) {
                int uid = Integer.valueOf(schedule.MainUser.split(",")[0]);
                UserInfo userInfo = EMWApplication.personMap.get(uid);
                if (userInfo != null) {
                    headIv.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 24);
                    TaskUtils.setCivImageView(mContext, userInfo.Image, headIv);
                } else {
                    headIv.setTextBg(EMWApplication.getIconColor(uid), "", 24);
                }
            }
            nameTv.setText(schedule.Title);
            addressTv.setText(schedule.Place);
            descTv.setText(schedule.Remark);
            if (note.AddType == 6 && note.info.busData != null) {
                busDataTv.setText(note.info.busData.Text);
                busDataLayout.setVisibility(View.VISIBLE);
            } else if (note.AddType == UserNoteAddTypes.File && note.info.File != null) {
                addFileItem(note, fileLayout);
            } else {
                busDataLayout.setVisibility(View.GONE);
                fileLayout.setVisibility(View.GONE);
            }


            otherLayout.addView(view);
            otherLayout.setVisibility(View.VISIBLE);

            /*view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CalendarInfoActivity.class);
                    intent.putExtra(CalendarInfoActivity.CALENDARID, note.TypeId);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.card_activity_open, R.anim.activity_out);
                }
            });*/
        } else {
            otherLayout.setVisibility(View.GONE);
        }
    }

    public void setRadioGroup(final UserNote note, final ViewGroup view) {
        boolean flag = false; // 当前用户是否已投过
        int count = note.info.vote.get(0).Count;
        final String endTime = note.info.vote.get(0).EndTime;
        String countStr = "";
        if (count > 0) {
            countStr = count + "人  ";
        }
        ((TextView) view.findViewById(R.id.tv_dynamicvote_count)).setText(countStr);
        String timeStr = "";
        if (!TextUtils.isEmpty(endTime)) {
            timeStr = endTime;
            ((TextView) view.findViewById(R.id.tv_dynamicvote_count)).setText(countStr+"截止时间" + timeStr);
            if (System.currentTimeMillis() > StringUtils.toDate(endTime).getTime()) {
                flag = true; // 当前时间大于截止时间，不可进行投票
            }
        }
        ArrayList<UserNoteVote> voteList = note.info.vote.get(0).Content;
        if (voteList != null && voteList.size() > 0) {
            for (UserNoteVote unv : voteList) {
                if (unv.IsSelected) {
                    flag = true;
                    break;
                }
            }

            if (voteList.get(0).TP == 1) { //图片投票
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.color.gray_1)
//		                .showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
//		                .showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                        .build(); // 创建配置过得DisplayImageOption对象
                FlowLayout tagsLayout = (FlowLayout) view.findViewById(R.id.fl_dynamicvote_tags);
                if (!TextUtils.isEmpty(voteList.get(0).Tag)) { //显示标签
                    String[] tags = voteList.get(0).Tag.split(",");
                    for (String tag : tags) {
                        TextView tagTv = new TextView(mContext);
                        tagTv.setBackgroundResource(R.drawable.votetag_tv_bg);
                        tagTv.setTextColor(Color.parseColor("#169BFF"));
                        tagTv.setTextSize(12);
                        tagTv.setPadding(DisplayUtil.dip2px(mContext, 10), DisplayUtil.dip2px(mContext, 5), DisplayUtil.dip2px(mContext, 10), DisplayUtil.dip2px(mContext, 5));
                        tagTv.setText(tag);
                        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        if (tagsLayout.getChildCount() != 0) {
                            params.leftMargin = DisplayUtil.dip2px(mContext, 16);
                        }
                        tagsLayout.addView(tagTv, params);
                    }
                    tagsLayout.setVisibility(View.VISIBLE);
                } else {
                    tagsLayout.setVisibility(View.GONE);
                }
                LinearLayout votedLayout = (LinearLayout) view.findViewById(R.id.ll_dynamicvote_voted);
                votedLayout.setOrientation(LinearLayout.HORIZONTAL);
                int imgWidth = (DisplayUtil.getDisplayWidth(mContext) - DisplayUtil.dip2px(mContext, 94))/2;
                for (int i = 0, size = voteList.size(); i < size; i++) {
                    final UserNoteVote unv = voteList.get(i);
                    LayoutParams params = new LayoutParams(imgWidth, LayoutParams.WRAP_CONTENT);
                    View childView = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_imgvote, null);
                    ImageView imgIv = (ImageView) childView.findViewById(R.id.iv_dynamicvote_img);
                    TextView nameTv = (TextView) childView.findViewById(R.id.tv_dynamicvote_name);
                    ProgressBar percentPb = (ProgressBar) childView.findViewById(R.id.pb_dynamicvote_percent);
                    Button voteBtn = (Button) childView.findViewById(R.id.btn_dynamicvote_vote);
                    /*ImageView votedIv = (ImageView) childView.findViewById(R.id.iv_dynamicvote_voted);
                    votedIv.setVisibility(unv.IsSelected ? View.VISIBLE : View.GONE);
                    childView.setBackgroundResource(unv.IsSelected ? R.drawable.dynamic_imgvoted_bg : R.drawable.dynamic_imgvote_bg);*/
                    String url = HelpUtil.getFileURL(unv);
                    Glide.with(mContext).load(url).asGif().centerCrop().placeholder(R.color.gray_1).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgIv);
//                    ImageLoader.getInstance().displayImage(url, imgIv, options);

                    if (count > 0) {
                        int percent = (int) Math.round(unv.Count * 100.0 / count);
//                        nameTv.setText(unv.Text + "(" + percent + "%)");
                        nameTv.setText(unv.Text);
                        percentPb.setProgress(percent);
                    } else {
//                        nameTv.setText(unv.Text + "(0%)");
                        nameTv.setText(unv.Text);
                        percentPb.setProgress(0);
                    }

                    if (flag) {
//                        childView.setOnClickListener(null);
                        voteBtn.setVisibility(View.GONE);
                    } else {
//                        childView.setOnClickListener(new OnClickListener() {
                        voteBtn.setVisibility(View.VISIBLE);
                        voteBtn.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog(mContext).builder().setMsg("确认投票给此选项？")
                                        .setPositiveButton(mContext.getString(R.string.confirm), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!TextUtils.isEmpty(endTime) && System.currentTimeMillis() > StringUtils.toDate(endTime).getTime()) {
                                                    ToastUtil.showToast(mContext, "已超过设定的投票截止时间，无法进行投票！");
                                                    notifyDataSetChanged();
                                                    return;
                                                }

                                                selectVote(note, unv);
                                            }
                                        }).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                            }
                        });
                    }

                    params.topMargin = DisplayUtil.dip2px(mContext, 10);
                    if (i > 0)
                        params.leftMargin = DisplayUtil.dip2px(mContext, 30);
                    votedLayout.addView(childView, params);
                }
            } else { //文字投票
                if (flag) {
                    FlowLayout tagsLayout = (FlowLayout) view.findViewById(R.id.fl_dynamicvote_tags);
                    tagsLayout.setVisibility(View.GONE);
                    LinearLayout votedLayout = (LinearLayout) view.findViewById(R.id.ll_dynamicvote_voted);
                    votedLayout.setOrientation(LinearLayout.VERTICAL);
                    LayoutParams params = new LayoutParams(
                            LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext, 32));
                    for (int i = 0, size = voteList.size(); i < size; i++) {
                        UserNoteVote unv = voteList.get(i);
                        View childView = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_voted, null);
                        View bgView = childView.findViewById(R.id.view_dynamicvote);
                        TextView nameTv = (TextView) childView.findViewById(R.id.tv_dynamicvote_name);
                        TextView percentTv = (TextView) childView.findViewById(R.id.tv_dynamicvote_percent);
                        bgView.setBackgroundResource(unv.IsSelected ? R.drawable.voted_radio_bg : R.drawable.vote_radio_bg);
                        nameTv.setText(unv.Text);
                        if (count > 0) {
                            percentTv.setText(Math.round(unv.Count * 100.0 / count) + "%(" + unv.Count + ")");
                            int percentBg = (DisplayUtil.getDisplayWidth(mContext) - DisplayUtil.dip2px(mContext, 105)) * unv.Count / count;
                            bgView.setLayoutParams(new RelativeLayout.LayoutParams(percentBg, DisplayUtil.dip2px(mContext, 32)));
                        } else {
                            percentTv.setText("0%(0)");
                            bgView.setLayoutParams(new RelativeLayout.LayoutParams(0, DisplayUtil.dip2px(mContext, 32)));
                        }
                        if (i > 0)
                            params.topMargin = DisplayUtil.dip2px(mContext, 8);
                        votedLayout.addView(childView, params);
                    }
                } else {
                    Button button = (Button) view.findViewById(R.id.btn_dynamicvote_vote);
                    ExListView listView = (ExListView) view.findViewById(R.id.lv_dynamicvote_voting);
                    final DynamicVoteAdapter adapter = new DynamicVoteAdapter(mContext, note.UserID, count, flag, button);
                    adapter.setData(voteList);
                    listView.setAdapter(adapter);

//                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(endTime) && System.currentTimeMillis() > StringUtils.toDate(endTime).getTime()) {
                                ToastUtil.showToast(mContext, "已超过设定的投票截止时间，无法进行投票！");
                                notifyDataSetChanged();
                                return;
                            }

                            UserNoteVote checkedRb = adapter.getTargetG();
                            if (checkedRb == null) {
                                ToastUtil.showToast(mContext, R.string.dynamic_select_vote);
                                return;
                            }
                            selectVote(note, checkedRb);
                        }
                    });
                }
            }
        }
    }

    /**
     * 获取投票记录
     *
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
                voteMap.put(note.ID, true);
                Gson gson = new Gson();
                ArrayList<UserNoteVote> voteList = note.info.vote.get(0).Content;
                if (voteList != null && voteList.size() > 0) {
                    //计算每项投票数和当前用户是否已投过
                    for (int i = 0, size = voteList.size(); i < size; i++) {
                        UserNoteVote unv = voteList.get(i);
                        int count = 0;
                        boolean isSelect = false;
                        for (UserNote un : respList) {
                            try {
                                Type typeOfT = new TypeToken<List<Integer>>() {
                                }.getType();
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
    private void selectVote(final UserNote note, UserNoteVote unv) {
        UserNote rev = new UserNote();
        rev.Content = "感谢您投票！";
        rev.PID = note.ID;
        rev.Property = "[" + unv.ID + "]";

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
                    if (Integer.valueOf(result) == -1) {
                        ToastUtil.showToast(mContext, "你已投过！");
                    } else {
                        ToastUtil.showToast(mContext, R.string.dynamic_vote_success, R.drawable.tishi_ico_gougou);
                    }
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
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
     *
     * @param noteID
     */
    private void doCollect(int noteID, final int flag, final View view) {
        API.TalkerAPI.DoEnjoyTalker(noteID, flag, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                ToastUtil.showToast(mContext, flag == 0 ? R.string.dynamicdetail_collect_error : R.string.dynamicdetail_cancelcollect_error);
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
//					ToastUtil.showToast(mContext, flag == 0 ? R.string.dynamicdetail_collect_success:R.string.dynamicdetail_cancelcollect_success, R.drawable.tishi_ico_gougou);
                    if (mUserNote.EnjoyList == null) {
                        mUserNote.EnjoyList = new ArrayList<>();
                    }
                    ApiEntity.TalkerUserInfo user = new ApiEntity.TalkerUserInfo();
                    user.ID = PrefsUtil.readUserInfo().ID;
                    user.Name = PrefsUtil.readUserInfo().Name;
                    user.Image = PrefsUtil.readUserInfo().Image;
                    if (flag == 0) {
                        mUserNote.IsEnjoy = true;
                        mUserNote.EnjoyCount = mUserNote.EnjoyCount + 1;
                        mUserNote.EnjoyList.add(user);
                    } else {
                        mUserNote.IsEnjoy = false;
                        mUserNote.EnjoyCount = mUserNote.EnjoyCount - 1;
                        Iterator<ApiEntity.TalkerUserInfo> it = mUserNote.EnjoyList.iterator();
                        while (it.hasNext()) {
                            if (user.ID == it.next().ID) {
                                it.remove();//注意此处不能用list.remove(it.next());
                            }
                        }
                    }
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    }, 10);
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                            intent.putExtra("col_note", mUserNote);
                            mContext.sendBroadcast(intent); // 刷新Talker列表
                        }
                    }, 500);
                } else {
                    ToastUtil.showToast(mContext, flag == 0 ? R.string.dynamicdetail_collect_error : R.string.dynamicdetail_cancelcollect_error);
                }
            }
        });
    }

    private void addSubReply(final UserNote note, final ViewGroup view, final int pid, final int topID) {
        View child = LayoutInflater.from(mContext).inflate(R.layout.dynamic_detail_item_subreply, null);
        CircleImageView headIv = (CircleImageView) child.findViewById(R.id.iv_dynamicreply_head);
        TextView contentTv = (TextView) child.findViewById(R.id.tv_dynamicreply_content);
        TextView delTv = (TextView) child.findViewById(R.id.tv_dynamicreply_del);
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
        String name = user != null ? user.Name : "?";
        String reply = "\t回复\t";
        String toName = (touser != null ? touser.Name : "?") + "：";
        String lastReply = user != null && user.ID != PrefsUtil.readUserInfo().ID? "    回复":"";
        if (note.Content == null) {
            note.Content = "";
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
        contentTv.setText(spannable);
        child.setTag(name);
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && user.ID != PrefsUtil.readUserInfo().ID) {
                    mContentEt.setTag(note);
                    mContentEt.setHint("回复 " + v.getTag());
                }
            }
        });

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

    private void deleteTalkerRev(final ApiEntity.UserNote un, final int topID) {
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
                    mUserNote.RevCount--;
                    notifyDataSetChanged();

                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    intent.putExtra("note_id", topID);
                    intent.putExtra("delrev_note", un);
                    mContext.sendBroadcast(intent); //刷新Talker列表
                } else {
                    ToastUtil.showToast(mContext, R.string.deletedynamic_error);
                }
            }
        });
    }

    /**
     * 删除动态
     *
     * @param nid
     */
    private void delete(int nid) {
        API.TalkerAPI.DeleteTalker(nid, new RequestCallback<String>(String.class) {
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
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    mContext.sendBroadcast(intent); // 刷新动态列表
                    ((Activity) mContext).finish();
                } else {
                    ToastUtil.showToast(mContext, R.string.deletedynamic_error);
                }
            }
        });
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_detail_pop_share, null);
        Button talkerBtn = (Button) view.findViewById(R.id.btn_popshare_talker);
        Button personBtn = (Button) view.findViewById(R.id.btn_popshare_person);
        Button groupBtn = (Button) view.findViewById(R.id.btn_popshare_group);

        final Dialog bottomSheetDialog = new Dialog(mContext, R.style.MaterialDialogSheet);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
                DisplayUtil.dip2px(mContext, 160));
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();

        talkerBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(mContext, ShareToActivity.class);
                intent.putExtra("user_note", mUserNote);
                intent.putExtra("start_anim", false);
                mContext.startActivity(intent);
            }
        });
        personBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(mContext, ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.RADIO_SELECT);
                intent.putExtra("is_share", true);
                intent.putExtra("share_note", mUserNote);
                intent.putExtra("start_anim", false);
                ((Activity) mContext).startActivityForResult(intent, ContactSelectActivity.RADIO_SELECT);
            }
        });
        groupBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(mContext, GroupSelectActivity.class);
                intent.putExtra("is_all", false);
                intent.putExtra("is_share", true);
                intent.putExtra("share_note", mUserNote);
                intent.putExtra("start_anim", false);
                ((Activity) mContext).startActivityForResult(intent, 99);
            }
        });
    }

    static class TopViewHolder {
        CircleImageView headIv;
        TextView nameTv;
        TextView rolesTv;
        TextView deptTv;
        TextView timeTv;
        TextView contentTv;

        FlowLayout imageLayout;
        CardView otherLayout;
        LinearLayout favourLayout, collectLayout;
        TextView discussTv;
        ThumbUpView collectTuv;
        TextView collectTv;
        TextView shareTv;
        IconTextView moreItv;
        TextView personTv;
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
}
