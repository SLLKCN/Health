package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.chat.PrivateChatLogDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.chat.PrivateChatParam;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.chat.ChatRes;
import com.example.eat.model.dto.res.chat.chatHistory.ChatHistoryData;
import com.example.eat.model.dto.res.chat.chatHistory.ChatHistoryRes;
import com.example.eat.model.dto.res.chat.chatHistory.EnterChatRes;
import com.example.eat.model.dto.res.chat.messagePage.MessagePageData;
import com.example.eat.model.dto.res.chat.messagePage.MessagePageRes;
import com.example.eat.model.po.chat.PrivateChatLog;
import com.example.eat.service.ChatService;
import com.example.eat.service.UserService;
import com.example.eat.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.eat.util.ChatHistoryUtil.getChatHistoryCacheName;
import static com.example.eat.util.WebSocketUtil.findReceiver;


@Slf4j
@Service
public class ChatServiceImpl extends ServiceImpl<PrivateChatLogDao, PrivateChatLog> implements ChatService {
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private PrivateChatLogDao privateChatLogDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;

    @Override
    public void sendInstantMsg(PrivateChatParam chatParam) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PrivateChatLog privateChatLog = new PrivateChatLog();
        privateChatLog.setSender(chatParam.getSender());
        privateChatLog.setReceiver(chatParam.getReceiver());
        privateChatLog.setSendTime(LocalDateTime.parse(chatParam.getSendTime(), dateTimeFormatter));
        privateChatLog.setMessage(chatParam.getMessage());
//        privateChatLog.setMsgType(1);
        this.save(privateChatLog);
        WebSocketUtil receiverUtil = findReceiver(chatParam.getReceiver());
        WebSocketUtil senderUtil = findReceiver(chatParam.getSender());
        String listCacheName = getChatHistoryCacheName(chatParam.getSender(), chatParam.getReceiver());
        redisUtil.listLeftPush(listCacheName, privateChatLog);
        if (redisUtil.listLen(listCacheName) >= 100) {
            redisUtil.listRightPop(listCacheName);
        }
        if (receiverUtil != null) {
            ChatRes chatRes = new ChatRes();
            chatRes.setSenderOpenId(chatParam.getSender());
            chatRes.setSenderNickName(userService.getNickName(chatParam.getSender()));
            chatRes.setSenderAvatar(userService.getAvatar(chatParam.getSender()));
            chatRes.setSendTime(chatParam.getSendTime());
            chatRes.setMessage(chatParam.getMessage());
            chatRes.setInChatView(receiverUtil.getInChatView());
//            chatRes.setMsgType(1);
            receiverUtil.sendMessage(chatRes);
            if (!receiverUtil.getInChatView()) {
                addUnreadMsgNum(chatParam.getReceiver(), chatParam.getSender(), getUnreadMsgNum(chatParam.getReceiver(), chatParam.getSender()));
            }
        } else {
            addUnreadMsgNum(chatParam.getReceiver(), chatParam.getSender(), getUnreadMsgNum(chatParam.getReceiver(), chatParam.getSender()));
            //TODO 调用通知接口
        }
        senderUtil.sendMessage(CommonResult.success("发送成功！"));
    }

