package org.springframework.samples.petclinic.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Druid的DataResource配置类
 * @author Andy Chen
 * @since 2016.11.7
 */
@Configuration
public class DruidDataSourceConfig implements EnvironmentAware
{
    private Logger logger = LoggerFactory.getLogger(DruidDataSourceConfig.class);
    private RelaxedPropertyResolver propertyResolver;

    public void setEnvironment(Environment env)
    {
        this.propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
    }

    @Bean
    public WallConfig wallConfig()
    {
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);

        return wallConfig;
    }

    @Bean
    @Autowired
    public WallFilter wallFilter(WallConfig wallConfig)
    {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig);

        return wallFilter;
    }

    @Bean     //声明其为Bean实例
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource
    @Autowired
    public DataSource dataSource(WallFilter wallFilter)
    {
        System.out.println("init druid...start");
        DruidDataSource datasource = new DruidDataSource();

        datasource.setUrl(propertyResolver.getProperty("url"));
        datasource.setDriverClassName(propertyResolver.getProperty("driver-class-name"));
        datasource.setUsername(propertyResolver.getProperty("username"));
        datasource.setPassword(propertyResolver.getProperty("password"));

        datasource.setInitialSize(Integer.valueOf(propertyResolver.getProperty("initial-size")));
        datasource.setMinIdle(Integer.valueOf(propertyResolver.getProperty("min-idle")));
        datasource.setMaxActive(Integer.valueOf(propertyResolver.getProperty("max-active")));
        datasource.setMaxWait(Long.valueOf(propertyResolver.getProperty("max-wait")));
        datasource.setTimeBetweenEvictionRunsMillis(Long.valueOf(propertyResolver.getProperty("time-between-eviction-runs-millis")));
        datasource.setTimeBetweenLogStatsMillis(Long.valueOf(propertyResolver.getProperty("timeBetweenLogStatsMillis")));
        datasource.setMinEvictableIdleTimeMillis(Long.valueOf(propertyResolver.getProperty("min-evictable-idle-time-millis")));

//        datasource.setValidationQuery(propertyResolver.getProperty("validationQuery"));
        datasource.setTestWhileIdle(Boolean.valueOf(propertyResolver.getProperty("testWhileIdle")));
        datasource.setTestOnBorrow(Boolean.valueOf(propertyResolver.getProperty("testOnBorrow")));
        datasource.setTestOnReturn(Boolean.valueOf(propertyResolver.getProperty("testOnReturn")));
        datasource.setPoolPreparedStatements(Boolean.valueOf(propertyResolver.getProperty("poolPreparedStatements")));
        datasource.setMaxPoolPreparedStatementPerConnectionSize(Integer.valueOf(propertyResolver.getProperty("maxPoolPreparedStatementPerConnectionSize")));

        List<Filter> filters = new ArrayList<>();
        filters.add(wallFilter);

        try
        {
            datasource.setFilters(propertyResolver.getProperty("filters"));
            datasource.setProxyFilters(filters);
        }
        catch (SQLException e)
        {
            logger.error("druid configuration initialization filter", e);
        }

        datasource.setConnectionProperties(propertyResolver.getProperty("connectionProperties"));

        System.out.println("init druid...complete");

        return datasource;
    }
}
