package com.bx.bxaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.prompt.Prompt;
@Component
public class Test2 implements CommandLineRunner{
    @Resource
    ChatModel dashscopeChatModel;
  
    @Override
    public void run(String... args) throws Exception {
       
      var result = dashscopeChatModel.call(new Prompt("你是谁？")).getResult().getOutput();
       System.out.println(result);


    }
}