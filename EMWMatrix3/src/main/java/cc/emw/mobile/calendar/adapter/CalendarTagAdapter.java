package cc.emw.mobile.calendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarTagActivity;
import cc.emw.mobile.calendar.fragment.CalendarFragment;
import cc.emw.mobile.calendar.fragment.CalendarFragmentView;
import cc.emw.mobile.calendar.fragment.WeekFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CollapseView;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by ${zrjt} on 2016/7/2.
 */
public class CalendarTagAdapter extends BaseAdapter {

    private Context mContext;
    private List<ApiEntity.UserLabel> mDataList;
    private ViewHolder holder;
    private int lastPosition = -1;
    private int select;
    private CollapseView mTitleLayout;
    private RelativeLayout tagContentLayout;
    private String delTagName;

    public CalendarTagAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setTagContentLayout(RelativeLayout tagContentLayout) {
        this.tagContentLayout = tagContentLayout;
    }

    public void setCollaspeView(CollapseView mTitleLayout) {
        this.mTitleLayout = mTitleLayout;
    }

    public void setData(List<ApiEntity.UserLabel> mDataList) {
        this.mDataList = mDataList;
    }

    public ApiEntity.UserLabel getSelectTag() {
        return mDataList.get(select);
    }

    public int getSelect() {
        return select - 1;
    }

    public void getLastPosition(int count) {
        lastPosition = count;
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_calendar_tag, null);
            holder.tvTag = (TextView) convertView.findViewById(R.id.tv_calendar_tag);
            holder.iconTextView = (IconTextView) convertView.findViewById(R.id.icon_tv_calendar_tag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ApiEntity.UserLabel label = mDataList.get(position);
        final String tagName = label.Name;

//        if (position == 0) {
//            holder.tvTag.setTextColor(mContext.getResources().getColor(R.color.dynamic_name_text));
//        }

        holder.iconTextView.setVisibility(lastPosition == position ? View.VISIBLE : View.GONE);

        if (lastPosition != -1)
            select = lastPosition;

        holder.tvTag.setText(tagName);

        if (position == 0) {
            holder.tvTag.setTextColor(mContext.getResources().getColor(R.color.main_menu_bg));
            holder.tvTag.setText("#无");
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleLayout.setTitle(tagName);
                if (position == 0) {
                    mTitleLayout.setTitle("#无");
                }
                if (lastPosition == position) {
                    lastPosition = -1;
                } else {
                    lastPosition = position;
                }
                mTitleLayout.rotateArrow();
                notifyDataSetChanged();
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog(mContext).builder().setMsg("确认要删除该标签？")
                        .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(mContext.getString(R.string.confirm),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (position <= 7) {
                                            ToastUtil.showToast(mContext, "系统自带标签,无法删除");
                                        } else {
                                            delTagName = label.Name;
                                            delUserLabel(label.ID);
                                        }
                                    }
                                })
                        .setNegativeButton(mContext.getString(R.string.cancel),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                return false;
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView tvTag;
        IconTextView iconTextView;
    }

    private void delUserLabel(int id) {
        API.TalkerAPI.DelUserLabel(id, new RequestCallback<ApiEntity.APIResult>(ApiEntity.APIResult.class) {

            @Override
            public void onStarted() {

            }

            @Override
            public void onParseSuccess(ApiEntity.APIResult respInfo) {
                if (respInfo.State == 1) {
                    notifyDataSetChanged();
                    if (delTagName.equals(mTitleLayout.getTitle())) {
                        mTitleLayout.setTitle("#无");
                    }
                    ToastUtil.showToast(mContext, "删除成功", R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent();
                    intent.setAction(CalendarTagActivity.ACTION_REFRESH_CALENDAR_TAG);
                    mContext.sendBroadcast(intent);
                    intent = new Intent();
                    intent.setAction(CalendarFragment.ACTION_REFRESH_CALENDAR_FRAGMENT);
                    mContext.sendBroadcast(intent);
                    intent = new Intent();
                    intent.setAction(WeekFragment.ACTION_REFRESH_SCHEDULE_LIST);
                    mContext.sendBroadcast(intent);
                    intent = new Intent();
                    intent.setAction(CalendarFragmentView.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                    mContext.sendBroadcast(intent);
                } else {
                    ToastUtil.showToast(mContext, "服务器异常");
                }
            }

            //
//            @Override
//            public void onSuccess(String result) {
//                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
//                    notifyDataSetChanged();
//                    ToastUtil.showToast(mContext, "删除成功", R.drawable.tishi_ico_gougou);
//                    Intent intent = new Intent();
//                    intent.setAction(CalendarTagActivity.ACTION_REFRESH_CALENDAR_TAG);
//                    mContext.sendBroadcast(intent);
//                    intent = new Intent();
//                    intent.setAction(CalendarFragment.ACTION_REFRESH_CALENDAR_FRAGMENT);
//                    mContext.sendBroadcast(intent);
//                    intent = new Intent();
//                    intent.setAction(WeekFragment.ACTION_REFRESH_SCHEDULE_LIST);
//                    mContext.sendBroadcast(intent);
//                    intent = new Intent();
//                    intent.setAction(CalendarMonthFragments.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
//                    mContext.sendBroadcast(intent);
//                }
//            }
//
            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(mContext, "服务器异常");
            }
        });
    }
}
