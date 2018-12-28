package cc.emw.mobile.contact.adapter;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zf.iosdialog.widget.AlertDialog;

/**
 * 人员拼音排序adapter
 * 
 * @author shaobo.zhuang
 * 
 */
public class ContactAdapter extends BaseExpandableListAdapter implements
		SectionIndexer {
	protected static final String TAG = "ContactAdapter";
	private ArrayList<UserInfo> list;
	// private HashMap<String, ArrayList<UserInfo>> map;
	private ArrayList<SortInfo> infos;
	private UserInfo user;
	// 汉字转换成拼音的类
	private CharacterParser characterParser;
	// 根据拼音来排列ListView里面的数据类
	private PinyinComparator pinyinComparator;
	private CViewHolder cvh;
	private PViewHolder pvh;
	private Context context;
	private LayoutInflater inflater;
	private SortInfo info;
	private DisplayImageOptions options;
	private Dialog mLoadingDialog; // 加载框
	private ExpandableListView mExpandableListView;

	/**
	 * @param Dlist
	 *            部门集合
	 * @param SUlist
	 *            人员集合
	 * */
	public ContactAdapter(Context context, ExpandableListView elv,
			Dialog loadingDialog) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.list = new ArrayList<UserInfo>();
		pinyinComparator = new PinyinComparator();
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		/*
		 * sortData(); // 根据a-z进行排序源数据 Collections.sort(this.list,
		 * pinyinComparator);
		 */
		infos = new ArrayList<ContactAdapter.SortInfo>();
		// makeDataSource(null);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象

		mExpandableListView = elv;
		mLoadingDialog = loadingDialog;
	}

	public void setDataList(ArrayList<UserInfo> dataList) {
		this.list = dataList;
		//makeDataSource(null);
	}

	/**
	 * 创建控件的数据源
	 * 
	 * @param s
	 *            插查询的字符串
	 * */
	public void makeDataSource(String s) {
		for (UserInfo su : list) {
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
			// temp = new ArrayList<UserInfo>();
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
			// makeDataSource(null);
		} else {
			// 遍历集合
			for (UserInfo su : list) {
				if (su.Name.indexOf(input) != -1
						|| characterParser.getSelling(su.Name).startsWith(
								input)) {
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
	public UserInfo getChild(int groupPosition, int childPosition) {
		return getGroup(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_contact_childs, null);
			vh.headIv = (CircleImageView) convertView
					.findViewById(R.id.contact_iv_icon);
			vh.nameTv = (TextView) convertView
					.findViewById(R.id.contact_tv_name);
			vh.followBtn = (ImageButton) convertView
					.findViewById(R.id.contact_btn_follow);
			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		final UserInfo user = infos.get(groupPosition).array
				.get(childPosition);
		//String uri = "http://www.bz55.com/uploads/allimg/150309/139-150309101F2.jpg";
		 String uri = String.format(Const.DOWN_ICON_URL,
		 PrefsUtil.readUserInfo().CompanyCode, user.Image);
		ImageLoader.getInstance().displayImage(uri, vh.headIv, options);

		vh.nameTv.setText(user.Name);
		vh.followBtn.setSelected(user.IsFollow ? true : false);
		vh.followBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String msg = user.IsFollow ? "确认取消追随？" : "确认追随？";
				new AlertDialog(context).builder().setMsg(msg)
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (user.IsFollow) {
									// delFollow(user);
								} else {
									// addFollow(user);
								}
							}
						}).setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {
							}
						}).show();
			}
		});
		convertView.setTag(R.id.tag_second, user);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return getGroup(groupPosition).size();
	}

	@Override
	public ArrayList<UserInfo> getGroup(int groupPosition) {
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
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder vh;
		if (convertView == null) {
			vh = new GroupViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_contact_groups, null);
			convertView.setClickable(true);
			vh.letterTv = (TextView) convertView
					.findViewById(R.id.contact_tv_letter);
			convertView.setTag(vh);
		} else {
			vh = (GroupViewHolder) convertView.getTag();
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
	 * 
	 * @param date
	 * @return
	 */
	private void sortData() {
		for (int i = 0; i < list.size(); i++) {
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(list.get(i).Name);
			if (!TextUtils.isEmpty(pinyin)) {
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					list.get(i).setSortLetters(sortString.toUpperCase());
				} else {
					list.get(i).setSortLetters("#");
				}
			} else {
				list.get(i).setSortLetters("#");
			}
		}
	}

	static class GroupViewHolder {
		TextView letterTv;
	}

	static class ViewHolder {
		CircleImageView headIv;
		TextView nameTv;
		ImageButton followBtn;
	}

	public class CViewHolder {
		ImageView iv_icon, iv_line;
		TextView tv_username;
	}

	public class PViewHolder {
		// 字母间隔
		TextView tv_letter;
	}

	class SortInfo {
		// 首字母
		String fchar;
		ArrayList<UserInfo> array;

		public SortInfo() {
			array = new ArrayList<UserInfo>();
		}
	}

