package com.example.eat.util;

public class ChatHistoryUtil {
    public static String getChatHistoryCacheName(Integer openId1,Integer openId2){
        String cacheName;
        if(openId1.compareTo(openId2)<=0){
            cacheName = openId1+"-"+openId2;
        }else cacheName = openId2+"-"+openId1;
        return "chat_history::"+cacheName;
    }
}
