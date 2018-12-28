package cc.emw.mobile.net;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class API {
	public static class JsonSplit {
		/**
		 * 判断是不是json或json数组格式开头
		 * @param json
		 * @param cb 回调
		 */
		public static Callback.Cancelable IsJson(String json,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(json);
			return Const.post("JsonSplit/IsJson",paramList,cb);
		}
	}
	public static class DeptService {
		/**
		 * 获取所有API方法
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetAll(RequestCallback cb) {
			return Const.get("DeptService/GetAll",cb);
		}
	}
	public static class UserAPI {
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetAllDpt(RequestCallback cb) {
			return Const.get("UserAPI/GetAllDpt",cb);
		}
		/**
		 * @param key
		 * @param deptid
		 * @param follow
		 * @param cb 回调
		 */
		public static Callback.Cancelable SearchUser(String key,int deptid,boolean follow,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("key",key);
			paramMap.put("deptid",deptid);
			paramMap.put("follow",follow);
			return Const.get("UserAPI/SearchUser",paramMap,cb);
		}
		/**
		 * 根据用户姓名查询用户坐标
		 * @param username 用户姓名
		 * @param cb 回调
		 */
		public static Callback.Cancelable getUserByName(String username,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(username);
			return Const.get("UserAPI/getUserByName",paramList,cb);
		}
		/**
		 * 获取全部用户坐标
		 * @param userid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserAxisList(int userid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("userid",userid);
			return Const.get("UserAPI/GetUserAxisList",paramMap,cb);
		}
		/**
		 * 更新用户信息
		 * @param ui 用户信息对象
		 * @param cb 回调
		 */
		public static Callback.Cancelable ModifyUserById(ApiEntity.UserInfo ui,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ui",ui);
			return Const.get("UserAPI/ModifyUserById",paramMap,cb);
		}
		/**
		 * 根据用户id更新坐标位置
		 * @param Axis 坐标位置值
		 * @param cb 回调
		 */
		public static Callback.Cancelable ModifyUserAxisById(String Axis,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("Axis",Axis);
			return Const.get("UserAPI/ModifyUserAxisById",paramMap,cb);
		}
		/**
		 * 保存关注
		 * @param fuids 用户id串
		 * @param type 1增加关注，2取消关注
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoFollow(List<Integer> fuids,int type,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fuids",fuids);
			paramMap.put("type",type);
			return Const.post("UserAPI/DoFollow",paramMap,cb);
		}
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable getFollowUserList(RequestCallback cb) {
			return Const.get("UserAPI/getFollowUserList",cb);
		}
		/**
		 * 登录功能
		 * @param uid
		 * @param key
		 * @param returnUrl
		 * @param cb 回调
		 */
		public static Callback.Cancelable SignIn(int uid,String key,String returnUrl,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("uid",uid);
			paramMap.put("key",key);
			paramMap.put("returnUrl",returnUrl);
			return Const.get("signin",paramMap,cb);
		}
		/**
		 * 获取当前登录用户信息
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetIdentity(RequestCallback cb) {
			return Const.get("UserAPI/GetIdentity",cb);
		}
		/**
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserInfoByID(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("UserAPI/GetUserInfoByID",paramList,cb);
		}
		/**
		 * 修改用户密码
		 * @param oldPwd 旧密码
		 * @param newPwd 新密码
		 * @param cb 回调
		 */
		public static Callback.Cancelable ModifyPassWord(String oldPwd,String newPwd,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("oldPwd",oldPwd);
			paramMap.put("newPwd",newPwd);
			return Const.post("UserAPI/ModifyPassWord",paramMap,cb);
		}
		/**
		 * @param token
		 * @param cb 回调
		 */
		public static Callback.Cancelable UpdateDeviceToken(String token,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(token);
			return Const.get("UserAPI/UpdateDeviceToken",paramList,cb);
		}
		/**
		 * 添加意见反馈
		 * @param ufb 意见反馈对象
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddFeedBack(ApiEntity.UserFeedBack ufb,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ufb",ufb);
			return Const.post("UserAPI/AddFeedBack",paramMap,cb);
		}
		/**
		 * 删除意见反馈
		 * @param id 记录id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelFeedBack(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.post("UserAPI/DelFeedBack",paramMap,cb);
		}
		/**
		 * 获取意见反馈[uid=0全部，否则传用户id]
		 * @param uid 用户id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFeedBack(int uid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("uid",uid);
			return Const.get("UserAPI/GetFeedBack",paramMap,cb);
		}
		/**
		 * 添加、修改用户备注
		 * @param um 用户备注对象
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoUserMark(ApiEntity.UserMark um,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("um",um);
			return Const.post("UserAPI/DoUserMark",paramMap,cb);
		}
		/**
		 * 删除用户备注
		 * @param id 记录id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelUserMark(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.post("UserAPI/DelUserMark",paramMap,cb);
		}
		/**
		 * 获取用户备注
		 * @param um 用户备注对象
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserMark(ApiEntity.UserMark um,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("um",um);
			return Const.post("UserAPI/GetUserMark",paramMap,cb);
		}
	}
	public static class Message {
		/**
		 * http的基本post方法
		 * @param message
		 * @param user
		 * @param cb 回调
		 */
		public static Callback.Cancelable Post(ApiEntity.Message message,ApiEntity.UserInfo user,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("message",message);
			paramMap.put("user",user);
			return Const.post("Message/Post",paramMap,cb);
		}
		/**
		 * 发送短信
		 * @param message
		 * @param saveTwo
		 * @param cb 回调
		 */
		public static Callback.Cancelable Send(ApiEntity.Message message,boolean saveTwo,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("message",message);
			paramMap.put("saveTwo",saveTwo);
			return Const.post("Message/Send",paramMap,cb);
		}
		/**
		 * @param message
		 * @param cb 回调
		 */
		public static Callback.Cancelable SendGroup(ApiEntity.Message message,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("message",message);
			return Const.post("Message/SendGroup",paramMap,cb);
		}
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable AssignEndPoint(RequestCallback cb) {
			return Const.get("Message/assignserver",cb);
		}
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetOnlines(RequestCallback cb) {
			return Const.get("Message/GetOnlines",cb);
		}
		/**
		 * @param senderID
		 * @param pageIndex
		 * @param pageSize
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetChatMessages(int senderID,int pageIndex,int pageSize,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(senderID);
			paramList.add(pageIndex);
			paramList.add(pageSize);
			return Const.get("Message/GetChatMessages",paramList,cb);
		}
		/**
		 * @param groupID
		 * @param pageIndex
		 * @param pageSize
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetGroupMessages(String groupID,int pageIndex,int pageSize,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupID);
			paramList.add(pageIndex);
			paramList.add(pageSize);
			return Const.get("Message/GetGroupMessages",paramList,cb);
		}
		/**
		 * @param pageIndex
		 * @param pageSize
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetAllGroupMessages(int pageIndex,int pageSize,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(pageIndex);
			paramList.add(pageSize);
			return Const.get("Message/GetAllGroupMessages",paramList,cb);
		}
		/**
		 * 获取用户新消息
		 * @param uid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetReceiveMessages(int uid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(uid);
			return Const.get("Message/GetReceiveMessages",paramList,cb);
		}
		/**
		 * @param uid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetLastMessages(int uid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(uid);
			return Const.get("Message/GetLastMessages",paramList,cb);
		}
		/**
		 * @param sender
		 * @param pageIndex
		 * @param pageCount
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetBakMessages(int sender,int pageIndex,int pageCount,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(sender);
			paramList.add(pageIndex);
			paramList.add(pageCount);
			return Const.get("Message/GetBakMessages",paramList,cb);
		}
		/**
		 * @param senderID
		 * @param cb 回调
		 */
		public static Callback.Cancelable RemoveNewMessageBySenderID(int senderID,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(senderID);
			return Const.get("Message/RemoveNewMessageBySenderID",paramList,cb);
		}
		/**
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable RemoveMessageByID(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("Message/RemoveMessageByID",paramList,cb);
		}
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetChatRecords(RequestCallback cb) {
			return Const.get("Message/GetChatRecords",cb);
		}
		/**
		 * @param receiverID
		 * @param cb 回调
		 */
		public static Callback.Cancelable RemoveChatRecord(int receiverID,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(receiverID);
			return Const.get("Message/RemoveChatRecord",paramList,cb);
		}
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetNewMessageCount(RequestCallback cb) {
			return Const.get("Message/GetNewMessageCount",cb);
		}
	}
	public static class TalkerAPI {
		/**
		 * 收藏/取消收藏talker
		 * @param nid talker的记录id
		 * @param flag 0收藏，1取消收藏
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoEnjoyTalker(int nid,int flag,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("nid",nid);
			paramMap.put("flag",flag);
			return Const.post("TalkerAPI/DoEnjoyTalker",paramMap,cb);
		}
		/**
		 * 发布Talker/发布项目组Message【需传TeamId】
		 * @param note 发布Talker的JSON对象UserNote
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveTalker(ApiEntity.UserNote note,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("note",note);
			return Const.post("TalkerAPI/SaveTalker",paramMap,cb);
		}
		/**
		 * 获取项目组【Message】消息
		 * @param teamID 项目组id
		 * @param s 页码
		 * @param size 一页的记录数
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadTeamTalker(int teamID,int s,int size,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("teamID",teamID);
			paramMap.put("s",s);
			paramMap.put("size",size);
			return Const.get("TalkerAPI/LoadTeamTalker",paramMap,cb);
		}
		/**
		 * 获取talker记录
		 * @param loadtype talk类型，0加载全部类型(loadtype--0所有 1我关注的 2提到我的 3我发布的 4我的小组 5某人发布的 6某个 7某个类型的 9图片 10投票 11连接 12文件)
		 * @param id 暂时不用，传0
		 * @param s 页码
		 * @param size 一页的记录数
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadTalker(int loadtype,int id,int s,int size,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("loadtype",loadtype);
			paramMap.put("id",id);
			paramMap.put("s",s);
			paramMap.put("size",size);
			return Const.get("TalkerAPI/LoadTalker",paramMap,cb);
		}
		/**
		 * 保存Talker回复
		 * @param rev 保存回复的JSON对象UserNote
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveTalkerRev(ApiEntity.UserNote rev,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("rev",rev);
			return Const.post("TalkerAPI/SaveTalkerRev",paramMap,cb);
		}
		/**
		 * 根据id删除Talker
		 * @param id 记录id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteTalker(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.post("TalkerAPI/DeleteTalker",paramMap,cb);
		}
		/**
		 * 根据id和父id删除Talker回复
		 * @param id 记录id
		 * @param pid 父id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteTalkerRev(int id,int pid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			paramMap.put("pid",pid);
			return Const.post("TalkerAPI/DeleteTalkerRev",paramMap,cb);
		}
		/**
		 * 根据id修改talker或者项目组消息
		 * @param un Talker对象
		 * @param cb 回调
		 */
		public static Callback.Cancelable ModifyNewTalkerById(ApiEntity.UserNote un,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("un",un);
			return Const.post("TalkerAPI/ModifyNewTalkerById",paramMap,cb);
		}
		/**
		 * 根据id修改talker属性值
		 * @param pro 新的属性值
		 * @param id 记录id
		 * @param cb 回调
		 */
		public static Callback.Cancelable ModifyTalkerById(String pro,int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("pro",pro);
			paramMap.put("id",id);
			return Const.post("TalkerAPI/ModifyTalkerById",paramMap,cb);
		}
		/**
		 * 根据Typeid获取talker信息
		 * @param Property 要修改的任务、计划实体
		 * @param TypeId 根据Typeid获取talker信息
		 * @param cb 回调
		 */
		public static Callback.Cancelable ModifyTalkerByTypeId(String Property,int TypeId,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("Property",Property);
			paramMap.put("TypeId",TypeId);
			return Const.post("TalkerAPI/ModifyTalkerByTypeId",paramMap,cb);
		}
		/**
		 * 根据Typeid获取talker信息
		 * @param TypeId 根据Typeid获取talker信息
		 * @param cb 回调
		 */
		public static Callback.Cancelable getTalkerByTypeId(int TypeId,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("TypeId",TypeId);
			return Const.get("TalkerAPI/getTalkerByTypeId",paramMap,cb);
		}
		/**
		 * 根据记录id获取talker信息
		 * @param id 记录id
		 * @param cb 回调
		 */
		public static Callback.Cancelable getTalkerById(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.get("TalkerAPI/getTalkerById",paramMap,cb);
		}
		/**
		 * 根据记录id获取talker回复
		 * @param talkerid 记录id
		 * @param cb 回调
		 */
		public static Callback.Cancelable getTalkerRevByTalkerId(int talkerid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("talkerid",talkerid);
			return Const.get("TalkerAPI/getTalkerRevByTalkerId",paramMap,cb);
		}
		/**
		 * 根据父id获取投票回复
		 * @param PId 父id
		 * @param cb 回调
		 */
		public static Callback.Cancelable getVoteRevByPId(int PId,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("PId",PId);
			return Const.get("TalkerAPI/getVoteRevByPId",paramMap,cb);
		}
		/**
		 * 增加多个群组成员
		 * @param gid 群组id
		 * @param userids 要添加的用户id列表
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddGroupUsers(int gid,List<Integer> userids,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("gid",gid);
			paramMap.put("userids",userids);
			return Const.post("TalkerAPI/AddGroupUsers",paramMap,cb);
		}
		/**
		 * 增加多个群组成员
		 * @param gid 群组id
		 * @param userid
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddGroupUser(int gid,int userid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("gid",gid);
			paramMap.put("userid",userid);
			return Const.post("TalkerAPI/AddGroupUser",paramMap,cb);
		}
		/**
		 * 删除群组成员
		 * @param gid 群组id
		 * @param userid 要删除的用户id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelGroupUser(int gid,int userid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("gid",gid);
			paramMap.put("userid",userid);
			return Const.post("TalkerAPI/DelGroupUser",paramMap,cb);
		}
		/**
		 * 删除多个群组成员
		 * @param gid 群组id
		 * @param useridList 要删除的用户id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelMoreGroupUser(int gid,List<Integer> useridList,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("gid",gid);
			paramMap.put("useridList",useridList);
			return Const.post("TalkerAPI/DelMoreGroupUser",paramMap,cb);
		}
		/**
		 * 删除群组成员
		 * @param gid 群组id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelGroup(int gid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("gid",gid);
			return Const.post("TalkerAPI/DelGroup",paramMap,cb);
		}
		/**
		 * 增加群组
		 * @param group 群组信息的JSON对象ChatterGroup
		 * @param userids List=int添加其他成员
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveChatterGroup(ApiEntity.ChatterGroup group,List<Integer> userids,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("group",group);
			paramMap.put("userids",userids);
			return Const.post("TalkerAPI/SaveChatterGroup",paramMap,cb);
		}
		/**
		 * 加载群组
		 * @param name 群组名称的关键字
		 * @param isAll 是否加载全部（true加载全部，false加载我的群组）
		 * @param teamType 0普通群组，1项目组
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadGroups(String name,boolean isAll,int teamType,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("name",name);
			paramMap.put("isAll",isAll);
			paramMap.put("teamType",teamType);
			return Const.get("TalkerAPI/LoadGroups",paramMap,cb);
		}
		/**
		 * 根据群组id获取群组人员
		 * @param gid 群组id
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadGroupUsersByGid(int gid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("gid",gid);
			return Const.get("TalkerAPI/LoadGroupUsersByGid",paramMap,cb);
		}
		/**
		 * 增加任务
		 * @param ufp 任务信息的JSON对象UserFenPai
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddFenPai(ApiEntity.UserFenPai ufp,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ufp",ufp);
			return Const.post("TalkerAPI/AddFenPai",paramMap,cb);
		}
		/**
		 * 根据任务id删除任务
		 * @param taskid 任务id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelTaskById(int taskid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("taskid",taskid);
			return Const.post("TalkerAPI/DelTaskById",paramMap,cb);
		}
		/**
		 * 更新任务流程状态
		 * @param ufp 任务对象【FlowState：1普通，2提交审核，3返工】
		 * @param cb 回调
		 */
		public static Callback.Cancelable ModifyTaskFlowState(ApiEntity.UserFenPai ufp,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ufp",ufp);
			return Const.post("TalkerAPI/ModifyTaskFlowState",paramMap,cb);
		}
		/**
		 * 删除任务文件信息
		 * @param ufp 任务对象
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelTaskFile(ApiEntity.UserFenPai ufp,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ufp",ufp);
			return Const.post("TalkerAPI/DelTaskFile",paramMap,cb);
		}
		/**
		 * 修改任务信息
		 * @param ufp 任务信息的JSON对象UserFenPai
		 * @param cb 回调
		 */
		public static Callback.Cancelable ModifySimpleTask(ApiEntity.UserFenPai ufp,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ufp",ufp);
			return Const.post("TalkerAPI/ModifySimpleTask",paramMap,cb);
		}
		/**
		 * 更新任务流程状态
		 * @param ufp 任务对象【FlowState：1普通，2提交审核，3返工】
		 * @param cb 回调
		 */
		public static Callback.Cancelable ModifyTask(ApiEntity.UserFenPai ufp,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ufp",ufp);
			return Const.post("TalkerAPI/ModifyTask",paramMap,cb);
		}
		/**
		 * 根据id串获取任务id
		 * @param ids 任务id串
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTaskByIds(String ids,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ids",ids);
			return Const.get("TalkerAPI/GetTaskByIds",paramMap,cb);
		}
		/**
		 * 根据用户id获取任务记录(type=0(我创建)1(我参与)2(我负责))
		 * @param userid 用户id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTaskByUserId(int userid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("userid",userid);
			return Const.get("TalkerAPI/GetTaskByUserId",paramMap,cb);
		}
		/**
		 * 根据id串获取任务id
		 * @param type
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTask(int type,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("type",type);
			return Const.get("TalkerAPI/GetTask",paramMap,cb);
		}
		/**
		 * 根据项目id获取任务记录
		 * @param state 任务状态值（1要做的，2在做的，3已完成）
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetMobileProjectByTaskState(int state,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("state",state);
			return Const.get("TalkerAPI/GetMobileProjectByTaskState",paramMap,cb);
		}
		/**
		 * 根据项目id获取任务记录
		 * @param pidStr 项目id串
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTaskByProjectId(String pidStr,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("pidStr",pidStr);
			return Const.get("TalkerAPI/GetTaskByProjectId",paramMap,cb);
		}
		/**
		 * 根据任务状态和用户id获取任务（分页）
		 * @param state 任务状态
		 * @param s 页码
		 * @param size 页记录数
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTaskByStateAndPage(int state,int s,int size,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("state",state);
			paramMap.put("s",s);
			paramMap.put("size",size);
			return Const.get("TalkerAPI/GetTaskByStateAndPage",paramMap,cb);
		}
		/**
		 * 根据任务状态和用户id获取任务（分页）
		 * @param state 任务状态
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTaskByState(int state,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("state",state);
			return Const.get("TalkerAPI/GetTaskByState",paramMap,cb);
		}
		/**
		 * 根据任务id更新任务状态
		 * @param state 任务状态
		 * @param taskid 任务id
		 * @param cb 回调
		 */
		public static Callback.Cancelable UpdateTaskState(int state,int taskid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("state",state);
			paramMap.put("taskid",taskid);
			return Const.post("TalkerAPI/UpdateTaskState",paramMap,cb);
		}
		/**
		 * 根据项目组id获取项目
		 * @param teamid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetProjectByTeamId(int teamid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("teamid",teamid);
			return Const.get("TalkerAPI/GetProjectByTeamId",paramMap,cb);
		}
		/**
		 * 根据项目id获取项目
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetProjectById(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.get("TalkerAPI/GetProjectById",paramMap,cb);
		}
		/**
		 * 根据状态值获取项目
		 * @param state
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetProjectByState(int state,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("state",state);
			return Const.get("TalkerAPI/GetProjectByState",paramMap,cb);
		}
		/**
		 * 根据用户id获取项目
		 * @param userid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetProjectByUserId(int userid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("userid",userid);
			return Const.get("TalkerAPI/GetProjectByUserId",paramMap,cb);
		}
		/**
		 * 根据项目组id获取项目
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetProject(RequestCallback cb) {
			return Const.get("TalkerAPI/GetProject",cb);
		}
		/**
		 * 获取项目(分页)
		 * @param PageNo 页码
		 * @param PageSize 记录数
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetProjectByPage(int PageNo,int PageSize,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("PageNo",PageNo);
			paramMap.put("PageSize",PageSize);
			return Const.get("TalkerAPI/GetProjectByPage",paramMap,cb);
		}
		/**
		 * 添加项目
		 * @param up 项目信息的JSON对象UserProject
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoProject(ApiEntity.UserProject up,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("up",up);
			return Const.post("TalkerAPI/DoProject",paramMap,cb);
		}
		/**
		 * 根据id更新项目
		 * @param id 项目id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelProjectById(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.post("TalkerAPI/DelProjectById",paramMap,cb);
		}
		/**
		 * 增加/删除项目用户
		 * @param up 项目对象
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoProjectUser(ApiEntity.UserProject up,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("up",up);
			return Const.post("TalkerAPI/DoProjectUser",paramMap,cb);
		}
		/**
		 * 删除冲刺
		 * @param sid 冲刺记录id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelSprint(int sid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("sid",sid);
			return Const.post("TalkerAPI/DelSprint",paramMap,cb);
		}
		/**
		 * 添加冲刺
		 * @param us 冲刺信息的JSON对象UserSprint
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddSprint(ApiEntity.UserSprint us,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("us",us);
			return Const.post("TalkerAPI/AddSprint",paramMap,cb);
		}
		/**
		 * 添加任务到冲刺
		 * @param usList 冲刺列表对象
		 * @param taskid 任务id
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddSprintTask(List<ApiEntity.UserSprint> usList,int taskid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("usList",usList);
			paramMap.put("taskid",taskid);
			return Const.post("TalkerAPI/AddSprintTask",paramMap,cb);
		}
		/**
		 * 更新冲刺中的任务[删除冲刺中的任务，再添加到选择的冲刺中]
		 * @param usList
		 * @param us
		 * @param taskid
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoSprintTask(List<ApiEntity.UserSprint> usList,ApiEntity.UserSprint us,int taskid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("usList",usList);
			paramMap.put("us",us);
			paramMap.put("taskid",taskid);
			return Const.post("TalkerAPI/DoSprintTask",paramMap,cb);
		}
		/**
		 * 根据id修改冲刺记录（type:0【content】更新任务id串，1更新冲刺状态）
		 * @param content 任务id串 / 冲刺状态（1未冲刺，2冲刺中，3已完成）
		 * @param id 记录id
		 * @param type type:0【content】更新任务id串，1更新冲刺状态
		 * @param cb 回调
		 */
		public static Callback.Cancelable UpdateSprintById(String content,int id,int type,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("content",content);
			paramMap.put("id",id);
			paramMap.put("type",type);
			return Const.post("TalkerAPI/UpdateSprintById",paramMap,cb);
		}
		/**
		 * 获取冲刺信息
		 * @param statu 冲刺状态
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetSprint(int statu,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("statu",statu);
			return Const.get("TalkerAPI/GetSprint",paramMap,cb);
		}
		/**
		 * 添加/修改日程
		 * @param us 日程信息的JSON对象UserSchedule
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddCalendar(ApiEntity.UserSchedule us,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("us",us);
			return Const.post("TalkerAPI/AddCalendar",paramMap,cb);
		}
		/**
		 * 删除日程
		 * @param id 日程id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelCalenderById(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.post("TalkerAPI/DelCalenderById",paramMap,cb);
		}
		/**
		 * 完成日程
		 * @param cid 日程id
		 * @param cb 回调
		 */
		public static Callback.Cancelable FinishCalendar(int cid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("cid",cid);
			return Const.post("TalkerAPI/FinishCalendar",paramMap,cb);
		}
		/**
		 * 根据状态获取全部日程[分页]
		 * @param state
		 * @param s
		 * @param size
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetCalenderByPage(int state,int s,int size,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("state",state);
			paramMap.put("s",s);
			paramMap.put("size",size);
			return Const.get("TalkerAPI/GetCalenderByPage",paramMap,cb);
		}
		/**
		 * 根据状态获取全部日程
		 * @param state
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetCalenderByState(int state,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("state",state);
			return Const.post("TalkerAPI/GetCalenderByState",paramMap,cb);
		}
		/**
		 * 根据记录id获取日程
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetCalenderById(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.get("TalkerAPI/GetCalenderById",paramMap,cb);
		}
		/**
		 * 根据用户id获取全部日程
		 * @param userid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetAllCalenderListByUserId(int userid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("userid",userid);
			return Const.get("TalkerAPI/GetAllCalenderListByUserId",paramMap,cb);
		}
		/**
		 * 根据用户id获取全部日程
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetAllCalenderList(RequestCallback cb) {
			return Const.post("TalkerAPI/GetAllCalenderList",cb);
		}
		/**
		 * 根据时间区间获取日程
		 * @param start 开始时间
		 * @param end 结束时间
		 * @param statu 状态值
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetCalenderListByTimeSpan(String start,String end,int statu,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("start",start);
			paramMap.put("end",end);
			paramMap.put("statu",statu);
			return Const.get("TalkerAPI/GetCalenderListByTimeSpan",paramMap,cb);
		}
		/**
		 * 保存日程数据
		 * @param PostStr List=UserSchedule
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveCalenderData(List<ApiEntity.UserSchedule> PostStr,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("PostStr",PostStr);
			return Const.post("TalkerAPI/SaveCalenderData",paramMap,cb);
		}
		/**
		 * 获取代办事项详细内容
		 * @param sid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserScheduleById(int sid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("sid",sid);
			return Const.post("TalkerAPI/GetUserScheduleById",paramMap,cb);
		}
		/**
		 * PC日程视图上操作事项（简单新建和拖拽修改时间）
		 * @param CalendarTitle
		 * @param CalendarStartTime
		 * @param CalendarEndTime
		 * @param IsAllDayEvent
		 * @param calendarId
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddCalendarPC(String CalendarTitle,String CalendarStartTime,String CalendarEndTime,int IsAllDayEvent,int calendarId,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("CalendarTitle",CalendarTitle);
			paramMap.put("CalendarStartTime",CalendarStartTime);
			paramMap.put("CalendarEndTime",CalendarEndTime);
			paramMap.put("IsAllDayEvent",IsAllDayEvent);
			paramMap.put("calendarId",calendarId);
			return Const.post("TalkerAPI/AddCalendarPC",paramMap,cb);
		}
		/**
		 * 根据时间区间获取数据(PC端)
		 * @param showdate
		 * @param viewtype
		 * @param color
		 * @param projectid
		 * @param statu
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetCalenderListByTimeSpanPC(String showdate,String viewtype,String color,String projectid,int statu,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("showdate",showdate);
			paramMap.put("viewtype",viewtype);
			paramMap.put("color",color);
			paramMap.put("projectid",projectid);
			paramMap.put("statu",statu);
			return Const.get("TalkerAPI/GetCalenderListByTimeSpanPC",paramMap,cb);
		}
		/**
		 * 删除日程事项(pc)
		 * @param calendarId
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelCalenderByIdPC(int calendarId,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("calendarId",calendarId);
			return Const.post("TalkerAPI/DelCalenderByIdPC",paramMap,cb);
		}
		/**
		 * 发布工作计划
		 * @param upList List=UserPlan(工作计划对象)
		 * @param usernote List=UserNote(Talker对象)
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddTalkerPlan(List<ApiEntity.UserPlan> upList,ApiEntity.UserNote usernote,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("upList",upList);
			paramMap.put("usernote",usernote);
			return Const.post("TalkerAPI/AddTalkerPlan",paramMap,cb);
		}
		/**
		 * 保存工作计划数据
		 * @param up UserPlan(工作计划对象)
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoTalkerPlan(ApiEntity.UserPlan up,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("up",up);
			return Const.post("TalkerAPI/DoTalkerPlan",paramMap,cb);
		}
		/**
		 * 删除工作计划数据
		 * @param PlanId 工作计划记录id)
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelTalkerPlan(int PlanId,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("PlanId",PlanId);
			return Const.post("TalkerAPI/DelTalkerPlan",paramMap,cb);
		}
		/**
		 * List=UserPlan
		 * @param state
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetPlanByState(int state,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("state",state);
			return Const.get("TalkerAPI/GetPlanByState",paramMap,cb);
		}
		/**
		 * List=UserPlan
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetPlanById(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.get("TalkerAPI/GetPlanById",paramMap,cb);
		}
		/**
		 * List=UserPlan
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetPlan(RequestCallback cb) {
			return Const.get("TalkerAPI/GetPlan",cb);
		}
		/**
		 * 保存任务评论数据
		 * @param tr TaskReply(任务评论对象)
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoTaskReply(ApiEntity.TaskReply tr,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("tr",tr);
			return Const.post("TalkerAPI/DoTaskReply",paramMap,cb);
		}
		/**
		 * 删除任务评论数据
		 * @param rid 任务评论记录id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelTaskReply(int rid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("rid",rid);
			return Const.post("TalkerAPI/DelTaskReply",paramMap,cb);
		}
		/**
		 * 获取任务评论记录[PC端]
		 * @param taskid 任务id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTaskReplyByTaskIdPC(int taskid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("taskid",taskid);
			return Const.get("TalkerAPI/GetTaskReplyByTaskIdPC",paramMap,cb);
		}
		/**
		 * 获取任务评论记录[PC端]
		 * @param taskid 任务id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTaskReplyByTaskId(int taskid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("taskid",taskid);
			return Const.get("TalkerAPI/GetTaskReplyByTaskId",paramMap,cb);
		}
		/**
		 * 获取文件关联项目
		 * @param FileId 文件id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetProjectByFileId(int FileId,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("FileId",FileId);
			return Const.get("TalkerAPI/GetProjectByFileId",paramMap,cb);
		}
		/**
		 * 获取文件关联任务
		 * @param FileId 文件id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTaskByFileId(int FileId,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("FileId",FileId);
			return Const.get("TalkerAPI/GetTaskByFileId",paramMap,cb);
		}
		/**
		 * 获取文件关联日程
		 * @param FileId 文件id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetScheduleByFileId(int FileId,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("FileId",FileId);
			return Const.get("TalkerAPI/GetScheduleByFileId",paramMap,cb);
		}
		/**
		 * 根据群组id获取项目列表
		 * @param groupid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetProjectByGroupId(int groupid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("groupid",groupid);
			return Const.get("TalkerAPI/GetProjectByGroupId",paramMap,cb);
		}
		/**
		 * 根据群组id获取任务列表
		 * @param groupid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTaskListByGroupId(int groupid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("groupid",groupid);
			return Const.get("TalkerAPI/GetTaskListByGroupId",paramMap,cb);
		}
		/**
		 * 根据群组id获取日程列表
		 * @param groupid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetScheduleByGroupId(int groupid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("groupid",groupid);
			return Const.get("TalkerAPI/GetScheduleByGroupId",paramMap,cb);
		}
		/**
		 * 根据群组id获取文件列表
		 * @param groupid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFileListByGroupId(int groupid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("groupid",groupid);
			return Const.get("TalkerAPI/GetFileListByGroupId",paramMap,cb);
		}
		/**
		 * 根据用户id获取工作项目/相关任务/活动日程/知识库各自的数量接口
		 * @param userid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTalkCountByUserId(int userid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("userid",userid);
			return Const.get("TalkerAPI/GetTalkCountByUserId",paramMap,cb);
		}
		/**
		 * 获取我发布的，我收藏的，相关到我的数量接口
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetMyTalkCount(RequestCallback cb) {
			return Const.get("TalkerAPI/GetMyTalkCount",cb);
		}
		/**
		 * 根据文件id获取项目、任务、日程的数量
		 * @param fileid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetMyTalkerCountByFileId(int fileid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fileid",fileid);
			return Const.get("TalkerAPI/GetMyTalkerCountByFileId",paramMap,cb);
		}
		/**
		 * 根据群组id获取项目、任务、日程、知识库的数量
		 * @param groupid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetMyTalkerCountByGroupId(int groupid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("groupid",groupid);
			return Const.get("TalkerAPI/GetMyTalkerCountByGroupId",paramMap,cb);
		}
		/**
		 * 保存项目组数据
		 * @param ut TaskReply(任务评论对象)
		 * @param type type(1增加，2修改，3删除)
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoUserTeam(ApiEntity.UserTeam ut,int type,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ut",ut);
			paramMap.put("type",type);
			return Const.post("TalkerAPI/DoUserTeam",paramMap,cb);
		}
		/**
		 * 获取项目组记录
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserTeam(RequestCallback cb) {
			return Const.get("TalkerAPI/GetUserTeam",cb);
		}
		/**
		 * 根据项目组id获取项目组记录
		 * @param id 项目组id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserTeamById(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.get("TalkerAPI/GetUserTeamById",paramMap,cb);
		}
		/**
		 * 根据项目组id获取项目组文件列表
		 * @param id 项目组id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserTeamFileById(int id,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("id",id);
			return Const.get("TalkerAPI/GetUserTeamFileById",paramMap,cb);
		}
		/**
		 * 保存项目组登入信息数据
		 * @param tc TeamCheckIn(项目组登入信息对象)
		 * @param type type(1增加，2修改，3删除)
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoUserTeamCheckIn(ApiEntity.TeamCheckIn tc,int type,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("tc",tc);
			paramMap.put("type",type);
			return Const.post("TalkerAPI/DoUserTeamCheckIn",paramMap,cb);
		}
		/**
		 * 获取项目组登入信息记录
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserTeamCheckIn(RequestCallback cb) {
			return Const.get("TalkerAPI/GetUserTeamCheckIn",cb);
		}
		/**
		 * @param pid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetProjectDetail(int pid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(pid);
			return Const.get("TalkerAPI/GetProjectDetail",paramList,cb);
		}
		/**
		 * @param search
		 * @param pid
		 * @param fid
		 * @param groupid
		 * @param s
		 * @param size
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTrackFileByProjectId(String search,int pid,int fid,String groupid,int s,int size,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(search);
			paramList.add(pid);
			paramList.add(fid);
			paramList.add(groupid);
			paramList.add(s);
			paramList.add(size);
			return Const.get("TalkerAPI/GetTrackFileByProjectId",paramList,cb);
		}
		/**
		 * @param search
		 * @param pid
		 * @param groupid
		 * @param s
		 * @param size
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetAllTrackFile(String search,int pid,String groupid,int s,int size,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(search);
			paramList.add(pid);
			paramList.add(groupid);
			paramList.add(s);
			paramList.add(size);
			return Const.get("TalkerAPI/GetAllTrackFile",paramList,cb);
		}
		/**
		 * 根据项目组id获取详情（1动态，2消息）
		 * @param teamid 项目组id
		 * @param s
		 * @param size
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetMessageByTeamId(int teamid,int s,int size,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("teamid",teamid);
			paramMap.put("s",s);
			paramMap.put("size",size);
			return Const.get("TalkerAPI/GetMessageByTeamId",paramMap,cb);
		}
	}
	public static class EventAPI {
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable get_Access(RequestCallback cb) {
			return Const.get("EventAPI/get_Access",cb);
		}
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveEvent(RequestCallback cb) {
			return Const.get("EventAPI/SaveEvent",cb);
		}
	}
	public static class PageBuilder {
		/**
		 * 获取某个页面的权限
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable Load(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("PageBuilder/Load",paramList,cb);
		}
	}
	public static class GroupAPI {
		/**
		 * 保存角色组
		 * @param group
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveGroup(ApiEntity.GroupEntity group,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(group);
			return Const.post("GroupAPI/SaveGroup",paramList,cb);
		}
		/**
		 *
		 * @param groupId
		 * @param pageId
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetGroupPowerById(int groupId,int pageId,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupId);
			paramList.add(pageId);
			return Const.get("GroupAPI/GetGroupPowerById",paramList,cb);
		}
		/**
		 * @param groupPower
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveGroupNav(ApiEntity.GroupPower groupPower,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupPower);
			return Const.post("GroupAPI/SaveGroupNav",paramList,cb);
		}
		/**
		 * 加载角色Portal的权限
		 * @param groupId
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadGroupPortal(int groupId,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupId);
			return Const.get("GroupAPI/LoadGroupPortal",paramList,cb);
		}
		/**
		 * 加载角色导航权限
		 * @param groupId
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadGroupNav(int groupId,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupId);
			return Const.get("GroupAPI/LoadGroupNav",paramList,cb);
		}
		/**
		 * 获取某个页面的权限
		 * @param groupId
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadPagePower(int groupId,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupId);
			return Const.get("GroupAPI/LoadPagePower",paramList,cb);
		}
		/**
		 * @param groupPower
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteGroupNav(ApiEntity.GroupPower groupPower,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupPower);
			return Const.post("GroupAPI/DeleteGroupNav",paramList,cb);
		}
		/**
		 * @param groupPower
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveGroupPower(ApiEntity.GroupPower groupPower,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupPower);
			return Const.post("GroupAPI/SaveGroupPower",paramList,cb);
		}
		/**
		 * @param groupPower
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveGroupTable(ApiEntity.GroupPower groupPower,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupPower);
			return Const.post("GroupAPI/SaveGroupTable",paramList,cb);
		}
		/**
		 * @param groupPower
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteGroupTable(ApiEntity.GroupPower groupPower,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupPower);
			return Const.post("GroupAPI/DeleteGroupTable",paramList,cb);
		}
		/**
		 * @param groupPower
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelAndInsTable(ApiEntity.GroupPower groupPower,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(groupPower);
			return Const.post("GroupAPI/DelAndInsTable",paramList,cb);
		}
		/**
		 * 获取所有角色
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetGroups(RequestCallback cb) {
			return Const.get("GroupAPI/GetGroups",cb);
		}
		/**
		 * 根据角色id获取信息
		 * @param id 角色id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetGroupByGroupId(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("GroupAPI/GetGroupByGroupId",paramList,cb);
		}
		/**
		 * 添加用户到某角色
		 * @param gmList
		 * @param cb 回调
		 */
		public static Callback.Cancelable AddToGroup(List<ApiEntity.GroupMember> gmList,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(gmList);
			return Const.post("GroupAPI/AddToGroup",paramList,cb);
		}
		/**
		 * 从某角色删除用户
		 * @param gmList
		 * @param cb 回调
		 */
		public static Callback.Cancelable RemoveFromGroup(List<ApiEntity.GroupMember> gmList,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(gmList);
			return Const.post("GroupAPI/RemoveFromGroup",paramList,cb);
		}
	}
	public static class AreaService {
		/**
		 * 获取国家列表
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetCountry(RequestCallback cb) {
			return Const.get("AreaService/GetCountry",cb);
		}
		/**
		 * @param pid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetChildren(int pid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(pid);
			return Const.get("AreaService/GetChildren",paramList,cb);
		}
	}
	public static class ExportFile {
		/**
		 * 下载文件
		 * @param fileName
		 * @param cb 回调
		 */
		public static Callback.Cancelable DownloadFile(String fileName,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fileName",fileName);
			return Const.get("ExportFile/DownloadFile",paramMap,cb);
		}
	}
	public static class ReportService {
		/**
		 * 获取报表数据
		 * @param tplid 模版id
		 * @param rptid 报表id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetReportData(int tplid,int rptid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tplid);
			paramList.add(rptid);
			return Const.get("ReportService/GetReportData",paramList,cb);
		}
		/**
		 * 展开报表数据源
		 * @param tplid 模版id
		 * @param rptid 报表id
		 * @param cb 回调
		 */
		public static Callback.Cancelable ExplainDataSource(int tplid,int rptid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tplid);
			paramList.add(rptid);
			return Const.get("ReportService/ExplainDataSource",paramList,cb);
		}
	}
	public static class UserData {
		/**
		 * 获取某个页面的权限
		 * @param key
		 * @param start
		 * @param end
		 * @param args
		 * @param cb 回调
		 */
		public static Callback.Cancelable Load(String key,int start,int end,String[] args,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("key",key);
			paramMap.put("start",start);
			paramMap.put("end",end);
			paramMap.put("args",args);
			return Const.post("LoadData",paramMap,cb);
		}
		/**
		 * @param key
		 * @param start
		 * @param end
		 * @param filterFields
		 * @param keyword
		 * @param args
		 * @param cb 回调
		 */
		public static Callback.Cancelable SearchSource(String key,int start,int end,String[] filterFields,String keyword,String[] args,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("key",key);
			paramMap.put("start",start);
			paramMap.put("end",end);
			paramMap.put("filterFields",filterFields);
			paramMap.put("keyword",keyword);
			paramMap.put("args",args);
			return Const.post("UserData/SearchSource",paramMap,cb);
		}
		/**
		 * @param key
		 * @param valField
		 * @param val
		 * @param args
		 * @param cb 回调
		 */
		public static Callback.Cancelable SearchItem(String key,String valField,String val,String[] args,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("key",key);
			paramMap.put("valField",valField);
			paramMap.put("val",val);
			paramMap.put("args",args);
			return Const.post("UserData/SearchItem",paramMap,cb);
		}
		/**
		 * 获取某个页面的权限
		 * @param pid
		 * @param sid
		 * @param cb 回调
		 */
		public static Callback.Cancelable Page(int pid,String sid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(pid);
			paramList.add(sid);
			return Const.get("Page",paramList,cb);
		}
		/**
		 * @param sid
		 * @param cb 回调
		 */
		public static Callback.Cancelable DataRequest(String sid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(sid);
			return Const.get("Data",paramList,cb);
		}
		/**
		 * 输出页面所有元素
		 * @param pid
		 * @param cb 回调
		 */
		public static Callback.Cancelable PageElemens(int pid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(pid);
			return Const.get("mpage",paramList,cb);
		}
		/**
		 * 文件上传
		 * @param UploadData
		 * @param cb 回调
		 */
		public static Callback.Cancelable PCUpload(List<ApiEntity.Files> UploadData,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("UploadData",UploadData);
			return Const.post("pcupload",paramMap,cb);
		}
		/**
		 * 文件上传
		 * @param UploadData
		 * @param cb 回调
		 */
		public static Callback.Cancelable Upload(List<ApiEntity.Files> UploadData,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("UploadData",UploadData);
			return Const.post("upload",paramMap,cb);
		}
		/**
		 * 图片上传
		 * @param cb 回调
		 */
		public static Callback.Cancelable ImageUpload(RequestCallback cb) {
			return Const.post("ImageUpload",cb);
		}
		/**
		 * 获取文件夹列表
		 * @param Search 关键字
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFoldList(String Search,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("Search",Search);
			return Const.get("UserData/GetFoldList",paramMap,cb);
		}
		/**
		 * 获取全部文件夹列表
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetAllFoldList(RequestCallback cb) {
			return Const.get("UserData/GetAllFoldList",cb);
		}
		/**
		 * 根据文件id串获取文件信息列表
		 * @param ids
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFileListByIds(String ids,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ids",ids);
			return Const.get("UserData/GetFileListByIds",paramMap,cb);
		}
		/**
		 * 获取文件信息列表[1获取文件，2获取文件夹,3获取文件和文件夹]
		 * @param search
		 * @param Type
		 * @param foldid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFileInfoByFolderId(String search,int Type,int foldid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("search",search);
			paramMap.put("Type",Type);
			paramMap.put("foldid",foldid);
			return Const.get("UserData/GetFileInfoByFolderId",paramMap,cb);
		}
		/**
		 * 根据用户id获取文件列表
		 * @param userid 用户id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFilesListByUserId(int userid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("userid",userid);
			return Const.get("UserData/GetFilesListByUserId",paramMap,cb);
		}
		/**
		 * 获取文件列表[分页]
		 * @param Search 关键字
		 * @param Type 查询类型（3我的文件，4共享给我的文件，5作废的文件）
		 * @param FoldId 文件夹id
		 * @param s 页码
		 * @param size 页记录数
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFilesListByPage(String Search,int Type,int FoldId,int s,int size,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("Search",Search);
			paramMap.put("Type",Type);
			paramMap.put("FoldId",FoldId);
			paramMap.put("s",s);
			paramMap.put("size",size);
			return Const.get("UserData/GetFilesListByPage",paramMap,cb);
		}
		/**
		 * 根据用户id获取文件列表
		 * @param Search 关键字
		 * @param Type 查询类型（3我的文件，4共享给我的文件，5作废的文件）
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFilesList(String Search,int Type,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("Search",Search);
			paramMap.put("Type",Type);
			return Const.get("UserData/GetFilesList",paramMap,cb);
		}
		/**
		 * 添加文件夹
		 * @param file 文件夹对象
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveFoldInfo(ApiEntity.Files file,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("file",file);
			return Const.post("UserData/SaveFoldInfo",paramMap,cb);
		}
		/**
		 * 根据文件id获取文件版本信息
		 * @param fid 文件id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFilesVersionById(int fid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fid",fid);
			return Const.get("UserData/GetFilesVersionById",paramMap,cb);
		}
		/**
		 * 根据类型获取文件数量
		 * @param Type 查询类型（3我的文件，4共享给我的文件，5作废的文件）
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFilesCountByType(int Type,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("Type",Type);
			return Const.get("UserData/GetFilesCountByType",paramMap,cb);
		}
		/**
		 * 作废文件
		 * @param fid 文件id
		 * @param cb 回调
		 */
		public static Callback.Cancelable cancellFile(int fid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fid",fid);
			return Const.post("UserData/cancellFile",paramMap,cb);
		}
		/**
		 * 彻底删除文件
		 * @param fid 文件id
		 * @param cb 回调
		 */
		public static Callback.Cancelable delFile(int fid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fid",fid);
			return Const.post("UserData/delFile",paramMap,cb);
		}
		/**
		 * 保存文件下载记录
		 * @param fid 文件id
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveDownLoadRecord(int fid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fid",fid);
			return Const.post("UserData/SaveDownLoadRecord",paramMap,cb);
		}
		/**
		 * 加载文件权限
		 * @param fid 文件id
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadFilePower(int fid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fid",fid);
			return Const.get("UserData/LoadFilePower",paramMap,cb);
		}
		/**
		 * @param fid
		 * @param cb 回调
		 */
		public static Callback.Cancelable FileDownLog(int fid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fid",fid);
			return Const.get("UserData/FileDownLog",paramMap,cb);
		}
		/**
		 * 下载文件
		 * @param fileId
		 * @param cb 回调
		 */
		public static Callback.Cancelable DownloadFile(int fileId,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(fileId);
			return Const.get("DownLoad",paramList,cb);
		}
		/**
		 * 操作文件权限
		 * @param upList
		 * @param fid
		 * @param cb 回调
		 */
		public static Callback.Cancelable DoFilePower(List<ApiEntity.UserFilePower> upList,int fid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("upList",upList);
			paramMap.put("fid",fid);
			return Const.post("UserData/DoFilePower",paramMap,cb);
		}
		/**
		 * @param fileInfo
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveFileInfo(ApiEntity.Files fileInfo,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("fileInfo",fileInfo);
			return Const.post("UserData/SaveFileInfo",paramMap,cb);
		}
		/**
		 * @param ds
		 * @param isDelete
		 * @param cb 回调
		 */
		public static Callback.Cancelable ImportData(ApiEntity.DataSource ds,boolean isDelete,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ds",ds);
			paramMap.put("isDelete",isDelete);
			return Const.post("UserData/ImportData",paramMap,cb);
		}
		/**
		 * 上传文件
		 * @param path 目录
		 * @param n 创建新文件名(不等于0则创建)
		 * @param save 保存文件信息(等于1才保存)
		 * @param cb 回调
		 */
		public static Callback.Cancelable UploadFile(String path,String n,String save,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("path",path);
			paramMap.put("n",n);
			paramMap.put("save",save);
			return Const.post("UploadFile",paramMap,cb);
		}
		/**
		 * 上传文件
		 * @param cb 回调
		 */
		public static Callback.Cancelable UploadFile(RequestCallback cb) {
			return Const.post("UploadFileByExport",cb);
		}
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable UploadAudio(RequestCallback cb) {
			return Const.post("UploadAudio",cb);
		}
		/**
		 * @param cb 回调
		 */
		public static Callback.Cancelable get_UserFileBasePath(RequestCallback cb) {
			return Const.get("UserData/get_UserFileBasePath",cb);
		}
	}
	public static class Develop {
		/**
		 * 获取所有API方法
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetAllMethod(RequestCallback cb) {
			return Const.get("Develop/GetAllMethod",cb);
		}
		/**
		 * 生成api列表（java版）
		 * @param cb 回调
		 */
		public static Callback.Cancelable BuildAndroidApi(RequestCallback cb) {
			return Const.get("Develop/BuildAndroidApi",cb);
		}
		/**
		 * 生成api列表（ios版）
		 * @param cb 回调
		 */
		public static Callback.Cancelable BuildIosApi(RequestCallback cb) {
			return Const.get("Develop/BuildIosApi",cb);
		}
	}
	public static class PowerAPI {
		/**
		 * 获取某个页面的权限
		 * @param pageId
		 * @param tplid
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadPagePower(int pageId,int tplid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(pageId);
			paramList.add(tplid);
			return Const.get("PowerAPI/LoadPagePower",paramList,cb);
		}
		/**
		 * 保存某个页面权限
		 * @param pagePower
		 * @param cb 回调
		 */
		public static Callback.Cancelable SavePower(ApiEntity.PagePower pagePower,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(pagePower);
			return Const.post("PowerAPI/SavePower",paramList,cb);
		}
		/**
		 *
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetPagePowerById(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("PowerAPI/GetPagePowerById",paramList,cb);
		}
		/**
		 *
		 * @param pageId
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadTablePower(int pageId,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(pageId);
			return Const.get("PowerAPI/LoadTablePower",paramList,cb);
		}
		/**
		 * 保存某个数据表权限
		 * @param pagePower
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveTablePower(ApiEntity.PagePower pagePower,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(pagePower);
			return Const.post("PowerAPI/SaveTablePower",paramList,cb);
		}
	}
	public static class Table {
		/**
		 * 获取所有的info
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetAllInfo(RequestCallback cb) {
			return Const.get("Table/GetAllInfo",cb);
		}
		/**
		 * 获取某个应用下的数据表
		 * @param infoid 应用ID
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTableByInfoID(int infoid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(infoid);
			return Const.get("Table/GetTableByInfoID",paramList,cb);
		}
		/**
		 * 搜索数据表
		 * @param tname 数据表名称
		 * @param cb 回调
		 */
		public static Callback.Cancelable SearchTable(String tname,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tname);
			return Const.get("Table/SearchTable",paramList,cb);
		}
		/**
		 * 获取数据表的字段
		 * @param tableid 数据表
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFieldsByTableID(int tableid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tableid);
			return Const.get("Table/GetFieldsByTableID",paramList,cb);
		}
		/**
		 * @param idstr
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFieldsByTableIDs(String idstr,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(idstr);
			return Const.get("Table/GetFieldsByTableIDs",paramList,cb);
		}
		/**
		 * @param tid
		 * @param isloadField
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTableByID(int tid,boolean isloadField,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			paramList.add(isloadField);
			return Const.get("Table/GetTableByID",paramList,cb);
		}
		/**
		 * 保存某个数据表权限
		 * @param table
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveTable(ApiEntity.UserTable table,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("table",table);
			return Const.post("Table/SaveTable",paramMap,cb);
		}
		/**
		 * 保存字段
		 * @param field 字段结构
		 * @param tid 所属的数据表
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveField(ApiEntity.UserField field,int tid,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("field",field);
			paramMap.put("tid",tid);
			return Const.post("Table/SaveField",paramMap,cb);
		}
		/**
		 * 保存应用
		 * @param info
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveInfo(ApiEntity.SystemInfo info,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("info",info);
			return Const.post("Table/SaveInfo",paramMap,cb);
		}
		/**
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteInfo(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("Table/DeleteInfo",paramList,cb);
		}
		/**
		 * @param tid
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteTable(int tid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			return Const.get("Table/DeleteTable",paramList,cb);
		}
		/**
		 * @param id
		 * @param tid
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteField(int id,int tid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			paramList.add(tid);
			return Const.get("Table/DeleteField",paramList,cb);
		}
		/**
		 * 获取表关系
		 * @param mtable 主表ID
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetRelations(int mtable,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(mtable);
			return Const.get("Table/GetRelations",paramList,cb);
		}
		/**
		 * @param rel
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteRelation(ApiEntity.RelationSetting rel,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("rel",rel);
			return Const.post("Table/DeleteRelation",paramMap,cb);
		}
		/**
		 * @param rel
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveRelation(ApiEntity.RelationSetting rel,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("rel",rel);
			return Const.post("Table/SaveRelation",paramMap,cb);
		}
		/**
		 * @param tid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetSubTable(int tid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			return Const.get("Table/GetSubTable",paramList,cb);
		}
		/**
		 * @param tid
		 * @param widthTable
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserViewByTableID(int tid,boolean widthTable,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			paramList.add(widthTable);
			return Const.get("Table/GetUserViewByTableID",paramList,cb);
		}
		/**
		 * 保存视图配置
		 * @param view
		 * @param isnew
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveView(ApiEntity.UserView view,boolean isnew,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("view",view);
			paramMap.put("isnew",isnew);
			return Const.post("Table/SaveView",paramMap,cb);
		}
		/**
		 * @param ids
		 * @param loadField
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTables(int[] ids,boolean loadField,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(ids);
			paramList.add(loadField);
			return Const.get("Table/GetTables",paramList,cb);
		}
		/**
		 * @param tid
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteTableFields(int tid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			return Const.get("Table/DeleteTableFields",paramList,cb);
		}
		/**
		 * @param ds
		 * @param cb 回调
		 */
		public static Callback.Cancelable DataSourceToSql(ApiEntity.DataSource ds,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ds",ds);
			return Const.post("Table/DataSourceToSql",paramMap,cb);
		}
		/**
		 * @param fid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetFieldSearchSet(int fid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(fid);
			return Const.get("Table/GetFieldSearchSet",paramList,cb);
		}
	}
	public static class TemplateAPI {
		/**
		 * 加载自己的所有模版
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadTemplates(RequestCallback cb) {
			return Const.get("TemplateAPI/LoadTemplates",cb);
		}
		/**
		 * 加载自己的所有模版
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadBaseTemplates(RequestCallback cb) {
			return Const.get("TemplateAPI/LoadBaseTemplates",cb);
		}
		/**
		 * 加载标准库中所有模版
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadSTDTemplates(RequestCallback cb) {
			return Const.get("TemplateAPI/LoadSTDTemplates",cb);
		}
		/**
		 * 读取某个模版
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadTemplateByID(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("TemplateAPI/LoadTemplateByID",paramList,cb);
		}
		/**
		 * 保存模版
		 * @param temp
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveTemplate(ApiEntity.Template temp,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("temp",temp);
			return Const.post("TemplateAPI/SaveTemplate",paramMap,cb);
		}
		/**
		 * 保存角色组
		 * @param group
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveGroup(ApiEntity.Group group,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(group);
			return Const.get("TemplateAPI/SaveGroup",paramList,cb);
		}
		/**
		 * 加载角色Portal的权限
		 * @param tid
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadGroup(int tid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			return Const.get("TemplateAPI/LoadGroup",paramList,cb);
		}
		/**
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteGroup(String id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("TemplateAPI/DeleteGroup",paramList,cb);
		}
		/**
		 * @param tid
		 * @param tableids
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveTemplateTables(int tid,List<Integer> tableids,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			paramList.add(tableids);
			return Const.get("TemplateAPI/SaveTemplateTables",paramList,cb);
		}
		/**
		 * @param ID
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteTemplate(int ID,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(ID);
			return Const.get("TemplateAPI/DeleteTemplate",paramList,cb);
		}
		/**
		 * @param tid
		 * @param tableids
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteTemplateTables(int tid,List<Integer> tableids,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			paramList.add(tableids);
			return Const.get("TemplateAPI/DeleteTemplateTables",paramList,cb);
		}
		/**
		 * @param tid
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetTemplateTables(int tid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			return Const.get("TemplateAPI/GetTemplateTables",paramList,cb);
		}
		/**
		 * 获取导航
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetNaviID(String id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("TemplateAPI/GetNaviID",paramList,cb);
		}
		/**
		 * 保存导航数据
		 * @param nav
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveNavigation(ApiEntity.Navigation nav,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("nav",nav);
			return Const.post("TemplateAPI/SaveNavigation",paramMap,cb);
		}
		/**
		 * 获取导航数据
		 * @param TemplateID
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetNavigation(String TemplateID,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(TemplateID);
			return Const.get("TemplateAPI/GetNavigation",paramList,cb);
		}
		/**
		 * 获取导航数据
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetIndexNavigation(RequestCallback cb) {
			return Const.get("TemplateAPI/GetIndexNavigation",cb);
		}
		/**
		 * 删除导航数据
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteNavigation(String id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("TemplateAPI/DeleteNavigation",paramList,cb);
		}
		/**
		 * 保存选择模块
		 * @param AddID
		 * @param DelID
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveSelectTemplate(String AddID,String DelID,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(AddID);
			paramList.add(DelID);
			return Const.get("TemplateAPI/SaveSelectTemplate",paramList,cb);
		}
		/**
		 * 获取模块管理
		 * @param where
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetSysModule(String where,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(where);
			return Const.get("TemplateAPI/GetSysModule",paramList,cb);
		}
		/**
		 * 保存模块管理
		 * @param ID
		 * @param Name
		 * @param URL
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveSysModule(int ID,String Name,String URL,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("ID",ID);
			paramMap.put("Name",Name);
			paramMap.put("URL",URL);
			return Const.post("TemplateAPI/SaveSysModule",paramMap,cb);
		}
		/**
		 * 删除模块管理
		 * @param ID
		 * @param cb 回调
		 */
		public static Callback.Cancelable DelSysModule(String ID,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(ID);
			return Const.get("TemplateAPI/DelSysModule",paramList,cb);
		}
		/**
		 * @param templateID
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadFlows(int templateID,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(templateID);
			return Const.get("TemplateAPI/LoadFlows",paramList,cb);
		}
		/**
		 * @param re
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveResource(ApiEntity.Resource re,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("re",re);
			return Const.post("TemplateAPI/SaveResource",paramMap,cb);
		}
		/**
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadResource(String id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("TemplateAPI/LoadResource",paramList,cb);
		}
		/**
		 * @param tempid
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadAutoCode(int tempid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tempid);
			return Const.get("TemplateAPI/LoadAutoCode",paramList,cb);
		}
		/**
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteResource(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("TemplateAPI/DeleteResource",paramList,cb);
		}
		/**
		 * 安装默认模版
		 * @param companyCode
		 * @param cb 回调
		 */
		public static Callback.Cancelable InstallBaseTemplate(String companyCode,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(companyCode);
			return Const.get("TemplateAPI/InstallBaseTemplate",paramList,cb);
		}
		/**
		 * 根据模版ID安装模版
		 * @param tplid
		 * @param companyCode
		 * @param cb 回调
		 */
		public static Callback.Cancelable InstallTemplate(int tplid,String companyCode,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tplid);
			paramList.add(companyCode);
			return Const.get("TemplateAPI/InstallTemplate",paramList,cb);
		}
		/**
		 * @param companyCode
		 * @param cb 回调
		 */
		public static Callback.Cancelable CreateBaseTables(String companyCode,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(companyCode);
			return Const.get("TemplateAPI/CreateBaseTables",paramList,cb);
		}
		/**
		 * @param tid
		 * @param cb 回调
		 */
		public static Callback.Cancelable Unload(int tid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tid);
			return Const.get("TemplateAPI/Unload",paramList,cb);
		}
	}

	public static class UserFileAPI {
		/**
		 * @param tempid
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadUserFilesByTemplate(int tempid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tempid);
			return Const.get("UserFile/LoadUserFilesByTemplate",paramList,cb);
		}
		/**
		 * @param tempid
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadUserFilesByTemplateForm(int tempid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tempid);
			return Const.get("UserFile/LoadUserFilesByTemplateForm",paramList,cb);
		}
		/**
		 * @param tempid
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadUserFilesByTemplateGrid(int tempid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tempid);
			return Const.get("UserFile/LoadUserFilesByTemplateGrid",paramList,cb);
		}
		/**
		 * @param tempid
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadUserFilesByTemplateCustomPage(int tempid,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tempid);
			return Const.get("UserFile/LoadUserFilesByTemplateCustomPage",paramList,cb);
		}
		/**
		 * @param tempid
		 * @param type
		 * @param key
		 * @param cb 回调
		 */
		public static Callback.Cancelable SearchUserFile(int tempid,int type,String key,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("tempid",tempid);
			paramMap.put("type",type);
			paramMap.put("key",key);
			return Const.post("UserFile/SearchUserFile",paramMap,cb);
		}
		/**
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable DeleteUserFile(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("UserFile/DeleteUserFile",paramList,cb);
		}
		/**
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable LoadUserFile(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("UserFile/LoadUserFile",paramList,cb);
		}
		/**
		 * @param file
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveUserFile(ApiEntity.UserFile file,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(file);
			return Const.post("UserFile/SaveUserFile",paramList,cb);
		}
		/**
		 * @param tplid
		 * @param fileID
		 * @param tids
		 * @param cb 回调
		 */
		public static Callback.Cancelable AppendReportDataSource(int tplid,int fileID,int[] tids,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(tplid);
			paramList.add(fileID);
			paramList.add(tids);
			return Const.get("UserFile/AppendReportDataSource",paramList,cb);
		}
		/**
		 * @param TypeId
		 * @param cb 回调
		 */
		public static Callback.Cancelable SearchTableList(String TypeId,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(TypeId);
			return Const.get("UserFile/SearchTableList",paramList,cb);
		}
		/**
		 * 获取文件信息(不包含Content)
		 * @param id
		 * @param cb 回调
		 */
		public static Callback.Cancelable GetUserFileInfo(int id,RequestCallback cb) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(id);
			return Const.get("UserFile/GetUserFileInfo",paramList,cb);
		}
		/**
		 * @param imageData
		 * @param cb 回调
		 */
		public static Callback.Cancelable SaveUploadImage(String imageData,RequestCallback cb) {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("imageData",imageData);
			return Const.post("UserFile/SaveUploadImage",paramMap,cb);
		}
	}
}