//	/**
//	 * 添加追随
//	 * 
//	 * @param uid
//	 */
//	private void addFollow(final UserInfo user) {
//		HttpUtils http = new HttpUtils();
//		http.configCookieStore(PrefsUtil.readLoginCookie());
//		RequestParam param = new RequestParam();
//		param.setStringParams(user.getID());
//		http.send(HttpMethod.POST, HttpConst.Url_Client
//				+ "?t=User.UserAdmin&m=FollowUser", param,
//				new RequestCallBack<String>() {
//					@Override
//					public void onStart() {
//						if (mLoadingDialog != null)
//							mLoadingDialog.show();
//					}
//
//					@Override
//					public void onSuccess(ResponseInfo<String> responseInfo) {
//						LogUtils.d("send:" + responseInfo.result);
//						if (mLoadingDialog != null)
//							mLoadingDialog.dismiss();
//						if (responseInfo.result != null
//								&& !"0".equals(responseInfo.result)) {
//							Toast.makeText(context, "追随成功！", Toast.LENGTH_SHORT)
//									.show();
//							user.setIsFollow(true);
//							notifyDataSetChanged();
//							for (int i = 0, length = getGroupCount(); i < length; i++) {
//								mExpandableListView.expandGroup(i);
//							}
//						} else {
//							Toast.makeText(context, "追随失败！", Toast.LENGTH_SHORT)
//									.show();
//						}
//					}
//
//					@Override
//					public void onFailure(HttpException error, String msg) {
//						if (mLoadingDialog != null)
//							mLoadingDialog.dismiss();
//						Toast.makeText(context,
//								error.getExceptionCode() + " " + msg,
//								Toast.LENGTH_SHORT).show();
//					}
//				});
//	}

//	/**
//	 * 取消追随
//	 * 
//	 * @param uid
//	 */
//	private void delFollow(final UserInfo user) {
//		HttpUtils http = new HttpUtils();
//		http.configCookieStore(PrefsUtil.readLoginCookie());
//		RequestParam param = new RequestParam();
//		param.setStringParams(user.getID());
//		http.send(HttpMethod.POST, HttpConst.Url_Client
//				+ "?t=User.UserAdmin&m=RemoveFollow", param,
//				new RequestCallBack<String>() {
//					@Override
//					public void onStart() {
//						if (mLoadingDialog != null)
//							mLoadingDialog.show();
//					}
//
//					@Override
//					public void onSuccess(ResponseInfo<String> responseInfo) {
//						LogUtils.d("send:" + responseInfo.result);
//						if (mLoadingDialog != null)
//							mLoadingDialog.dismiss();
//						if (responseInfo.result != null
//								&& !"0".equals(responseInfo.result)) {
//							Toast.makeText(context, "取消追随成功！",
//									Toast.LENGTH_SHORT).show();
//							user.setIsFollow(false);
//							notifyDataSetChanged();
//							for (int i = 0, length = getGroupCount(); i < length; i++) {
//								mExpandableListView.expandGroup(i);
//							}
//						} else {
//							Toast.makeText(context, "取消追随失败！",
//									Toast.LENGTH_SHORT).show();
//						}
//					}
//
//					@Override
//					public void onFailure(HttpException error, String msg) {
//						if (mLoadingDialog != null)
//							mLoadingDialog.dismiss();
//						Toast.makeText(context,
//								error.getExceptionCode() + " " + msg,
//								Toast.LENGTH_SHORT).show();
//					}
//				});
//	}
}
