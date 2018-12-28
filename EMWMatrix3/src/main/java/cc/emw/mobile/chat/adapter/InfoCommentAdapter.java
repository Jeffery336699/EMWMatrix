package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brucetoo.imagebrowse.ImageBrowseFragment;
import com.brucetoo.imagebrowse.widget.ImageInfo;
import com.brucetoo.imagebrowse.widget.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zf.iosdialog.widget.AlertDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.utils.QqFilter;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.file.FilePreviewActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.contral.CommentContral;
import cc.emw.mobile.net.ApiEntity.UserNote;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.FlowLayout;
import sj.keyboard.utils.EmoticonsKeyboardUtils;

/**
 *  右侧通知栏动态评论列表Adatper
 */
public class InfoCommentAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserNote> mDataList;
    private DisplayImageOptions options;

    public InfoCommentAdapter(Context context, List<UserNote> dataList) {
        this.mContext = context;
        this.mDataList = new ArrayList<>();
        this.mDataList.addAll(dataList);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
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
        if (mDataList == null) {
            return 0;
        } else if (mDataList.size() > 3) {
            return 3;
        } else {
            return mDataList.size();
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.info_item_comment, null);
            vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_dynamicdetail_head);
            vh.contentTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_content);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) convertView.getTag(R.id.tag_first);
        }
        final UserNote revs = (UserNote) getItem(position);
        String name = "";
        String image = "";
        if (EMWApplication.personMap != null && EMWApplication.personMap.get(revs.UserID) != null) {
            UserInfo user = EMWApplication.personMap.get(revs.UserID);
            name = user.Name;
            image = user.Image;
        }
        ChatUtils.spannableEmoticonFilter(vh.contentTv, revs.Content);
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
        ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
        convertView.setTag(R.id.tag_second, name);

        return convertView;
    }


    static class ViewHolder {
        CircleImageView headIv;
        TextView contentTv;
    }
}
