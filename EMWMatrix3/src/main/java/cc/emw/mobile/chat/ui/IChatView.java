package cc.emw.mobile.chat.ui;

import java.util.List;

import cc.emw.mobile.chat.model.bean.ChatMsgBean;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.entity.UserInfo;

/**
 * Created by sunny.du on 2017/5/8.
 */

public interface IChatView {
        /**
         *当消息状态更改时刷新消息列表展示
         * @param msgList  消息列表
         * @param isR   是否滚动
         */
        void modifyMsgSateView( List<ChatMsgBean> msgList,boolean isR);

        /**
         * 发送广播
         * @param broadCastName      广播字段
         * @param action             传递参数 标识key
         * @param id                 传递int的参数   标识value
         * @param broadCastAction    action
         * 逻辑  当broadCastName，action，id为空的时候仅将broadCastAction作为广播的action字段并且发送广播，否则broadCastName，action，id使用这三个参数
         */
        void sendBroadCast(String broadCastName,String action,int id,String broadCastAction);

        /**
         * 给ChatActivity的hanlder发消息
         * @param msg   消息内容
         */
        void sendHandlerView1(android.os.Message msg);

        /**
         * 开启消息列表上拉刷新展示
         */
        void showRefreshView();

        /**
         * 当刷新完成时调用本方法进行列表更新
         * @param dataList  消息列表
         * @param pageIndex  当前页数
         * @param pageSize   消息每页数量
         */
        void refreshListView(List<ChatMsgBean> dataList,int pageIndex,int pageSize);

        /**
         * 弹起吐司
         * @param name   吐司内容
         */
        void showToast(String name);

        /**
         * 当添加群组成员时调用本方法发送广播刷新群组信息
         * @param action   字段
         */
        void sendBroadCast4saveGroupInfo(String action);

        /**
         * 截取视频的第一帧   初始化VideoPlayer
         * @param videos  要播放视频的实体类
         */
        void initVideoPlayer(Videos videos);

        /**
         * 聊天对话框头部聊天目标人的名字设置方法
         * @param name   目标人的名字
         */
        void setTitleNameTextView(String name);

        /**
         * 弹出AlertDialog 告知用户当前用户信息错误，点击关闭当前activity
         */
        void showDialogError();

        /**
         *  弹起带有图片的吐司
         * @param name   吐司内容
         * @param id     图片Rid
         */
        void showToastPro(String name,int id);

        /**
         * 弹起Dialog加载框
         * 当请求网络的时候调用
         */
        void showDialog();

        /**
         * 关闭Dialog加载框
         * 当请求网络的时候调用
         */
        void dismissDialog();

        /**
         * 关闭当前Activity
         */
        void finishActivity();

        /**
         * 群组界面当第一个字符是@的时候   用于隐藏人员选择列表
         */
        void groupUserSearchGone();

        /**
         *群组界面当第一个字符是@的时候   用于展示人员选择列表
         * @param parserList  检索出的人员列表
         */
        void groupUserSearchVisible(List<UserInfo> parserList);

}
