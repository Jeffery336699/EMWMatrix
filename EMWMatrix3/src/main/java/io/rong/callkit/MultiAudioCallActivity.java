package io.rong.callkit;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.CircleImageView;
import io.rong.calllib.CallUserProfile;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
//import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;

public class MultiAudioCallActivity extends BaseCallActivity {
    private static final String TAG = "VoIPMultiAudioCallActivity";
    LinearLayout maudioContainer;
    CallUserGridView memberContainer;
    LinearLayout memberLayout;
    RadarViewGroup radarViewGroup;

    RelativeLayout incomingLayout;
    RelativeLayout outgoingLayout;
    FrameLayout outgoingController;
    FrameLayout incomingController;
    RongCallAction callAction;
    RongCallSession callSession;

    boolean shouldShowFloat = true;
    boolean startForCheckPermissions = false;

    private DisplayImageOptions options;

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RLog.e(TAG,"----oncreate-----");
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.rc_voip_ac_muti_audio);
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象

        maudioContainer = (LinearLayout) findViewById(R.id.rc_voip_container);
        incomingLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.rc_voip_item_incoming_maudio, null);
        outgoingLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.rc_voip_item_outgoing_maudio, null);
        outgoingController = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        incomingController = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.rc_voip_call_bottom_incoming_button_layout, null);

        startForCheckPermissions = getIntent().getBooleanExtra("checkPermissions", false);
        if (!requestCallPermissions(RongCallCommon.CallMediaType.AUDIO, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
            return;
        }
//        RongContext.getInstance().getEventBus().register(this);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        startForCheckPermissions = getIntent().getBooleanExtra("checkPermissions", false);
        if (!requestCallPermissions(RongCallCommon.CallMediaType.AUDIO, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
            return;
        }
        initView();

        super.onNewIntent(intent);
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (PermissionCheckUtil.checkPermissions(this, AUDIO_CALL_PERMISSIONS)) {
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionGranted();
                    } else {
                        initView();
                    }
                } else {
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionDenied();
                    } else {
                        finish();
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onRestoreFloatBox(Bundle bundle) {
        super.onRestoreFloatBox(bundle);
        if (bundle != null) {
            maudioContainer.addView(outgoingLayout);
            radarViewGroup = (RadarViewGroup) maudioContainer.findViewById(R.id.radar);
            memberLayout = (LinearLayout) maudioContainer.findViewById(R.id.member);
            memberContainer = (CallUserGridView) maudioContainer.findViewById(R.id.rc_voip_members_container);
            memberLayout.setVisibility(View.GONE);
            radarViewGroup.setVisibility(View.VISIBLE);
            FrameLayout controller = (FrameLayout) maudioContainer.findViewById(R.id.rc_voip_control_layout);
            controller.addView(outgoingController);
            callSession = RongCallClient.getInstance().getCallSession();
            if (callSession == null || callSession.getParticipantProfileList() == null){
                setShouldShowFloat(false);
                finish();
            }
            memberContainer.enableShowState(true);

            List<CallUserProfile> participantProfiles = callSession.getParticipantProfileList();
            for (CallUserProfile item : participantProfiles) {
                if (!item.getUserId().equals(callSession.getSelfUserId()) && !item.getUserId().equals(callSession.getCallerUserId())) {
                    if (item.getCallStatus().equals(RongCallCommon.CallStatus.CONNECTED)) {
//                        memberContainer.addChild(item.getUserId(), RongContext.getInstance().getUserInfoFromCache(item.getUserId()));
                        memberContainer.addChild(item.getUserId(), EMWApplication.personMap.get(Integer.valueOf(item.getUserId())));
                        radarViewGroup.addChildView(item.getUserId());
                    } else {
//                        String state = getString(R.string.rc_voip_call_connecting);
//                        memberContainer.addChild(item.getUserId(), RongContext.getInstance().getUserInfoFromCache(item.getUserId()), state);
                        memberContainer.addChild(item.getUserId(), EMWApplication.personMap.get(Integer.valueOf(item.getUserId())), "…");
                    }
                }
            }
            onCallConnected(callSession, null);
        }
    }

    void initView() {
        Intent intent = getIntent();
        callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
        if (callAction == null || callAction.equals(RongCallAction.ACTION_RESUME_CALL)) {
            return;
        }

        ArrayList<String> invitedList = new ArrayList<>();

        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            CircleImageView userPortrait = (CircleImageView) incomingLayout.findViewById(R.id.rc_voip_user_portrait);
            int uid = Integer.valueOf(callSession.getCallerUserId());
            UserInfo userInfo = EMWApplication.personMap.get(uid);
            if (userInfo != null) {
                String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
                if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                    url = Const.DOWN_ICON_URL2 + userInfo.Image;
                }
                userPortrait.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 62);
                ImageLoader.getInstance().displayImage(url, userPortrait, options);
                TextView name = (TextView) incomingLayout.findViewById(R.id.rc_user_name);
                /*UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(callSession.getCallerUserId());
                if (userInfo != null && userInfo.getName() != null)
                    name.setText(userInfo.getName());
                else
                    name.setText(callSession.getCallerUserId());*/
                name.setText(userInfo.Name);
                name.setTag(callSession.getCallerUserId() + "callerName");
            } else {
                userPortrait.setTextBg(EMWApplication.getIconColor(uid), "", 62);
            }
            maudioContainer.addView(incomingLayout);
            radarViewGroup = (RadarViewGroup) maudioContainer.findViewById(R.id.radar);
            memberLayout = (LinearLayout) maudioContainer.findViewById(R.id.member);
            memberContainer = (CallUserGridView) maudioContainer.findViewById(R.id.rc_voip_members_container);
            memberContainer.setChildPortraitSize(memberContainer.dip2pix(32));
            List<CallUserProfile> list = callSession.getParticipantProfileList();
            for (CallUserProfile profile : list) {
                if (!profile.getUserId().equals(callSession.getCallerUserId())) {
                    invitedList.add(profile.getUserId());
//                    io.rong.imlib.model.UserInfo userInfo1 = RongContext.getInstance().getUserInfoFromCache(profile.getUserId());
                    userInfo = EMWApplication.personMap.get(Integer.valueOf(profile.getUserId()));
                    memberContainer.addChild(profile.getUserId(), userInfo);
                }
            }
            FrameLayout controller = (FrameLayout) maudioContainer.findViewById(R.id.rc_voip_control_layout);
            controller.addView(incomingController);
            onIncomingCallRinging();
        } else if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            Conversation.ConversationType conversationType = Conversation.ConversationType.valueOf(intent.getStringExtra("conversationType").toUpperCase(Locale.US));
            String targetId = intent.getStringExtra("targetId");
            ArrayList<String> userIds = intent.getStringArrayListExtra("invitedUsers");

            CircleImageView userPortrait = (CircleImageView) outgoingLayout.findViewById(R.id.rc_voip_user_portrait);
            UserInfo userInfo = EMWApplication.personMap.get(PrefsUtil.readUserInfo().ID);
            if (userInfo != null) {
                String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
                if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                    url = Const.DOWN_ICON_URL2 + userInfo.Image;
                }
                userPortrait.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 62);
                ImageLoader.getInstance().displayImage(url, userPortrait, options);
                TextView name = (TextView) outgoingLayout.findViewById(R.id.rc_user_name);
                name.setText(userInfo.Name);
            } else {
                userPortrait.setTextBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), "", 62);
            }
            maudioContainer.addView(outgoingLayout);
            radarViewGroup = (RadarViewGroup) maudioContainer.findViewById(R.id.radar);
            memberLayout = (LinearLayout) maudioContainer.findViewById(R.id.member);
            memberContainer = (CallUserGridView) maudioContainer.findViewById(R.id.rc_voip_members_container);
