package com.company.wm.middleware.core.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.azure.security.keyvault.secrets.SecretClient;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.akbank.wm.middleware.core.repository", entityManagerFactoryRef = "entityManagerFactoryWM", transactionManagerRef = "transactionManagerWM")
@AllArgsConstructor
public class DBConfigWM {

    private SecretClient secretClient;

    @Primary
    @Bean(name = "dataSourceWM")
    public DataSource dataSource() {

        try {
            return DataSourceBuilder.create()
                    .url(secretClient.getSecret("wm-db-url").getValue())
                    .username(secretClient.getSecret("wm-db-username").getValue())
                    .password(secretClient.getSecret("wm-db-password").getValue())
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create Datasource for WM database: " + e.getMessage(), e);
        }

    }

    @Primary
    @Bean(name = "entityManagerFactoryWM")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
            @Qualifier("dataSourceWM") DataSource dataSource) {

        try {
            return builder
                    .dataSource(dataSource)
                    .packages("com.akbank.wm.middleware.core.entity")
                    .persistenceUnit("db-wm")
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to create EntityManagerFactory for WM database: " + e.getMessage(), e);
        }

    }

    @Primary
    @Bean(name = "transactionManagerWM")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactoryWM") EntityManagerFactory entityManagerFactory) {

        try {
            return new JpaTransactionManager(entityManagerFactory);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create TransactionManager for WM database: " + e.getMessage(),
                    e);
        }
    }

}