//    @Override
//    public CommonResult<BlankRes> chatImg(MultipartFile multipartFile, Integer sender, Integer receiver, String sendTime, int voiceLen) {
//        String filename = minioUtil.uploadFileByFile(multipartFile);
//        if (filename == null) {
//            return CommonResult.fail("发送失败！");
//        }
//        PrivateChatLog privateChatLog = new PrivateChatLog();
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        privateChatLog.setSender(sender);
//        privateChatLog.setReceiver(receiver);
//        privateChatLog.setSendTime(LocalDateTime.parse(sendTime, dateTimeFormatter));
//        privateChatLog.setMessage(filename);
////        if (msgType == 3) msgType = 300 + voiceLen;
////        privateChatLog.setMsgType(msgType);
//        this.save(privateChatLog);
//        WebSocketUtil receiverUtil = findReceiver(receiver);
//        String listCacheName = getChatHistoryCacheName(sender, receiver);
//        redisUtil.listLeftPush(listCacheName, privateChatLog);
//        if (redisUtil.listLen(listCacheName) >= 100) {
//            redisUtil.listRightPop(listCacheName);
//        }
//        if (receiverUtil != null) {
//            ChatRes chatRes = new ChatRes();
//            chatRes.setSenderOpenId(sender);
//            chatRes.setSenderAvatar(userService.getAvatar(sender));
//            chatRes.setSenderNickName(userService.getNickName(sender));
//            chatRes.setSendTime(sendTime);
//            chatRes.setMessage(filename);
////            chatRes.setMsgType(msgType);
//            chatRes.setInChatView(receiverUtil.getInChatView());
//            receiverUtil.sendMessage(chatRes);
//            if (!receiverUtil.getInChatView()) {
//                addUnreadMsgNum(receiver, sender, getUnreadMsgNum(receiver, sender));
//            }
//        } else {
//            addUnreadMsgNum(receiver, sender, getUnreadMsgNum(receiver, sender));
//            //TODO 调用通知接口
//        }
//        return CommonResult.success("发送成功！");
//    }

    @Override
    public CommonResult<MessagePageRes> getMessagePage() {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }        List<PrivateChatLog> privateChatLogList = privateChatLogDao.findRecentChatLog(userId);
        List<PrivateChatLog> rightLogList = new ArrayList<>();
        Map<Integer, Long> maxIdMap = new HashMap<>();
        for (PrivateChatLog privateChatLog : privateChatLogList) {
            Integer talkerOpenId = userId==privateChatLog.getReceiver() ? privateChatLog.getSender() : privateChatLog.getReceiver();
            Long id = privateChatLog.getId();
            if (!maxIdMap.containsKey(talkerOpenId) || id > maxIdMap.get(talkerOpenId)) {
                maxIdMap.put(talkerOpenId, id);
            }
        }
        for (PrivateChatLog privateChatLog : privateChatLogList) {
            Integer talkerOpenId = userId==privateChatLog.getReceiver() ? privateChatLog.getSender() : privateChatLog.getReceiver();
            Long id = privateChatLog.getId();
            if (id.equals(maxIdMap.get(talkerOpenId))) {
                rightLogList.add(privateChatLog);
            }
        }
        MessagePageRes messagePageRes = new MessagePageRes();
        List<MessagePageData> messagePageDataList = new ArrayList<>();
        //排序，后发的消息在上
        rightLogList.sort(Comparator.comparingLong(PrivateChatLog::getId).reversed());
        for (PrivateChatLog privateChatLog : rightLogList) {
            Integer talkerOpenId = userId==privateChatLog.getReceiver() ? privateChatLog.getSender() : privateChatLog.getReceiver();
            MessagePageData messagePageData = new MessagePageData();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            messagePageData.setLastTalkMsg(privateChatLog.getMessage());
//            messagePageData.setLastTalkMsgType(privateChatLog.getMsgType());
//            if (privateChatLog.getMsgType() != 1)
//                messagePageData.setLastTalkMsg(cacheService.getFileUrl(privateChatLog.getMessage()));
            messagePageData.setLastTalkTime(dateTimeFormatter.format(privateChatLog.getSendTime()));
            messagePageData.setReceiverAvatar(userService.getAvatar(talkerOpenId));
            messagePageData.setReceiverName(userService.getNickName(talkerOpenId));
            messagePageData.setReceiverOpenId(talkerOpenId);
            messagePageData.setUnreadMsgNum(getUnreadMsgNum(userId, talkerOpenId));
            messagePageDataList.add(messagePageData);
        }
        messagePageRes.setChatList(messagePageDataList);
        log.info("getMessagePage:" + userId);
        return CommonResult.success("查找成功！", messagePageRes);
    }

    @Override
    public CommonResult<EnterChatRes> enterChat(Integer talkerOpenId) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }
        WebSocketUtil senderUtil = findReceiver(userId);
        if (senderUtil == null) return CommonResult.fail("ws连接未建立！");
        senderUtil.setInChatView(true);
        String listCacheName = getChatHistoryCacheName(talkerOpenId, userId);
        //默认给出最近的20条聊天记录
        List<PrivateChatLog> privateChatLogList = redisUtil.listRange(listCacheName, 0, 20);
        List<ChatHistoryData> chatHistoryList = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (PrivateChatLog privateChatLog : privateChatLogList) {
            ChatHistoryData chatHistoryData = new ChatHistoryData();
            chatHistoryData.setMessage(privateChatLog.getMessage());
            chatHistoryData.setMsgId(privateChatLog.getId());
//            chatHistoryData.setMsgType(privateChatLog.getMsgType());
//            if (privateChatLog.getMsgType() != 1)
//                chatHistoryData.setMessage(cacheService.getFileUrl(privateChatLog.getMessage()));
            chatHistoryData.setReceiverOpenId(privateChatLog.getReceiver());
            chatHistoryData.setSenderOpenId(privateChatLog.getSender());
            chatHistoryData.setSendTime(dateTimeFormatter.format(privateChatLog.getSendTime()));
            chatHistoryList.add(chatHistoryData);
        }
        EnterChatRes enterChatRes = new EnterChatRes();
        //排序，后发的消息在下
        chatHistoryList.sort(Comparator.comparingLong(ChatHistoryData::getMsgId));
        enterChatRes.setChatHistoryList(chatHistoryList);
        enterChatRes.setReceiverAvatar(userService.getAvatar(talkerOpenId));
        enterChatRes.setReceiverNickname(userService.getNickName(talkerOpenId));
        deleteUnreadMsgNum(userId, talkerOpenId);
        log.info("enterChatPage:" + userId);
        return CommonResult.success("查询成功！", enterChatRes);
    }

    @Override
    public CommonResult<ChatHistoryRes> getChatHistory(Integer talkerOpenId, Integer currentMsgId) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }
        String listCacheName = getChatHistoryCacheName(talkerOpenId, userId);
        long startIndex = redisUtil.getIndexOfLogId(listCacheName, currentMsgId);
        List<PrivateChatLog> privateChatLogList = new ArrayList<>();
        if (startIndex != -1) {
            privateChatLogList.addAll(redisUtil.listRange(listCacheName, startIndex + 1, startIndex + 21));
            if (privateChatLogList.size() < 20) {
                int difference = 20 - privateChatLogList.size();
                long id = privateChatLogList.size() == 0 ? currentMsgId : privateChatLogList.get(privateChatLogList.size() - 1).getId();
                QueryWrapper<PrivateChatLog> queryWrapper = new QueryWrapper<>();
                queryWrapper.and(wrapper -> wrapper
                                .eq("sender", userId).eq("receiver", talkerOpenId)
                                .or().eq("sender", talkerOpenId).eq("receiver", userId))
                        .lt("id", id)
                        .orderByDesc("id")
                        .last("LIMIT " + difference);
                privateChatLogList.addAll(privateChatLogDao.selectList(queryWrapper));
            }
        } else {
            QueryWrapper<PrivateChatLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(wrapper -> wrapper
                            .eq("sender", userId).eq("receiver", talkerOpenId)
                            .or().eq("sender", talkerOpenId).eq("receiver", userId))
                    .lt("id", currentMsgId)
                    .orderByDesc("id")
                    .last("LIMIT " + 20);
            privateChatLogList.addAll(privateChatLogDao.selectList(queryWrapper));
        }
        List<ChatHistoryData> chatHistoryList = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (PrivateChatLog privateChatLog : privateChatLogList) {
            ChatHistoryData chatHistoryData = new ChatHistoryData();
            chatHistoryData.setMessage(privateChatLog.getMessage());
            chatHistoryData.setMsgId(privateChatLog.getId());
//            chatHistoryData.setMsgType(privateChatLog.getMsgType());
//            if (privateChatLog.getMsgType() != 1)
//                chatHistoryData.setMessage(cacheService.getFileUrl(privateChatLog.getMessage()));
            chatHistoryData.setReceiverOpenId(privateChatLog.getReceiver());
            chatHistoryData.setSenderOpenId(privateChatLog.getSender());
            chatHistoryData.setSendTime(dateTimeFormatter.format(privateChatLog.getSendTime()));
            chatHistoryList.add(chatHistoryData);
        }
        ChatHistoryRes chatHistoryRes = new ChatHistoryRes();
        //排序，后发的消息在下
        chatHistoryList.sort(Comparator.comparingLong(ChatHistoryData::getMsgId));
        chatHistoryRes.setChatHistoryList(chatHistoryList);
        return CommonResult.success("查询成功！", chatHistoryRes);
    }

    @Override
    public CommonResult<BlankRes> quitChatPage() {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }
        WebSocketUtil senderUtil = findReceiver(userId);
        if (senderUtil == null) return CommonResult.fail("ws连接未建立！");
        senderUtil.setInChatView(false);
        log.info("quitChatPage:" + userId);
        return CommonResult.success("退出成功！");
    }

    @Cacheable(cacheNames = "unreadMsgNum", key = "#openId.concat('-').concat(#talkerOpenId)")
    public int getUnreadMsgNum(Integer openId, Integer talkerOpenId) {
        return 0;
    }
    @CacheEvict(cacheNames = "unreadMsgNum", key = "#openId.concat('-').concat(#talkerOpenId)")
    public void deleteUnreadMsgNum(Integer openId, Integer talkerOpenId) {
    }

    @CachePut(cacheNames = "unreadMsgNum", key = "#openId.concat('-').concat(#talkerOpenId)")
    public int addUnreadMsgNum(Integer openId, Integer talkerOpenId, int value) {
        return value + 1;
    }

}
