package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class PhoneBookAdapter extends RecyclerView.Adapter<PhoneBookAdapter.ViewHolder> {

    private Context mContext;
    private List<UserInfo> mDatas;
    private LayoutInflater mInflater;

    public PhoneBookAdapter(Context mContext, List<UserInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setmDatas(List<UserInfo> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.phone_book_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserInfo user = mDatas.get(position);
        holder.nameTv.setText(user.Name);
        holder.phoneTv.setText(user.Phone);
        holder.nameTv.setTextColor(Color.parseColor("#101010"));
        holder.headIv.setTvBg(0, user.Name, 40);

        holder.iconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.Phone.toString()));
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ToastUtil.showToast(mContext, "后续功能，尽请期待");
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView headIv;
        TextView nameTv;
        TextView phoneTv;
        IconTextView iconTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            headIv = (CircleImageView) itemView
                    .findViewById(R.id.iv_contactselect_icon);
            nameTv = (TextView) itemView
                    .findViewById(R.id.tv_contactselect_name);
            phoneTv = (TextView) itemView
                    .findViewById(R.id.tv_contactselect_phone);
            iconTextView = (IconTextView) itemView.findViewById(R.id.itv_contact_item);
        }
    }

}

