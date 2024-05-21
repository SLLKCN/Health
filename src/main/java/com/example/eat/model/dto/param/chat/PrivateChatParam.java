package com.example.eat.model.dto.param.chat;

import lombok.Data;

@Data
public class PrivateChatParam {
    private Integer sender;
    private Integer receiver;
    private String sendTime;
    private String message;
}
