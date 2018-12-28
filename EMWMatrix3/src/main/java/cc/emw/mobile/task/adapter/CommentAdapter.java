package cc.emw.mobile.task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.task.activity.CommentActivity;
import cc.emw.mobile.task.base.BaseHolder;
import cc.emw.mobile.task.base.SuperBaseAdapter;
import cc.emw.mobile.task.util.StringUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.view.CircleImageView;

/**
 * 任务评论
 */
public class CommentAdapter extends SuperBaseAdapter<ApiEntity.TaskReply> {
    private Context mContext;
    private List<TaskReply> mDataList;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//            .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//            .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
            .build(); // 创建配置过得DisplayImageOption对象
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private TaskReply childReply;// 储存子评论信息
    private EditText mEditText;

    public CommentAdapter(Context context, EditText et) {
        mDataList = new ArrayList<>();
        this.mDatas = mDataList;
        this.mContext = context;
        mEditText = et;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = vh.mHolderView;
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        ApiEntity.TaskReply data = mDataList.get(position);
        vh.setDataAndRefreshHolderView(position, data);
        return convertView;
    }

    @Override
    public void setData(List<TaskReply> dataList) {
        super.setData(dataList);
        if (dataList != null) {
            mDataList.clear();
            mDataList.addAll(dataList);
        }
    }

    /**
     * 外部获取子评论信息
     *
     * @return
     */
    public TaskReply getChildReply() {
        return childReply;
    }

    public class ViewHolder extends BaseHolder<ApiEntity.TaskReply> {
        View view;
        TextView Commcreator;
        TextView createTime;
        TextView content;
        LinearLayout reply;
        CircleImageView civ;
        View divideLine;
        LinearLayout llChildReply;
        LinearLayout container;

        @Override
        public View initHolderViewAndFindViews() {
            view = LayoutInflater.from(mContext).inflate(R.layout.listitem_task_child_comment, null);
            Commcreator = (TextView) view.findViewById(R.id.tv_task_comment_creator);
            createTime = (TextView) view.findViewById(R.id.tv_task_comment_time);
            content = (TextView) view.findViewById(R.id.tv_task_comment_content);
            reply = (LinearLayout) view.findViewById(R.id.tv_task_comment_reply);
            civ = (CircleImageView) view.findViewById(R.id.civ_task_comment_head_image);
            divideLine = view.findViewById(R.id.divide_line);
            llChildReply = (LinearLayout) view.findViewById(R.id.ll_task_comment_container);
            container = (LinearLayout) view.findViewById(R.id.ll_task_comment_container); //子评论容器
            return view;
        }

