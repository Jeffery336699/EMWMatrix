package cc.emw.mobile.asr;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.emw.mobile.R;
import cc.emw.mobile.asr.asrfinishjson.AsrFinishJsonData;
import cc.emw.mobile.asr.asrpartialjson.AsrPartialJsonData;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.view.EmoticonsEditText;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;

import static cc.emw.mobile.R.id.cm_header_tv_right;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.asr
 * @data on 2018/9/28  17:02
 * @describe TODO
 */
@ContentView(R.layout.activity_asr_txt)
public class AsrTxtActivity extends BaseActivity implements EventListener {

    public EditText et_help_feedback;
    public EditText et_help_hand;
    private Dialog mLoadingDialog; //加载框
    private String storePath = "";

    private EventManager asr;
    private String final_result;
    private View cover;

    private Button btn_send_help_feedback;
    private final static String TAG = "AsrTxtActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        et_help_feedback = (EditText) findViewById(R.id.et_help_feedback);
        et_help_hand = (EditText) findViewById(R.id.et_help_hand);
        initPermission();
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
        btn_send_help_feedback = (Button) findViewById(R.id.btn_send_help_feedback);
        btn_send_help_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    @Event(value = {R.id.cm_header_btn_left, cm_header_tv_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case cm_header_tv_right:
                if (null == et_help_feedback.getText().toString() || et_help_feedback.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(this, "内容不能为空");
                    return;
                }
                String fileName = "";
                if (null == et_help_hand.getText().toString() || et_help_hand.getText().toString().trim().equals("")) {
                    fileName = StringUtils.getDataTime("yyyy-MM-dd_HH:mm:ss");
                } else {
                    fileName = et_help_hand.getText().toString().trim();
                }
                stringTxt(fileName, et_help_feedback.getText().toString().trim());
                break;
        }
    }

    public void stringTxt(String fileName, String str) {
        try {
            storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName + ".txt";
            File filePath = new File(storePath);
            /*FileWriter fw = new FileWriter(storePath);//SD卡中的路径,该方式有编码风险，文件内容可能出现乱码
            fw.flush();
            fw.write(str);
            fw.close();*/

            /*FileOutputStream fout = new FileOutputStream(filePath);
            byte[] bytes = str.getBytes();
            fout.write(bytes);
            fout.close();*/

            OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(filePath), "utf-8");
            oStreamWriter.append(str);
            oStreamWriter.close();

            File file = new File(storePath);
            if (file.exists()) {
                uploadFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    /*
    * EventListener  回调方法
    * name:回调事件
    * params: JSON数据，其格式如下：
    *
    * */
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String result = "";

        if (length > 0 && data.length > 0) {
            result += ", 语义解析结果：" + new String(data, offset, length);
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            result += "引擎准备就绪，可以开始说话";

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            result += "检测到用户的已经开始说话";

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            result += "检测到用户的已经停止说话";
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            // 临时识别结果, 长语音模式需要从此消息中取出结果
            result += "识别临时识别结果";
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
            //            Log.d(TAG, "Temp Params:"+params);
            parseAsrPartialJsonData(params);
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束， 最终识别结果或可能的错误
            result += "识别结束";
            btn_send_help_feedback.setEnabled(true);
            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
            Log.d(TAG, "Result Params:" + params);
            parseAsrFinishJsonData(params);
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_AUDIO)) {

        }
        printResult(result);
    }

    private void printResult(String text) {
        //mEetChatAddText.append(text + "\n");
    }

    Button cancel_btn, confirm_btn;
    EmoticonsEditText tv_dialog_content;
    int state;//1为说完了，2为发送

