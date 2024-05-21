package com.example.eat.decoderAndEncoder;

import com.example.eat.model.dto.param.chat.PrivateChatParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

/**
 * 为PrivateConversationParam反序列化
 * @author silentdragon
 * @date 2023-08-05 11:39:07
 */
public class PrivateChatParamDecoder implements Decoder.Text<PrivateChatParam> {
    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }

    /**
     * 反序列化失败返回null
     * @param s
     * @return {@code PrivateConversationParam}
     * @throws DecodeException
     */
    @Override
    public PrivateChatParam decode(String s) throws DecodeException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(s, PrivateChatParam.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }
}
