package com.example.demo.entity;

import lombok.Data;
import org.bson.types.ObjectId;

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
public class UserInfoMongo {

    private ObjectId _id;
    private String userName;
    private String password;
    private String realName;
}
