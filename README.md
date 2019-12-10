"# springboot_serial_learning" 
"# demo" 
# demo

文档是笔记形式的，很粗糙。投合一下：
http://note.youdao.com/noteshare?id=20e0e46abbcf53e73d7f192cd455c953


一、快速创建独立的，生产级的Spring项目，并且配置很少。

二、特点：（反正就是多方支持，兼容好，上手快各种优势）
1、创建独立的Spring应用程序
2、直接嵌入Tomcat，Jetty或Undertow（无需部署WAR文件）
3、提供自以为是的“入门”依赖项，以简化构建配置
4、尽可能自动配置Spring和3rd Party库
5、提供生产就绪的功能，例如指标，运行状况检查和外部配置
6、完全没有代码生成，也不需要XML配置

三、快速搭建demo系统
1、第一个Springboot应用程序：https://start.spring.io/  （可能回报：Empty test suite 异常，maven install -X，就能解决）
2、整合web开发 （整合 Thymeleaf  参考：https://blog.csdn.net/XiaoA82/article/details/88052450）
3、整合的源码已上传到 github：https://github.com/iFreemen/demo

四、web开发基础系统（基于官方下载的springboot的开发）
1、提前安装好JDK1.8 和 IDEA环境
2、引入Springboot后，启动test和application是否正常
3、整合web、Thymeleaf、lombok，实现基本的页面展示
<!-- 以下依赖添加 -->
<!-- web开发必备 -->
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- 官方推荐的 魔板引擎框架 -->
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- @Data： 注解在类，生成setter/getter、equals、canEqual、hashCode、toString，需要提前在IDEA中插入lombok插件 -->
<dependency>
   <groupId>org.projectlombok</groupId>
   <artifactId>lombok</artifactId>
   <version>1.16.20</version>
</dependency>


2、目前的项目结构


3、创建controller、entity模块。
注意注解：@Controller  @GetMapping @Data  @RequestParam String name

4、Application扫描包
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo"})

5、创建templates模块、Thymeleaf的默认路径就是templates下的html文件。文件名要和return中的名字保持一致，否则会报错
6、页面和后端的参数传递，详情查看源代码及注解

五、系列学习 Thymeleaf的各种类型的传递和显示
1、基础参数：http://note.youdao.com/noteshare?id=4f75162899939a27ea28f70814c665e1    实现字符串、对象在页面的展示和List的循环、在js中的显示[[${user.name}]]，a 标签跳转
2、表单提交字符串、图片：http://note.youdao.com/noteshare?id=37a9750c8dae79a560f322e361bfe1bd 
3、ajax提交简单数据：http://note.youdao.com/noteshare?id=9488f0107dcb6f2f47054d52e39718bd
目前就是这些简单的运用：如有需要在补充。。。。。。。（想要什么功能demo可以联系我补充：ifreemen@qq.com）

六、 整合JPA、Mybatis、redis、mongo
1、整合JPA：https://github.com/iFreemen/springBoot_Jpa
2、整合Mybatis:
i、用Mybatis少不了逆向工程：https://github.com/iFreemen/mybatis_generator
ii、整合Mybatis：  Mybatis官方：https://mybatis.org/mybatis-3/zh/getting-started.html
      				在整合过程中因为用的通过Mapper依赖搞错了浪费了不少时间,留意  @MapperScan  是 tk，指定主键 @id ，返回包含主键的对象  @GeneratedValue(strategy = GenerationType.IDENTITY) 等等
<!-- 通用Mapper -->
<dependency>
   <groupId>tk.mybatis</groupId>
   <artifactId>mapper-spring-boot-starter</artifactId>
   <version>2.0.2</version>
</dependency>

详情查看代码：https://github.com/iFreemen/demo

iii、整合Druid: 参考外部博客：https://www.jianshu.com/p/fd2c8113f79d    源码： https://github.com/iFreemen/demo
多数据源：

iiii、整合redis：首先要先准备好redis环境：http://note.youdao.com/noteshare?id=96878ddd8ea0fded0882f90a85072f3f
工具参考：https://www.cnblogs.com/zeng1994/p/03303c805731afc9aa9c60dbbd32a323.html
源码： https://github.com/iFreemen/demo

3、整合 MongoDB：首先准备好MongoDB环境:http://note.youdao.com/noteshare?id=3ae56b5dd2d8b26101e549f33f6abb9e
注意 _id 的类型是ObjectId，另外：不用建表，mongo会自动根据写入的对象class，创建一个对应的表
源码： https://github.com/iFreemen/demo

七、整合 Excel 实现读操作，导入导出；各种文件操作：txt、XML（暂时不弄）
Excel读取会遇到很多问题的：例如整型会变成带小数点的，读取单元时循环内要使用表单头，对不用的类型要分来处理
Excel导出：表头的顺序、反射导致不能用  serialVersionUID 等等
源码：https://github.com/iFreemen/demo

八、邮件发送、附件Excel
参考链接：http://note.youdao.com/noteshare?id=62000a5141c28a28cc0ea3618fe4c424
详情见源码：https://github.com/iFreemen/demo   （附件的格式没做处理）

八、队列实现
九、EA
十、Dubbo+Zookepper
十一、Docker
十二、springcloud
十三、发布
十四、Jenkins
十五、日志分析、性能调优：JVM、SQL



