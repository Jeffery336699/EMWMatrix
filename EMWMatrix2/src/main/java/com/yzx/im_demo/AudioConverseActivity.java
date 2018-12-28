package com.yzx.im_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yzx.api.UCSCall;
import com.yzx.tools.ContactTools;
import com.yzx.tools.DfineAction;
import com.yzx.tools.DialConfig;
import com.yzx.tools.UIDfineAction;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.emw.mobile.R;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * @Title AudioConverseActivity
 * @Description  语音电话界面
 * @Company yunzhixun
 * @author xhb
 * @date 2016-1-2 下午5:54:30  
 */
public class AudioConverseActivity extends ConverseActivity implements OnClickListener{
	private static final int CHAT_MODE_AUDIO = 1;//音频聊天
	private static final int CHAT_MODE_VIDEO = 2;//视频聊天
	public String calledUid ;
	public String calledPhone ;
	public String userName;
	public String phoneNumber;
	public String nickName;
	public boolean inCall = false; // true:来电；false:去电
	private int mCallType = 1; //1语音聊天  2视频聊天
	private TextView converse_client;
	private TextView converse_information;
	private LinearLayout ll_network_call_time;
	private LinearLayout ll_mute_pad_speaker;
	private ImageView converse_network;
	private TextView converse_call_time;
	private ImageButton converse_call_mute;
	private ImageButton converse_call_speaker;
	private ImageButton converse_call_hangup;
	private ImageButton converse_call_answer;
	private ImageButton converse_call_endcall;
	private boolean open_headset=false;
	private int calltype = 1;
	private boolean speakerPhoneState = false;
	public static String IncomingCallId;	// 来电时的callId，作用是防止有些时间挂断电话的信令来了，但是通话界面还没有开启，这个时候在来来电信令，电话就不挂断。
	private String timer;	// 通话时间
	private String callStartTime = null; //通话开始时间
	private String callId="";
	private boolean incallAnswer = false;
	private static final int AUDIO_CONVERSE_CLOSE = 1; // 关闭界面
	private static final int ACTION_NETWORK_STATE = 2;	// 更新网络状态
	//	private static final String TAG = "AudioConverseActivity";
	private int sound; // 触摸提示音的状态，0：关，1：开
	private AudioManager mAudioManager;

