package io.rong.callkit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SideBar;
import cc.emw.mobile.view.SideBar.OnTouchingLetterChangedListener;
import in.srain.cube.views.ptr.PtrFrameLayout;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 人员选择
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.rc_voip_activity_select_contact)
public class CallSelectContactActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left9)
    private IconTextView mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right9)
    private TextView mHeaderOkTv;

    @ViewInject(R.id.et_contact_search)
    private EditText mSearchEt;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.contact_elv_person)
    private FloatingGroupExpandableListView mListView;
    @ViewInject(R.id.contact_sidebar)
    private SideBar mSideBar;
    @ViewInject(R.id.contact_tv_letter)
    private TextView mTextDialog;

    public static final String EXTRA_INTO_FLAG = "into_tag";
    public static final int FLAG_NORMAL = 0; //默认
    public static final int FLAG_CHAT_AUDIO = 1; //从聊天发起语音聊天
    public static final int FLAG_CHAT_VIDEO = 2; //从聊天发起视频聊天

    public static final String EXTRA_SELECT_TYPE = "select_type";
    /**
     * 单选
     */
    public static final int RADIO_SELECT = 1;
    /**
     * 多选
     */
    public static final int MULTI_SELECT = 2;

    public static final String EXTRA_SELECT_USER = "select_user"; //默认选中的用户
    public static final String EXTRA_SELECT_LIST = "select_list"; //默认选中的用户集
    public static final String EXTRA_FILTER_LIST = "filter_list"; //显示的用户集
    public static final String EXTRA_CLICKABLE_UID = "clickable_uid"; //不处理点击事件的用户ID

    private Dialog mLoadingDialog; // 加载框
    private ContactSelectAdapter mSelectAdapter;
    private ArrayList<UserInfo> mDataList, mFilterList;
    private int mPosition, mSelectType;
    private boolean hasOneself; //是否显示自己
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getIntent().getIntExtra("position", 0);
        mSelectType = getIntent().getIntExtra(EXTRA_SELECT_TYPE, MULTI_SELECT);

        hasOneself = getIntent().getBooleanExtra("has_oneself", true);
        UserInfo user = (UserInfo) getIntent().getSerializableExtra(EXTRA_SELECT_USER);

        ArrayList<UserInfo> selectList = (ArrayList<UserInfo>) getIntent().getSerializableExtra(EXTRA_SELECT_LIST);
        mFilterList = (ArrayList<UserInfo>) getIntent().getSerializableExtra(EXTRA_FILTER_LIST);

        flag = getIntent().getIntExtra(EXTRA_INTO_FLAG, FLAG_NORMAL);

        mHeaderBackBtn.setIconText("eb68");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
