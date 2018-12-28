package cc.emw.mobile.contact.adapter;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zf.iosdialog.widget.AlertDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.ChatTeamInfoActivity3;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.contact.GroupInActivity;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by ${zrjt} on 2016/7/28.
 */
public class GroupAdapters extends BaseAdapter {

    private Context context;
    private List<GroupInfo> mDataList;
    private Dialog mLoadingDialog;
    private DisplayImageOptions options;
    //    private ImageSize imageSize;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private LinearLayout.LayoutParams params;
    private LayoutInflater inflater;

    public GroupAdapters(Context context) {
        this.context = context;
        this.mDataList = new ArrayList<>();
        mLoadingDialog = DialogUtil.createLoadingDialog(context, "正在处理");
        inflater = LayoutInflater.from(context);
        params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(context, 20), DisplayUtil.dip2px(context, 20));
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.ALPHA_8)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .displayer(new FadeInBitmapDisplayer(388))
                .handler(new Handler()) // default
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public ImageLoader getImageLoader() {
        return this.imageLoader;
    }

    public void setData(List<GroupInfo> mDataList) {
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_contact_groups, parent, false);
            holder.relativeLayout = (LinearLayout) convertView.findViewById(R.id.rl_gmember_img);
            holder.gName = (TextView) convertView.findViewById(R.id.tv_group_name);
            holder.gNum = (TextView) convertView.findViewById(R.id.tv_group_member_num);
            holder.gInto = (TextView) convertView.findViewById(R.id.tv_into_btn);
            holder.linearLayout = (IconTextView) convertView.findViewById(R.id.ll_group_item_into);
            holder.itemLinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_contact_group_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.relativeLayout.removeAllViews();
        final GroupInfo groupInfo = mDataList.get(position);

//        if (EMWApplication.personMap != null && EMWApplication.personMap.size() > 0)
//            groupInfo.Users.add(EMWApplication.personMap.get(groupInfo.CreateUser));

        if (groupInfo.IsAddIn) {
            holder.relativeLayout.setVisibility(View.VISIBLE);
            holder.gNum.setVisibility(View.VISIBLE);
            holder.gInto.setVisibility(View.GONE);
        } else {
            holder.relativeLayout.setVisibility(View.GONE);
            holder.gNum.setVisibility(View.GONE);
            holder.gInto.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < groupInfo.Users.size(); i++) {
            if (i > 4)
                continue;
            CircleImageView circleImageView = new CircleImageView(context);
            String uri;
            if (i != 0) {
                params.leftMargin = -DisplayUtil.dip2px(context, 4);
            }
            circleImageView.setTvBg(EMWApplication.getIconColor(groupInfo.Users.get(i).ID), groupInfo.Users.get(i).Name, 20);
            uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode, groupInfo.Users.get(i).Image);
            imageLoader.displayImage(uri, new ImageViewAware(circleImageView), options,
                    new ImageSize(DisplayUtil.dip2px(context, 20), DisplayUtil.dip2px(context, 20)), null, null);
