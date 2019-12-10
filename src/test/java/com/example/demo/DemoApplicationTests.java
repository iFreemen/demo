package com.example.demo;

import com.example.demo.entity.Buyer;
import com.example.demo.entity.Staff;
import com.example.demo.service.Impl.deleteIfelse.AVIPPayServiceImpl;
import com.example.demo.service.Impl.deleteIfelse.FactoryAndBean.CalPrice;
import com.example.demo.service.UserPayService;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;

@SpringBootTest
class DemoApplicationTests {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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


    /**
     * https://blog.csdn.net/lisheng19870305/article/details/103349316
     */
    @Test
    public void testOrderPrice(){
        AVIPPayServiceImpl avipPayService = new AVIPPayServiceImpl();
        Double quote = avipPayService.quote(300.0,new Buyer());
        System.out.println("A会员的最终价格"+quote);
    }

    @Test
    public void testOrderPrice2() throws Exception{
        CalPrice calPrice = new CalPrice();
        Buyer buyer = new Buyer();
        buyer.setName("Freemen1");
        buyer.setLevel("AVIP");
        String s = "2019-12-31 09:46:57";
        Date parse = format.parse(s);
        buyer.setInvalidDate(parse);
        Double aDouble = calPrice.calPrice(300.0, buyer);
        System.out.println("价格"+aDouble);
    }
}
