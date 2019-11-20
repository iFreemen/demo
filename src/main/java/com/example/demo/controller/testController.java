package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.text.resources.cldr.ar.FormatData_ar_MA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @ClassName testController
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/11/19 11:49
 * @Version V1.0
 **/
@Controller
public class testController {

    /**
     * 后台传递单个元素至前端页面
     * @param model
     * @return
     */
    @GetMapping("/hello")
    public String hello(Model model){
        model.addAttribute("name", "Freemen");
        return "hellohtml";
    }


    /**
     * 1、传对象给后台,在html和js中使用
     * 2、导航栏
     * @param model
     * @return
     */
    @GetMapping("/show")
    public String show(Model model){

        Calendar cr = Calendar.getInstance();
        System.out.println("时间："+cr.getTime());


        User user = new User("Freemen",18);
        Date date = new Date();
        model.addAttribute("user",user );
        model.addAttribute("today",date );

        List<User> list = new ArrayList<>();
        User user1 = new User("tom",20,false);
        User user2 = new User("lily",12,true);
        User user3 = new User("bingbing",17,true);
        list.add(user1);
        list.add(user2);
        list.add(user3);
        model.addAttribute("list", list);

        return "userhtml";
    }

    /**
     * 接收页面传递的参数--同步
     * @param model
     * @param name
     * @return
     */
    @GetMapping("/hellohtml")
    public String hellohtml(Model model,@RequestParam String name){
        model.addAttribute("name", name);
        return "hellohtml";
    }

}
