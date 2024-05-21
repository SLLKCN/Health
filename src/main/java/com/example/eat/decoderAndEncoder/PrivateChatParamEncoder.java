package com.example.eat.decoderAndEncoder;

import com.example.eat.model.dto.param.chat.PrivateChatParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;

/**
 * 对PrivateConversationParam序列化
 *
 * @author silentdragon
 * @date 2023-08-05 11:35:57
 */
public class PrivateChatParamEncoder implements Encoder.Text<PrivateChatParam> {

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }

    /**
     * 发生异常返回null
     * @param param
     * @return {@code String}
     * @throws EncodeException
     */
    @Override
    public String encode(PrivateChatParam param) throws EncodeException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
