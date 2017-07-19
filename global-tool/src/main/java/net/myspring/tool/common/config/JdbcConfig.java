package net.myspring.tool.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.collect.Maps;
import net.myspring.tool.GlobalToolApplication;
import net.myspring.tool.common.dataSource.DynamicDataSource;
import net.myspring.tool.common.enums.DataSourceTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
public class JdbcConfig {
    @Autowired
    private Environment environment;

    @Bean
    public DynamicDataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = Maps.newHashMap();
        targetDataSources.put("FACTORY_JXOPPO",getDataSouce("spring.datasource.factory.JXOPPO"));
        targetDataSources.put("FACTORY_JXVIVO",getDataSouce("spring.datasource.factory.JXVIVO"));
        targetDataSources.put("FUTURE_JXOPPO",getDataSouce("spring.datasource.ws-future.JXOPPO"));
        targetDataSources.put("FUTURE_JXVIVO",getDataSouce("spring.datasource.ws-future.JXVIVO"));
        targetDataSources.put("FACTORY_IDVIVO",getDataSouce("spring.datasource.factory.IDVIVO"));
        targetDataSources.put("FUTURE_IDVIVO",getDataSouce("spring.datasource.ws-future.IDVIVO"));
        targetDataSources.put(DataSourceTypeEnum.LOCAL.name(),getDataSouce("spring.datasource.global-tool"));
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        return dataSource;
    }


    private DataSource getDataSouce(String prefix) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(environment.getProperty(prefix + ".driver-class-name"));
        dataSource.setUrl(environment.getProperty(prefix + ".url"));
        dataSource.setUsername(environment.getProperty(prefix + ".username"));
        dataSource.setPassword(environment.getProperty(prefix + ".password"));
        dataSource.setMaxActive(20);
        dataSource.setMinIdle(5);
        dataSource.setInitialSize(0);
        dataSource.setRemoveAbandonedTimeout(6000);
        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dynamicDataSource());
        localContainerEntityManagerFactoryBean.setPackagesToScan(ClassUtils.getPackageName(GlobalToolApplication.class), ClassUtils.getPackageName(Jsr310JpaConverters.class));
        localContainerEntityManagerFactoryBean.getJpaPropertyMap().put("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        localContainerEntityManagerFactoryBean.getJpaPropertyMap().put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaDialect(jpaDialect());
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public JpaDialect jpaDialect() {
        return new HibernateJpaDialect();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return localContainerEntityManagerFactoryBean().getNativeEntityManagerFactory();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager jpaTransactionManager =new JpaTransactionManager(entityManagerFactory());
        jpaTransactionManager.setJpaDialect(jpaDialect());
        return jpaTransactionManager;
    }

    @Bean
    public JdbcTemplate JdbcTemplate() {
        return new JdbcTemplate(dynamicDataSource());
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dynamicDataSource());
    }

    @Bean
    public SimpleJdbcCall simpleJdbcCall(){
        return new SimpleJdbcCall(dynamicDataSource());
    }
}
