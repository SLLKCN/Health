package com.example.eat.controller;


import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.chat.chatHistory.ChatHistoryRes;
import com.example.eat.model.dto.res.chat.chatHistory.EnterChatRes;
import com.example.eat.model.dto.res.chat.messagePage.MessagePageRes;
import com.example.eat.service.ChatService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
public class ChatController {
    @Autowired
    private ChatService chatService;


//    @PostMapping("/chat/file")
//    public CommonResult<BlankRes> chatImg(@RequestParam("file") MultipartFile multipartFile,
//                                          @NotNull(message = "sender不能为空") String sender,
//                                          @NotNull(message = "receiver不能为空") String receiver,
//                                          @NotNull(message = " sendTime不能为空") String sendTime,
//                                          @RequestParam("msgType") @NotNull(message = " msgType不能为空") int msgType,
//                                           int voiceLen) {
//        return chatService.chatImg(multipartFile, sender, receiver, sendTime, msgType,voiceLen);
//    }

    @GetMapping("/chat")
    public CommonResult<MessagePageRes> getMessagePage() {
        return chatService.getMessagePage();
    }

    @GetMapping("/chat/{talkerId}")
    public CommonResult<EnterChatRes> enterChat(@NotNull(message = "talkerOpenid can't be null!") @PathVariable("talkerId") Integer talkerId) {
        return chatService.enterChat(talkerId);
    }

    @GetMapping("/chat/history/{talkerId}")
    public CommonResult<ChatHistoryRes> getChatHistory(@NotNull(message = "talkerOpenid can't be null!") @PathVariable("talkerId") Integer talkerId,
                                                       @NotNull(message = "currentMsgId can't be null!") Integer currentMsgId) {
        return chatService.getChatHistory(talkerId,currentMsgId);
    }
    @PostMapping("/chat/quit")
    public CommonResult<BlankRes> quitChatPage(){
        return chatService.quitChatPage();
    }
}
