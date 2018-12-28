package cc.emw.mobile.dynamic.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.ViewSwitcher;

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
import java.util.Timer;
import java.util.TimerTask;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.GroupSelectActivity;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.dynamic.DateDetailActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.DynamicDiscussActivity;
import cc.emw.mobile.dynamic.MailDetailActivity;
import cc.emw.mobile.dynamic.PhoneDetailActivity;
import cc.emw.mobile.dynamic.ServiceDetailActivity;
import cc.emw.mobile.dynamic.ShareToActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.UserNoteFile;
import cc.emw.mobile.entity.UserNote.UserNoteLink;
import cc.emw.mobile.entity.UserNote.UserNoteShareTo;
import cc.emw.mobile.entity.UserNote.UserNoteVote;
import cc.emw.mobile.entity.UserNote.UserRootVote;
import cc.emw.mobile.file.FilePreviewActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.contral.CommentContral;
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
import cc.emw.mobile.view.DynamicTextView;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.PinnedSectionListView;
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;
import cc.emw.mobile.view.ThumbUpView;
import cc.emw.mobile.view.expandablelayout.ExpandableLayout;

//import com.lzy.ninegrid.NineGridView;
//import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

/**
 * 首页列表Adapter
 *
 * @author shaobo.zhuang
 */
