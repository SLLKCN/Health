package com.example.eat.model.dto.res.chat.chatHistory;

import lombok.Data;

import java.util.List;

@Data
public class EnterChatRes {
    private List<ChatHistoryData> chatHistoryList;
    private String receiverAvatar;
    private String receiverNickname;
}
