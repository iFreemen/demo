package com.example.demo.service.es;

/**
 * @ClassName BeerRepository
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/17 11:16
 * @Version V1.0
 **/

import com.example.demo.entity.ES.Beer;

import java.util.List;

/**
 * 	Beer:为实体类
 * 	Long:为Beer实体类中主键的数据类型
 */
public interface BeerRepository extends ElasticsearchRepositiry<Beer,Long> {

    /**
     * 按规律自定义方法，无需实现，ES自动实现-非常方便
     * @param price1
     * @param price2
     * @return
     */
    List<Beer> findByPriceBetween(double price1, double price2);
}

