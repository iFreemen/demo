package com.example.demo.service;

import com.example.demo.entity.Buyer;

/**
 * @ClassName UserPayService
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/9 17:30
 * @Version V1.0
 **/
public interface UserPayService {

    /**
     * 计算应付价格
     */

    public Double quote(Double orderPrice, Buyer buyer);
}
