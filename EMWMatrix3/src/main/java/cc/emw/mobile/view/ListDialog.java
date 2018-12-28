package cc.emw.mobile.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.util.HelpUtil;

/**
 * 对弹出菜单的封装.
 * Author: msdx (645079761@qq.com)
 * Time: 14-6-13 下午1:51
 */
public class ListDialog {
    /**
     * 上下文.
     */
    private Context mContext;
    /**
     * 菜单项
     */
    private ArrayList<Item> mItemList;
    /**
     * 列表适配器.
     */
    private DialogAdapter mAdapter;
    /**
     * 菜单选择监听.
     */
    private OnItemSelectedListener mListener;
    /**
     * 列表.
     */
    private ListView mListView;
    /**
     * 弹出窗口.
     */
    private AlertDialog mDialog;

    public ListDialog(Context context, boolean isShowIcon) {
        this(context, isShowIcon, false, false);
    }

    public ListDialog(Context context, boolean isShowIcon, boolean isShowType, boolean isShowImgType) {
        mContext = context;
        mItemList = new ArrayList<Item>(2);
        mDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.view_list_dialog, null, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        mAdapter = new DialogAdapter(context, mItemList);
        mAdapter.setShowIcon(isShowIcon);
        mAdapter.setShowTypeIcon(isShowType);
        mAdapter.setShowImgType(isShowImgType);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectItem(position);
                Item item = mAdapter.getItem(position);
                if (mListener != null) {
                    mListener.selected(view, item, position);
                }
                mDialog.dismiss();
            }
        });
        mDialog.setView(view);
    }

    public void setSelectItem(int position) {
        mAdapter.setSelectItem(position);
    }

    public static class DialogAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<Item> mDataList;
        private int selectItem;
        private boolean isShow; //是否右边出现选择的√
        private boolean isShowType; //是否左边显示类型图标，动态筛选类型专用
        private boolean isShowImgType; //新建圈子协作，颜色选择专用

        public DialogAdapter(Context context,
                             ArrayList<Item> dataList) {
            this.mContext = context;
            this.mDataList = dataList;
        }

        public void setSelectItem(int position) {
            selectItem = position;
        }

        public void setShowIcon(boolean isShow) {
            this.isShow = isShow;
        }

        public void setShowTypeIcon(boolean isShow) {
            this.isShowType = isShow;
        }
        public void setShowImgType(boolean isShow) {
            this.isShowImgType = isShow;
        }

        @Override
        public int getCount() {
            return mDataList != null ? mDataList.size() : 0;
        }

        @Override
        public Item getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.view_listitem_list_dialog, null);
                vh.typeItv = (IconTextView) convertView.findViewById(R.id.itv_dialoglist_typeicon);
                vh.typeIv = (ImageView) convertView.findViewById(R.id.iv_dialoglist_typeicon);
                vh.textTv = (TextView) convertView.findViewById(R.id.tv_dialoglist_type);
                vh.iconTextView = (IconTextView) convertView.findViewById(R.id.itv_dialoglist_icon);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            Item item = mDataList.get(position);
            vh.textTv.setText(item.text);
            if (isShow) {
                vh.iconTextView.setVisibility(selectItem == position? View.VISIBLE : View.GONE);
            }
            if (isShowType) {
//                HelpUtil.setDynamicType(position, vh.typeItv);
                HelpUtil.setDynamicType(position, vh.typeIv);
            }
            if (isShowImgType) {
                vh.typeIv.setImageResource(CommonUtil.getProjectRoundColor(item.id));
                vh.typeIv.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        IconTextView typeItv;
        ImageView typeIv;
        TextView textTv;
        IconTextView iconTextView;
    }

    public AlertDialog getAlertDialog() {
        return mDialog;
    }

    /**
     * 添加菜单项.
     *
     * @param text 菜单项文字内容.
     * @param id   菜单项的ID
     */
    public void addItem(String text, int id) {
        mItemList.add(new Item(text, id));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 添加菜单项.
     *
     * @param resId 菜单项文字内容的资源ID
     * @param id    菜单项的ID.
     */
    public void addItem(int resId, int id) {
        addItem(mContext.getString(resId), id);
    }

    /**
     *
     */
    public void show() {
        mDialog.show();
    }
    

    /**
     * 隐藏菜单.
     */
    public void dismiss() {
        mDialog.dismiss();
    }

    /**
     * 设置菜单选择监听.
     *
     * @param listener 监听器.
     */
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    /**
     * 当前菜单是否正在显示.
     *
     * @return
     */
    public boolean isShowing() {
        return mDialog.isShowing();
    }

    /**
     * 菜单项.
     */
    public static class Item {
        public String text;
        public int id;

        public Item(String text, int id) {
            this.text = text;
            this.id = id;
        }
        
        @Override
        public String toString() {
            return text;
        }
    }

    /**
     * 菜单项选择监听接口.
     */
    public static interface OnItemSelectedListener {
        /**
         * 菜单被选择时的回调接口.
         *
         * @param view     被选择的内容的View.
         * @param item     被选择的菜单项.
         * @param position 被选择的位置.
         */
        public void selected(View view, Item item, int position);
    }
}
