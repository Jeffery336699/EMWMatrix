package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.utils.QqFilter;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.main.contral.CommentContral;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserNote;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.view.CircleImageView;
import sj.keyboard.utils.EmoticonsKeyboardUtils;

/**
 * 动态列表item中直接显示讨论:显示最后5条评论
 */
public class DynamicDiscussAdapter3 extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserNote> mDataList;
    private DisplayImageOptions options;
    private int noteID;
    private int rootPosition;

    public DynamicDiscussAdapter3(Context context, List<UserNote> dataList, int noteID, int rootPosition) {
        this.mContext = context;
        this.mDataList = new ArrayList<>();
        this.mDataList.addAll(dataList);
        this.noteID = noteID;
        this.rootPosition = rootPosition;

        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setData(ArrayList<UserNote> dataList) {
        this.mDataList = dataList;
    }

    private CommentContral mCommentContral;
    public void setCirclePublicCommentContral(CommentContral commentContral) {
        this.mCommentContral = commentContral;
    }

    @Override
    public int getCount() {
//        return mDataList == null ? 0 : mDataList.size();
        if (mDataList == null) {
            return 0;
        } else if (mDataList.size() < 5) {
            return mDataList.size();
        } else {
            return 5; //最多显示5条评论
        }
    }

    @Override
    public UserNote getItem(int position) {
//        return mDataList.get(position);
        if (mDataList.size() > 5) {
            return mDataList.get(mDataList.size() - 5 + position); //显示最后5条评论
        } else {
            return mDataList.get(position);
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamic_discuss, null);
            vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_dynamicdetail_head);
            vh.timeTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_time);
            vh.nameTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_name);
            vh.contentTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_content);
            vh.delTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_del);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) convertView.getTag(R.id.tag_first);
        }
        vh.delTv.setVisibility(View.GONE);
        convertView.setTag(R.id.tag_second, null);
        final UserNote un = getItem(position);
        ApiEntity.TalkerUserInfo user = un.UserIdInfo;
        ApiEntity.TalkerUserInfo touser = un.ToUserIdInfo;
        if (user != null) {
            if (un.ToUserId > 0) {
                String name = user != null ? user.Name : "?";
                String reply = "\t回复\t";
                String toName = touser != null ? touser.Name : "?";
                SpannableString spanStr = new SpannableString(name + reply + toName);
                ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.cm_text));
                ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.cm_text));
                ForegroundColorSpan colorSpan3 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamic_time_text));
                spanStr.setSpan(colorSpan1, 0, name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spanStr.setSpan(colorSpan2, name.length() + reply.length(), name.length() + reply.length() + toName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spanStr.setSpan(colorSpan3, name.length(), name.length()+reply.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.9f);
                spanStr.setSpan(sizeSpan, name.length(), name.length()+reply.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                Spannable spannable = QqFilter.spannableFilter(mContext,
                        spanStr,
                        name + reply + toName,
                        EmoticonsKeyboardUtils.getFontHeight(vh.contentTv),
                        null);
                vh.nameTv.setText(spannable);
            } else {
                vh.nameTv.setTextColor(mContext.getResources().getColor(R.color.cm_text));
                vh.nameTv.setText(user.Name);
            }

            String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(un.UserIdInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : un.UserIdInfo.CompanyCode, un.UserIdInfo.Image);
            if (un.UserIdInfo.Image != null && un.UserIdInfo.Image.startsWith("/")) {
                uri = Const.DOWN_ICON_URL2 + un.UserIdInfo.Image;
            }
            vh.headIv.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 26);
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);

            if (user.ID != PrefsUtil.readUserInfo().ID) {
                convertView.setTag(R.id.tag_second, un);
            }
        } else {
            vh.headIv.setTextBg(un.UserID, "", 26);
            vh.nameTv.setText("");
        }
        if (TextUtils.isEmpty(un.Content) && un.AddType == ApiEnum.UserNoteAddTypes.Image) {
            ChatUtils.spannableEmoticonFilter(vh.contentTv, "[图片]");
        } else {
            ChatUtils.spannableEmoticonFilter(vh.contentTv, un.Content);
        }
        vh.timeTv.setText(StringUtils.friendly_time(un.CreateTime));
        /*child.setTag(name);
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentEt.setTag(note);
                mContentEt.setHint("回复 " + v.getTag());
            }
        });*/
        /*contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.ACTION_TALKER_COMMENT);
//                intent.putExtra("is_show", true);
//                intent.putExtra("user_note", un);
//                intent.putExtra("is_subrev", true);
//                intent.putExtra("note_id", noteID);
//                mContext.sendBroadcast(intent);
                if (mCommentContral != null) {
                    mCommentContral.editTextBodyVisible(View.VISIBLE, 1, rootPosition, position, noteID, un);
                }
            }
        });*/
        return convertView;
    }

    class ViewHolder {
        TextView timeTv;
        CircleImageView headIv;
        TextView nameTv;
        TextView contentTv;
        TextView delTv;
    }

}
