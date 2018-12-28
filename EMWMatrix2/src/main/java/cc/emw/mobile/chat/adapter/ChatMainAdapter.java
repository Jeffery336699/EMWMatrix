package cc.emw.mobile.chat.adapter;

import java.lang.reflect.Type;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.bean.Audios;
import cc.emw.mobile.chat.bean.Files;
import cc.emw.mobile.chat.bean.Task;
import cc.emw.mobile.chat.util.MediaPlayerManger;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote.UserNoteShareTo;
import cc.emw.mobile.main.PhotoActivity;
import cc.emw.mobile.net.ApiEnum.MessageType;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.BubbleImageView;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.RoundProgressBar;

import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 聊天列表适配器
 *
 * @author xiang.peng
 */
public class ChatMainAdapter extends BaseAdapter {

    private static final int TYPE_ITEM_COUNT = 2; // 不同item的数量
    private static final int TYPE_ITEM_RECE = 0; // 接收的item布局
    private static final int TYPE_ITEM_SEND = 1; // 发送的item布局
    private Context mContext;
    private ArrayList<Message> mDataList;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format;
    private DisplayImageOptions optiones;
    private MediaPlayerManger mMediaPlayerMangers;
    private boolean isPlay = true;
    private Calendar mCalendar;
    private Date DATESTART, DATEEND;
    private long BETWEEN = 3000001;
    private StringBuffer sb;
    private String tempPath = "";
    private String urles;

