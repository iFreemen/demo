package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Buyer;
import com.example.demo.entity.ES.Beer;
import com.example.demo.mq.MQProducer;
import com.example.demo.service.Impl.deleteIfelse.AVIPPayServiceImpl;
import com.example.demo.service.Impl.deleteIfelse.FactoryAndBean.CalPrice;
import com.example.demo.service.es.BeerRepository;
import com.example.demo.utils.RedisUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.sun.deploy.util.URLUtil;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.jms.Destination;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.Expression;
import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Objects;
import static org.apache.activemq.kaha.impl.index.hash.HashIndex.MAXIMUM_CAPACITY;

@SpringBootTest
class DemoApplicationTests {

    private Logger logger = LoggerFactory.getLogger(DemoApplicationTests.class);

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
    public void createInddex() {
        boolean index = elasticsearchTemplate.createIndex(Beer.class);
        System.out.println("index:" + index);
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
        System.out.println("保存" + save.toString());
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
    public void queryES() {
        Iterable<Beer> all = beerRepository.findAll();
        for (Beer beer : all) {
            System.out.println("数据" + beer);
        }
    }

    /**
     * @Description:定义查询方法,含对价格的降序、升序查询
     */
    @Test
    public void testQueryAll() {
        // 查找所有
        //Iterable<Beer> list = beerRepository.findAll();
        // 对某字段排序查找所有 Sort.by("price").descending() 降序
        // Sort.by("price").ascending():升序
        Iterable<Beer> list = beerRepository.findAll(Sort.by("price").ascending());

        for (Beer beer : list) {
            System.out.println("数据：" + beer);
        }
    }

    /**
     * @Description:按照价格区间查询
     * @Author: https://blog.csdn.net/chen_2890
     */
    @Test
    public void queryByPriceBetween() {
        List<Beer> list = beerRepository.findByPriceBetween(2000.00, 3500.00);
        for (Beer beer : list) {
            System.out.println("item = " + beer);
        }
    }

    /**
     * @Description:matchQuery底层采用的是词条匹配查询
     */
    @Test
    public void testMatchQuery() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米手机"));
        // 搜索，获取结果
        Page<Beer> items = beerRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.out.println("total = " + total);
        for (Beer item : items) {
            System.out.println(item);
        }
    }

