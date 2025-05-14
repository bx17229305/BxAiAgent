package com.bx.bxaiagent.redisTest;

import com.bx.bxaiagent.model.entity.User;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.bx.bxaiagent.model.entity.SchoolUser;
import java.util.List;
import java.util.ArrayList;

@SpringBootTest
public class RedisTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Test
  public void testRedis() {
    List<User>users = new ArrayList<>();
    users.add(new SchoolUser("1", "张三", "123456"));
    users.add(new SchoolUser("2", "李四", "123456"));
    users.add(new SchoolUser("3", "王五", "123456"));
    
    // 清除之前的数据
    redisTemplate.delete("users");

    // users to jsonarray
    JSONArray jsonArray = new JSONArray();
    for (User user : users) {
      jsonArray.add(JSONUtil.parseObj(user));
    }
    
    // 存储到Redis
    redisTemplate.opsForValue().set("users", jsonArray.toString());

    // 获取并打印结果
    String result = redisTemplate.opsForValue().get("users");
    List<User>ans = new ArrayList<>();
    JSONArray resultArray = JSONUtil.parseArray(result);
    for (Object obj : resultArray) {
      SchoolUser user = JSONUtil.toBean(obj.toString(), SchoolUser.class);
      ans.add(user);
    }
    System.out.println("用户: " + ans);
  }
}
