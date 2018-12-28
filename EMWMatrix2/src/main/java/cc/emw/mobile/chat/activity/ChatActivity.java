package cc.emw.mobile.chat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.farsunset.cim.client.android.CIMEventListener;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.soundcloud.android.crop.Crop;
import com.yzx.im_demo.AudioConverseActivity;
import com.yzx.im_demo.VideoConverseActivity;
import com.yzx.tools.NetWorkTools;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.zf.iosdialog.widget.ActionSheetDialog.SheetItemColor;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.activity.RecordButon.onAudioRecordFinished;
import cc.emw.mobile.chat.adapter.ChatMainAdapter;
import cc.emw.mobile.chat.bean.Audios;
import cc.emw.mobile.chat.bean.Files;
import cc.emw.mobile.chat.bean.SendMessage;
import cc.emw.mobile.chat.util.MediaPlayerManger;
import cc.emw.mobile.contact.GroupIntoActivity;
import cc.emw.mobile.contact.PersonActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.map.ToastUtil;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum.MessageType;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 聊天主页面
 *
 * @author xiang.peng
 */

@ContentView(R.layout.activity_chat)
public class ChatActivity extends BaseActivity implements OnClickListener,
        onAudioRecordFinished, CIMEventListener {
    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部打电话按钮
    @ViewInject(R.id.cm_header_btn_right1)
    private ImageButton mCallButn; // 顶部人员资料
    @ViewInject(R.id.chat_btn_send)
    private Button bt_send;
    @ViewInject(R.id.chat_et_content)
    private EditText mEditText;
    @ViewInject(R.id.chat_lv_message)
    private ListView mListview;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.record_hide)
    private Button record_hide;
    @ViewInject(R.id.audio_send)
    private LinearLayout audio_send;
    @ViewInject(R.id.normal_send)
    private LinearLayout normal_send;
    @ViewInject(R.id.btn_record)
    private RecordButon but_record;
    @ViewInject(R.id.audio_time_change)
    private TextView timechange;
    public static final String ACTION_REFRESH_HOME_LIST = "cc.emw.mobile.refresh_chat_list";
    private String send_msg, name;
    private ArrayList<Message> dataList;
    private Dialog mProgress;
    private ChatMainAdapter adapater;
    private int SenderID, pageIndex, pageSize, type, GroupID;// 发送者ID,消息页数,消息类型,群组id
    private int normal_mes = 4;// 4 为普通消息 7为图片消息 6为附件消息 8为音频信息,10为分享 信息
    private int audio_mes = 8;
    private int image_mes = 7;
    private GroupInfo info;
    private cc.emw.mobile.entity.UserInfo user;
    private RightMenu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getIntentDatas();
        CIMListenerManager.registerMessageListener(this, this);
    }

    @Override
    public void onBackPressed() {
        if (audio_send.getVisibility() == View.VISIBLE && normal_send.getVisibility() == View.GONE) {
            audio_send.setVisibility(View.GONE);
            normal_send.setVisibility(View.VISIBLE);
            but_record.clearAnimation();
            but_record.setVisibility(View.GONE);
            record_hide.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void init() {

        mMenu = new RightMenu(this);
        mMenu.addItem(getString(R.string.audioChat), 1);
        mMenu.addItem(getString(R.string.videoChat), 2);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenu.Item item, int position) {
                String call_head = user.Image;
                String phone = user.VoipCode;
                switch (item.id) {
                    case 1:
                        Intent intentVoice = new Intent(ChatActivity.this, AudioConverseActivity.class);
                        intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intentVoice.putExtra("userName", name);
                        intentVoice.putExtra("userId", phone);
                        intentVoice.putExtra("call_phone", phone);
                        intentVoice.putExtra("call_type", 1);//免费电话
                        intentVoice.putExtra("call_head", call_head);
                        startActivity(intentVoice);
                        break;
                    case 2:
                        Intent intentVideo = new Intent(ChatActivity.this, VideoConverseActivity.class);
                        intentVideo.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intentVideo.putExtra("userName", name);
                        intentVideo.putExtra("userId", phone);
                        intentVideo.putExtra("call_phone", phone);
                        intentVideo.putExtra("call_position", "");
                        startActivity(intentVideo);
                        break;
                }
            }
        });

        FrameLayout layout = (FrameLayout) findViewById(R.id.chat_layout);
        hideKeyBoade(layout);
        but_record.setOnAudioRecordFinished(this);
        mHeaderBackBtn.setOnClickListener(this);
        mProgress = createLoadingDialog(R.string.loading);
        pageIndex = 0;
        pageSize = 5;
        dataList = new ArrayList<>();
        mHeaderMoreBtn.setOnClickListener(this);
        mCallButn.setOnClickListener(this);
        findViewById(R.id.chat_btn_add).setOnClickListener(this);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    bt_send.setText(R.string.send);
                    bt_send.setBackgroundResource(R.drawable.cm_button_bg);
                } else {
                    bt_send.setText("");
                    bt_send.setBackgroundResource(R.drawable.chatb_btn_microphone_h);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        bt_send.setOnClickListener(this);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListview, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageIndex++;
                firstReciecMes(type);
            }
        });
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);

    }


    private void getIntentDatas() {
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 1);
        GroupID = intent.getIntExtra("GroupID", 0);
        SenderID = intent.getIntExtra("SenderID", -1);
        switch (type) {
            case 1:
                if (EMWApplication.personMap.get(SenderID) != null) {
                    user = EMWApplication.personMap.get(SenderID);
                    if (intent.hasExtra("name")) {
                        name = intent.getStringExtra("name");
                    } else {
                        name = user.Name;
                    }
                }
                break;
            case 2:
                if (EMWApplication.groupMap.get(GroupID) != null) {
                    mCallButn.setVisibility(View.INVISIBLE);
                    info = EMWApplication.groupMap.get(GroupID);
                    if (intent.hasExtra("name")) {
                        name = intent.getStringExtra("name");
                    } else {
                        name = info.Name;
                    }
                }
                break;
        }
        mHeaderTitleTv.setText(name);

        if (SenderID != -1) {
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrameLayout.autoRefresh(false);
                }
            }, 100);
        }
        // 分享 地图 路径
        if (intent.hasExtra("url_mes")) {
            send_msg = getIntent().getStringExtra("url_mes");
            sendMessage(type, normal_mes, false);
        }
        // 分享动态消息
        if (intent.hasExtra("share")) {
            send_msg = getIntent().getStringExtra("share");
            int share_mes = 10;
            sendMessage(type, share_mes, false);
        }
        // 转发图片信息
        if (intent.hasExtra("url_image")) {
            send_msg = getIntent().getStringExtra("url_image");
            sendMessage(type, image_mes, false);
        }
        mListview.setAdapter(adapater = new ChatMainAdapter(ChatActivity.this,
                dataList));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:// 回退
                onBackPressed();
                break;
            case R.id.chat_btn_send:// 发送消息
                send_msg = mEditText.getText().toString();
                if (!send_msg.equals("")) {
                    sendMessage(type, normal_mes, true);
                } else {
                    Animation scale = AnimationUtils.loadAnimation(
                            ChatActivity.this, R.anim.audios_bg);
                    record_hide.setVisibility(View.VISIBLE);
                    but_record.setVisibility(View.VISIBLE);
                    audio_send.setVisibility(View.VISIBLE);
                    but_record.startAnimation(scale);// 开启按钮动画
                    normal_send.setVisibility(View.GONE);
                }
                break;
            case R.id.cm_header_btn_right:// 人员资料
                Intent intent1;
                if (type == 1) {
                    intent1 = new Intent(this, PersonActivity.class);
                    intent1.putExtra("simple_user", user);
                    intent1.putExtra("IsFromChat", true);
                    startActivity(intent1);
                } else if (type == 2) {
                    intent1 = new Intent(this, GroupIntoActivity.class);
                    intent1.putExtra("group_info", info);
                    intent1.putExtra("IsFromChat", true);
                    startActivity(intent1);
                }
                break;
            // 语音电话
            case R.id.cm_header_btn_right1:
                mMenu.showAsDropDown(v);
