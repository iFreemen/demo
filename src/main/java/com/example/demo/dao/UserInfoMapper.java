package com.example.demo.dao;

import com.example.demo.entity.UserInfo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName UserMapper
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/11/26 15:57
 * @Version V1.0
 **/
@Repository
public interface UserInfoMapper extends Mapper<UserInfo> {

}
