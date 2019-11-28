package com.example.demo;

import com.example.demo.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    void contextLoads() {
    }

    /**
     * 测试redis
     * @throws Exception
     */
    @Test
    public void testRedis() throws Exception{
        redisUtil.set("name", "Freemen", 10);
        for (int i = 0 ; i < 20;i++){
            Thread.sleep(1000);
            System.out.println("当前循环次数:"+i+"获取的value:"+redisUtil.get("name"));
        }
    }
}
