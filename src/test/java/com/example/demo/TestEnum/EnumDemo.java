package com.example.demo.TestEnum;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @ClassName EnumDemo
 * @Description: TODO
 * @Author Freemen
 * @Time 2020/4/20 10:19
 * @Version V1.0
 **/
public class EnumDemo {

    private static final int RED = 0x1;
    private static final int Green = 0x2;
    private static final int BLUE = 0x3;

    public int color;

    @Test
    public void test1(){
        color = RED;
        color = 4;
    }


    public Color colorEnum;
    @Test
    public void testEnum(){
        colorEnum = Color.RED;
        colorEnum = Color.GREEN;
        colorEnum = Color.BLUE;

        System.out.println(colorEnum);  // BLUE  字符串
        System.out.println(colorEnum.name()); // BLUE  字符串
        System.out.println(colorEnum.ordinal()); //  序号
        System.out.println(colorEnum.toString()); // BLUE  字符串 public String toString() {return name; }

        // 枚举转成数组
        Color[] values = Color.values();
        System.out.println(Arrays.toString(values));
    }


    /**
     * 单例设计模式：利用枚举的只有一个元素时，就是只有一个对象
     */
    @Test
    public void testSingleTon(){
        SingleTon.SINGLETON.method();
    }

}
