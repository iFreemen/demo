package com.example.demo.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import javax.jms.Destination;   // 注意是这个包

/**
 * @ClassName MQproducer
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/10 16:38
 * @Version V1.0
 **/
// @Bean 跟@Compent 有什么区别？
@Component
public class MQProducer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /** 发送消息 */
    public void sendMessage(Destination des, String message){
        jmsMessagingTemplate.convertAndSend(des,message);
    }
}
