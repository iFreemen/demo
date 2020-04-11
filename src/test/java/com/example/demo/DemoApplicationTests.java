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
import org.apache.activemq.command.ActiveMQQueue;
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

import javax.jms.Destination;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

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

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>多线程学习-end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>设计模式-start<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>设计模式-end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    // 锁
    public class MyAQS extends AbstractQueuedSynchronizer{

    }
}
