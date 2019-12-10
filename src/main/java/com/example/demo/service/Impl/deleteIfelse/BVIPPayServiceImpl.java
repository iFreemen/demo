package com.example.demo.service.Impl.deleteIfelse;

import com.example.demo.entity.Buyer;
import com.example.demo.service.UserPayService;

/**
 * @ClassName BVIPPayServiceImpl
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/9 17:42
 * @Version V1.0
 **/
public class BVIPPayServiceImpl implements UserPayService {
    @Override
    public Double quote(Double orderPrice,Buyer buyer)  {
        return orderPrice * 0.8;
    }
}
