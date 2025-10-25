package com.thetsajeet.dbv;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@AutoConfiguration
@ConditionalOnClass({DataSource.class, EntityManagerFactory.class})
@ConditionalOnProperty(prefix = "db.validator", name = "enabled", havingValue = "true", matchIfMissing = true)
class DBVAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DatabaseValidationService databaseValidationService(DataSource dataSource,
                                                               EntityManagerFactory entityManagerFactory,
                                                               DbvStarterProperties properties) {
        return new DatabaseValidationService(dataSource, entityManagerFactory, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DBVRunner dbvRunner(DatabaseValidationService service) {
        return new DBVRunner(service);
    }
}
