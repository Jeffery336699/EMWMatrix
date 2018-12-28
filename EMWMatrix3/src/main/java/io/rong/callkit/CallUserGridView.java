package io.rong.callkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import io.rong.imkit.widget.AsyncImageView;
//import io.rong.imlib.model.UserInfo;

/**
 * Created by weiqinxiao on 16/3/25.
 */
public class CallUserGridView extends HorizontalScrollView {
//public class CallUserGridView extends ScrollView {
    private Context context;
    private boolean enableTitle;
    private LinearLayout linearLayout;

    private final static int CHILDREN_PER_LINE = 4;
    private final static int CHILDREN_SPACE = 10;

    private int portraitSize;

    private DisplayImageOptions options;

    public CallUserGridView(Context context) {
        super(context);
        init(context);
    }

    public CallUserGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
        addView(linearLayout);

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

    public int dip2pix(int dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    public void setChildPortraitSize(int size) {
        portraitSize = size;
    }

    public void enableShowState(boolean enable) {
        enableTitle = enable;
    }

    public void addChild(String childId, UserInfo userInfo) {
        addChild(childId, userInfo, null);
    }

    public void addChild(String childId, UserInfo userInfo, String state) {
        /*int containerCount = linearLayout.getChildCount();
        LinearLayout lastContainer = null;
        int i;
        for (i = 0; i < containerCount; i++) {
            LinearLayout container = (LinearLayout)linearLayout.getChildAt(i);
            if (container.getChildCount() < CHILDREN_PER_LINE) {
                lastContainer = container;
                break;
            }
        }
        if (lastContainer == null) {
            lastContainer = new LinearLayout(context);
            lastContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lastContainer.setGravity(Gravity.CENTER);
            lastContainer.setPadding(0, dip2pix(CHILDREN_SPACE), 0, 0);
            linearLayout.addView(lastContainer);
        }*/

        LinearLayout child = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.rc_voip_user_info, null);
//        child.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        child.setPadding(0, 0, dip2pix(CHILDREN_SPACE), 0);
        child.setTag(childId);
        if (portraitSize > 0) {
            child.findViewById(R.id.rc_user_portrait_layout).setLayoutParams(new LinearLayout.LayoutParams(portraitSize, portraitSize));
        }
        CircleImageView imageView = (CircleImageView) child.findViewById(R.id.rc_user_portrait);
        TextView name = (TextView)child.findViewById(R.id.rc_user_name);
//        name.setVisibility(enableTitle ? VISIBLE : GONE);
        TextView stateV = (TextView)child.findViewById(R.id.rc_voip_member_state);
        stateV.setVisibility(enableTitle ? VISIBLE : GONE);
        if (state != null) {
            stateV.setText(state);
        } else {
            stateV.setVisibility(GONE);
        }

        /*if (userInfo != null) {
            imageView.setAvatar(userInfo.getPortraitUri());
            name.setText(userInfo.getName() == null ? userInfo.getUserId() : userInfo.getName());
        } else {
            name.setText(childId);
        }
        lastContainer.addView(child);*/
        if (userInfo != null) {
            String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
            if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                url = Const.DOWN_ICON_URL2 + userInfo.Image;
            }
            imageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, portraitSize > 0 ? portraitSize : 60);
            ImageLoader.getInstance().displayImage(url, imageView, options);
            name.setText(userInfo.Name == null ? Integer.toString(userInfo.ID) : userInfo.Name);
        } else {
            imageView.setTextBg(0, "", portraitSize > 0 ? portraitSize : 60);
            name.setText(childId);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (linearLayout.getChildCount() != 0) {
            params.leftMargin = dip2pix(CHILDREN_SPACE);
        }
        linearLayout.addView(child, params);
    }


    public void removeChild(String childId) {
        /*int containerCount = linearLayout.getChildCount();

        LinearLayout lastContainer = null;
        List<LinearLayout> containerList = new ArrayList<>();
        for (int i = 0; i < containerCount; i++) {
            LinearLayout container = (LinearLayout) linearLayout.getChildAt(i);
            containerList.add(container);
        }
        for (LinearLayout resultContainer : containerList) {
            if (lastContainer == null) {
                LinearLayout child = (LinearLayout) resultContainer.findViewWithTag(childId);
                if (child != null) {
                    resultContainer.removeView(child);
                    if (resultContainer.getChildCount() == 0) {
                        linearLayout.removeView(resultContainer);
                        break;
                    } else {
                        lastContainer = resultContainer;
                    }
                }
            } else {
                View view = resultContainer.getChildAt(0);
                resultContainer.removeView(view);
                lastContainer.addView(view);
                if (resultContainer.getChildCount() == 0) {
                    linearLayout.removeView(resultContainer);
                    break;
                } else {
                    lastContainer = resultContainer;
                }
            }
        }*/
        LinearLayout child = (LinearLayout) linearLayout.findViewWithTag(childId);
        if (child != null) {
            linearLayout.removeView(child);
        }
    }

    public View findChildById(String childId) {
        /*int containerCount = linearLayout.getChildCount();

        for (int i = 0; i < containerCount; i++) {
            LinearLayout container = (LinearLayout) linearLayout.getChildAt(i);
            LinearLayout child = (LinearLayout) container.findViewWithTag(childId);
            if (child != null) {
                return child;
            }
        }*/
        LinearLayout child = (LinearLayout) linearLayout.findViewWithTag(childId);
        if (child != null) {
            return child;
        }
        return null;
    }

    public void updateChildInfo(String childId, UserInfo userInfo) {
        /*int containerCount = linearLayout.getChildCount();

        LinearLayout lastContainer = null;
        for (int i = 0; i < containerCount; i++) {
            LinearLayout container = (LinearLayout) linearLayout.getChildAt(i);
            LinearLayout child = (LinearLayout) container.findViewWithTag(childId);
            if (child != null) {
                AsyncImageView imageView = (AsyncImageView)child.findViewById(R.id.rc_user_portrait);
                imageView.setAvatar(userInfo.getPortraitUri());
                if (enableTitle) {
                    TextView textView = (TextView)child.findViewById(R.id.rc_user_name);
                    textView.setText(userInfo.getName());
                }
            }
        }*/
        if (userInfo == null) {
            return;
        }
        LinearLayout child = (LinearLayout) linearLayout.findViewWithTag(childId);
        if (child != null) {
            CircleImageView imageView = (CircleImageView) child.findViewById(R.id.rc_user_portrait);
            String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
            if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                url = Const.DOWN_ICON_URL2 + userInfo.Image;
            }
            imageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, portraitSize > 0 ? portraitSize : 60);
            ImageLoader.getInstance().displayImage(url, imageView, options);
            if (enableTitle) {
                TextView textView = (TextView)child.findViewById(R.id.rc_user_name);
                textView.setText(userInfo.Name);
            }
        }
    }

    public void updateChildState(String childId, String state) {
        /*int containerCount = linearLayout.getChildCount();

        for (int i = 0; i < containerCount; i++) {
            LinearLayout container = (LinearLayout) linearLayout.getChildAt(i);
            LinearLayout child = (LinearLayout) container.findViewWithTag(childId);
            if (child != null) {
                TextView textView = (TextView)child.findViewById(R.id.rc_voip_member_state);
                textView.setText(state);
            }
        }*/
        LinearLayout child = (LinearLayout) linearLayout.findViewWithTag(childId);
        if (child != null) {
            TextView textView = (TextView)child.findViewById(R.id.rc_voip_member_state);
            textView.setText(state);
        }
    }

    public void updateChildState(String childId, boolean visible) {
        /*int containerCount = linearLayout.getChildCount();

        for (int i = 0; i < containerCount; i++) {
            LinearLayout container = (LinearLayout) linearLayout.getChildAt(i);
            LinearLayout child = (LinearLayout) container.findViewWithTag(childId);
            if (child != null) {
                TextView textView = (TextView)child.findViewById(R.id.rc_voip_member_state);
                textView.setVisibility(visible ? VISIBLE : GONE);
            }
        }*/
        LinearLayout child = (LinearLayout) linearLayout.findViewWithTag(childId);
        if (child != null) {
            TextView textView = (TextView)child.findViewById(R.id.rc_voip_member_state);
            textView.setVisibility(visible ? VISIBLE : GONE);
        }
    }
}
