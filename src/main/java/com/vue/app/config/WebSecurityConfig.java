package com.vue.app.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String USERS_BY_USERNAME_QUERY = "SELECT username, password, enabled FROM users WHERE username = ?";
    private static final String AUTOHORITIES_BY_USERNAME_QUERY = "SELECT username, role FROM user_roles WHERE username = ?";

    @Autowired
    DataSource dataSource;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery(USERS_BY_USERNAME_QUERY)
                .authoritiesByUsernameQuery(AUTOHORITIES_BY_USERNAME_QUERY);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
            .and()
                .formLogin().loginPage("/login").failureUrl("/login?error").loginProcessingUrl("/login-processing")
                .usernameParameter("username").passwordParameter("password").permitAll()
            .and()
                .logout().logoutSuccessUrl("/login?logout")
            .and()
                .exceptionHandling().accessDeniedPage("/denied")
             .and()
                .csrf();
    }
}