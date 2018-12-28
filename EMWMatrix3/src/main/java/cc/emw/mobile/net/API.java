package cc.emw.mobile.net;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cc.emw.mobile.R.string.state;

public class API {
    public static class NewPayServer {
        /**
         * 支付宝支付接口，经过处理，一单为一个商品
         *
         * @param auth_code 条码
         * @param cb        回调
         */
        public static Callback.Cancelable ZFBRequest(String auth_code, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("auth_code", auth_code);
            return Const.post("NewPayServer/ZFBRequest", paramMap, cb);
        }

        /**
         * @param productId
         * @param cb        回调
         */
        public static Callback.Cancelable WXAPPZFRequest(String productId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("productId", productId);
            return Const.post("NewPayServer/WXAPPZFRequest", paramMap, cb);
        }

        /**
         * @param o
         * @param cb 回调
         */
        public static Callback.Cancelable cancelOrderRetry(Object o, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(o);
            return Const.get("NewPayServer/cancelOrderRetry", paramList, cb);
        }

        /**
         * @param txnAmt
         * @param cb     回调
         */
        public static Callback.Cancelable UnionPay(String txnAmt, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("txnAmt", txnAmt);
            return Const.post("NewPayServer/UnionPay", paramMap, cb);
        }
    }

    public static class JsonSplit {
        /**
         * 判断是不是json或json数组格式开头
         *
         * @param json
         * @param cb   回调
         */
        public static Callback.Cancelable IsJson(String json, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(json);
            return Const.post("JsonSplit/IsJson", paramList, cb);
        }
    }

    public static class CompanyAPI {
        /**
         * 查询注册公司列表
         *
         * @param cb 回调
         */
        public static Callback.Cancelable LoadCompanyList(RequestCallback cb) {
            return Const.get("CompanyAPI/LoadCompanyList", cb);
        }

        /**
         * 获取注册公司详情
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetCompanyInfoByID(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("CompanyAPI/GetCompanyInfoByID", paramList, cb);
        }

        /**
         * 修改注册公司信息
         *
         * @param company
         * @param cb      回调
         */
        public static Callback.Cancelable UpdateCompanyInfo(ApiEntity.Company company, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("company", company);
            return Const.post("CompanyAPI/UpdateCompanyInfo", paramMap, cb);
        }
    }

    public static class DeptService {
        /**
         * 获取所有API方法
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetAll(RequestCallback cb) {
            return Const.get("DeptService/GetAll", cb);
        }
    }

    public static class SystemLog {
        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetLogs(RequestCallback cb) {
            return Const.get("SystemLog/GetLogs", cb);
        }

        /**
         * 获取日志路径
         *
         * @param name
         * @param cb   回调
         */
        public static Callback.Cancelable GetLog(String name, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("name", name);
            return Const.get("SystemLog/GetLog", paramMap, cb);
        }

