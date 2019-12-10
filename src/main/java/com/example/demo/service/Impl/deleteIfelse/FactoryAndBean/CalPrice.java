package com.example.demo.service.Impl.deleteIfelse.FactoryAndBean;

import com.example.demo.entity.Buyer;
import com.example.demo.service.UserPayService;

/**
 * @ClassName calPrice
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/10 14:54
 * @Version V1.0
 **/
public class CalPrice {

    public Double calPrice(Double orderPrice,Buyer buyer){
        String level = buyer.getLevel();
        UserPayService userType = UserPayServiceStrategyFactory.getUserType(level);
        Double quote = userType.quote(orderPrice,buyer);
        return quote;
    }
}