        @Override
        public void refreshHolderView(int postion, final TaskReply data) {
            createTime.setText(StringUtils.friendly_time(data.CreateTime));
            content.setText(StringUtil.delHTMLTag(data.Content));
            final int creator = data.Creator;
            // 通过创建ID获取创建者:
            UserInfo userInfo = EMWApplication.personMap.get(creator);
            String creatorName = "未知";
            if (userInfo != null) {
                // 设置创建者的头像
                civ.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
                ImageLoader.getInstance().displayImage(getHeadImageUri(userInfo.Image), civ, options);
                creatorName = userInfo.Name;// 设置创建者名字
            } else {
                civ.setTextBg(creator, "", 30);
            }
            Commcreator.setText(creatorName);
            container.removeAllViews();
            // 判断是否有子集合
            List<TaskReply> replys = data.Replys;
            if (replys != null && replys.size() != 0) {
                // 有子评论添加子评论
                addChildReply(replys, container, data.ID);
                divideLine.setVisibility(View.VISIBLE);
                llChildReply.setVisibility(View.VISIBLE);
            } else {
                divideLine.setVisibility(View.GONE);
                llChildReply.setVisibility(View.GONE);
            }

            final int ID = data.ID;//本条评论的ID 是其子评论的父ID。
            reply.setTag(creatorName);
            reply.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (creator != PrefsUtil.readUserInfo().ID) {
                        //点击对标记文件进行赋值
                        PrefsUtil.setChildReply(true);//设置是子评论
                        TaskReply childComment = new TaskReply();
                        childComment.ToUserID = creator;
                        childComment.ParentID = ID;
                        childComment.ID = 0;
                        childReply = childComment;
                        mEditText.setHint("回复" + v.getTag().toString());
                    }
                }
            });
            reply.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (creator == PrefsUtil.readUserInfo().ID) {
                        //长按删除评论
                        new AlertDialog(mContext).builder().setMsg("确定删除该评论？")
                            .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                            .setPositiveButton(mContext.getString(R.string.ok), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((CommentActivity) mContext).delTaskReply(data);
                                }
                            }).setNegativeButton(mContext.getString(R.string.cancel), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                        }).show();
                    }
                    return true;
                }
            });
        }
        /**
         * 添加子评论布局
         */
        private void addChildReply(List<TaskReply> replys, LinearLayout container,
                                   final int parentID) {
            int length = replys.size();
            for (int i = 0; i < length; i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_task_comment_item, null);
                // 找到view的布局
                TextView content = (TextView) view.findViewById(R.id.tv_child_comment_content);
                TextView creatorUser = (TextView) view.findViewById(R.id.tv_child_comment_creator);
                TextView touser = (TextView) view.findViewById(R.id.tv_child_comment_touser);
                LinearLayout reply = (LinearLayout) view.findViewById(R.id.tv_child_comment_reply);
                final TaskReply taskReply = replys.get(i);

                content.setText(taskReply.Content);
                // 获取谁回复谁
                final int creatorID = taskReply.Creator;
                int toUserID = taskReply.ToUserID;
                final UserInfo creator = EMWApplication.personMap.get(creatorID);
                UserInfo toUser = EMWApplication.personMap.get(toUserID);
                creatorUser.setText(creator.Name);
                touser.setText(toUser.Name + ": ");
                // 回复的点击事件
                reply.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PrefsUtil.setChildReply(true);//设置是子评论
                        // 创建评论
                        // 将评论数据保存起来
                        TaskReply addReply = new TaskReply();
                        addReply.ToUserID = creatorID;
                        addReply.ParentID = parentID;
                        addReply.ID = 0;
                        childReply = addReply;
                        mEditText.setHint("回复" + creator.Name);
                    }
                });
                reply.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (creatorID == PrefsUtil.readUserInfo().ID) {
                            //长按删除评论
                            new AlertDialog(mContext).builder().setMsg("确定删除该评论？")
                                .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                                .setPositiveButton(mContext.getString(R.string.ok), new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((CommentActivity) mContext).delTaskReply(taskReply);
                                    }
                                }).setNegativeButton(mContext.getString(R.string.cancel), new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                            }).show();
                        }
                        return true;
                    }
                });
                // 添加布局
                container.addView(view, params);
            }
        }
        private String getHeadImageUri(String imageUrl) {
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil
                    .readUserInfo().CompanyCode, imageUrl);
            return uri;
        }
    }
}



/**
 *   被重构好的adapter取代，后续稳定后删除下面预留的原内容
 */
