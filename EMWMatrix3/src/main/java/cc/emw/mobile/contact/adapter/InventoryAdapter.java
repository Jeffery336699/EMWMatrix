package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.bean.CiclerItemBean;

/**
 * 清单列表adapter
 * <p>
 * Created by DavidChen on 2018/5/30.
 */

public class InventoryAdapter extends BaseRecyclerViewAdapter<CiclerItemBean.ItemData> {

    private OnDeleteClickLister mDeleteClickListener;
    public static final String TAG_HAVEREAD="tag_haveread";
    public static final String TAG_LIKE="tag_like";
    public static final String TAG_DELETE="tag_delete";

   public InventoryAdapter(Context context, List<CiclerItemBean.ItemData> data, int layoutId) {
        super(context, data, layoutId);
    }


    public void setOnDeleteClickListener(OnDeleteClickLister listener) {
        this.mDeleteClickListener = listener;
    }

    @Override
    protected void onBindData(RecyclerViewHolder holder, CiclerItemBean.ItemData bean, int position) {
        View view = holder.getView(R.id.tvDelete);
        view.setTag(position);
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteClickListener != null) {
                        mDeleteClickListener.onTagClick(v, (Integer) v.getTag(),TAG_DELETE);
                    }
                }
            });
        }

        View tvHaveRead = holder.getView(R.id.tvHaveRed);
        tvHaveRead.setTag(position);
        if (!tvHaveRead.hasOnClickListeners()) {
            tvHaveRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteClickListener != null) {
                        mDeleteClickListener.onTagClick(v, (Integer) v.getTag(),TAG_HAVEREAD);
                    }
                }
            });
        }

        View tvLike = holder.getView(R.id.tvLike);
        tvLike.setTag(position);
        if (!tvLike.hasOnClickListeners()) {
            tvLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteClickListener != null) {
                        mDeleteClickListener.onTagClick(v, (Integer) v.getTag(),TAG_LIKE);
                    }
                }
            });
        }


    }


    public interface OnDeleteClickLister {
        void onTagClick(View view, int position,String tagType);
    }
}