    private void start() {
        if (null == cover) {
            cover = getLayoutInflater().inflate(R.layout.asr_txt_dialog, null);
            cancel_btn = (Button) cover.findViewById(R.id.cancel_btn);
            confirm_btn = (Button) cover.findViewById(R.id.confirm_btn);
            tv_dialog_content = (EmoticonsEditText) cover.findViewById(R.id.tv_dialog_content);
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                    if (null != cover) {
                        FrameLayout root = (FrameLayout) findViewById(R.id.asr_main);
                        root.removeView(cover);
                        cover = null;
                        btn_send_help_feedback.setEnabled(true);
                        state = 1;
                    }
                }
            });
            confirm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state == 1) {
                        stop();
                    } else {
                        String str = tv_dialog_content.getText().toString();
                        if (!"".equals(str) && !str.trim().equals("")) {
                            String txtStr = et_help_feedback.getText().toString();
                            et_help_feedback.setText(txtStr + str);
                        } else {
                            ToastUtil.showToast(AsrTxtActivity.this, "未识别到有效语音");
                        }
                        if (null != cover) {
                            FrameLayout root = (FrameLayout) findViewById(R.id.asr_main);
                            root.removeView(cover);
                            cover = null;
                            btn_send_help_feedback.setEnabled(true);
                        }
                        state = 1;
                    }
                }
            });
            FrameLayout root = (FrameLayout) findViewById(R.id.asr_main);
            root.addView(cover);
            state = 1;
        }
        btn_send_help_feedback.setEnabled(false);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START;
        params.put(SpeechConstant.PID, 15362); // 默认1536
        params.put(SpeechConstant.DECODER, 0); // 纯在线(默认)
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN); // 语音活动检测
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 3000); // 不开启长语音。开启VAD尾点检测，即静音判断的毫秒数。建议设置800ms-3000ms
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);// 是否需要语音音频数据回调
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);// 是否需要语音音量数据回调
        params.put(SpeechConstant.OUT_FILE, "/storage/emulated/0/baiduASR/outfile.pcm");// 音频存放地址

        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        printResult("输入参数：" + json);
    }

    private void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    private void cancel() {
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    private void parseAsrPartialJsonData(String data) {
        Log.d(TAG, "parseAsrPartialJsonData data:" + data);
        Gson gson = new Gson();
        AsrPartialJsonData jsonData = gson.fromJson(data, AsrPartialJsonData.class);
        String resultType = jsonData.getResult_type();
        Log.d(TAG, "resultType:" + resultType);
        if (resultType != null && resultType.equals("final_result")) {
            //只有识别结束才会进入此if
            final_result = jsonData.getBest_result();
        } else if (null != jsonData.getBest_result() && !jsonData.getBest_result().equals("")) {
            tv_dialog_content.setText(jsonData.getBest_result());
        }
    }

    private void parseAsrFinishJsonData(String data) {
        Log.d(TAG, "parseAsrFinishJsonData data:" + data);
        Gson gson = new Gson();
        AsrFinishJsonData jsonData = gson.fromJson(data, AsrFinishJsonData.class);
        String desc = jsonData.getDesc();
        if (desc != null && desc.equals("Speech Recognize success.")) {
            tv_dialog_content.setText(final_result);
            state = 2;
            confirm_btn.setText("发送");
            tv_dialog_content.setFocusable(true);
            tv_dialog_content.setFocusableInTouchMode(true);
        } else {
            String errorCode = "\n错误码:" + jsonData.getError();
            String errorSubCode = "\n错误子码:" + jsonData.getSub_error();
            String errorResult = errorCode + errorSubCode;
            //mEetChatAddText.setText("解析错误,原因是:" + desc + "\n" + errorResult);
            tv_dialog_content.setText("");
            ToastUtil.showToast(this, "未识别到有效语音");
            if (null != cover) {
                FrameLayout root = (FrameLayout) findViewById(R.id.asr_main);
                root.removeView(cover);
                cover = null;
                btn_send_help_feedback.setEnabled(true);
            }
            state = 1;
        }
    }

    /**
     * 上传文件
     */
    private void uploadFile() {
        RequestParam params = new RequestParam(Const.UPLOAD_FILE_URL);
        params.setMultipart(true);
        File file = new File(storePath);
        params.addBodyParameter("file_" + file.getName(), file);
        ArrayList<ApiEntity.Files> fileList = new ArrayList<ApiEntity.Files>();
        ApiEntity.Files noteFile = new ApiEntity.Files();
        noteFile.Name = file.getName();
        noteFile.Length = file.length();
        noteFile.Creator = PrefsUtil.readUserInfo().ID;
        noteFile.Url = file.getName();
        fileList.add(noteFile);
        params.addBodyParameter("UploadData", new Gson().toJson(fileList));
        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips4);
        mLoadingDialog.show();
        Callback.Cancelable cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                ToastUtil.showToast(AsrTxtActivity.this, R.string.filelist_upload_error);
                File file = new File(storePath);
                if (file.exists()) {
                    file.delete();
                }
            }

            @Override
            public void onFinished() {
                File file = new File(storePath);
                if (file.exists()) {
                    file.delete();
                }
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(AsrTxtActivity.this, R.string.filelist_upload_success, R.drawable.tishi_ico_gougou);
                    File file = new File(storePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    finish();
                } else {
                    ToastUtil.showToast(AsrTxtActivity.this, R.string.filelist_upload_error);
                    File file = new File(storePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        });
    }
}
