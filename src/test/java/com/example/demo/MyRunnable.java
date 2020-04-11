package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

/**
 * @ClassName MyRunnable
 * @Description: TODO
 * @Author Freemen
 * @Time 2020/3/20 17:13
 * @Version V1.0
 **/
public class MyRunnable implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MyRunnable.class);

    private String name;

    MyRunnable(String name){
        this.name = name;
    }

    @Override
    public void run() {

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
