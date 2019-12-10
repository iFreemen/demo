package com.example.demo.service.Impl.deleteIfelse;

import com.example.demo.entity.Buyer;
import com.example.demo.service.UserPayService;

import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName AVIPPayServiceImpl
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/9 17:38
 * @Version V1.0
 **/
public class AVIPPayServiceImpl implements UserPayService {
    @Override
    public Double quote(Double orderPrice,Buyer buyer) {
        Calendar instance = Calendar.getInstance();
        Date timeNow = instance.getTime();
        instance.add(Calendar.DATE, -7);
        Date time_7 = instance.getTime();

        instance.set(Calendar.DATE, 1);
        buyer.setInvalidDate(instance.getTime());

        Date invalidDate = buyer.getInvalidDate();
        if(invalidDate.compareTo(timeNow) -1 > 0){  //
            if(orderPrice > 50){return orderPrice * 0.66;}
            else if(orderPrice > 30){
                return orderPrice * 0.68;
            }else{
                return orderPrice * 0.7;
            }
        }else if (invalidDate.compareTo(time_7) - 1 > 0 && invalidDate.compareTo(timeNow) - 1 < 0){
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
}
