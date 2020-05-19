package com.example.demo.Annotation;

import lombok.Data;

/**
 * @ClassName Cat
 * @Description: TODO
 * @Author Freemen
 * @Time 2020/4/20 11:18
 * @Version V1.0
 **/

@MyAnnotation(name="catname",age=12,like = {"小黄鱼","泥鳅"},color = CatColor.RED )
@Data
public class Cat {

    private String name;

    private int age;

    private String[] like;

    private CatColor color;




}
