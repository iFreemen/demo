package com.example.demo.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @ClassName UserInfo
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/11/26 15:59
 * @Version V1.0
 **/
@Data
public class UserInfo {

    @Id   // 指定主键。不指定：会导致执行一些数据库操作时可能会有问题。
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 让对象插入数据库后能返回含有主键的对象
    private Integer id;
    private String userName;
    private String password;
    private String realName;
}
