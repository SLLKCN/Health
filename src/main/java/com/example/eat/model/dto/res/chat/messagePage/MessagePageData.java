package com.example.eat.model.dto.res.chat.messagePage;

import lombok.Data;

@Data
public class MessagePageData {
    /**
     * 最后一次聊天的内容
     */
    private String lastTalkMsg;
    /**
     * 最后一次信息的类型
     */
//    private int lastTalkMsgType;
    /**
     * 最后一次聊天的时间
     */
    private String lastTalkTime;
    /**
     * 头像链接
     */
    private String receiverAvatar;
    /**
     * 另一个人的昵称
     */
    private String receiverName;
    /**
     * 另一个人的openid
     */
    private Integer receiverOpenId;
    /**
     * 未读消息的数量
     */
    private int unreadMsgNum;
}
