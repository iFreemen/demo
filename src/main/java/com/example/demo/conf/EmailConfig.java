package com.example.demo.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @ClassName EmailConfig
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/3 17:01
 * @Version V1.0
 **/
@Data
@Configuration
@Component
public class EmailConfig {
    @Value("${spring.mail.username}")
    private String emailFrom;
}
