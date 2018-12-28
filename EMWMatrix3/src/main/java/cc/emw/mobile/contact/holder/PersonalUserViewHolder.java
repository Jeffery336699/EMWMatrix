package cc.emw.mobile.contact.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.contact.base.BaseViewHolder;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

public class PersonalUserViewHolder extends BaseViewHolder {

    public CircleImageView headIv;
    public View isOnline;
    public TextView nameTv;
    public IconTextView iconTextView;
    public TextView tvTitle;

    public PersonalUserViewHolder(Context ctx, View itemView, int viewType) {
        super(ctx, itemView, viewType);
        headIv = (CircleImageView) itemView
                .findViewById(R.id.iv_contactselect_icon);
        nameTv = (TextView) itemView
                .findViewById(R.id.tv_contactselect_name);
        isOnline = itemView.findViewById(R.id.view_contact_is_online);
        iconTextView = (IconTextView) itemView.findViewById(R.id.itv_contact_item);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
    }

    @Override
    public int getGroupViewResId() {
        return R.id.group;
    }

    @Override
    public int getChildViewResId() {
        return R.id.child;
    }
}