    /**
     * @Description:matchQuery
     */
    @Test
    public void testMathQuery() {
        // 创建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 在queryBuilder对象中自定义查询
        //matchQuery:底层就是使用的termQuery
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "坚果"));
        //查询，search 默认就是分页查找
        Page<Beer> page = beerRepository.search(queryBuilder.build());
        //获取数据
        long totalElements = page.getTotalElements();
        System.out.println("获取的总条数:" + totalElements);

        for (Beer item : page) {
            System.out.println(item);
        }


    }


    /**
     * @Description: termQuery:功能更强大，除了匹配字符串以外，还可以匹配
     * int/long/double/float/....
     * @Author: https://blog.csdn.net/chen_2890
     */
    @Test
    public void testTermQuery() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.termQuery("price", 998.0));
        // 查找
        Page<Beer> page = beerRepository.search(builder.build());

        for (Beer item : page) {
            System.out.println(item);
        }
    }

    /**
     * @Description:布尔查询
     * @Author: https://blog.csdn.net/chen_2890
     */
    @Test
    public void testBooleanQuery() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        builder.withQuery(
                QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("title", "华为"))
                        .must(QueryBuilders.matchQuery("brand", "华为"))
        );

        // 查找
        Page<Beer> page = beerRepository.search(builder.build());
        for (Beer item : page) {
            System.out.println(item);
        }
    }

    /**
     * @Description:模糊查询
     * @Author: https://blog.csdn.net/chen_2890
     */
    @Test
    public void testFuzzyQuery() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.fuzzyQuery("title", "faceoooo"));
        Page<Beer> page = beerRepository.search(builder.build());
        for (Beer item : page) {
            System.out.println(item);
        }

    }


    //*******************************源码学习-start************************************//

    final static String getString() {
        return "a";
    }

    @Test
    public void testStringSource001() throws Exception {

        String a = getString();
        String a1 = a + "1";
        String a2 = "a1";
        System.out.println(a1 == a2);


        final String b1;
        b1 = "b";
        logger.info("final也能赋值，只是只能一次{}", b1);


        // 1、不可变性
        String s = "Hello";
        logger.error("String-s的初始化内存地址:{}", System.identityHashCode(s));
        s = "world";
        logger.info("String-s的被修改后内存地址:{}", System.identityHashCode(s));

        // 2、String 的一些方法

        // 2-1 首字母大小写
        String name = "freemen";
        String newName = name.substring(0, 1).toUpperCase() + name.substring(1);
        logger.info("首字母大写:{}", newName);

        // 2-2 判断相等
        String s1 = "good good study,day day up";
        String s2 = "good good study,day day up";
        boolean equals = s1.equals(s2);

        // 打印存储的内容
        char[] chars = s1.toCharArray();
        for (char aChar : chars) {
            logger.info("打印:{}", aChar);
        }

        // 打印存储内容对应的码
        byte[] bytes = s1.getBytes("utf8");
        for (byte aByte : bytes) {
            logger.info("打印2：{}", aByte);
        }

        // 判断相等--忽略到小写(这是一个意想不到的判断逻辑)
        String s3 = "good good study,day day up".toUpperCase();
        boolean b = s3.equalsIgnoreCase(s1);
        logger.info("忽略大小写是否相等:{}", b);

        // 3-1  替换：replace  删除：
        String s4 = "Freemen";
        logger.info("替换前：{}", s4);
        String replace = s4.replace('F', 'G');
        logger.info("替换后：{}", replace);
    }

    @Test
    public void testStringSource002() {

        // 3 拆分 split
        String s = "boo:and:foo";
        char[] chars = s.toCharArray();

        int length = chars.length;
        logger.info("长度{}", length);
        int length1 = s.length();
        logger.info("长度1:{}", length1);


        String[] split = s.split(":");
        for (String s1 : split) {
            logger.info("拆分函数split,只有第一个参数时:{}", s1);
        }
        List<String> strings = Arrays.asList(split);
        logger.info("Array转List:{}", JSON.toJSONString(strings));


        String[] split1 = s.split(":", 2);
        for (String s1 : split1) {
            logger.info("拆分函数split,有第两个参数时:{}", s1);
        }

        String a = ",a, ,  b  c ,";
        // Splitter 是 Guava 提供的 API
        List<String> list = Splitter.on(',')
                .trimResults()// 去掉空格
                .omitEmptyStrings()// 去掉空值
                .splitToList(a);
        logger.info("Guava 去掉空格的分割方法：{}", JSON.toJSONString(list));// 打印出的结果为：["a","b  c"]


        // 合并 join
        List<String> names = new ArrayList<>();
        names.add("1");
        names.add("2");
        names.add(null);
        names.add("4");
        logger.info("join合并后：{}", String.join("-", names));
        // 打印：join合并后：1-2-null-4

        // 合并：Guava
        // 依次 join 多个字符串，Joiner 是 Guava 提供的 API
        Joiner joiner = Joiner.on(",").skipNulls();
        String result = joiner.join("hello", null, "china");
        logger.info("依次 join 多个字符串:{}", result);

        List<String> list1 = Lists.newArrayList(new String[]{"hello", "china", null});
        logger.info("自动删除 list 中空值:{}", joiner.join(list1));
        // 输出的结果为；
        //依次 join 多个字符串:hello,china
        // 自动删除 list 中空值:hello,china
    }


    // Long 源码学习
    @Test
    public void testLongSource001() {
        // 2-1 缓存   -128到127
        Long aLong = -128L;
        Long bLong = 127L;

        long l = Long.parseLong("1");
        Long aLong1 = Long.valueOf("2");   //  valueOf比paseLong快，因为有使用Long的缓存。
    }


    // 关键字
    // 3-1 static
    public static List list = new ArrayList();  // 线程不安全
    public static List listSafe = new CopyOnWriteArrayList(); // 线程安全

    @Test
    public void testStaticKey() {
        list.add("线程不安全");
        list.add(1);
        logger.info("打印static定义的线程不安全的List：{}", JSON.toJSONString(list));

        listSafe.add("线程安全");
        listSafe.add(2);
        logger.info("打印static定义的线程安全的List：{}", JSON.toJSONString(listSafe));

    }

    //  4、Arrays 排序，查找、填充、拷贝、相等判断
    // 4-1排序
    @Test
    public void arraysSort() {
        String[] strings = {"300", "100", "01", "10"};
        Arrays.sort(strings);   // 可以有第二个参数，作为外部排序器
        logger.info("数组排序后：{}", JSON.toJSONString(strings));  // 双轴快速排序
    }

    // 4-2、二分查找法，必须先排序
    @Test
    public void arrayBinarySearch() {
        String[] strings = {"300", "100", "01", "10"};
        Arrays.sort(strings);
        int i = Arrays.binarySearch(strings, "100");
        logger.info("Arrays.binarySearch结果：{}", i);   // 查询不到返回的是负数，不一定是 -1

    }

    // 4-3拷贝
    @Test
    public void arrayCopy() {

        String[] s = new String[10];
        logger.info("长度：{}", s.length);
        logger.info("内容：{}", JSON.toJSONString(s));

        String[] strings = {"a", "b", "c"};
        String[] strings1 = strings;
        logger.info("String[]直接赋值:{}", JSON.toJSONString(strings1));  // 内存地址赋值

        ArrayList<String> strings2 = new ArrayList<>();
        strings2.add("d");
        strings2.add("e");
        strings2.add("f");
        ArrayList<String> strings3 = new ArrayList<>();
        strings3 = strings2;
        Object clone = strings2.clone();
        logger.info("ArrayList-clone后的{}", JSON.toJSONString(clone));
        logger.info("ArrayList[] 直接赋值的结果：{}", JSON.toJSONString(strings3));  //直接赋值的结果：["d","e","f"]
        strings2.remove(0);
        logger.info("ArrayList[] 修改原数组后的结果：{}", JSON.toJSONString(strings3));  // 修改原数组后的结果：["e","f"]
        // 说明直接赋值的，其实都是内存地址，除了8种基本数据类型
        logger.info("ArrayList-clone后再修改的{}", JSON.toJSONString(clone));   //  ArrayList 使用lone复制,可以转换；类型

        // 复制一个新的数组的方法：整个拷贝--copyOf ，部分拷贝--copyOfRange
        Arrays.copyOf(strings, 2);   // 注意：copyOf 只能复制 [] 类型的数组
        Arrays.copyOfRange(strings, 0, 2);  // 注意：copyOfRange 只能复制 [] 类型的数组
    }

    // Collections
    // 3.1 求集合中最大、小值
    @Test
    public void getCollectionsMax() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("500");
        arrayList.add("10");
        arrayList.add("400");
        arrayList.add("800");

        String max = Collections.max(arrayList);
        logger.info("Collections的max方法：{}", max);  //  800
    }

    // 3-2-1 线程安全的集合
    @Test
    public void safeCollection() {
        List<String> list = Collections.synchronizedList(new ArrayList<String>());
        Set<String> set = Collections.synchronizedSet(new HashSet<String>());
        Map<String, String> map = Collections.synchronizedMap(new HashMap<String, String>());
    }

    // 3-2-2 不可变集合
    @Test
    public void unmodifiableCollections() {

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("AAA");

        List<String> list = Collections.unmodifiableList(new ArrayList<String>());
        List<String> list1 = Collections.unmodifiableList(arrayList);
        Set<String> set = Collections.unmodifiableSet(new HashSet<String>());
        Map<String, String> map = Collections.unmodifiableMap(new HashMap<String, String>());

        logger.info("不可变集合：{}", JSON.toJSONString(arrayList.get(0)));
    }

    // 4 Object
    // 4-1 相等判断  equal()   deepEquals():判断数组
    @Test
    public void testEqual() {
        Object o1 = "123";
        Object o2 = "123";
        boolean b = o1.equals(o2);   // 底层：this == obj 判断内存地址
        logger.info("o1.equals(o2):{}", b); // true
        boolean equals = Objects.equals(o1, o2);  // (a == b) || (a != null && a.equals(b))
        logger.info("Objects.equals(o1, o2):{}", equals); // true

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("ABC");
        ArrayList<String> arrayList1 = new ArrayList<>();
        arrayList1.add("ABC");
        boolean b1 = Objects.deepEquals(arrayList, arrayList1);
        logger.info("Objects.deepEquals(arrayList, arrayList1):{}", b1); // true

        boolean aNull = Objects.isNull(01);  //  obj == null
        boolean b2 = Objects.nonNull(o1);  // obj != null
        Object o = Objects.requireNonNull(o2); // 要求不为null，null的话就报错：nullpointException，不是null，返回原对象
    }


    // 05 ArrayList
    @Test
    public void testArrayList() {

        // BUG
//        List<String> list = Arrays.asList("hello");
//        Object[] objects = list.toArray();
//        logger.info("object当前真实的类型：{}",objects.getClass().getSimpleName());
//        objects[0] = new Object();


        // add 扩容
        ArrayList<String> list = new ArrayList<>();
        list.add("abc");   // 空数组首次添加数据，扩容为10
        list.add("123");
        list.add("abc");
        list.add(null);
        logger.info("ArrayList#add:{}", JSON.toJSONString(list));

        // ArrayList 删除：索引删除、值删除、批量
        boolean abc = list.remove("abc");  // 删除的是一个
        logger.info("ArrayList#addremove:{}", JSON.toJSONString(list));


        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(2);
        integers.add(3);
        integers.add(1);

        Integer integer = integers.get(1);

        integers.remove(1);
        logger.info("ArrayList#remove:{}", JSON.toJSONString(integers));  // [2,1]  remove 此时用根据索引删除的。

        // 迭代器：hasNext()  next() remove()
        Iterator<Integer> iterator = integers.iterator();
        while (iterator.hasNext()) {
            logger.info("ArrayList#itertor#next,当前的值:{}", iterator.next());

            iterator.remove();  // 无返回值
            logger.info("ArrayList#itertor#remove,调用remove后打印当前的值:{}", iterator.next());
        }


        // 线程安全的ArrayList：Collections.synchronizedList
        List<Integer> integers1 = Collections.synchronizedList(integers);
        integers1.add(5);
        logger.info("线程安全的 Collections.synchronizedList：{}", JSON.toJSONString(integers1));
    }

    // LinkedList
    @Test
    public void testLinkedList() {
        LinkedList<String> linkedList = new LinkedList<>();

        // 默认链尾添加
        linkedList.add("b");
        linkedList.add("c");
        linkedList.add(null);

        // 链头添加
        linkedList.addFirst("a");  // 在链头添加
        logger.info("打印LinkedList：{}", JSON.toJSONString(linkedList));

        // 链头删除
        String first = linkedList.removeFirst();
        logger.info("被removeFirst删除的链头是：{}", first);
        logger.info("打印被删除链头后的LinkedList：{}", JSON.toJSONString(linkedList));

        // 节点查询
        // 是否存在
        boolean b = linkedList.contains("b");
        logger.info("LinkedList#contains，查询是否存在:{}", b);

        // 查询元素索引
        logger.info("链表内容：{}", JSON.toJSONString(linkedList)); // ["b","c",null]
        int i = linkedList.indexOf(null);
        linkedList.offer("ss");     //  offer()  就是 add()
        logger.info("查询的索引位置：{}", i);  // 2
        logger.info("offer后链表内容：{}", JSON.toJSONString(linkedList));  //offer后链表内容：["b","c",null,"ss"]

        // 链表截取
        ListIterator<String> stringListIterator = linkedList.listIterator(1);
        logger.info("listIterator后链表内容：{}", JSON.toJSONString(stringListIterator));  //offer后链表内容：["b","c",null,"ss"]
        String s = linkedList.get(3);  // 链表也能通过索引，快速获取值

        String s1 = linkedList.get(1);

    }


    // HashMap 16 0,75
    // 7-1 什么是HashMap
    @Test
    public void testHashMap() {
        int cap = 17;
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        int h = (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
        logger.info("扩容多少合适:{}", h);   // 大于等于原数值，并且这个数是2的N次方，至少16

        HashMap<String, String> map = new HashMap<>();
        map.put("abc", "ABC");
        map.put("cde", "CDE");
        logger.info("HashMap#put:{}", JSON.toJSONString(map));  // {"abc":"ABC","cde":"CDE"}

        map.remove("abc");
        logger.info("HashMap#remove:{}", JSON.toJSONString(map)); // {"cde":"CDE"}


        boolean cde = map.containsKey("cde");  // 是否存在key
        String cde1 = map.get("cde");   // 查找value
        map.size();
    }

    // TreeMap
    @Test
    public void testTreeMap() {
        HashMap<Integer, String> hmap = new HashMap<>();

        hmap.put(13, "Yellow");
        hmap.put(3, "Red");
        hmap.put(2, "Green");
        hmap.put(33, "Blue");
        System.out.println("key & values in hmap:");
        for (Map.Entry entry : hmap.entrySet()) {
            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
        }

        TreeMap<Integer, String> tmap = new TreeMap<>(hmap);
        System.out.println("key & values in tmap:");
        for (Map.Entry entry : tmap.entrySet()) {
            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
        }

        int size = 0;
        if (++size > 5) {

        }
        System.out.println("size:" + size);


        HashMap<Integer, String> hmap1 = new HashMap<>();
        String put1 = hmap1.put(0, "00");
        System.out.println("第一次打印:" + put1);
        String put2 = hmap1.put(0, "11");
        System.out.println("第二次打印:" + put2);  // 返回旧值
    }

    // ArrayList
    @Test
    public void testBatchInsert() {
        // 准备拷贝数据
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 3000; i++) {
            list.add(i);
            if (i < 10000) {
                integers.add(i);
            }
        }

        // for 循环 + add
        ArrayList<Integer> list2 = new ArrayList<>();
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            list2.add(list.get(i));
        }
        logger.info("单个 for 循环新增 300 w 个，耗时{}", System.currentTimeMillis() - start1);

        // 批量新增
        ArrayList<Integer> list3 = new ArrayList<>();
        long start2 = System.currentTimeMillis();
        list3.addAll(list);
        logger.info("批量新增 300 w 个，耗时{}", System.currentTimeMillis() - start2);

        ArrayList<Integer> list4 = new ArrayList<>(list);

        // 批量删除，效率低
        long start3 = System.currentTimeMillis();
        boolean b = list3.removeAll(integers);
        logger.info("删除是否成功：{}", b);
        logger.info("list3被删除后的长度：{}", list3.size());
        logger.info("批量删除 300 w 个，耗时{}", System.currentTimeMillis() - start3);

        // 迭代器删除，效率好
        long start4 = System.currentTimeMillis();
        List<Integer> integers1 = removeAll0(list4, integers);
        logger.info("批量删除 300 w 个，耗时{}", System.currentTimeMillis() - start4);


        // clear 方法
        logger.info("执行clear前数组长度：{}", list4.size());
        list4.clear();
        logger.info("执行clear后数组长度：{}", list4.size());
    }


    // Guava 运用工厂模式创建集合
    @Test
    public void guavaCreateArrayList() throws Exception {
        ArrayList<String> list = Lists.newArrayList();  // Lists 是 Guava的类

        // 这种方式命名是错误的
//        List<String> list0 = Lists.newArrayList(8);

        // 可以预估 list 的大小为 20
        List<String> list1 = Lists.newArrayListWithCapacity(20);
        // 不太肯定 list 大小是多少，但期望是大小是 20 上下。
        List<String> list2 = Lists.newArrayListWithExpectedSize(20);


        list.add("abc");
        ArrayList<String> list3 = Lists.newArrayList(list);  // list 不能为null，不然报  nullpointException  相当于   list.addAll()
        list.add("def");
        logger.info("Guava初始化打印：{}", JSON.toJSONString(list3));

        // 反转数组
        List<String> reverse = Lists.reverse(list);
        logger.info("list反转前：{}", JSON.toJSONString(list));
        logger.info("list反转后：{}", JSON.toJSONString(reverse));

        // 反转字符串
        String s = "32y4hh09485%&^*)(*噢诶入味";
        String s1 = reverseStringBuffer(s);
        logger.info("反转后：{}", s1);
        String s2 = reverseRecursive(s);
        logger.info("反转后：{}", s2);
        String s3 = charAtReverse(s);
        logger.info("反转后：{}", s3);

        // Guava 使用Lists对List分组
        List<List<String>> partition = Lists.partition(list, 2);
    }

    private List<Integer> removeAll0(List<Integer> src, List<Integer> target) {
        LinkedList<Integer> result = new LinkedList<>(src); //大集合用linkedlist
        HashSet<Integer> targetHash = new HashSet<>(target); //小集合用hashset
        Iterator<Integer> iter = result.iterator(); //采用Iterator迭代器进行数据的操作

        while (iter.hasNext()) {
            if (targetHash.contains(iter.next())) {
                iter.remove();
            }
        }
        return result;
    }


    // 字符串反转 利用 StringBuffer
    public static String reverseStringBuffer(String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        StringBuffer sb = new StringBuffer(s);
        return sb.reverse().toString();
    }

    // 字符串反转 利用二分法折半的思想
    public static String reverseRecursive(String s) {
        int length = s.length();
        if (length <= 1) {
            return s;
        }
        String left = s.substring(0, length / 2);
        String right = s.substring(length / 2, length);
        String afterReverse = reverseRecursive(right) + reverseRecursive(left);//此处是递归的方法调用
        return afterReverse;
    }

    // 循环拼接
    public static String charAtReverse(String s) {
        int length = s.length();
        String reverse = " ";
        for (int i = 0; i < length; i++) {
            reverse = s.charAt(i) + reverse;//字符串中获取单个字符的字符的放法
        }
        return reverse;
    }


    // 并发集合类
    // CopyOnWriteArrayList
    @Test
    public void testCopyOnWriteArrayList() {
        CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add("abc1");  // 在尾部添加元素
        copyOnWriteArrayList.add("abc2");  // 在尾部添加元素
        copyOnWriteArrayList.add("abc3");  // 在尾部添加元素

        // 指定位置添加
        copyOnWriteArrayList.add(3, "def");
        logger.info("CopyOnWriteArrayList指定位置的添加：{}", JSON.toJSONString(copyOnWriteArrayList));

        // 指定位置修改
        copyOnWriteArrayList.set(0,"修改");

        // 批量添加
//      copyOnWriteArrayList.addAll();

        // 删除操作
        copyOnWriteArrayList.remove(2);  // 加锁--》判断位置，不同策略的拷贝--》解锁

        // 批量删除
        ArrayList<String> list = new ArrayList<>();
        list.add("abc1");
        boolean b = copyOnWriteArrayList.removeAll(list);  // 如果原数组长度为0，返回只有false
        logger.info("CopyOnWriteArrayList#removeAll:{}", JSON.toJSONString(copyOnWriteArrayList));

        int i = copyOnWriteArrayList.indexOf(null);  // 查询在哪个索引，不存在返回 -1

        // 迭代器
        Iterator<String> iterator = copyOnWriteArrayList.iterator();
        copyOnWriteArrayList.add("new1");
        String next1 = iterator.next();
        copyOnWriteArrayList.add("new3");
    }


    // ConcurrentHashMap
    @Test
    public void testConcurrentHashMap(){
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("abc", "ABC");  // synchronized
        concurrentHashMap.put("abc", "ABC1");  // synchronized
        logger.info("ConcurrentHashMap：{}", JSON.toJSONString(concurrentHashMap));
    }

    /**
     * 队列
     */
    // 1、链表阻塞队列   LinkedBlockingQueue
    @Test
    public void testLinkedBlockingQueue(){
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(2);

        // 新增有多种方法
        // 1-1 add   ： 满了就抛异常
        // 1-2 offer ：超时就返回false
        // 1-3 put : 队列满了就一直阻塞
        try {
            queue.put("a");
            queue.put("b");   // 亲测，队列满后会一直堵塞
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("链表阻塞队列#put():{}",JSON.toJSONString(queue));

        // 阻塞删除,一次只能获取头部元素，如果是空队列时。take就会阻塞直到有值
        // 2-1 take:查看并删除
        try {
            String qtake0 = queue.take();
            logger.info("链表阻塞队列#take：{}",qtake0);

            String qtake1 = queue.take();
            logger.info("链表阻塞队列#take：{}",qtake1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2-2 peek查看不删除,当空是返回 null，不会阻塞
        String peek = queue.peek();
        logger.info("链表队列#peek：{}",peek);
    }

    //

    // LinkedList是Queue的实现类
    @Test
    public void testLinkedListImplQueue(){
        Queue<String> queue = new LinkedList<>();
        queue.add("a");
        logger.info("LinkedList实现Queue：{}",JSON.toJSONString(queue));
    }

    // 同步队列
    @Test
    public void testSynchronousQueue(){
        SynchronousQueue<String> sQueue = new SynchronousQueue<>();
        try {
            sQueue.put("A");

            String take = sQueue.take();

            int size = sQueue.size();  // 注意：同步队列的size是个坑爹的size，永远还有0

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数组阻塞队列
     * ArrayBlockingQueue
     * 必须指定大小
     */
    @Test
    public void testArrayBlockingQueue(){
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
        try {
            queue.put("abc");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 延时队列
     */
    @Test
    public void testDelayQueue(){
// 把 String 放到 DelayQueue 中是不行的，编译都无法通过，
// DelayQueue 类在定义的时候，是有泛型定义的，
// 泛型类型必须是 Delayed 接口的子类才行。
//        DelayQueue<String> strings = new DelayQueue<String>();

    }


    /**
     * 锁
     */
    @Test
    public void testAQS(){

    }

    //*******************************源码学习-end************************************//


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>多线程学习-start<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * join
     */
    @Test
    public void join() throws Exception {
        Thread main = Thread.currentThread();
        logger.info("{} is run。",main.getName());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("{} begin run",Thread.currentThread().getName());
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("{} end run",Thread.currentThread().getName());
            }
        });
        // 开一个子线程去执行
        thread.start();
        // 当前主线程等待子线程执行完成之后再执行
        thread.join();
        logger.info("{} is end", Thread.currentThread());
    }

    // 实现多线程的四种方法
    // 第一种: 实现Thread类，重写run()
    // 第二种: 继承 Runable接口，实现run()
    // 第三种: 使用Future Task 实现有返回结果的线程
    // 第四种: 线程池

    /**
     * 第一种: 实现Thread类，重写run()
     */
    @Test
    public void testMyThread() {
        Thread myThreadA = new MyThread("A");
        Thread myThreadB = new MyThread("B");
        myThreadA.start();
        myThreadB.start();
    }

    /**
     * 第二种: 继承 Runnable接口，实现run()
     */
    @Test
    public void testRunnable() {
        Runnable a = new MyRunnable("A");
        Runnable b = new MyRunnable("B");

        new Thread(a).start();
        new Thread(b).start();

        new Thread(a).run();   // 不用start,用run的话，就是主线程 mian 直接执行MyRunnable的run()。
    }

    /**
     * 第三种：FutureTask
     */
    @Test
    public void testMyFutureTask() {
        // 创建任务集合
        List<FutureTask<Integer>> taskList = new ArrayList<>();
        // 创建线程池
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            // 传入Callable 对象创建FutureTask对象,当然除了入参是Callable还可以是Runnable,底层使用适配器转为Callable
            FutureTask<Integer> ft = new FutureTask<>(new ComputeTask(i, "" + i));
            taskList.add(ft);
            // 提交给线程池执行任务，也可以通过exec.invokeAll(taskList)一次性提交所有任务;
            exec.submit(ft);
        }

        System.out.println("所有计算任务提交完毕, 主线程接着干其他事情！");

        // 开始统计各计算线程计算结果
        Integer totalResult = 0;
        for (FutureTask<Integer> ft : taskList) {
            try {
                //FutureTask的get方法会自动阻塞,直到获取计算结果为止
                totalResult = totalResult + ft.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 关闭线程池
        exec.shutdown();
        System.out.println("多任务计算后的总结果是:" + totalResult);
    }

    /**
     * 第四种：线程池
     */
    @Test
    public void testThreadPool(){
        // 这种不指定队列大小的线程，很容易导致超时
        ExecutorService executorService = Executors.newFixedThreadPool(10);  // 大小固定的线程池
        // submit是提交任务的意思
        // Thread.currentThread() 得到当前线程
        executorService.submit(() -> System.out.println(Thread.currentThread().getName() + " is run"));
    }

    /**
     * 只有一个线程的线程池
     */
    @Test
    public void testSingleThreadPoll(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();  // 只有一个线程的线程池
    }

    /**
     * CachedThreadPool
     */
    @Test
    public void testCachedThreadPool(){
        ExecutorService executorService = Executors.newCachedThreadPool();
    }

    /**
     * ScheduledThreadPool
     * 定时任务线程池
     */
    @Test
    public void testDelay(){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

    }


    /**
     * 线程池
     */
    public void testThreadPoolExecutor(){

    }


    /**
     * 测试没有使用任何安全机制下并发对增量的影响
     */
    private volatile int sycInt = 0;
    public void setSycInt(int sycInt) {
        this.sycInt = sycInt;
    }

    public int getSycInt() {
        return sycInt;
    }
    @Test
    public void testNonLock(){

        Thread thread0 = new Thread(()->{for (int i = 0;i < 100;i++){
            setSycInt(getSycInt() + 1);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }});

        Thread thread1 = new Thread(()->{for (int i = 0;i < 200;i++){
            setSycInt(getSycInt() + 1);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }});

        thread0.start();
        thread1.start();
        try {
            thread0.join();
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("没有使用安全机制的情况下:{}",getSycInt());
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>多线程学习-end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>设计模式-start<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>设计模式-end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    // 锁
    public class MyAQS extends AbstractQueuedSynchronizer{

    }




    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<合并图片-start>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * 把两张图片合并
     * @version 2018-2-27 上午11:12:09
     *
     */
    public static class Picture1
    {
        private Graphics2D g        = null;

        /**
         * 导入本地图片到缓冲区
         */
        public BufferedImage loadImageLocal(String imgName) {
            try {
                return ImageIO.read(new File(imgName));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        public BufferedImage modifyImagetogeter(BufferedImage b, BufferedImage d) {

            try {
                int w = b.getWidth();
                int h = b.getHeight();

                g = d.createGraphics();
                g.drawImage(b, 3000, -8000, w, h, null);
                g.dispose();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return d;
        }

        /**
         * 生成新图片到本地
         */
        public void writeImageLocal(String newImage, BufferedImage img) {
            if (newImage != null && img != null) {
                try {
                    File outputfile = new File(newImage);
                    ImageIO.write(img, "jpg", outputfile);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        @Test
        public  void test() {

            Picture1 tt = new Picture1();

            BufferedImage d = tt.loadImageLocal("C:\\Users\\Admin\\Desktop\\fj.jpg");
            BufferedImage b = tt.loadImageLocal("C:\\Users\\Admin\\Desktop\\hh.jpg");

            tt.writeImageLocal("C:\\Users\\Admin\\Desktop\\Fr.jpg", tt.modifyImagetogeter(b, d));
            //将多张图片合在一起
            System.out.println("success");
        }
    }
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<合并图片-end>>>>>>>>>>>>>>>>>>>>>>>>>>>



    @Test
    public void testtu(){
        try(
            FileInputStream fb=new FileInputStream("C:\\Users\\Admin\\Desktop\\hh.jpg");
            FileInputStream fa=new FileInputStream("C:\\Users\\Admin\\Desktop\\fj.jpg");
            FileOutputStream fout=new FileOutputStream("C:\\Users\\Admin\\Desktop\\Fr1.jpg");
            ByteArrayOutputStream memorystream=new ByteArrayOutputStream())
        {
            byte[] buff=new byte[1024];
            int len=-1;
            while((len=fa.read(buff))!=-1){
                memorystream.write(buff,0,len);
            }
            while (((len=fb.read(buff))!=-1)){
                memorystream.write(buff,0,len);
            }

            byte[] newdata =memorystream .toByteArray();
            fout.write(newdata);
            fout.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }




    /**
     * 待合并的两张图必须满足这样的前提，如果水平方向合并，则高度必须相等；如果是垂直方向合并，宽度必须相等。
     * mergeImage方法不做判断，自己判断。
     * @param img1 待合并的第一张图
     * @param img2 带合并的第二张图
     * @param isHorizontal 为true时表示水平方向合并，为false时表示垂直方向合并
     * @return 返回合并后的BufferedImage对象
     * @throws IOException
     */
    public static BufferedImage mergeImage(BufferedImage img1,
                                           BufferedImage img2, boolean isHorizontal) throws IOException {
        int w1 = img1.getWidth();
        int h1 = img1.getHeight();
        int w2 = img2.getWidth();
        int h2 = img2.getHeight();

        // 从图片中读取RGB
        int[] ImageArrayOne = new int[w1 * h1];
        ImageArrayOne = img1.getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
        int[] ImageArrayTwo = new int[w2 * h2];
        ImageArrayTwo = img2.getRGB(0, 0, w2, h2, ImageArrayTwo, 0, w2);

        // 生成新图片
        BufferedImage DestImage = null;
        if (isHorizontal) { // 水平方向合并
            DestImage = new BufferedImage(w1+w2, h1, BufferedImage.TYPE_INT_RGB);
            DestImage.setRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            DestImage.setRGB(w1, 0, w2, h2, ImageArrayTwo, 0, w2);
        } else { // 垂直方向合并
            DestImage = new BufferedImage(w1, h1 + h2, BufferedImage.TYPE_INT_RGB);
            DestImage.setRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            DestImage.setRGB(0, h1, w2, h2, ImageArrayTwo, 0, w2); // 设置下半部分的RGB
        }

        return DestImage;
    }

    /**
     * 远程图片转BufferedImage
     * @param destUrl  远程图片地址
     * @return
     */
    public static BufferedImage getBufferedImageDestUrl(String destUrl) {
        HttpURLConnection conn = null;
        BufferedImage image = null;
        try {
            URL url = new URL(destUrl);
            URLConnection conn1 = url.openConnection();
                image = ImageIO.read(conn1.getInputStream());
                return image;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            conn.disconnect();
        }
        return image;
    }

    /**
     * @param fileUrl
     *      文件绝对路径或相对路径
     * @return 读取到的缓存图像
     * @throws IOException
     *       路径错误或者不存在该文件时抛出IO异常
     */
    public static BufferedImage getBufferedImage(String fileUrl)
            throws IOException {
        File f = new File(fileUrl);
        return ImageIO.read(f);
    }

    /**
     * 输出图片
     *
     * @param buffImg
     *      图像拼接叠加之后的BufferedImage对象
     * @param savePath
     *      图像拼接叠加之后的保存路径
     */
    public static void generateSaveFile(BufferedImage buffImg, String savePath) {
        int temp = savePath.lastIndexOf(".") + 1;
        try {
            File outFile = new File(savePath);
            if(!outFile.exists()){
                outFile.createNewFile();
            }
            ImageIO.write(buffImg, savePath.substring(temp), outFile);


//            ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
//            ImageIO.write(buffImg, "jpg", baos);//写入流中
//            byte[] bytes = baos.toByteArray();//转换成字节
//            BASE64Encoder encoder = new BASE64Encoder();
//            String png_base64 =  encoder.encodeBuffer(bytes).trim();//转换成base64串
//            png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
//            System.out.println("BASE64:"+"data:image/jpg;base64,"+png_base64);

            System.out.println("ImageIO write...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Java 测试图片合并方法，pdf不行
     */
    @Test
    public void imageMargeTest() {
        // 读取待合并的文件
        BufferedImage bi1 = null;
        BufferedImage bi2 = null;
        // 调用mergeImage方法获得合并后的图像
        BufferedImage destImg = null;
        System.out.println("下面是垂直合并的情况：");
        String saveFilePath = "C:\\Users\\Admin\\Desktop\\fj.jpg";
        String divingPath = "C:\\Users\\Admin\\Desktop\\hh.jpg";
        String margeImagePath = "C:\\Users\\Admin\\Desktop\\margeNew2.jpg";
        try {
//            bi1 = getBufferedImage(saveFilePath);
            bi1 = getBufferedImageDestUrl("http://img.epbox.com.cn/images/oss_website/fvg1w6m2x1k33p41.jpg");
            bi2 = getBufferedImage("C:\\Users\\Admin\\Desktop\\ppd.pdf");
//            bi2 = getBufferedImageDestUrl("http://img.epbox.com.cn/images/oss_website/11acj7v7yrwf0shl.png");
            // 调用mergeImage方法获得合并后的图像
            destImg = mergeImage(bi1, bi2, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 保存图像
        generateSaveFile(destImg, margeImagePath);
        System.out.println("垂直合并完毕!");
    }



    //

    /**
     *  将PDF转换成base64编码
     *  1.使用BufferedInputStream和FileInputStream从File指定的文件中读取内容；
     *  2.然后建立写入到ByteArrayOutputStream底层输出流对象的缓冲输出流BufferedOutputStream
     *  3.底层输出流转换成字节数组，然后由BASE64Encoder的对象对流进行编码
     * */
    @Test
    public void getPDFBinary() {
        File file = new File("C:\\Users\\Admin\\Desktop\\ppd.pdf");
//        File file = new File("http://img.epbox.com.cn/images/oss_website/fvg1w6m2x1k33p41.jpg");
        FileInputStream fin = null;
        BufferedInputStream bin = null;
        ByteArrayOutputStream baos = null;
        BufferedOutputStream bout = null;
        try {
            // 建立读取文件的文件输出流
            fin = new FileInputStream(file);
            // 在文件输出流上安装节点流（更大效率读取）
            bin = new BufferedInputStream(fin);
            // 创建一个新的 byte 数组输出流，它具有指定大小的缓冲区容量
            baos = new ByteArrayOutputStream();
            // 创建一个新的缓冲输出流，以将数据写入指定的底层输出流
            bout = new BufferedOutputStream(baos);
            byte[] buffer = new byte[1024];
            int len = bin.read(buffer);
            while (len != -1) {
                bout.write(buffer, 0, len);
                len = bin.read(buffer);
            }
            // 刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
            bout.flush();
            byte[] bytes = baos.toByteArray();
            // sun公司的API

            String enCode = new BASE64Encoder().encodeBuffer(bytes).trim();

            System.out.println("pdfbase64:"+enCode);
            // apache公司的API
            // return Base64.encodeBase64String(bytes);
//            base64StringToPDF(enCode);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fin.close();
                bin.close();
                // 关闭 ByteArrayOutputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何 IOException
                // IOException
                // baos.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将base64编码转换成PDF
     *
     * @param base64sString
     *            1.使用BASE64Decoder对编码的字符串解码成字节数组
     *            2.使用底层输入流ByteArrayInputStream对象从字节数组中获取数据；
     *            3.建立从底层输入流中读取数据的BufferedInputStream缓冲输出流对象；
     *            4.使用BufferedOutputStream和FileOutputSteam输出数据到指定的文件中
     */
     void base64StringToPDF(String base64sString) {
        BufferedInputStream bin = null;
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {
            // 将base64编码的字符串解码成字节数组
            byte[] bytes = new sun.misc.BASE64Decoder().decodeBuffer(base64sString);
            // apache公司的API
            // byte[] bytes = Base64.decodeBase64(base64sString);
            // 创建一个将bytes作为其缓冲区的ByteArrayInputStream对象
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            // 创建从底层输入流中读取数据的缓冲输入流对象
            bin = new BufferedInputStream(bais);
            // 指定输出的文件
            File file = new File("C:\\Users\\Admin\\Desktop\\pdd002.pdf");
//            File file = new File("C:\\Users\\Admin\\Desktop\\fr3.jpg");
            // 创建到指定文件的输出流
            fout = new FileOutputStream(file);
            // 为文件输出流对接缓冲输出流对象
            bout = new BufferedOutputStream(fout);

            byte[] buffers = new byte[1024];
            int len = bin.read(buffers);
            while (len != -1) {
                bout.write(buffers, 0, len);
                len = bin.read(buffers);
            }
            // 刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
            bout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bin.close();
                fout.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 日期正则表达式判断（亲测有效）
    @Test
    public void testEXP(){
        String exp = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-" +
                "(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))" +
                " ((0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]))";
        Pattern pattern = Pattern.compile(exp);
        String text = "2020-04-01 00:01:59";

        Matcher matcher = pattern.matcher(text);
        boolean matches = matcher.matches();
        System.out.println("符合："+ matches);

    }

    @Test
    public void testBase64EXP(){
        String exp = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$"; // 仅适用于图片的判断，不适用pdf
        Pattern pattern = Pattern.compile(exp);
//        String text = "data:image/jpg;base64,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";
//        String text = "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";
//        String text = "data:image/png;base64,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";
        String text = "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";

        Matcher matcher = pattern.matcher(text);
        boolean matches = matcher.matches();
        System.out.println("符合："+ matches);
    }


    //////////////////////////////////////////////////////////////////
    public  byte[] getImageFromNetByUrl(String strUrl){
        try {
            URL url = new URL(strUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream inStream = urlConnection.getInputStream();//通过输入流获取图片数据
            byte[] btImg = readInputStream(inStream);//得到图片的二进制数据
            return btImg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //由于读取需要一定时间，所以不能单纯往字节数组里读，所以需要判断是否读完
    public  byte[] readInputStream(InputStream inStream) throws Exception{ //存放读取的所有的字节数组
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }


    /**
     * 获取远程图片BASE64，亲测有效
     */
    @Test
    public void getImageStr(){

        String url= "http://img.epbox.com.cn/images/oss_website/fvg1w6m2x1k33p41.jpg";
        String imageStr = getImageStr(url);
        System.out.println("打印Base64："+imageStr);
    }

    private  String getImageStr(String imgUrl) {
        byte[] data = null;
        try {
            data =getImageFromNetByUrl(imgUrl);;
        } catch (Exception e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }




    ///  本地全部pdf 转 图片

    /**
     * 本地pdf转成图片可以，试试远程pdf
     */
    @Test
    public void pdf2png() {
        String fileAddress = "D:\\ChromeCoreDownloads";
        String filename = "664c3e562e574790abc7c627d430d7a4";
        String type = "png";

        String url = "http://ltf.ldxinyong.com/online/jjxy/664c3e562e574790abc7c627d430d7a4.pdf";

        // 将pdf装图片 并且自定义图片得格式大小
        File file = new File(fileAddress + "\\" + filename + ".pdf");
//        File file = new File(url);
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                ImageIO.write(image, type, new File(fileAddress + "\\" + filename + "_" + (i + 1) + "." + type));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Lambda源码解析-start>>>>>>>>>>>>>>>>>>>>>>>>>
    @Test
    public void testSimpleLambda(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("没有使用Lambda");
            }
        };
        runnable.run();

        Runnable runnable1 = () -> System.out.println("使用Lambda");
        runnable1.run();

    }
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Lambda源码解析-end>>>>>>>>>>>>>>>>>>>>>>>>>


    // <<<<<<<<<<<<<<<<<<<<<<<<<<< 不同的ArrayList-start >>>>>>>>>>>>>>>>>>>>
    @Test
    public void testArraysArrayList(){
        String[] strings = {"a", "b", "c", "d", "e"};
        List<String> list = Arrays.asList(strings);
//        list.add("f");   // 非常要注意这里虽然有add方法，但是实际上是不能使用的，会报错:因为这个内部类父级有add，但是没有实现add方法
        System.out.println(list);  // 所以list实际上是固定长度的

        ArrayList<String> list1 = new ArrayList<String>(list);
        boolean f = list1.add("f");
        System.out.println(list1);

    }

    @Test
    public void testCollectionRemove(){
        String[] strings = {"a", "b", "c", "d", "e",null};
        ArrayList<String> list = new ArrayList<>(Arrays.asList(strings));

        // 使用迭代器删除函数
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();

            if(null == next){
                iterator.remove();
            }

            System.out.println(list);

        }
    }


    @Test
    public void testAliTest(){
        float a = 0.125f;
        double b = 0.125d;
        System.out.println((a - b) == 0.0);

        double c = 0.8; double d = 0.7; double e = 0.6;
        System.out.println(c-d == d-e);
    }
    // <<<<<<<<<<<<<<<<<<<<<<<<<<< 不同的ArrayList-end >>>>>>>>>>>>>>>>>>>>

    // <<<<<<<<<<<<<<阿里java开发手册-start>>>>>>>>>>>>>>>>
    @Test
    public void testAliDev(){
        // ali推荐使用 java.util.Objects#equals（JDK7 引入的工具类）。
        boolean equals = Objects.equals("a", "b");

        // ali 推荐都是用 Objects.equals(a,b)  判断
        Integer i = new Integer(129);
        Integer i1 = new Integer(129);
        System.out.println(i == i1);  // false
        System.out.println(Objects.equals(i,i1));  // true
        int i2 = 129;
        int i3 = 129;
        System.out.println(i2 == i3);  // true
        System.out.println(Objects.equals(i2,i3));  // true


        // ali：比较两个浮点数是否相等，不能用 ==  或者 equal
        // 正解1：
        float a = 1.0f - 0.9f;
        float b = 0.9f - 0.8f;
        float diff = 1e-6f;
        if (Math.abs(a - b) < diff) {
            System.out.println("true");
        }

//        正解2
        BigDecimal a1 = new BigDecimal("1.0");
        BigDecimal b1 = new BigDecimal("0.9");
        BigDecimal c1 = new BigDecimal("0.8");
        BigDecimal x1 = a1.subtract(b1);   // 相减
        BigDecimal y1 = b1.subtract(c1);
        if (Objects.equals(x1,y1)) {
            System.out.println("true");
        }


//        ali：浮点数禁止直接用 BigDecimal ，一定会不准确
        System.out.println("_______________浮点数计算__________________");
        BigDecimal recommend1 = new BigDecimal("0.1");  // 方法一,用字符串  优先推荐入参为 String 的构造方法
        BigDecimal recommend2 = BigDecimal.valueOf(0.1);   // 方法二： 用valueOf ： Double 的 toString 按 double 的实际能表达的精度对尾数进行了截断。
        BigDecimal g = new BigDecimal(0.1f);
        System.out.println(recommend1);
        System.out.println(recommend2);
        System.out.println(g);  //  0.1 的设计大小：0.100000001490116119384765625


        System.out.println("String#aplit");
        String str = "a,b,c,,";
        String[] ary = str.split(",");
        System.out.println(ary.length); // 预期大于 3，结果是 3

    }

    private static void method(String param) {
        switch (param) {
            // 肯定不是进入这里
            case "sth":
                System.out.println("it's sth");
                break;
            // 也不是进入这里
            case "null":
                System.out.println("it's null");
                break;
            // 也不是进入这里
            default:
                System.out.println("default");
        }
    }

    @Test
    public void testSwitch(){
        method(null);   // 这个方法在 null 时，调用会直接报错：java.lang.NullPointerException，
                               // 所以此变量为外部参数时，必须先进行 null判断
    }

    @Test
    public void testCondition(){
        Integer a = 1;
        Integer b = 2;
        Integer c = null;
        Boolean flag = false;
        // a*b 的结果是 int 类型，那么 c 会强制拆箱成 int 类型，抛出 NPE 异常
        Integer result=(flag? a*b : c);
        System.out.println(result);    //  java.lang.NullPointerException :NPE异常
    }
    // <<<<<<<<<<<<<<阿里java开发手册-end>>>>>>>>>>>>>>>>
}
