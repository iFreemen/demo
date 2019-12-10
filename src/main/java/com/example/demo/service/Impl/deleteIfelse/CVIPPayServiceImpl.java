package com.example.demo.service.Impl.deleteIfelse;

import com.example.demo.entity.Buyer;
import com.example.demo.service.UserPayService;

/**
 * @ClassName CVIPPayServiceImpl
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/9 17:43
 * @Version V1.0
 **/
public class CVIPPayServiceImpl implements UserPayService {
    @Override
    public Double quote(Double orderPrice,Buyer buyer) {
        return orderPrice * 0.9;
    }
}
