package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.PinyinComparatorGroups;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

public class GroupSelectAdapter extends BaseExpandableListAdapter implements
		SectionIndexer {
	private ArrayList<SortInfo> infos;
	private CharacterParser characterParser; // 汉字转换成拼音的类
	private PinyinComparatorGroups pinyinComparatorGroups; // 根据拼音来排列ListView里面的数据类
	private Context context;
	private List<GroupInfo> mDataList;
	private DisplayImageOptions options;
	private GroupInfo targetG = new GroupInfo();

	public GroupSelectAdapter(Context context) {
		this.context = context;
		this.mDataList = new ArrayList<GroupInfo>();
		pinyinComparatorGroups = new PinyinComparatorGroups();
		characterParser = CharacterParser.getInstance();

		infos = new ArrayList<>();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cm_img_grouphead) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.cm_img_grouphead) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.cm_img_grouphead) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
	}

	public GroupInfo getTargetG() {
		return targetG;
	}

	public void setDataList(List<GroupInfo> dataList) {
		this.mDataList = dataList;
		sortData(); // 根据a-z进行排序源数据
		Collections.sort(this.mDataList, pinyinComparatorGroups);
		setSearch("");
	}

	/**
	 * 设置搜索的字符串
	 */
	public void setSearch(String input) {
		infos.clear();
        for (GroupInfo su : mDataList) {
            if (!TextUtils.isEmpty(input.trim()) && su.Name.indexOf(input) != -1
                    || characterParser.getSelling(su.Name)
                    .startsWith(input)) {
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
                    SortInfo info = new SortInfo();
                    info.fchar = su.getSortLetters();
                    info.array.add(su);
                    infos.add(info);
                }
            }
        }
		notifyDataSetChanged();
	}

	@Override
	public GroupInfo getChild(int groupPosition, int childPosition) {
		return getGroup(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
		CViewHolder vh;
		if (convertView == null) {
			vh = new CViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_contact_groups_select, null);
			vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_groupselect_head);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_groupselect_name);
			vh.checkBox = (CheckBox) convertView.findViewById(R.id.cb_groupselect);
			convertView.setTag(vh);
		} else {
			vh = (CViewHolder) convertView.getTag();
		}

		final GroupInfo gInfo = infos.get(groupPosition).array.get(childPosition);

		final String uri = String.format(Const.DOWN_ICON_URL,
				PrefsUtil.readUserInfo().CompanyCode, gInfo.Image);

		ImageLoader.getInstance().displayImage(uri, vh.headIv, options);

		vh.nameTv.setText(gInfo.Name);

		vh.checkBox.setChecked(gInfo.ID == targetG.ID ? true : false);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				targetG = gInfo;
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return getGroup(groupPosition).size();
	}

	@Override
	public ArrayList<GroupInfo> getGroup(int groupPosition) {
		return infos.get(groupPosition).array;
	}

	@Override
	public int getGroupCount() {
		return infos.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		PViewHolder vh;
		if (convertView == null) {
			vh = new PViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_contact_group, null);
			convertView.setClickable(true);
			vh.letterTv = (TextView) convertView.findViewById(R.id.tv_contactselect_letter);
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
	public boolean isChildSelectable(int groupPosition, int childPosition) {
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
		CheckBox checkBox;
	}

	public class PViewHolder {
		TextView letterTv; // 首字母
	}

	class SortInfo {

		String fchar; // 首字母
		ArrayList<GroupInfo> array;

		public SortInfo() {
			array = new ArrayList<>();
		}
	}
}