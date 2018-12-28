package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.utils.QqFilter;
import cc.emw.mobile.dynamic.DynamicDiscussActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.contral.CommentContral;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserNote;
import sj.keyboard.utils.EmoticonsKeyboardUtils;

/**
 * 动态列表item中直接显示讨论:显示最后5条评论
 */
public class DynamicDiscussAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserNote> mDataList;
    private int noteID;
    private int rootPosition;

    public DynamicDiscussAdapter(Context context, List<UserNote> dataList, int noteID, int rootPosition) {
        this.mContext = context;
        this.mDataList = new ArrayList<>();
        this.mDataList.addAll(dataList);
        this.noteID = noteID;
        this.rootPosition = rootPosition;
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
    public View getView(final int position, View contentView, ViewGroup arg2) {
        ViewHolder vh;
        if (contentView == null) {
            vh = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.dynamic_detail_item_subreply, null);
            vh.contentTv = (TextView) contentView.findViewById(R.id.tv_dynamicreply_content);
            vh.deleteTv = (TextView) contentView.findViewById(R.id.tv_dynamicreply_del);
            contentView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) contentView.getTag(R.id.tag_first);
        }
        vh.deleteTv.setVisibility(View.GONE);
        final UserNote un = getItem(position);
        ApiEntity.TalkerUserInfo user = un.UserIdInfo;
        ApiEntity.TalkerUserInfo touser = un.ToUserIdInfo;

        if (un.ToUserId > 0) {
            String name = user != null? user.Name : "?";
            String reply = "\t回复\t";
            String toName = (touser != null? touser.Name : "?")+"：";
            SpannableString spanStr = new SpannableString(name + reply + toName + un.Content);
            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamicreply_name_text));
            ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamicreply_name_text));
            spanStr.setSpan(colorSpan1, 0, name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spanStr.setSpan(colorSpan2, name.length()+reply.length(), name.length()+reply.length()+toName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            Spannable spannable = QqFilter.spannableFilter(mContext,
                    spanStr,
                    name + reply + toName + un.Content,
                    EmoticonsKeyboardUtils.getFontHeight(vh.contentTv),
                    null);
            vh.contentTv.setText(spannable);
        } else {
            String name = (user != null? user.Name : "?")+"：";
            SpannableString spanStr = new SpannableString(name + un.Content);
            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.dynamicreply_name_text));
            spanStr.setSpan(colorSpan1, 0, name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            Spannable spannable = QqFilter.spannableFilter(mContext,
                    spanStr,
                    name + un.Content,
                    EmoticonsKeyboardUtils.getFontHeight(vh.contentTv),
                    null);
            vh.contentTv.setText(spannable);
        }

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
        return contentView;
    }

    class ViewHolder {
        TextView contentTv;
        TextView deleteTv;
    }

}
