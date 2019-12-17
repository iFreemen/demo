package com.example.demo.mq;

/**
 * @ClassName MQConsumer
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/10 16:52
 * @Version V1.0
 **/

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 接收者
 */

@Component
public class MQConsumer {


    @JmsListener(destination = "myqueuees")
    public void receiveMsg(String text){
        System.out.println(text+"............");
    }
}
