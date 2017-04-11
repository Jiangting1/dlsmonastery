package net.myspring.cloud.common.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.dataSource.DynamicDataSource;
import net.myspring.cloud.common.enums.DataSourceTypeEnum;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.mybatis.context.ProviderMapperAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by liuj on 2017/3/19.
 */
@Configuration
public class MybatisConfig {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public ProviderMapperAspect providerMapperAspect() {
        return new ProviderMapperAspect();
    }
    @Bean
    public DynamicDataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = Maps.newHashMap();
        List<KingdeeBook> kingdeeBookList = getKingdeeBookList();
        for (KingdeeBook kingdeeBook:kingdeeBookList) {
            targetDataSources.put(DataSourceTypeEnum.KINGDEE.name() + "_" + kingdeeBook.getCompanyId(),getCloudDataSource(kingdeeBook));
        }
        targetDataSources.put(DataSourceTypeEnum.LOCAL.name(),getLocalDataSource());
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        return dataSource;
    }

    private DataSource getLocalDataSource() {
        Properties props = new Properties();
        props.put("driverClassName", "com.mysql.jdbc.Driver");
        props.put("url", url);
        props.put("username", username);
        props.put("password", password);
        try {
            return DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            return null;
        }
    }

    private List<KingdeeBook> getKingdeeBookList() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getLocalDataSource());
        String sql = "select * from sys_kingdee_book";
        List<KingdeeBook> kingdeeBookList = jdbcTemplate.query(sql,new BeanPropertyRowMapper<KingdeeBook>(KingdeeBook.class));
        return kingdeeBookList;
    }

    private DataSource getCloudDataSource(KingdeeBook kingdeeBook) {
        Properties props = new Properties();
        props.put("driverClassName", "net.sourceforge.jtds.jdbc.Driver");
        props.put("url", kingdeeBook.getKingdeeUrl());
        props.put("username", kingdeeBook.getKingdeeUsername());
        props.put("password", kingdeeBook.getKingdeePassword());
        try {
            return DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            return null;
        }
    }
}