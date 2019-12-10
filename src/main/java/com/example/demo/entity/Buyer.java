package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName Buyer
 * @Description: TODO  为了实现以下 策略模式和工厂模式一起解决 if-else过多的问题
 * 原文链接：https://www.toutiao.com/i6763487018673504771/
 * @Author Freemen
 * @Time 2019/12/4 17:10
 * @Version V1.0
 **/
/*
外卖平台上的某家店铺为了促销，设置了多种会员优惠，其中包含超级会员折扣 8 折、普通会员折扣 9 折和普通用户没有折扣三种。
希望用户在付款的时候，根据用户的会员等级，就可以知道用户符合哪种折扣策略，进而进行打折，计算出应付金额。
随着业务发展，新的需求要求专属会员要在店铺下单金额大于 30 元的时候才可以享受优惠。
接着，又有一个变态的需求，如果用户的超级会员已经到期了，并且到期时间在一周内，
那么就对用户的单笔订单按照超级会员进行折扣，并在收银台进行强提醒，引导用户再次开通会员，而且折扣只进行一次。
 */
@Data
public class Buyer implements Serializable {


    private static final long serialVersionUID = -4573075577546379085L;

    private String name;

    private String level; // 用户等级  A B C D

    private Date invalidDate; // 会员失效时间
}
