package cc.emw.mobile.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.view.EmoticonsToolBarView;
import cc.emw.mobile.util.HelpUtil;
import sj.keyboard.adpater.PageSetAdapter;
import sj.keyboard.data.PageSetEntity;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.EmoticonsFuncView;
import sj.keyboard.widget.EmoticonsIndicatorView;
import sj.keyboard.widget.FuncLayout;

public class DynamicEmoticonsKeyBoard extends AutoHeightLayout implements EmoticonsFuncView.OnEmoticonsPageViewListener,
        EmoticonsToolBarView.OnToolBarItemClickListener, EmoticonsEditText.OnBackKeyClickListener, FuncLayout.OnFuncChangeListener, View.OnClickListener {

    public final int APPS_HEIGHT = 240;

    public static final int FUNC_TYPE_IMG = 1;
    public static final int FUNC_TYPE_FILE = 2;
    public static final int FUNC_TYPE_EMOTICON = 3;

    protected LayoutInflater mInflater;

    protected EmoticonsFuncView mEmoticonsFuncView;
    protected EmoticonsIndicatorView mEmoticonsIndicatorView;
    protected EmoticonsToolBarView mEmoticonsToolBarView;

    protected boolean mDispatchKeyEventPreImeLock = false;
    private EmoticonsEditText etChat;
    private ImageView btnSend;
    private TextView btnPtv;
    private TextView btnImage;
    private ImageView btnEmoticon;
    private FuncLayout lyKvml;
    private Context mContext;

    public DynamicEmoticonsKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.view_keyboard_dynamic, this);
        initView(view);
        initFuncView();
    }

    protected void initView(View view) {
        lyKvml = (FuncLayout) view.findViewById(R.id.ly_kvml);

        btnEmoticon = (ImageView) view.findViewById(R.id.btn_emoticon);
        btnImage = (TextView) view.findViewById(R.id.btn_image);
        btnPtv = (TextView) view.findViewById(R.id.btn_ptv);
        btnSend = (ImageView) view.findViewById(R.id.btn_send);
        etChat = (EmoticonsEditText) view.findViewById(R.id.et_chat);
        btnSend.setVisibility(GONE);
        lyKvml.setOnClickListener(this);
        btnEmoticon.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnPtv.setOnClickListener(this);
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
                if (!TextUtils.isEmpty(s)) {
                    btnSend.setVisibility(VISIBLE);
                } else {
                    btnSend.setVisibility(GONE);
                }
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
        Log.d("keyboard", "onSoftKeyboardHeightChanged():"+height + ","+isSoftKeyboardPop());
    }

    @Override
    public void OnSoftPop(int height) {
        super.OnSoftPop(height);
        Log.d("keyboard", "OnSoftPop():"+height + ","+isSoftKeyboardPop());
        lyKvml.setVisibility(true);
        onFuncChange(lyKvml.DEF_KEY);
    }

    @Override
    public void OnSoftClose() {
        super.OnSoftClose();
        Log.d("keyboard", "onSoftClose()" + ","+isSoftKeyboardPop());
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

    public EmoticonsFuncView getEmoticonsFuncView() {
        return mEmoticonsFuncView;
    }

    public EmoticonsIndicatorView getEmoticonsIndicatorView() {
        return mEmoticonsIndicatorView;
    }

    public EmoticonsToolBarView getEmoticonsToolBarView() {
        return mEmoticonsToolBarView;
    }


    public ImageView getBtnSend() {
        return btnSend;
    }

    public FuncLayout getFuncLayout() {
        return lyKvml;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ptv:
                toggleFuncView(FUNC_TYPE_FILE);
                setFuncViewHeight(EmoticonsKeyboardUtils.dip2px(getContext(), APPS_HEIGHT));
                break;
            case R.id.btn_image:
//                Crop.pickImage((Activity) mContext);
                toggleFuncView(FUNC_TYPE_IMG);
                setFuncViewHeight(EmoticonsKeyboardUtils.dip2px(getContext(), APPS_HEIGHT));
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
        }
    }
}
