package io.rong.callkit;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
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
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
//import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;

import static cc.emw.mobile.R.id.all;
import static cc.emw.mobile.R.id.recyclerView;

public class MultiVideoCallActivity extends BaseCallActivity {
    private static final String TAG = "VoIPMultiVideoCallActivity";
    RongCallSession callSession;
    SurfaceView localView;
    FrameLayout localViewContainer;
    LinearLayout topContainer;
    LinearLayout bottomButtonContainer;
    LayoutInflater inflater;
    ImageView minimizeButton;
    ImageView addButton;
    ImageView switchCameraButton;

    boolean isFullScreen = false;
    boolean startForCheckPermissions = false;

    String localViewUserId;

    CallUserGridView memberContainer;
    LinearLayout memberLayout;
    private DisplayImageOptions options;
    VideoViewGroup videoViewGroup;

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.rc_voip_multi_video_call);
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象

        Intent intent = getIntent();
        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
//        RongContext.getInstance().getEventBus().register(this);
        if (!requestCallPermissions(RongCallCommon.CallMediaType.VIDEO, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
            return;
        }
        initViews();
        setupIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
        if (!requestCallPermissions(RongCallCommon.CallMediaType.VIDEO, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
            return;
        }
        initViews();
        setupIntent();
        super.onNewIntent(intent);
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                Map<String, Integer> mapPermissions = new HashMap<>();
                mapPermissions.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                mapPermissions.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++) {
                    mapPermissions.put(permissions[i], grantResults[i]);
                }
                if (mapPermissions.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && mapPermissions.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionGranted();
                    } else {
                        initViews();
                        setupIntent();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            if (PermissionCheckUtil.checkPermissions(this, VIDEO_CALL_PERMISSIONS)) {
                if (startForCheckPermissions) {
                    startForCheckPermissions = false;
                    RongCallClient.getInstance().onPermissionGranted();
                } else {
                    initViews();
                    setupIntent();
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
            setShouldShowFloat(true);
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
                            ToastUtil.showToast(MultiVideoCallActivity.this, "邀请失败:"+errorCode.getValue());
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
        super.onDestroy();
    }

    @Override
    public String onSaveFloatBoxState(Bundle bundle) {
        super.onSaveFloatBoxState(bundle);
        String intentAction = getIntent().getAction();
        bundle.putString("localViewUserId", localViewUserId);
        bundle.putString("callAction", RongCallAction.ACTION_RESUME_CALL.getName());
        bundle.putInt("mediaType", RongCallCommon.CallMediaType.VIDEO.getValue());
        bundle.putString("action", RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIVIDEO);
        return intentAction;
    }

    @Override
    public void onRestoreFloatBox(Bundle bundle) {
        super.onRestoreFloatBox(bundle);

        callSession = RongCallClient.getInstance().getCallSession();
        if (bundle != null) {
            memberLayout = (LinearLayout) findViewById(R.id.member);
            videoViewGroup = (VideoViewGroup) findViewById(R.id.video_view);
            memberLayout.setVisibility(View.GONE);
            videoViewGroup.setVisibility(View.VISIBLE);
            RongCallAction callAction = RongCallAction.valueOf(bundle.getString("callAction"));
            if (!callAction.equals(RongCallAction.ACTION_RESUME_CALL))
                return;
            localViewUserId = bundle.getString("localViewUserId");
            if (callSession == null || callSession.getParticipantProfileList() == null){
                setShouldShowFloat(false);
                finish();
            }

            boolean isLocalViewExist = false;
            for (CallUserProfile profile : callSession.getParticipantProfileList()) {
                if (profile.getUserId().equals(localViewUserId)) {
                    isLocalViewExist = true;
                    break;
                }
            }
            for (CallUserProfile profile : callSession.getParticipantProfileList()) {
                String currentUserId = RongIMClient.getInstance().getCurrentUserId();
                if (profile.getUserId().equals(localViewUserId)
                        || (!isLocalViewExist && profile.getUserId().equals(currentUserId))) {
                    localView = profile.getVideoView();
                    if (localView.getParent() != null) {
                        ((ViewGroup) localView.getParent()).removeAllViews();
                    }
                    localViewUserId = profile.getUserId();
                    localView.setZOrderOnTop(false);
                    localView.setZOrderMediaOverlay(false);
                    localViewContainer.addView(localView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    localView.setTag(localViewUserId);
                    TextView userNameView = (TextView) topContainer.findViewById(R.id.rc_voip_user_name);
                    userNameView.setTag(localViewUserId + "name");
                }
            }
            onCallConnected(callSession, null);
        }
    }

    @Override
    public void onCallOutgoing(final RongCallSession callSession, SurfaceView localVideo) {
        super.onCallOutgoing(callSession, localVideo);
        this.callSession = callSession;
        RongCallClient.getInstance().setEnableLocalAudio(true);
        RongCallClient.getInstance().setEnableLocalVideo(true);
        localView = localVideo;
        onOutgoingCallRinging();
        localViewContainer.addView(localView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        localViewUserId = RongIMClient.getInstance().getCurrentUserId();
        localView.setTag(localViewUserId);
    }

    @Override
    public void onRemoteUserJoined(String userId, RongCallCommon.CallMediaType mediaType, SurfaceView remoteVideo) {
        stopRing();

        videoViewGroup.updateChildView(userId, remoteVideo);
    }

    @Override
    public void onRemoteUserLeft(String userId, RongCallCommon.CallDisconnectedReason reason) {
        //incoming状态，localViewUserId为空
        if (localViewUserId == null)
            return;

        videoViewGroup.removeChildView(userId);
    }

    @Override
    public void onRemoteUserInvited(String userId, RongCallCommon.CallMediaType mediaType) {
        super.onRemoteUserInvited(userId, mediaType);

        if (callSession != null) {
            for (CallUserProfile profile : callSession.getParticipantProfileList()) {
                if (profile.getUserId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                    if (profile.getCallStatus().equals(RongCallCommon.CallStatus.CONNECTED)) {
                        videoViewGroup.addChildView(userId);
                    }
                }
            }
        }
    }

    @Override
    public void onCallConnected(RongCallSession callSession, SurfaceView localVideo) {
        super.onCallConnected(callSession, localVideo);
        this.callSession = callSession;
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            RongCallClient.getInstance().setEnableSpeakerphone(false);
        } else {
            RongCallClient.getInstance().setEnableSpeakerphone(true);
        }
        if (localView == null) {
            localView = localVideo;
            localViewContainer.addView(localView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            localViewUserId = RongIMClient.getInstance().getCurrentUserId();
            localView.setTag(localViewUserId);

//            videoViewGroup.addChildView(localViewUserId, localView);
        }
        localViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFullScreen) {
                    isFullScreen = true;
                    topContainer.setVisibility(View.GONE);
                    bottomButtonContainer.setVisibility(View.GONE);
                } else {
                    isFullScreen = false;
                    topContainer.setVisibility(View.VISIBLE);
                    bottomButtonContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        bottomButtonContainer.removeAllViews();
        FrameLayout bottomButtonLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_multi_video_calling_bottom_view, null);
        bottomButtonContainer.addView(bottomButtonLayout);
        topContainer.setVisibility(View.VISIBLE);
        minimizeButton.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.VISIBLE);
        switchCameraButton.setVisibility(View.VISIBLE);
        memberLayout.setVisibility(View.GONE);
        videoViewGroup.setVisibility(View.VISIBLE);
        TextView remindInfo = (TextView) findViewById(R.id.rc_voip_time);
        setupTime(remindInfo);
        videoViewGroup.addChildView(localViewUserId, localView);
        updateRemoteVideoViews(callSession);
    }

    void updateRemoteVideoViews(RongCallSession callSession) {
        for (CallUserProfile profile : callSession.getParticipantProfileList()) {
            String userId = profile.getUserId();

            if (userId.equals(localViewUserId))
                continue;
            SurfaceView video = profile.getVideoView();
            if (video != null) {
                View childView = videoViewGroup.findViewWithTag(userId);
                if (childView == null) {
                    videoViewGroup.addChildView(userId, video);
                } else {
                    videoViewGroup.updateChildView(userId, video);
                }
            } else {
                videoViewGroup.addChildView(userId, video);
            }
        }
    }

    @Override
    public void onCallDisconnected(RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason) {
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
            informationMessage = InformationNotificationMessage.obtain(RongContext.getInstance().getString(R.string.rc_voip_video_no_response));
        } else {
            informationMessage = InformationNotificationMessage.obtain(RongContext.getInstance().getString(R.string.rc_voip_video_ended));
        }
        RongIM.getInstance().insertMessage(callSession.getConversationType(), callSession.getTargetId(), callSession.getCallerUserId(), informationMessage, null);
        stopRing();
        postRunnableDelay(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

        super.onCallDisconnected(callSession, reason);
    }

    @Override
    public void onRemoteCameraDisabled(String userId, boolean muted) {
        if (muted) {
            if (userId.equals(localViewUserId)) {
                localView.setVisibility(View.GONE);
            } else {
//                remoteViewContainer.findViewWithTag(userId).setVisibility(View.GONE);
            }
        } else {
            if (userId.equals(localViewUserId)) {
                localView.setVisibility(View.VISIBLE);
            } else {
//                remoteViewContainer.findViewWithTag(userId).setVisibility(View.VISIBLE);
            }
        }
    }

    protected void initViews() {
        inflater = LayoutInflater.from(this);
        localViewContainer = (FrameLayout) findViewById(R.id.rc_local_user_view);
        topContainer = (LinearLayout) findViewById(R.id.rc_top_container);
        bottomButtonContainer = (LinearLayout) findViewById(R.id.rc_bottom_button_container);
        minimizeButton = (ImageView) findViewById(R.id.rc_voip_call_minimize);
        addButton = (ImageView) findViewById(R.id.rc_voip_call_add);
        switchCameraButton = (ImageView) findViewById(R.id.rc_voip_switch_camera);

        localView = null;
        localViewContainer.removeAllViews();

        minimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiVideoCallActivity.super.onMinimizeClick(v);
            }
        });
    }

    protected void setupIntent() {
        Intent intent = getIntent();
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
        if (callAction == null || callAction.equals(RongCallAction.ACTION_RESUME_CALL)) {
            return;
        }
        ArrayList<String> invitedList = new ArrayList<>();
        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");

            onIncomingCallRinging();
            TextView callRemindInfoView = (TextView) findViewById(R.id.rc_voip_call_remind_info);
            callRemindInfoView.setText(R.string.rc_voip_video_call_inviting);
            if (callSession != null) {
                CircleImageView userPortrait = (CircleImageView) findViewById(R.id.rc_voip_user_portrait);
                int uid = Integer.valueOf(callSession.getInviterUserId());
                UserInfo userInfo = EMWApplication.personMap.get(uid);
                if (userInfo != null) {
                    String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
                    if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                        url = Const.DOWN_ICON_URL2 + userInfo.Image;
                    }
                    userPortrait.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 62);
                    ImageLoader.getInstance().displayImage(url, userPortrait, options);
                    TextView name = (TextView) findViewById(R.id.rc_user_name);
                    name.setText(userInfo.Name);
                    name.setTag(callSession.getInviterUserId() + "callerName");
                } else {
                    userPortrait.setTextBg(EMWApplication.getIconColor(uid), "", 62);
                }
                List<CallUserProfile> list = callSession.getParticipantProfileList();
                for (CallUserProfile profile : list) {
//                    if (!profile.getUserId().equals(callSession.getSelfUserId()))
                        invitedList.add(profile.getUserId());
                }

                FrameLayout bottomButtonLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_incoming_button_layout, null);
                /*ImageView answerV = (ImageView) bottomButtonLayout.findViewById(R.id.rc_voip_call_answer_btn);
                answerV.setImageResource(R.drawable.rc_voip_vedio_answer_selector);*/
                bottomButtonContainer.addView(bottomButtonLayout);
                memberLayout = (LinearLayout) findViewById(R.id.member);
                videoViewGroup = (VideoViewGroup) findViewById(R.id.video_view);
                memberContainer = (CallUserGridView) findViewById(R.id.rc_voip_members_container);
                memberContainer.setChildPortraitSize(memberContainer.dip2pix(32));
                for (int i = 0; i < invitedList.size(); i++) {
                    if (invitedList.get(i).equals(callSession.getCallerUserId()))
                        continue;
                    userInfo = EMWApplication.personMap.get(Integer.valueOf(invitedList.get(i)));
                    memberContainer.addChild(invitedList.get(i), userInfo);
                }
            }

            topContainer.setVisibility(View.VISIBLE);
            minimizeButton.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            switchCameraButton.setVisibility(View.GONE);
            bottomButtonContainer.setVisibility(View.VISIBLE);
        } else if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            Conversation.ConversationType conversationType = Conversation.ConversationType.valueOf(intent.getStringExtra("conversationType").toUpperCase(Locale.US));
            String targetId = intent.getStringExtra("targetId");
            ArrayList<String> userIds = intent.getStringArrayListExtra("invitedUsers");
            for (int i = 0; i < userIds.size(); i++) {
                if (!userIds.get(i).equals(RongIMClient.getInstance().getCurrentUserId())) {
                    invitedList.add(userIds.get(i));
                }
            }
            TextView callRemindInfoView = (TextView) findViewById(R.id.rc_voip_call_remind_info);
            callRemindInfoView.setText(R.string.rc_voip_call_waiting);
            CircleImageView userPortrait = (CircleImageView) findViewById(R.id.rc_voip_user_portrait);
            UserInfo userInfo = EMWApplication.personMap.get(PrefsUtil.readUserInfo().ID);
            if (userInfo != null) {
                String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
                if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                    url = Const.DOWN_ICON_URL2 + userInfo.Image;
                }
                userPortrait.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 62);
                ImageLoader.getInstance().displayImage(url, userPortrait, options);
                TextView name = (TextView) findViewById(R.id.rc_user_name);
                name.setText(userInfo.Name);
            } else {
                userPortrait.setTextBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), "", 62);
            }
            RongCallClient.getInstance().startCall(conversationType, targetId, invitedList, RongCallCommon.CallMediaType.VIDEO, "multi");
            FrameLayout bottomButtonLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_multi_video_calling_bottom_view, null);
            bottomButtonLayout.findViewById(R.id.rc_voip_call_mute).setVisibility(View.GONE);
