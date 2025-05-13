package com.bx.bxaiagent.chatclients;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Component
public class DashboardChatClient {

  String systemPrompt = "你是一个AI助手,请根据用户的问题给出回答。";
  private final ChatClient dashscopeChatClient; 
  @Autowired
  public DashboardChatClient(@Qualifier("dashscopeChatModel") ChatModel dashScopeChatModel) {
      ChatMemory memory = new InMemoryChatMemory();
      dashscopeChatClient = ChatClient.builder(dashScopeChatModel)
         .defaultSystem(systemPrompt)
         .defaultAdvisors(new MessageChatMemoryAdvisor(memory))
         .build();  
  }

  public JSONObject doChat(String message, String id) {
    JSONObject result = dashscopeChatClient.prompt()
        .system(systemPrompt + "生成的回答请使用Json格式返回，格式为: {\"answer\": \"你的回答\"}")
        .user(message)
        .advisors(spec->spec.param("CHAT_MEMORY_CONVERSATION_ID_KEY",id)
        .param("CHAT_MEMORY_RETRIEVER_SIZE_KEY",10))
        .call()
        .entity(JSONObject.class);
        
    return result;
  }
}
