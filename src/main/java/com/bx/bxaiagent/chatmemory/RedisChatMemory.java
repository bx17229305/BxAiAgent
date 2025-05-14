package com.bx.bxaiagent.chatmemory;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import jakarta.annotation.Resource;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisChatMemory implements ChatMemory {
  @Resource 
  private RedisTemplate<String, Object> redisTemplate;
  
  private static final String KEY_PREFIX = "chatmemory:";

  private final ObjectMapper objectMapper;

  public RedisChatMemory() {
    this.objectMapper = new ObjectMapper();
    // 配置类型信息处理
    this.objectMapper.activateDefaultTyping(
      LaissezFaireSubTypeValidator.instance,
      ObjectMapper.DefaultTyping.NON_FINAL,
      JsonTypeInfo.As.PROPERTY
    );
    // 注册 Message 接口的实现类
    this.objectMapper.registerSubtypes(
      UserMessage.class,
      AssistantMessage.class,
      SystemMessage.class
      
    );
  }

  @Override
  public void add(String conversationId, List<Message> messages) {
    List<Message> oldMessages = load(conversationId);
    oldMessages.addAll(messages);
    save(conversationId, oldMessages);
  }

  @Override
  public List<Message> get(String conversationId, int lastN) {
    List<Message> oldMessages = load(conversationId);
    return oldMessages.stream().skip(Math.max(0, oldMessages.size() - lastN)).toList();
  }

  @Override
  public void clear(String conversationId) {
    redisTemplate.delete(KEY_PREFIX + conversationId);
  }

  public void save(String conversationId, List<Message> messages) {
    try {
      String key = KEY_PREFIX + conversationId;
      String json = objectMapper.writeValueAsString(messages);
      redisTemplate.opsForValue().set(key, json);
    } catch (Exception e) {
      throw new RuntimeException("保存消息失败", e);
    }
  }

  public List<Message> load(String conversationId) {
    try {
      String key = KEY_PREFIX + conversationId;
      Object obj = redisTemplate.opsForValue().get(key);
      if (obj instanceof List) {

         JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class,Message.class);
         return objectMapper.convertValue(obj, javaType);
      }
      return new ArrayList<>();
    } catch (Exception e) {
      throw new RuntimeException("加载消息失败", e);
    }
  }
}
