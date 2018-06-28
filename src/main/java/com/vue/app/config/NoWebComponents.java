package com.vue.app.config;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.vue.app"},
        excludeFilters={@Filter(type=FilterType.ANNOTATION, value=EnableWebMvc.class)})
public class NoWebComponents {

}