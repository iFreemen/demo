package com.example.demo;

import com.example.demo.entity.Buyer;
import com.example.demo.entity.ES.Beer;
import com.example.demo.entity.Staff;
import com.example.demo.mq.MQConsumer;
import com.example.demo.mq.MQProducer;
import com.example.demo.service.Impl.deleteIfelse.AVIPPayServiceImpl;
import com.example.demo.service.Impl.deleteIfelse.FactoryAndBean.CalPrice;
import com.example.demo.service.UserPayService;
import com.example.demo.service.es.BeerRepository;
import com.example.demo.utils.RedisUtil;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jms.Destination;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

@SpringBootTest
class DemoApplicationTests {


    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private RedisUtil redisUtil;

    @Value("${file.poi.path}")
    private String path;
    @Autowired
    private MQProducer producer;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    void contextLoads() {
    }

    /**
     * 测试redis
     *
     * @throws Exception
     */
    @Test
    public void testRedis() throws Exception {
        redisUtil.set("name", "Freemen", 10);
        for (int i = 0; i < 20; i++) {
            Thread.sleep(1000);
            System.out.println("当前循环次数:" + i + "获取的value:" + redisUtil.get("name"));
        }
    }

    /**
     * 读取配置文件的值
     */
    @Test
    public void testReadApplication() throws Exception {
        System.out.println(path);
    }

    @Test
    public void testNumberFormatException() {
        System.out.println("输出" + Integer.parseInt("29"));
    }


    /**
     * https://blog.csdn.net/lisheng19870305/article/details/103349316
     */
    @Test
    public void testOrderPrice() {
        AVIPPayServiceImpl avipPayService = new AVIPPayServiceImpl();
        Double quote = avipPayService.quote(300.0, new Buyer());
        System.out.println("A会员的最终价格" + quote);
    }

    @Test
    public void testOrderPrice2() throws Exception {
        CalPrice calPrice = new CalPrice();
        Buyer buyer = new Buyer();
        buyer.setName("Freemen1");
        buyer.setLevel("AVIP");
        String s = "2019-12-31 09:46:57";
        Date parse = format.parse(s);
        buyer.setInvalidDate(parse);
        Double aDouble = calPrice.calPrice(300.0, buyer);
        System.out.println("价格" + aDouble);
    }


    /**
     * 测试MQ
     */
    @Test
    public void testMq1() {
        /** 点对点发送 */
        Destination des = new ActiveMQQueue("myqueuees");
        int j = 4;
        for (int i = 0; i < j; i++) {
            producer.sendMessage(des, "hello ----- " + i);
        }
    }

    /**
     * 测试ES,创建索引
     */
    @Test
    public void createInddex(){
        boolean index = elasticsearchTemplate.createIndex(Beer.class);
        System.out.println("index:"+index);
    }

    @Autowired
    private BeerRepository beerRepository;

    /**
     * 1、新增单个文档
     * 2、没有就新增，有就修改-根据id
     */
    @Test
    public void insertES() {
        Beer beer = new Beer(2L, "小米手机8-2", "手机-2",
                "小米-2", 3492.00, "https://gss3.bdstatic.com/7Po3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D480/sign=35a4e8020bd79123e0e0957c9d345917/ae51f3deb48f8c546bca7d253d292df5e0fe7f9b.jpg");
        Beer save = beerRepository.save(beer);
        System.out.println("保存"+save.toString());
    }
    /**
     * @Description:定义批量新增方法
     */
    @Test
    public void insertList() {
        List<Beer> list = new ArrayList<Beer>();
        list.add(new Beer(3L, "坚果手机R1", " 手机", "锤子", 3699.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Beer(4L, "华为META10", " 手机", "华为", 4499.00, "http://image.baidu.com/13123.jpg"));
        // 接收对象集合，实现批量新增
        beerRepository.saveAll(list);
    }

    /**
     * 查询ES
     */
    @Test
    public void queryES(){
        Iterable<Beer> all = this.beerRepository.findAll();
        for (Beer beer : all) {
            System.out.println("数据"+beer);
        }
    }

    /**
     * @Description:定义查询方法,含对价格的降序、升序查询
     */
    @Test
    public void testQueryAll(){
        // 查找所有
        //Iterable<Beer> list = this.beerRepository.findAll();
        // 对某字段排序查找所有 Sort.by("price").descending() 降序
        // Sort.by("price").ascending():升序
        Iterable<Beer> list = this.beerRepository.findAll(Sort.by("price").ascending());

        for (Beer beer:list){
            System.out.println("数据："+beer);
        }
    }

    /**
     * @Description:按照价格区间查询
     * @Author: https://blog.csdn.net/chen_2890
     */
    @Test
    public void queryByPriceBetween(){
        List<Beer> list = this.beerRepository.findByPriceBetween(2000.00, 3500.00);
        for (Beer beer : list) {
            System.out.println("item = " + beer);
        }
    }

    /**
     * @Description:matchQuery底层采用的是词条匹配查询
     */
    @Test
    public void testMatchQuery(){
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米手机"));
        // 搜索，获取结果
        Page<Beer> items = this.beerRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.out.println("total = " + total);
        for (Beer item : items) {
            System.out.println(item);
        }
    }

    /**
     *
     * @Description:matchQuery
     */
    @Test
    public void testMathQuery(){
        // 创建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 在queryBuilder对象中自定义查询
        //matchQuery:底层就是使用的termQuery
        queryBuilder.withQuery(QueryBuilders.matchQuery("title","坚果"));
        //查询，search 默认就是分页查找
        Page<Beer> page = this.beerRepository.search(queryBuilder.build());
        //获取数据
        long totalElements = page.getTotalElements();
        System.out.println("获取的总条数:"+totalElements);

        for(Beer item:page){
            System.out.println(item);
        }


    }


    /**
     * @Description:
     * termQuery:功能更强大，除了匹配字符串以外，还可以匹配
     * int/long/double/float/....	
     * @Author: https://blog.csdn.net/chen_2890			
     */
    @Test
    public void testTermQuery(){
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.termQuery("price",998.0));
        // 查找
        Page<Beer> page = this.beerRepository.search(builder.build());

        for(Beer item:page){
            System.out.println(item);
        }
    }
    /**
     * @Description:布尔查询
     * @Author: https://blog.csdn.net/chen_2890			
     */
    @Test
    public void testBooleanQuery(){
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        builder.withQuery(
                QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("title","华为"))
                        .must(QueryBuilders.matchQuery("brand","华为"))
        );

        // 查找
        Page<Beer> page = this.beerRepository.search(builder.build());
        for(Beer item:page){
            System.out.println(item);
        }
    }

    /**
     * @Description:模糊查询
     * @Author: https://blog.csdn.net/chen_2890			
     */
    @Test
    public void testFuzzyQuery(){
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.fuzzyQuery("title","faceoooo"));
        Page<Beer> page = this.beerRepository.search(builder.build());
        for(Beer item:page){
            System.out.println(item);
        }

    }

}
