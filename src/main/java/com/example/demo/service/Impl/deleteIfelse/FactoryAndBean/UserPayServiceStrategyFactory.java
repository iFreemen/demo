package com.example.demo.service.Impl.deleteIfelse.FactoryAndBean;

/**
 * @ClassName UserPayServiceStrategyFactory
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/10 14:41
 * @Version V1.0
 **/

import com.example.demo.service.UserPayService;
import tk.mybatis.mapper.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用Spring和工厂模式解决
 * 接下来，我们就想办法调用register方法，把Spring通过IOC创建出来的Bean注册进去就行了。
 */
public class UserPayServiceStrategyFactory {
    private static Map<String,UserPayService> serviceMaps = new ConcurrentHashMap<>();

    public static UserPayService getUserType(String type){
        return serviceMaps.get(type);
    }

    public static void register(String userType,UserPayService userPayService){
        String s = Assert.notNull(userType, "userType can't be null");
        serviceMaps.put(userType, userPayService);
    }
}
