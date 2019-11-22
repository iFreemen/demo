package com.example.demo.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @ClassName User
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/11/19 16:27
 * @Version V1.0
 **/
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -1369148652554782331L;

    private String name;
    private int age;
    private Boolean isStudent;
    private MultipartFile file;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public User() {
    }

    public User(String name, int age, boolean isStudent) {
        this.name = name;
        this.age = age;
        this.isStudent = isStudent;
    }

}
