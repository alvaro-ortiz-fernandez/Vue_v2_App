package com.vue.app.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@PropertySource("classpath:messages.properties")
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    Environment env;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print(exception.getMessage());
        //response.getWriter().print(env.getProperty("login.invalido"));
        response.getWriter().flush();
    }
}