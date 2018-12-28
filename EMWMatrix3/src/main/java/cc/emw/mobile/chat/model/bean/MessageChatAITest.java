package cc.emw.mobile.chat.model.bean;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.chat.base.ChatContent;


/**
 * Created by sunny.du on 2017/1/11.
 * 模拟数据    后续删除
 */
public class MessageChatAITest {
    private int type;//接收/发送消息类型
    private int msgType;//消息类型
    private int msgSunType;//子列表消息类型

    private MessageChatAITest(int type, int msgType){
        this(type,msgType,-1);
    }
    private MessageChatAITest(int type, int msgType,int msgSunType){
        this.type=type;
        this.msgType=msgType;
        this.msgSunType=msgSunType;
    }

    public static List<MessageChatAITest> initBean() {
        List<MessageChatAITest> msgList=new ArrayList<>();//列表
        MessageChatAITest msg1=new MessageChatAITest(ChatContent.TYPE_ITEM_SEND,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg2=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg3=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.WEB_MSG);

        MessageChatAITest msg4=new MessageChatAITest(ChatContent.TYPE_ITEM_SEND,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg5=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg6=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.LIST_MSG,ChatContent.LIST_MAP);

        MessageChatAITest msg7=new MessageChatAITest(ChatContent.TYPE_ITEM_SEND,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg8=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg9=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.MAP_MSG);

        MessageChatAITest msg10=new MessageChatAITest(ChatContent.TYPE_ITEM_SEND,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg11=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg12=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.LIST_MSG,ChatContent.LIST_IMAGE);

        MessageChatAITest msg13=new MessageChatAITest(ChatContent.TYPE_ITEM_SEND,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg14=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.MAP_MSG);
        MessageChatAITest msg15=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.LIST_MSG,ChatContent.LIST_SCHEDULE);

        MessageChatAITest msg16=new MessageChatAITest(ChatContent.TYPE_ITEM_SEND,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg17=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg18=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.LIST_MSG,ChatContent.LIST_TRAVEL);

        MessageChatAITest msg19=new MessageChatAITest(ChatContent.TYPE_ITEM_SEND,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg20=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg21=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.WEATHER_MSG);

        MessageChatAITest msg22=new MessageChatAITest(ChatContent.TYPE_ITEM_SEND,ChatContent.DEFAULT_MSG);
        MessageChatAITest msg23=new MessageChatAITest(ChatContent.TYPE_ITEM_RECE,ChatContent.DEFAULT_MSG);
        msgList.add(msg1);
        msgList.add(msg2);
        msgList.add(msg3);
        msgList.add(msg4);
        msgList.add(msg5);
        msgList.add(msg6);
        msgList.add(msg7);
        msgList.add(msg8);
        msgList.add(msg9);
        msgList.add(msg10);
        msgList.add(msg11);
        msgList.add(msg12);
        msgList.add(msg13);
        msgList.add(msg14);
        msgList.add(msg15);
        msgList.add(msg16);
        msgList.add(msg17);
        msgList.add(msg18);
        msgList.add(msg19);
        msgList.add(msg20);
        msgList.add(msg21);
        msgList.add(msg22);
        msgList.add(msg23);
        return msgList;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMsgType() {
        return msgType;
    }

    public int getType() {
        return type;
    }

    public void setMsgSunType(int msgSunType) {
        this.msgSunType = msgSunType;
    }

    public int getMsgSunType() {
        return msgSunType;
    }
}
