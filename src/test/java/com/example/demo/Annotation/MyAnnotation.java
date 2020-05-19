package com.example.demo.Annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @ClassName MyAnnotation
 * @Description: TODO
 * @Author Freemen
 * @Time 2020/4/20 11:15
 * @Version V1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    // 定义变量
    public String name() default "我是默认名字，也可以不加";

    public int age();

    // 定义接收数组
    public String[]  like();

    // 定义枚举类型变量，限定取值范围
    public CatColor color();

}