//        mHeaderTitleTv.setText(R.string.contactselect);
        mHeaderOkTv.setText(R.string.ok);
        mHeaderOkTv.setVisibility(View.VISIBLE);
        mPtrFrameLayout.setEnabled(false);

        mSideBar.setTextView(mTextDialog);
        mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mSelectAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    // elv.setSelection(position);
                    mListView.setSelectedGroup(position);
                }
            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                HelpUtil.hideSoftInput(CallSelectContactActivity.this, mSearchEt);
                mSelectAdapter.setSearch(s.toString().toLowerCase().trim());
                // 展开所有
                for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
                    mListView.expandGroup(i);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDataList = new ArrayList<>();
        mSelectAdapter = new ContactSelectAdapter(this);
        mSelectAdapter.setSelectType(mSelectType);
        int uid = getIntent().getIntExtra(EXTRA_CLICKABLE_UID, 0);
        mSelectAdapter.setClickableItemID(uid);
        if (mSelectType == RADIO_SELECT) {
            mSelectAdapter.setRadioUser(user);
        } else {
            mSelectAdapter.setSelectList(selectList);
        }
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(mSelectAdapter);
        mListView.setAdapter(wrapperAdapter);
        mListView.setDividerHeight(0);

        if (mFilterList != null) {
            mSelectAdapter.setDataList(mFilterList);
            mSelectAdapter.notifyDataSetChanged();
            for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
                mListView.expandGroup(i);
            }
        } else if (EMWApplication.personSortList != null && EMWApplication.personSortList.size() > 0) {
            mDataList.addAll(EMWApplication.personSortList);
            if (!hasOneself) {
                for (UserInfo userInfo : mDataList) {
                    if (userInfo.ID == PrefsUtil.readUserInfo().ID) {
                        mDataList.remove(userInfo);
                        break;
                    }
                }
            }
            mSelectAdapter.setDataList(mDataList);
            mSelectAdapter.notifyDataSetChanged();
            for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
                mListView.expandGroup(i);
            }
        } else {
            getPersonList("");
        }

    }

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                HelpUtil.hideSoftInput(this, mSearchEt);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                Intent data = new Intent();
                if (mSelectType == RADIO_SELECT) {
                    data.putExtra("position", mPosition);
                    data.putExtra(EXTRA_SELECT_USER, mSelectAdapter.getSelectUser());
                    if (mSelectAdapter.getSelectUser() == null) {
                        ToastUtil.showToast(this, R.string.contactselect_empty);
                        return;
                    }
                } else {
                    if (flag == FLAG_CHAT_AUDIO || flag == FLAG_CHAT_VIDEO) {
                        ArrayList<UserInfo> selectList = new ArrayList<>();
                        selectList.addAll(mSelectAdapter.getSelectList());
                        StringBuilder disName = new StringBuilder();
                        final ArrayList<String> userIdList = new ArrayList<>();
                        userIdList.add(Integer.toString(PrefsUtil.readUserInfo().ID));
                        disName.append(PrefsUtil.readUserInfo().Name).append("、");
                        for (int i = 0, count = selectList.size(); i < count; i++) {
                            UserInfo userInfo = selectList.get(i);
                            userIdList.add(Integer.toString(userInfo.ID));
                            disName.append(userInfo.Name);
                            if (i != count - 1) {
                                disName.append("、");
                            }
                        }
                        RongIMClient.getInstance().createDiscussion(disName.toString(), userIdList, new RongIMClient.CreateDiscussionCallback() {
                            @Override
                            public void onSuccess(String s) {
                                Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIAUDIO);
                                if (flag == FLAG_CHAT_VIDEO) {
                                    intent.setAction(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIVIDEO);
                                }
                                intent.putExtra("conversationType", Conversation.ConversationType.DISCUSSION.getName().toLowerCase());
                                intent.putExtra("targetId", s);
                                intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
                                intent.putStringArrayListExtra("invitedUsers", userIdList);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setPackage(getPackageName());
                                getApplicationContext().startActivity(intent);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                if (flag == FLAG_CHAT_VIDEO) {
                                    ToastUtil.showToast(getApplicationContext(), "创建视频聊天失败:" + errorCode.getValue());
                                } else {
                                    ToastUtil.showToast(getApplicationContext(), "创建语音聊天失败:" + errorCode.getValue());
                                }
                            }
                        });
                        finish();
                        return;
                    }
                    data.putExtra("position", mPosition);
                    ArrayList<UserInfo> userInfos = new ArrayList<>();
                    userInfos.addAll(mSelectAdapter.getSelectList());
                    data.putExtra(EXTRA_SELECT_LIST, userInfos);
                /*if (mSelectAdapter.getSelectList().size() == 0) {
					ToastUtil.showToast(this, R.string.contactselect_empty);
					return;
				}*/
                }
                setResult(Activity.RESULT_OK, data);
                onBackPressed();
                break;
        }
    }

    private void getPersonList(String keyword) {
        API.UserAPI.SearchUser(keyword, 0, false, new RequestCallback<UserInfo>(UserInfo.class, true) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(CallSelectContactActivity.this, R.string.contactselect_list_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                mDataList.addAll(respList);
                if (!hasOneself) {
                    for (UserInfo userInfo : mDataList) {
                        if (userInfo.ID == PrefsUtil.readUserInfo().ID) {
                            mDataList.remove(userInfo);
                            break;
                        }
                    }
                }
                mSelectAdapter.setDataList(mDataList);
                mSelectAdapter.notifyDataSetChanged();
                // 展开所有
                for (int i = 0, length = mSelectAdapter.getGroupCount(); i < length; i++) {
                    mListView.expandGroup(i);
                }
                EMWApplication.personSortList = mDataList;
            }
        });
    }


    class ContactSelectAdapter extends BaseExpandableListAdapter implements
            SectionIndexer, View.OnClickListener {
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
        private int itemID; //不可以操作的ID，例如添加群成员，不可把群主取消掉

        public ContactSelectAdapter(Context context) {
            this.context = context;
            this.mDataList = new ArrayList<>();
            mSelectList = new ArrayList<>();
            mSelectMap = new SparseBooleanArray();
            pinyinComparator = new PinyinComparator();
            // 实例化汉字转拼音类
            characterParser = CharacterParser.getInstance();
		/*
		 * sortData(); // 根据a-z进行排序源数据 Collections.sort(this.list,
		 * pinyinComparator);
		 */
            infos = new ArrayList<>();
            onlineIdList = new ArrayList<>();
            // makeDataSource(null);
            options = new DisplayImageOptions.Builder()
//                    .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                    .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                    .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
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

        public void setClickableItemID(int itemID) {
            this.itemID = itemID;
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
                    if (user == null)
                        continue;
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
                        if (su == null)
                            continue;
                        if (onlineId == su.ID)
                            info.array.add(su);
                    }
                }
                infos.add(info);
            }

            for (UserInfo su : mDataList) {
                if (su == null) {
                    continue;
                }
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
                    if (su == null)
                        continue;
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
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            CViewHolder vh;
            if (convertView == null) {
                vh = new CViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.rc_voip_listitem_select_contact_child, null);
                vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_contactselect_icon);
                vh.nameTv = (TextView) convertView.findViewById(R.id.tv_contactselect_name);
                vh.selectCb = (CheckBox) convertView.findViewById(R.id.cb_contactselect_select);
                vh.dividerView = convertView.findViewById(R.id.view_contactselect_divider);
                convertView.setTag(R.id.tag_first, vh);
            } else {
                vh = (CViewHolder) convertView.getTag(R.id.tag_first);
            }
            UserInfo user = infos.get(groupPosition).array.get(childPosition);
            String uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode, user.Image);
            vh.headIv.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 32);
            ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
            switch (mSelectType) {
                case ContactSelectActivity.RADIO_SELECT:
                    vh.selectCb.setButtonDrawable(R.drawable.cm_radio_select2);
                    if (mSelectUser != null && mSelectUser.ID == user.ID) {
                        vh.selectCb.setChecked(true);
                    } else {
                        vh.selectCb.setChecked(false);
                    }
                    break;
                case ContactSelectActivity.MULTI_SELECT:
                    vh.selectCb.setButtonDrawable(R.drawable.cm_radio_select2);
                    Boolean isSelect = mSelectMap.get(user.ID);
                    if (isSelect != null && isSelect) {
                        vh.selectCb.setChecked(true);
                    } else {
                        vh.selectCb.setChecked(false);
                    }
                    break;
            }

            vh.nameTv.setText(user.Name);
            vh.dividerView.setVisibility(isLastChild ? View.VISIBLE : View.GONE);
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
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            PViewHolder vh;
            if (convertView == null) {
                vh = new PViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.rc_voip_listitem_select_contact_group, null);
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
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

        @Override
        public int getPositionForSection(int section) {
            int i = 0;
            for (SortInfo si : infos) {
                if (si == null)
                    continue;
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
                if (si == null)
                    continue;
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
                    if (user.ID == itemID)
                        return;
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
            View dividerView;
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
                array = new ArrayList<>();
            }
        }
    }
}
