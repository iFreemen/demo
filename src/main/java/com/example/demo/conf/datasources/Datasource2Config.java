package com.example.demo.conf.datasources;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
//扫描 Mapper 接口并容器管理
@MapperScan(basePackages = Datasource2Config.PACKAGE, sqlSessionFactoryRef = "ds2SqlSessionFactory")
public class Datasource2Config {
    // 精确到 master 目录，以便跟其他数据源隔离
    static final String PACKAGE = "com.example.demo.dao.mysql.ds2";
    static final String MAPPER_LOCATION = "classpath:mybatis/mappers/ds2/*.xml";

    @Value("${ds2.datasource.url}")
    private String url;
    @Value("${ds2.datasource.username}")
    private String user;
    @Value("${ds2.datasource.password}")
    private String password;
    @Value("${ds2.datasource.driverClassName}")
    private String driverClass;

    @Value("${ds2.datasource.maxActive}")
    private Integer maxActive;
    @Value("${ds2.datasource.minIdle}")
    private Integer minIdle;
    @Value("${ds2.datasource.initialSize}")
    private Integer initialSize;
    @Value("${ds2.datasource.maxWait}")
    private Long maxWait;
    @Value("${ds2.datasource.timeBetweenEvictionRunsMillis}")
    private Long timeBetweenEvictionRunsMillis;
    @Value("${ds2.datasource.minEvictableIdleTimeMillis}")
    private Long minEvictableIdleTimeMillis;
    @Value("${ds2.datasource.testWhileIdle}")
    private Boolean testWhileIdle;
    @Value("${ds2.datasource.testWhileIdle}")
    private Boolean testOnBorrow;
    @Value("${ds2.datasource.testOnBorrow}")
    private Boolean testOnReturn;

    @Bean(name = "ds2DataSource")
    public DataSource ds2DataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        //连接池配置
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setValidationQuery("SELECT 'x'");

        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        return dataSource;
    }

    @Bean(name = "ds2TransactionManager")
    public DataSourceTransactionManager ds2TransactionManager() {
        return new DataSourceTransactionManager(ds2DataSource());
    }

    @Bean(name = "ds2SqlSessionFactory")
    public SqlSessionFactory ds2SqlSessionFactory(@Qualifier("ds2DataSource") DataSource ds2DataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(ds2DataSource);
        sessionFactory.setTypeAliasesPackage("com.example.demo.entity.fordatasources");
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(Datasource2Config.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