public class DynamicAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

    private final String TAG = DynamicAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<UserNote> mDataList;
    private ArrayList items;
    private Dialog mLoadingDialog;
    private DisplayImageOptions options;
    private SparseBooleanArray voteMap; //标记是否需要请求投票记录, 代替HashMap<Integer, Boolean>性能更优
    private SparseBooleanArray animMap; //标记是否执行过动画
    private SparseBooleanArray expandMap; //标记是否展开评论列表
    private boolean hasDiscuss; //是否可以直接评论
    String content = "";
    public DynamicAdapter(Context context, ArrayList<UserNote> dataList) {
        this(context, dataList, false);
    }
    public DynamicAdapter(Context context, ArrayList<UserNote> dataList, boolean hasDiscuss) {
        this.mContext = context;
        this.mDataList = dataList;
        this.hasDiscuss = hasDiscuss;
        items = new ArrayList();
        voteMap = new SparseBooleanArray();
        animMap = new SparseBooleanArray();
        expandMap = new SparseBooleanArray();

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

    public void setData(ArrayList<UserNote> dataList) {
        mDataList = dataList;
        items.clear();
        for (int i = 0, count = mDataList.size(); i < count; i++) {
            UserNote un = mDataList.get(i);
            if (i > 0 && StringUtils.isSameDate(StringUtils.toDate(un.CreateTime), StringUtils.toDate(mDataList.get(i - 1).CreateTime))) {
                items.add(un);
            } else {
                items.add(un.CreateTime);
                items.add(un);
            }
        }
    }

    public void setDataList(ArrayList<UserNote> dataList) {
        if (dataList != null && dataList.size() >0) {
            mDataList = dataList;
            items.clear();
            items.addAll(dataList);
        }
    }

    public void clearVoteMap() {
        if (voteMap != null)
            voteMap.clear();
        if (animMap != null)
            animMap.clear();
        if (expandMap != null)
            expandMap.clear();

    }

    private CommentContral mCommentContral;

    public void setCommentContral(CommentContral commentContral) {
        this.mCommentContral = commentContral;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
     public int getItemViewType(int position) {
         return items.get(position) instanceof UserNote ? 1 : 0;
     }

    @Override
     public int getViewTypeCount() {
         return 2;
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
    public View getView(final int position,View convertView,final ViewGroup parent) {
        switch (getItemViewType(position)) {
            case 0:
                final TopViewHolder tvh;
                if (convertView == null) {
                    tvh = new TopViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamic_group, null);
                    tvh.textTv = (TextView) convertView.findViewById(R.id.tv_dynamic_groupdate);
                    convertView.setTag(R.id.tag_first, tvh);
                } else {
                    tvh = (TopViewHolder) convertView.getTag(R.id.tag_first);
                }
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
                String dateStr = StringUtils.friendly_time4((String)items.get(position));
                if (dateStr.contains("\n")) {
                    params.topMargin = -DisplayUtil.dip2px(mContext, 3);
                    params.bottomMargin = DisplayUtil.dip2px(mContext, 5);
                    tvh.textTv.setLayoutParams(params);

                    SpannableString spanStr = new SpannableString(dateStr);
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#85B2F6"));
                    spanStr.setSpan(colorSpan, 0, dateStr.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.16F);
                    spanStr.setSpan(sizeSpan, 0, dateStr.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tvh.textTv.setText(spanStr);
                } else { //今天、昨天、前天
                    params.topMargin = -DisplayUtil.dip2px(mContext, 3);
                    params.bottomMargin = DisplayUtil.dip2px(mContext, 12);
                    tvh.textTv.setLayoutParams(params);

                    tvh.textTv.setText(dateStr);
                }
                break;
            case 1:
                final ViewHolder vh;
                if (convertView == null) {
                    vh = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamic_root, null);
//                    vh.dateLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamic_date);
//                    vh.dateTv = (TextView) convertView.findViewById(R.id.tv_dynamic_date);
                    vh.typeItv = (ImageView) convertView.findViewById(R.id.itv_dynamic_type);//类型图像
                    vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_dynamic_head);//头像
                    vh.nameTv = (TextView) convertView.findViewById(R.id.tv_dynamic_name);//名称
                    vh.rolesTv = (TextView) convertView.findViewById(R.id.tv_dynamic_roles);//职业
                    vh.deptTv = (TextView) convertView.findViewById(R.id.tv_dynamic_dept);//描述
                    vh.locationTv = (TextView) convertView.findViewById(R.id.tv_dynamic_location);//位置

                    vh.dynamicTextView = (DynamicTextView) convertView.findViewById(R.id.desc_dynamic_tv_all);//内容，全文，展开全部
                   // vh.contentTv = (TextView) convertView.findViewById(R.id.tv_dynamic_content);
                   // vh.showAllContentTv = (TextView) convertView.findViewById(R.id.tv_dynamic_content_showall);
                    //文件、链接、投票、日程、工作分派、工作计划根Layout
                    vh.otherLayout = (CardView) convertView.findViewById(R.id.ll_dynamic_other);
                    //图片集的根Layout
                    vh.imageLayout = (FlowLayout) convertView.findViewById(R.id.fl_dynamic_image);
                    vh.videoLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamic_video);
                    //点赞根Layout
                    vh.favourLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamic_favour);//评论人员相关评论信息 头像+评论语
                    vh.nodiscussLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamic_nodiscuss);//评论UI界面
                    vh.expandableLayout = (ExpandableLayout) convertView.findViewById(R.id.expandable_dynamic_discuss);//评论人员评论信息列表显示出来
                    vh.timeTv = (TextView) convertView.findViewById(R.id.tv_dynamic_time);//动态时间
                    vh.discussTv = (TextView) convertView.findViewById(R.id.tv_dynamic_discuss);//评论显示数量
                    vh.collectLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamic_collect);//收藏layout
                    vh.collectTuv = (ThumbUpView) convertView.findViewById(R.id.tuv_dynamic_collect);//收藏图标
                    vh.collectTv = (TextView) convertView.findViewById(R.id.tv_dynamic_collect);//收藏数量
                    vh.shareTv = (TextView) convertView.findViewById(R.id.tv_dynamic_share);//分享,转发
                    vh.moreItv = (IconTextView) convertView.findViewById(R.id.itv_dynamic_more);//更多
                    vh.personTv = (TextView) convertView.findViewById(R.id.tv_dynamic_person);
                    vh.discussLv = (ListView) convertView.findViewById(R.id.lv_dynamic_discuss);//评论

                    vh.animLayout = (LinearLayout) convertView.findViewById(R.id.ll_dynamic_anim);
                    vh.set = anim();
                    convertView.setTag(R.id.tag_first, vh);
                } else {
                    vh = (ViewHolder) convertView.getTag(R.id.tag_first);
                }
                vh.otherLayout.removeAllViews();
                vh.imageLayout.removeAllViews();
                vh.videoLayout.removeAllViews();
                vh.favourLayout.removeAllViews();
                vh.otherLayout.setVisibility(View.GONE);
                vh.imageLayout.setVisibility(View.GONE);
                vh.videoLayout.setVisibility(View.GONE);
                final UserNote un = (UserNote) items.get(position);
                if (un.UserIdInfo != null) {
                    String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(un.UserIdInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : un.UserIdInfo.CompanyCode, un.UserIdInfo.Image);
                    if (un.UserIdInfo.Image != null && un.UserIdInfo.Image.startsWith("/")) {
                        uri = Const.DOWN_ICON_URL2 + un.UserIdInfo.Image;
                    }
                    vh.headIv.setTextBg(EMWApplication.getIconColor(un.UserIdInfo.ID), un.UserIdInfo.Name);
                    ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                    vh.nameTv.setText(un.UserIdInfo.Name);
                    vh.deptTv.setText(un.UserIdInfo.Job);
                } else {
                    vh.headIv.setTextBg(EMWApplication.getIconColor(un.UserID), "");
                    vh.nameTv.setText("");
                    vh.deptTv.setText("");
                }
                vh.headIv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (EMWApplication.personMap != null && EMWApplication.personMap.get(un.UserID) != null) {
                            Intent intent = new Intent(mContext, PersonInfoActivity.class);
                            intent.putExtra("intoTag", 1);
                            intent.putExtra("UserInfo", EMWApplication.personMap.get(un.UserID));
                            intent.putExtra("start_anim", false);
                            int[] location = new int[2];
                            v.getLocationInWindow(location);
                            intent.putExtra("click_pos_y", location[1]);
                            mContext.startActivity(intent);
                        }
                    }
                });
                /*if (un.Roles != null && un.Roles.size() > 0) {
                    StringBuilder roleStr = new StringBuilder();
                    for (int i = 0, count = un.Roles.size(); i < count; i++) {
                        roleStr.append(un.Roles.get(i).Name).append("、");
                    }
                    roleStr.deleteCharAt(roleStr.length() - 1);
                    vh.rolesTv.setText(roleStr.toString());
                } else {
                    vh.rolesTv.setText("");
                }*/
                vh.locationTv.setText("");
                String createTime = un.CreateTime;
                vh.timeTv.setText(StringUtils.friendly_time(createTime));

                //String content = "";
                if (!TextUtils.isEmpty(un.Content)) {
                    content = Html.fromHtml(un.Content).toString();
                    vh.dynamicTextView.setFlag(false);
                    vh.dynamicTextView.setDesc(content,BufferType.NORMAL);
                    vh.dynamicTextView.setVisibility(View.VISIBLE);
                    //vh.contentTv.setText(content);
                    //vh.contentTv.setVisibility(View.VISIBLE);
                } else {
                     vh.dynamicTextView.setVisibility(View.GONE);
                   // vh.contentTv.setVisibility(View.GONE);
                }
                vh.discussTv.setText(String.valueOf(un.RevCount));
                //评论图标监听事件
                vh.discussTv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        Intent intent = new Intent(MainActivity.ACTION_TALKER_COMMENT);
                        intent.putExtra("is_show", true);
