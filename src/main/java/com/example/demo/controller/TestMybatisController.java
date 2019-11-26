package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.UserInfo;
import com.example.demo.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName TestMybatisController
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/11/26 16:23
 * @Version V1.0
 **/
@Controller
public class TestMybatisController {
    private final static Logger logger = LoggerFactory.getLogger(TestMybatisController.class);

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 增
     * @return
     */
    @GetMapping("insertUserInfo")
    public String insertUserInfo(){
        UserInfo userInfo = new UserInfo();
        double d = Math.random()*100;
        userInfo.setPassword(String.valueOf(d));
        userInfo.setRealName("hhh");
        userInfo.setUserName("Freemen");

        UserInfo userInfo1 = userInfoService.insertUserInfo(userInfo);
        logger.info("已插入数据库并返回含有主键的对象:{}",JSON.toJSONString(userInfo1));
        return "success";
    }

    /**
     * 删
     * @param id
     * @return
     */
    @GetMapping("deleteUserInfo/{id}")
    public String deleteUserInfo(@PathVariable int id) {
        int i = userInfoService.deleteUserInfo(id);
        logger.info("删除影响的行数:{}", i);
        return "success";
    }

    /**
     * 改
     * @param password
     * @return
     */
    @GetMapping("updatetUserInfo/{id}/{password}")
    public String updatetUserInfo(@PathVariable int id,@PathVariable String password){
        UserInfo userInfo1 = new UserInfo();
        userInfo1.setId(id);
        userInfo1.setPassword(password);
        UserInfo userInfo = userInfoService.updateByPrimaryKeySelective(userInfo1);

        logger.info("修改后的对象:{}",JSON.toJSONString(userInfo));
        return "success";
    }

    /**
     * 查
     * @param id
     * @return
     */
    @GetMapping("selectUserInfo/{id}")
    public String selectUserInfo(@PathVariable int id){
        UserInfo userInfo = userInfoService.selectUserInfo(id);

        logger.info("查询到的数据对象:{}",JSON.toJSONString(userInfo));
        return "success";
    }




}
