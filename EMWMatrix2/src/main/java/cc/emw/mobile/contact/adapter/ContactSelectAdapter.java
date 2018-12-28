package cc.emw.mobile.contact.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 选人
 * 
 * @author shaobo.zhuang
 * 
 */
public class ContactSelectAdapter extends BaseExpandableListAdapter implements
		SectionIndexer, OnClickListener {
	protected static final String TAG = "PersonalAdapter";
	private ArrayList<SortInfo> infos;
	// 汉字转换成拼音的类
	private CharacterParser characterParser;
	// 根据拼音来排列ListView里面的数据类
	private PinyinComparator pinyinComparator;
	private Context context;
	private SortInfo info;

	private ArrayList<UserInfo> mDataList;
	private ArrayList<UserInfo> mSelectList;
	private int mSelectType;
	private UserInfo mSelectUser;
	private SparseBooleanArray mSelectMap;
	private DisplayImageOptions options;

	private static final String ONLINE_TITLE = "在线人员";
	private ArrayList<Integer> onlineIdList;

	public ContactSelectAdapter(Context context) {
		this.context = context;
		this.mDataList = new ArrayList<UserInfo>();
		mSelectList = new ArrayList<UserInfo>();
		mSelectMap = new SparseBooleanArray();
		pinyinComparator = new PinyinComparator();
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		/*
		 * sortData(); // 根据a-z进行排序源数据 Collections.sort(this.list,
		 * pinyinComparator);
		 */
		infos = new ArrayList<ContactSelectAdapter.SortInfo>();
		onlineIdList = new ArrayList<Integer>();
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

	public void setOnlineIdList(List<Integer> onlineIdList) {
		if (onlineIdList != null) {
			this.onlineIdList.clear();
			this.onlineIdList.addAll(onlineIdList);
		}
	}

	public void setDataList(ArrayList<UserInfo> dataList) {
		this.mDataList = dataList;
		makeDataSource(null);
	}

	public void setSelectType(int type) {
		this.mSelectType = type;
	}

	public void setRadioUser(UserInfo user) {
		this.mSelectUser = user;
	}

	public void setSelectList(ArrayList<UserInfo> selectList) {
		if (selectList != null) {
			this.mSelectList = selectList;
			for (UserInfo user : mSelectList) {
				mSelectMap.put(user.ID, true);
			}
		}
	}

	public SparseBooleanArray getSelectMap() {
		return mSelectMap;
	}

	/**
	 * 创建控件的数据源
	 * 
	 * @param s
	 *            插查询的字符串
	 * */
	public void makeDataSource(String s) {
		infos.clear();
		if (onlineIdList != null && onlineIdList.size() > 0) {
			info = new SortInfo();
			info.fchar = ONLINE_TITLE;
			for (Integer onlineId : onlineIdList) {
				for (UserInfo su : mDataList) {
					if (onlineId == su.ID)
						info.array.add(su);
				}
			}
			infos.add(info);
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
	 * */
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
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_contactselect_child, null);
			vh.headIv = (CircleImageView) convertView
					.findViewById(R.id.iv_contactselect_icon);
			vh.nameTv = (TextView) convertView
					.findViewById(R.id.tv_contactselect_name);
			vh.selectCb = (CheckBox) convertView
					.findViewById(R.id.cb_contactselect_select);
			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (CViewHolder) convertView.getTag(R.id.tag_first);
		}
		UserInfo user = infos.get(groupPosition).array.get(childPosition);
		String uri = String.format(Const.DOWN_ICON_URL,
				PrefsUtil.readUserInfo().CompanyCode, user.Image);
		ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
		switch (mSelectType) {
		case ContactSelectActivity.RADIO_SELECT:
			vh.selectCb.setButtonDrawable(R.drawable.cm_radio_select);
			if (mSelectUser != null && mSelectUser.ID == user.ID) {
				vh.selectCb.setChecked(true);
			} else {
				vh.selectCb.setChecked(false);
			}
			break;
		case ContactSelectActivity.MULTI_SELECT:
			vh.selectCb.setButtonDrawable(R.drawable.cm_multi_select);
			Boolean isSelect = mSelectMap.get(user.ID);
			if (isSelect != null && isSelect) {
				vh.selectCb.setChecked(true);
			} else {
				vh.selectCb.setChecked(false);
			}
			break;
		}

		vh.nameTv.setText(user.Name);
		convertView.setTag(R.id.tag_second, user);
		convertView.setOnClickListener(this);
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
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_contactselect_group, null);
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
				if (ONLINE_TITLE.equalsIgnoreCase(si.fchar))
					continue;
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
	 * 
	 * @param date
	 * @return
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

	@Override
	public void onClick(View v) {
		UserInfo user = (UserInfo) v.getTag(R.id.tag_second);
		switch (mSelectType) {
		case ContactSelectActivity.RADIO_SELECT:
			setRadioUser(user);
			break;
		case ContactSelectActivity.MULTI_SELECT:
			SparseBooleanArray selectMap = getSelectMap();
			Boolean isSelect = selectMap.get(user.ID);
			if (isSelect != null) {
				boolean curSelect = !isSelect;
				selectMap.put(user.ID, curSelect);
				if (curSelect) {
					mSelectList.add(user);
				} else {
					for (int i = 0, size = mSelectList.size(); i < size; i++) {
						if (user.ID == mSelectList.get(i).ID) {
							mSelectList.remove(i);
							break;
						}
					}
				}
			} else {
				selectMap.put(user.ID, true);
				mSelectList.add(user);
			}
			break;
		}
		notifyDataSetChanged();
	}

	public UserInfo getSelectUser() {

		return mSelectUser;
	}

	public List<UserInfo> getSelectList() {

		return mSelectList;
	}

	public class CViewHolder {
		CircleImageView headIv;
		TextView nameTv;
		CheckBox selectCb;
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