//            memberContainer.enableShowState(true);
            memberContainer.setChildPortraitSize(memberContainer.dip2pix(32));
            FrameLayout controller = (FrameLayout) maudioContainer.findViewById(R.id.rc_voip_control_layout);
            outgoingController.findViewById(R.id.rc_voip_call_mute).setVisibility(View.GONE);
            outgoingController.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
            controller.addView(outgoingController);
            for (int i = 0; i < userIds.size(); i++) {
                if (!userIds.get(i).equals(RongIMClient.getInstance().getCurrentUserId())) {
                    invitedList.add(userIds.get(i));
//                    io.rong.imlib.model.UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(userIds.get(i));
//                    memberContainer.addChild(userIds.get(i), userInfo, getString(R.string.rc_voip_call_connecting));
                    userInfo = EMWApplication.personMap.get(Integer.valueOf(userIds.get(i)));
                    memberContainer.addChild(userIds.get(i), userInfo, "…");
                }
            }
            RongCallClient.getInstance().startCall(conversationType, targetId, invitedList, RongCallCommon.CallMediaType.AUDIO, "multi");
        }
        memberContainer.setOverScrollMode(View.OVER_SCROLL_NEVER);

        createPowerManager();
        createPickupDetector();
    }

    @Override
    protected void onPause() {
        if (pickupDetector != null){
            pickupDetector.unRegister();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (pickupDetector == null) createPickupDetector();
        if (wakeLock == null) createPowerManager();
        if (pickupDetector != null){
            pickupDetector.register(this);
        }
        super.onResume();
    }

    public void onHangupBtnClick(View view) {
        if (callSession == null || isFinishing) {
            return;
        }
        RongCallClient.getInstance().hangUpCall(callSession.getCallId());
    }

    public void onReceiveBtnClick(View view) {
        RLog.e(TAG,"----onReceiveBtnClick ====");
        if (callSession == null || isFinishing) {
            RLog.e(TAG,"----callSession == null-------");
            return;
        }
        RLog.e(TAG,"----acceptCall-- start ----");
        RongCallClient.getInstance().acceptCall(callSession.getCallId());
        RLog.e(TAG,"----acceptCall-- end ----");
    }

    @Override
    public void onRemoteUserRinging(String userId) {

    }

    @Override
    public void onCallOutgoing(RongCallSession callSession, SurfaceView localVideo) {
        super.onCallOutgoing(callSession, localVideo);
        this.callSession = callSession;
        onOutgoingCallRinging();
    }

    @Override
    public void onRemoteUserInvited(String userId, RongCallCommon.CallMediaType mediaType) {
        super.onRemoteUserInvited(userId, mediaType);
//        memberContainer.addChild(userId, RongContext.getInstance().getUserInfoFromCache(userId), getString(R.string.rc_voip_call_connecting));
        memberContainer.addChild(userId, EMWApplication.personMap.get(Integer.valueOf(userId)), "…");
    }

    @Override
    public void onRemoteUserJoined(String userId, RongCallCommon.CallMediaType mediaType, SurfaceView remoteVideo) {
        View view = memberContainer.findChildById(userId);
        if (view != null) {
            memberContainer.updateChildState(userId, false);
        } else {
//            memberContainer.addChild(userId, RongContext.getInstance().getUserInfoFromCache(userId));
            memberContainer.addChild(userId, EMWApplication.personMap.get(Integer.valueOf(userId)));
        }
        View childView = radarViewGroup.findViewWithTag(userId);
        if (childView == null) {
            radarViewGroup.addChildView(userId);
        }
    }

    @Override
    public void onRemoteUserLeft(final String userId, RongCallCommon.CallDisconnectedReason reason) {
        String text = null;
        switch (reason) {
            case REMOTE_BUSY_LINE:
                text = getString(R.string.rc_voip_mt_busy);
                break;
            case REMOTE_CANCEL:
                text = getString(R.string.rc_voip_mt_cancel);
                break;
            case REMOTE_REJECT:
                text = getString(R.string.rc_voip_mt_reject);
                break;
            case NO_RESPONSE:
                text = getString(R.string.rc_voip_mt_no_response);
                break;
            case NETWORK_ERROR:
            case HANGUP:
            case REMOTE_HANGUP:
                break;
        }
        if (text != null) {
            memberContainer.updateChildState(userId, text);
        }
        memberContainer.removeChild(userId);
        radarViewGroup.removeChildView(userId);
    }

    @Override
    public void onCallConnected(final RongCallSession callSession, SurfaceView localVideo) {
        super.onCallConnected(callSession, localVideo);
        RongCallClient.getInstance().setEnableSpeakerphone(false);
        this.callSession = callSession;
        stopRing();

        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            maudioContainer.removeAllViews();
            FrameLayout controller = (FrameLayout) outgoingLayout.findViewById(R.id.rc_voip_control_layout);
            controller.addView(outgoingController);
            maudioContainer.addView(outgoingLayout);
            radarViewGroup = (RadarViewGroup) maudioContainer.findViewById(R.id.radar);
            memberLayout = (LinearLayout) maudioContainer.findViewById(R.id.member);
            memberContainer = (CallUserGridView) outgoingLayout.findViewById(R.id.rc_voip_members_container);
            memberContainer.enableShowState(true);
            for (CallUserProfile profile : callSession.getParticipantProfileList()) {
                if (!profile.getUserId().equals(callSession.getSelfUserId())) {
//                    io.rong.imlib.model.UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(profile.getUserId());
//                    String state = profile.getCallStatus().equals(RongCallCommon.CallStatus.CONNECTED) ? null : getString(R.string.rc_voip_call_connecting);
                    UserInfo userInfo = EMWApplication.personMap.get(Integer.valueOf(profile.getUserId()));
                    String state = profile.getCallStatus().equals(RongCallCommon.CallStatus.CONNECTED) ? null : "…";
                    memberContainer.addChild(profile.getUserId(), userInfo, state);
                    if (state == null) {
                        radarViewGroup.addChildView(profile.getUserId());
                    }
                }
            }
        }
        memberLayout.setVisibility(View.GONE);
        radarViewGroup.setVisibility(View.VISIBLE);

        outgoingLayout.findViewById(R.id.rc_voip_remind).setVisibility(View.GONE);
        outgoingLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.VISIBLE);
        outgoingLayout.findViewById(R.id.rc_voip_call_mute).setVisibility(View.VISIBLE);

        View muteV = outgoingLayout.findViewById(R.id.rc_voip_call_mute_btn);
        muteV.setVisibility(View.VISIBLE);
        muteV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongCallClient.getInstance().setEnableLocalAudio(v.isSelected());
                v.setSelected(!v.isSelected());
            }
        });

        View handfreeV = outgoingLayout.findViewById(R.id.rc_voip_handfree_btn);
        handfreeV.setVisibility(View.VISIBLE);
        handfreeV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongCallClient.getInstance().setEnableSpeakerphone(!v.isSelected());
                v.setSelected(!v.isSelected());
            }
        });

        outgoingLayout.findViewById(R.id.rc_voip_title).setVisibility(View.VISIBLE);
        TextView timeV = (TextView) outgoingLayout.findViewById(R.id.rc_voip_time);
        setupTime(timeV);

        View imgvAdd = outgoingLayout.findViewById(R.id.rc_voip_add_btn);
        imgvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldShowFloat = false;
                if (callSession.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) {
                    RongIMClient.getInstance().getDiscussion(callSession.getTargetId(), new RongIMClient.ResultCallback<Discussion>() {
                        @Override
                        public void onSuccess(Discussion discussion) {
                            Intent intent = new Intent(MultiAudioCallActivity.this, CallSelectMemberActivity.class);
                            ArrayList<String> added = new ArrayList<String>();
                            List<CallUserProfile> list = RongCallClient.getInstance().getCallSession().getParticipantProfileList();
                            for (CallUserProfile profile : list) {
                                added.add(profile.getUserId());
                            }
                            ArrayList<String> allIdList = new ArrayList<>();
                            if (EMWApplication.personSortList != null) {
                                for (UserInfo userInfo : EMWApplication.personSortList) {
                                    if (userInfo != null) {
                                        allIdList.add(Integer.toString(userInfo.ID));
                                    }
                                }
                            }
                            intent.putStringArrayListExtra("allMembers", allIdList);
//                            intent.putStringArrayListExtra("allMembers", (ArrayList<String>) discussion.getMemberIdList());
                            intent.putStringArrayListExtra("invitedMembers", added);
                            intent.putExtra("mediaType", RongCallCommon.CallMediaType.AUDIO.getValue());
                            startActivityForResult(intent, 110);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });
                } else if (callSession.getConversationType().equals(Conversation.ConversationType.GROUP)) {
                    Intent intent = new Intent(MultiAudioCallActivity.this, CallSelectMemberActivity.class);
                    ArrayList<String> added = new ArrayList<>();
                    List<CallUserProfile> list = RongCallClient.getInstance().getCallSession().getParticipantProfileList();
                    for (CallUserProfile profile : list) {
                        added.add(profile.getUserId());
                    }
                    intent.putStringArrayListExtra("invitedMembers", added);
                    intent.putExtra("groupId", callSession.getTargetId());
                    intent.putExtra("mediaType", RongCallCommon.CallMediaType.AUDIO.getValue());
                    startActivityForResult(intent, 110);
                }
            }
        });

        View minimizeV = outgoingLayout.findViewById(R.id.rc_voip_minimize);
        minimizeV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiAudioCallActivity.super.onMinimizeClick(v);
            }
        });
    }

    @Override
    public void onCallDisconnected(RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason) {
        super.onCallDisconnected(callSession, reason);

        isFinishing = true;
        if (reason == null || callSession == null) {
            RLog.e(TAG, "onCallDisconnected. callSession is null!");
            postRunnableDelay(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
            return;
        }

        InformationNotificationMessage informationMessage;
        if (reason.equals(RongCallCommon.CallDisconnectedReason.NO_RESPONSE)) {
            informationMessage = InformationNotificationMessage.obtain(RongContext.getInstance().getString(R.string.rc_voip_audio_no_response));
        } else {
            informationMessage = InformationNotificationMessage.obtain(RongContext.getInstance().getString(R.string.rc_voip_audio_ended));
        }
        RongIM.getInstance().insertMessage(callSession.getConversationType(), callSession.getTargetId(), callSession.getCallerUserId(), informationMessage, null);
        stopRing();
        postRunnableDelay(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            if (PermissionCheckUtil.checkPermissions(this, AUDIO_CALL_PERMISSIONS)) {
                if (startForCheckPermissions) {
                    startForCheckPermissions = false;
                    RongCallClient.getInstance().onPermissionGranted();
                } else {
                    initView();
                }
            } else {
                if (startForCheckPermissions) {
                    startForCheckPermissions = false;
                    RongCallClient.getInstance().onPermissionDenied();
                } else {
                    finish();
                }
            }

        } else {
            if (callSession.getEndTime() != 0) {
                finish();
                return;
            }
            shouldShowFloat = true;
            if (resultCode == RESULT_OK) {
                final ArrayList<String> invited = data.getStringArrayListExtra("invited");
                if (callSession.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) {
                    RongIMClient.getInstance().addMemberToDiscussion(callSession.getTargetId(), invited, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            RongCallClient.getInstance().addParticipants(callSession.getCallId(), invited);
                        }
                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            ToastUtil.showToast(MultiAudioCallActivity.this, "邀请失败:"+errorCode.getValue());
                        }
                    });
                } else {
                    RongCallClient.getInstance().addParticipants(callSession.getCallId(), invited);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
//        RongContext.getInstance().getEventBus().unregister(this);
        if (wakeLock != null && wakeLock.isHeld()){
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
        }
        super.onDestroy();
    }

    public void onHandFreeButtonClick(View view) {
        RongCallClient.getInstance().setEnableSpeakerphone(!view.isSelected());
        view.setSelected(!view.isSelected());
    }

    public void onMuteButtonClick(View view) {
        RongCallClient.getInstance().setEnableLocalAudio(view.isSelected());
        view.setSelected(!view.isSelected());
    }

    @Override
    public String onSaveFloatBoxState(Bundle bundle) {
        super.onSaveFloatBoxState(bundle);
        String intentAction = null;
        if (shouldShowFloat) {
            intentAction = getIntent().getAction();
            bundle.putInt("mediaType", RongCallCommon.CallMediaType.AUDIO.getValue());
        }
        return intentAction;
    }

    @Override
    public void onBackPressed() {
        List<CallUserProfile> participantProfiles = callSession.getParticipantProfileList();
        RongCallCommon.CallStatus callStatus = null;
        for (CallUserProfile item : participantProfiles) {
            if (item.getUserId().equals(callSession.getSelfUserId())) {
                callStatus = item.getCallStatus();
                break;
            }
        }
        if (callStatus != null && callStatus.equals(RongCallCommon.CallStatus.CONNECTED)) {
            super.onBackPressed();
        } else {
            RongCallClient.getInstance().hangUpCall(callSession.getCallId());
        }
    }

    /*public void onEventMainThread(UserInfo userInfo) {
        TextView callerName = (TextView) maudioContainer.findViewWithTag(userInfo.getUserId() + "callerName");
        if (callerName != null && userInfo.getName() != null)
            callerName.setText(userInfo.getName());
        if (memberContainer != null && memberContainer.findChildById(userInfo.getUserId()) != null) {
            memberContainer.updateChildInfo(userInfo.getUserId(), userInfo);
        }
    }*/
}
