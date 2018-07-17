package com.vue.app.config.security;

import java.util.Arrays;

import com.vue.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /* Podemos hacer el opt.get(), si el usuario no existe simplemente
        saltará excepción y se llamará al AuthFailureHandler */
        com.vue.app.repo.model.User user = userService.findById(username).get();
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        UserDetails userDetails = (UserDetails)new User(user.getUsername(), user.getPassword(), Arrays.asList(authority));
        return userDetails;
    }
}