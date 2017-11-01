package com.geb.config;


import com.geb.util.automation.AutoResultSetMapper;
import com.geb.util.automation.GlobalContext;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/*
SqlSessionFactory的配置
 */
@Configuration
public class DataSourceConfig {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    @Autowired
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        Properties prop = new Properties();
        prop.put("logImpl", "LOG4J2");
        prop.put("cacheEnabled", "false");
        prop.put("lazyLoadingEnabled", "false");
        bean.setConfigurationProperties(prop);
        bean.setTypeAliasesPackage("com.geb.entity");
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/*.xml"));
        GlobalContext g = new GlobalContext();
        Interceptor[] icps = new Interceptor[]{new AutoResultSetMapper(new GlobalContext())};
        bean.setPlugins(icps);
        return bean.getObject();
    }

}
