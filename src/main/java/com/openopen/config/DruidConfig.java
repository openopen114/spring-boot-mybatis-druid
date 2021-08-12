package com.openopen.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.SQLException;
import java.util.Properties;


@Configuration
public class DruidConfig {
    private Logger logger = LoggerFactory.getLogger(DruidConfig.class);


    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.filters}")
    private String filters;

    @Value("{spring.datasource.connectionProperties}")
    private String connectionProperties;

    @Value("${spring.datasource.connectionProperties.druid.stat.mergeSql}")
    private String mergeSql;


    @Value("${spring.datasource.connectionProperties.druid.stat.slowSqlMillis}")
    private String slowSqlMillis;





    @Value("${APIENV}")
    private String APIENV;

    @Value("${gcloud.sql.socketFactory}")
    private String socketFactory;


    @Value("${gcloud.sql.cloudSqlInstance}")
    private String cloudSqlInstance;

    @Bean     //声明其为Bean实例
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource
    public DruidDataSource dataSource(){
        DruidDataSource pool = new DruidDataSource();


        pool.setDriverClassName(driverClassName);

        //configuration
        pool.setInitialSize(initialSize);
        pool.setMinIdle(minIdle);
        pool.setMaxActive(maxActive);
        pool.setMaxWait(maxWait);
        pool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        pool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        pool.setValidationQuery(validationQuery);
        pool.setTestWhileIdle(testWhileIdle);
        pool.setTestOnBorrow(testOnBorrow);
        pool.setTestOnReturn(testOnReturn);
        pool.setPoolPreparedStatements(poolPreparedStatements);
        pool.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            pool.setFilters(filters);
        } catch (SQLException e) {
            logger.error("druid configuration initialization filter", e);
        }


        logger.info("===> connectionProperties");
        logger.info(connectionProperties);




        if(APIENV.equals("dev")) {
            //開發環境
            pool.setUrl(this.dbUrl);
            pool.setUsername(username);
            pool.setPassword(password);
        }else {
            //cloud 環境
            Properties connProps = new Properties();
            connProps.setProperty("user", username);
            connProps.setProperty("password", password);
            connProps.setProperty("sslmode", "disable");
            connProps.setProperty("socketFactory", socketFactory);
            connProps.setProperty("cloudSqlInstance", cloudSqlInstance);
            connProps.setProperty("connectTimeout","60");
            connProps.setProperty("socketTimeout", "60");
            connProps.setProperty("loginTimeout", "60");
            pool.setUrl(this.dbUrl);
            pool.setConnectProperties(connProps);
        }





        logger.info("");
        logger.info("===========================================");
        logger.info("=============== APIENV: " + APIENV + " ===============");
        logger.info("===========================================");
        logger.info("");

        logger.info("==> db url: " + dbUrl);
        logger.info("connectionProperties:" + connectionProperties);
        logger.info("socketFactory==>"+socketFactory);
        logger.info("cloudSqlInstance===>"+cloudSqlInstance);
        logger.info("username:"+username);
        logger.info("password:"+password);


        return pool;
    }
}
