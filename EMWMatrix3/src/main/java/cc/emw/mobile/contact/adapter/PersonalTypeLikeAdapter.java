package cc.emw.mobile.contact.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.contact.bean.PersonalLikeBean;


public class PersonalTypeLikeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public PersonalTypeLikeAdapter(int item_personal_like, @Nullable List<String> data) {
        super(item_personal_like,data);
    }


    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}

