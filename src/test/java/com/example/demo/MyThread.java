package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName MyThread
 * @Description: TODO
 * @Author Freemen
 * @Time 2020/3/20 15:27
 * @Version V1.0
 **/
public class MyThread extends Thread {

    private Logger logger = LoggerFactory.getLogger(MyThread.class);

    private String name;

    public MyThread(String name){
        this.name = name;
    }

    @Override
    public void run() {
//        super.run();
        for (int i = 0;i < 5; ++i){

            try {
                sleep((int) Math.random() * 10);
                logger.info("{},当前线程是:{},i:{}",name,Thread.currentThread().getName(),i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
