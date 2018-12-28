package cc.emw.mobile.contact.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.contact.GroupInActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

public class GroupAllAdapter extends BaseExpandableListAdapter implements
        SectionIndexer {
    protected static final String TAG = "PersonalAdapter";
    private ArrayList<SortInfo> infos;
    // 汉字转换成拼音的类
    private CharacterParser characterParser;
    // 根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;
    private Context context;
    private SortInfo info;

    private List<GroupInfo> mDataList;
    private DisplayImageOptions options;

    public GroupAllAdapter(Context context) {
        this.context = context;
        this.mDataList = new ArrayList<GroupInfo>();
        pinyinComparator = new PinyinComparator();
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        /*
         * sortData(); // 根据a-z进行排序源数据 Collections.sort(this.list,
		 * pinyinComparator);
		 */
        infos = new ArrayList<>();
        // makeDataSource(null);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setDataList(ArrayList<GroupInfo> dataList) {
        this.mDataList = dataList;
        makeDataSource(null);
    }

    /**
     * 创建控件的数据源
     *
     * @param s 插查询的字符串
     */
    public void makeDataSource(String s) {
        infos.clear();
        for (GroupInfo su : mDataList) {
            int flag = -1;
            for (int i = 0; i < infos.size(); i++) {
                if (infos.get(i).fchar.equalsIgnoreCase(su.getSortLetters())) {
                    flag = i;
                    break;
                }
            }
            if (flag != -1) {
                infos.get(flag).array.add(su);
            } else {
                info = new SortInfo();
                info.fchar = su.getSortLetters();
                info.array.add(su);
                infos.add(info);
            }
            // if (map.containsKey(su.getSortLetters())) {
            // map.get(su.getSortLetters()).add(su);
            // } else {
            // temp = new ArrayList<SimpleUser>();
            // temp.add(su);
            // map.put(su.getSortLetters(), temp);
            // }
        }
    }

    /**
     * 设置搜索的字符串
     */
    public void setSearch(String input) {
        infos.clear();
        if ("".equals(input.trim())) {
            makeDataSource(null);
        } else {
            // 遍历集合
            for (GroupInfo su : mDataList) {
                if (su.Name.indexOf(input) != -1
                        || characterParser.getSelling(su.Name)
                        .startsWith(input)) {
                    // ------------------------------------
                    int flag = -1;
                    for (int i = 0; i < infos.size(); i++) {
                        if (infos.get(i).fchar.equalsIgnoreCase(su
                                .getSortLetters())) {
                            flag = i;
                            break;
                        }
                    }
                    if (flag != -1) {
                        infos.get(flag).array.add(su);
                    } else {
                        info = new SortInfo();
                        info.fchar = su.getSortLetters();
                        info.array.add(su);
                        infos.add(info);
                    }
                    // -------------------------------------
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public GroupInfo getChild(int arg0, int arg1) {
        return getGroup(arg0).get(arg1);
    }

    @Override
    public long getChildId(int arg0, int arg1) {
        return arg1;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        CViewHolder vh;
        if (convertView == null) {
            vh = new CViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.listitem_contact_child, null);
            vh.headIv = (CircleImageView) convertView
                    .findViewById(R.id.iv_contactselect_icon);
            vh.nameTv = (TextView) convertView
                    .findViewById(R.id.tv_contactselect_name);
            vh.tag = convertView.findViewById(R.id.view_contact_is_online);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (CViewHolder) convertView.getTag(R.id.tag_first);
        }
        final GroupInfo user = infos.get(groupPosition).array.get(childPosition);
        String uri = Const.BASE_URL + user.Image;
        ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
        vh.tag.setVisibility(View.GONE);
        vh.nameTv.setText(user.Name);
        convertView.setTag(R.id.tag_second, user);
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupInActivity.class);
                if (!user.IsAddIn) {
                    intent.putExtra("intoTag", 2);
                }
                intent.putExtra("intoTag", 1);
                intent.putExtra("GroupInfo", user);
                intent.putExtra("start_anim", false);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int arg0) {
        return getGroup(arg0).size();
    }

    @Override
    public ArrayList<GroupInfo> getGroup(int arg0) {
        return infos.get(arg0).array;
    }

    @Override
    public int getGroupCount() {
        return infos.size();
    }

    @Override
    public long getGroupId(int arg0) {
        return arg0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        PViewHolder vh;
        if (convertView == null) {
            vh = new PViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.listitem_contact_group, null);
            convertView.setClickable(true);
            vh.letterTv = (TextView) convertView
                    .findViewById(R.id.tv_contactselect_letter);
            convertView.setTag(vh);
        } else {
            vh = (PViewHolder) convertView.getTag();
        }
        vh.letterTv.setText(infos.get(groupPosition).fchar);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    @Override
    public int getPositionForSection(int section) {
        int i = 0;
        for (SortInfo si : infos) {
            for (int j = 0; j < si.array.size(); j++) {
                String sortStr = si.array.get(j).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            i++;
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        int i = 0, m = 0;
        for (SortInfo si : infos) {
            for (int j = 0; j < si.array.size(); j++) {
                if (i == position) {
                    m = si.array.get(j).getSortLetters().charAt(0);
                    break;
                }
            }
            i++;
        }
        return m;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 为ListView填充数据
     */
    private void sortData() {
        for (int i = 0; i < mDataList.size(); i++) {
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(mDataList.get(i).Name);
            if (!TextUtils.isEmpty(pinyin)) {
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    mDataList.get(i).setSortLetters(sortString.toUpperCase());
                } else {
                    mDataList.get(i).setSortLetters("#");
                }
            } else {
                mDataList.get(i).setSortLetters("#");
            }
        }
    }

    public class CViewHolder {
        CircleImageView headIv;
        TextView nameTv;
        View tag;
    }

    public class PViewHolder {
        // 字母间隔
        TextView letterTv;
    }

    class SortInfo {
        // 首字母
        String fchar;
        ArrayList<GroupInfo> array;

        public SortInfo() {
            array = new ArrayList<GroupInfo>();
        }
    }
}
