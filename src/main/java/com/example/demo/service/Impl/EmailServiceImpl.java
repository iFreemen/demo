package com.example.demo.service.Impl;

import com.example.demo.conf.EmailConfig;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @ClassName EmailServiceImpl
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/3 17:04
 * @Version V1.0
 **/
@Service("emailService")
public class EmailServiceImpl implements EmailService {
    @Autowired
    private EmailConfig emailConfig;
    /**
     * 依赖了jar包就有了
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 简单邮件发送
     */
    @Override
    public void sendSimpleEmail(String sendTo, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailConfig.getEmailFrom());
        message.setTo(sendTo);
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
    }

    @Override
    public void sendAttachmentMail(String sendTo, String title, String content, File file){
        // 邮件对象
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(emailConfig.getEmailFrom());
            helper.setTo(sendTo);
            helper.setSubject(title);
            helper.setText(content);
            // 处理文件
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            helper.addAttachment("我是附件", fileSystemResource);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(msg);
    }
}
