package com.example.eat.dao.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eat.model.po.chat.PrivateChatLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PrivateChatLogDao extends BaseMapper<PrivateChatLog> {
    @Select("SELECT * FROM private_chat_log " +
            "WHERE (receiver = #{openId} OR sender = #{openId}) " +
            "AND id IN (SELECT MAX(id) FROM private_chat_log WHERE receiver = #{openId} OR sender = #{openId} GROUP BY receiver, sender) " +
            "ORDER BY id ")
    List<PrivateChatLog> findRecentChatLog(@Param("openId") Integer openId);
}
