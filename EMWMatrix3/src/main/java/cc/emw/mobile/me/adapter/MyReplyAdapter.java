package cc.emw.mobile.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.dynamic.adapter.DynamicDiscussAdapter2;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.view.ListScroller;
import cc.emw.mobile.view.MyListView;

/**
 * Created by tao.zhou on 2017/7/13.
 */

public class MyReplyAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserNote> mDataList;

    public MyReplyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<UserNote> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder vh = null;
        if (vh == null) {
            vh = new MyViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_my_reply, parent, false);
            vh.mLvItem = (MyListView) convertView.findViewById(R.id.lv_my_reply_item);
            convertView.setTag(vh);
        } else {
            vh = (MyViewHolder) convertView.getTag();
        }
        UserNote userNote = mDataList.get(position);
        List<ApiEntity.UserNote> mData = new ArrayList<>();
        if (userNote.RevInfo != null) {
            for (ApiEntity.UserNote un : userNote.RevInfo) {
                if (un.PID == userNote.ID) {
                    mData.add(un);
                }
            }
        } else {
            userNote.RevInfo = new ArrayList<>();
        }
        DynamicDiscussAdapter2 adapter2 = new DynamicDiscussAdapter2(mContext, mData, null);
        adapter2.setAllData(userNote.RevInfo);
        adapter2.setDetailID(userNote.ID);
        vh.mLvItem.setAdapter(adapter2);
        vh.mLvItem.setOnScrollListener(new ListScroller(mContext));//滚动事件监听
        vh.mLvItem.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        vh.mLvItem.setEnabled(false);
        return convertView;
    }

    private class MyViewHolder {
        MyListView mLvItem;
    }
}
