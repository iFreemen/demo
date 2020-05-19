package com.example.demo.Annotation;

import org.junit.jupiter.api.Test;

/**
 * @ClassName MyAnnotationDemo
 * @Description: TODO
 * @Author Freemen
 * @Time 2020/4/20 11:40
 * @Version V1.0
 **/
public class MyAnnotationDemo {

    /**
     * 反射处理注解
     */
    @Test
    public void test(){

        // 获取类上应用的指定注解
        Class<Cat> catClass = Cat.class;
        MyAnnotation myAnnotation = catClass.getAnnotation(MyAnnotation.class);

        // 获取注解上变量值 @MyAnnotation(name="catname",age=12,like = {"小黄鱼","泥鳅"},color = CatColor.RED )
        int age = myAnnotation.age();
        String name = myAnnotation.name();
        CatColor color = myAnnotation.color();
        String[] like = myAnnotation.like();

        try {
            Cat cat = catClass.newInstance();
            cat.setAge(age);
            cat.setColor(color);
            cat.setName(name);
            cat.setLike(like);

            System.out.println(cat);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }
}
