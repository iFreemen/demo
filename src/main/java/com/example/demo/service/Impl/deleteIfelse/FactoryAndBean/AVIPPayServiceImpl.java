package com.example.demo.service.Impl.deleteIfelse.FactoryAndBean;

import com.example.demo.entity.Buyer;
import com.example.demo.service.UserPayService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName AVIPPayServiceImpl
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/9 17:38
 * @Version V1.0
 **/
@Service
public class AVIPPayServiceImpl implements UserPayService ,InitializingBean {
    @Override
    public Double quote(Double orderPrice,Buyer buyer) {

        Calendar instance = Calendar.getInstance();
        Date timeNow = instance.getTime();
        instance.add(Calendar.DATE, -7);
        Date time_7 = instance.getTime();

        Date invalidDate = buyer.getInvalidDate();
        if(invalidDate.compareTo(timeNow) -1 == 0){  //   直接使用  getMillisOf 判断更方便
            if(orderPrice > 50){return orderPrice * 0.66;}
            else if(orderPrice > 30){
                return orderPrice * 0.68;
            }else{
                return orderPrice * 0.7;
            }
        }else if (invalidDate.compareTo(time_7) - 1 < 0 && invalidDate.compareTo(timeNow) - 1 == 0){
            // TODO 数据库获取当前的次数
            int i = 0;
            if(i == 0){
                // TODO 保存更新次数
                if(orderPrice > 50){return orderPrice * 0.66;}
                else if(orderPrice > 30){
                    return orderPrice * 0.68;
                }else{
                    return orderPrice * 0.7;
                }
            }else{
                return orderPrice * 0.7;
            }
        }else{
            return orderPrice * 0.8;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        UserPayServiceStrategyFactory.register("AVIP",this);
    }
}
