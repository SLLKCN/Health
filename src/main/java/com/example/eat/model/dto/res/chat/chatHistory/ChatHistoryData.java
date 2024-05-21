package com.example.eat.model.dto.res.chat.chatHistory;

import lombok.Data;

@Data
public class ChatHistoryData {
    private String message;
    private long msgId;
//    private long msgType;
    private Integer receiverOpenId;
    private Integer senderOpenId;
    private String sendTime;
}
