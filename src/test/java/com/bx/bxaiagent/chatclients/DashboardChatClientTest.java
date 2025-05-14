package com.bx.bxaiagent.chatclients;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

@SpringBootTest


class DashboardChatClientTest {

    @Resource
    DashboardChatClient dashboardChatClient;

    @Test
    void doChat() {
        String id = UUID.randomUUID().toString();
        var res = dashboardChatClient.doChat("你好,给我几个学习编程的建议", id);
        System.out.println(res);
    }
}