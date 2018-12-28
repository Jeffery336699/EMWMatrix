package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.model.bean.HistoryMessage;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.view.CircleImageView;

/**
 * Created by ${zrjt} on 2016/10/28.
 */
public class ChatHistoryAdapters extends RecyclerView.Adapter<ChatHistoryAdapters.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<HistoryMessage> mDataList;
    private List<Integer> onLineList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private DisplayImageOptions option;
    public String name = "";
    public String image = "";

    public ChatHistoryAdapters(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        initOptions();
    }

    public void setmDataList(List<HistoryMessage> mDataList) {
        this.mDataList = mDataList;
    }

    public void setOnLineList(List<Integer> onLineList) {
        this.onLineList = onLineList;
    }

    private void initOptions() {
        option = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head)
                .showImageForEmptyUri(R.drawable.cm_img_head)
                .showImageOnFail(R.drawable.cm_img_head).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true).build();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_grouphead)
                .showImageForEmptyUri(R.drawable.cm_img_grouphead)
                .showImageOnFail(R.drawable.cm_img_grouphead).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true).build();
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.listitem_chat_histories, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, final int position) {
        String uri;
        final HistoryMessage msg = mDataList.get(position);
        vh.line.setVisibility(position == mDataList.size() - 1 ? View.GONE : View.VISIBLE);
        if (msg.getMessage() != null) {
            // 群组消息
            if (mDataList.get(position).getType() == 2) {
                vh.online.setVisibility(View.GONE);
                if (msg.getMessage().Group != null) {
                    name = msg.getMessage().Group.Name;
                    image = msg.getMessage().Group.Image;
                }
                if (TextUtils.isEmpty(image)) {
                    vh.head.setImageResource(R.drawable.cm_img_grouphead);
                } else {
                    uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode,
                            image);
                    imageLoader.displayImage(uri, new ImageViewAware(vh.head), options,
                            new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                    image = "";
                }
            } else {
                vh.online.setVisibility(View.VISIBLE);
                if (msg.Receiver != null) {
                    name = msg.Receiver.Name;
                    image = msg.Receiver.Image;
                    if (onLineList != null && onLineList.contains(msg.Receiver.ID))
                        vh.online.setImageResource(R.drawable.circle_is_online);
                    else
                        vh.online.setImageResource(R.drawable.circle_is_not_online);
                    if (TextUtils.isEmpty(image)) {
                        vh.head.setImageResource(R.drawable.cm_img_head);
                    } else {
                        uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode,
                                image);
                        imageLoader.displayImage(uri, new ImageViewAware(vh.head), option,
                                new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                        image = "";
                    }
                }
            }

            vh.time.setText(StringUtils.friendly_time((msg.getMessage().getCreateTime())));
            vh.count.setVisibility(msg.getUnReadCount() == 0 ? View.INVISIBLE : View.VISIBLE);

            switch (msg.getMessage().getType()) {
                case ApiEnum.MessageType.Audio:
                    vh.content.setText(R.string.audio);
                    break;
                case ApiEnum.MessageType.Video:
                    vh.content.setText(R.string.video);
                    break;
                case ApiEnum.MessageType.Image:
                    vh.content.setText(R.string.photoes);
                    break;
                case ApiEnum.MessageType.Attach:
                    vh.content.setText(R.string.doc);
                    break;
                case ApiEnum.MessageType.Share:
                    vh.content.setText(R.string.task_attachment_share);
                    break;
                case ApiEnum.MessageType.Task:
                    vh.content.setText("[任务]");
                    break;
                case ApiEnum.MessageType.Flow:
                    vh.content.setText(R.string.follow_mes);
                    break;
                case ApiEnum.MessageType.CallSchedule:
                    vh.online.setVisibility(View.GONE);
                    vh.name.setText("日程提醒");
                    vh.content.setText(msg.getMessage().getContent());
                    vh.head.setImageResource(R.drawable.nav_notice);
                    break;
                default:
                    if (msg.getMessage().getContent().contains("FlowID")) {
                        vh.content.setText(R.string.follow_mes);
                    } else if (msg.getMessage().getContent().contains("m.amap")) {
                        vh.content.setText(R.string.road_way);
                    } else if (msg.getMessage().getContent().contains("ControlType")) {
                        vh.content.setText("[通知]");
                    } else {
                        ChatUtils.spannableEmoticonFilter(vh.content, msg.getMessage().getContent());
                    }
                    break;
            }

        }
        if (TextUtils.isEmpty(name)) {
            vh.name.setText("");
        } else {
            vh.name.setText(name);
        }

        if (msg.getUnReadCount() != 0) {
            vh.count.setText(msg.getUnReadCount() + "");
        }

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("SenderID", msg.getReceiverID());
                intent.putExtra("type", mDataList.get(position).getType());
                intent.putExtra("GroupID", mDataList.get(position).getMessage().getGroupID());
                intent.putExtra("Count", mDataList.get(position).getUnReadCount());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                HelpUtil.hideSoftInput(mContext, v);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView head;
        TextView name, content, time, count;
        View itemLayout, line;
        ImageView online;

        public ViewHolder(View convertView) {
            super(convertView);
            content = (TextView) convertView.findViewById(R.id.content);
            head = (CircleImageView) convertView.findViewById(R.id.head);
            time = (TextView) convertView.findViewById(R.id.time);
            name = (TextView) convertView.findViewById(R.id.name);
            itemLayout = convertView.findViewById(R.id.item_layout);
            count = (TextView) convertView.findViewById(R.id.count);
            online = (ImageView) convertView.findViewById(R.id.online);
            line = convertView.findViewById(R.id.view_line_chat_item);
        }
    }
}
