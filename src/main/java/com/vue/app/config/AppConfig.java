package com.vue.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = {"com.vue.app.config"},
        excludeFilters={@Filter(type=FilterType.ANNOTATION, value=Configuration.class)})
public class AppConfig {

    /* Método que devuelve un bean DataSource
     * con la conexión a la BBDD: */
    /*@Bean
    public DataSource dataSource() {
        DriverManagerDataSource managerDataSource = new DriverManagerDataSource();
        managerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        managerDataSource.setUrl("jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11199452");
        managerDataSource.setUsername("sql11199452");
        managerDataSource.setPassword("Xx7mps271q");

        return managerDataSource;
    }*/

    /* Método que devuelve un bean JdbcTemplate asociado al
     * DataSource para trabajar directamente con la BBDD: */
    /*@Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }*/
}