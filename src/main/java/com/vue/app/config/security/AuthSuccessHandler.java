package com.vue.app.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@PropertySource("classpath:messages.properties")
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    Environment env;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(env.getProperty("login.success"));
        response.getWriter().flush();
    }
}