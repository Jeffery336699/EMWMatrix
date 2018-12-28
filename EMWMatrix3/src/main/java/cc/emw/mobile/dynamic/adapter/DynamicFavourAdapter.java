package cc.emw.mobile.dynamic.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * 滑动面板赞
 */
public class DynamicFavourAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ApiEntity.TalkerUserInfo> mDataList;
    private DisplayImageOptions options;
    private Dialog mLoadingDialog;
    private ApiEntity.UserNote note;

    public DynamicFavourAdapter(Context context, ApiEntity.UserNote note) {
        this.mContext = context;
        this.mDataList = new ArrayList<>();
        this.note = note;
        if (note.EnjoyList != null)
            this.mDataList.addAll(note.EnjoyList);

        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setData(ArrayList<ApiEntity.TalkerUserInfo> dataList) {
        this.mDataList = dataList;
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
    public View getView(final int position, View contentView, ViewGroup parent) {
        ViewHolder vh;
        if (contentView == null) {
            vh = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamic_favour, null);
            vh.headIv = (CircleImageView) contentView.findViewById(R.id.iv_dynamicfavour_head);
            vh.nameTv = (TextView) contentView.findViewById(R.id.tv_dynamicfavour_name);
            vh.concernedTv = (TextView) contentView.findViewById(R.id.tv_dynamicfavour_concerned);
            vh.concernTv = (TextView) contentView.findViewById(R.id.tv_dynamicfavour_concern);
            contentView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) contentView.getTag(R.id.tag_first);
        }

        final ApiEntity.TalkerUserInfo user = mDataList.get(position);
        String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);
        if (user.Image != null && user.Image.startsWith("/")) {
            url = Const.DOWN_ICON_URL2 + user.Image;
        }
        vh.headIv.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 30);
        ImageLoader.getInstance().displayImage(url, new ImageViewAware(vh.headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
        vh.headIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EMWApplication.personMap != null && EMWApplication.personMap.get(user.ID) != null) {
                    Intent intent = new Intent(mContext, PersonInfoActivity.class);
                    intent.putExtra("intoTag", 1);
                    intent.putExtra("UserInfo", EMWApplication.personMap.get(user.ID));
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            }
        });
        vh.nameTv.setText(user.Name);
        vh.concernedTv.setVisibility(user.IsFollow ? View.VISIBLE : View.GONE);
        vh.concernTv.setVisibility(user.IsFollow ? View.GONE : View.VISIBLE);
        vh.concernTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog(mContext).builder().setMsg(mContext.getString(R.string.follow_tips))
                        .setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            doFollow(user);
                            }
                        }).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        });

        return contentView;
    }

    class ViewHolder {
        CircleImageView headIv;
        TextView nameTv;
        TextView concernedTv;
        TextView concernTv;

    }

    private void doFollow(final ApiEntity.TalkerUserInfo sUser) {
        List<Integer> fuids = new ArrayList<Integer>();
        fuids.add(sUser.ID);
        API.UserAPI.DoFollow(fuids, 1, new RequestCallback<String>(String.class) {

                @Override
                public void onStarted() {
                    mLoadingDialog = DialogUtil.createLoadingDialog(mContext, R.string.loading_dialog_tips3);
                    mLoadingDialog.show();
                }

                @Override
                public void onSuccess(String responseInfo) {
                    mLoadingDialog.dismiss();
                    if (responseInfo != null && "1".equals(responseInfo)) {
                        ToastUtil.showToast(mContext, R.string.person_follow_success, R.drawable.tishi_ico_gougou);
                        sUser.IsFollow = true;
                        notifyDataSetChanged();
                        Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                        intent.putExtra("col_note", note);
                        mContext.sendBroadcast(intent); // 刷新Talker列表
                    } else if (responseInfo != null && "0".equals(responseInfo)) {
                        ToastUtil.showToast(mContext, R.string.person_follow_error);
                    } else {
                        Toast.makeText(mContext, "超时！", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable arg0, boolean arg1) {
                    mLoadingDialog.dismiss();
                    ToastUtil.showToast(mContext, "关注失败");
                }
            }

        );
    }
}
