package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * Created by tao.zhou on 2017/6/24.
 */

public class PersonNewFriendAdapter extends BaseAdapter {

    private Context mContext;

    private List<ApiEntity.PubConApply> mDataList;

    public PersonNewFriendAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setMDataList(List<ApiEntity.PubConApply> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_add_friend_search, null);
            vh.mSearchUserImg = (CircleImageView) convertView.findViewById(R.id.civ_add_friend_search);
            vh.mSearchName = (TextView) convertView.findViewById(R.id.tv_add_friend_name);
            vh.mSearchSameNum = (TextView) convertView.findViewById(R.id.tv_same_friend_num);
            vh.mTvAdd = (TextView) convertView.findViewById(R.id.tv_add_friend);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final ApiEntity.PubConApply user = mDataList.get(position);

        String uri = String.format(Const.DOWN_ICON_URL, user.CompanyCode, user.Image);

        Picasso.with(mContext)
                .load(uri)
                .resize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40))
                .centerCrop()
                .config(Bitmap.Config.ALPHA_8)
                .placeholder(R.drawable.cm_img_head)
                .error(R.drawable.cm_img_head)
                .into(vh.mSearchUserImg);

        vh.mSearchName.setText(user.Name);

        vh.mSearchSameNum.setText(user.SameCount == 0 ? "共同好友：无" : "共同好友：" + user.SameCount + "人");

        if (user.State == 0) {
            vh.mTvAdd.setText("接受");
            vh.mTvAdd.setTextColor(Color.parseColor("#1DA1F3"));
        } else if (user.State == 1) {
            vh.mTvAdd.setText("已添加");
            vh.mTvAdd.setTextColor(Color.parseColor("#989898"));
        }

        vh.mTvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doApplyFriend(user.ID, 1, vh.mTvAdd);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        CircleImageView mSearchUserImg;
        TextView mSearchName, mSearchSameNum, mTvAdd;
    }

    /**
     * 处理好友申请
     *
     * @param conId
     * @param state
     */
    private void doApplyFriend(final int conId, int state, final TextView mText) {
        API.UserPubAPI.DoPubConApply(conId, state, new RequestCallback<String>(String.class) {

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    if (EMWApplication.personMap != null && EMWApplication.personMap.get(conId) != null) {
                        EMWApplication.personMap.get(conId).IsFollow = true;
                    }
                    mText.setText("已添加");
                    mText.setTextColor(Color.parseColor("#989898"));
                    Toast.makeText(mContext, "处理成功!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, "处理失败!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
