package com.bx.bxaiagent.demo.invoke;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.ai.chat.prompt.Prompt;
import jakarta.annotation.Resource;

@Component
public class Ollama implements CommandLineRunner {
    @Resource
    private OllamaChatModel ollamaChatModel;

    @Override
    public void run(String... args) throws Exception {
        try {
            ollamaChatModel.call(new Prompt("你是谁？"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}