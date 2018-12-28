package cc.emw.mobile.chat.base;

/**
 * Created by sunny.du on 2017/1/13.
 */
public interface ChatContent {
    /***************************************************************************************************
     * ***************************************************************************************************
     * ********************************************智能聊天常量定义***************************************
     * ***************************************************************************************************
     * *消息类型
     ******************************************************************************************/
    int DEFAULT_MSG = 36;//普通消息
    int SCHEDULE_MSG = 37;//日程相关信息
    int CHAT_LOCATION = 38;//定位消息
    int CHAT_SHARE_LOCATION = 41;//共享位置
    int CHAT_UNSHARE_LOCATION = 42;//停止共享地理位置
    int ITEM_MSG = 0;//机器人普通消息
    int ITEM_IMAGE_MSG = 1;//图片列表展示
    int MAP_MSG = 2;//地图相关信息
    int DYNAMIC = 39;//动态消息


    int WEB_MSG = 1;//网页相关信息
    int LIST_MSG = 3;//列表信息
    int WEATHER_MSG = 5;//天气消息
    int IMPROTANCE_MSG = 1001;//天气消息


    int NORMAL_MSG = 4;//普通消息
    int AUDIO_MSG = 8;//音频信息
    int IMAGE_MSG = 7;//图片消息
    int VIDEO_MSG = 9;//视频
    /**
     * 子列表消息类型 LIST_MAP LIST_IMAGE LIST_SCHEDULE LIST_TRAVEL
     *********************************************************************************/
    int LIST_MAP = 1;
    int LIST_IMAGE = 2;
    int LIST_SCHEDULE = 3;
    int LIST_TRAVEL = 4;
    /**
     * 消息接收/发送类型
     *********************************************************************************/
    int TYPE_ITEM_RECE = 0;//接收到的消息
    int TYPE_ITEM_SEND = 1;//发送的消息
    /**
     * 语音功能相关
     *********************************************************************************/
    int AUDIO_DEVICE_SPEEKER = 9; //扬声器
    int AUDIO_DEVICE_EAR = 10; //听筒

    int ITEM_TITLE = 0; //条目头部
    int ITEM_STATIC = 1; //条目内容


    /**
     * 正则表达式
     *********************************************************************************/
    String EDIT_CHAT_AI = "@|@(E|e)|@(E|e)(M|m)|@(E|e)(M|m)(W|w)";
    String CONSULT_EMW = "@EMW ";//发送消息给机器人


    /**********************************************
     *发送消息 成功/失败  的标记
     */
    int SEND_MSG_ERROR = 1;//发送消息失败
    int SEND_MSG_SUCCESS = 0;//发送消息成功
    int SEND_MSG_STARTED = 3;//发送消息中。。。。


    /**********************************************
     *广播字段
     */
    String loopSendMessageBean = "LOOP_SEND_MESSAGE_BEAN";
    String REFRESH_CHAT_CAMARE_NEW_INFO = "refresh_chat_camare_new_info";//全屏照片传递新照片url的广播字段
    String REFRESH_CHAT_VIDEO_NEW_INFO = "refresh_chat_video_new_info";//全屏视频传递新照片url的广播字段
    String REFRESH_CHAT_COLSE_PHOTO_INFO = "refresh_chat_colse_photo_info";//关闭相机和照片栏的广播字段
    String REFRESH_CHAT_OPEN_FILE_DIR_INFO = "refresh_chat_open_file_dir_info";//文件目录操作广播字段
    String ADD_USER_TO_NOTE="add_user_to_note";
    String REFRESH_CHAT_TEAM_INFO = "refresh_chat_team_info";//刷新数据的广播  sunnydu
    String REFRESH_CHAT_VIDEO_INFO = "refresh_chat_video_info";//刷新视频数据的广播  sunnydu
    String REFRESH_CHAT_IMAGE_INFO = "refresh_chat_image_info";//刷新图片数据的广播  sunnydu
    String REFRESH_CHAT_PHOTO_INFO = "refresh_chat_photo_info";//触发上传图片的广播  sunnydu
    String REFRESH_CHAT_CAMARE_INFO = "refresh_chat_camare_info";//打开相机的广播  sunnydu
    String REFRESH_CHAT_OPEN_FILE = "refresh_chat_open_file";//打开文件的广播  sunnydu
    String REFRESH_CHAT_OPEN_MAP = "refresh_chat_open_map";//打开共享位置的广播  taozrjt
    String REFRESH_CHAT_CHANGE_MSG = "refresh_chat_change_msg";//变更消息状态为重要状态  sunnydu
    String REFRESH_CHAT_UPDATE_MSG = "refresh_chat_update_msg";//收藏消息  sunnydu
    String REFRESH_CHAT_COLSE_IMPROTANCE_MSG = "refresh_chat_colse_improtance_msg";//关闭群组重要消息  sunnydu
    String REFRESH_CHAT_COLSE_MORE_VIEW = "refresh_chat_colse_more_view";//关闭群组重要消息  sunnydu
    String REFRESH_CHAT_RECEIVED_MSG = "refresh_chat_received_msg";//关闭群组重要消息  sunnydu
    String OPEN_VIDEO_CHAT = "open_video_chat";
    String SEND_SHRRE_POS_MESSAGE = "send_share_pos_message";
    String SEND_UNSHARE_POS_MESSAGE = "send_un_share_pos_message";


    int CHAT_REQUESTCODE_MAINUSER = 8001;//创建chatg模块专属的动态相关字段   负责人
    int CHAT_REQUESTCODE_CALLUSER = 8002;//创建chatg模块专属的动态相关字段   参与人
    /**********************************************
     * 控制点击事件循环的事件
     */
    int MIN_CLICK_DELAY_TIME = 1000;
    int SYSTEM_IMAGE = 112;


    int VIDEO_REQUEST_CODE = 0;//视频返回activity成功的标识
    int REQUEST_CAPTURE_PHOTO = 1002;//设置群组图片 跳转图片选择界面返回数据的标识
    int RECORD_TIME_MAX = 10000;//设置视频进度条的最大进度


    /***********************************************************************
     *TestActivity  不知道干什么用的一些字段
     */
    String ACTION_CHATS = "refresh_chats";
    int M1 = 1;
    int M2 = 2;
    int M3 = 3;
    int COMMENT = 4;
    int ENJOY = 5;

    /***********************************************************************
     *常量字段
     */
    String REMOTE_VIDEO_FOLDER = "http://10.0.10.80:8000/Resource/emw/UserVideo/";
}