        /**
         * 从某角色删除用户
         *
         * @param name
         * @param cb   回调
         */
        public static Callback.Cancelable Remove(String name, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("name", name);
            return Const.post("SystemLog/Remove", paramMap, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable Empty(RequestCallback cb) {
            return Const.get("SystemLog/Empty", cb);
        }
    }

    public static class UCPaasService {
        /**
         * 发送注册短信
         *
         * @param Phone
         * @param val
         * @param cb    回调
         */
        public static Callback.Cancelable SendRegisterSMS(String Phone, String val, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(Phone);
            paramList.add(val);
            return Const.get("UCPaasService/SendRegisterSMS", paramList, cb);
        }

        /**
         * @param clientNum
         * @param cb        回调
         */
        public static Callback.Cancelable RemoveClient(String clientNum, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(clientNum);
            return Const.get("UCPaasService/RemoveClient", paramList, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetNoSetUsers(RequestCallback cb) {
            return Const.get("UCPaasService/GetNoSetUsers", cb);
        }
        /**
         * @param uid
         * @param clientType
         * @param charge
         * @param cb 回调
         */
        //        public static Callback.Cancelable AddClient(int uid,String clientType,ApiEntity.Double charge,RequestCallback cb) {
        //            List<Object> paramList = new ArrayList<Object>();
        //            paramList.add(uid);
        //            paramList.add(clientType);
        //            paramList.add(charge);
        //            return Const.get("UCPaasService/AddClient",paramList,cb);
        //        }

        /**
         * @param start
         * @param end
         * @param cb    回调
         */
        public static Callback.Cancelable GetClients(int start, int end, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(start);
            paramList.add(end);
            return Const.get("UCPaasService/GetClients", paramList, cb);
        }
    }

    public static class UserPubAPI {

        /**
         * @param upList
         * @param Type
         * @param cb     回调
         */
        public static Callback.Cancelable AddUserSetting(List<ApiEntity.UserSetting> upList, int Type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("upList", upList);
            paramMap.put("Type", Type);
            return Const.post("UserPubAPI/AddUserSetting", paramMap, cb);
        }

        /**
         * @param Type
         * @param cb   回调
         */
        public static Callback.Cancelable DelUserSetting(int Type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Type", Type);
            return Const.get("UserPubAPI/DelUserSetting", paramMap, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserSetting(RequestCallback cb) {
            return Const.get("UserPubAPI/GetUserSetting", cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserInfoByID(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("UserPubAPI/GetUserInfoByID", paramMap, cb);
        }

        /**
         * 修改用户密码
         *
         * @param oldPwd 旧密码
         * @param newPwd 新密码
         * @param cb     回调
         */
        public static Callback.Cancelable ModifyPassWord(String oldPwd, String newPwd, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("oldPwd", oldPwd);
            paramMap.put("newPwd", newPwd);
            return Const.post("UserPubAPI/ModifyPassWord", paramMap, cb);
        }

        /**
         * @param key
         * @param deptid
         * @param follow
         * @param cb     回调
         */
        public static Callback.Cancelable SearchUser(String key, int deptid, boolean follow, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            paramMap.put("deptid", deptid);
            paramMap.put("follow", follow);
            return Const.get("UserPubAPI/SearchUser", paramMap, cb);
        }

        /**
         * 增加公共用户{Name,email,tel,PASSWORD,CompanyCode}
         *
         * @param user
         * @param cb   回调
         */
        public static Callback.Cancelable AddPubUser(ApiEntity.UserInfo user, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("user", user);
            return Const.post("UserPubAPI/AddPubUser", paramMap, cb);
        }

        /**
         * 更新用户信息
         *
         * @param ui 用户信息对象
         * @param cb 回调
         */
        public static Callback.Cancelable ModifyUserById(ApiEntity.UserInfo ui, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ui", ui);
            return Const.post("UserPubAPI/ModifyUserById", paramMap, cb);
        }

        /**
         * 更新用户其他信息[工作经验、职业技能、大学、高中]
         *
         * @param ui   用户信息对象
         * @param type 类型【1工作经验、2职业技能、3大学、4高中】
         * @param cb   回调
         */
        public static Callback.Cancelable ModifyUserOtherById(ApiEntity.UserInfo ui, int type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ui", ui);
            paramMap.put("type", type);
            return Const.post("UserPubAPI/ModifyUserOtherById", paramMap, cb);
        }

        /**
         * 增加联系人
         *
         * @param pcObj
         * @param cb    回调
         */
        public static Callback.Cancelable AddPubUserContacts(ApiEntity.PubUserContacts pcObj, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("pcObj", pcObj);
            return Const.post("UserPubAPI/AddPubUserContacts", paramMap, cb);
        }

        /**
         * @param key
         * @param cb  回调
         */
        public static Callback.Cancelable GetSameUserContacts(String key, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(key);
            return Const.get("UserPubAPI/GetSameUserContacts", paramList, cb);
        }

        /**
         * 添加好友申请记录(并添加消息通知好友审批)
         *
         * @param conid 添加好友的ID
         * @param cb    回调
         */
        public static Callback.Cancelable AddPubConApply(int conid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("conid", conid);
            return Const.post("UserPubAPI/AddPubConApply", paramMap, cb);
        }

        /**
         * 处理好友申请(1同意申请，2拒绝好友申请)
         *
         * @param conid
         * @param state
         * @param cb    回调
         */
        public static Callback.Cancelable DoPubConApply(int conid, int state, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("conid", conid);
            paramMap.put("state", state);
            return Const.post("UserPubAPI/DoPubConApply", paramMap, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetPubConApplyByType(RequestCallback cb) {
            return Const.get("UserPubAPI/GetPubConApplyByType", cb);
        }

        /**
         * 获取可能认识的人列表
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetPossibleContactsList(RequestCallback cb) {
            return Const.get("UserPubAPI/GetPossibleContactsList", cb);
        }

        public static Callback.Cancelable GetPhoneStateByList(List<String> phoneList, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("phoneList", phoneList);
            return Const.get("UserPubAPI/GetPhoneStateByList", paramMap, cb);
        }

        /**
         * 修改联系人
         *
         * @param pcObj
         * @param cb    回调
         */
        public static Callback.Cancelable UpdatePubUserContacts(ApiEntity.PubUserContacts pcObj, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("pcObj", pcObj);
            return Const.post("UserPubAPI/UpdatePubUserContacts", paramMap, cb);
        }

        /**
         * 删除联系人
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DelPubUserContacts(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("UserPubAPI/DelPubUserContacts", paramMap, cb);
        }

        /**
         * 根据id获取联系人
         *
         * @param ID
         * @param cb 回调
         */
        public static Callback.Cancelable GetPubUserContactsById(int ID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ID", ID);
            return Const.get("UserPubAPI/GetPubUserContactsById", paramMap, cb);
        }

        /**
         * 根据用户id获取联系人
         *
         * @param UserID
         * @param cb     回调
         */
        public static Callback.Cancelable GetPubUserContactsListByUserId(int UserID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("UserID", UserID);
            return Const.get("UserPubAPI/GetPubUserContactsListByUserId", paramMap, cb);
        }

        /**
         * 增加联系人分组
         *
         * @param pcObj
         * @param cb    回调
         */
        public static Callback.Cancelable AddPubConGroups(ApiEntity.PubConGroups pcObj, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("pcObj", pcObj);
            return Const.post("UserPubAPI/AddPubConGroups", paramMap, cb);
        }

        /**
         * 修改联系人分组
         *
         * @param pcObj
         * @param cb    回调
         */
        public static Callback.Cancelable UpdatePubConGroups(ApiEntity.PubConGroups pcObj, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("pcObj", pcObj);
            return Const.post("UserPubAPI/UpdatePubConGroups", paramMap, cb);
        }

        /**
         * 删除联系人分组
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DelPubConGroups(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("UserPubAPI/DelPubConGroups", paramMap, cb);
        }

        /**
         * 根据id获取联系人分组
         *
         * @param ID
         * @param cb 回调
         */
        public static Callback.Cancelable GetPubConGroupsById(int ID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ID", ID);
            return Const.get("UserPubAPI/GetPubConGroupsById", paramMap, cb);
        }

        /**
         * 根据用户id获取联系人分组
         *
         * @param UserID
         * @param cb     回调
         */
        public static Callback.Cancelable GetPubConGroupsByUserId(int UserID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("UserID", UserID);
            return Const.get("UserPubAPI/GetPubConGroupsByUserId", paramMap, cb);
        }

        /**
         * 添加、修改用户围栏
         *
         * @param ur 用户围栏对象
         * @param cb 回调
         */
        public static Callback.Cancelable DoUserRail(ApiEntity.UserRail ur, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ur", ur);
            return Const.post("UserPubAPI/DoUserRail", paramMap, cb);
        }

        /**
         * 删除用户围栏
         *
         * @param id 记录id
         * @param cb 回调
         */
        public static Callback.Cancelable DelUserRail(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("UserPubAPI/DelUserRail", paramMap, cb);
        }

        /**
         * 获取用户围栏
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserRail(RequestCallback cb) {
            return Const.get("UserPubAPI/GetUserRail", cb);
        }

        /**
         * 添加围栏管理
         *
         * @param ur 围栏管理对象
         * @param cb 回调
         */
        public static Callback.Cancelable AddUserRailManage(ApiEntity.UserRailManage ur, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ur", ur);
            return Const.post("UserPubAPI/AddUserRailManage", paramMap, cb);
        }

        /**
         * 删除用户围栏
         *
         * @param id 记录id
         * @param cb 回调
         */
        public static Callback.Cancelable DelUserRailManage(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("UserPubAPI/DelUserRailManage", paramMap, cb);
        }

        /**
         * 获取用户围栏
         *
         * @param rid 围栏id
         * @param cb  回调
         */
        public static Callback.Cancelable GetRailManageByRid(int rid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("rid", rid);
            return Const.post("UserPubAPI/GetRailManageByRid", paramMap, cb);
        }

        /**
         * 获取我的用户围栏管理记录
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetMyRailManage(RequestCallback cb) {
            return Const.get("UserPubAPI/GetMyRailManage", cb);
        }

        /**
         * 注册公共用户
         *
         * @param user 用户对象【必须：邮箱、手机】
         * @param cb   回调
         */
        public static Callback.Cancelable JoinRegiter(ApiEntity.UserInfo user, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("user", user);
            return Const.post("UserPubAPI/JoinRegiter", paramMap, cb);
        }

        /**
         * @param email
         * @param cb    回调
         */
        public static Callback.Cancelable SendVerifyCode(String email, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("email", email);
            return Const.get("UserPubAPI/SendVerifyCode", paramMap, cb);
        }

        /**
         * @param code
         * @param cb   回调
         */
        public static Callback.Cancelable VerifyCode(String code, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("code", code);
            return Const.get("UserPubAPI/VerifyCode", paramMap, cb);
        }

        /**
         * @param code
         * @param pwd
         * @param cb   回调
         */
        public static Callback.Cancelable UpdatePassword(String code, String pwd, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("code", code);
            paramMap.put("pwd", pwd);
            return Const.get("UserPubAPI/UpdatePassword", paramMap, cb);
        }
    }

    public static class UserAPI {
        /**
         * @param key
         * @param val
         * @param cb  回调
         */
        public static Callback.Cancelable SetCache(String key, String val, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            paramMap.put("val", val);
            return Const.post("UserAPI/SetCache", paramMap, cb);
        }

        /**
         * @param key
         * @param cb  回调
         */
        public static Callback.Cancelable GetCacheByKey(String key, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            return Const.get("UserAPI/GetCacheByKey", paramMap, cb);
        }

        /**
         * @param Phone
         * @param cb    回调
         */
        public static Callback.Cancelable SendRegisterPhone(String Phone, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(Phone);
            return Const.get("UserAPI/SendRegisterPhone", paramList, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetAllDpt(RequestCallback cb) {
            return Const.get("UserAPI/GetAllDpt", cb);
        }

        /**
         * @param key
         * @param deptid
         * @param follow
         * @param cb     回调
         */
        public static Callback.Cancelable SearchUser(String key, int deptid, boolean follow, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            paramMap.put("deptid", deptid);
            paramMap.put("follow", follow);
            return Const.get("UserAPI/SearchUser", paramMap, cb);
        }

        /**
         * 根据邮箱获取用户信息
         *
         * @param email 邮箱
         * @param cb    回调
         */
        public static Callback.Cancelable GetUserInfoByEmail(String email, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("email", email);
            return Const.get("UserAPI/GetUserInfoByEmail", paramMap, cb);
        }

        /**
         * 根据用户姓名查询用户坐标
         *
         * @param username 用户姓名
         * @param cb       回调
         */
        public static Callback.Cancelable getUserByName(String username, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(username);
            return Const.get("UserAPI/getUserByName", paramList, cb);
        }

        /**
         * 获取全部用户坐标
         *
         * @param userids
         * @param cb      回调
         */
        public static Callback.Cancelable GetUserAxisList(List<Integer> userids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userids", userids);
            return Const.get("UserAPI/GetUserAxisList", paramMap, cb);
        }

        /**
         * 根据圈子id获取全部成员坐标
         *
         * @param groupid 圈子id
         * @param cb      回调
         */
        public static Callback.Cancelable GetNewUserAxisListByGroupId(int groupid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupid", groupid);
            return Const.get("UserAPI/GetNewUserAxisListByGroupId", paramMap, cb);
        }

        /**
         * 更新用户信息
         *
         * @param ui 用户信息对象
         * @param cb 回调
         */
        public static Callback.Cancelable ModifyUserById(ApiEntity.UserInfo ui, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ui", ui);
            return Const.get("UserAPI/ModifyUserById", paramMap, cb);
        }

        /**
         * 更新用户其他信息[工作经验、职业技能、大学、高中]
         *
         * @param ui   用户信息对象
         * @param type 类型【1工作经验、2职业技能、3大学、4高中】
         * @param cb   回调
         */
        public static Callback.Cancelable ModifyUserOtherById(ApiEntity.UserInfo ui, int type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ui", ui);
            paramMap.put("type", type);
            return Const.post("UserAPI/ModifyUserOtherById", paramMap, cb);
        }

        /**
         * 根据用户id更新坐标位置
         *
         * @param Axis 坐标位置值
         * @param cb   回调
         */
        public static Callback.Cancelable ModifyUserAxisById(String Axis, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Axis", Axis);
            return Const.get("UserAPI/ModifyUserAxisById", paramMap, cb);
        }

        /**
         * 保存关注
         *
         * @param fuids 用户id串
         * @param type  1增加关注，2取消关注
         * @param cb    回调
         */
        public static Callback.Cancelable DoFollow(List<Integer> fuids, int type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fuids", fuids);
            paramMap.put("type", type);
            return Const.post("UserAPI/DoFollow", paramMap, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable getFollowUserList(RequestCallback cb) {
            return Const.get("UserAPI/getFollowUserList", cb);
        }

        /**
         * 登录功能
         *
         * @param uid
         * @param key
         * @param returnUrl
         * @param cb        回调
         */
        public static Callback.Cancelable SignIn(int uid, String key, String returnUrl, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("uid", uid);
            paramMap.put("key", key);
            paramMap.put("returnUrl", returnUrl);
            return Const.get("signin", paramMap, cb);
        }

        /**
         * 获取当前登录用户信息
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetIdentity(RequestCallback cb) {
            return Const.get("UserAPI/GetIdentity", cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserInfoByID(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("UserPubAPI/GetUserInfoByID", paramMap, cb);
        }

        /**
         * 修改用户密码
         *
         * @param oldPwd 旧密码
         * @param newPwd 新密码
         * @param cb     回调
         */
        public static Callback.Cancelable ModifyPassWord(String oldPwd, String newPwd, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("oldPwd", oldPwd);
            paramMap.put("newPwd", newPwd);
            return Const.post("UserAPI/ModifyPassWord", paramMap, cb);
        }

        /**
         * @param token
         * @param cb    回调
         */
        public static Callback.Cancelable UpdateDeviceToken(String token, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("token", token);
            return Const.get("UserAPI/UpdateDeviceToken", paramMap, cb);
        }

        /**
         * 获取用户下级
         *
         * @param userid
         * @param cb     回调
         */
        public static Callback.Cancelable GetUserByParent(int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            return Const.get("UserAPI/GetUserByParent", paramMap, cb);
        }

        /**
         * 添加意见反馈
         *
         * @param ufb 意见反馈对象
         * @param cb  回调
         */
        public static Callback.Cancelable AddFeedBack(ApiEntity.UserFeedBack ufb, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ufb", ufb);
            return Const.post("UserAPI/AddFeedBack", paramMap, cb);
        }

        /**
         * 删除意见反馈
         *
         * @param id 记录id
         * @param cb 回调
         */
        public static Callback.Cancelable DelFeedBack(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("UserAPI/DelFeedBack", paramMap, cb);
        }

        /**
         * 获取意见反馈[uid=0全部，否则传用户id]
         *
         * @param uid 用户id
         * @param cb  回调
         */
        public static Callback.Cancelable GetFeedBack(int uid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("uid", uid);
            return Const.get("UserAPI/GetFeedBack", paramMap, cb);
        }

        /**
         * 添加、修改用户备注
         *
         * @param um 用户备注对象
         * @param cb 回调
         */
        public static Callback.Cancelable DoUserMark(ApiEntity.UserMark um, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("um", um);
            return Const.post("UserAPI/DoUserMark", paramMap, cb);
        }

        /**
         * 删除用户备注
         *
         * @param id 记录id
         * @param cb 回调
         */
        public static Callback.Cancelable DelUserMark(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("UserAPI/DelUserMark", paramMap, cb);
        }

        /**
         * 获取用户备注
         *
         * @param um 用户备注对象
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserMark(ApiEntity.UserMark um, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("um", um);
            return Const.post("UserAPI/GetUserMark", paramMap, cb);
        }

        /**
         * 添加、修改用户围栏
         *
         * @param ur 用户围栏对象
         * @param cb 回调
         */
        public static Callback.Cancelable DoUserRail(ApiEntity.UserRail ur, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ur", ur);
            return Const.post("UserAPI/DoUserRail", paramMap, cb);
        }

        /**
         * 删除用户围栏
         *
         * @param id 记录id
         * @param cb 回调
         */
        public static Callback.Cancelable DelUserRail(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("UserAPI/DelUserRail", paramMap, cb);
        }

        /**
         * 获取用户围栏
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserRail(RequestCallback cb) {
            return Const.get("UserAPI/GetUserRail", cb);
        }

        /**
         * 添加围栏管理
         *
         * @param ur 围栏管理对象
         * @param cb 回调
         */
        public static Callback.Cancelable AddUserRailManage(ApiEntity.UserRailManage ur, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ur", ur);
            return Const.post("UserAPI/AddUserRailManage", paramMap, cb);
        }

        /**
         * 删除用户围栏
         *
         * @param id 记录id
         * @param cb 回调
         */
        public static Callback.Cancelable DelUserRailManage(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("UserAPI/DelUserRailManage", paramMap, cb);
        }

        /**
         * 获取用户围栏
         *
         * @param rid 围栏id
         * @param cb  回调
         */
        public static Callback.Cancelable GetRailManageByRid(int rid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("rid", rid);
            return Const.post("UserAPI/GetRailManageByRid", paramMap, cb);
        }

        /**
         * 获取我的用户围栏管理记录
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetMyRailManage(RequestCallback cb) {
            return Const.get("UserAPI/GetMyRailManage", cb);
        }

        /**
         * @param upList
         * @param Type
         * @param cb     回调
         */
        public static Callback.Cancelable AddUserSetting(List<ApiEntity.UserSetting> upList, int Type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("upList", upList);
            paramMap.put("Type", Type);
            return Const.post("UserAPI/AddUserSetting", paramMap, cb);
        }

        /**
         * @param Type
         * @param cb   回调
         */
        public static Callback.Cancelable DelUserSetting(int Type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Type", Type);
            return Const.get("UserAPI/DelUserSetting", paramMap, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserSetting(RequestCallback cb) {
            return Const.get("UserAPI/GetUserSetting", cb);
        }

        /**
         * 添加二步验证码
         *
         * @param Code
         * @param UserID
         * @param cb     回调
         */
        public static Callback.Cancelable AddVerificationCode(String Code, int UserID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Code", Code);
            paramMap.put("UserID", UserID);
            return Const.post("UserAPI/AddVerificationCode", paramMap, cb);
        }

        /**
         * 添加用户唯一手机IMEI码
         *
         * @param IMEI
         * @param UserID
         * @param cb     回调
         */
        public static Callback.Cancelable UpdateUserIMEI(String IMEI, int UserID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("IMEI", IMEI);
            paramMap.put("UserID", UserID);
            return Const.post("UserAPI/UpdateUserIMEI", paramMap, cb);
        }

        /**
         * 获取用户绑定的唯一手机IMEI码
         *
         * @param UserID
         * @param cb     回调
         */
        public static Callback.Cancelable GetUserIMEI(int UserID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("UserID", UserID);
            return Const.post("UserAPI/GetUserIMEI", paramMap, cb);
        }

        /**
         * 根据手机IMEI码获取用户
         *
         * @param IMEI
         * @param cb   回调
         */
        public static Callback.Cancelable GetUserInfoForIMEI(String IMEI, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("IMEI", IMEI);
            return Const.post("UserAPI/GetUserInfoForIMEI", paramMap, cb);
        }

        /**
         * 开启两步验证
         *
         * @param Code
         * @param UserID
         * @param cb     回调
         */
        public static Callback.Cancelable OpenTwoVer(int Code, int UserID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Code", Code);
            paramMap.put("UserID", UserID);
            return Const.post("UserAPI/OpenTwoVer", paramMap, cb);
        }

        /**
         * 关闭两步验证
         *
         * @param UserID
         * @param cb     回调
         */
        public static Callback.Cancelable CloseTwoVer(int UserID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("UserID", UserID);
            return Const.post("UserAPI/CloseTwoVer", paramMap, cb);
        }

        /**
         * 验证二步验证码
         *
         * @param VarCode
         * @param UserID
         * @param cb      回调
         */
        public static Callback.Cancelable MatchVarCode(int VarCode, int UserID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("VarCode", VarCode);
            paramMap.put("UserID", UserID);
            return Const.post("UserAPI/MatchVarCode", paramMap, cb);
        }

        /**
         * 验证用户是否开启二步
         *
         * @param UserID
         * @param cb     回调
         */
        public static Callback.Cancelable IsOpenVar(int UserID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("UserID", UserID);
            return Const.post("UserAPI/IsOpenVar", paramMap, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable LoginSys(RequestCallback cb) {
            return Const.get("UserAPI/LoginSys", cb);
        }

        /**
         * 发送注册短信
         *
         * @param phone
         * @param cb    回调
         */
        public static Callback.Cancelable SendRegisterSMS(String phone, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("phone", phone);
            return Const.get("UserAPI/SendRegisterSMS", paramMap, cb);
        }

        /**
         * 验证注册短信
         *
         * @param Phone
         * @param Content
         * @param cb      回调
         */
        public static Callback.Cancelable CheckRegisterSMS(String Phone, String Content, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Phone", Phone);
            paramMap.put("Content", Content);
            return Const.get("UserAPI/CheckRegisterSMS", paramMap, cb);
        }
    }

    public static class Robot {
        /**
         * @param cb 回调
         */
        public static Callback.Cancelable EndPoint(RequestCallback cb) {
            return Const.post("Robot/bot/endpoint", cb);
        }

        /**
         * @param m
         * @param cb 回调
         */
        public static Callback.Cancelable SendToBot(String m, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("m", m);
            return Const.post("Robot/SendToBot", paramMap, cb);
        }

        /**
         * 根据提交方式GET/POST获取返回结果
         *
         * @param m
         * @param cb 回调
         */
        public static Callback.Cancelable GetResult(String m, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("m", m);
            return Const.post("Robot/GetResult", paramMap, cb);
        }

        /**
         * @param pageIndex
         * @param pageSize
         * @param cb        回调
         */
        public static Callback.Cancelable GetRecords(int pageIndex, int pageSize, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pageIndex);
            paramList.add(pageSize);
            return Const.get("Robot/GetRecords", paramList, cb);
        }
    }

    public static class Message {
        /**
         * 群组重要消息获取
         *
         * @param userID    当前用户ID
         * @param groupID   群组ID
         * @param pageIndex 页数
         * @param pageSize  条数
         * @param state     状态   1为重要信息
         * @param cb        回调
         * @return 结果
         */
        public static Callback.Cancelable GetGroupMessagesByState(int userID, String groupID, int pageIndex, int pageSize, int state, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(userID);
            paramList.add(groupID);
            paramList.add(pageIndex);
            paramList.add(pageSize);
            paramList.add(state);
            return Const.get("Message/GetGroupMessagesByState", paramList, cb);
        }

        /**
         * 群组内@信息查询
         *
         * @param MyName    当前用户姓名
         * @param MyUserID  当前用户Id
         * @param groupID   当前群组ID
         * @param pageIndex 页数
         * @param pageSize  条数
         * @param cb        回调
         * @return 结果
         */
        public static Callback.Cancelable GetMyPinMessages(String MyName, int MyUserID, int groupID, int pageIndex, int pageSize, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(MyName);
            paramList.add(MyUserID);
            paramList.add(groupID);
            paramList.add(pageIndex);
            paramList.add(pageSize);
            return Const.get("Message/GetMyPinMessages", paramList, cb);
        }

        /**
         * 个人重要消息获取
         *
         * @param senderID
         * @param pageIndex
         * @param pageSize
         * @param cb
         * @return
         */
        public static Callback.Cancelable GetChatImportanceMessages(int senderID, int pageIndex, int pageSize, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(senderID);
            paramList.add(pageIndex);
            paramList.add(pageSize);
            paramList.add(state);
            return Const.get("Message/GetChatImportanceMessages", paramList, cb);
        }

        /**
         * 个人收藏消息获取
         *
         * @param senderID
         * @param pageIndex
         * @param pageSize
         * @param cb
         * @return
         */
        public static Callback.Cancelable GetChatMyStarMessages(int senderID, int pageIndex, int pageSize, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(senderID);
            paramList.add(pageIndex);
            paramList.add(pageSize);
            paramList.add(state);
            return Const.get("Message/GetChatMyStarMessages", paramList, cb);
        }

        /**
         * 更新重要消息状态  (删除重要消息)
         *
         * @param id    消息ID
         * @param state 消息状态
         * @param cb    回调
         * @return 结果
         */
        public static Callback.Cancelable UpdateOptionsByID(String id, int state, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            paramMap.put("state", state);
            return Const.post("Message/UpdateOptionsByID", paramMap, cb);
        }

        /**
         * 获取我发布的【邮件、任务、约会】的动态列表信息
         *
         * @param s    页码
         * @param size 一页的记录数
         * @param cb   回调
         */
        public static Callback.Cancelable GetMyMessageNoteList(int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetMyMessageNoteList", paramMap, cb);
        }

        /**
         * POST方式请求远程页面数据
         *
         * @param message
         * @param cb      回调
         */
        public static Callback.Cancelable Post(ApiEntity.Message message, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("message", message);
            return Const.post("Message/Post", paramMap, cb);
        }

        /**
         * @param msgs
         * @param cmp
         * @param cb   回调
         */
        public static Callback.Cancelable SendOnly(List<ApiEntity.Message> msgs, String cmp, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("msgs", msgs);
            paramMap.put("cmp", cmp);
            return Const.post("Message/SendOnly", paramMap, cb);
        }

        /**
         * @param token
         * @param message
         * @param cb      回调
         */
        public static Callback.Cancelable SendByToken(String token, ApiEntity.Message message, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("token", token);
            paramMap.put("message", message);
            return Const.post("Message/SendByToken", paramMap, cb);
        }

        /**
         * 发送注册短信
         *
         * @param message
         * @param saveTwo
         * @param cb      回调
         */
        public static Callback.Cancelable Send(ApiEntity.Message message, boolean saveTwo, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("message", message);
            paramMap.put("saveTwo", saveTwo);
            return Const.post("Message/Send", paramMap, cb);
        }
        /**
         * @param message
         * @param saveTwo
         * @param cmd
         * @param cb 回调
         */
        //        public static Callback.Cancelable TransactionSend(ApiEntity.Message message,boolean saveTwo,ApiEntity.IDBCommand cmd,RequestCallback cb) {
        //            List<Object> paramList = new ArrayList<Object>();
        //            paramList.add(message);
        //            paramList.add(saveTwo);
        //            paramList.add(cmd);
        //            return Const.get("Message/TransactionSend",paramList,cb);
        //        }

        /**
         * @param message
         * @param cb      回调
         */
        public static Callback.Cancelable SendGroup(ApiEntity.Message message, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("message", message);
            return Const.post("Message/SendGroup", paramMap, cb);
        }

        /**
         * @param message
         * @param users
         * @param cb      回调
         */
        public static Callback.Cancelable SendGroupUsers(ApiEntity.Message message, List<Integer> users, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("message", message);
            paramMap.put("users", users);
            return Const.post("Message/SendGroupUsers", paramMap, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable AssignEndPoint(RequestCallback cb) {
            return Const.get("Message/assignserver", cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetOnlines(RequestCallback cb) {
            return Const.get("Message/GetOnlines", cb);
        }

        /**
         * @param senderID
         * @param pageIndex
         * @param pageSize
         * @param cb        回调
         */
        public static Callback.Cancelable GetChatMessages(int senderID, int pageIndex, int pageSize, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(senderID);
            paramList.add(pageIndex);
            paramList.add(pageSize);
            return Const.get("Message/GetChatMessages", paramList, cb);
        }

        /**
         * @param groupID
         * @param pageIndex
         * @param pageSize
         * @param cb        回调
         */
        public static Callback.Cancelable GetGroupFileMessages(String groupID, int pageIndex, int pageSize, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupID", groupID);
            paramMap.put("pageIndex", pageIndex);
            paramMap.put("pageSize", pageSize);
            return Const.get("Message/GetGroupFileMessages", paramMap, cb);
        }

        /**
         * @param groupID
         * @param pageIndex
         * @param pageSize
         * @param cb        回调
         */
        public static Callback.Cancelable GetGroupVideoAndPicMessages(String groupID, int pageIndex, int pageSize, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupID", groupID);
            paramMap.put("pageIndex", pageIndex);
            paramMap.put("pageSize", pageSize);
            return Const.get("Message/GetGroupVideoAndPicMessages", paramMap, cb);
        }

        /**
         * @param groupID
         * @param pageIndex
         * @param pageSize
         * @param cb        回调　获取群消息
         */
        public static Callback.Cancelable GetGroupMessages(String groupID, int pageIndex, int pageSize, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(groupID);
            paramList.add(pageIndex);
            paramList.add(pageSize);
            return Const.get("Message/GetGroupMessages", paramList, cb);
        }

        /**
         * @param groupID
         * @param cb      回调
         */
        public static Callback.Cancelable GetGroupApplyMessages(String groupID, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(groupID);
            return Const.get("Message/GetGroupApplyMessages", paramList, cb);
        }

        /**
         * @param pageIndex
         * @param pageSize
         * @param cb        回调
         */
        public static Callback.Cancelable GetAllGroupMessages(int pageIndex, int pageSize, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pageIndex);
            paramList.add(pageSize);
            return Const.get("Message/GetAllGroupMessages", paramList, cb);
        }

        /**
         * 获取用户新消息
         *
         * @param uid
         * @param cb  回调
         */
        public static Callback.Cancelable GetReceiveMessages(int uid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(uid);
            return Const.get("Message/GetReceiveMessages", paramList, cb);
        }

        /**
         * @param uid
         * @param cb  回调
         */
        public static Callback.Cancelable GetLastMessages(int uid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(uid);
            return Const.get("Message/GetLastMessages", paramList, cb);
        }

        /**
         * @param sender
         * @param pageIndex
         * @param pageCount
         * @param cb        回调
         */
        public static Callback.Cancelable GetBakMessages(int sender, int pageIndex, int pageCount, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(sender);
            paramList.add(pageIndex);
            paramList.add(pageCount);
            return Const.get("Message/GetBakMessages", paramList, cb);
        }

        /**
         * @param senderID
         * @param type
         * @param cb       回调
         */
        public static Callback.Cancelable RemoveNewMessageBySenderID(int senderID, int type, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(senderID);
            paramList.add(type);
            return Const.get("Message/RemoveNewMessageBySenderID", paramList, cb);
        }

        /**
         * @param senders
         * @param type
         * @param cb      回调
         */
        public static Callback.Cancelable RemoveMesssageBySenders(String senders, int type, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(senders);
            paramList.add(type);
            return Const.get("Message/RemoveMesssageBySenders", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable RemoveMessageByID(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("Message/RemoveMessageByID", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable RemoveAllMessageByID(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("Message/RemoveAllMessageByID", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable RemoveMessageBakByID(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("Message/RemoveMessageBakByID", paramList, cb);
        }

        /**
         * @param gid
         * @param cb  回调
         */
        public static Callback.Cancelable RemoveMessageByGroupID(int gid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(gid);
            return Const.get("Message/RemoveMessageByGroupID", paramList, cb);
        }

        /**
         * @param content
         * @param id
         * @param cb      回调
         */
        public static Callback.Cancelable UpdateMessageByID(String content, int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("content", content);
            paramMap.put("id", id);
            return Const.post("Message/UpdateMessageByID", paramMap, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetChatRecords(RequestCallback cb) {
            return Const.get("Message/GetChatRecords", cb);
        }

        /**
         * @param receiverID
         * @param type
         * @param cb         回调
         */
        public static Callback.Cancelable RemoveChatRecord(int receiverID, int type, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(receiverID);
            paramList.add(type);
            return Const.get("Message/RemoveChatRecord", paramList, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetNewMessageCount(RequestCallback cb) {
            return Const.get("Message/GetNewMessageCount", cb);
        }

        /**
         * @param token
         * @param cb    回调
         */
        public static Callback.Cancelable ResetPushCount(String token, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("token", token);
            return Const.get("Message/ResetPushCount", paramMap, cb);
        }

        /**
         * @param message
         * @param cb      回调
         */
        public static Callback.Cancelable Notice(ApiEntity.Message message, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(message);
            return Const.post("Message/Notice", paramList, cb);
        }

        /**
         * @param pageIndex
         * @param pageCount
         * @param cb        回调
         */
        public static Callback.Cancelable GetUserWorkMessages(int pageIndex, int pageCount, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pageIndex);
            paramList.add(pageCount);
            return Const.get("Message/GetUserWorkMessages", paramList, cb);
        }

        /**
         * @param pageIndex
         * @param pageCount
         * @param cb        回调
         */
        public static Callback.Cancelable GetUserNoticeMessages(int pageIndex, int pageCount, RequestCallback cb) {
//            List<Object> paramList = new ArrayList<Object>();
//            paramList.add(pageIndex);
//            paramList.add(pageCount);
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("pageIndex", pageIndex);
            paramMap.put("pageCount", pageCount);
            return Const.get("Message/GetUserNoticeMessages", paramMap, cb);
        }

        /**
         * @param bt
         * @param cb 回调
         */
        public static Callback.Cancelable ClearUnReadByBusType(int bt, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(bt);
            return Const.get("Message/ClearUnReadByBusType", paramList, cb);
        }
    }

    public static class PubTalkerAPI {
        /**
         * 获取talker记录
         *
         * @param loadtype   talk类型，0加载全部类型(loadtype--0所有 1我关注的 2提到我的 3我发布的 4我的小组 5某人发布的 6某个 7某个类型的 9图片 10投票 11连接 12文件)
         * @param id         暂时不用，传0
         * @param s          页码
         * @param size       一页的记录数
         * @param ProjectIds
         * @param GroupID
         * @param cb         回调
         */
        public static Callback.Cancelable LoadTalkers(int loadtype, int id, int s, int size, String ProjectIds, int GroupID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("loadtype", loadtype);
            paramMap.put("id", id);
            paramMap.put("s", s);
            paramMap.put("size", size);
            paramMap.put("ProjectIds", ProjectIds);
            paramMap.put("GroupID", GroupID);
            return Const.get("PubTalkerAPI/LoadTalkers", paramMap, cb);
        }
    }

    public static class TalkerAPI {
        /**
         * 设置群屏蔽信息      后台逻辑：给groupList中添加自己的ID
         *
         * @param gid
         * @param mh
         * @param cb
         * @return
         */
        public static Callback.Cancelable DoChatterGroupMsgHideByGid(int gid, int mh, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<>();
            paramMap.put("gid", gid);
            paramMap.put("mh", mh);
            return Const.post("TalkerAPI/DoChatterGroupMsgHideByGid", paramMap, cb);
        }

        /**
         * 根据动态id将人员添加进【邮件、任务、约会、服务活动】的动态中
         *
         * @param noteid   动态id
         * @param mesageid 消息ID
         * @param isgroup  false 个人    true群组
         * @param cb       回调
         */
        public static Callback.Cancelable AddUserToMessageByNoteID(int noteid, int mesageid, boolean isgroup, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("noteid", noteid);
            paramMap.put("messageid", mesageid);
            paramMap.put("isgroup", isgroup);
            return Const.post("TalkerAPI/AddUserToMessageByNoteID", paramMap, cb);
        }

        /**
         * 获取我发布的【邮件、任务、约会】的动态列表信息
         *
         * @param s    页码
         * @param size 一页的记录数
         * @param cb   回调
         */
        public static Callback.Cancelable GetMyMessageNoteList(int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetMyMessageNoteList", paramMap, cb);
        }

        /**
         * 根据项目组id获取附件
         *
         * @param teamid 项目组id
         * @param s
         * @param size
         * @param cb     回调
         */
        public static Callback.Cancelable GetFileByTeamId(int teamid, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("teamid", teamid);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetFileByTeamId", paramMap, cb);
        }

        /**
         * 根据项目组id获取附件
         *
         * @param ProjectIds 项目id串
         * @param cb         回调
         */
        public static Callback.Cancelable GetFileByProjectId(String ProjectIds, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ProjectIds", ProjectIds);
            return Const.get("TalkerAPI/GetFileByProjectId", paramMap, cb);
        }

        /**
         * 根据项目组id获取任务
         *
         * @param teamid 项目组id
         * @param cb     回调
         */
        public static Callback.Cancelable GetTaskByTeamId(int teamid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("teamid", teamid);
            return Const.get("TalkerAPI/GetTaskByTeamId", paramMap, cb);
        }

        /**
         * 根据用户id和起止时间获取任务
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetTaskByTimeSpan(int state, int uid, String startTime, String endTime, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("state", state);
            paramMap.put("uid", uid);
            paramMap.put("start", startTime);
            paramMap.put("end", endTime);
            return Const.get("TalkerAPI/GetTaskByTimeSpan", paramMap, cb);
        }

        /**
         * 根据用户获取工作相关
         *
         * @param userid 用户id
         * @param s      页码
         * @param size   记录数
         * @param cb     回调
         */
        public static Callback.Cancelable GetUserJobLineByUser(int userid, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetUserJobLineByUser", paramMap, cb);
        }

        /**
         * 根据用户获取工作相关
         *
         * @param userid 用户id
         * @param s      页码
         * @param size   记录数
         * @param cb     回调
         */
        public static Callback.Cancelable GetUserJobLineByUserTest(int userid, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetUserJobLineByUserTest", paramMap, cb);
        }

        /**
         * 根据用户获取粉丝列表
         *
         * @param userid 用户id
         * @param cb     回调
         */
        public static Callback.Cancelable GetMyFans(int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            return Const.get("TalkerAPI/GetMyFans", paramMap, cb);
        }

        /**
         * 根据用户获取标签
         *
         * @param userid 用户id
         * @param cb     回调
         */
        public static Callback.Cancelable GetUserLabel(int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            return Const.get("TalkerAPI/GetUserLabel", paramMap, cb);
        }

        /**
         * 添加标签
         *
         * @param ul 用户标签对象
         * @param cb 回调
         */
        public static Callback.Cancelable AddUserLabel(ApiEntity.UserLabel ul, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ul", ul);
            return Const.post("TalkerAPI/AddUserLabel", paramMap, cb);
        }

        /**
         * 修改用户标签
         *
         * @param ul 用户标签对象
         * @param cb 回调
         */
        public static Callback.Cancelable ModifyUserLabel(ApiEntity.UserLabel ul, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ul", ul);
            return Const.post("TalkerAPI/ModifyUserLabel", paramMap, cb);
        }

        /**
         * 根据记录id删除标签
         *
         * @param id 标签id
         * @param cb 回调
         */
        public static Callback.Cancelable DelUserLabel(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("TalkerAPI/DelUserLabel", paramMap, cb);
        }

        /**
         * 添加Talker日志
         *
         * @param id
         * @param type
         * @param content
         * @param cb      回调
         */
        public static Callback.Cancelable AddTalkerLog(int id, int type, String content, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            paramList.add(type);
            paramList.add(content);
            return Const.get("TalkerAPI/AddTalkerLog", paramList, cb);
        }

        /**
         * 收藏/取消收藏talker
         *
         * @param nid  talker的记录id
         * @param flag 0收藏，1取消收藏
         * @param cb   回调
         */
        public static Callback.Cancelable DoEnjoyTalker(int nid, int flag, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("nid", nid);
            paramMap.put("flag", flag);
            return Const.post("TalkerAPI/DoEnjoyTalker", paramMap, cb);
        }

        /**
         * @param Content
         * @param MessageType
         * @param BusTypes
         * @param ReceiverID
         * @param cb          回调
         */
        public static Callback.Cancelable SendCallMessage(String Content, int MessageType, int BusTypes, int ReceiverID, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(Content);
            paramList.add(MessageType);
            paramList.add(BusTypes);
            paramList.add(ReceiverID);
            return Const.get("TalkerAPI/SendCallMessage", paramList, cb);
        }

        /**
         * 发布Talker/发布项目组Message【需传TeamId】
         *
         * @param note 发布Talker的JSON对象UserNote
         * @param cb   回调
         */
        public static Callback.Cancelable SaveTalker(ApiEntity.UserNote note, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("note", note);
            return Const.post("TalkerAPI/SaveTalker", paramMap, cb);
        }

        /**
         * 获取最新被收藏的动态
         *
         * @param pageSize  记录数
         * @param pageIndex 页码
         * @param cb        回调
         */
        public static Callback.Cancelable GetEnjoyNoteList(int pageSize, int pageIndex, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("pageSize", pageSize);
            paramMap.put("pageIndex", pageIndex);
            return Const.get("TalkerAPI/GetEnjoyNoteList", paramMap, cb);
        }

        /**
         * 获取talker记录
         *
         * @param search
         * @param loadtype   talk类型，0加载全部类型(loadtype--0所有 1我关注的 2提到我的 3我发布的 4我的小组 5某人发布的 6某个 7某个类型的 9图片 10投票 11连接 12文件)
         * @param id         暂时不用，传0
         * @param s          页码
         * @param size       一页的记录数
         * @param ProjectIds
         * @param cb         回调
         */
        public static Callback.Cancelable LoadTalker(String search, int loadtype, int id, int s, int size, String ProjectIds, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("search", search);
            paramMap.put("loadtype", loadtype);
            paramMap.put("id", id);
            paramMap.put("s", s);
            paramMap.put("size", size);
            paramMap.put("ProjectIds", ProjectIds);
            return Const.get("TalkerAPI/LoadTalker", paramMap, cb);
        }

        /**
         * 获取talker记录
         *
         * @param loadtype   talk类型，0加载全部类型(loadtype--0所有 1我关注的 2提到我的 3我发布的 4我的小组 5某人发布的 6某个 7某个类型的 9图片 10投票 11连接 12文件)
         * @param id         暂时不用，传0
         * @param s          页码
         * @param size       一页的记录数
         * @param ProjectIds
         * @param GroupID
         * @param cb         回调
         */
        public static Callback.Cancelable LoadTalkers(int loadtype, int id, int s, int size, String ProjectIds, int GroupID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("loadtype", loadtype);
            paramMap.put("id", id);
            paramMap.put("s", s);
            paramMap.put("size", size);
            paramMap.put("ProjectIds", ProjectIds);
            paramMap.put("GroupID", GroupID);
            return Const.get("TalkerAPI/LoadTalkers", paramMap, cb);
        }

        /**
         * @param rid
         * @param pageIndex
         * @param pageSize
         * @param cb        回调
         */
        public static Callback.Cancelable LoadTalkerByRID(int rid, int pageIndex, int pageSize, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("rid", rid);
            paramMap.put("pageIndex", pageIndex);
            paramMap.put("pageSize", pageSize);
            return Const.get("TalkerAPI/LoadTalkerByRID", paramMap, cb);
        }

        /**
         * 保存Talker回复
         *
         * @param rev 保存回复的JSON对象UserNote
         * @param cb  回调
         */
        public static Callback.Cancelable SaveTalkerRev(ApiEntity.UserNote rev, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("rev", rev);
            return Const.post("TalkerAPI/SaveTalkerRev", paramMap, cb);
        }

        /**
         * 根据id删除Talker
         *
         * @param id 记录id
         * @param cb 回调
         */
        public static Callback.Cancelable DeleteTalker(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("TalkerAPI/DeleteTalker", paramMap, cb);
        }

        /**
         * 根据id和父id删除Talker回复
         *
         * @param id  记录id
         * @param pid 父id
         * @param cb  回调
         */
        public static Callback.Cancelable DeleteTalkerRev(int id, int pid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            paramMap.put("pid", pid);
            return Const.post("TalkerAPI/DeleteTalkerRev", paramMap, cb);
        }

        /**
         * 根据id修改talker或者项目组消息
         *
         * @param un Talker对象
         * @param cb 回调
         */
        public static Callback.Cancelable ModifyNewTalkerById(ApiEntity.UserNote un, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("un", un);
            return Const.post("TalkerAPI/ModifyNewTalkerById", paramMap, cb);
        }

        /**
         * 根据id修改talker属性值
         *
         * @param pro 新的属性值
         * @param id  记录id
         * @param cb  回调
         */
        public static Callback.Cancelable ModifyTalkerById(String pro, int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("pro", pro);
            paramMap.put("id", id);
            return Const.post("TalkerAPI/ModifyTalkerById", paramMap, cb);
        }

        /**
         * 根据Typeid获取talker信息
         *
         * @param Property 要修改的任务、计划实体
         * @param TypeId   根据Typeid获取talker信息
         * @param cb       回调
         */
        public static Callback.Cancelable ModifyTalkerByTypeId(String Property, int TypeId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Property", Property);
            paramMap.put("TypeId", TypeId);
            return Const.post("TalkerAPI/ModifyTalkerByTypeId", paramMap, cb);
        }

        /**
         * 根据Typeid获取talker信息
         *
         * @param TypeId 根据Typeid获取talker信息
         * @param cb     回调
         */
        public static Callback.Cancelable getTalkerByTypeId(int TypeId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("TypeId", TypeId);
            return Const.get("TalkerAPI/getTalkerByTypeId", paramMap, cb);
        }

        /**
         * 根据群组id获取talker列表
         *
         * @param gid 群组id
         * @param cb  回调
         */
        public static Callback.Cancelable getTalkerByGroupID(int gid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            return Const.get("TalkerAPI/getTalkerByGroupID", paramMap, cb);
        }

        /**
         * 根据记录id获取talker信息
         *
         * @param id 记录id
         * @param cb 回调
         */
        public static Callback.Cancelable getTalkerById(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("TalkerAPI/getTalkerById", paramMap, cb);
        }

        /**
         * 根据记录id获取talker回复
         *
         * @param talkerid 记录id
         * @param cb       回调
         */
        public static Callback.Cancelable getTalkerRevByTalkerId(int talkerid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("talkerid", talkerid);
            return Const.get("TalkerAPI/getTalkerRevByTalkerId", paramMap, cb);
        }

        /**
         * 获取与我相关的talker回复
         *
         * @param key
         * @param s
         * @param size
         * @param cb   回调
         */
        public static Callback.Cancelable getTalkerRevBySelf(String key, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/getTalkerRevBySelf", paramMap, cb);
        }

        /**
         * 获取与我相关的talker转发
         *
         * @param key
         * @param s
         * @param size
         * @param cb   回调
         */
        public static Callback.Cancelable getShareTalkerBySelf(String key, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/getShareTalkerBySelf", paramMap, cb);
        }

        /**
         * 根据关键字获取全部日程
         *
         * @param key
         * @param state
         * @param cb    回调
         */
        public static Callback.Cancelable GetCalenderByKey(String key, int state, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            paramMap.put("state", state);
            return Const.get("TalkerAPI/GetCalenderByKey", paramMap, cb);
        }

        /**
         * 根据父id获取投票回复
         *
         * @param PId 父id
         * @param cb  回调
         */
        public static Callback.Cancelable getVoteRevByPId(int PId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("PId", PId);
            return Const.get("TalkerAPI/getVoteRevByPId", paramMap, cb);
        }

        /**
         * 增加多个群组成员
         *
         * @param gid
         * @param userids 要添加的用户id列表
         * @param cb      回调
         */
        public static Callback.Cancelable AddGroupUsers(int gid, List<Integer> userids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            paramMap.put("userids", userids);
            return Const.post("TalkerAPI/AddGroupUsers", paramMap, cb);
        }

        /**
         * 增加多个群组成员
         *
         * @param gid
         * @param userid
         * @param cb     回调
         */
        public static Callback.Cancelable AddGroupUser(int gid, int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            paramMap.put("userid", userid);
            return Const.post("TalkerAPI/AddGroupUser", paramMap, cb);
        }

        /**
         * 删除群组成员
         *
         * @param gid    群组id
         * @param userid 要删除的用户id
         * @param cb     回调
         */
        public static Callback.Cancelable DelGroupUser(int gid, int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            paramMap.put("userid", userid);
            return Const.post("TalkerAPI/DelGroupUser", paramMap, cb);
        }

        /**
         * 删除多个群组成员
         *
         * @param gid 群组id
         * @param cb  回调
         */
        public static Callback.Cancelable DelGroupAllUser(int gid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            return Const.post("TalkerAPI/DelGroupAllUser", paramMap, cb);
        }

        /**
         * 删除群组成员
         *
         * @param gid 群组id
         * @param cb  回调
         */
        public static Callback.Cancelable DelGroup(int gid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            return Const.post("TalkerAPI/DelGroup", paramMap, cb);
        }

        /**
         * 增加、修改群组
         *
         * @param group   群组信息的JSON对象ChatterGroup
         * @param userids List=int添加其他成员
         * @param cb      回调
         */
        public static Callback.Cancelable SaveChatterGroup(ApiEntity.ChatterGroup group, List<Integer> userids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("group", group);
            paramMap.put("userids", userids);
            return Const.post("TalkerAPI/SaveChatterGroup", paramMap, cb);
        }

        /**
         * 群组管理权转让(是否直接退出群组)
         *
         * @param gid   群组id
         * @param actor 群组接管人id
         * @param cb    回调
         */
        public static Callback.Cancelable OutChatterGroupByCreator(int gid, int actor, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            paramMap.put("actor", actor);
            return Const.post("TalkerAPI/OutChatterGroupByCreator", paramMap, cb);
        }

        /**
         * 同意加入群组的申请
         *
         * @param gid 群组id
         * @param uid 用户id
         * @param cb  回调
         */
        public static Callback.Cancelable AgreeJoinGroupUser(int gid, int uid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            paramMap.put("uid", uid);
            return Const.post("TalkerAPI/AgreeJoinGroupUser", paramMap, cb);
        }

        /**
         * 处理加入群组的申请
         *
         * @param gid       群组id
         * @param uid       用户id
         * @param type      1表示同意，2表示拒绝
         * @param content   处理的JSON
         * @param mid       消息id
         * @param groupName 组名称
         * @param cb        回调
         */
        public static Callback.Cancelable DoJoinGroupUser(int gid, int uid, int type, String content, int mid, String groupName, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            paramMap.put("uid", uid);
            paramMap.put("type", type);
            paramMap.put("content", content);
            paramMap.put("mid", mid);
            paramMap.put("groupName", groupName);
            return Const.post("TalkerAPI/DoJoinGroupUser", paramMap, cb);
        }

        /**
         * 申请加入群组
         *
         * @param group 群组对象
         * @param cb    回调
         */
        public static Callback.Cancelable JoinToGroup(ApiEntity.ChatterGroup group, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("group", group);
            return Const.post("TalkerAPI/JoinToGroup", paramMap, cb);
        }

        /**
         * 加载群组
         *
         * @param name     群组名称的关键字
         * @param isAll    是否加载全部（true加载全部，false加载我的群组）
         * @param teamType 0普通群组，1项目组
         * @param IsTalker true加载项目组关联业务数量，false不加载
         * @param cb       回调
         */
        public static Callback.Cancelable LoadGroups(String name, boolean isAll, int teamType, boolean IsTalker, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("name", name);
            paramMap.put("isAll", isAll);
            paramMap.put("teamType", teamType);
            paramMap.put("IsTalker", IsTalker);
            return Const.get("TalkerAPI/LoadGroups", paramMap, cb);
        }

        /**
         * 加载群组(分页)
         *
         * @param name      群组名称的关键字
         * @param isAll     是否加载全部（true加载全部，false加载我的群组）
         * @param teamType  0普通群组，1项目组
         * @param PageIndex 页码
         * @param PageSize  页记录
         * @param IsTalker  true加载项目组关联业务数量，false不加载
         * @param cb        回调
         */
        public static Callback.Cancelable LoadGroupsPage(String name, boolean isAll, int teamType, int PageIndex, int PageSize, boolean IsTalker, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("name", name);
            paramMap.put("isAll", isAll);
            paramMap.put("teamType", teamType);
            paramMap.put("PageIndex", PageIndex);
            paramMap.put("PageSize", PageSize);
            paramMap.put("IsTalker", IsTalker);
            return Const.get("TalkerAPI/LoadGroupsPage", paramMap, cb);
        }

        /**
         * 加载群组
         *
         * @param name     群组名称的关键字
         * @param isAll    是否加载全部（true加载全部，false加载我的群组）
         * @param teamType 0普通群组，1项目组
         * @param cb       回调
         */
        public static Callback.Cancelable LoadSimpleGroups(String name, boolean isAll, int teamType, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("name", name);
            paramMap.put("isAll", isAll);
            paramMap.put("teamType", teamType);
            return Const.get("TalkerAPI/LoadSimpleGroups", paramMap, cb);
        }

        /**
         * 加载群组
         *
         * @param name     群组名称的关键字
         * @param isAll    是否加载全部（true加载全部，false加载我的群组）
         * @param teamType 0普通群组，1项目组
         * @param cb       回调
         */
        public static Callback.Cancelable LoadGroupsTask(String name, boolean isAll, int teamType, int state, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("name", name);
            paramMap.put("isAll", isAll);
            paramMap.put("teamType", teamType);
            paramMap.put("state", state);
            return Const.get("TalkerAPI/LoadGroupsTask", paramMap, cb);
        }

        /**
         * 根据群组id获取群组
         *
         * @param groupids
         * @param cb       回调
         */
        public static Callback.Cancelable GetChatterGroupByIds(String groupids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupids", groupids);
            return Const.get("TalkerAPI/GetChatterGroupByIds", paramMap, cb);
        }

        /**
         * 根据群组id获取群组
         *
         * @param groupid
         * @param cb      回调
         */
        public static Callback.Cancelable GetChatterGroupById(int groupid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupid", groupid);
            return Const.get("TalkerAPI/GetChatterGroupById", paramMap, cb);
        }

        /**
         * 根据群组id获取群组人员
         *
         * @param gid 群组id
         * @param cb  回调
         */
        public static Callback.Cancelable LoadGroupUsersByGid(int gid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("gid", gid);
            return Const.get("TalkerAPI/LoadGroupUsersByGid", paramMap, cb);
        }

        /**
         * 增加任务
         *
         * @param ufp 任务信息的JSON对象UserFenPai
         * @param cb  回调
         */
        public static Callback.Cancelable AddFenPai(ApiEntity.UserFenPai ufp, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ufp", ufp);
            return Const.post("TalkerAPI/AddFenPai", paramMap, cb);
        }

        /**
         * 增加任务
         *
         * @param ufp 任务信息的JSON对象UserFenPai
         * @param cb  回调
         */
        public static Callback.Cancelable AddFenPaiPc(ApiEntity.UserFenPai ufp, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ufp", ufp);
            return Const.post("TalkerAPI/AddFenPaiPc", paramMap, cb);
        }

        /**
         * 根据任务id删除任务
         *
         * @param taskid 任务id
         * @param cb     回调
         */
        public static Callback.Cancelable DelTaskById(int taskid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("taskid", taskid);
            return Const.post("TalkerAPI/DelTaskById", paramMap, cb);
        }

        /**
         * 更新任务流程状态
         *
         * @param ufp 任务对象【FlowState：1普通，2提交审核，3返工】
         * @param cb  回调
         */
        public static Callback.Cancelable ModifyTaskFlowState(ApiEntity.UserFenPai ufp, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ufp", ufp);
            return Const.post("TalkerAPI/ModifyTaskFlowState", paramMap, cb);
        }

        /**
         * 删除任务文件信息
         *
         * @param ufp 任务对象
         * @param cb  回调
         */
        public static Callback.Cancelable DelTaskFile(ApiEntity.UserFenPai ufp, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ufp", ufp);
            return Const.post("TalkerAPI/DelTaskFile", paramMap, cb);
        }

        /**
         * 修改任务信息
         *
         * @param ufp 任务信息的JSON对象UserFenPai
         * @param cb  回调
         */
        public static Callback.Cancelable ModifySimpleTask(ApiEntity.UserFenPai ufp, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ufp", ufp);
            return Const.post("TalkerAPI/ModifySimpleTask", paramMap, cb);
        }

        /**
         * 更新任务流程状态
         *
         * @param ufp 任务对象【FlowState：1普通，2提交审核，3返工】
         * @param cb  回调
         */
        public static Callback.Cancelable ModifyTask(ApiEntity.UserFenPai ufp, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ufp", ufp);
            return Const.post("TalkerAPI/ModifyTask", paramMap, cb);
        }

        /**
         * 根据id串获取任务id
         *
         * @param ids 任务id串
         * @param cb  回调
         */
        public static Callback.Cancelable GetTaskByIds(String ids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ids", ids);
            return Const.get("TalkerAPI/GetTaskByIds", paramMap, cb);
        }

        /**
         * 根据用户id获取任务记录(type=0(我创建)1(我参与)2(我负责))
         *
         * @param userid 用户id
         * @param cb     回调
         */
        public static Callback.Cancelable GetTaskByUserId(int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            return Const.get("TalkerAPI/GetTaskByUserId", paramMap, cb);
        }

        /**
         * 根据用户id获取任务记录(type=0(我创建)1(我参与)2(我负责))
         *
         * @param userid 用户id
         * @param s
         * @param size
         * @param cb     回调
         */
        public static Callback.Cancelable GetTaskOnPageByUserId(int userid, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetTaskOnPageByUserId", paramMap, cb);
        }

        /**
         * 根据id串获取任务id
         *
         * @param type
         * @param cb   回调
         */
        public static Callback.Cancelable GetTask(int type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("type", type);
            return Const.get("TalkerAPI/GetTask", paramMap, cb);
        }

        /**
         * 根据项目id获取任务记录
         *
         * @param state 任务状态值（1要做的，2在做的，3已完成）
         * @param cb    回调
         */
        public static Callback.Cancelable GetMobileProjectByTaskState(int state, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("state", state);
            return Const.get("TalkerAPI/GetMobileProjectByTaskState", paramMap, cb);
        }

        /**
         * 根据团队id归档
         *
         * @param id 项目id
         * @param cb 回调
         */
        public static Callback.Cancelable ArchiveProjectById(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("TalkerAPI/ArchiveProjectById", paramList, cb);
        }

        /**
         * 根据项目id获取任务记录
         *
         * @param pidStr 项目id串
         * @param cb     回调
         */
        public static Callback.Cancelable GetTaskByProjectId(String pidStr, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("pidStr", pidStr);
            return Const.get("TalkerAPI/GetTaskByProjectId", paramMap, cb);
        }

        /**
         * 根据任务状态和用户id获取任务（分页）
         *
         * @param search 搜索关键字
         * @param state  任务状态
         * @param s      页码
         * @param size   页记录数
         * @param cb     回调
         */
        public static Callback.Cancelable GetTaskByStateAndPage(String search, int state, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("search", search);
            paramMap.put("state", state);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetTaskByStateAndPage", paramMap, cb);
        }

        /**
         * 根据任务状态和用户id获取任务（分页）
         *
         * @param state 任务状态
         * @param cb    回调
         */
        public static Callback.Cancelable GetTaskByState(int state, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("state", state);
            return Const.get("TalkerAPI/GetTaskByState", paramMap, cb);
        }

        /**
         * 根据任务id更新任务状态
         *
         * @param state  任务状态
         * @param taskid 任务id
         * @param cb     回调
         */
        public static Callback.Cancelable UpdateTaskState(int state, int taskid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("state", state);
            paramMap.put("taskid", taskid);
            return Const.post("TalkerAPI/UpdateTaskState", paramMap, cb);
        }

        /**
         * 根据任务id更新任务状态
         *
         * @param fileStr 文件字符串
         * @param taskid  任务id
         * @param cb      回调
         */
        public static Callback.Cancelable UpdateTaskFile(String fileStr, int taskid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fileStr", fileStr);
            paramMap.put("taskid", taskid);
            return Const.post("TalkerAPI/UpdateTaskFile", paramMap, cb);
        }

        /**
         * 根据项目组id获取项目
         *
         * @param teamid
         * @param cb     回调
         */
        public static Callback.Cancelable GetProjectByTeamId(int teamid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("teamid", teamid);
            return Const.get("TalkerAPI/GetProjectByTeamId", paramMap, cb);
        }

        /**
         * 根据项目组id串获取项目
         *
         * @param TeamStr
         * @param cb      回调
         */
        public static Callback.Cancelable GetProjectByTeamStr(String TeamStr, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("TeamStr", TeamStr);
            return Const.get("TalkerAPI/GetProjectByTeamStr", paramMap, cb);
        }

        /**
         * 根据项目id获取项目
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetProjectById(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("TalkerAPI/GetProjectById", paramMap, cb);
        }

        /**
         * 根据状态值获取项目
         *
         * @param state
         * @param cb    回调
         */
        public static Callback.Cancelable GetProjectByState(int state, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("state", state);
            return Const.get("TalkerAPI/GetProjectByState", paramMap, cb);
        }

        /**
         * 根据用户id获取项目
         *
         * @param userid
         * @param cb     回调
         */
        public static Callback.Cancelable GetProjectByUserId(int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            return Const.get("TalkerAPI/GetProjectByUserId", paramMap, cb);
        }

        /**
         * 根据用户id获取项目
         *
         * @param userid
         * @param s
         * @param size
         * @param cb     回调
         */
        public static Callback.Cancelable GetProjectOnPageByUserId(int userid, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetProjectOnPageByUserId", paramMap, cb);
        }

        /**
         * 获取项目
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetSimpleProject(RequestCallback cb) {
            return Const.get("TalkerAPI/GetSimpleProject", cb);
        }

        /**
         * 根据项目组id获取项目
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetProject(RequestCallback cb) {
            return Const.get("TalkerAPI/GetProject", cb);
        }

        /**
         * 获取项目(分页)
         *
         * @param PageNo   页码
         * @param PageSize 记录数
         * @param cb       回调
         */
        public static Callback.Cancelable GetProjectByPage(int PageNo, int PageSize, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("PageNo", PageNo);
            paramMap.put("PageSize", PageSize);
            return Const.get("TalkerAPI/GetProjectByPage", paramMap, cb);
        }

        /**
         * 添加项目
         *
         * @param up 项目信息的JSON对象UserProject
         * @param cb 回调
         */
        public static Callback.Cancelable DoProject(ApiEntity.UserProject up, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("up", up);
            return Const.post("TalkerAPI/DoProject", paramMap, cb);
        }

        /**
         * 根据id删除项目
         *
         * @param id 项目id
         * @param cb 回调
         */
        public static Callback.Cancelable DelProjectById(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("TalkerAPI/DelProjectById", paramMap, cb);
        }

        /**
         * 增加/删除项目用户
         *
         * @param up 项目对象
         * @param cb 回调
         */
        public static Callback.Cancelable DoProjectUser(ApiEntity.UserProject up, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("up", up);
            return Const.post("TalkerAPI/DoProjectUser", paramMap, cb);
        }

        /**
         * 删除冲刺
         *
         * @param sid 冲刺记录id
         * @param cb  回调
         */
        public static Callback.Cancelable DelSprint(int sid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("sid", sid);
            return Const.post("TalkerAPI/DelSprint", paramMap, cb);
        }

        /**
         * 添加冲刺
         *
         * @param us 冲刺信息的JSON对象UserSprint
         * @param cb 回调
         */
        public static Callback.Cancelable AddSprint(ApiEntity.UserSprint us, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("us", us);
            return Const.post("TalkerAPI/AddSprint", paramMap, cb);
        }

        /**
         * 添加任务到冲刺
         *
         * @param SprintID
         * @param taskid   任务id
         * @param cb       回调
         */
        public static Callback.Cancelable AddSprintTask(int SprintID, int taskid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("SprintID", SprintID);
            paramMap.put("taskid", taskid);
            return Const.post("TalkerAPI/AddSprintTask", paramMap, cb);
        }

        /**
         * 更新冲刺中的任务[删除冲刺中的任务，再添加到选择的冲刺中]
         *
         * @param usList
         * @param us
         * @param taskid
         * @param cb     回调
         */
        public static Callback.Cancelable DoSprintTask(List<ApiEntity.UserSprint> usList, ApiEntity.UserSprint us, int taskid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("usList", usList);
            paramMap.put("us", us);
            paramMap.put("taskid", taskid);
            return Const.post("TalkerAPI/DoSprintTask", paramMap, cb);
        }

        /**
         * 根据id修改冲刺记录（type:0【content】更新任务id串，1更新冲刺状态）
         *
         * @param content 任务id串 / 冲刺状态（1未冲刺，2冲刺中，3已完成）
         * @param id      记录id
         * @param type    type:0【content】更新任务id串，1更新冲刺状态
         * @param cb      回调
         */
        public static Callback.Cancelable UpdateSprintById(String content, int id, int type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("content", content);
            paramMap.put("id", id);
            paramMap.put("type", type);
            return Const.post("TalkerAPI/UpdateSprintById", paramMap, cb);
        }

        /**
         * 获取冲刺信息
         *
         * @param statu 冲刺状态
         * @param cb    回调
         */
        public static Callback.Cancelable GetSprint(int statu, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("statu", statu);
            return Const.get("TalkerAPI/GetSprint", paramMap, cb);
        }

        /**
         * 添加/修改日程
         *
         * @param us 日程信息的JSON对象UserSchedule
         * @param cb 回调
         */
        public static Callback.Cancelable AddCalendar(ApiEntity.UserSchedule us, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("us", us);
            return Const.post("TalkerAPI/AddCalendar", paramMap, cb);
        }

        /**
         * @param us
         * @param cb 回调
         */
        public static Callback.Cancelable NewCreateWaitHandle(ApiEntity.UserSchedule us, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(us);
            return Const.get("TalkerAPI/NewCreateWaitHandle", paramList, cb);
        }

        /**
         * 删除日程
         *
         * @param id 日程id
         * @param cb 回调
         */
        public static Callback.Cancelable DelCalenderById(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("TalkerAPI/DelCalenderById", paramMap, cb);
        }

        /**
         * 完成日程
         *
         * @param cid 日程id
         * @param cb  回调
         */
        public static Callback.Cancelable FinishCalendar(int cid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("cid", cid);
            return Const.post("TalkerAPI/FinishCalendar", paramMap, cb);
        }

        /**
         * 根据状态获取全部日程[分页]
         *
         * @param state
         * @param s
         * @param size
         * @param cb    回调
         */
        public static Callback.Cancelable GetCalenderByPage(int state, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("state", state);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetCalenderByPage", paramMap, cb);
        }

        /**
         * 根据状态获取全部日程
         *
         * @param state
         * @param cb    回调
         */
        public static Callback.Cancelable GetCalenderByState(int state, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("state", state);
            return Const.post("TalkerAPI/GetCalenderByState", paramMap, cb);
        }

        /**
         * 根据记录id获取日程
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetCalenderById(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("TalkerAPI/GetCalenderById", paramMap, cb);
        }

        /**
         * 根据用户列表获取全部日程
         *
         * @param userids
         * @param cb      回调
         */
        public static Callback.Cancelable GetAllCalenderListByUsers(String userids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userids", userids);
            return Const.get("TalkerAPI/GetAllCalenderListByUsers", paramMap, cb);
        }

        /**
         * 根据用户列表获取全部日程
         *
         * @param userids
         * @param isAll
         * @param cb      回调
         */
        public static Callback.Cancelable GetAllNewCalenderListByUsers(String userids, boolean isAll, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userids", userids);
            paramMap.put("isAll", isAll);
            return Const.get("TalkerAPI/GetAllNewCalenderListByUsers", paramMap, cb);
        }

        /**
         * 根据用户id获取全部日程
         *
         * @param userid
         * @param cb     回调
         */
        public static Callback.Cancelable GetAllCalenderListByUserId(int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            return Const.get("TalkerAPI/GetAllCalenderListByUserId", paramMap, cb);
        }

        /**
         * 根据用户id获取全部日程
         *
         * @param userid
         * @param s
         * @param size
         * @param cb     回调
         */
        public static Callback.Cancelable GetAllCalenderListOnPageByUserId(int userid, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetAllCalenderListOnPageByUserId", paramMap, cb);
        }

        /**
         * @param userid
         * @param isAll
         * @param cb     回调
         */
        public static Callback.Cancelable GetAllNewCalenderListByUserId(int userid, boolean isAll, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(userid);
            paramList.add(isAll);
            return Const.get("TalkerAPI/GetAllNewCalenderListByUserId", paramList, cb);
        }

        /**
         * 根据用户id获取全部日程
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetAllCalenderList(RequestCallback cb) {
            return Const.post("TalkerAPI/GetAllCalenderList", cb);
        }

        /**
         * 根据时间区间获取日程
         *
         * @param start  开始时间
         * @param end    结束时间
         * @param statu  状态值
         * @param userid 用户id
         * @param cb     回调
         */
        public static Callback.Cancelable GetCalenderListByTimeSpan(String start, String end, int statu, int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("start", start);
            paramMap.put("end", end);
            paramMap.put("statu", statu);
            paramMap.put("userid", userid);
            return Const.get("TalkerAPI/GetCalenderListByTimeSpan", paramMap, cb);
        }

        /**
         * 保存日程数据
         *
         * @param PostStr List=UserSchedule
         * @param cb      回调
         */
        public static Callback.Cancelable SaveCalenderData(List<ApiEntity.UserSchedule> PostStr, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("PostStr", PostStr);
            return Const.post("TalkerAPI/SaveCalenderData", paramMap, cb);
        }

        /**
         * 获取代办事项详细内容
         *
         * @param sid
         * @param cb  回调
         */
        public static Callback.Cancelable GetUserScheduleById(int sid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("sid", sid);
            return Const.post("TalkerAPI/GetUserScheduleById", paramMap, cb);
        }

        /**
         * PC日程视图上操作事项（简单新建和拖拽修改时间）
         *
         * @param CalendarTitle
         * @param CalendarStartTime
         * @param CalendarEndTime
         * @param IsAllDayEvent
         * @param calendarId
         * @param cb                回调
         */
        public static Callback.Cancelable AddCalendarPC(String CalendarTitle, String CalendarStartTime, String CalendarEndTime, int IsAllDayEvent, int calendarId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("CalendarTitle", CalendarTitle);
            paramMap.put("CalendarStartTime", CalendarStartTime);
            paramMap.put("CalendarEndTime", CalendarEndTime);
            paramMap.put("IsAllDayEvent", IsAllDayEvent);
            paramMap.put("calendarId", calendarId);
            return Const.post("TalkerAPI/AddCalendarPC", paramMap, cb);
        }

        /**
         * 根据时间区间获取数据(PC端)
         *
         * @param showdate
         * @param viewtype
         * @param color
         * @param projectid
         * @param otheruserid
         * @param statu
         * @param cb          回调
         */
        public static Callback.Cancelable GetCalenderListByTimeSpanPC(String showdate, String viewtype, String color, String projectid, int otheruserid, int statu, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("showdate", showdate);
            paramMap.put("viewtype", viewtype);
            paramMap.put("color", color);
            paramMap.put("projectid", projectid);
            paramMap.put("otheruserid", otheruserid);
            paramMap.put("statu", statu);
            return Const.get("TalkerAPI/GetCalenderListByTimeSpanPC", paramMap, cb);
        }

        /**
         * 删除日程事项(pc)
         *
         * @param calendarId
         * @param cb         回调
         */
        public static Callback.Cancelable DelCalenderByIdPC(int calendarId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("calendarId", calendarId);
            return Const.post("TalkerAPI/DelCalenderByIdPC", paramMap, cb);
        }

        /**
         * 发布工作计划
         *
         * @param upList   List=UserPlan(工作计划对象)
         * @param usernote List=UserNote(Talker对象)
         * @param cb       回调
         */
        public static Callback.Cancelable AddTalkerPlan(List<ApiEntity.UserPlan> upList, ApiEntity.UserNote usernote, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("upList", upList);
            paramMap.put("usernote", usernote);
            return Const.post("TalkerAPI/AddTalkerPlan", paramMap, cb);
        }

        /**
         * 保存工作计划数据
         *
         * @param up UserPlan(工作计划对象)
         * @param cb 回调
         */
        public static Callback.Cancelable DoTalkerPlan(ApiEntity.UserPlan up, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("up", up);
            return Const.post("TalkerAPI/DoTalkerPlan", paramMap, cb);
        }

        /**
         * 删除工作计划数据
         *
         * @param PlanId 工作计划记录id)
         * @param cb     回调
         */
        public static Callback.Cancelable DelTalkerPlan(int PlanId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("PlanId", PlanId);
            return Const.post("TalkerAPI/DelTalkerPlan", paramMap, cb);
        }

        /**
         * List=UserPlan
         *
         * @param state
         * @param cb    回调
         */
        public static Callback.Cancelable GetPlanByState(int state, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("state", state);
            return Const.get("TalkerAPI/GetPlanByState", paramMap, cb);
        }

        /**
         * List=UserPlan
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetPlanByRId(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("TalkerAPI/GetPlanByRId", paramMap, cb);
        }

        /**
         * List=UserPlan
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetPlanById(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("TalkerAPI/GetPlanById", paramMap, cb);
        }

        /**
         * List=UserPlan
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetPlan(RequestCallback cb) {
            return Const.get("TalkerAPI/GetPlan", cb);
        }

        /**
         * 保存任务评论数据
         *
         * @param tr TaskReply(任务评论对象)
         * @param cb 回调
         */
        public static Callback.Cancelable DoTaskReply(ApiEntity.TaskReply tr, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("tr", tr);
            return Const.post("TalkerAPI/DoTaskReply", paramMap, cb);
        }

        /**
         * 删除评论数据
         *
         * @param rid    评论记录id
         * @param taskid 任务id[非任务评论传0]
         * @param cb     回调
         */
        public static Callback.Cancelable DelTaskReply(int rid, int taskid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("rid", rid);
            paramMap.put("taskid", taskid);
            return Const.post("TalkerAPI/DelTaskReply", paramMap, cb);
        }

        /**
         * 获取任务评论记录[PC端]
         *
         * @param taskid 任务id
         * @param type
         * @param cb     回调
         */
        public static Callback.Cancelable GetTaskReplyByTaskIdPC(int taskid, int type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("taskid", taskid);
            paramMap.put("type", type);
            return Const.get("TalkerAPI/GetTaskReplyByTaskIdPC", paramMap, cb);
        }

        /**
         * 获取任务评论记录[PC端]
         *
         * @param taskid 任务id
         * @param type
         * @param cb     回调
         */
        public static Callback.Cancelable GetTaskReplyByTaskId(int taskid, int type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("taskid", taskid);
            paramMap.put("type", type);
            return Const.get("TalkerAPI/GetTaskReplyByTaskId", paramMap, cb);
        }

        /**
         * 获取操作记录
         *
         * @param taskid  任务id
         * @param logtype 操作记录类型
         * @param cb      回调
         */
        public static Callback.Cancelable GetTalkerLogByTaskId(int taskid, int logtype, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("taskid", taskid);
            paramMap.put("logtype", logtype);
            return Const.get("TalkerAPI/GetTalkerLogByTaskId", paramMap, cb);
        }

        /**
         * 获取文件关联项目
         *
         * @param FileId 文件id
         * @param cb     回调
         */
        public static Callback.Cancelable GetProjectByFileId(int FileId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("FileId", FileId);
            return Const.get("TalkerAPI/GetProjectByFileId", paramMap, cb);
        }

        /**
         * 获取文件关联任务
         *
         * @param FileId 文件id
         * @param cb     回调
         */
        public static Callback.Cancelable GetTaskByFileId(int FileId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("FileId", FileId);
            return Const.get("TalkerAPI/GetTaskByFileId", paramMap, cb);
        }

        /**
         * 获取文件关联日程
         *
         * @param FileId 文件id
         * @param cb     回调
         */
        public static Callback.Cancelable GetScheduleByFileId(int FileId, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("FileId", FileId);
            return Const.get("TalkerAPI/GetScheduleByFileId", paramMap, cb);
        }

        /**
         * 根据群组id获取项目列表
         *
         * @param groupid
         * @param cb      回调
         */
        public static Callback.Cancelable GetProjectByGroupId(int groupid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupid", groupid);
            return Const.get("TalkerAPI/GetProjectByGroupId", paramMap, cb);
        }

        /**
         * 根据群组id获取任务列表
         *
         * @param groupid
         * @param cb      回调
         */
        public static Callback.Cancelable GetTaskListByGroupId(int groupid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupid", groupid);
            return Const.get("TalkerAPI/GetTaskListByGroupId", paramMap, cb);
        }

        /**
         * 根据群组id获取日程列表
         *
         * @param groupid
         * @param cb      回调
         */
        public static Callback.Cancelable GetScheduleByGroupId(int groupid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupid", groupid);
            return Const.get("TalkerAPI/GetScheduleByGroupId", paramMap, cb);
        }

        /**
         * 根据群组id获取文件列表
         *
         * @param groupid
         * @param cb      回调
         */
        public static Callback.Cancelable GetFileListByGroupId(int groupid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupid", groupid);
            return Const.get("TalkerAPI/GetFileListByGroupId", paramMap, cb);
        }

        /**
         * 根据用户id获取工作项目/相关任务/活动日程/知识库各自的数量接口
         *
         * @param userid
         * @param cb     回调
         */
        public static Callback.Cancelable GetTalkCountByUserId(int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            return Const.get("TalkerAPI/GetTalkCountByUserId", paramMap, cb);
        }

        /**
         * 获取我发布的，我收藏的，相关到我的数量接口
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetMyTalkCount(RequestCallback cb) {
            return Const.get("TalkerAPI/GetMyTalkCount", cb);
        }

        /**
         * 根据文件id获取项目、任务、日程的数量
         *
         * @param fileid
         * @param cb     回调
         */
        public static Callback.Cancelable GetMyTalkerCountByFileId(int fileid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fileid", fileid);
            return Const.get("TalkerAPI/GetMyTalkerCountByFileId", paramMap, cb);
        }

        /**
         * 根据群组id获取项目、任务、日程、知识库的数量
         *
         * @param groupid
         * @param cb      回调
         */
        public static Callback.Cancelable GetMyTalkerCountByGroupId(int groupid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("groupid", groupid);
            return Const.get("TalkerAPI/GetMyTalkerCountByGroupId", paramMap, cb);
        }

        /**
         * 保存项目组数据
         *
         * @param ut   TaskReply(任务评论对象)
         * @param type type(1增加，2修改，3删除)
         * @param cb   回调
         */
        public static Callback.Cancelable DoUserTeam(ApiEntity.UserTeam ut, int type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ut", ut);
            paramMap.put("type", type);
            return Const.post("TalkerAPI/DoUserTeam", paramMap, cb);
        }

        /**
         * 获取项目组记录
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserTeam(RequestCallback cb) {
            return Const.get("TalkerAPI/GetUserTeam", cb);
        }

        /**
         * 根据项目组id获取项目组记录
         *
         * @param id 项目组id
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserTeamById(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("TalkerAPI/GetUserTeamById", paramMap, cb);
        }

        /**
         * 根据项目组id获取项目组文件列表
         *
         * @param id 项目组id
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserTeamFileById(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("TalkerAPI/GetUserTeamFileById", paramMap, cb);
        }

        /**
         * 保存项目组登入信息数据
         *
         * @param tc   TeamCheckIn(项目组登入信息对象)
         * @param type type(1增加，2修改，3删除)
         * @param cb   回调
         */
        public static Callback.Cancelable DoUserTeamCheckIn(ApiEntity.TeamCheckIn tc, int type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("tc", tc);
            paramMap.put("type", type);
            return Const.post("TalkerAPI/DoUserTeamCheckIn", paramMap, cb);
        }

        /**
         * 获取项目组登入信息记录
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserTeamCheckIn(RequestCallback cb) {
            return Const.get("TalkerAPI/GetUserTeamCheckIn", cb);
        }

        /**
         * @param pid
         * @param cb  回调
         */
        public static Callback.Cancelable GetProjectDetail(int pid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pid);
            return Const.get("TalkerAPI/GetProjectDetail", paramList, cb);
        }

        /**
         * @param search
         * @param pid
         * @param fid
         * @param groupid
         * @param s
         * @param size
         * @param cb      回调
         */
        public static Callback.Cancelable GetTrackFileByProjectId(String search, int pid, int fid, String groupid, int s, int size, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(search);
            paramList.add(pid);
            paramList.add(fid);
            paramList.add(groupid);
            paramList.add(s);
            paramList.add(size);
            return Const.get("TalkerAPI/GetTrackFileByProjectId", paramList, cb);
        }

        /**
         * @param search
         * @param pid
         * @param groupid
         * @param s
         * @param size
         * @param cb      回调
         */
        public static Callback.Cancelable GetAllTrackFile(String search, int pid, String groupid, int s, int size, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(search);
            paramList.add(pid);
            paramList.add(groupid);
            paramList.add(s);
            paramList.add(size);
            return Const.get("TalkerAPI/GetAllTrackFile", paramList, cb);
        }

        /**
         * 根据项目组id获取详情（1动态，2消息）
         *
         * @param teamid 项目组id
         * @param s
         * @param size
         * @param cb     回调
         */
        public static Callback.Cancelable GetMessageByTeamId(int teamid, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("teamid", teamid);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/GetMessageByTeamId", paramMap, cb);
        }

        /**
         * 获取项目组动态
         *
         * @param teamID 项目组id
         * @param s      页码
         * @param size   一页的记录数
         * @param cb     回调
         */
        public static Callback.Cancelable LoadTeamTalker(int teamID, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("teamID", teamID);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("TalkerAPI/LoadTeamTalker", paramMap, cb);
        }
    }

    public static class DocumentAPI {
        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable ReadExcel(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("editor", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable ReadHTML(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("DocumentAPI/ReadHTML", paramList, cb);
        }

        /**
         * 查看word视图，word生成image
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable SeeView(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("DocumentAPI/SeeView", paramMap, cb);
        }

        /**
         * @param id
         * @param html
         * @param paperSize
         * @param cb        回调
         */
        public static Callback.Cancelable SaveDoc(int id, String html, String paperSize, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            paramMap.put("html", html);
            paramMap.put("paperSize", paperSize);
            return Const.post("DocumentAPI/SaveDoc", paramMap, cb);
        }

        /**
         * 创建物理表、字段
         *
         * @param parentid
         * @param filename
         * @param groupID
         * @param cb       回调
         */
        public static Callback.Cancelable Create(int parentid, String filename, int groupID, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("parentid", parentid);
            paramMap.put("filename", filename);
            paramMap.put("groupID", groupID);
            return Const.post("DocumentAPI/Create", paramMap, cb);
        }

        /**
         * 保存Excel
         *
         * @param id             文件ID
         * @param objSaveDataStr
         * @param cb             回调
         */
        public static Callback.Cancelable SaveExcelData(String id, String objSaveDataStr, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            paramMap.put("objSaveDataStr", objSaveDataStr);
            return Const.post("DocumentAPI/SaveExcelData", paramMap, cb);
        }

        /**
         * 读取文件是否有人正在编辑 若有，则返回正在编辑的人，并记录浏览人；若无，则记录正在编辑人
         *
         * @param id 文件ID
         * @param cb 回调
         */
        public static Callback.Cancelable ReadPower(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("DocumentAPI/ReadPower", paramList, cb);
        }

        /**
         * 请求编辑 给正在编辑的人发送编辑请求，让对方做出处理
         *
         * @param id 文件ID
         * @param cb 回调
         */
        public static Callback.Cancelable RequestEdit(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("DocumentAPI/RequestEdit", paramList, cb);
        }

        /**
         * 回应请求编辑 若同意，则更改正在编辑人
         *
         * @param id       文件ID
         * @param touserid 请求人ID
         * @param isok     是否同意请求编辑
         * @param cb       回调
         */
        public static Callback.Cancelable ResponseEdit(int id, int touserid, boolean isok, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            paramList.add(touserid);
            paramList.add(isok);
            return Const.get("DocumentAPI/ResponseEdit", paramList, cb);
        }

        /**
         * 同步更新
         *
         * @param id        文件ID
         * @param sheetName Sheet名
         * @param val       值
         * @param cb        回调
         */
        public static Callback.Cancelable SynchronousUpdate(int id, String sheetName, String val, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            paramMap.put("sheetName", sheetName);
            paramMap.put("val", val);
            return Const.post("DocumentAPI/SynchronousUpdate", paramMap, cb);
        }

        /**
         * @param id
         * @param val
         * @param cb  回调
         */
        public static Callback.Cancelable SynchronousUpdateWord1(int id, String val, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            paramMap.put("val", val);
            return Const.post("DocumentAPI/SynchronousUpdateWord1", paramMap, cb);
        }
        /**
         * word同步更新
         * @param id 文件ID
         * @param action 动作（增删改）
         * @param val 内容
         * @param arr 节点深度
         * @param offset 偏移量
         * @param cb 回调
         */
        //        public static Callback.Cancelable SynchronousUpdateWord(int id,String action,String val,ApiEntity.ArrayList arr,int offset,RequestCallback cb) {
        //            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        //            paramMap.put("id",id);
        //            paramMap.put("action",action);
        //            paramMap.put("val",val);
        //            paramMap.put("arr",arr);
        //            paramMap.put("offset",offset);
        //            return Const.post("DocumentAPI/SynchronousUpdateWord",paramMap,cb);
        //        }

        /**
         * 停止编辑 即关闭编辑窗口，移除浏览人或者正在编辑人
         *
         * @param id 文件ID
         * @param cb 回调
         */
        public static Callback.Cancelable StopEdit(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("DocumentAPI/StopEdit", paramList, cb);
        }

        /**
         * @param id
         * @param userID
         * @param com
         * @param cb     回调
         */
        public static Callback.Cancelable StopEditInternal(int id, int userID, String com, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            paramList.add(userID);
            paramList.add(com);
            return Const.get("DocumentAPI/StopEditInternal", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable ExportWord(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.get("DocumentAPI/ExportWord", paramMap, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable ExportExcel(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("DocumentAPI/ExportExcel", paramList, cb);
        }

        /**
         * 创建圈子
         *
         * @param id   文件ID
         * @param name 文件名称
         * @param cb   回调
         */
        public static Callback.Cancelable CreateGroupChart(int id, String name, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            paramMap.put("name", name);
            return Const.post("DocumentAPI/CreateGroupChart", paramMap, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable RestoreHistory(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("DocumentAPI/RestoreHistory", paramMap, cb);
        }
    }

    public static class EventAPI {
        /**
         * @param cb 回调
         */
        public static Callback.Cancelable get_Access(RequestCallback cb) {
            return Const.get("EventAPI/get_Access", cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable get_ScheduleAccess(RequestCallback cb) {
            return Const.get("EventAPI/get_ScheduleAccess", cb);
        }
        /**
         * @param setting
         * @param cb 回调
         */
        //        public static Callback.Cancelable SaveEvent(ApiEntity.EventSetting setting,RequestCallback cb) {
        //            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        //            paramMap.put("setting",setting);
        //            return Const.post("EventAPI/SaveEvent",paramMap,cb);
        //        }
        /**
         * @param setting
         * @param tid
         * @param rid
         * @param cb 回调
         */
        //        public static Callback.Cancelable SaveAndStartEvent(ApiEntity.EventSetting setting,int tid,int rid,RequestCallback cb) {
        //            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        //            paramMap.put("setting",setting);
        //            paramMap.put("tid",tid);
        //            paramMap.put("rid",rid);
        //            return Const.post("EventAPI/SaveAndStartEvent",paramMap,cb);
        //        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DeleteEventSetting(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("EventAPI/DeleteEventSetting", paramMap, cb);
        }

        /**
         * @param tid
         * @param rid
         * @param cb  回调
         */
        public static Callback.Cancelable LoadRecordEvents(int tid, int rid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            paramList.add(rid);
            return Const.get("EventAPI/LoadRecordEvents", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable LoadEventSetting(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("EventAPI/LoadEventSetting", paramList, cb);
        }

        /**
         * @param tid
         * @param eventid
         * @param rid
         * @param cb      回调
         */
        public static Callback.Cancelable GetEventObject(int tid, int eventid, int rid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            paramList.add(eventid);
            paramList.add(rid);
            return Const.get("EventAPI/GetEventObject", paramList, cb);
        }
        /**
         * @param setting
         * @param tid
         * @param rid
         * @param cb 回调
         */
        //        public static Callback.Cancelable StartUpEvent(ApiEntity.EventSetting setting,int tid,int rid,RequestCallback cb) {
        //            List<Object> paramList = new ArrayList<Object>();
        //            paramList.add(setting);
        //            paramList.add(tid);
        //            paramList.add(rid);
        //            return Const.get("EventAPI/StartUpEvent",paramList,cb);
        //        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DelEvent(int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("id", id);
            return Const.post("EventAPI/DelEvent", paramMap, cb);
        }

        /**
         * @param evtid
         * @param cb    回调
         */
        public static Callback.Cancelable StopEvent(int evtid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(evtid);
            return Const.get("EventAPI/StopEvent", paramList, cb);
        }
        /**
         * @param obj
         * @param cb 回调
         */
        //        public static Callback.Cancelable SaveEventObject(ApiEntity.EventObject obj,RequestCallback cb) {
        //            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        //            paramMap.put("obj",obj);
        //            return Const.post("EventAPI/SaveEventObject",paramMap,cb);
        //        }

        /**
         * @param rid
         * @param cb  回调
         */
        public static Callback.Cancelable GetRecordEvents(int rid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(rid);
            return Const.get("EventAPI/GetRecordEvents", paramList, cb);
        }
    }

    public static class ExpressAPI {
        /**
         * @param cb 回调
         */
        public static Callback.Cancelable get_Express(RequestCallback cb) {
            return Const.get("ExpressAPI/get_Express", cb);
        }

        /**
         * 获取物流公司列表
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetExpressList(RequestCallback cb) {
            return Const.get("ExpressAPI/GetExpressList", cb);
        }

        /**
         * 搜索数据表
         *
         * @param companycode 物流公司名称
         * @param num         快递单号
         * @param checkcode   验证码
         * @param cb          回调
         */
        public static Callback.Cancelable Search(String companycode, String num, String checkcode, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("companycode", companycode);
            paramMap.put("num", num);
            paramMap.put("checkcode", checkcode);
            return Const.get("ExpressAPI/Search", paramMap, cb);
        }

        /**
         * 根据公司代码获取物流公司信息
         *
         * @param code
         * @param cb   回调
         */
        public static Callback.Cancelable GetExpressByCode(String code, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("code", code);
            return Const.get("ExpressAPI/GetExpressByCode", paramMap, cb);
        }
        /**
         * @param url
         * @param Referer
         * @param data
         * @param encode
         * @param SaveCookie
         * @param cb 回调
         */
        //        public static Callback.Cancelable PostHtml(String url,String Referer,String data,ApiEntity.Encoding encode,boolean SaveCookie,RequestCallback cb) {
        //            List<Object> paramList = new ArrayList<Object>();
        //            paramList.add(url);
        //            paramList.add(Referer);
        //            paramList.add(data);
        //            paramList.add(encode);
        //            paramList.add(SaveCookie);
        //            return Const.get("ExpressAPI/PostHtml",paramList,cb);
        //        }

        /**
         * 获取验证码图片
         *
         * @param checkcodeurl
         * @param cb           回调
         */
        public static Callback.Cancelable ReadImage(String checkcodeurl, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("checkcodeurl", checkcodeurl);
            return Const.get("VerImage", paramMap, cb);
        }
    }

    public static class MonitorService {
        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetAppStatus(RequestCallback cb) {
            return Const.get("MonitorService/GetAppStatus", cb);
        }

        /**
         * @param name
         * @param cb   回调
         */
        public static Callback.Cancelable CloseApp(String name, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(name);
            return Const.get("MonitorService/CloseApp", paramList, cb);
        }

        /**
         * @param text
         * @param cb   回调
         */
        public static Callback.Cancelable Execute(String text, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(text);
            return Const.get("MonitorService/Execute", paramList, cb);
        }
    }

    public static class QRCodeService {
        /**
         * @param data
         * @param path
         * @param fileName
         * @param size
         * @param cb 回调
         */
        //        public static Callback.Cancelable Encode(String data,String path,String fileName,ApiEntity.Nullable`1,Int32 size,RequestCallback cb) {
        //            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        //            paramMap.put("data",data);
        //            paramMap.put("path",path);
        //            paramMap.put("fileName",fileName);
        //            paramMap.put("size",size);
        //            return Const.post("QRCodeService/Encode",paramMap,cb);
        //        }

        /**
         * @param fileName
         * @param cb       回调
         */
        public static Callback.Cancelable Decode(String fileName, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(fileName);
            return Const.get("QRCodeService/Decode", paramList, cb);
        }
    }

    public static class WordContainer {
        /**
         * 文件上传
         *
         * @param cb 回调
         */
        public static Callback.Cancelable Upload(RequestCallback cb) {
            return Const.post("WordContainer/Upload", cb);
        }

        /**
         * @param path
         * @param id
         * @param cb   回调
         */
        public static Callback.Cancelable RemoveComment(String path, int id, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("path", path);
            paramMap.put("id", id);
            return Const.get("RemoveComment", paramMap, cb);
        }
        /**
         * @param path
         * @param entity
         * @param cb 回调
         */
        //        public static Callback.Cancelable EditComment(String path,ApiEntity.CommentEntity entity,RequestCallback cb) {
        //            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        //            paramMap.put("path",path);
        //            paramMap.put("entity",entity);
        //            return Const.post("EditComment",paramMap,cb);
        //        }
        /**
         * @param path
         * @param entity
         * @param cb 回调
         */
        //        public static Callback.Cancelable AddComment(String path,ApiEntity.CommentEntity entity,RequestCallback cb) {
        //            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        //            paramMap.put("path",path);
        //            paramMap.put("entity",entity);
        //            return Const.get("AddComment",paramMap,cb);
        //        }

        /**
         * @param path
         * @param type
         * @param cb   回调
         */
        public static Callback.Cancelable ExtractComments(String path, String type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("path", path);
            paramMap.put("type", type);
            return Const.get("ExtractComments", paramMap, cb);
        }
    }

    public static class DataTest {
        /**
         * test
         *
         * @param a
         * @param b
         * @param cb 回调
         */
        public static Callback.Cancelable Test(int a, int b, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(a);
            paramList.add(b);
            return Const.get("DataTest/Test", paramList, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetData(RequestCallback cb) {
            return Const.get("DataTest/GetData", cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetException(RequestCallback cb) {
            return Const.get("DataTest/GetException", cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetSumValue(RequestCallback cb) {
            return Const.get("DataTest/GetSumValue", cb);
        }
        /**
         *
         * @param arr
         * @param file
         * @param cb 回调
         */
        //        public static Callback.Cancelable MyFilter(int[] arr,ApiEntity.FilterDelegate file,RequestCallback cb) {
        //            List<Object> paramList = new ArrayList<Object>();
        //            paramList.add(arr);
        //            paramList.add(file);
        //            return Const.get("DataTest/MyFilter",paramList,cb);
        //        }
    }

    public static class PageBuilder {
        /**
         * 获取某个页面的权限
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable Load(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("PageBuilder/Load", paramList, cb);
        }
    }

    public static class PriceModuleService {
        /**
         * @param price
         * @param cb 回调
         */
        //        public static Callback.Cancelable SavePrice(ApiEntity.CustomerPrice price,RequestCallback cb) {
        //            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        //            paramMap.put("price",price);
        //            return Const.post("PriceModuleService/SavePrice",paramMap,cb);
        //        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetPrice(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("PriceModuleService/GetPrice", paramList, cb);
        }
    }

    public static class GroupAPI {
        /**
         * 保存角色组
         *
         * @param group
         * @param cb    回调
         */
        public static Callback.Cancelable SaveGroup(ApiEntity.GroupEntity group, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(group);
            return Const.post("GroupAPI/SaveGroup", paramList, cb);
        }

        /**
         * 获取页面或数据表的详细权限设置
         *
         * @param groupId
         * @param pageId
         * @param cb      回调
         */
        public static Callback.Cancelable GetPowerSettingById(int groupId, int pageId, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(groupId);
            paramList.add(pageId);
            return Const.get("GroupAPI/GetPowerSettingById", paramList, cb);
        }

        /**
         * @param groupPower
         * @param cb         回调
         */
        public static Callback.Cancelable SaveGroupPower(ApiEntity.GroupPower groupPower, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(groupPower);
            return Const.post("GroupAPI/SaveGroupPower", paramList, cb);
        }

        /**
         * 获取某个页面的权限
         *
         * @param tplid
         * @param cb    回调
         */
        public static Callback.Cancelable LoadPagePowerSetting(int tplid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tplid);
            return Const.get("GroupAPI/LoadPagePowerSetting", paramList, cb);
        }

        /**
         * 获取某个页面的权限
         *
         * @param groupid
         * @param cb      回调
         */
        public static Callback.Cancelable LoadGroupPower(int groupid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(groupid);
            return Const.get("GroupAPI/LoadGroupPower", paramList, cb);
        }

        /**
         * 保存某个页面权限
         *
         * @param pagePower
         * @param cb        回调
         */
        public static Callback.Cancelable SavePagePowerSetting(ApiEntity.PagePower pagePower, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pagePower);
            return Const.post("GroupAPI/SavePagePowerSetting", paramList, cb);
        }

        /**
         * @param pid
         * @param cb  回调
         */
        public static Callback.Cancelable DeletePowerSetting(int pid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pid);
            return Const.get("GroupAPI/DeletePowerSetting", paramList, cb);
        }

        /**
         * @param ListCopyUser
         * @param ListUser
         * @param cb           回调
         */
        public static Callback.Cancelable CopyUserPower(List<ApiEntity.GroupMember> ListCopyUser, List<ApiEntity.GroupMember> ListUser, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ListCopyUser", ListCopyUser);
            paramMap.put("ListUser", ListUser);
            return Const.post("GroupAPI/CopyUserPower", paramMap, cb);
        }

        /**
         * 获取所有角色
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetGroups(RequestCallback cb) {
            return Const.get("GroupAPI/GetGroups", cb);
        }

        /**
         * 根据用户ID获取用户所有的角色
         *
         * @param userid
         * @param cb     回调
         */
        public static Callback.Cancelable GetGroupsByUserId(int userid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(userid);
            return Const.get("GroupAPI/GetGroupsByUserId", paramList, cb);
        }

        /**
         * 根据角色id获取信息
         *
         * @param id 角色id
         * @param cb 回调
         */
        public static Callback.Cancelable GetGroupByGroupId(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("GroupAPI/GetGroupByGroupId", paramList, cb);
        }

        /**
         * 添加用户到某角色
         *
         * @param gmList
         * @param cb     回调
         */
        public static Callback.Cancelable AddToGroup(List<ApiEntity.GroupMember> gmList, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(gmList);
            return Const.post("GroupAPI/AddToGroup", paramList, cb);
        }

        /**
         * 从某角色删除用户
         *
         * @param gmList
         * @param cb     回调
         */
        public static Callback.Cancelable RemoveFromGroup(List<ApiEntity.GroupMember> gmList, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(gmList);
            return Const.post("GroupAPI/RemoveFromGroup", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetMembersByGroupID(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("GroupAPI/GetMembersByGroupID", paramList, cb);
        }

        /**
         * @param keyword
         * @param cb      回调
         */
        public static Callback.Cancelable SearchGroup(String keyword, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(keyword);
            return Const.get("GroupAPI/SearchGroup", paramList, cb);
        }
    }

    public static class AreaService {
        /**
         * 获取国家列表
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetCountry(RequestCallback cb) {
            return Const.get("AreaService/GetCountry", cb);
        }

        /**
         * @param pid
         * @param cb  回调
         */
        public static Callback.Cancelable GetChildren(int pid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pid);
            return Const.get("AreaService/GetChildren", paramList, cb);
        }

        /**
         * @param names
         * @param cb    回调
         */
        public static Callback.Cancelable SearchByNames(String names, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("names", names);
            return Const.post("AreaService/SearchByNames", paramMap, cb);
        }
    }

    public static class ExportFile {
        /**
         * 下载文件
         *
         * @param fileName
         * @param cb       回调
         */
        public static Callback.Cancelable DownloadFile(String fileName, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fileName", fileName);
            return Const.get("ExportFile/DownloadFile", paramMap, cb);
        }
    }

    public static class ReportService {
        /**
         * 获取报表数据
         *
         * @param tplid 模版id
         * @param rptid 报表id
         * @param cb    回调
         */
        public static Callback.Cancelable GetReportData(int tplid, int rptid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tplid);
            paramList.add(rptid);
            return Const.get("ReportService/GetReportData", paramList, cb);
        }

        /**
         * 展开报表数据源
         *
         * @param tplid 模版id
         * @param rptid 报表id
         * @param cb    回调
         */
        public static Callback.Cancelable ExplainDataSource(int tplid, int rptid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tplid);
            paramList.add(rptid);
            return Const.get("ReportService/ExplainDataSource", paramList, cb);
        }
    }

    public static class UserData {
        /**
         * 获取某个页面的权限
         *
         * @param key
         * @param start
         * @param end
         * @param args
         * @param cb    回调
         */
        public static Callback.Cancelable Load(String key, int start, int end, String[] args, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            paramMap.put("start", start);
            paramMap.put("end", end);
            paramMap.put("args", args);
            return Const.post("LoadData", paramMap, cb);
        }

        /**
         * @param key
         * @param start
         * @param end
         * @param filterFields
         * @param keyword
         * @param args
         * @param cb           回调
         */
        public static Callback.Cancelable SearchSource(String key, int start, int end, String[] filterFields, String keyword, String[] args, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            paramMap.put("start", start);
            paramMap.put("end", end);
            paramMap.put("filterFields", filterFields);
            paramMap.put("keyword", keyword);
            paramMap.put("args", args);
            return Const.post("UserData/SearchSource", paramMap, cb);
        }

        /**
         * @param key
         * @param valField
         * @param val
         * @param args
         * @param cb       回调
         */
        public static Callback.Cancelable SearchItem(String key, String valField, String val, String[] args, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("key", key);
            paramMap.put("valField", valField);
            paramMap.put("val", val);
            paramMap.put("args", args);
            return Const.post("UserData/SearchItem", paramMap, cb);
        }

        /**
         * 获取某个页面的权限
         *
         * @param pid
         * @param sid
         * @param cb  回调
         */
        public static Callback.Cancelable Page(int pid, String sid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pid);
            paramList.add(sid);
            return Const.get("Page", paramList, cb);
        }

        /**
         * @param sid
         * @param cb  回调
         */
        public static Callback.Cancelable DataRequest(String sid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(sid);
            return Const.get("Data", paramList, cb);
        }

        /**
         * 输出页面所有元素
         *
         * @param pid
         * @param cb  回调
         */
        public static Callback.Cancelable PageElemens(int pid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pid);
            return Const.get("mpage", paramList, cb);
        }

        /**
         * 文件上传
         *
         * @param UploadData
         * @param cb         回调
         */
        public static Callback.Cancelable PCUpload(List<ApiEntity.Files> UploadData, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("UploadData", UploadData);
            return Const.post("pcupload", paramMap, cb);
        }

        /**
         * 文件上传
         *
         * @param UploadData
         * @param cb         回调
         */
        public static Callback.Cancelable Upload(List<ApiEntity.Files> UploadData, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("UploadData", UploadData);
            return Const.post("upload", paramMap, cb);
        }

        /**
         * 图片上传
         *
         * @param cb 回调
         */
        public static Callback.Cancelable ImageUpload(RequestCallback cb) {
            return Const.post("ImageUpload", cb);
        }

        /**
         * 恢复文件
         *
         * @param fileInfo 当前要恢复文件对象
         * @param cb       回调
         */
        public static Callback.Cancelable ReCoverFileByFileId(ApiEntity.Files fileInfo, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fileInfo", fileInfo);
            return Const.post("UserData/ReCoverFileByFileId", paramMap, cb);
        }

        /**
         * 获取文件夹列表
         *
         * @param Search 关键字
         * @param cb     回调
         */
        public static Callback.Cancelable GetFoldList(String Search, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Search", Search);
            return Const.get("UserData/GetFoldList", paramMap, cb);
        }

        /**
         * 获取全部文件夹列表
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetAllFoldList(RequestCallback cb) {
            return Const.get("UserData/GetAllFoldList", cb);
        }

        /**
         * 根据文件id串获取文件信息列表
         *
         * @param ids
         * @param cb  回调
         */
        public static Callback.Cancelable GetFileListByIds(String ids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ids", ids);
            return Const.get("UserData/GetFileListByIds", paramMap, cb);
        }

        /**
         * 获取文件信息列表[1获取文件，2获取文件夹,3获取文件和文件夹]
         *
         * @param search
         * @param Type
         * @param foldid
         * @param cb     回调
         */
        public static Callback.Cancelable GetFileInfoByFolderId(String search, int Type, int foldid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("search", search);
            paramMap.put("Type", Type);
            paramMap.put("foldid", foldid);
            return Const.get("UserData/GetFileInfoByFolderId", paramMap, cb);
        }

        /**
         * 根据用户id获取文件列表
         *
         * @param Search   关键字
         * @param Type     查询类型（3我的文件，4共享给我的文件，5作废的文件）
         * @param FileType
         * @param cb       回调
         */
        public static Callback.Cancelable GetNewFilesList(String Search, int Type, int FileType, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Search", Search);
            paramMap.put("Type", Type);
            paramMap.put("FileType", FileType);
            return Const.get("UserData/GetNewFilesList", paramMap, cb);
        }

        /**
         * 根据用户id获取文件列表
         *
         * @param userid 用户id
         * @param cb     回调
         */
        public static Callback.Cancelable GetFilesListByUserId(int userid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("userid", userid);
            return Const.get("UserData/GetFilesListByUserId", paramMap, cb);
        }

        /**
         * 获取文件列表[分页]
         *
         * @param Search 关键字
         * @param Type   查询类型（3我的文件，4共享给我的文件，5作废的文件）
         * @param FoldId 文件夹id
         * @param s      页码
         * @param size   页记录数
         * @param cb     回调
         */
        public static Callback.Cancelable GetFilesListByPage(String Search, int Type, int FoldId, int s, int size, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Search", Search);
            paramMap.put("Type", Type);
            paramMap.put("FoldId", FoldId);
            paramMap.put("s", s);
            paramMap.put("size", size);
            return Const.get("UserData/GetFilesListByPage", paramMap, cb);
        }

        /**
         * 根据用户id获取文件列表
         *
         * @param Search   关键字
         * @param Type     查询类型（3我的文件，4共享给我的文件，5作废的文件）
         * @param FileType
         * @param cb       回调
         */
        public static Callback.Cancelable GetFilesList(String Search, int Type, int FileType, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Search", Search);
            paramMap.put("Type", Type);
            paramMap.put("FileType", FileType);
            return Const.get("UserData/GetFilesList", paramMap, cb);
        }

        /**
         * 添加文件夹
         *
         * @param file 文件夹对象
         * @param cb   回调
         */
        public static Callback.Cancelable SaveFoldInfo(ApiEntity.Files file, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("file", file);
            return Const.post("UserData/SaveFoldInfo", paramMap, cb);
        }

        /**
         * 根据文件id获取文件版本信息
         *
         * @param fid 文件id
         * @param cb  回调
         */
        public static Callback.Cancelable GetFilesVersionById(int fid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fid", fid);
            return Const.get("UserData/GetFilesVersionById", paramMap, cb);
        }

        /**
         * 根据类型获取文件数量
         *
         * @param Type 查询类型（3我的文件，4共享给我的文件，5作废的文件）
         * @param cb   回调
         */
        public static Callback.Cancelable GetFilesCountByType(int Type, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("Type", Type);
            return Const.get("UserData/GetFilesCountByType", paramMap, cb);
        }

        /**
         * 恢复文件
         *
         * @param fid 文件id
         * @param cb  回调
         */
        public static Callback.Cancelable renewFile(int fid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fid", fid);
            return Const.post("UserData/renewFile", paramMap, cb);
        }

        /**
         * 恢复多个文件
         *
         * @param fids 文件id
         * @param cb   回调
         */
        public static Callback.Cancelable renewFiles(String fids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fids", fids);
            return Const.post("UserData/renewFiles", paramMap, cb);
        }

        /**
         * 作废文件
         *
         * @param fid 文件id
         * @param cb  回调
         */
        public static Callback.Cancelable cancellFile(int fid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fid", fid);
            return Const.post("UserData/cancellFile", paramMap, cb);
        }

        /**
         * 作废多个文件
         *
         * @param fids 多个文件id
         * @param cb   回调
         */
        public static Callback.Cancelable cancellNewFile(String fids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fids", fids);
            return Const.post("UserData/cancellNewFile", paramMap, cb);
        }

        /**
         * 彻底删除文件
         *
         * @param fid       文件id
         * @param isDelFile true删除文件夹及其内容，false仅删除文件夹
         * @param cb        回调
         */
        public static Callback.Cancelable delFold(int fid, boolean isDelFile, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fid", fid);
            paramMap.put("isDelFile", isDelFile);
            return Const.post("UserData/delFold", paramMap, cb);
        }

        /**
         * 彻底删除文件
         *
         * @param fid 文件id
         * @param cb  回调
         */
        public static Callback.Cancelable delFile(int fid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fid", fid);
            return Const.post("UserData/delFile", paramMap, cb);
        }

        /**
         * 彻底删除服务器文件
         *
         * @param fileSource 服务器文件
         * @param cb         回调
         */
        public static Callback.Cancelable delNewFileSource(String[] fileSource, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fileSource", fileSource);
            return Const.post("UserData/delNewFileSource", paramMap, cb);
        }

        /**
         * 彻底删除服务器文件
         *
         * @param fids
         * @param cb   回调
         */
        public static Callback.Cancelable delNewFile(String fids, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fids", fids);
            return Const.post("UserData/delNewFile", paramMap, cb);
        }

        /**
         * 保存文件下载记录
         *
         * @param fid 文件id
         * @param cb  回调
         */
        public static Callback.Cancelable SaveDownLoadRecord(int fid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fid", fid);
            return Const.post("UserData/SaveDownLoadRecord", paramMap, cb);
        }

        /**
         * 加载文件权限
         *
         * @param fid 文件id
         * @param cb  回调
         */
        public static Callback.Cancelable LoadFilePower(int fid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fid", fid);
            return Const.get("UserData/LoadFilePower", paramMap, cb);
        }

        /**
         * @param fid
         * @param cb  回调
         */
        public static Callback.Cancelable FileDownLog(int fid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fid", fid);
            return Const.get("UserData/FileDownLog", paramMap, cb);
        }

        /**
         * 下载文件
         *
         * @param fileId
         * @param cb     回调
         */
        public static Callback.Cancelable DownloadFile(int fileId, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(fileId);
            return Const.get("DownLoad", paramList, cb);
        }

        /**
         * 操作文件权限
         *
         * @param upList
         * @param fid
         * @param cb     回调
         */
        public static Callback.Cancelable DoFilePower(List<ApiEntity.UserFilePower> upList, int fid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("upList", upList);
            paramMap.put("fid", fid);
            return Const.post("UserData/DoFilePower", paramMap, cb);
        }

        /**
         * @param fileInfo
         * @param cb       回调
         */
        public static Callback.Cancelable ModifyFile(ApiEntity.Files fileInfo, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fileInfo", fileInfo);
            return Const.post("UserData/ModifyFile", paramMap, cb);
        }

        /**
         * @param fileInfo
         * @param cb       回调
         */
        public static Callback.Cancelable SaveFileInfo(ApiEntity.Files fileInfo, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("fileInfo", fileInfo);
            return Const.post("UserData/SaveFileInfo", paramMap, cb);
        }

        /**
         * @param ds
         * @param isDelete
         * @param cb       回调
         */
        public static Callback.Cancelable ImportData(ApiEntity.DataSource ds, boolean isDelete, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ds", ds);
            paramMap.put("isDelete", isDelete);
            return Const.post("UserData/ImportData", paramMap, cb);
        }

        /**
         * 上传文件
         *
         * @param path 目录
         * @param n    创建新文件名(不等于0则创建)
         * @param save 保存文件信息(等于1才保存)
         * @param cb   回调
         */
        public static Callback.Cancelable UploadFile(String path, String n, String save, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("path", path);
            paramMap.put("n", n);
            paramMap.put("save", save);
            return Const.post("UploadFile", paramMap, cb);
        }

        /**
         * 上传文件
         *
         * @param cb 回调
         */
        public static Callback.Cancelable UploadFile(RequestCallback cb) {
            return Const.post("UploadFileByExport", cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable UploadAudio(RequestCallback cb) {
            return Const.post("UploadAudio", cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable UploadVideo(RequestCallback cb) {
            return Const.post("UploadVideo", cb);
        }

        /**
         * @param targetID
         * @param newParentID
         * @param cb          回调
         */
        public static Callback.Cancelable ChangeParent(int targetID, int newParentID, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(targetID);
            paramList.add(newParentID);
            return Const.get("UserData/ChangeParent", paramList, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable get_UserFileBasePath(RequestCallback cb) {
            return Const.get("UserData/get_UserFileBasePath", cb);
        }

        /**
         * @param rid
         * @param cb  回调
         */
        public static Callback.Cancelable GetFlowContents(int rid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(rid);
            return Const.get("UserData/GetFlowContents", paramList, cb);
        }

        /**
         * @param flowid
         * @param flowmoduleid
         * @param cb           回调
         */
        public static Callback.Cancelable GetFlowContent(int flowid, int flowmoduleid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(flowid);
            paramList.add(flowmoduleid);
            return Const.get("UserData/GetFlowContent", paramList, cb);
        }

        /**
         * @param path
         * @param cb   回调
         */
        public static Callback.Cancelable DeleteFile(String path, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("path", path);
            return Const.post("UserData/DeleteFile", paramMap, cb);
        }
    }

    public static class Develop {
        /**
         * 获取所有API方法
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetAllMethod(RequestCallback cb) {
            return Const.get("Develop/GetAllMethod", cb);
        }

        /**
         * 生成api列表（java版）
         *
         * @param cb 回调
         */
        public static Callback.Cancelable BuildAndroidApi(RequestCallback cb) {
            return Const.get("Develop/BuildAndroidApi", cb);
        }

        /**
         * 生成api列表（ios版）
         *
         * @param cb 回调
         */
        public static Callback.Cancelable BuildIosApi(RequestCallback cb) {
            return Const.get("Develop/BuildIosApi", cb);
        }
    }

    public static class HelpAPI {
        /**
         * 保存某个页面权限
         *
         * @param url
         * @param settings
         * @param cb       回调
         */
        public static Callback.Cancelable Save(String url, String settings, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("url", url);
            paramMap.put("settings", settings);
            return Const.post("Help/Save", paramMap, cb);
        }

        /**
         * @param url
         * @param cb  回调
         */
        public static Callback.Cancelable LoadSetting(String url, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("url", url);
            return Const.post("Help/LoadSetting", paramMap, cb);
        }
    }

    public static class PowerAPI {
        /**
         * 获取某个页面的权限
         *
         * @param pageId
         * @param tplid
         * @param groupid
         * @param cb      回调
         */
        public static Callback.Cancelable LoadPagePower(int pageId, int tplid, int groupid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pageId);
            paramList.add(tplid);
            paramList.add(groupid);
            return Const.get("PowerAPI/LoadPagePower", paramList, cb);
        }

        /**
         * 保存某个页面权限
         *
         * @param pagePower
         * @param cb        回调
         */
        public static Callback.Cancelable SavePower(ApiEntity.PagePower pagePower, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pagePower);
            return Const.post("PowerAPI/SavePower", paramList, cb);
        }

        /**
         * @param pid
         * @param cb  回调
         */
        public static Callback.Cancelable DeletePowerSetting(int pid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pid);
            return Const.get("PowerAPI/DeletePowerSetting", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetPagePowerById(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("PowerAPI/GetPagePowerById", paramList, cb);
        }

        /**
         * @param pageId
         * @param cb     回调
         */
        public static Callback.Cancelable LoadTablePower(int pageId, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pageId);
            return Const.get("PowerAPI/LoadTablePower", paramList, cb);
        }

        /**
         * 保存某个数据表权限
         *
         * @param pagePower
         * @param cb        回调
         */
        public static Callback.Cancelable SaveTablePower(ApiEntity.PagePower pagePower, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(pagePower);
            return Const.post("PowerAPI/SaveTablePower", paramList, cb);
        }
    }

    public static class Table {
        /**
         * 获取所有的info
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetAllInfo(RequestCallback cb) {
            return Const.get("Table/GetAllInfo", cb);
        }

        /**
         * 获取某个应用下的数据表
         *
         * @param infoid 应用ID
         * @param cb     回调
         */
        public static Callback.Cancelable GetTableByInfoID(int infoid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(infoid);
            return Const.get("Table/GetTableByInfoID", paramList, cb);
        }

        /**
         * 搜索数据表
         *
         * @param tname 数据表名称
         * @param cb    回调
         */
        public static Callback.Cancelable SearchTable(String tname, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tname);
            return Const.get("Table/SearchTable", paramList, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetEventTables(RequestCallback cb) {
            return Const.get("Table/GetEventTables", cb);
        }

        /**
         * 获取数据表的字段
         *
         * @param tableid 数据表
         * @param cb      回调
         */
        public static Callback.Cancelable GetFieldsByTableID(int tableid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tableid);
            return Const.get("Table/GetFieldsByTableID", paramList, cb);
        }

        /**
         * @param idstr
         * @param cb    回调
         */
        public static Callback.Cancelable GetFieldsByTableIDs(String idstr, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(idstr);
            return Const.get("Table/GetFieldsByTableIDs", paramList, cb);
        }

        /**
         * @param tid
         * @param isloadField
         * @param cb          回调
         */
        public static Callback.Cancelable GetTableByID(int tid, boolean isloadField, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            paramList.add(isloadField);
            return Const.get("Table/GetTableByID", paramList, cb);
        }

        /**
         * @param tname
         * @param isloadField
         * @param cb          回调
         */
        public static Callback.Cancelable GetTableByName(String tname, boolean isloadField, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tname);
            paramList.add(isloadField);
            return Const.get("Table/GetTableByName", paramList, cb);
        }

        /**
         * 保存某个数据表权限
         *
         * @param table
         * @param cb    回调
         */
        public static Callback.Cancelable SaveTable(ApiEntity.UserTable table, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("table", table);
            return Const.post("Table/SaveTable", paramMap, cb);
        }

        /**
         * 保存字段
         *
         * @param field 字段结构
         * @param tid   所属的数据表
         * @param cb    回调
         */
        public static Callback.Cancelable SaveField(ApiEntity.UserField field, int tid, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("field", field);
            paramMap.put("tid", tid);
            return Const.post("Table/SaveField", paramMap, cb);
        }

        /**
         * 保存应用
         *
         * @param info
         * @param cb   回调
         */
        public static Callback.Cancelable SaveInfo(ApiEntity.SystemInfo info, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("info", info);
            return Const.post("Table/SaveInfo", paramMap, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DeleteInfo(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("Table/DeleteInfo", paramList, cb);
        }

        /**
         * @param tid
         * @param cb  回调
         */
        public static Callback.Cancelable DeleteTable(int tid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            return Const.get("Table/DeleteTable", paramList, cb);
        }

        /**
         * @param id
         * @param tid
         * @param cb  回调
         */
        public static Callback.Cancelable DeleteField(int id, int tid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            paramList.add(tid);
            return Const.get("Table/DeleteField", paramList, cb);
        }

        /**
         * 获取表关系
         *
         * @param mtable 主表ID
         * @param cb     回调
         */
        public static Callback.Cancelable GetRelations(int mtable, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(mtable);
            return Const.get("Table/GetRelations", paramList, cb);
        }

        /**
         * @param rel
         * @param cb  回调
         */
        public static Callback.Cancelable DeleteRelation(ApiEntity.RelationSetting rel, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("rel", rel);
            return Const.post("Table/DeleteRelation", paramMap, cb);
        }

        /**
         * @param rel
         * @param cb  回调
         */
        public static Callback.Cancelable SaveRelation(ApiEntity.RelationSetting rel, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("rel", rel);
            return Const.post("Table/SaveRelation", paramMap, cb);
        }

        /**
         * @param tid
         * @param cb  回调
         */
        public static Callback.Cancelable GetSubTable(int tid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            return Const.get("Table/GetSubTable", paramList, cb);
        }

        /**
         * @param tid
         * @param widthTable
         * @param cb         回调
         */
        public static Callback.Cancelable GetUserViewByTableID(int tid, boolean widthTable, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            paramList.add(widthTable);
            return Const.get("Table/GetUserViewByTableID", paramList, cb);
        }

        /**
         * 保存视图配置
         *
         * @param view
         * @param isnew
         * @param cb    回调
         */
        public static Callback.Cancelable SaveView(ApiEntity.UserView view, boolean isnew, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("view", view);
            paramMap.put("isnew", isnew);
            return Const.post("Table/SaveView", paramMap, cb);
        }

        /**
         * @param ids
         * @param loadField
         * @param cb        回调
         */
        public static Callback.Cancelable GetTables(int[] ids, boolean loadField, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(ids);
            paramList.add(loadField);
            return Const.get("Table/GetTables", paramList, cb);
        }

        /**
         * @param tid
         * @param cb  回调
         */
        public static Callback.Cancelable DeleteTableFields(int tid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            return Const.get("Table/DeleteTableFields", paramList, cb);
        }

        /**
         * @param ds
         * @param cb 回调
         */
        public static Callback.Cancelable DataSourceToSql(ApiEntity.DataSource ds, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ds", ds);
            return Const.post("Table/DataSourceToSql", paramMap, cb);
        }

        /**
         * @param fid
         * @param cb  回调
         */
        public static Callback.Cancelable GetFieldSearchSet(int fid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(fid);
            return Const.get("Table/GetFieldSearchSet", paramList, cb);
        }
    }

    public static class TemplateAPI {
        /**
         * 加载自己的所有模版
         *
         * @param cb 回调
         */
        public static Callback.Cancelable LoadTemplates(RequestCallback cb) {
            return Const.get("TemplateAPI/LoadTemplates", cb);
        }

        /**
         * 获取个人导航设置列表
         *
         * @param cb 回调
         */
        public static Callback.Cancelable LoadPersonTemplates(RequestCallback cb) {
            return Const.get("TemplateAPI/LoadPersonTemplates", cb);
        }

        /**
         * 根据注册公司代码加载该公司模板
         *
         * @param code
         * @param cb   回调
         */
        public static Callback.Cancelable LoadTemplatesByCompany(String code, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(code);
            return Const.get("TemplateAPI/LoadTemplatesByCompany", paramList, cb);
        }

        /**
         * @param folder
         * @param cb     回调
         */
        public static Callback.Cancelable LoadTemplatesByFolder(int folder, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(folder);
            return Const.get("TemplateAPI/LoadTemplatesByFolder", paramList, cb);
        }

        /**
         * 加载自己的所有模版
         *
         * @param cb 回调
         */
        public static Callback.Cancelable LoadBaseTemplates(RequestCallback cb) {
            return Const.get("TemplateAPI/LoadBaseTemplates", cb);
        }

        /**
         * 加载标准库中所有模版
         *
         * @param cb 回调
         */
        public static Callback.Cancelable LoadSTDTemplates(RequestCallback cb) {
            return Const.get("TemplateAPI/LoadSTDTemplates", cb);
        }

        /**
         * 读取某个模版
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable LoadTemplateByID(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("TemplateAPI/LoadTemplateByID", paramList, cb);
        }

        /**
         * 保存模版
         *
         * @param temp
         * @param cb   回调
         */
        public static Callback.Cancelable SaveTemplate(ApiEntity.Template temp, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("temp", temp);
            return Const.post("TemplateAPI/SaveTemplate", paramMap, cb);
        }

        /**
         * 保存角色组
         *
         * @param group
         * @param cb    回调
         */
        public static Callback.Cancelable SaveGroup(ApiEntity.Group group, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(group);
            return Const.get("TemplateAPI/SaveGroup", paramList, cb);
        }

        /**
         * 获取某个页面的权限
         *
         * @param tid
         * @param cb  回调
         */
        public static Callback.Cancelable LoadGroup(int tid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            return Const.get("TemplateAPI/LoadGroup", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DeleteGroup(String id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("TemplateAPI/DeleteGroup", paramList, cb);
        }

        /**
         * @param tid
         * @param tableids
         * @param cb       回调
         */
        public static Callback.Cancelable SaveTemplateTables(int tid, List<Integer> tableids, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            paramList.add(tableids);
            return Const.get("TemplateAPI/SaveTemplateTables", paramList, cb);
        }

        /**
         * @param ID
         * @param cb 回调
         */
        public static Callback.Cancelable DeleteTemplate(int ID, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(ID);
            return Const.get("TemplateAPI/DeleteTemplate", paramList, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetTemplateIcons(RequestCallback cb) {
            return Const.get("TemplateAPI/GetTemplateIcons", cb);
        }

        /**
         * @param tid
         * @param tableids
         * @param cb       回调
         */
        public static Callback.Cancelable DeleteTemplateTables(int tid, List<Integer> tableids, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            paramList.add(tableids);
            return Const.get("TemplateAPI/DeleteTemplateTables", paramList, cb);
        }

        /**
         * @param tid
         * @param cb  回调
         */
        public static Callback.Cancelable GetTemplateTables(int tid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            return Const.get("TemplateAPI/GetTemplateTables", paramList, cb);
        }

        /**
         * @param folder
         * @param cb     回调
         */
        public static Callback.Cancelable SaveTemplateFolder(ApiEntity.SystemInfo folder, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("folder", folder);
            return Const.post("TemplateAPI/SaveTemplateFolder", paramMap, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable LoadTemplateFolder(RequestCallback cb) {
            return Const.get("TemplateAPI/LoadTemplateFolder", cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DeleteTemplateFolder(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("TemplateAPI/DeleteTemplateFolder", paramList, cb);
        }

        /**
         * 获取导航
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetNaviID(String id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("TemplateAPI/GetNaviID", paramList, cb);
        }

        /**
         * 保存导航数据
         *
         * @param nav
         * @param cb  回调
         */
        public static Callback.Cancelable SaveNavigation(ApiEntity.Navigation nav, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("nav", nav);
            return Const.post("TemplateAPI/SaveNavigation", paramMap, cb);
        }

        /**
         * 获取导航数据
         *
         * @param TemplateID
         * @param cb         回调
         */
        public static Callback.Cancelable GetNavigation(String TemplateID, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(TemplateID);
            return Const.get("TemplateAPI/GetNavigation", paramList, cb);
        }

        /**
         * 获取导航数据
         *
         * @param cb 回调
         */
        public static Callback.Cancelable GetIndexNavigation(RequestCallback cb) {
            return Const.get("TemplateAPI/GetIndexNavigation", cb);
        }

        /**
         * 更改导航 显示/隐藏
         *
         * @param navid
         * @param visible
         * @param cb      回调
         */
        public static Callback.Cancelable UpdateNavVisible(int navid, int visible, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(navid);
            paramList.add(visible);
            return Const.get("TemplateAPI/UpdateNavVisible", paramList, cb);
        }

        /**
         * 导航设置更改排序
         *
         * @param cb 回调
         */
        public static Callback.Cancelable UpdateNavSort(int navid1, int navid2, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(navid1);
            paramList.add(navid2);
            return Const.get("TemplateAPI/UpdateNavSort", paramList, cb);
        }

        /**
         * 更改导航名称
         *
         * @param navid
         * @param navName
         * @param cb      回调
         */
        public static Callback.Cancelable UpdateNavName(int navid, String navName, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(navid);
            paramList.add(navName);
            return Const.get("TemplateAPI/UpdateNavName", paramList, cb);
        }

        /**
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserGridNavigation(RequestCallback cb) {
            return Const.get("TemplateAPI/GetUserGridNavigation", cb);
        }

        /**
         * 删除导航数据
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DeleteNavigation(String id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("TemplateAPI/DeleteNavigation", paramList, cb);
        }

        /**
         * 保存选择模块
         *
         * @param AddID
         * @param DelID
         * @param cb    回调
         */
        public static Callback.Cancelable SaveSelectTemplate(String AddID, String DelID, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(AddID);
            paramList.add(DelID);
            return Const.get("TemplateAPI/SaveSelectTemplate", paramList, cb);
        }

        /**
         * 获取模块管理
         *
         * @param where
         * @param cb    回调
         */
        public static Callback.Cancelable GetSysModule(String where, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(where);
            return Const.get("TemplateAPI/GetSysModule", paramList, cb);
        }

        /**
         * 保存模块管理
         *
         * @param ID
         * @param Name
         * @param URL
         * @param cb   回调
         */
        public static Callback.Cancelable SaveSysModule(int ID, String Name, String URL, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("ID", ID);
            paramMap.put("Name", Name);
            paramMap.put("URL", URL);
            return Const.post("TemplateAPI/SaveSysModule", paramMap, cb);
        }

        /**
         * 删除模块管理
         *
         * @param ID
         * @param cb 回调
         */
        public static Callback.Cancelable DelSysModule(String ID, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(ID);
            return Const.get("TemplateAPI/DelSysModule", paramList, cb);
        }

        /**
         * @param templateID
         * @param cb         回调
         */
        public static Callback.Cancelable LoadFlows(int templateID, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(templateID);
            return Const.get("TemplateAPI/LoadFlows", paramList, cb);
        }

        /**
         * @param re
         * @param cb 回调
         */
        public static Callback.Cancelable SaveResource(ApiEntity.Resource re, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("re", re);
            return Const.post("TemplateAPI/SaveResource", paramMap, cb);
        }

        /**
         * @param tid
         * @param cb  回调
         */
        public static Callback.Cancelable LoadResource(String tid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tid);
            return Const.get("TemplateAPI/LoadResource", paramList, cb);
        }

        /**
         * @param rid
         * @param cb  回调
         */
        public static Callback.Cancelable LoadResourceByID(int rid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(rid);
            return Const.get("TemplateAPI/LoadResourceByID", paramList, cb);
        }

        /**
         * @param tempid
         * @param cb     回调
         */
        public static Callback.Cancelable LoadAutoCode(int tempid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tempid);
            return Const.get("TemplateAPI/LoadAutoCode", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DeleteResource(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("TemplateAPI/DeleteResource", paramList, cb);
        }

        /**
         * 卸载模板
         *
         * @param tplid
         * @param companyCode
         * @param cb          回调
         */
        public static Callback.Cancelable UnloadTemplate(int tplid, String companyCode, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tplid);
            paramList.add(companyCode);
            return Const.get("TemplateAPI/UnloadTemplate", paramList, cb);
        }

        /**
         * 更新模板
         *
         * @param tplid
         * @param companyCode
         * @param cb          回调
         */
        public static Callback.Cancelable UpdateTemplate(int tplid, String companyCode, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tplid);
            paramList.add(companyCode);
            return Const.get("TemplateAPI/UpdateTemplate", paramList, cb);
        }

        /**
         * 安装模板
         *
         * @param tplid
         * @param companyCode
         * @param cb          回调
         */
        public static Callback.Cancelable InitTemplate(int tplid, String companyCode, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tplid);
            paramList.add(companyCode);
            return Const.get("TemplateAPI/InitTemplate", paramList, cb);
        }
    }

    public static class TestAPI {
        /**
         * test
         * @param tname
         * @param columns
         * @param datas
         * @param cb 回调
         */
        //        public static Callback.Cancelable Test(String tname,List<String> columns,List<ApiEntity.List`1> datas,RequestCallback cb) {
        //            List<Object> paramList = new ArrayList<Object>();
        //            paramList.add(tname);
        //            paramList.add(columns);
        //            paramList.add(datas);
        //            return Const.post("test",paramList,cb);
        //        }

        /**
         * test
         *
         * @param cb 回调
         */
        public static Callback.Cancelable Test(RequestCallback cb) {
            return Const.post("test1", cb);
        }

        /**
         * @param tname
         * @param columns
         * @param datas
         * @param cb      回调
         */
        public static Callback.Cancelable CheckField(String tname, List<String> columns, List<String> datas, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tname);
            paramList.add(columns);
            paramList.add(datas);
            return Const.post("TestAPI/CheckField", paramList, cb);
        }

        /**
         * @param index
         * @param cb    回调
         */
        public static Callback.Cancelable Def(int index, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(index);
            return Const.get("TestAPI/Def", paramList, cb);
        }
    }

    public static class UserFileAPI {
        /**
         * @param tempid
         * @param cb     回调
         */
        public static Callback.Cancelable LoadUserFilesByTemplate(int tempid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tempid);
            return Const.get("UserFile/LoadUserFilesByTemplate", paramList, cb);
        }

        /**
         * @param tempid
         * @param cb     回调
         */
        public static Callback.Cancelable LoadUserFilesByTemplateForm(int tempid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tempid);
            return Const.get("UserFile/LoadUserFilesByTemplateForm", paramList, cb);
        }

        /**
         * @param tempid
         * @param cb     回调
         */
        public static Callback.Cancelable LoadUserFilesByTemplateGrid(int tempid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tempid);
            return Const.get("UserFile/LoadUserFilesByTemplateGrid", paramList, cb);
        }

        /**
         * @param tempid
         * @param cb     回调
         */
        public static Callback.Cancelable LoadUserFilesByTemplateCustomPage(int tempid, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tempid);
            return Const.get("UserFile/LoadUserFilesByTemplateCustomPage", paramList, cb);
        }

        /**
         * @param tempid
         * @param type
         * @param key
         * @param cb     回调
         */
        public static Callback.Cancelable SearchUserFile(int tempid, int type, String key, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("tempid", tempid);
            paramMap.put("type", type);
            paramMap.put("key", key);
            return Const.post("UserFile/SearchUserFile", paramMap, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable DeleteUserFile(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("UserFile/DeleteUserFile", paramList, cb);
        }

        /**
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable LoadUserFile(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("UserFile/LoadUserFile", paramList, cb);
        }

        /**
         * @param file
         * @param cb   回调
         */
        public static Callback.Cancelable SaveUserFile(ApiEntity.UserFile file, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(file);
            return Const.post("UserFile/SaveUserFile", paramList, cb);
        }

        /**
         * @param tplid
         * @param fileID
         * @param tids
         * @param cb     回调
         */
        public static Callback.Cancelable AppendReportDataSource(int tplid, int fileID, int[] tids, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(tplid);
            paramList.add(fileID);
            paramList.add(tids);
            return Const.get("UserFile/AppendReportDataSource", paramList, cb);
        }

        /**
         * @param TypeId
         * @param cb     回调
         */
        public static Callback.Cancelable SearchTableList(String TypeId, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(TypeId);
            return Const.get("UserFile/SearchTableList", paramList, cb);
        }

        /**
         * 获取文件信息(不包含Content)
         *
         * @param id
         * @param cb 回调
         */
        public static Callback.Cancelable GetUserFileInfo(int id, RequestCallback cb) {
            List<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            return Const.get("UserFile/GetUserFileInfo", paramList, cb);
        }

        /**
         * @param imageData
         * @param cb        回调
         */
        public static Callback.Cancelable SaveUploadImage(String imageData, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("imageData", imageData);
            return Const.post("UserFile/SaveUploadImage", paramMap, cb);
        }

        /**
         * @param pages
         * @param toTemplate
         * @param isdelete
         * @param cb         回调
         */
        public static Callback.Cancelable MoveTo(List<ApiEntity.UserFile> pages, int toTemplate, boolean isdelete, RequestCallback cb) {
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            paramMap.put("pages", pages);
            paramMap.put("toTemplate", toTemplate);
            paramMap.put("isdelete", isdelete);
            return Const.post("UserFile/MoveTo", paramMap, cb);
        }
    }
}