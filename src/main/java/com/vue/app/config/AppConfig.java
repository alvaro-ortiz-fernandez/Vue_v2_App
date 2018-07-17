package com.vue.app.config;

import com.vue.app.config.security.WebSecurityConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@ComponentScan(basePackages = {"com.vue.app.config", "com.vue.app.config.security"},
        excludeFilters={@Filter(type=FilterType.ANNOTATION, value=Configuration.class)})
@Import({ WebSecurityConfig.class })
public class AppConfig {

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

}