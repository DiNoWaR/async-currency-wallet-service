package com.zad.wallet.config;


import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, LiquibaseProperties props) {
        var liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(props.getChangeLog());
        liquibase.setDefaultSchema(props.getDefaultSchema());
        liquibase.setDropFirst(props.isDropFirst());
        liquibase.setShouldRun(props.isEnabled());
        return liquibase;
    }
}