//                if (!checkNetwork(false)) {
//                    return;
//                }
//                String call_head = user.Image;
//                String phone = user.VoipCode;
//                Intent intent = new Intent(ChatActivity.this,
//                        AudioConverseActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                intent.putExtra("call_head", call_head);
//                intent.putExtra("call_name", name);
//                intent.putExtra("call_client", phone);
//                intent.putExtra("call_type", 1);
//                startActivity(intent);
                break;
            case R.id.chat_btn_add:
                ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
                dialog.addSheetItem(getString(R.string.Photo), SheetItemColor.Blue,
                        new OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                // 开启相机
                                Intent intent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                        .fromFile(new File(EMWApplication.tempPath
                                                + "tempraw.png")));
                                startActivityForResult(intent, 1);

                            }
                        });
                dialog.addSheetItem(getString(R.string.photos),
                        SheetItemColor.Blue, new OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Crop.pickImage(ChatActivity.this);
                            }
                        });
                dialog.addSheetItem(getString(R.string.attache),
                        SheetItemColor.Blue, new OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent fileIntent = new Intent(ChatActivity.this,
                                        FileSelectActivity.class);
                                startActivityForResult(fileIntent, 21);
                            }
                        });
                dialog.show();
                break;
        }
    }

    //  向服务器发送消息""
    private void sendMessage(int type, int types_mes, final boolean add) {
        final Dialog mDialog = createLoadingDialog(R.string.sending);
        ApiEntity.Message message = new SendMessage(0, send_msg,
                PrefsUtil.readUserInfo().ID, PrefsUtil.readUserInfo().ID,
                SenderID, types_mes, PrefsUtil.readUserInfo().CompanyCode,
                null, GroupID);
        final String temp = new Gson().toJson(message);

        switch (type) {
            case 1:// 单人消息
                ApiEntity.Message messages = new SendMessage(0, send_msg,
                        PrefsUtil.readUserInfo().ID, PrefsUtil.readUserInfo().ID,
                        SenderID, types_mes, PrefsUtil.readUserInfo().CompanyCode,
                        null, 0);
                API.Message.Send(messages, true, new RequestCallback<String>(
                        String.class) {
                    @Override
                    public void onStarted() {
                        super.onStarted();
                        if (mDialog != null) {
                            mDialog.show();
                        }
                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Message msg = new Gson().fromJson(temp, Message.class);
                        if (add) {
                            dataList.add(msg);
                            adapater.add(dataList);
                            mEditText.setText("");
                            mListview.setSelection(dataList.size() - 1);
                        }
                    }
                });

                break;
            case 2:// 群组消息
                API.Message.SendGroup(message, new RequestCallback<String>(
                        String.class) {
                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Message msg = new Gson().fromJson(temp, Message.class);
                        if (add) {
                            dataList.add(msg);
                            adapater.add(dataList);
                            mEditText.setText("");
                            mListview.setSelection(dataList.size() - 1);
                        }
                    }
                });
                break;
        }
    }


    @Override
    public void onDestroy() {
        MediaPlayerManger.getInstance().pause();
        super.onDestroy();
    }

    // 从服务器收到消息
    @Override
    public void onMessageReceived(String message) {
        if (!"".equals(message)) {  // 如果是当前页面的聊天直接展示
            Log.d("px","me="+message);
            Message msg = new Gson().fromJson(message, Message.class);
            if (!"".equals(msg.getContent())
                    && msg.getType() == MessageType.Message) {// 还要修改
                if (msg.getSenderID() == SenderID) {
                    dataList.add(msg);
                    adapater.add(dataList);
                    mListview.setSelection(dataList.size() - 1);
                }
            }
        }
    }


    @Override
    public void onReplyReceived(String replybody) {

    }

    @Override
    public void onNetworkChanged(NetworkInfo networkinfo) {

    }

    @Override
    public void onConnectionStatus(boolean isConnected) {

    }

    @Override
    public void onCIMConnectionSucceed() {

    }

    @Override
    public void onCIMConnectionClosed() {

    }

    // 根据类型获取消息记录
    private void firstReciecMes(int type) {
        switch (type) {
            case 1:// 获取普通消息
                API.Message.GetChatMessages(SenderID, pageIndex, pageSize,
                        new RequestCallback<Message>(Message.class) {
                            @Override
                            public void onStarted() {
                                super.onStarted();
                            }

                            @Override
                            public void onError(Throwable arg0, boolean arg1) {
                                if (mPtrFrameLayout != null) {
                                    mPtrFrameLayout.refreshComplete();
                                    pageIndex--;
                                }
                                if (mProgress != null && mProgress.isShowing()) {
                                    mProgress.dismiss();
                                }
                            }

                            @Override
                            public void onParseSuccess(List<Message> respList) {
                                if (mPtrFrameLayout != null) {
                                    mPtrFrameLayout.refreshComplete();
                                }
                                if (mProgress != null && mProgress.isShowing()) {
                                    mProgress.dismiss();
                                }
                                if (respList.size() != 0) {
                                    Collections.reverse(respList);
                                    dataList.addAll(0, respList);
                                    adapater.add(dataList);
                                }
                            }
                        });
                break;
            case 2:// 获取群组消息
                API.Message.GetGroupMessages(GroupID + "", pageIndex, pageSize,
                        new RequestCallback<Message>(Message.class) {
                            @Override
                            public void onStarted() {
                                super.onStarted();
                            }

                            @Override
                            public void onError(Throwable arg0, boolean arg1) {
                                if (mPtrFrameLayout != null) {
                                    mPtrFrameLayout.refreshComplete();
                                    pageIndex--;
                                }
                                if (mProgress != null && mProgress.isShowing()) {
                                    mProgress.dismiss();
                                }
                            }

                            @Override
                            public void onParseSuccess(List<Message> respList) {
                                if (mPtrFrameLayout != null) {
                                    mPtrFrameLayout.refreshComplete();
                                }
                                if (mProgress != null && mProgress.isShowing()) {
                                    mProgress.dismiss();
                                }
                                if (respList.size() != 0) {
                                    Collections.reverse(respList);
                                    dataList.addAll(0, respList);
                                    adapater.add(dataList);
                                }
                            }
                        });

                break;
        }

    }

    // 检测网络是否可用
    private boolean checkNetwork(boolean edgeCanDial) {
        int netstate = NetWorkTools.getCurrentNetWorkType(ChatActivity.this);

        if (netstate == NetWorkTools.NETWORK_ON) {
            Toast.makeText(ChatActivity.this, R.string.network_bad,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (netstate == NetWorkTools.NETWORK_EDGE && !edgeCanDial) {
            Toast.makeText(this, R.string.network_bad, Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (edgeCanDial) {
            TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int simState = mTelephonyManager.getSimState();
            if (TelephonyManager.SIM_STATE_READY != simState) {
                Toast.makeText(this, R.string.system_error, Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri inputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempraw.png"));
            Uri outputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempcrop.png"));
            Crop.of(inputUri, outputUri).asSquare().start(this);
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData()); // 开始裁剪
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data); // 裁剪完成
        }
        if (requestCode == 21 && resultCode == RESULT_OK) {

            ArrayList<ApiEntity.Files> fileList = (ArrayList<ApiEntity.Files>) data
                    .getSerializableExtra("select_list");
            for (ApiEntity.Files userNoteFile : fileList) {
                Files files = new Files();
                files.setLength(userNoteFile.Length);
                files.setName(userNoteFile.Name);
                files.setUrl(userNoteFile.Url);
                files.setID(userNoteFile.ID);

                send_msg = new Gson().toJson(files);
                int attach_mes = 6;
                sendMessage(type, attach_mes, true);
            }
        }
    }

    // 裁剪图片
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "zkbr.png"));
        Crop.of(source, destination).asSquare().start(this);
    }

    // 处理图片
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            uploadImage(uri.getPath());
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 上传图片
    private void uploadImage(String path) {
        final Dialog mDialog = createLoadingDialog(R.string.uploading);
        RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.addBodyParameter(file.getName(), file);
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                super.onStarted();
                if (mDialog != null) {
                    mDialog.show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                ToastUtil.show(ChatActivity.this,
                        getString(R.string.network_bad));
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                ToastUtil.show(ChatActivity.this,
                        getString(R.string.network_bad));
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onParseSuccess(List<String> result) {
                // 获取服务器返回的图片的url发送消息
                if (!"".equals(result.get(0))) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    send_msg = result.get(0);
                    sendMessage(type, image_mes, true);
                }
            }
        });
    }

    // 上传音频
    private void uploadAudios(String path, final long time) {
        final Dialog mDialog = createLoadingDialog(R.string.uploading);
        RequestParam params = new RequestParam(Const.UPLOAD_AUDIO_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.setMultipart(true);
        params.addBodyParameter(file.getName(), file);
        x.http().post(params, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                super.onStarted();
                if (mDialog != null) {
                    mDialog.show();
                }
            }

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                ToastUtil.show(ChatActivity.this,
                        getString(R.string.network_bad));
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String arg0) {

                Audios audios = new Audios();
                audios.setUrl(new Gson().fromJson(arg0, String.class));
                audios.setLength(time + "");
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                if (!"".equals(arg0)) {
                    send_msg = new Gson().toJson(audios);
                    sendMessage(type, audio_mes, true);
                }
            }
        });
    }

    // 录音完成的回调
    @Override
    public void audioRecordFinished(String path, long time) {

        record_hide.setVisibility(View.GONE);
        but_record.clearAnimation();
        but_record.setVisibility(View.GONE);
        audio_send.setVisibility(View.GONE);
        normal_send.setVisibility(View.VISIBLE);
        if (path != null) {
            uploadAudios(path, time);
        }
    }

    // 设置录音时间的变化
    @SuppressLint("SimpleDateFormat")
    @Override
    public void audioTimeChange(long time) {
        try {
            Format f0 = new SimpleDateFormat("ss");
            SimpleDateFormat f1 = new SimpleDateFormat("mm:ss");
            Date d = (Date) f0.parseObject(time + "");
            timechange.setText(f1.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /*@SuppressWarnings("unused")
    private void conferenceConverse(ArrayList<UserInfo> selectList) {
        ArrayList<ConferenceMemberInfo> list = new ArrayList<ConferenceMemberInfo>();
        int conferencenum = 0;// 会议最多支持6人，选中超过5人显示满员
        for (int i = 0; i < selectList.size(); i++) {
            conferencenum++;
            if (conferencenum > 5) {
                Toast.makeText(this, R.string.over, Toast.LENGTH_SHORT).show();
                return;
            }
            ConferenceMemberInfo conference = new ConferenceMemberInfo();
            // 因为默认测试账户为6个，除掉本身是5个，所以大于5的是设置的手机号码
            if (i < 6) {
                conference.setUid(selectList.get(i).VoipCode);
                conference.setPhone(selectList.get(i).Phone); // 如果手机号码冲突会呼叫失败
            } else {
                conference.setUid("");
                conference.setPhone(selectList.get(i).Phone);
            }
            list.add(conference);
        }

        Intent intent = new Intent(ChatActivity.this,
                ConferenceConverseActivity.class);
        intent.putExtra("conference", list);
        intent.putExtra("type", "1");// 1:免费通话 2:直拨 3：智能
        intent.putExtra("select_list", selectList);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (list.size() > 0) {
            startActivity(intent);
        }
    }*/

    // 隐藏键盘 更改底部输入布局
    public void hideKeyBoade(View view) {
        if (!(view instanceof EditText) && !(view instanceof Button)) {
            view.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    InputMethodManager inputMethodManager = (InputMethodManager) ChatActivity.this
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (ChatActivity.this.getCurrentFocus() != null) {
                        if (true) {
                            inputMethodManager.hideSoftInputFromWindow(
                                    ChatActivity.this.getCurrentFocus()
                                            .getWindowToken(), 0);
                        }

                    }
                    if (audio_send.getVisibility() == View.VISIBLE
                            && normal_send.getVisibility() == View.GONE
                            && but_record.getVisibility() == View.VISIBLE) {
                        audio_send.setVisibility(View.GONE);
                        normal_send.setVisibility(View.VISIBLE);
                        but_record.clearAnimation();
                        but_record.setVisibility(View.GONE);
                        record_hide.setVisibility(View.GONE);

                    }
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyBoade(innerView);
            }
        }
    }
}