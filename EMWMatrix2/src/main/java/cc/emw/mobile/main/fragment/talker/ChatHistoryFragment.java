package cc.emw.mobile.main.fragment.talker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.chat.activity.ChatActivity;
import cc.emw.mobile.chat.bean.HistoryMessage;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 会话列表
 *
 * @author xiang.peng
 */
@ContentView(R.layout.fragment_chathistory)
public class ChatHistoryFragment extends BaseFragment {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderMenuBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条右菜单按钮

    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;
    @ViewInject(R.id.smlv_chat_history)
    private SwipeMenuListView mListView;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;

    private SimpleDateFormat format;
    private ArrayList<HistoryMessage> messge = new ArrayList<>();
    private DisplayImageOptions options;
    private Intent intent;
    private String headerTitle;
    private LayoutInflater inflater;
    private final int SINGLE = 1;
    private final int GROUP = 2;
//    private static int size;

    public static ChatHistoryFragment newInstance(String title) {
        ChatHistoryFragment fragment = new ChatHistoryFragment();
        Bundle args = new Bundle();
        args.putString("header_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headerTitle = getArguments().getString("header_title");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater = LayoutInflater.from(getActivity());
        initView();
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                getActivity().sendBroadcast(
                        new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;

            case R.id.cm_header_btn_right:
                Intent intent = new Intent(getActivity(),
                        ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE,
                        ContactSelectActivity.RADIO_SELECT);
                intent.putExtra("isSend", true);
                startActivity(intent);
                break;
        }
    }

    @Event(R.id.ll_network_tips)
    private void onNetworkTipsClick(View v) {
        mPtrFrameLayout.autoRefresh(false);
    }

    @SuppressLint("SimpleDateFormat")
    private void initView() {

        mHeaderMenuBtn.setImageResource(R.drawable.nav_btn_menu);
        mHeaderMenuBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(headerTitle);
        mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_share);
        mHeaderMoreBtn.setVisibility(View.VISIBLE);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(DisplayUtil.dip2px(getActivity(), 65));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);

            }
        };
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                // 删除聊天纪录
                int type = messge.get(position).getType();
                if (SINGLE == type) {
                    // 59 根据ReceiverID删除 61根据sendID删除
                    int item = messge.get(position).getMessage().getReceiverID();
                    delete(item, 0);

                } else if (GROUP == type) {
                    int item = messge.get(position).getMessage().GroupID;
                    delete(item, 0);
                }
                return false;
            }

        });
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (Integer.valueOf(String.valueOf(id)) != 0) {
                    intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("SenderID",
                            Integer.valueOf(String.valueOf(id)));
                    intent.putExtra("type", messge.get(position).getType());
                    intent.putExtra("GroupID", messge.get(position)
                            .getMessage().getGroupID());
                    startActivity(intent);
                }
            }
        });

        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.disableWhenHorizontalMove(true);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getChatList();
            }
        });

        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1000);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);

        format = new SimpleDateFormat("MM-dd HH:mm");

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head)
                .showImageForEmptyUri(R.drawable.cm_img_head)
                .showImageOnFail(R.drawable.cm_img_head).cacheInMemory(true)
                .cacheOnDisk(true).build();
    }

    @Override
    public void onFirstUserVisible() {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrameLayout.autoRefresh(false);
                }
            }, 500);
        }
    }

    @Override
    public void onUserVisible() {
        getChatList();
    }

    class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return messge.size();
        }

        @Override
        public Object getItem(int arg0) {
            return messge.get(arg0).getMessage();
        }

        @Override
        public long getItemId(int arg0) {
            return messge.get(arg0).getReceiverID();
        }


        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            HistoryMessage msg = messge.get(arg0);
            ViewHolder vh;
            if (null == convertView) {
                vh = new ViewHolder();
                convertView = inflater.inflate(R.layout.listitem_chat, null);
                vh.content = (TextView) convertView.findViewById(R.id.content);
                vh.head = (CircleImageView) convertView.findViewById(R.id.head);
                vh.time = (TextView) convertView.findViewById(R.id.time);
                vh.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(R.id.tag_first, vh);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag(R.id.tag_first);
            }
            switch (msg.getMessage().getType()) {

                case ApiEnum.MessageType.Audio:
                    vh.content.setText(R.string.audio);
                    break;
                case ApiEnum.MessageType.Image:
                    vh.content.setText(R.string.photoes);
                    break;
                case ApiEnum.MessageType.Attach:
                    vh.content.setText(R.string.doc);
                    break;
                case ApiEnum.MessageType.Share:
                    vh.content.setText(R.string.task_attachment_share);
                    break;
                case ApiEnum.MessageType.Task:
                    vh.content.setText(R.string.task);
                    break;
                case ApiEnum.MessageType.Flow:
                    vh.content.setText(R.string.follow_mes);
                default:
                    if (msg.getMessage().getContent().contains("FlowID")) {
                        vh.content.setText(R.string.follow_mes);
                    } else if (msg.getMessage().getContent().contains("m.amap")) {
                        vh.content.setText(R.string.road_way);
                    } else {
                        vh.content.setText(msg.getMessage().getContent());
                    }
                    break;
            }
            String name = "";
            String image = "";
            String uri = "";
            // 群组消息
            if (messge.get(arg0).getType() == 2) {
                if (EMWApplication.groupMap.size() > 0) {
                    GroupInfo info = EMWApplication.groupMap.get(msg
                            .getMessage().getGroupID());
                    if (info != null) {
                        image = info.Image;
                        name = info.Name;
                    } else {
                        delete(msg.getMessage().getGroupID(), 1);
                    }
                }
            } else {
                if (EMWApplication.personMap.size() > 0) {
                    if (EMWApplication.personMap.get(msg.getReceiverID()) != null) {
                        UserInfo user = EMWApplication.personMap.get(messge.get(
                                arg0).getReceiverID());
                        if (user != null) {
                            name = user.Name;
                            image = user.Image;
                        }
                    }
                }
            }
            vh.name.setText(name);
            if (msg.getMessage().GroupID != 0) {
                if (EMWApplication.groupMap.get(msg.getMessage().GroupID) != null) {
                    uri = Const.BASE_URL + EMWApplication.groupMap.get(msg.getMessage().GroupID).Image;
                }
            } else {
                uri = String.format(Const.DOWN_ICON_URL,
                        PrefsUtil.readUserInfo().CompanyCode, image);
            }
            ImageLoader.getInstance().displayImage(uri, vh.head, options);
            vh.time.setText(format.format(HelpUtil.string2Time(msg
                    .getMessage().getCreateTime())));
            return convertView;
        }
    }

    static class ViewHolder {
        TextView time;
        CircleImageView head;
        TextView name;
        TextView content;
    }

    /**
     * 获取聊天记录列表 HttpConstant.CHATHISTTROY_UR
     */

    private void getChatList() {

        API.Message.GetChatRecords(new RequestCallback<HistoryMessage>(
                HistoryMessage.class) {
            @Override
            public void onError(Throwable arg0, boolean arg1) {
                mPtrFrameLayout.refreshComplete();
                if (arg0 instanceof ConnectException) {
                    mNetworkTipsLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onParseSuccess(List<HistoryMessage> respList) {
                mNetworkTipsLayout.setVisibility(View.GONE);
                mPtrFrameLayout.refreshComplete();
//                size = messge.size();
                messge.clear();
                if (respList.size() > 0) {
                    for (HistoryMessage historyMessage : respList) {
                        if (historyMessage.getMessage() != null) {
                            messge.add(historyMessage);
                        }
                    }
//                    if (size != messge.size()) {
                        if (messge.size() > -1) {
                            mListView.setAdapter(new ChatAdapter());
//                        }
                    }
                }
            }
        });
    }

    /**
     * 删除聊天记录
     */
    private void delete(int sendID, final int type) {

        API.Message.RemoveChatRecord(sendID, new RequestCallback<String>(
                String.class) {

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                ToastUtil.showToast(getContext(), "删除失败！");
            }

            @Override
            public void onSuccess(String result) {

                if ("true".equals(result)) {
                    getChatList();
                    if (type == 0) {
                        ToastUtil.showToast(getContext(), R.string.delete_success,
                                R.drawable.tishi_ico_gougou);
                    }
                }
            }
        });
    }

}