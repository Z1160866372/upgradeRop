package com.richeninfo.config;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;


/**
 * @Author : zhouxiaohu
 * @create 2023/3/13 15:10
 */


@Configuration
//扫描 Mapper 接口并容器管理
@MapperScan(basePackages = {"com.richeninfo.entity.mapper.mapper.master"}, sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDataSourceConfig {

    // 精确到 master 目录，以便跟其他数据源隔离
    static final String PACKAGE = "com.richeninfo.entity.mapper.mapper.master";
    static final String MAPPER_LOCATION = "classpath:/*.xml";

    @Value("${spring.master.datasource.url}")
    private String url;

    @Value("${spring.master.datasource.username}")
    private String user;

    @Value("${spring.master.datasource.password}")
    private String password;

    @Value("${spring.master.datasource.driver-class-name}")
    private String driverClass;

    @Primary
    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "masterTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(masterDataSource());
    }

    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:/*.xml"));
        return sessionFactory.getObject();
    }
}