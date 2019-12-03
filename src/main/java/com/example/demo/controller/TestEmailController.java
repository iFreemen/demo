package com.example.demo.controller;

import com.example.demo.service.Impl.EmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;

/**
 * @ClassName testEmailController
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/3 17:09
 * @Version V1.0
 **/
@Controller
public class TestEmailController {
    private final static Logger logger = LoggerFactory.getLogger(TestEmailController.class);


    @Autowired
    private EmailServiceImpl emailService;

    @Value("${file.poi.path}")
    private String path;

    @GetMapping("testSendEmail")
    public String testSendEmail(){
        String sendTo = "ifreemen@qq.com";
        String title = "主题";
        String content = "内容，有时候是文章或日志，很长很长。。。。";
        emailService.sendSimpleEmail(sendTo,title,content);
        return "success";
    }

    @GetMapping("sendAttachmentMail")
    public String sendAttachmentMail() {
        String sendTo = "903944126@qq.com";
        String title = "主题";
        String content = "内容，有时候是文章或日志，很长很长。。。。";
        String readFile = "read.xlsx";
        String filePath = path + readFile;
        emailService.sendAttachmentMail(sendTo,title,content,new File(filePath));
        return "success";
    }
}
