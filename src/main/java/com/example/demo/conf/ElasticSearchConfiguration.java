package com.example.demo.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @ClassName ElasticSearchConfiguration
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/16 18:24
 * @Version V1.0
 **/
@Slf4j
@Component
public class ElasticSearchConfiguration implements InitializingBean {

    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("*****************es_config*************************");
        log.info("es.set.netty.runtime.available.processors:{}", System.getProperty("es.set.netty.runtime.available.processors"));
        log.info("***************************************************");
    }
}
