package com.example.eat.model.dto.res.chat;

import lombok.Data;

@Data
public class ChatRes {
    private Integer senderOpenId;
    private String senderNickName;
    private String senderAvatar;
    private String sendTime;
    private String message;
//    private Integer msgType;
    private boolean inChatView;

}
