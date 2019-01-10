package cc.emw.mobile.contact.adapter;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.contact.bean.PersonalLikeBean;
import cc.emw.mobile.util.UiUtil;


public class PersonalLikeAdapter extends BaseMultiItemQuickAdapter<PersonalLikeBean, BaseViewHolder> {


    public PersonalLikeAdapter(List<PersonalLikeBean> data) {
        super(data);

        addItemType(1, R.layout.item_smart_refresh);
        addItemType(2, R.layout.item_smart_refresh_2);
    }

    @Override
    protected void convert(BaseViewHolder helper, PersonalLikeBean item) {
        switch (helper.getItemViewType()) {
            case 1:
                Log.d("junjun","type1"+item.getTitle());

                helper.setText(R.id.title, item.getTitle());
                break;
            case 2:
                Log.d("junjun","我进来了type2");

               RecyclerView mRecyclerView= helper.itemView.findViewById(R.id.recycle_view2);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setNestedScrollingEnabled(false);
                LinearLayoutManager ms= new LinearLayoutManager(mContext);
                mRecyclerView.addItemDecoration(new SpacingItemDecoration(UiUtil.dip2px(mContext,20 )));
                ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                mRecyclerView.setLayoutManager(ms);
                mRecyclerView.setAdapter(new PersonalTypeLikeAdapter(R.layout.item_personal_like,item.getList()));
                break;
        }

    }


    public static class SpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spacing;

        public SpacingItemDecoration( int spacing) {
            this.spacing = spacing;

        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            if(position!=0){
                outRect.left= spacing/3;
            }

        }
    }
}

