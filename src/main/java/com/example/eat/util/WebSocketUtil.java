package com.example.eat.util;

import com.example.eat.decoderAndEncoder.PrivateChatParamDecoder;
import com.example.eat.decoderAndEncoder.PrivateChatParamEncoder;
import com.example.eat.model.dto.param.chat.PrivateChatParam;
import com.example.eat.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/chat/{token}", encoders = PrivateChatParamEncoder.class, decoders = PrivateChatParamDecoder.class)
public class WebSocketUtil {
    private static ChatService chatService;
    private static int onlineCount = 0;
    private static ConcurrentHashMap<Integer, WebSocketUtil> webSocketMap = new ConcurrentHashMap<>();
    private Session session;//与某个客户端的连接会话
    private Integer currentUser;
    private boolean inChatView = false; // 用户当前位置，默认主页

    public boolean getInChatView(){
        return inChatView;
    }
    public void setInChatView(boolean inChatView){
        this.inChatView = inChatView;
    }
    /**
     * 查找某个用户是否在线,若存在返回WebSocketUtil，不存在返回null
     *
     * @param openId
     * @return boolean
     */
    public static WebSocketUtil findReceiver(Integer openId) {
        return webSocketMap.get(openId);
    }


    /**
     * 获取当前所有在线用户名
     */
    public static void allCurrentOnline() {
        for (Map.Entry<Integer, WebSocketUtil> item : webSocketMap.entrySet()) {
            System.out.println(item.getKey());
        }
    }

    /**
     * 发送给指定用户
     */
    public static void sendMessageTo(String message, String openId) {
        WebSocketUtil item = webSocketMap.get(openId);
        System.out.println("to " + openId + ":" + message);
        try {
            item.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) {
        System.out.println(message);
        for (Map.Entry<Integer, WebSocketUtil> item : webSocketMap.entrySet()) {
            item.getValue().sendMessage(message);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketUtil.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketUtil.onlineCount--;
    }

    @Autowired
    public void setInstantChatService(ChatService chatService) {
        WebSocketUtil.chatService = chatService;
    }

    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session) {
        if ((!StringUtils.hasLength(token)) || (!JwtUtils.verify(token))) {
            log.error("WebSocket token错误!");
            throw new RuntimeException("鉴权异常");
        }
        Integer userId = JwtUtils.getUserIdByToken(token);
        if (webSocketMap.get(userId) != null) {
            throw new RuntimeException("已存在当前用户！");
        }
        this.currentUser = userId;
        this.session = session;
        webSocketMap.put(userId, this);
        addOnlineCount();
        log.info("有新连接" + currentUser + "加入！当前在线人数为" + getOnlineCount());
    }

    @OnClose
    public void onClose() {
        if (this.currentUser == null) {
            return;
        }
        Integer closeUser = this.currentUser;
        webSocketMap.remove(this.currentUser);
        subOnlineCount();
        log.info(closeUser + "连接关闭！当前在线人数为" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info(currentUser + ":" + message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PrivateChatParam chatParam = objectMapper.readValue(message, PrivateChatParam.class);
            if(!chatParam.getSender().equals(currentUser)){
                log.error(String.valueOf(currentUser));
                log.error(String.valueOf(chatParam.getSender()));
                sendMessage("sender错误！");
                return;
            }
            chatService.sendInstantMsg(chatParam);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("信息格式错误！");
            sendMessage("信息格式错误！");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if ("鉴权异常".equals(throwable.getMessage())) {
            return;
        }
        log.error("websocket发生错误！");
        throwable.printStackTrace();
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessage(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.error("sendMessage()序列化异常");
        }
        try {
            this.session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}