//package cc.emw.mobile.task.adapter;
//
//        import android.content.Context;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.View.OnClickListener;
//        import android.view.ViewGroup;
//        import android.widget.BaseAdapter;
//        import android.widget.EditText;
//        import android.widget.ImageButton;
//        import android.widget.ImageView;
//        import android.widget.LinearLayout;
//        import android.widget.RelativeLayout;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import com.nostra13.universalimageloader.core.DisplayImageOptions;
//        import com.nostra13.universalimageloader.core.ImageLoader;
//        import com.zf.iosdialog.widget.AlertDialog;
//
//        import java.util.ArrayList;
//        import java.util.List;
//
//        import cc.emw.mobile.EMWApplication;
//        import cc.emw.mobile.R;
//        import cc.emw.mobile.net.ApiEntity.TaskReply;
//        import cc.emw.mobile.net.ApiEntity.UserInfo;
//        import cc.emw.mobile.net.Const;
//        import cc.emw.mobile.task.activity.CommentActivity;
//        import cc.emw.mobile.util.PrefsUtil;
//        import cc.emw.mobile.util.StringUtils;
//        import cc.emw.mobile.util.ToastUtil;
//        import cc.emw.mobile.view.CircleImageView;
//
//public class CommentAdapter extends BaseAdapter {
//
//    private static final String TAG = "CommentAdapter";
//    private Context mContext;
//    private List<TaskReply> mDataList;
//    private DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//            .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//            .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
//            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
//            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//            // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
//            .build(); // 创建配置过得DisplayImageOption对象
//    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT);
//
//    private TaskReply childReply;// 储存子评论信息
//    private EditText mEditText;
//
//    public CommentAdapter(Context context, EditText et) {
//        mDataList = new ArrayList<TaskReply>();
//        this.mContext = context;
//        mEditText = et;
//    }
//
//    public void setData(List<TaskReply> dataList) {
//        if (dataList != null) {
//            mDataList.clear();
//            mDataList.addAll(dataList);
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return mDataList != null ? mDataList.size() : 0;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mDataList != null ? mDataList.get(position) : null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder vh;
//        if (convertView == null) {
//            vh = new ViewHolder();
//            convertView = LayoutInflater.from(mContext).inflate(
//                    R.layout.listitem_task_child_comment, null);
//            vh.creator = (TextView) convertView
//                    .findViewById(R.id.tv_task_comment_creator);
//            vh.createTime = (TextView) convertView
//                    .findViewById(R.id.tv_task_comment_time);
//            vh.content = (TextView) convertView
//                    .findViewById(R.id.tv_task_comment_content);
//            vh.reply = (LinearLayout) convertView
//                    .findViewById(R.id.tv_task_comment_reply);
//            vh.civ = (CircleImageView) convertView
//                    .findViewById(R.id.civ_task_comment_head_image);
//            vh.divideLine = convertView.findViewById(R.id.divide_line);
//            vh.llChildReply = (LinearLayout) convertView.findViewById(R.id.ll_task_comment_container);
//            // 子评论容器
//            vh.container = (LinearLayout) convertView
//                    .findViewById(R.id.ll_task_comment_container);
//            convertView.setTag(vh);
//        } else {
//            vh = (ViewHolder) convertView.getTag();
//        }
//        // 绑定数据
//        final TaskReply taskReply = mDataList.get(position);
//        vh.createTime.setText(StringUtils.friendly_time(taskReply.CreateTime));
//        vh.content.setText(taskReply.Content);
//        final int creator = taskReply.Creator;
//        // 通过创建ID获取创建者:
//        UserInfo UserInfo = EMWApplication.personMap.get(creator);
//        // 设置创建者名字
//        final String creatorName = UserInfo.Name;
//        vh.creator.setText(creatorName);
//        String imageUrl = UserInfo.Image;
////		Log.d(TAG,imageUrl);
////		ToastUtil.showToast(mContext,imageUrl);
//        // 设置创建者的头像
//        ImageLoader.getInstance().displayImage(getHeadImageUri(imageUrl),
//                vh.civ, options);
//        vh.container.removeAllViews();
//        // 判断是否有子集合
//        List<TaskReply> replys = taskReply.Replys;
//        if (replys != null && replys.size() != 0) {
//            // 有子评论添加子评论
//            addChildReply(replys, vh.container, taskReply.ID);
//            vh.divideLine.setVisibility(View.VISIBLE);
//            vh.llChildReply.setVisibility(View.VISIBLE);
//        } else {
//            vh.divideLine.setVisibility(View.GONE);
//            vh.llChildReply.setVisibility(View.GONE);
//        }
//
//        final int ID = taskReply.ID;//本条评论的ID 是其子评论的父ID。
//        vh.reply.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //点击对标记文件进行赋值
//                PrefsUtil.setChildReply(true);//设置是子评论
//                TaskReply childComment = new TaskReply();
////				childComment.setToUserID(creator);
////				childComment.setParentID(ID);
////				childComment.setID(0);// 新建评论的ID都为0;
//                childComment.ToUserID = creator;
//                childComment.ParentID = ID;
//                childComment.ID = 0;
//                childReply = childComment;
//                mEditText.setHint("回复" + creatorName);
//            }
//        });
//        vh.reply.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                //长按删除评论
//                new AlertDialog(mContext).builder().setMsg("确定删除该评论?")
//                        .setPositiveButton("确定", new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                ((CommentActivity)mContext).delTaskReply(taskReply);
//                            }
//                        }).setNegativeButton("取消", new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                }).show();
//                return true;
//            }
//        });
//        return convertView;
//    }
//
//    /**
//     * 添加子评论布局
//     */
//    private void addChildReply(List<TaskReply> replys, LinearLayout container,
//                               final int parentID) {
//        int length = replys.size();
//        for (int i = 0; i < length; i++) {
//
//            // View view = View.inflate(mContext,
//            // R.layout.listitem_task_comment_item, null);
//            View view = LayoutInflater.from(mContext).inflate(
//                    R.layout.listitem_task_comment_item, null);
//            // 找到view的布局
//            TextView content = (TextView) view
//                    .findViewById(R.id.tv_child_comment_content);
//            TextView creatorUser = (TextView) view
//                    .findViewById(R.id.tv_child_comment_creator);
//            TextView touser = (TextView) view
//                    .findViewById(R.id.tv_child_comment_touser);
//            LinearLayout reply = (LinearLayout) view
//                    .findViewById(R.id.tv_child_comment_reply);
//            final TaskReply taskReply = replys.get(i);
//
//            content.setText(taskReply.Content);
//            // 获取谁回复谁
//            final int creatorID = taskReply.Creator;
//            int toUserID = taskReply.ToUserID;
//            final UserInfo creator = EMWApplication.personMap.get(creatorID);
//            UserInfo toUser = EMWApplication.personMap.get(toUserID);
//            creatorUser.setText(creator.Name);
//            touser.setText(toUser.Name + ": ");
//            // 回复的点击事件
//            reply.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    PrefsUtil.setChildReply(true);//设置是子评论
//                    // 创建评论
//                    // 将评论数据保存起来
//                    TaskReply addReply = new TaskReply();
//                    // addReply.setTaskId(taskId);//任务ID在fragment中拿
//                    // addReply.setContent(content)//内容在Fragment中拿;
//                    // addReply.setCreator(creator)//内容在Fragment中拿
//                    // addReply.setCreateTime(createTime)//在fragment中设置
//                    // addReply.setReplys(replys)//默认为空;
////					addReply.setToUserID(creatorID);
////					addReply.setParentID(parentID);
////					addReply.setID(0);// 新建评论的ID都为0;
//                    addReply.ToUserID = creatorID;
//                    addReply.ParentID = parentID;
//                    addReply.ID = 0;
//                    childReply = addReply;
//                    mEditText.setHint("回复" + creator.Name);
//                    // 控制软键盘弹出来
////					TaskUtils.showSoftInput(mContext, mEditText);
//                }
//            });
//            reply.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    //长按删除评论
//                    new AlertDialog(mContext).builder().setMsg("确定删除该评论?")
//                            .setPositiveButton("确定", new View.OnClickListener() {
//
//                                @Override
//                                public void onClick(View v) {
//                                    ((CommentActivity)mContext).delTaskReply(taskReply);
//                                }
//                            }).setNegativeButton("取消", new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    }).show();
//                    return true;
//                }
//            });
//            // 添加布局
//            container.addView(view, params);
//
//        }
//    }
//
//    static class ViewHolder {
//        LinearLayout container;
//        LinearLayout reply;
//        TextView creator;
//        TextView createTime;
//        TextView content;
//        CircleImageView civ;
//        View divideLine;
//        LinearLayout llChildReply;
//    }
//
//    private String getHeadImageUri(String imageUrl) {
//        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil
//                .readUserInfo().CompanyCode, imageUrl);
//        return uri;
//    }
//
//    /**
//     * 外部获取子评论信息
//     *
//     * @return
//     */
//    public TaskReply getChildReply() {
//        return childReply;
//    }
//}
