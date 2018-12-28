package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserNote.UserNoteVote;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;

/**
 * 未投票adapter
 */
public class DynamicVoteAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserNoteVote> mDataList;
    private int lastPosition = -1;
    private int uid;
    private int count = 1; //投票数
    private boolean voted; //是否已投票
    private Button button; //投票按钮

    private UserNoteVote targetG;

    public DynamicVoteAdapter(Context context, int uid, int count, boolean voted, Button button) {
        this.mContext = context;
        this.uid = uid;
        this.count = count;
        this.voted = voted;
        this.button = button;
    }

    public UserNoteVote getTargetG() {
        return targetG;
    }

    public void setData(ArrayList<UserNoteVote> mDataList) {
        this.mDataList = mDataList;
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
    public View getView(final int position, View contentView, ViewGroup arg2) {
        ViewHolder vh;
        if (contentView == null) {
            vh = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_unvote, null);
            vh.positionTv = (TextView) contentView.findViewById(R.id.tv_dynamicvote_position);
            vh.nameTv = (TextView) contentView.findViewById(R.id.tv_dynamicvote_name);
            vh.percentTv = (TextView) contentView.findViewById(R.id.tv_dynamicvote_percent);
            vh.selectCb = (CheckBox) contentView.findViewById(R.id.cb_dynamicvote_select);
            vh.tempView = contentView.findViewById(R.id.view_dynamicvote_temp);
            contentView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) contentView.getTag(R.id.tag_first);
        }

        UserNoteVote unv = mDataList.get(position);

        vh.positionTv.setText(position + 1 + ".");
        vh.nameTv.setText(unv.Text);
        if (uid == PrefsUtil.readUserInfo().ID) {
            vh.percentTv.setText(Math.round(unv.Count * 100.0 / count) + "%");
            vh.percentTv.setVisibility(View.VISIBLE);
//            vh.selectCb.setVisibility(View.VISIBLE);
            vh.tempView.setVisibility(View.VISIBLE);
        } else {
            vh.percentTv.setVisibility(View.GONE);
//            vh.selectCb.setVisibility(voted ? View.INVISIBLE : View.VISIBLE);
            vh.tempView.setVisibility(View.GONE);
        }

        vh.selectCb.setChecked(lastPosition == position ? true : false);
//        contentView.setBackgroundResource(lastPosition == position ? R.drawable.voteing_radio_bg2 : R.drawable.voteing_radio_bg1);
        contentView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /*if (lastPosition != position) {
                    lastPosition = position;
                    targetG = mDataList.get(position);
                    notifyDataSetChanged();

                    button.setEnabled(true);
                    button.setText("投票");
                    button.setTextColor(Color.WHITE);
                    button.setBackgroundResource(R.drawable.dynamic_button_bg2);
                }*/
                targetG = mDataList.get(position);
                button.performClick();
            }
        });
        return contentView;
    }

    class ViewHolder {
        TextView positionTv;
        TextView nameTv;
        TextView percentTv;
        CheckBox selectCb;
        View tempView;
    }

}
