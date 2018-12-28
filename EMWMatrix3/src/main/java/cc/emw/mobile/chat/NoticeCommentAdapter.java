package cc.emw.mobile.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.entity.NoticeComment;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * Created by ${zrjt} on 2016/9/19.
 */
public class NoticeCommentAdapter extends BaseAdapter {

    private Context context;
    private List<NoticeComment.CommentInfo> noticeComments;
    private DisplayImageOptions options;

    public NoticeCommentAdapter(Context context, List<NoticeComment.CommentInfo> noticeComments) {
        this.context = context;
        this.noticeComments = noticeComments;
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象

    }

    @Override
    public int getCount() {
        return noticeComments == null ? 0 : noticeComments.size();
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
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.info_item_comment, null);
            vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_dynamicdetail_head);
            vh.contentTv = (TextView) convertView.findViewById(R.id.tv_dynamicdetail_content);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) convertView.getTag(R.id.tag_first);
        }
        final NoticeComment.CommentInfo noticeComment = noticeComments.get(position);
        ChatUtils.spannableEmoticonFilter(vh.contentTv, noticeComment.newContent);
        vh.headIv.setTvBg(EMWApplication.getIconColor(noticeComment.userID), noticeComment.userName, 30);
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, noticeComment.urls);
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.headIv), options,
                new ImageSize(DisplayUtil.dip2px(context, 40), DisplayUtil.dip2px(context, 40)), null, null);
        return convertView;
    }

    static class ViewHolder {
        CircleImageView headIv;
        TextView contentTv;
    }
}