//                            intent.putExtra("user_note", un);
                        intent.putExtra("note_id", un.ID);
                        mContext.sendBroadcast(intent);

                    }
                });
                /*
                vh.discussTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {*/
                    /*Intent intent = new Intent(MainActivity.ACTION_TALKER_COMMENT);
                    intent.putExtra("is_show", true);
                    intent.putExtra("user_note", un);
                    intent.putExtra("note_id", un.ID);
                    mContext.sendBroadcast(intent);
                    if (mCommentContral != null) {
                        mCommentContral.editTextBodyVisible(View.VISIBLE, 0, position, 0, un.ID, un);
                    }*/
                  /*      Intent intent = new Intent(mContext, DynamicDiscussActivity.class);
                        intent.putExtra("user_note", un);
                        intent.putExtra("start_anim", false);
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        intent.putExtra("click_pos_y", location[1]);
                        mContext.startActivity(intent);
                    }
                });
                */
                vh.collectTv.setText(String.valueOf(un.EnjoyCount));
                vh.shareTv.setText(String.valueOf(un.ShareCount));
                /*
                vh.shareTv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        openBottomSheet(un);
                    }
                });
                */
                //转发点击事件
                vh.shareTv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        Intent intent = new Intent(mContext, ShareToActivity.class);
                        intent.putExtra("user_note", un);
                        intent.putExtra("start_anim", false);
                        mContext.startActivity(intent);
                        //openBottomSheet(un);
                    }
                });
                //删除自己的动态更多-------是否删除当前动态
                vh.moreItv.setVisibility(un.UserID == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.INVISIBLE);
                vh.moreItv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        RightMenu mMenu = new RightMenu(mContext);
                        mMenu.addItem(R.string.dynamicdetail_more_delete, 1);
                        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
                            @Override
                            public void selected(View view, PopMenu.Item item, final int position) {
                                switch (item.id) {
                                    case 1:
                                        new AlertDialog(mContext).builder().setMsg(mContext.getString(R.string.deletedynamic_tips_title))
                                                .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                                                .setPositiveButton(mContext.getString(R.string.deletedynamic_tips_delete), new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        delete(un);
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

                //        Drawable left = mContext.getResources().getDrawable(un.IsEnjoy ? R.drawable.index_ico_sel_shoucang : R.drawable.index_ico_dianzan);
                //        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
                //        vh.collectTv.setCompoundDrawables(left, null, null, null);
                //判断那一行需要爱心动画
                if (vh.collectTuv.getTag() == null) {
                    if (un.IsEnjoy)
                        vh.collectTuv.setLike();
                    else
                        vh.collectTuv.setUnlike();
                } else {
                    if (un.IsEnjoy)
                        vh.collectTuv.Like();
                    else
                        vh.collectTuv.UnLike();
                    vh.collectTuv.setTag(null);
                }
                //点击收藏，收藏具体实现方法
                vh.collectLayout.setOnClickListener(new NoDoubleClickListener() {

                    @Override
                    public void onNoDoubleClick(View v) {
                        doCollect(un, un.IsEnjoy ? 1 : 0, vh.collectTuv);
                    }
                });
                /*//收藏人员
                if (un.EnjoyList != null && un.EnjoyList.size() > 0) {
                    vh.favourLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < un.EnjoyList.size(); i++) {
                        if (un.EnjoyList.size() - i - 5 > 0) //超过5个只需显示最后5个
                            continue;
                        ApiEntity.TalkerUserInfo user = un.EnjoyList.get(i);
                        final CircleImageView imgview = new CircleImageView(mContext);
                        imgview.setBorderWidth(DisplayUtil.dip2px(mContext, 1));
                        imgview.setBorderColorResource(R.color.cm_headimg_border);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 21), DisplayUtil.dip2px(mContext, 21));
                        String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);
                        ImageLoader.getInstance().displayImage(url, new ImageViewAware(imgview), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                        if (vh.favourLayout.getChildCount() != 0)
                            params.leftMargin = -DisplayUtil.dip2px(mContext, 4);
                        vh.favourLayout.addView(imgview, params);
                    }
                    TextView countTv = new TextView(mContext);
                    countTv.setTextSize(12);
                    countTv.setTextColor(mContext.getResources().getColor(R.color.dynamic_count_text));
                    countTv.setText("等觉得很赞");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.leftMargin = DisplayUtil.dip2px(mContext, 10);
                    vh.favourLayout.addView(countTv, params);
                } else {
                    vh.favourLayout.setVisibility(View.GONE);
                }*/

                if (un.RevInfo != null && un.RevInfo.size() > 0) {
                    vh.favourLayout.setVisibility(View.VISIBLE);
                    vh.nodiscussLayout.setVisibility(View.GONE);
                    RecyclerView recyclerView = new RecyclerView(mContext);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.addItemDecoration(new SpaceItemDecoration(-DisplayUtil.dip2px(mContext, 10)));
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                vh.favourLayout.performClick();
                            }
                            return false;
                        }
                    });
                    final ArrayList<ApiEntity.UserNote> revList = new ArrayList<>();
                    ArrayList<Integer> uidList = new ArrayList<>();
                    for (int i = 0; i < un.RevInfo.size(); i++) {
                        ApiEntity.TalkerUserInfo user = un.RevInfo.get(i).UserIdInfo;
                        if (user != null) {
                            if (!uidList.contains(user.ID)) { //过滤相同的用户头像
                                uidList.add(user.ID);
                                revList.add(un.RevInfo.get(i));
                            } else {
                                int index = uidList.indexOf(user.ID);
                                uidList.remove(index);
                                revList.remove(index);
                                uidList.add(user.ID);
                                revList.add(un.RevInfo.get(i));
                            }
                        } else {

                        }
                    }
                    final TextSwitcher textSwitcher = new TextSwitcher(mContext);
                    textSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                    textSwitcher.setOutAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out));
                    textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                        @Override
                        public View makeView() {
                            TextView countTv = new TextView(mContext);
                            countTv.setMaxLines(1);
                            countTv.setTextSize(14);
                            countTv.setTextColor(Color.parseColor("#757575"));
//                            ChatUtils.spannableEmoticonFilter(countTv, un.RevInfo.get(un.RevInfo.size() - 1).Content);
                            return countTv;
                        }
                    });
                    if (revList.size() > 5) {
                        int count = revList.size() - 5;
                        for (int i = 0; i < count; i++) {
                            revList.remove(0); //超过5个只需显示最后5个
                        }
                    }
                    final RecyclerViewAdapter adapter = new RecyclerViewAdapter(mContext, revList);
                    recyclerView.setAdapter(adapter);
                    if (revList.size() > 1 ) {
                        final Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                adapter.notifyItemMoved(revList.size()-1, 0);
                                ApiEntity.UserNote lastItem = revList.get(revList.size()-1);
                                revList.add(0, lastItem);
                                revList.remove(revList.size()-1);
                                ApiEntity.UserNote rev = revList.get(revList.size()-1);
                                if ((TextUtils.isEmpty(rev.Content) || TextUtils.isEmpty(rev.Content.trim())) && rev.AddType == ApiEnum.UserNoteAddTypes.Image) {
                                    textSwitcher.setText(ChatUtils.getEmoticonSpannable(textSwitcher, "[图片]"));
                                } else {
                                    textSwitcher.setText(ChatUtils.getEmoticonSpannable(textSwitcher, rev.Content));
                                }
                            }
                        };
                        Timer mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0);
                            }
                        }, 3*1000, 3*1000);
                    }
                    vh.favourLayout.addView(recyclerView);
                    ApiEntity.UserNote revs = un.RevInfo.get(un.RevInfo.size() - 1);
                    if ((TextUtils.isEmpty(revs.Content) || TextUtils.isEmpty(revs.Content.trim())) && revs.AddType == ApiEnum.UserNoteAddTypes.Image) {
                        textSwitcher.setText(ChatUtils.getEmoticonSpannable(textSwitcher, "[图片]"));
                    } else {
                        textSwitcher.setText(ChatUtils.getEmoticonSpannable(textSwitcher, revs.Content));
                    }
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
                    param.leftMargin = DisplayUtil.dip2px(mContext, 10);
                    param.rightMargin = DisplayUtil.dip2px(mContext, 10);
                    vh.favourLayout.addView(textSwitcher, param);
                    /* 评论区，添加右侧评论图标
                    ImageView discussIv = new ImageView(mContext);
                    discussIv.setImageResource(R.drawable.dynamic_ic_discuss);
                    vh.favourLayout.addView(discussIv);
                    discussIv.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            if (hasDiscuss) {
                                View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_reply, null);
                                popupView.setMinimumWidth(DisplayUtil.getDisplayWidth(mContext)-DisplayUtil.dip2px(mContext, 73));
                                final PopupWindow window = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                popupView.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        window.dismiss();
                                        Intent intent = new Intent(MainActivity.ACTION_TALKER_COMMENT);
                                        intent.putExtra("is_show", true);
//                                  intent.putExtra("user_note", un);
                                        intent.putExtra("note_id", un.ID);
                                        mContext.sendBroadcast(intent);
                                    }
                                });
                                window.setOutsideTouchable(true);
                                window.setFocusable(false);
                                window.setAnimationStyle(R.style.popup_more_anim);
                                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupView.measure(0, 0);
                                int xoff = -popupView.getMeasuredWidth() + v.getHeight();
                                int yoff = -(popupView.getMeasuredHeight() + v.getHeight()) / 2;
                                window.showAsDropDown(v, xoff, yoff);
                            } else {
                                Intent intent = new Intent(mContext, DynamicDetailActivity.class);
                                intent.putExtra("note_id", un.ID);
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                    */

                    //评论人员区---点击事件
                    vh.favourLayout.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View view) {
                            if (vh.expandableLayout.isExpanded()) {
                                vh.expandableLayout.collapse();
                                expandMap.put(un.ID, false);
                            } else {
                                vh.expandableLayout.expand();
                                expandMap.put(un.ID, true);
                            }
                        }
                    });
                    if (expandMap.get(un.ID)) {
                        vh.expandableLayout.expand();
                    } else {
                        vh.expandableLayout.collapse();
                    }

                    vh.discussLv.setVisibility(View.VISIBLE);
                    DynamicDiscussAdapter3 adapter3 = new DynamicDiscussAdapter3(mContext, revList, un.ID, position);
                    vh.discussLv.setAdapter(adapter3);
                    vh.discussLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(mContext, DynamicDiscussActivity.class);
                            intent.putExtra("user_note", un);
                            if (view.getTag(R.id.tag_second) != null) {
                                intent.putExtra("rev_note", (ApiEntity.UserNote)view.getTag(R.id.tag_second));
                            }
                            intent.putExtra("start_anim", false);
                            mContext.startActivity(intent);
                        }
                    });
                } else {
                    vh.discussLv.setVisibility(View.GONE);
                    vh.favourLayout.setVisibility(View.GONE);
                    vh.nodiscussLayout.setVisibility(View.GONE);
                    vh.nodiscussLayout.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            if (hasDiscuss) {
                                Intent intent = new Intent(MainActivity.ACTION_TALKER_COMMENT);
                                intent.putExtra("is_show", true);
//                            intent.putExtra("user_note", un);
                                intent.putExtra("note_id", un.ID);
                                mContext.sendBroadcast(intent);
                            } else {
                                Intent intent = new Intent(mContext, DynamicDetailActivity.class);
                                intent.putExtra("note_id", un.ID);
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                }

                HelpUtil.setDynamicType(un, vh.typeItv);
               // addTypes(un, vh.otherLayout, vh.imageLayout, vh.videoLayout, vh.contentTv);
                addTypes(un, vh.otherLayout, vh.imageLayout, vh.videoLayout, vh.dynamicTextView);

                convertView.setTag(R.id.tag_second, un);

                if (!animMap.get(position)) { //是否执行列表动画
                    vh.animLayout.startAnimation(vh.set);
                    animMap.put(position, true);
                }
                break;
        }
        return convertView;
    }

    /**
     * 根据不同类型展示布局
     */
    //添加类型
   // private void addTypes(UserNote un, ViewGroup otherLayout, ViewGroup imageLayout, ViewGroup videoLayout, TextView contentTv) {
    private void addTypes(UserNote un, ViewGroup otherLayout, ViewGroup imageLayout, ViewGroup videoLayout, DynamicTextView dyTV) {
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
                        addVideo(un, videoLayout);
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
                    addShareTo(un, otherLayout, imageLayout, videoLayout);
                    break;
                case UserNoteAddTypes.Appoint: //约会
                case UserNoteAddTypes.Phone: //电话
                case UserNoteAddTypes.Email: //邮件
                case UserNoteAddTypes.SeviceActive: //服务活动
                   // addDate(un, otherLayout, contentTv);
                    addDate(un, otherLayout, dyTV);
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
            TextView sure_time = view.findViewById(R.id.tv_dynamicschedule_sure_time);
            TextView startTv = (TextView) view.findViewById(R.id.tv_dynamicschedule_starttime);
            TextView endTv = (TextView) view.findViewById(R.id.tv_dynamicschedule_endtime);
            DynamicTextView dynamicTextView = (DynamicTextView)view.findViewById(R.id.tv_dynamicschedule_desc);
            final UserSchedule schedule = scheduleList.get(0);
            if(schedule.OverTime.length() > 10){
                sure_time.setText(schedule.OverTime);
            }else {
                sure_time.setText(schedule.OverTime+" 00:00");
            }
            startTv.setText("标题"+schedule.Title);
            if(schedule.Remark != null && !TextUtils.isEmpty(schedule.Remark)){
                dynamicTextView.setVisibility(View.VISIBLE);
                dynamicTextView.setDescTextColor("#757575");
                dynamicTextView.setDesc(schedule.Remark,BufferType.NORMAL);
            }else{
                dynamicTextView.setVisibility(View.GONE);
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
        ArrayList<UserFenPai> fenPaiList = un.info.task;
        if (fenPaiList != null && fenPaiList.size() > 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_allot, null);
            TextView timeTv = (TextView) view.findViewById(R.id.tv_dynamicallot_time);
            CircleImageView headIv = (CircleImageView) view.findViewById(R.id.iv_dynamicallot_head);
            ExListView listView = (ExListView) view.findViewById(R.id.lv_dynamicallot_list);
            LinearLayout fileLayout = (LinearLayout) view.findViewById(R.id.ll_dynamicallot_file);
            UserFenPai task = fenPaiList.get(0);
            timeTv.setText(task.FinishTime);
            if (!TextUtils.isEmpty(task.MainUser)) {
                String[] mains = task.MainUser.split(",");
                if (!TextUtils.isEmpty(mains[0]) && TextUtils.isDigitsOnly(mains[0]) && EMWApplication.personMap != null) {
                    int uid = Integer.valueOf(mains[0]);
                    UserInfo user = EMWApplication.personMap.get(uid);
                    if (user != null) {
                        headIv.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 24);
                        TaskUtils.setCivImageView(mContext, user.Image, headIv);
                    } else {
                        headIv.setTextBg(EMWApplication.getIconColor(uid), "", 24);
                    }
                }
            }
            DynamicAllotAdapter adapter = new DynamicAllotAdapter(mContext, un);
            listView.setAdapter(adapter);
            //添加附件
            if (un.AddType == UserNoteAddTypes.File && un.info.File != null) {
                addFileItem(un, fileLayout);
            } else {
                fileLayout.setVisibility(View.GONE);
            }
            otherLayout.addView(view);
            otherLayout.setVisibility(View.VISIBLE);
        } else {
            otherLayout.setVisibility(View.GONE);
        }
    }

    //添加工作计划
    private void addWorkPlan(UserNote un, ViewGroup otherLayout) {
        final ArrayList<UserPlan> planList = un.info.log;
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

    //添加转发，分享显示视图
    public void addShareTo(UserNote note, ViewGroup otherLayout, ViewGroup imgLayout, ViewGroup videoLayout) {
        UserNote shareNote = note.info.shareNote;
        UserNoteShareTo shareTo = null;
        ArrayList<UserNoteShareTo> shareToList = note.info.shareTo;
        if (shareToList != null && shareToList.size() > 0) {
            shareTo = shareToList.get(0);
        }
        if (shareNote != null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_sharenote, null, false);
            final TextView contentTv = (TextView) view.findViewById(R.id.tv_dynamicshare_content);
            //显示评论区
            DynamicTextView dynamicTextView = (DynamicTextView) view.findViewById(R.id.share_desc_dynamic_tv_all);
            //配置被分享人的头像，姓名和时间
            CircleImageView otherUserImage = (CircleImageView) view.findViewById(R.id.share_iv_dynamic_head);//头像
            TextView shareNameTv = (TextView) view.findViewById(R.id.share_tv_dynamic_name);//姓名
            TextView shareDateTv = (TextView) view.findViewById(R.id.share_tv_dynamic_date);//日期
            TextView shareTimeTv = (TextView) view.findViewById(R.id.share_tv_dynamic_time);//时间
            if(shareNote.UserIdInfo != null){
                String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(shareNote.UserIdInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : shareNote.UserIdInfo.CompanyCode, shareNote.UserIdInfo.Image);
                if (shareNote.UserIdInfo.Image != null && shareNote.UserIdInfo.Image.startsWith("/")) {
                    uri = Const.DOWN_ICON_URL2 + shareNote.UserIdInfo.Image;
                }
                otherUserImage.setTextBg(EMWApplication.getIconColor(shareNote.UserIdInfo.ID), shareNote.UserIdInfo.Name);
                ImageLoader.getInstance().displayImage(uri, new ImageViewAware(otherUserImage), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
            }else {
                otherUserImage.setTextBg(EMWApplication.getIconColor(shareNote.UserID), "");
            }
            shareNameTv.setText(shareNote.UserName);
            String dataStr = shareNote.CreateTime;
            if(dataStr.contains(" ")){
                String [] arrayStr = dataStr.split(" ");
                shareDateTv.setText(arrayStr[0]);
                shareTimeTv.setText(arrayStr[1]);
            }

            CardView shareLayout = (CardView) view.findViewById(R.id.ll_dynamicshare_other);
            FlowLayout imageLayout = (FlowLayout) view.findViewById(R.id.fl_dynamicshare_image);
            addTypes(shareNote, shareLayout, imageLayout, videoLayout, null);
            String name = shareNote.UserName + "：";
            String content = !TextUtils.isEmpty(shareNote.Content) ? Html.fromHtml(shareNote.Content).toString() : "";
            ArrayList<UserSchedule> scheduleList = shareNote.info.schedule;
            if (scheduleList != null && scheduleList.size() > 0) {
                content = scheduleList.get(0).Title;
            }

            SpannableString spanStr = new SpannableString(name + content); //姓名颜色改为蓝色
            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamicshareto_name_text));
            spanStr.setSpan(colorSpan1, 0, name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            contentTv.setText(spanStr);
            contentTv.setVisibility(View.GONE);

            dynamicTextView.setDesc(content,BufferType.NORMAL);//显示被分享人的评论
            int typeCode = shareNote.AddType;
            //普通动态，图片 视频动态显示被转发人评论
            if(typeCode != 0 && typeCode != 2 && typeCode != 15){
                dynamicTextView.setVisibility(View.GONE);
            }else {
                if(shareNote.info.schedule == null){
                    dynamicTextView.setVisibility(View.VISIBLE);
                }else {
                    dynamicTextView.setVisibility(View.GONE);
                }
            }

            imgLayout.addView(view);//添加转发视图
            imgLayout.setVisibility(View.VISIBLE);

            view.setTag(shareNote.ID);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DynamicDetailActivity.class);
                    intent.putExtra("note_id", Integer.valueOf(v.getTag().toString()));
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            });
        } else if (shareTo != null && shareTo.NoteID > 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_shareto, null, false);
            final TextView nameTv = (TextView) view.findViewById(R.id.tv_dynamicshareto_name);
            TextView contentTv = (TextView) view.findViewById(R.id.tv_dynamicshareto_content);
            ImageView iconIv = (ImageView) view.findViewById(R.id.iv_dynamicshareto_icon);
            iconIv.setVisibility(View.GONE);
            nameTv.setVisibility(View.GONE);
            contentTv.setText("原内容已被删除");
            otherLayout.addView(view);
            otherLayout.setVisibility(View.VISIBLE);

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
    }

    //添加投票方法
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

    //添加约会、电话、邮件或服务活动
   // private void addDate(final UserNote note, ViewGroup otherLayout, TextView contentTv) {
    private void addDate(final UserNote note, ViewGroup otherLayout, DynamicTextView dyTV) {
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
                        schedule.ID = note.TypeId;
                        schedule.NoteAddPriority = note.AddProperty;
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

            if (dyTV != null && TextUtils.isEmpty(note.Content)){
                dyTV.setFlag(false);
                dyTV.setDesc(schedule.Title, BufferType.NORMAL);
            }
          //  if (contentTv != null && TextUtils.isEmpty(note.Content)) {
           //     contentTv.setText(schedule.Title);
           // }

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
            //添加附件或业务数据
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
    /*private void addImage2(final UserNote note, ViewGroup imageLayout) {
        final ArrayList<UserNoteFile> imageList = note.info.File;
        if (imageList != null && imageList.size() > 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_image, null, false);
            NineGridView nineGridView = (NineGridView) view.findViewById(R.id.nineGrid);
            ArrayList<com.lzy.ninegrid.ImageInfo> imageInfo = new ArrayList<>();
            for (UserNoteFile file : imageList) {
                com.lzy.ninegrid.ImageInfo info = new com.lzy.ninegrid.ImageInfo();
                String url = HelpUtil.getFileURL(file);
                info.setThumbnailUrl(url);
                info.setBigImageUrl(url);
                imageInfo.add(info);
            }
            nineGridView.setAdapter(new NineGridViewClickAdapter(mContext, imageInfo));
            imageLayout.addView(view);
            imageLayout.setVisibility(View.VISIBLE);
        }
    }*/
    //添加图片
    private void addImage(final UserNote note, ViewGroup imageLayout) {
        final ArrayList<UserNoteFile> imageList = note.info.File;
        if (imageList != null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.color.gray_1)
                    .delayBeforeLoading(100).bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true).resetViewBeforeLoading(true)
                    .imageScaleType(ImageScaleType.EXACTLY) // 图像将完全按比例缩小的目标大小
//                    .showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
//                    .showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
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
            Log.e("zrjtsss", "start --file -----Url = "+file.Url);
            if (file.Url.startsWith("/")) {
                fileUrl = Const.BASE_URL + "/" + "Resource" + file.Url;
            } else if (file.Url.startsWith("\\")) {
                fileUrl = Const.BASE_URL + "/" + "Resource" + file.Url.replaceAll("\\\\", "/");
            } else {
                fileUrl = Const.BASE_URL + "/" + "Resource/" + companyCode + "/UserFile/" + file.Url;
            }
            jcVideoPlayer.setUp(fileUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "");
            Log.e("zrjtsss", "fileUrl = "+fileUrl);
            String coverPath = fileUrl.substring(0, fileUrl.lastIndexOf(".")) + ".jpg";
            Picasso.with(mContext).load(coverPath)
                    .config(Bitmap.Config.ALPHA_8)
                    .into(jcVideoPlayer.thumbImageView);
            Log.e("zrjtsss", coverPath);

//            final String fileName = file.Url.substring(file.Url.lastIndexOf("/") + 1);
//            String videoThumbPath = EMWApplication.tempPath + fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg";
//            if (FileUtil.hasFile(videoThumbPath)) {
//                Picasso.with(mContext).load("file://" + videoThumbPath).error(R.drawable.trans_bg).into(jcVideoPlayer.thumbImageView);
//            } else {
//                jcVideoPlayer.thumbImageView.setImageResource(R.drawable.trans_bg);
//                jcVideoPlayer.thumbImageView.setTag(fileUrl);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final Bitmap bitmap = getFirstVideoFrame(true, fileUrl, fileName);
//                        jcVideoPlayer.thumbImageView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (fileUrl.equals(jcVideoPlayer.thumbImageView.getTag())) {
//                                    if (bitmap != null) {
//                                        jcVideoPlayer.thumbImageView.setImageBitmap(bitmap);
//                                    }
//                                }
//                            }
//                        });
//                    }
//                }).start();
//            }
            imageLayout.addView(jcVideoPlayer);
            imageLayout.setVisibility(View.VISIBLE);
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
//        ((TextView) view.findViewById(R.id.tv_dynamicvote_count)).setText("匿名投票：" + count + "人  " + "截止到" + timeStr);
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
                int imgWidth = (DisplayUtil.getDisplayWidth(mContext) - DisplayUtil.dip2px(mContext, 135))/2;
                for (int i = 0, size = voteList.size(); i < size; i++) {
                    final UserNoteVote unv = voteList.get(i);
                    LayoutParams params = new LayoutParams(imgWidth, LayoutParams.WRAP_CONTENT);
                    View childView = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_imgvote, null);
//                    RoundedImageView imgIv = (RoundedImageView) childView.findViewById(R.id.iv_dynamicvote_img);
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
                        nameTv.setText(unv.Text + "(" + percent + "%)");
//                        nameTv.setText(unv.Text);
                        percentPb.setProgress(percent);
                    } else {
                        nameTv.setText(unv.Text + "(0%)");
//                        nameTv.setText(unv.Text);
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
            public void onStarted() {
                mLoadingDialog = DialogUtil.createLoadingDialog(mContext, R.string.loading_dialog_tips3);
                mLoadingDialog.show();
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
     * @param un
     */
    private void doCollect(final UserNote un, final int flag, final ThumbUpView view) {
        API.TalkerAPI.DoEnjoyTalker(un.ID, flag, new RequestCallback<String>(String.class) {
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
//                    ToastUtil.showToast(mContext, flag == 0 ? R.string.dynamicdetail_collect_success : R.string.dynamicdetail_cancelcollect_success, R.drawable.tishi_ico_gougou);
                    if (un.EnjoyList == null) {
                        un.EnjoyList = new ArrayList<>();
                    }
                    ApiEntity.TalkerUserInfo user = new ApiEntity.TalkerUserInfo();
                    user.ID = PrefsUtil.readUserInfo().ID;
                    user.Name = PrefsUtil.readUserInfo().Name;
                    user.Image = PrefsUtil.readUserInfo().Image;
                    if (flag == 0) {
                        un.IsEnjoy = true;
                        un.EnjoyCount = un.EnjoyCount + 1;
                        un.EnjoyList.add(user);
                    } else {
                        un.IsEnjoy = false;
                        un.EnjoyCount = un.EnjoyCount - 1;
                        Iterator<ApiEntity.TalkerUserInfo> it = un.EnjoyList.iterator();
                        while (it.hasNext()) {
                            if (user.ID == it.next().ID) {
                                it.remove();//注意此处不能用list.remove(it.next());
                            }
                        }
                    }
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setTag(true);
                            notifyDataSetChanged();
                        }
                    }, 10);
                } else {
                    ToastUtil.showToast(mContext, flag == 0 ? R.string.dynamicdetail_collect_error : R.string.dynamicdetail_cancelcollect_error);
                }
            }
        });
    }

    /**
     * 删除动态
     *
     * @param un
     */
    private void delete(final UserNote un) {
        API.TalkerAPI.DeleteTalker(un.ID, new RequestCallback<String>(String.class) {
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
                    setData(mDataList);
                    notifyDataSetChanged();
                } else {
                    ToastUtil.showToast(mContext, R.string.deletedynamic_error);
                }
            }
        });
    }

    private void showMemberMenu(View anchor, String uids) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_menu, null);
        view.setFocusableInTouchMode(true);

        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        ListView listView = (ListView) view.findViewById(R.id.menu_listview);
        listView.setBackgroundResource(R.drawable.date_lv_bg);
        ColorDrawable divider = new ColorDrawable(Color.parseColor("#1A000000"));
        listView.setDivider(divider);
        listView.setDividerHeight(DisplayUtil.dip2px(mContext, 1));
        DynamicMemberAdapter adapter = new DynamicMemberAdapter(mContext, TaskUtils.getUsers(uids));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                popupWindow.dismiss();
            }
        });
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        popupWindow.showAsDropDown(anchor);
    }

    private void openBottomSheet(final UserNote un) {
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
                intent.putExtra("user_note", un);
                intent.putExtra("start_anim", false);
                mContext.startActivity(intent);
            }
        });
        personBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                if (mContext instanceof BaseActivity) {
                    ((BaseActivity) mContext).setStartAnim(true);
                }
                Intent intent = new Intent(mContext, ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.RADIO_SELECT);
                intent.putExtra("is_share", true);
                intent.putExtra("share_note", un);
                intent.putExtra("start_anim", false);
                ((Activity) mContext).startActivityForResult(intent, ContactSelectActivity.RADIO_SELECT);
            }
        });
        groupBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                if (mContext instanceof BaseActivity) {
                    ((BaseActivity) mContext).setStartAnim(true);
                }
                Intent intent = new Intent(mContext, GroupSelectActivity.class);
                intent.putExtra("is_all", false);
                intent.putExtra("is_share", true);
                intent.putExtra("share_note", un);
                intent.putExtra("start_anim", false);
                ((Activity) mContext).startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 0;
    }

    static class TopViewHolder {
        TextView textTv;
    }

    static class ViewHolder {
        LinearLayout dateLayout;
        TextView dateTv;
//        IconTextView typeItv;
        ImageView typeItv;
        CircleImageView headIv;
        TextView nameTv;
        TextView rolesTv;
        TextView deptTv;
        TextView locationTv;
        DynamicTextView dynamicTextView;
       // TextView contentTv;
        //TextView showAllContentTv;
        CardView otherLayout;
        LinearLayout videoLayout;
        FlowLayout imageLayout;
        LinearLayout favourLayout, collectLayout, nodiscussLayout;
        ExpandableLayout expandableLayout;
        TextView timeTv;
        TextView discussTv;
        ThumbUpView collectTuv;
        TextView collectTv;
        TextView shareTv;
        IconTextView moreItv;
        TextView personTv;
        ListView discussLv;

        LinearLayout animLayout;
        AnimationSet set;
    }

    private AnimationSet anim() {
        // 动画集合
        AnimationSet set = new AnimationSet(false);
        // 缩放动画
        ScaleAnimation scale = new ScaleAnimation(0.8f, 1, 0.8f, 1,
                Animation.RELATIVE_TO_SELF, 0.8f, Animation.RELATIVE_TO_SELF,
                0.8f);
        scale.setDuration(700);// 动画时间
        scale.setFillAfter(true);// 保持动画状态
        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0.6f, 1);
        alpha.setDuration(400);// 动画时间
        alpha.setFillAfter(true);// 保持动画状态
        set.addAnimation(scale);
        set.addAnimation(alpha);
        return set;
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

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildPosition(view) != 0)
                outRect.left = space;
        }
    }
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerHolder> {
        private List<ApiEntity.UserNote> dataList;
        private Context mContext;
        private RecyclerView recyclerView;

        public RecyclerViewAdapter(Context context, List<ApiEntity.UserNote> dataList) {
            mContext = context;
            this.dataList = dataList;
        }

        @Override
        public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_view, parent, false);
            RecyclerHolder holder = new RecyclerHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, final int position) {
            ApiEntity.TalkerUserInfo user = dataList.get(position).UserIdInfo;
            if (user != null) {
                String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);
                holder.textView.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 32);
                ImageLoader.getInstance().displayImage(url, new ImageViewAware(holder.textView), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
            } else {
//                holder.textView.setImageResource(R.drawable.cm_img_head);
                holder.textView.setTextBg(0, "", 32);
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            this.recyclerView = recyclerView;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class RecyclerHolder extends RecyclerView.ViewHolder {
            CircleImageView textView;

            public RecyclerHolder(View itemView) {
                super(itemView);
                textView = (CircleImageView) itemView.findViewById(R.id.image1);
            }
        }
    }
}