//            bottomButtonLayout.findViewById(R.id.rc_voip_disable_camera).setVisibility(View.GONE);
            bottomButtonLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
            bottomButtonLayout.findViewById(R.id.rc_voip_camera).setVisibility(View.GONE);
            bottomButtonContainer.addView(bottomButtonLayout);

            memberLayout = (LinearLayout) findViewById(R.id.member);
            videoViewGroup = (VideoViewGroup) findViewById(R.id.video_view);
            memberContainer = (CallUserGridView) findViewById(R.id.rc_voip_members_container);
            memberContainer.setChildPortraitSize(memberContainer.dip2pix(32));
            for (int i = 0; i < invitedList.size(); i++) {
                if (invitedList.get(i).equals(Integer.toString(PrefsUtil.readUserInfo().ID)))
                    continue;
                userInfo = EMWApplication.personMap.get(Integer.valueOf(invitedList.get(i)));
                memberContainer.addChild(invitedList.get(i), userInfo);
            }

            topContainer.setVisibility(View.GONE);
            bottomButtonContainer.setVisibility(View.VISIBLE);
        }
    }

    public void onHangupBtnClick(View view) {
        if (callSession == null || isFinishing) {
            return;
        }
        stopRing();
        RongCallClient.getInstance().hangUpCall(callSession.getCallId());
    }

    public void onReceiveBtnClick(View view) {
        if (callSession == null || isFinishing) {
            return;
        }
        RongCallClient.getInstance().acceptCall(callSession.getCallId());
        RongCallClient.getInstance().setEnableLocalAudio(true);
        RongCallClient.getInstance().setEnableLocalVideo(true);
        stopRing();
    }

    public void onAddButtonClick(View view) {
        setShouldShowFloat(false);

        if (callSession.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) {
            RongIMClient.getInstance().getDiscussion(callSession.getTargetId(), new RongIMClient.ResultCallback<Discussion>() {
                @Override
                public void onSuccess(Discussion discussion) {
                    Intent intent = new Intent(MultiVideoCallActivity.this, CallSelectMemberActivity.class);
                    ArrayList<String> added = new ArrayList<>();
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
//                    intent.putStringArrayListExtra("allMembers", (ArrayList<String>) discussion.getMemberIdList());
                    intent.putStringArrayListExtra("invitedMembers", added);
                    intent.putExtra("mediaType", RongCallCommon.CallMediaType.VIDEO.getValue());
                    startActivityForResult(intent, 110);
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        } else if (callSession.getConversationType().equals(Conversation.ConversationType.GROUP)) {
            Intent intent = new Intent(MultiVideoCallActivity.this, CallSelectMemberActivity.class);
            ArrayList<String> added = new ArrayList<>();
            List<CallUserProfile> list = RongCallClient.getInstance().getCallSession().getParticipantProfileList();
            for (CallUserProfile profile : list) {
                added.add(profile.getUserId());
            }
            intent.putStringArrayListExtra("invitedMembers", added);
            intent.putExtra("groupId", callSession.getTargetId());
            intent.putExtra("mediaType", RongCallCommon.CallMediaType.VIDEO.getValue());
            startActivityForResult(intent, 110);
        }
    }

    public void onSwitchCameraClick(View view) {
        RongCallClient.getInstance().switchCamera();
    }

    public void onMuteButtonClick(View view) {
        RongCallClient.getInstance().setEnableLocalAudio(view.isSelected());
        view.setSelected(!view.isSelected());
    }

    public void onHandFreeButtonClick(View view) {
        RongCallClient.getInstance().setEnableSpeakerphone(!view.isSelected());
        view.setSelected(!view.isSelected());
    }

    /*public void onDisableCameraBtnClick(View view) {
        TextView text = (TextView) bottomButtonContainer.findViewById(R.id.rc_voip_disable_camera_text);
        String currentUserId = RongIMClient.getInstance().getCurrentUserId();

        RongCallClient.getInstance().setEnableLocalVideo(view.isSelected());
        if (view.isSelected()) {
            text.setText(R.string.rc_voip_disable_camera);
            if (localViewUserId.equals(currentUserId)) {
                localView.setVisibility(View.VISIBLE);
            } else {
                remoteViewContainer.findViewWithTag(currentUserId).setVisibility(View.VISIBLE);
            }
        } else {
            text.setText(R.string.rc_voip_enable_camera);
            if (localViewUserId.equals(currentUserId)) {
                localView.setVisibility(View.GONE);
            } else {
                remoteViewContainer.findViewWithTag(currentUserId).setVisibility(View.GONE);
            }
        }
        view.setSelected(!view.isSelected());
    }*/

    public void onSwitchRemoteUsers(View view) {
        String from = (String) view.getTag();
        if (from == null)
            return;
        String to = (String) localView.getTag();
        FrameLayout layout = (FrameLayout) view;
        SurfaceView fromView = (SurfaceView) layout.getChildAt(0);
        SurfaceView toView = localView;

        localViewContainer.removeAllViews();
        layout.removeAllViews();

        /*View singleRemoteView = remoteViewContainer.findViewWithTag(from + "view");
        UserInfo toUserInfo = RongContext.getInstance().getUserInfoFromCache(to);
        UserInfo fromUserInfo = RongContext.getInstance().getUserInfoFromCache(from);*/

        /*AsyncImageView userPortraitView = (AsyncImageView) singleRemoteView.findViewById(R.id.user_portrait);
        TextView backUserNameView = (TextView) singleRemoteView.findViewById(R.id.user_name);

        if (toUserInfo != null) {
            if (toUserInfo.getPortraitUri() != null) {
                userPortraitView.setAvatar(toUserInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
            }
            backUserNameView.setText(toUserInfo.getName());
        } else {
            backUserNameView.setText(to);
        }
        singleRemoteView.setTag(to + "view");*/
        fromView.setZOrderOnTop(false);
        fromView.setZOrderMediaOverlay(false);
        localViewContainer.addView(fromView);
        toView.setZOrderOnTop(true);
        toView.setZOrderMediaOverlay(true);
        layout.addView(toView);

        TextView topUserNameView = (TextView) topContainer.findViewById(R.id.rc_voip_user_name);
        topUserNameView.setTag(from + "name");
        /*if (fromUserInfo != null) {
            topUserNameView.setText(fromUserInfo.getName());
        } else {
            topUserNameView.setText(from);
        }*/
        layout.setTag(to);
        localView = fromView;
        localView.setTag(from);
        localViewUserId = from;
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
        if (participantPortraitContainer.getVisibility() == View.VISIBLE) {
            View participantView = participantPortraitContainer.findViewWithTag(userInfo.getUserId() + "participantPortraitView");
            if (participantView != null && userInfo.getPortraitUri() != null) {
                AsyncImageView portraitView = (AsyncImageView) participantView.findViewById(R.id.rc_user_portrait);
                portraitView.setAvatar(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
            }
        }
        if (remoteViewContainer.getVisibility() == View.VISIBLE) {
            View remoteView = remoteViewContainer.findViewWithTag(userInfo.getUserId() + "view");
            if (remoteView != null && userInfo.getPortraitUri() != null) {
                AsyncImageView portraitView = (AsyncImageView) remoteView.findViewById(R.id.rc_user_portrait);
                portraitView.setAvatar(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
            }
        }
        if (topContainer.getVisibility() == View.VISIBLE) {
            TextView nameView = (TextView) topContainer.findViewWithTag(userInfo.getUserId() + "name");
            if (nameView != null && userInfo.getName() != null)
                nameView.setText(userInfo.getName());
        }
    }*/
}
