package com.bx.bxaiagent.chatmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.objenesis.strategy.StdInstantiatorStrategy;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

public class FileChatMemory implements ChatMemory {

  private static final Kryo kryo = new Kryo();

  private static final String BASE_PATH = "chatmemory";

  static {
    kryo.setRegistrationRequired(false);
    // 标准策略
    kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
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
    return oldMessages.stream().skip(Math.max(0, oldMessages.size() - lastN)).collect(Collectors.toList());
  }

  @Override
  public void clear(String conversationId) {
    File file = getFileByConversationId(conversationId);
    if (file.exists()) {
      file.delete();
    }
  }

  // kryo 保存和读取文件
  public void save(String conversationId, List<Message> messages) {

    File file = getFileByConversationId(conversationId);
    try (Output output = new Output(new FileOutputStream(file))) {
      kryo.writeObject(output, messages);
    } catch (IOException e) {
      throw new RuntimeException("保存聊天记录失败", e);
    }
  }

  @SuppressWarnings("unchecked")
  public List<Message> load(String conversationId) {
    File file = getFileByConversationId(conversationId);
    List<Message> messages = new ArrayList<>();
    if (file.exists()) {
      try (Input input = new Input(new FileInputStream(file))) {
        messages = kryo.readObject(input, List.class);
      } catch (IOException e) {
        throw new RuntimeException("读取聊天记录失败", e);
      }
    }
    return messages;
  }

  public File getFileByConversationId(String conversationId) {
    return new File(BASE_PATH, conversationId + ".kryo");
  }

}
