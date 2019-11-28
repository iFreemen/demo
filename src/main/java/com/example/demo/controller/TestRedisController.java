package com.example.demo.controller;

import com.example.demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName TestRedisController
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/11/28 14:54
 * @Version V1.0
 **/

@Controller
public class TestRedisController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("setRedis")
    public String setRedis() {
        redisUtil.set("name", "Freemen-redis", 10*60);
        return "success";
    }

    @GetMapping("getRedis")
    public String getRedis() {
        System.out.println("name:"+redisUtil.get("name"));
        return "success";
    }
}
