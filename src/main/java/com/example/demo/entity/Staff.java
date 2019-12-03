package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName Staff
 * @Description: TODO  用于Excel,职员类
 * @Author Freemen
 * @Time 2019/11/28 17:45
 * @Version V1.0
 **/
@Data
public class Staff implements Serializable {

    private String name;

    private Integer age;

    private Double salary;

    private String idCard;

    private String birthday;
}
