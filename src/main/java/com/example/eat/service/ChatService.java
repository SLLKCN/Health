package com.example.eat.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.chat.PrivateChatParam;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.chat.chatHistory.ChatHistoryRes;
import com.example.eat.model.dto.res.chat.chatHistory.EnterChatRes;
import com.example.eat.model.dto.res.chat.messagePage.MessagePageRes;
import com.example.eat.model.po.chat.PrivateChatLog;
import org.springframework.web.multipart.MultipartFile;

public interface ChatService extends IService<PrivateChatLog> {
    void sendInstantMsg(PrivateChatParam chatParam);
//    CommonResult<BlankRes> chatImg(MultipartFile multipartFile, Integer sender, Integer receiver, String sendTime, int voiceLen);
    CommonResult<MessagePageRes> getMessagePage();
    CommonResult<EnterChatRes> enterChat(Integer talkerOpenId);
    CommonResult<ChatHistoryRes> getChatHistory(Integer talkerOpenId, Integer currentMsgId);
    CommonResult<BlankRes> quitChatPage();
}