    public ChatMainAdapter(Context context, ArrayList<Message> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
        mCalendar = Calendar.getInstance();
        mMediaPlayerMangers = MediaPlayerManger.getInstance();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        optiones = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head)
                .showImageForEmptyUri(R.drawable.cm_img_head)
                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true).cacheOnDisk(true).build();
        sb = new StringBuffer();
    }

    public void add(ArrayList<Message> dataList) {
        if (dataList != null)
            notifyDataSetChanged();
    }

    // 获取地址数组的下标
    private int getIndexPosition(String url) {
        int index = 0;
        urles = getImageUrls();
        String[] urls = urles.split(",");
        for (int i = 0; i < urls.length; i++) {
            if (url.equals(urls[i])) {
                index = i;
            }
        }
        return index;
    }

    // 获取图片地址
    private String getImageUrls() {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getType() == MessageType.Image) {
                if (mDataList.get(i).getContent().endsWith(".jpg")) {
                    sb.append(Const.BASE_URL + "/").append(mDataList.get(i).getContent());
                } else {
                    sb.append(Const.BASE_URL).append(mDataList.get(i).getContent());
                }
                if (i != mDataList.size() - 1) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
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
    public int getViewTypeCount() {
        return TYPE_ITEM_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        UserInfo user = PrefsUtil.readUserInfo();// 用户
        return mDataList.get(position).getSenderID() == user.ID ? TYPE_ITEM_SEND
                : TYPE_ITEM_RECE;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Message msg = mDataList.get(position);
        if (position != 0) {
            if (msg.getCreateTime() == null) {
                try {
                    DATEEND = format.parse(format.format(new Date()));
                    DATESTART = format.parse(format.format(new Date()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    DATESTART = format.parse(format.format(HelpUtil
                            .string2Time(msg.getCreateTime())));

                    DATEEND = format.parse(format.format(HelpUtil
                            .string2Time(mDataList.get(position - 1)
                                    .getCreateTime())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            mCalendar.setTime(DATEEND);
            long TIMEEND = mCalendar.getTimeInMillis();
            mCalendar.setTime(DATESTART);
            long TIMESTART = mCalendar.getTimeInMillis();
            BETWEEN = TIMESTART - TIMEEND;
        }

        final ViewHolder vh;
        final int type = getItemViewType(position);
        if (null == convertView) {
            vh = new ViewHolder();
            switch (type) {
                case TYPE_ITEM_RECE:
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.chat_list_item_rece, null);
                    vh.audio_but = (RoundProgressBar) convertView
                            .findViewById(R.id.chat_audio_bt);
                    vh.audio_but.setCricleProgressColor(Color.rgb(22, 155, 255));

                    break;
                case TYPE_ITEM_SEND:
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.chat_list_item_send, null);
                    vh.audio_but = (RoundProgressBar) convertView
                            .findViewById(R.id.chat_audio_bt);
                    vh.audio_but.setCricleProgressColor(Color.WHITE);
                    break;

            }
            vh.messageView = convertView.findViewById(R.id.message);

            vh.timeTv = (TextView) (convertView.findViewById(R.id.chat_tv_time));
            vh.headIv = (CircleImageView) (convertView
                    .findViewById(R.id.chat_iv_head));
            vh.contentTv = (TextView) convertView
                    .findViewById(R.id.chat_tv_content);
            vh.image = (BubbleImageView) (convertView.findViewById(R.id.chat_iv_image));
            vh.fileName = (TextView) (convertView
                    .findViewById(R.id.chat_tv_filename));
            vh.fileSize = (TextView) (convertView
                    .findViewById(R.id.chat_tv_filesize));
            vh.view = convertView.findViewById(R.id.file_layout);
            vh.fileIv = (ImageView) (convertView.findViewById(R.id.file_head));
            vh.baseView = convertView.findViewById(R.id.base_layout);
            vh.audio_layout = convertView.findViewById(R.id.audio_layout);
            vh.time_audio = (TextView) (convertView
                    .findViewById(R.id.chat_audio_tv));
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (BETWEEN < 300000) {
            if (vh.timeTv != null) {
                vh.timeTv.setVisibility(View.GONE);
            }
        } else {
            if (vh.timeTv != null) {
                vh.timeTv.setVisibility(View.VISIBLE);
            }
        }
        String image = "";
        UserInfo user = EMWApplication.personMap.get(msg.getSenderID());
        if (user != null) {
            image = user.Image;
        }
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode, image);
        ImageLoader.getInstance().displayImage(uri, vh.headIv, optiones);

        if (msg.getType() == MessageType.Image && msg.getContent() != null) {// 拍照/照片消息
            switch (type) {
                case TYPE_ITEM_RECE:
                    vh.baseView.setBackgroundDrawable(null);
                    if (msg.getContent().endsWith(".jpg")) {
                        vh.image.load(Const.BASE_URL + "/" + msg.getContent(), R.drawable.mes_rec, R.drawable.chat_jiazaishibai);
                    } else {
                        vh.image.load(Const.BASE_URL + msg.getContent(), R.drawable.mes_rec, R.drawable.chat_jiazaishibai);
                    }
                    break;
                case TYPE_ITEM_SEND:
                    vh.baseView.setBackgroundDrawable(null);
                    if (msg.getContent().endsWith(".jpg")) {
                        vh.image.load(Const.BASE_URL + "/" + msg.getContent(), R.drawable.mes_send, R.drawable.chat_jiazaishibai);
                    } else {
                        vh.image.load(Const.BASE_URL + msg.getContent(), R.drawable.mes_send, R.drawable.chat_jiazaishibai);
                    }
                    break;
            }

            vh.baseView.setVisibility(View.VISIBLE);
            vh.messageView.setVisibility(View.GONE);
            vh.audio_layout.setVisibility(View.GONE);
            vh.view.setVisibility(View.GONE);
            vh.image.setVisibility(View.VISIBLE);
            vh.contentTv.setVisibility(View.GONE);

            vh.image.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String temp;
                    if (msg.getContent().endsWith(".jpg")) {
                        temp = Const.BASE_URL + "/" + msg.getContent();
                    } else {
                        temp = Const.BASE_URL + msg.getContent();
                    }
                    Intent intent = new Intent(mContext, PhotoActivity.class);
                    intent.putExtra(PhotoActivity.INTENT_EXTRA_POSITION, getIndexPosition(temp));
                    intent.putExtra(PhotoActivity.INTENT_EXTRA_IMGURLS, urles);
                    intent.putExtra(PhotoActivity.INTENT_EXTRA_ISFORMAT, false);
                    mContext.startActivity(intent);
                    ((Activity) mContext)
                            .overridePendingTransition(
                                    R.anim.image_activity_in,
                                    R.anim.image_activity_out);
                    sb.delete(0, sb.length());
                }
            });

        } else if (MessageType.Message == msg.getType()
                && msg.getContent() != null) {// 普通消息
            vh.baseView.setVisibility(View.VISIBLE);
            vh.messageView.setVisibility(View.GONE);
            vh.audio_layout.setVisibility(View.GONE);
            vh.view.setVisibility(View.GONE);
            vh.image.setVisibility(View.GONE);
            vh.contentTv.setVisibility(View.VISIBLE);
            vh.contentTv.setText(msg.getContent());
            if (msg.getContent().contains("http://m.amap.com/")) {

                vh.contentTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String temp = msg.getContent().replaceAll("[\u4e00-\u9fa5]", "").trim();
                        Log.d("px", temp);
//                        Uri uri = Uri.parse(temp);
//                        Log.d("px", uri.toString());
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(uri);
//                        mContext.startActivity(intent);
                    }
                });
            }
            switch (type) {
                case TYPE_ITEM_RECE:
                    vh.baseView.setBackgroundResource(R.drawable.mes_rec);
                    break;
                case TYPE_ITEM_SEND:
                    vh.baseView.setBackgroundResource(R.drawable.mes_send);
                    break;
            }

        } else if (msg.getType() == 12
                && msg.getContent() != null) {
            vh.baseView.setVisibility(View.VISIBLE);
            vh.audio_layout.setVisibility(View.GONE);
            vh.view.setVisibility(View.GONE);
            vh.image.setVisibility(View.GONE);
            vh.contentTv.setVisibility(View.VISIBLE);
            vh.contentTv.setText(msg.getContent());
            vh.messageView.setVisibility(View.VISIBLE);
            switch (type) {
                case TYPE_ITEM_RECE:
                    vh.baseView.setBackgroundResource(R.drawable.mes_rec);
                    break;
                case TYPE_ITEM_SEND:
                    vh.baseView.setBackgroundResource(R.drawable.mes_send);
                    break;
            }

        } else if (MessageType.Attach == msg.getType()
                && msg.getContent() != null) {// 附件消息
            switch (type) {
                case TYPE_ITEM_RECE:
                    vh.baseView.setBackgroundResource(R.drawable.mes_rec);
                    break;
                case TYPE_ITEM_SEND:
                    vh.baseView.setBackgroundResource(R.drawable.files_bug);
                    break;
            }
            vh.baseView.setVisibility(View.VISIBLE);
            vh.messageView.setVisibility(View.GONE);
            vh.audio_layout.setVisibility(View.GONE);
            vh.view.setVisibility(View.VISIBLE);
            vh.image.setVisibility(View.GONE);
            vh.contentTv.setVisibility(View.GONE);
            if (msg.getContent().startsWith("{")) {
                Type types = new TypeToken<Files>() {
                }.getType();
                final Files files = new Gson()
                        .fromJson(msg.getContent(), types);
                int id = FileUtil.getResIconId(files.getName());
                vh.fileIv.setBackgroundResource(id);
                vh.fileIv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (files.getName() != null) {
                            if (!FileUtil.hasFile(EMWApplication.filePath
                                    + files.getUrl())) {
                                String fileUrl = String.format(
                                        Const.DOWN_FILE_URL,
                                        PrefsUtil.readUserInfo().CompanyCode,
                                        files.getUrl());
                                NotificationUtil.notificationForDLAPK(mContext,
                                        fileUrl, EMWApplication.filePath);
                            } else {
                                FileUtil.openFile(
                                        mContext,
                                        EMWApplication.filePath
                                                + files.getUrl());
                            }
                        }
                    }
                });
                vh.fileName.setText(files.getName());
                vh.fileSize.setText(FileUtil.getReadableFileSize(files.getLength())); // 设置文件的大小和文件名
            }
        } else if (MessageType.Audio == msg.getType()
                && msg.getContent() != null) {// 音频消息

            vh.baseView.setVisibility(View.GONE);
            vh.audio_layout.setVisibility(View.VISIBLE);
            vh.view.setVisibility(View.GONE);
            vh.image.setVisibility(View.GONE);
            vh.contentTv.setVisibility(View.GONE);

            Type types = new TypeToken<Audios>() {
            }.getType();
            Audios audios = new Gson().fromJson(msg.getContent(), types);
            final String path = Const.BASE_URL + "/" + audios.getUrl();
            final String paths = path.replace("\"", "");
            final String times;
            String time = null;
            if (audios.getLength().contains(":")) {
                vh.time_audio.setText(audios.getLength());
                time = audios.getLength();
            } else {
                try {
                    Format f0 = new SimpleDateFormat("ss");
                    SimpleDateFormat f1 = new SimpleDateFormat("mm:ss");
                    Date d = (Date) f0.parseObject(audios.getLength());
                    time = f1.format(d);
                    vh.time_audio.setText(f1.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            times = time;
            vh.audio_but.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!paths.equals(tempPath)) {
                        mMediaPlayerMangers.pause();
                        isPlay = true;
                    }
                    if (isPlay) {
                        mMediaPlayerMangers.playSound(paths, vh.audio_but,
                                vh.time_audio, times, type,
                                mContext);//, ChatMainAdapter.this
                    } else {
                        mMediaPlayerMangers.pause();
                    }
                    isPlay = !isPlay;
                    tempPath = paths;
                }
            });

        } else if (MessageType.Share == msg.getType()
                && msg.getContent() != null) {// 动态消息
            vh.baseView.setVisibility(View.VISIBLE);
            vh.messageView.setVisibility(View.GONE);
            vh.audio_layout.setVisibility(View.GONE);
            vh.view.setVisibility(View.VISIBLE);
            vh.image.setVisibility(View.GONE);
            vh.contentTv.setVisibility(View.GONE);
            final UserNoteShareTo share = new Gson().fromJson(msg.getContent(),
                    UserNoteShareTo.class);
            vh.fileIv.setBackgroundResource(R.drawable.index_ico_share);
            vh.fileName.setText(share.UserName);
            vh.fileSize.setText(share.Content);
            switch (type) {
                case TYPE_ITEM_RECE:
                    vh.baseView.setBackgroundResource(R.drawable.mes_rec);
                    break;
                case TYPE_ITEM_SEND:
                    vh.baseView.setBackgroundResource(R.drawable.files_bug);
                    break;
            }
            vh.fileIv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,
                            DynamicDetailActivity.class);
                    intent.putExtra("note_id", share.NoteID);
                    mContext.startActivity(intent);
                }
            });
        } else if (MessageType.Task == msg.getType()
                && msg.getContent() != null) {// 任务消息
            vh.baseView.setVisibility(View.VISIBLE);
            vh.messageView.setVisibility(View.GONE);
            vh.audio_layout.setVisibility(View.GONE);
            vh.view.setVisibility(View.VISIBLE);
            vh.image.setVisibility(View.GONE);
            vh.contentTv.setVisibility(View.GONE);
            final Task task = new Gson().fromJson(msg.getContent(), Task.class);
            vh.fileIv.setBackgroundResource(R.drawable.index_ico_share);
            vh.fileName.setText(R.string.new_task);
            vh.fileSize.setText(task.getTitle());
            switch (type) {
                case TYPE_ITEM_RECE:
                    vh.baseView.setBackgroundResource(R.drawable.mes_rec);
                    break;
                case TYPE_ITEM_SEND:
                    vh.baseView.setBackgroundResource(R.drawable.files_bug);
                    break;
            }
            vh.fileIv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,
                            TaskDetailActivity.class);
                    intent.putExtra(TaskDetailActivity.TASK_ID, task.getID());
                    mContext.startActivity(intent);
                }
            });

        } else if (MessageType.Flow == msg.getType()// 流程消息
                && msg.getContent() != null) {
            vh.baseView.setVisibility(View.VISIBLE);
            vh.messageView.setVisibility(View.GONE);
            vh.audio_layout.setVisibility(View.GONE);
            vh.view.setVisibility(View.GONE);
            vh.image.setVisibility(View.GONE);
            vh.contentTv.setVisibility(View.VISIBLE);
            vh.contentTv.setText(R.string.follow_mes);
            vh.contentTv.setCompoundDrawables(null, null, null, null);
            switch (type) {
                case TYPE_ITEM_RECE:
                    vh.baseView.setBackgroundResource(R.drawable.mes_rec);
                    break;
                case TYPE_ITEM_SEND:
                    vh.baseView.setBackgroundResource(R.drawable.mes_send);
                    break;
            }
        }
        if (msg.getCreateTime() == null) {
            vh.timeTv.setText(format.format(new Date()));
        } else {
            vh.timeTv.setText(format.format(HelpUtil.string2Time(msg
                    .getCreateTime())));
        }

        return convertView;
    }

    class ViewHolder {
        TextView timeTv, contentTv, fileName, fileSize, time_audio;
        CircleImageView headIv;
        ImageView fileIv;
        BubbleImageView image;
        View view, baseView, audio_layout, messageView;
        RoundProgressBar audio_but;
//        Button sure, notSure;
    }

}
