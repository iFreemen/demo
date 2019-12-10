package com.example.demo.controller;

import com.example.demo.entity.Buyer;
import org.springframework.stereotype.Controller;

import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName TestDeleteIfElse
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/4 17:15
 * @Version V1.0
 **/

/**
 * 这是If-else的原来处理的流程
 *
 * 外卖平台上的某家店铺为了促销，设置了多种会员优惠，其中包含超级会员折扣 8 折、普通会员折扣 9 折和普通用户没有折扣三种。
 * 希望用户在付款的时候，根据用户的会员等级，就可以知道用户符合哪种折扣策略，进而进行打折，计算出应付金额。
 * 随着业务发展，新的需求要求专属会员要在店铺下单金额大于 30 元的时候才可以享受优惠。
 * 接着，又有一个变态的需求，如果用户的超级会员已经到期了，并且到期时间在一周内，那么就对用户的单笔订单按照超级会员进行折扣，
 * 并在收银台进行强提醒，引导用户再次开通会员，而且折扣只进行一次。
 */
@Controller
public class TestDeleteIfElse {

    /**
     * 场景一：按照等级直接算金额,并且现在的会员是永久有效的
     * @param orderPrice
     * @param level
     * @return
     */
    private Double price1(Double orderPrice, String level) {
        if("A".equals(level)){return orderPrice * 0.7; }
        if("B".equals(level)){return orderPrice * 0.8; }
        if("C".equals(level)){return orderPrice * 0.9;}

        return orderPrice;
    }

    /**
     * 场景二：按照等级直接算金额,并且现在的会员是永久有效的,
     * 但是：A等级要在店铺下单金额大于 30 元的时候才可以68折，A：50元，66折
     * @param orderPrice
     * @param level
     * @return
     */
    private Double price2(Double orderPrice, String level) {
        if("A".equals(level)){
            if(orderPrice > 50){return orderPrice * 0.66;}
            else if(orderPrice > 30){
                return orderPrice * 0.68;
            }else{
                return orderPrice * 0.7;
            }
        }
        if("B".equals(level)){return orderPrice * 0.8; }
        if("C".equals(level)){return orderPrice * 0.9;}
        return orderPrice;
    }


    /**
     * 场景三：按照等级直接算金额,并且现在的会员是不是。。。。不是。。。。永久有效的,
     * 但是：A等级要在店铺下单金额大于 30 元的时候才可以68折，A：50元，66折
     * 需求又来了：   超级会员已经到期了，并且到期时间在一周内，
     * 那么就对用户的单笔订单按照超级会员进行折扣，并在收银台进行强提醒，
     * 引导用户再次开通会员，而且折扣只进行一次。
     *
     * @param orderPrice
     * @param buyer
     * @return
     */
    private Double price3(Double orderPrice, Buyer buyer) {   // 这里就看出传对象的好处了
        if("A".equals(buyer.getLevel())){     //  一堆if-else新增需求是往往只能在前面加，为了保证功能可行
            Calendar instance = Calendar.getInstance();
            Date timeNow = instance.getTime();
            instance.add(Calendar.DATE, -7);
            Date time_7 = instance.getTime();
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
            }
        }
        if("B".equals(buyer.getLevel())){return orderPrice * 0.8; }
        if("C".equals(buyer.getLevel())){return orderPrice * 0.9;}
        return orderPrice;
    }
}