//            Picasso.with(context)
//                    .load(uri)
//                    .resize(DisplayUtil.dip2px(context, 20), DisplayUtil.dip2px(context, 20))
//                    .centerCrop()
//                    .config(Bitmap.Config.ALPHA_8)
////                    .placeholder(R.drawable.cm_img_head)
////                    .error(R.drawable.cm_img_head)
//                    .into(circleImageView);
            holder.relativeLayout.addView(circleImageView, params);

        }

        holder.gName.setText(groupInfo.Name);
        holder.gNum.setText(groupInfo.Users.size() + "人");

        switch (groupInfo.Color) {
            case 0:
                holder.itemLinearLayout.setBackgroundResource(R.drawable.btn_corners_group);
                break;
            case 1:
                holder.itemLinearLayout.setBackgroundResource(R.drawable.btn_corners_group_1);
                break;
            case 2:
                holder.itemLinearLayout.setBackgroundResource(R.drawable.btn_corners_group_2);
                break;
            case 3:
                holder.itemLinearLayout.setBackgroundResource(R.drawable.btn_corners_group_3);
                break;
            case 4:
                holder.itemLinearLayout.setBackgroundResource(R.drawable.btn_corners_group_4);
                break;
            case 5:
                holder.itemLinearLayout.setBackgroundResource(R.drawable.btn_corners_group_5);
                break;
            case 6:
                holder.itemLinearLayout.setBackgroundResource(R.drawable.btn_corners_group_6);
                break;
        }
        holder.gInto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog(context).builder().setMsg("确认申请加入群" + groupInfo.Name)
                        .setNegativeButton(context.getString(R.string.cancel), new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                            }
                        }).setPositiveButton(context.getString(R.string.ok), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (groupInfo.Type == 0)
                            getGroupInfo(groupInfo, PrefsUtil.readUserInfo().ID);
                        else
                            askForGroup(groupInfo, PrefsUtil.readUserInfo().ID);
                    }
                }).show();
            }
        });

        convertView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (groupInfo.IsAddIn) {
                    Intent intents = new Intent(context, ChatActivity.class);
                    intents.putExtra("GroupID", groupInfo.ID);
                    intents.putExtra("start_anim", false);
                    intents.putExtra("type", 2);
                    HelpUtil.hideSoftInput(context, v);
                    context.startActivity(intents);
                }
            }
        });

        holder.linearLayout.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int[] location = new int[2];
                v.getLocationInWindow(location);
                if (groupInfo.IsAddIn) {
                    Intent intent = new Intent(context, ChatTeamInfoActivity3.class);
                    intent.putExtra("groupInfo", groupInfo);
                    intent.putExtra("start_anim", false);
                    context.startActivity(intent);
                } else {
                    Intent intents = new Intent(context, GroupInActivity.class);
                    intents.putExtra("GroupID", groupInfo.ID);
                    intents.putExtra("start_anim", false);
                    intents.putExtra("click_pos_y", location[1]);
                    context.startActivity(intents);
                }
            }

        });

        return convertView;
    }

    class ViewHolder {
        LinearLayout relativeLayout;
        TextView gName;
        TextView gNum;
        TextView gInto;
        LinearLayout itemLinearLayout;
        IconTextView linearLayout;
    }

    /**
     * 私有群组申请加入群组
     *
     * @param id
     */
    private void askForGroup(GroupInfo groupInfo, int id) {
        API.TalkerAPI.JoinToGroup(groupInfo, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(context, "申请失败");
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showToast(context,
                            "申请成功,等待审核", R.drawable.tishi_ico_gougou);
                }
            }
        });
    }

    // 申请加入群组
    private void getGroupInfo(GroupInfo groupInfo, int uid) {
        API.TalkerAPI.AddGroupUser(groupInfo.ID, uid, new RequestCallback<String>(
                String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(context, R.string.groupinto_join_error);
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(context, R.string.groupinto_join_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(GroupFragment.ACTION_REFRESH_GROUP);
                    context.sendBroadcast(intent); // 刷新群组列表
                } else {
                    ToastUtil.showToast(context, R.string.groupinto_join_error);
                }
            }
        });
    }

    //根据路径获取用户选择的图片
    public static Bitmap getImage(String imgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;//直接设置它的压缩率，表示1/2
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeFile(imgPath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //将Bitmap转换成Base64
    public static String getImgStr(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    //将Base64转换成bitmap
    public static Bitmap getimg(String str) {
        byte[] bytes;
        bytes = Base64.decode(str, 0);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * 获取网络图片
     */
    protected void getNetImage(String imageUrl) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(imageUrl);
            HttpResponse response = client.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();

            if (200 == code) {
                InputStream is = response.getEntity().getContent();

                BitmapFactory.Options opts = new BitmapFactory.Options();

                //根据计算出的比例进行缩放
                int scale = 4;
                opts.inSampleSize = scale;

                Bitmap bm = BitmapFactory.decodeStream(is, null, opts);

                //将bm发生给主线程用于显示图片，更新UI
                Message msg = Message.obtain();
                msg.obj = bm;
//                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
