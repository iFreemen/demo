package com.example.demo.service;

import com.example.demo.dao.UserInfoMapper;
import com.example.demo.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserInfoService
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/11/26 16:03
 * @Version V1.0
 **/
@Service
public class UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfo insertUserInfo(UserInfo userInfo){
        int i = userInfoMapper.insertSelective(userInfo);
        return userInfo;
    }

    public int deleteUserInfo(int id){
        return userInfoMapper.deleteByPrimaryKey(id);
    }

    public UserInfo updateByPrimaryKeySelective(UserInfo userInfo){
        int i = userInfoMapper.updateByPrimaryKeySelective(userInfo);
        return userInfo;
    }

    public UserInfo selectUserInfo(int id){
        return userInfoMapper.selectByPrimaryKey(id);
    }

}
