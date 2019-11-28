package com.example.demo.controller;

import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserInfoMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Iterator;
import java.util.List;

/**
 * @ClassName testMongoController
 * @Description: TODO  mongoDB
 * @Author Freemen
 * @Time 2019/11/28 15:50
 * @Version V1.0
 **/
@Controller
public class testMongoController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("testMongoSave")
    public String testMongoSave(){
        UserInfoMongo userInfoMongo = new UserInfoMongo();
        userInfoMongo.setPassword("password");
        userInfoMongo.setUserName("user_name");
        userInfoMongo.setRealName("real_name");
        UserInfoMongo userInfoMongo1 = mongoTemplate.save(userInfoMongo);
        System.out.println("保存:"+userInfoMongo1.toString());
        return "success";
    }

    /** mongodb取 */
    @GetMapping("/testMongoFind")
    public String MongoFind(){
        List<UserInfoMongo> list = mongoTemplate.findAll(UserInfoMongo.class);

        //
        // 普通for循环
        for( int i = 0 ; i < list.size() ; i++) {//内部不锁定，效率最高，但在多线程要考虑并发操作的问题。
            System.out.println(list.get(i));
        }


        // 迭代器遍历
        Iterator<UserInfoMongo> iter = list.iterator();
        while(iter.hasNext()){  //执行过程中会执行数据锁定，性能稍差，若在循环过程中要去掉某个元素只能调用iter.remove()方法。
            System.out.println(iter.next());
        }

        return "success";
    }
}
