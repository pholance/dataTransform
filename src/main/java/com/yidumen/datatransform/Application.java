package com.yidumen.datatransform;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author 蔡迪旻
 *         2015年11月30日
 */
@SpringBootApplication
@EnableTransactionManagement
public class Application {

    @Bean
    @ConfigurationProperties(prefix = "datasource.secondary")
    public DataSource getAceDataSource() {
        final BasicDataSource result = new BasicDataSource();
        result.setDriverClassName("com.mysql.jdbc.Driver");
        result.setUrl("jdbc:mysql://localhost:3306/rl8k07vaxq6b1663");
        result.setUsername("yidumen");
        result.setPassword("yidumen");
        return result;
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.primary")
    public DataSource getLocalDataSource() {
        final BasicDataSource result = new BasicDataSource();
        result.setDriverClassName("com.mysql.jdbc.Driver");
        result.setUrl("jdbc:mysql://localhost:3306/yidumen");
        result.setUsername("yidumen");
        result.setPassword("yidumen");
        return result;
    }

    @Bean
    public PlatformTransactionManager aceTransaction() {
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(getAceDataSource());
        return transactionManager;
    }

    @Bean
    @Primary
    public PlatformTransactionManager localTransaction() {
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(getLocalDataSource());
        return transactionManager;
    }

    @Bean(name = "aceJdbc")
    public JdbcTemplate aceJdbcTemplate() {
        final JdbcTemplate result = new JdbcTemplate();
        result.setDataSource(getAceDataSource());
        return result;
    }

    @Bean(name = "localJdbc")
    @Primary
    public JdbcTemplate localJdbcTemplate() {
        final JdbcTemplate result = new JdbcTemplate();
        result.setDataSource(getLocalDataSource());
        return result;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class);
    }
}