	private CircleImageView converse_head;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case AUDIO_CONVERSE_CLOSE: 	// 关闭界面
					UCSCall.setSpeakerphone(AudioConverseActivity.this, false);
					AudioConverseActivity.this.finish();
					break;
				case ACTION_NETWORK_STATE:	// 更新网络状态
					switch (msg.arg1) {
						case 1:
							converse_network.setBackgroundResource(R.drawable.audio_signal3);
							break;
						case 2:
							converse_network.setBackgroundResource(R.drawable.audio_signal2);
							break;
						case 3:
							converse_network.setBackgroundResource(R.drawable.audio_signal1);
							break;
						case 4:
							converse_network.setBackgroundResource(R.drawable.audio_signal0);
							break;
					}
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_converse);
		initview();
		initListener();
		initData();
		IntentFilter ift = new IntentFilter();
		ift.addAction(UIDfineAction.ACTION_DIAL_STATE);
		ift.addAction(UIDfineAction.ACTION_ANSWER);
		ift.addAction(UIDfineAction.ACTION_CALL_TIME);
		ift.addAction(UIDfineAction.ACTION_DIAL_HANGUP);
		ift.addAction(UIDfineAction.ACTION_NETWORK_STATE);
		ift.addAction("android.intent.action.HEADSET_PLUG");
		registerReceiver(br, ift);

		try {
			//如果系统触摸音是关的就不用管，开的就把它给关掉，因为在个别手机上有可能会影响音质
			mAudioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
			sound = Settings.System.getInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED);
			CustomLog.v("AudioConverseActivity sound: " + sound);
			if(sound == 1) {
				Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
				mAudioManager.unloadSoundEffects();
			}
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			CustomLog.v("SettingNotFound SOUND_EFFECTS_ENABLED ...");
		}

		if (inCall) {
			CustomLog.v("IncomingCallId = " + IncomingCallId + ",callId = " + getIntent().getStringExtra("callId"));
			if(getIntent().hasExtra("callId")) {
				callId = getIntent().getStringExtra("callId");
				if(callId.equals(IncomingCallId)) {
//                  sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_OTHER));
					converse_information.setVisibility(View.VISIBLE);
					converse_information.setText("对方已挂机");
					UCSCall.stopRinging(AudioConverseActivity.this);
					handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
					return;
				}
			}
		}

		//通话接通前按钮不可用
		converse_call_mute.setClickable(false);
		converse_call_speaker.setClickable(false);

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.build(); // 创建配置过得DisplayImageOption对象
		String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil
						.readUserInfo().CompanyCode,
				getIntent().getStringExtra("call_head"));
		ImageLoader.getInstance().displayImage(uri, converse_head, options);
	}

	/**
	 * @Description 初始化界面上控件的监听器
	 * @date 2016-1-3 上午10:13:27 
	 * @author xhb
	 * @return void    返回类型 
	 */
	private void initListener() {
		converse_call_mute.setOnClickListener(this);	//静音
		converse_call_speaker.setOnClickListener(this);	//扬声器
		converse_call_answer.setOnClickListener(this);	//接通
		converse_call_hangup.setOnClickListener(this);	//挂断
		converse_call_endcall.setOnClickListener(this);	//结束通话
	}

	/**
	 * @Description 初始化view
	 * @date 2015-12-15 下午2:58:08 
	 * @author xhb
	 * @return void    返回类型 
	 */
	private void initview() {
		converse_head = (CircleImageView) findViewById(R.id.converse_head);
		converse_client = (TextView) findViewById(R.id.converse_name);
		converse_information = (TextView) findViewById(R.id.converse_information);
		ll_network_call_time = (LinearLayout) findViewById(R.id.ll_network_call_time);
		ll_mute_pad_speaker = (LinearLayout)findViewById(R.id.id_layout_mps);
		converse_network = (ImageView) findViewById(R.id.converse_network);
		converse_call_time = (TextView) findViewById(R.id.converse_call_time);

		converse_call_mute = (ImageButton) findViewById(R.id.converse_call_mute);
		converse_call_speaker = (ImageButton)findViewById(R.id.converse_call_speaker);
		converse_call_answer = (ImageButton)findViewById(R.id.audio_call_answer);
		converse_call_hangup = (ImageButton)findViewById(R.id.audio_call_hangup);
		converse_call_endcall  = (ImageButton)findViewById(R.id.audio_call_endcall);
	}


	/**
	 * @Description 初始化数据
	 * @date 2015-12-15 下午2:57:53 
	 * @author xhb
	 * @return void    返回类型 
	 */
	private void initData() {
		if(getIntent().hasExtra("call_type")){//直拨 免费电话
			calltype = getIntent().getIntExtra("call_type", 1);
		}
		if(getIntent().hasExtra("callType")){//音频或视频
			mCallType = getIntent().getIntExtra("callType", 1);
		}
		//判断是否是来电信息，默认是去电，（来电信息是由ConnectionService中的onIncomingCall回调中发送广播，开启通话界面，inCall为true）
		if(getIntent().hasExtra("inCall")){
			inCall = getIntent().getBooleanExtra("inCall", false);
			//判断网络类型，2G时提示一下
			int netstate = NetWorkTools.getCurrentNetWorkType(this);
			if (netstate == NetWorkTools.NETWORK_EDGE)
				Toast.makeText(this, "网络状态差", Toast.LENGTH_SHORT).show();
		}
		//获得要拨打的号码，智能拨打和免费通话：phoneNumber代表ClientID，直拨和回拨代表ClientID绑定的手机号码
		if(getIntent().hasExtra("userId")){
			calledUid = getIntent().getStringExtra("userId");
		}
		if(getIntent().hasExtra("call_phone")){
			calledPhone = getIntent().getStringExtra("call_phone");
		}
		if(getIntent().hasExtra("userName")) {
			userName = getIntent().getStringExtra("userName");
		}
		if(getIntent().hasExtra("phoneNumber")) {
			phoneNumber = getIntent().getStringExtra("phoneNumber");
		}
		if(getIntent().hasExtra("nickName")) {
			nickName = getIntent().getStringExtra("nickName");
		}

		/*if(phoneNumber != null && phoneNumber.length() > 0) {
			// 先显示通讯录中的昵称
			userName = ContactTools.getConTitle(phoneNumber);
			// 在从IM会话中获取通话记录昵称
			if(TextUtils.isEmpty(userName)) {
				@SuppressWarnings("unchecked")
				List<ConversationInfo> conversationLists = IMManager.getInstance(this).getConversationList(CategoryId.PERSONAL);
				if(conversationLists != null && conversationLists.size() > 0) {
					for (ConversationInfo conversationInfo : conversationLists) {
						if(phoneNumber.equals(conversationInfo.getTargetId())) {
							CustomLog.i("conversation number ...");
							userName = conversationInfo.getConversationTitle();
						}
					}
				}
			}
		}*/
		if (userName != null && !"".equals(userName)){
			converse_client.setText(userName);
		} else if(calledUid != null && !"".equals(calledUid)){
			converse_client.setText(calledUid);
		}else if(calledPhone != null && !"".equals(calledPhone)){
			converse_client.setText(calledPhone);
		}
		if(inCall){
			//来电
			if (userName != null && !"".equals(userName)){
				converse_client.setText(userName);
			}else if(nickName != null && !"".equals(nickName)){
				converse_client.setText(nickName);
			}else if(phoneNumber != null && !"".equals(phoneNumber)){
				converse_client.setText(phoneNumber);
			}
			converse_call_answer.setVisibility(View.VISIBLE);
			converse_call_hangup.setVisibility(View.VISIBLE);
			converse_call_endcall.setVisibility(View.GONE);
			if(CHAT_MODE_AUDIO == mCallType){
				converse_information.setText("语音聊天");
			}else{
				converse_information.setText("视频聊天");
			}
			UCSCall.setSpeakerphone(AudioConverseActivity.this, true);
			startRing(AudioConverseActivity.this);
		}else{
			//去电
			converse_call_answer.setVisibility(View.GONE);
			converse_call_hangup.setVisibility(View.VISIBLE);
			converse_call_endcall.setVisibility(View.GONE);
			converse_information.setText("呼叫请求中");
			//进行拨号

//			UCSCall.startCallRinging(AudioConverseActivity.this,"dialling_tone.pcm");
			Intent intent = new Intent(UIDfineAction.ACTION_DIAL);
			sendBroadcast(intent.putExtra(UIDfineAction.CALL_UID, calledUid).putExtra(UIDfineAction.CALL_PHONE, calledPhone).putExtra("type", calltype));
		}
	}

	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(UIDfineAction.ACTION_DIAL_STATE)){
				int state = intent.getIntExtra("state", 0);
				CustomLog.v(DfineAction.TAG_TCP, "AUDIO_CALL_STATE:"+state);
				if(UIDfineAction.dialState.keySet().contains(state)){
					CustomLog.v(state+UIDfineAction.dialState.get(state));
					//获得通话状态信息
					if(state == 300226){//对方挂断电话
						ll_network_call_time.setVisibility(View.GONE);
						converse_information.setVisibility(View.VISIBLE);
					}
					converse_information.setText(UIDfineAction.dialState.get(state));
				}
				if((calltype == 1 && state == UCSCall.CALL_VOIP_RINGING_180)
						|| (calltype == 5 && state == UCSCall.CALL_VOIP_TRYING_183)){
					UCSCall.setSpeakerphone(AudioConverseActivity.this, true);
//					UCSCall.stopCallRinging(AudioConverseActivity.this);
					UCSCall.stopRinging(AudioConverseActivity.this);
				}
				if(state == UCSCall.NOT_NETWORK){
					converse_information.setText("当前处于无网络状态");
					UCSCall.stopRinging(AudioConverseActivity.this);
					handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
				}
				if(state == UCSCall.HUNGUP_REFUSAL || state == UCSCall.HUNGUP_MYSELF
						|| state == UCSCall.HUNGUP_OTHER || state == UCSCall.HUNGUP_MYSELF_REFUSAL
						|| state == UCSCall.HUNGUP_RTP_TIMEOUT || state == UCSCall.HUNGUP_OTHER_REASON){
					CustomLog.v("收到挂断信息");
					UCSCall.stopRinging(AudioConverseActivity.this);
					handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
				}else{
					if((state >= 300210 && state <= 300250)&&
							(state != 300221 && state != 300222 && state !=300247)
							|| state == UCSCall.HUNGUP_NOT_ANSWER){
						handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
					}
				}
			} else if(intent.getAction().equals(UIDfineAction.ACTION_ANSWER)){
				AudioConverseActivity.this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
				incallAnswer = true;
				ll_network_call_time.setVisibility(View.VISIBLE);
				//接通后通知服务开启计时器
				//sendBroadcast(new Intent(UIDfineAction.ACTION_START_TIME));
				converse_information.setVisibility(View.GONE);
				//接通后关闭回铃音
				converse_call_answer.setVisibility(View.GONE);
				converse_call_hangup.setVisibility(View.GONE);
				converse_call_endcall.setVisibility(View.VISIBLE);
				ll_mute_pad_speaker.setVisibility(View.VISIBLE);
				//接通电话后按钮变为可用
				converse_call_mute.setClickable(true);
				converse_call_speaker.setClickable(true);

				//记录通话开始时间
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd-HH:mm");
				callStartTime = dateFormat.format(new Date());

				UCSCall.stopRinging(AudioConverseActivity.this);
				UCSCall.setSpeakerphone(AudioConverseActivity.this, false);
				CustomLog.v("Speakerphone state:" + mAudioManager.isSpeakerphoneOn());
				converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
				open_headset = true;
			}else if(intent.getAction().equals(UIDfineAction.ACTION_DIAL_HANGUP)){
				handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
			}else if(intent.getAction().equals(UIDfineAction.ACTION_CALL_TIME)){
				timer = intent.getStringExtra("timer");
				if(converse_call_time != null){
					converse_call_time.setText(timer);
				}
			}else if(intent.getAction().equals(UIDfineAction.ACTION_NETWORK_STATE)){
				int networkState = intent.getIntExtra("state", 0);
				Message msg = handler.obtainMessage();
				msg.what = ACTION_NETWORK_STATE;
				msg.arg1 = networkState;
				handler.sendMessageDelayed(msg, 1000);
			}else if(intent.getAction().equals("android.intent.action.HEADSET_PLUG")){
				//发现个别手机会接通电话前收到这个广播并把扬声器打开了，所以open_headset作为判断必须接通后再接收耳机插拔广播才有效
				if(intent.getIntExtra("state", 0) == 1 && open_headset){
					CustomLog.e(DfineAction.TAG_TCP,"Speaker false");
					converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
					speakerPhoneState = UCSCall.isSpeakerphoneOn(AudioConverseActivity.this);
					UCSCall.setSpeakerphone(AudioConverseActivity.this, false);
				}else if(intent.getIntExtra("state", 0) == 0 && open_headset){//headset disconnected
					//CustomLog.e("headset unplug");
					if(speakerPhoneState){
						//CustomLog.e("Speaker true");
						converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker_down);
						UCSCall.setSpeakerphone(AudioConverseActivity.this, true);
					} else {
						mAudioManager.setSpeakerphoneOn(false);
						converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
					}
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if(mCallType == CHAT_MODE_AUDIO ||
				(mCallType == CHAT_MODE_VIDEO &&incallAnswer == false)){
			if(inCall == true && incallAnswer == false) { // 呼入未接
				addCallRecord(2, inCall, userName, phoneNumber, calledPhone, mCallType, callStartTime, timer);
			} else if(inCall == false && incallAnswer == false) { // 呼出未接
				addCallRecord(3, inCall, userName, phoneNumber, calledPhone, mCallType, callStartTime, timer);
			} else {
				addCallRecord(1, inCall, userName, phoneNumber, calledPhone, mCallType, callStartTime, timer);
			}
		}
		unregisterReceiver(br);
//		UCSCall.stopCallRinging(AudioConverseActivity.this);
		CustomLog.i("audioConverseActivity onDestroy() ...");
		if(sound == 1) {  // 如果系统触摸提示音是开的，前面把它给关系，现在退出页面要把它还原
			Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
			mAudioManager.loadSoundEffects();
		}
		super.onDestroy();
	}

	/**
	 * @Description 界面上控件的监听事件
	 * @author xhb
	 * @date 2016-1-3 上午11:36:19 
	 * @return void    返回类型 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.converse_call_mute:	// 静音
				if(UCSCall.isMicMute()){
					converse_call_mute.setBackgroundResource(R.drawable.converse_mute);
				}else{
					converse_call_mute.setBackgroundResource(R.drawable.converse_mute_down);
				}
				UCSCall.setMicMute(!UCSCall.isMicMute());
				break;
			case R.id.converse_call_speaker:	//扬声器
				if(UCSCall.isSpeakerphoneOn(AudioConverseActivity.this)){
					converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
				}else{
					converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker_down);
				}
				UCSCall.setSpeakerphone(AudioConverseActivity.this, !UCSCall.isSpeakerphoneOn(AudioConverseActivity.this));
				break;
			case R.id.audio_call_answer:	// 接通
				if(CHAT_MODE_AUDIO == mCallType){
					incallAnswer = true;
					ll_network_call_time.setVisibility(View.VISIBLE);
					CustomLog.v(DfineAction.TAG_TCP,"接通电话");
					UCSCall.stopRinging(AudioConverseActivity.this);
					UCSCall.answer(AudioConverseActivity.this, "");
				}else if(CHAT_MODE_VIDEO == mCallType){
					startVideoActivity();
				}

				break;
			case R.id.audio_call_hangup:	//挂断
				DialConfig.saveCallType(AudioConverseActivity.this, "");
				UCSCall.stopRinging(AudioConverseActivity.this);
				UCSCall.hangUp(AudioConverseActivity.this, "");
				handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1500);
				break;
			case R.id.audio_call_endcall:	// 结束通话
				CustomLog.v(DfineAction.TAG_TCP,"结束电话");
				UCSCall.stopRinging(AudioConverseActivity.this);
				UCSCall.hangUp(AudioConverseActivity.this, "");
				handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1500);
				break;
			default:
				break;
		}
	}

	/**
	 * @Description 开启视频聊天界面
	 * @author xhb
	 * @date 2016-1-20 上午11:36:19
	 * @return void    返回类型
	 */
	private void startVideoActivity(){
		if(inCall){ //视频来电
			Intent intentVideo = new Intent(AudioConverseActivity.this, VideoConverseActivity.class);
			intentVideo.putExtra("phoneNumber", phoneNumber).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentVideo.putExtra("inCall", true);
			intentVideo.putExtra("nickName", nickName);
			intentVideo.putExtra("callId", callId);
			intentVideo.putExtra("userName",nickName);
			startActivity(intentVideo);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			}, 1000);
		}
	}
}
