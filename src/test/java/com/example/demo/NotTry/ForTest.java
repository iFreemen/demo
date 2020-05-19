package com.example.demo.NotTry;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName ForTest
 * @Description: TODO
 * @Author Freemen
 * @Time 2020/4/30 16:07
 * @Version V1.0
 **/
public class ForTest {

    @RequestMapping("eatChicken")
    public Result eatChicken() {
        String 马化腾 = null;
        if(马化腾.equals("码云")){
            System.out.println("一起搞基");
        }
        return Result.ok();
    }
}
