package com.bx.bxaiagent.chatclients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import com.bx.bxaiagent.chatmemory.RedisChatMemory;

import cn.hutool.json.JSONObject;
import dev.langchain4j.model.input.PromptTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;

@Component
public class DashboardChatClient {

  String systemPrompt = "你是一个AI助手,请根据用户的问题给出回答。";
  private final ChatClient dashscopeChatClient; 

  public DashboardChatClient(@Qualifier("dashscopeChatModel") ChatModel dashScopeChatModel,RedisChatMemory redisChatMemory) {
      
      dashscopeChatClient = ChatClient.builder(dashScopeChatModel)
         .defaultSystem(systemPrompt)
         .defaultAdvisors(new MessageChatMemoryAdvisor(redisChatMemory))
         .build();  
  }

  public JSONObject doChat(String message, String id) {

     // 创建包含变量的提示词模板
     String templateText = "{{systemPrompt}}生成的回答请使用Json格式返回,格式为: {\"answer\": \"你的回答\"}";
     PromptTemplate promptTemplate = PromptTemplate.from(templateText);
     
     // 设置变量
     Map<String, Object> variables = new HashMap<>();
     variables.put("systemPrompt", systemPrompt);
     
     // 使用变量渲染模板
     String formattedSystemPrompt = promptTemplate.apply(variables).text();
     
     // 使用Spring AI的prompt API直接传入系统提示
     JSONObject result = dashscopeChatClient.prompt()
         .system(formattedSystemPrompt)
         .user(message)
         .advisors(spec->spec.param("CHAT_MEMORY_CONVERSATION_ID_KEY",id)
         .param("CHAT_MEMORY_RETRIEVER_SIZE_KEY",10))
         .call()
         .entity(JSONObject.class);
        
    return result;
  }


  
}
