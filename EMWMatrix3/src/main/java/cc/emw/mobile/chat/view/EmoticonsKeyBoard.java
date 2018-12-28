package cc.emw.mobile.chat.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.VideoCaptureActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ListDialog;
import sj.keyboard.adpater.PageSetAdapter;
import sj.keyboard.data.PageSetEntity;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.EmoticonsFuncView;
import sj.keyboard.widget.EmoticonsIndicatorView;
import sj.keyboard.widget.FuncLayout;

public class EmoticonsKeyBoard extends AutoHeightLayout implements EmoticonsFuncView.OnEmoticonsPageViewListener,
        EmoticonsToolBarView.OnToolBarItemClickListener, EmoticonsEditText.OnBackKeyClickListener, FuncLayout.OnFuncChangeListener, View.OnClickListener {

    public final int APPS_HEIGHT = 240;

    public static final int FUNC_TYPE_PTT = 1;
    public static final int FUNC_TYPE_PTV = 2;
    public static final int FUNC_TYPE_EMOTICON = 6;
    public static final int FUNC_TYPE_PLUG = 7;

    protected LayoutInflater mInflater;

    protected EmoticonsFuncView mEmoticonsFuncView;
    protected EmoticonsIndicatorView mEmoticonsIndicatorView;
    protected EmoticonsToolBarView mEmoticonsToolBarView;

    protected boolean mDispatchKeyEventPreImeLock = false;
    private EmoticonsEditText etChat;
    private TextView btnSend;
    private TextView btnVoice;
    private TextView btnPtv;
    private TextView btnImage;
    private TextView btnCamera;
    //    private TextView btnHongbao;
    private TextView btnEmoticon;
    private TextView btnshiping;
    private FuncLayout lyKvml;
    private Context mContext;
    private cc.emw.mobile.entity.UserInfo user;
    private String name;
    private ListDialog mAddDialog;

    public EmoticonsKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.view_keyboard_qq, this);
        initView(view);
        initFuncView();
    }

    protected void initView(View view) {
        lyKvml = (FuncLayout) view.findViewById(R.id.ly_kvml);

        btnEmoticon = (TextView) view.findViewById(R.id.btn_emoticon);
//        btnHongbao = (TextView) view.findViewById(R.id.btn_hongbao);
        btnCamera = (TextView) view.findViewById(R.id.btn_camera);
        btnImage = (TextView) view.findViewById(R.id.btn_image);
        btnPtv = (TextView) view.findViewById(R.id.btn_ptv);
        btnVoice = (TextView) view.findViewById(R.id.btn_voice);
        btnSend = (TextView) view.findViewById(R.id.btn_send);
        btnshiping = (TextView) view.findViewById(R.id.btn_shiping);
        etChat = (EmoticonsEditText) view.findViewById(R.id.et_chat);
        lyKvml.setOnClickListener(this);
        btnEmoticon.setOnClickListener(this);
//        btnHongbao.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnPtv.setOnClickListener(this);
        btnVoice.setOnClickListener(this);
        btnshiping.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        etChat.setOnBackKeyClickListener(this);
    }

    protected void initFuncView() {
        initEmoticonFuncView();
        initEditView();
    }

    protected void initEmoticonFuncView() {
        View keyboardView = inflateFunc();
        lyKvml.addFuncView(FUNC_TYPE_EMOTICON, keyboardView);
        mEmoticonsFuncView = ((EmoticonsFuncView) findViewById(com.keyboard.view.R.id.view_epv));
        mEmoticonsIndicatorView = ((EmoticonsIndicatorView) findViewById(com.keyboard.view.R.id.view_eiv));
        mEmoticonsToolBarView = ((EmoticonsToolBarView) findViewById(com.keyboard.view.R.id.view_etv));
        mEmoticonsFuncView.setOnIndicatorListener(this);
        mEmoticonsToolBarView.setOnToolBarItemClickListener(this);
        lyKvml.setOnFuncChangeListener(this);
        mAddDialog = new ListDialog(mContext, false);
        mAddDialog.addItem("语音通话", ApiEnum.UserNoteAddTypes.Notice);
        mAddDialog.addItem("直拨电话", ApiEnum.UserNoteAddTypes.Share);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case ApiEnum.UserNoteAddTypes.Share://直播电话
                        if (user != null && user.Phone != null) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.Phone));
                            mContext.startActivity(intent);
                        } else {
                            ToastUtil.showToast(mContext, "未能获取到对方手机号码");
                        }
                        break;
                    case ApiEnum.UserNoteAddTypes.Notice://语音电话
                        /*if (TextUtils.isEmpty(PrefsUtil.readUserInfo().VoipCode)) {
                            ToastUtil.showToast(mContext, "你暂未开通语音通话服务，请联系管理员申请开通。");
                            break;
                        }
                        if (user != null && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(user.VoipCode)) {
                            Intent intentVoice = new Intent(mContext, AudioConverseActivity.class);
                            intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intentVoice.putExtra("userName", name);
                            intentVoice.putExtra("userId", user.VoipCode);
                            intentVoice.putExtra("call_phone", user.VoipCode);
                            intentVoice.putExtra("call_type", 1);//免费电话
                            intentVoice.putExtra("call_head", user.Image);
                            (mContext).startActivity(intentVoice);
                        } else {
                            ToastUtil.showToast(mContext, "对方暂未开通语音通话服务，请联系管理员申请开通。");
                        }*/
                        HelpUtil.startVoice(mContext, user);
                        break;

                }
            }
        });
    }

    protected View inflateFunc() {
        return mInflater.inflate(R.layout.view_func_emoticon_qq, null);
    }

    protected void initEditView() {
        etChat.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!etChat.isFocused()) {
                    etChat.setFocusable(true);
                    etChat.setFocusableInTouchMode(true);
                }
                return false;
            }
        });

        etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setAdapter(PageSetAdapter pageSetAdapter) {
        if (pageSetAdapter != null) {
            ArrayList<PageSetEntity> pageSetEntities = pageSetAdapter.getPageSetEntityList();
            if (pageSetEntities != null) {
                for (PageSetEntity pageSetEntity : pageSetEntities) {
                    mEmoticonsToolBarView.addToolItemView(pageSetEntity);
                }
            }
        }
        mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

    public void addFuncView(int type, View view) {
        lyKvml.addFuncView(type, view);
    }

    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(getContext());
        lyKvml.hideAllFuncView();
        resetIcon();
    }

    public void resetIcon() {
//        btnVoice.setImageResource(R.drawable.qq_skin_aio_panel_ptt);
//        btnPtv.setImageResource(R.drawable.qq_skin_aio_panel_ptv);
//        btnImage.setImageResource(R.drawable.qq_skin_aio_panel_image);
//        btnCamera.setImageResource(R.drawable.qq_skin_aio_panel_camera);
//        btnHongbao.setImageResource(R.drawable.qq_skin_aio_panel_hongbao);
//        btnEmoticon.setImageResource(R.drawable.qq_skin_aio_panel_emotion);
    }

    protected void toggleFuncView(int key) {
        lyKvml.toggleFuncView(key, isSoftKeyboardPop(), etChat);
    }

    @Override
    public void onFuncChange(int key) {
        resetIcon();
        switch (key) {
//            case FUNC_TYPE_PTT:
//                btnVoice.setImageResource(R.mipmap.qq_skin_aio_panel_ptt_press);
//                break;
//            case FUNC_TYPE_PTV:
//                btnPtv.setImageResource(R.mipmap.qq_skin_aio_panel_ptv_press);
//                break;
//            case FUNC_TYPE_IMAGE:
//                btnImage.setImageResource(R.mipmap.qq_skin_aio_panel_image_press);
//                break;
//            case FUNC_TYPE_CAMERA:
//                btnCamera.setImageResource(R.mipmap.qq_skin_aio_panel_camera_press);
//                break;
//            case FUNC_TYPE_HONGBAO:
//                btnHongbao.setImageResource(R.mipmap.qq_skin_aio_panel_hongbao_press);
//                break;
//            case FUNC_TYPE_EMOTICON:
//                btnEmoticon.setImageResource(R.mipmap.qq_skin_aio_panel_emotion_press);
//                break;
        }
    }

    protected void setFuncViewHeight(int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lyKvml.getLayoutParams();
        params.height = height;
        lyKvml.setLayoutParams(params);
        super.OnSoftPop(height);
    }

    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        lyKvml.updateHeight(height);
    }

    @Override
    public void OnSoftPop(int height) {
        super.OnSoftPop(height);
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            lyKvml.setVisibility(true);
//        } else {
//            lyKvml.setVisibility(false);
//        }
        onFuncChange(lyKvml.DEF_KEY);
    }

    @Override
    public void OnSoftClose() {
        super.OnSoftClose();
        if (lyKvml.isOnlyShowSoftKeyboard()) {
            reset();
        } else {
            onFuncChange(lyKvml.getCurrentFuncKey());
        }
    }

    public void addOnFuncKeyBoardListener(FuncLayout.OnFuncKeyBoardListener l) {
        lyKvml.addOnKeyBoardListener(l);
    }

    @Override
    public void emoticonSetChanged(PageSetEntity pageSetEntity) {
        mEmoticonsToolBarView.setToolBtnSelect(pageSetEntity.getUuid());
    }

    @Override
    public void playTo(int position, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    @Override
    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }

    @Override
    public void onToolBarItemClick(PageSetEntity pageSetEntity) {
        mEmoticonsFuncView.setCurrentPageSet(pageSetEntity);
    }

    @Override
    public void onBackKeyClick() {
        if (lyKvml.isShown()) {
            mDispatchKeyEventPreImeLock = true;
            reset();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (mDispatchKeyEventPreImeLock) {
                    mDispatchKeyEventPreImeLock = false;
                    return true;
                }
                if (lyKvml.isShown()) {
                    reset();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    public EmoticonsEditText getEtChat() {
        return etChat;
    }

    public TextView getButtonVideo() {
        return btnshiping;
    }

    public EmoticonsFuncView getEmoticonsFuncView() {
        return mEmoticonsFuncView;
    }

    public EmoticonsIndicatorView getEmoticonsIndicatorView() {
        return mEmoticonsIndicatorView;
    }

    public EmoticonsToolBarView getEmoticonsToolBarView() {
        return mEmoticonsToolBarView;
    }


    public TextView getBtnSend() {
        return btnSend;
    }

    public void setID(cc.emw.mobile.entity.UserInfo user, String name) {
        this.user = user;
        this.name = name;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                toggleFuncView(FUNC_TYPE_PTT);
                setFuncViewHeight(EmoticonsKeyboardUtils.dip2px(getContext(), APPS_HEIGHT));
                break;
            case R.id.btn_ptv:
                Intent fileIntent = new Intent(mContext, FileSelectActivity.class);
                fileIntent.putExtra("start_anim", false);
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                fileIntent.putExtra("click_pos_y", location[1]);
                ((Activity) mContext).startActivityForResult(fileIntent, 21);
                break;
            case R.id.btn_image:
                Crop.pickImage((Activity) mContext);
                break;
            case R.id.btn_camera:
                Intent intent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                        .fromFile(new File(EMWApplication.tempPath
                                + "tempraw.png")));
                ((Activity) mContext).startActivityForResult(intent, 1);
                break;

            case R.id.btn_emoticon:
                toggleFuncView(FUNC_TYPE_EMOTICON);
                setFuncViewHeight(EmoticonsKeyboardUtils.dip2px(getContext(), APPS_HEIGHT));
                break;
            case R.id.btn_shiping:
                Intent videoIntent = new Intent(mContext, VideoCaptureActivity.class);
                ((Activity) mContext).startActivityForResult(videoIntent, ChatContent.VIDEO_REQUEST_CODE);
                break;

        }
    }
}
