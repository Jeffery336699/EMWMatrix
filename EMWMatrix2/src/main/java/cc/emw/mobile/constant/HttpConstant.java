package cc.emw.mobile.constant;

public class HttpConstant {

	public static final String BASE_URL = "http://10.0.10.59:8080";// 10.0.10.61:8082
	public static final String LOGIN_URL = "http://10.0.10.59:8081/account/signin";// 10.0.10.61:8081
	public static final String HOME_URL = BASE_URL + "/TalkerAPI/LoadTalker";
	public static final String CONTACT_URL = BASE_URL + "/UserAPI/SearchUser";
	public static final String ICON_URL = BASE_URL
			+ "/Resource/%s/UserImage/%s"; // 头像URL
	public static final String FILE_URL = BASE_URL + "/Resource/%s/UserFile/%s"; // 文件URL
	public static final String FILE_LIST_URL = BASE_URL
			+ "/UserAPI/GetFileCountByType"; // 文件数量URL
	// //
	// ////////////////////////////////////////////////////////////////////////////////
	public static final String DOFOLLOW_URL = BASE_URL + "/UserAPI/DoFollow"; // 添加关注
	public static final String GROUP_URL = BASE_URL
			+ "/TalkerAPI/LoadGroups?isall=true"; // 群主列表
	public static final String NO_DEAL_TASK = BASE_URL + "/TalkerAPI/GetTask"; // 待处理工作;
	public static final String ADD_IN_GROUP = BASE_URL
			+ "/TalkerAPI/AddGroupUser";// 加入群组的接口

	public static final String DEL_FROM_GROUP = BASE_URL
			+ "/TalkerAPI/DelGroupUser"; // 根据群组ID删除指定用户
	public static final String DEL_GROUP = BASE_URL + "/TalkerAPI/DelGroup";// 根据群组id删除群组

	public static final String GET_GROUP_MEMBER = BASE_URL
			+ "/TalkerAPI/LoadGroupUsersByGid";// 获取群主人员信息
	public static final String MYINFO_COUNT = BASE_URL
			+ "/TalkerAPI/GetMyTalkCount";// 我发布、收藏、相关到我
	public static final String ALLRECORD_TALKER_USER = BASE_URL
			+ "/TalkerAPI/LoadTalker";// 我发布的
	public static final String SUGGESTION_SUBMIT = BASE_URL
			+ "/UserAPI/AddFeedBack";// 提交意见反馈
	public static final String BEIZHU_ADD = BASE_URL + "/UserAPI/DoUserMark";// 添加/修改用户备注
	public static final String BEIZHU_GET = BASE_URL + "/UserAPI/GetUserMark";// 获取用户备注
	public static final String CHANGE_PW = BASE_URL + "/UserAPI/ModifyPassWord";// 修改用户密码
	// ///////////////////////////////////////////////////////////////////////////////
	public static final String UN_COMPLETE_TASK = BASE_URL
			+ "/TalkerAPI/GetTaskByState";// 根据任务状态获取任务信息

	public static final String GET_CALENDAR_INFO = BASE_URL
			+ "/TalkerAPI/GetCalenderListByTimeSpan"; // 根据时间区间获取时间信息
	public static final String CREATE_GROUP = BASE_URL
			+ "/TalkerAPI/SaveChatterGroup";

	public static final String TASK_GET_BYSTATE = BASE_URL
			+ "/TalkerAPI/GetTaskByState";// 根据用户ID获取任务
	public static final String GET_PROJECT = BASE_URL + "/TalkerAPI/GetProject";// 获取项目
	public static final String GET_SPRINT = BASE_URL + "/TalkerAPI/GetSprint";// 获取冲刺
	public static final String PROJECT_DO_PROJECT = BASE_URL
			+ "/TalkerAPI/DoProject";// 添加或修改项目
	public static final String PROJECT_GET_ALL_SCHEDULE = BASE_URL
			+ "/TalkerAPI/GetAllCalenderList";// 获得所有日程
	public static final String GET_TASKS_BY_IDS = BASE_URL
			+ "/TalkerAPI/GetTaskByIds";// 根据id字符串获取任务
	public static final String ADD_SPRINT = BASE_URL + "/TalkerAPI/AddSprint";// 添加冲刺
	public static final String SEND_CALENDAR = BASE_URL
			+ "/TalkerAPI/AddCalendar";// 发布日程接口
	public static final String FINISH_CALENDAR = BASE_URL
			+ "/TalkerAPI/FinishCalendar";// 完成日程
	public static final String EDIT_CALENDAR = BASE_URL
			+ "/TalkerAPI/ModifySchedule";

	public static final String GET_REPLY = BASE_URL
			+ "/TalkerAPI/GetTaskReplyByTaskId";// 根据任务ID获取任务评论
	public static final String SAVE_REPLY = BASE_URL + "/TalkerAPI/DoTaskReply";// 保存评论
	public static final String DELETE_REPLY = BASE_URL
			+ "/TalkerAPI/DelTaskReply"; // 删除评论

	public static final String REVLIST_URL = BASE_URL
			+ "/TalkerAPI/getTalkerRevByTalkerId"; // 评论列表
	public static final String FOLDLIST_URL = BASE_URL
			+ "/UserData/GetFoldList"; // 知识库文件夹列表
	public static final String DELFILE_URL = BASE_URL + "/UserData/delFile"; // 删除文件
	public static final String SAVEFOLDER_URL = BASE_URL
			+ "/UserData/SaveFoldInfo";
	public static final String REPLY_URL = BASE_URL
			+ "/TalkerAPI/SaveTalkerRev"; // 回复
	public static final String UPLOADFILE_URL = BASE_URL + "/upload"; // 上传文件

	// 根据任务状态获取所有项目任务
	public static final String GETPROJECTS_BY_TASK_STATE = BASE_URL
			+ "/TalkerAPI/GetMobileProjectByTaskState"; // 根据任务状态获取所有项目

	public static final String MESS_URL = BASE_URL + "/Message/assignserver";// 获取连接URL
	public static final String CHATHIS_URL = BASE_URL
			+ "/message/GetChatMessages/"; // 获取聊天记录消息
	public static final String CHATHISTTROY_URL = BASE_URL
			+ "/message/GetChatRecords";// 获取聊天列表消息CHATHISTTROY_URL

	public static final String SENDMESG_URL = "http://10.0.10.59:8080/Message/Send";// 发送消息
	public static final String ABOUTME_URL = BASE_URL
			+ "/UserAPI/GetUserInfoByID/";// 获取个人相关信息
	public static final String DLEMES_URL = BASE_URL
			+ "/message/RemoveNewMessageBySenderID/";// 删除聊天记录信息

	public static final String MODIFY_TASK_URL = BASE_URL
			+ "/TalkerAPI/ModifyTask";// 编辑修改任务实例
	public static final String CREATE_TASK_URL = BASE_URL
			+ "/TalkerAPI/AddFenPai";// 创建任务实例
	public static final String DOWNLOAD_FILE = BASE_URL + "/DownLoad/";// 任务附件下载
	public static final String SHARE_FILE = BASE_URL + "/UserData/DoFilePower";// 文件分享
	public static final String ADDSPRINTTASK = BASE_URL
			+ "/TalkerAPI/AddSprintTask";// 添加任务冲刺
	public static final String IMAGEUPLOAD_URL = BASE_URL + "/ImageUpload";// 图片上传
	public static final String AUDIO_URL = BASE_URL + "/UploadAudio";// 音频上传
}
