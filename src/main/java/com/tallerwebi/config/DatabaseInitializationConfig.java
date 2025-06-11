package com.tallerwebi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class DatabaseInitializationConfig {

    @Bean
    @DependsOn("sessionFactory")
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        // Solo ejecutar si existe el archivo data.sql
        try {
            ClassPathResource resource = new ClassPathResource("data.sql");
            if (resource.exists()) {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(resource);
                populator.setIgnoreFailedDrops(true);
                initializer.setDatabasePopulator(populator);
            }
        } catch (Exception e) {
            // Si no existe data.sql, no hacer nada
            System.out.println("No se encontró data.sql, saltando inicialización de datos");
        }

        return initializer;
    }
}