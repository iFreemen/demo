package com.example.demo;

import com.example.demo.entity.Staff;
import com.example.demo.utils.RedisUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private RedisUtil redisUtil;

    @Value("${file.poi.path}")
    private String path;

    @Test
    void contextLoads() {
    }

    /**
     * 测试redis
     * @throws Exception
     */
    @Test
    public void testRedis() throws Exception{
        redisUtil.set("name", "Freemen", 10);
        for (int i = 0 ; i < 20;i++){
            Thread.sleep(1000);
            System.out.println("当前循环次数:"+i+"获取的value:"+redisUtil.get("name"));
        }
    }

    /**
     * 读取配置文件的值
     */
    @Test
    public void testReadApplication() throws Exception{
        System.out.println(path);
    }

    @Test
    public void testNumberFormatException(){
        System.out.println("输出"+Integer.parseInt("29"));
    }


}
