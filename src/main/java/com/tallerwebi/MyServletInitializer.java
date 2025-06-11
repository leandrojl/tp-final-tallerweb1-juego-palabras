package com.tallerwebi;

import com.tallerwebi.config.*;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MyServletInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {

    // services and data sources
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                HibernateConfig.class,
                DatabaseInitializationConfig.class,
                StartupDataLoader.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{
                SpringWebConfig.class,
                WebSocketConfig.class
        };
    }


    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
