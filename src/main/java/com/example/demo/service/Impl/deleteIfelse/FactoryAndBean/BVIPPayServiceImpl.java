package com.example.demo.service.Impl.deleteIfelse.FactoryAndBean;

import com.example.demo.entity.Buyer;
import com.example.demo.service.UserPayService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @ClassName BVIPPayServiceImpl
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/9 17:42
 * @Version V1.0
 **/
@Service
public class BVIPPayServiceImpl implements UserPayService ,InitializingBean {
    @Override
    public Double quote(Double orderPrice,Buyer buyer) {
        return orderPrice * 0.8;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        UserPayServiceStrategyFactory.register("BVIP",this);
    }
}
