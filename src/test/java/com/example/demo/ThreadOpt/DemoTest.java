package com.example.demo.ThreadOpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @ClassName DemoTest
 * @Description: TODO
 * @Author Freemen
 * @Time 2020/4/14 16:05
 * @Version V1.0
 **/
@SpringBootTest
public class DemoTest {

    @Test
    public void testSys(){
    }

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<多线程-start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @Test
    public void test(){

        int i = 0;
        i = i++;
        int j = 0;
        j = ++j;

        System.out.println("打印i:"+i);
        System.out.println("打印j:"+j);
    }

    private  int i = 0;
    @Test
    public void testIncrement1(){

        AtomicInteger atomicInteger = new AtomicInteger(i);
        Thread thread0 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 10000; j++) {
                    int i = atomicInteger.incrementAndGet();
                }
            }
        });

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 10000; j++) {
                    atomicInteger.incrementAndGet();
                }
            }
        });

        thread0.start();
        thread1.start();
        try {
            thread1.join();
            thread0.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("打印"+atomicInteger.get());
    }

    @Test
    public void testIncrement0(){

        Thread thread0 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 10000; j++) {
                    i = ++i;
                }
            }
        });

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 10000; j++) {
                    i = ++i;
                }
            }
        });

        thread0.start();
        thread1.start();
        try {
            thread1.join();
            thread0.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(i);
    }




    /**
     * Copyright © 2018 五月工作室. All rights reserved.
     *
     * @Project: tools
     * @ClassName: AtomicReferenceDemo
     * @Package: com.amos.tools.common.bean
     * @author: zhuqb
     * @Description: 主要用来展示AtomicReference使用方法
     * @date: 2019/9/11 0011 上午 9:46
     * @Version: V1.0
     *
     * CAS的多原子解决方案
     */
    public class AtomicReferenceDemo {

        private Reference reference;

        private AtomicReference<Reference> atomicReference;

        /**
         * 构建器中初始化AtomicReference
         *
         * @param reference
         */
        public AtomicReferenceDemo(Reference reference) {
            this.reference = reference;
            this.atomicReference = new AtomicReference<>(reference);
        }

        public void atomic(Reference reference) {
            Reference referenceOld;
            Reference referenceNew;

            long sequence;
            long timestamp;

            while (true) {
                referenceOld = this.atomicReference.get();
                sequence = referenceOld.getSequence();
                sequence++;
                timestamp = System.currentTimeMillis();

                referenceNew = new Reference(sequence, timestamp);
                /**
                 * 比较交换
                 */
                if (this.atomicReference.compareAndSet(referenceOld, referenceNew)) {
                    reference.setSequence(sequence);
                    reference.setTimestamp(timestamp);
                    break;
                }
            }
        }
    }

    /**
     * 业务场景模拟
     * 序列需要自增并且时间需要更新成最新的时间戳
     */
    @Data
    @AllArgsConstructor
    class Reference {
        /**
         * 序列
         */
        private long sequence;
        /**
         * 时间戳
         */
        private long timestamp;
    }


    /**
     * 解决 ABA问题
     */
        private static AtomicInteger atomicInt = new AtomicInteger(100);
        private static AtomicStampedReference atomicStampedRef = new AtomicStampedReference(
                100, 0);
        public static void main(String[] args) throws InterruptedException {
            Thread intT1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    atomicInt.compareAndSet(100, 101);
                    atomicInt.compareAndSet(101, 100);
                }
            });

            Thread intT2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                    }
                    boolean c3 = atomicInt.compareAndSet(100, 101);
                    System.out.println(c3); // true
                }
            });
            intT1.start();
            intT2.start();
            intT1.join();
            intT2.join();
            Thread refT1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {

                    }
                    atomicStampedRef.compareAndSet(100, 101,
                            atomicStampedRef.getStamp(),
                            atomicStampedRef.getStamp() + 1);

                    atomicStampedRef.compareAndSet(101, 100,
                            atomicStampedRef.getStamp(),
                            atomicStampedRef.getStamp() + 1);

                }
            });

            Thread refT2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    int stamp = atomicStampedRef.getStamp();
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                    }
                    boolean c3 = atomicStampedRef.compareAndSet(100, 101, stamp,
                            stamp + 1);
                    System.out.println(c3); // false
                }

            });
            refT1.start();
            refT2.start();
        }
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<多线程-end>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
