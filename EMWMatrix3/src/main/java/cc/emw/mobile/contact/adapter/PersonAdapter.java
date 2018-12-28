package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;

public class PersonAdapter extends BaseExpandableListAdapter implements
        SectionIndexer {
    protected static final String TAG = "PersonalAdapter";
    // 汉字转换成拼音的类
    private CharacterParser characterParser;
    // 根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;
    private Context context;
    private SortInfo info;
    private DisplayImageOptions options;
    private ArrayList<SortInfo> infos;
    private List<UserInfo> mDataList;
    private List<UserInfo> mConcernList;
    private ImageLoader imageLoader;
    private List<Integer> onLineList;
    private LayoutInflater inflater;

    private ListDialog mAddDialog;

    private void initAddDialog(final UserInfo userInfo) {
        mAddDialog = new ListDialog(context, false);
        mAddDialog.addItem("语音通话", 1);
        mAddDialog.addItem("视频通话", 2);
        mAddDialog.addItem("拨打电话", 3);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case 1:
                        /*if (TextUtils.isEmpty(PrefsUtil.readUserInfo().VoipCode)) {
                            ToastUtil.showToast(context, "你暂未开通语音通话服务，请联系管理员申请开通。");
                            break;
                        }
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.Name) && !TextUtils.isEmpty(userInfo.VoipCode)) {
                            Intent intentVoice = new Intent(context, AudioConverseActivity.class);
                            intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intentVoice.putExtra("userName", userInfo.Name);
                            intentVoice.putExtra("userId", userInfo.VoipCode);
                            intentVoice.putExtra("call_phone", userInfo.Phone);
                            intentVoice.putExtra("call_type", 4);//1:免费电话 2:直拨 4:智能
                            intentVoice.putExtra("call_head", userInfo.Image);
                            context.startActivity(intentVoice);
                        } else {
                            ToastUtil.showToast(context, "对方暂未开通语音通话服务，请联系管理员申请开通。");
                        }*/
                        HelpUtil.startVoice(context, userInfo);
                        break;
                    case 2:
                        /*if (TextUtils.isEmpty(PrefsUtil.readUserInfo().VoipCode)) {
                            ToastUtil.showToast(context, "你暂未开通视频通话服务，请联系管理员申请开通。");
                            break;
                        }
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.Name) && !TextUtils.isEmpty(userInfo.VoipCode)) {
                            Intent intentVideo = new Intent(context, VideoConverseActivity.class);
                            intentVideo.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intentVideo.putExtra("userName", userInfo.Name);
                            intentVideo.putExtra("userId", userInfo.VoipCode);
                            intentVideo.putExtra("call_phone", userInfo.Phone);
                            intentVideo.putExtra("call_position", "");
                            context.startActivity(intentVideo);
                        } else {
                            ToastUtil.showToast(context, "对方暂未开通视频通话服务，请联系管理员申请开通。");
                        }*/
                        HelpUtil.startVideo(context, userInfo);
                        break;
                    case 3:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userInfo.Phone.toString()));
                        context.startActivity(intent);
                        break;
                }
            }
        });
    }

    public PersonAdapter(Context context) {
        this.context = context;
        this.mDataList = new ArrayList<>();
        this.mConcernList = new ArrayList<>();
        this.onLineList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        pinyinComparator = new PinyinComparator();
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        /*
         * sortData(); // 根据a-z进行排序源数据 Collections.sort(this.list,
		 * pinyinComparator);
		 */
        infos = new ArrayList<>();
//        makeDataSource(null);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setOnLineList(List<Integer> onLineList) {
        this.onLineList = onLineList;
    }

    public ImageLoader getImageLoader() {
        return this.imageLoader;
    }

    public void setDataList(List<UserInfo> mDataList) {
        this.mDataList = mDataList;
        makeDataSource(null);
    }

    public void setConcernList(List<UserInfo> mConcernList) {
        this.mConcernList = mConcernList;
        makeDataSource(null);
    }

    /**
     * 创建控件的数据源
     *
     * @param s 插查询的字符串
     */
    public void makeDataSource(String s) {
        infos.clear();
        if (mConcernList != null && mConcernList.size() > 0) {
            for (int i = 0; i < mConcernList.size(); i++) {
                mConcernList.get(i).setSortLetters("我关注的");
            }
            info = new SortInfo();
            info.fchar = "我关注的";
            info.array.addAll(mConcernList);
            infos.add(info);
        } else {
            infos.clear();
        }
        for (UserInfo su : mDataList) {
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
            for (UserInfo su : mDataList) {
                if (su.Name.indexOf(input) != -1
                        || characterParser.getSelling(su.Name)
                        .startsWith(input)) {
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
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public UserInfo getChild(int arg0, int arg1) {
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
            convertView = inflater.inflate(
                    R.layout.listitem_contact_child, null);
            vh.headIv = (CircleImageView) convertView
                    .findViewById(R.id.iv_contactselect_icon);
            vh.nameTv = (TextView) convertView
                    .findViewById(R.id.tv_contactselect_name);
            vh.isOnline = convertView.findViewById(R.id.view_contact_is_online);
            vh.iconTextView = (IconTextView) convertView.findViewById(R.id.itv_contact_item);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (CViewHolder) convertView.getTag(R.id.tag_first);
        }

        if (infos.get(groupPosition).array.size() > childPosition) {
            final UserInfo user = infos.get(groupPosition).array.get(childPosition);
            String uri = String.format(Const.DOWN_ICON_URL,
                    TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);

            imageLoader.displayImage(uri, new ImageViewAware(vh.headIv), options,
                    new ImageSize(DisplayUtil.dip2px(context, 40), DisplayUtil.dip2px(context, 40)), null, null);

            vh.nameTv.setText(user.Name);

            if (onLineList.contains(user.ID))
                vh.isOnline.setBackgroundResource(R.drawable.circle_is_online);
            else
                vh.isOnline.setBackgroundResource(R.drawable.circle_is_not_online);

            if (TextUtils.isEmpty(user.Phone.toString()) || !TextUtils.isDigitsOnly(user.Phone.toString())) {
                if (TextUtils.isEmpty(PrefsUtil.readUserInfo().VoipCode))
                    vh.iconTextView.setVisibility(View.GONE);
            } else {
                vh.iconTextView.setVisibility(View.VISIBLE);
            }

            vh.iconTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    initAddDialog(user);
                    mAddDialog.show();
                }
            });

            convertView.setTag(R.id.tag_second, user);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonInfoActivity.class);
                    intent.putExtra("UserInfo", user);
                    intent.putExtra("intoTag", 1);
                    intent.putExtra("start_anim", false);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int arg0) {
        return getGroup(arg0).size();
    }

    @Override
    public ArrayList<UserInfo> getGroup(int arg0) {
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
            convertView = inflater.inflate(
                    R.layout.listitem_contact_group, null);
            convertView.setClickable(true);
            vh.letterTv = (TextView) convertView
                    .findViewById(R.id.tv_contactselect_letter);
            convertView.setTag(vh);
        } else {
            vh = (PViewHolder) convertView.getTag();
        }
        vh.letterTv.setText(infos.get(groupPosition).fchar);
        if (mConcernList != null && groupPosition == 0 && mConcernList.size() > 0) {
            vh.letterTv.setText("我关注的");
        }
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
        View isOnline;
        TextView nameTv;
        IconTextView iconTextView;
    }

    public class PViewHolder {
        // 字母间隔
        TextView letterTv;
    }

    class SortInfo {
        // 首字母
        String fchar;
        ArrayList<UserInfo> array;

        public SortInfo() {
            array = new ArrayList<UserInfo>();
        }
    }
}
