package com.thetsajeet.dbv;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
class DBVAutoConfigurationTest {
    // Create a reusable ApplicationContextRunner for testing auto-configuration
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DBVAutoConfiguration.class));

    @Test
    void whenConditionsMet_databaseValidationServiceAndDbvRunnerBeansCreated() {
        // Arrange: Default case (db.validator.enabled=true or missing)
        contextRunner
                .withUserConfiguration(TestConfig.class) // Provides DataSource and EntityManagerFactory
                .run(context -> {
                    // Assert: Both beans are created
                    assertThat(context).hasSingleBean(DatabaseValidationService.class);
                    assertThat(context).hasSingleBean(DBVRunner.class);

                    // Verify bean types and dependencies
                    DatabaseValidationService service = context.getBean(DatabaseValidationService.class);
                    DBVRunner runner = context.getBean(DBVRunner.class);
                    assertThat(service).isNotNull();
                    assertThat(runner).isNotNull();
                });
    }

    @Test
    void whenValidatorDisabled_noBeansCreated() {
        // Arrange: Set db.validator.enabled=false
        contextRunner
                .withUserConfiguration(TestConfig.class)
                .withPropertyValues("db.validator.enabled=false")
                .run(context -> {
                    // Assert: No beans are created
                    assertThat(context).doesNotHaveBean(DatabaseValidationService.class);
                    assertThat(context).doesNotHaveBean(DBVRunner.class);
                });
    }


    @Test
    void whenDatabaseValidationServiceExists_noNewServiceBeanCreated() {
        // Arrange: Provide a custom DatabaseValidationService bean
        contextRunner
                .withUserConfiguration(TestConfig.class, CustomDatabaseValidationServiceConfig.class)
                .run(context -> {
                    // Assert: Custom service bean is used, not the one from DBVAutoConfiguration
                    assertThat(context).hasSingleBean(DatabaseValidationService.class);
                    assertThat(context.getBean(DatabaseValidationService.class))
                            .isInstanceOf(CustomDatabaseValidationService.class);
                    // DBVRunner is still created, using the custom service
                    assertThat(context).hasSingleBean(DBVRunner.class);
                });
    }

    @Test
    void whenDBVRunnerExists_noNewRunnerBeanCreated() {
        // Arrange: Provide a custom DBVRunner bean
        contextRunner
                .withUserConfiguration(TestConfig.class, CustomDBVRunnerConfig.class)
                .run(context -> {
                    // Assert: Custom runner bean is used, not the one from DBVAutoConfiguration
                    assertThat(context).hasSingleBean(DBVRunner.class);
                    assertThat(context.getBean(DBVRunner.class))
                            .isInstanceOf(CustomDBVRunner.class);
                    // DatabaseValidationService is still created
                    assertThat(context).hasSingleBean(DatabaseValidationService.class);
                });
    }

    // Test configuration to provide DataSource and EntityManagerFactory
    @org.springframework.context.annotation.Configuration
    static class TestConfig {
        @org.springframework.context.annotation.Bean
        public DataSource dataSource() {
            return org.mockito.Mockito.mock(DataSource.class);
        }

        @org.springframework.context.annotation.Bean
        public EntityManagerFactory entityManagerFactory() {
            return org.mockito.Mockito.mock(EntityManagerFactory.class);
        }
    }

    // Configuration to provide only EntityManagerFactory
    @org.springframework.context.annotation.Configuration
    static class EntityManagerFactoryConfig {
        @org.springframework.context.annotation.Bean
        public EntityManagerFactory entityManagerFactory() {
            return org.mockito.Mockito.mock(EntityManagerFactory.class);
        }
    }

    // Configuration to provide only DataSource
    @org.springframework.context.annotation.Configuration
    static class DataSourceConfig {
        @org.springframework.context.annotation.Bean
        public DataSource dataSource() {
            return org.mockito.Mockito.mock(DataSource.class);
        }
    }

    // Configuration to provide a custom DatabaseValidationService
    @org.springframework.context.annotation.Configuration
    static class CustomDatabaseValidationServiceConfig {
        @org.springframework.context.annotation.Bean
        public DatabaseValidationService databaseValidationService() {
            return new CustomDatabaseValidationService();
        }
    }

    // Configuration to provide a custom DBVRunner
    @org.springframework.context.annotation.Configuration
    static class CustomDBVRunnerConfig {
        @org.springframework.context.annotation.Bean
        public DBVRunner dbvRunner(DatabaseValidationService service) {
            return new CustomDBVRunner();
        }
    }

    // Custom implementation for testing @ConditionalOnMissingBean
    static class CustomDatabaseValidationService extends DatabaseValidationService {
        CustomDatabaseValidationService() {
            super(org.mockito.Mockito.mock(DataSource.class),
                    org.mockito.Mockito.mock(EntityManagerFactory.class));
        }
    }

    // Custom implementation for testing @ConditionalOnMissingBean
    static class CustomDBVRunner extends DBVRunner {
        CustomDBVRunner() {
            super(org.mockito.Mockito.mock(DatabaseValidationService.class));
        }
    }
}