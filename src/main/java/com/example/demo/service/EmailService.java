package com.example.demo.service;

import java.io.File;

/**
 * @ClassName EmailService
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/3 17:05
 * @Version V1.0
 **/
public interface EmailService {
    public void sendSimpleEmail(String sendTo, String title, String content);

    public void sendAttachmentMail(String sendTo, String title, String content, File file);

}
