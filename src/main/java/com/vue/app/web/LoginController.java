package com.vue.app.web;

import com.vue.app.repo.model.User;
import com.vue.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@PropertySource("classpath:messages.properties")
public class LoginController {

    @Autowired
    Environment env;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login/login";
    }

    @RequestMapping(value = "/registro", method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> registerForm(@RequestBody User user) {
        // Si el nombre de usuario ya está devolvemos el error para tratarlo en el front:
        if (userService.exists(user.getUsername())) {
            return new ResponseEntity<>(new Error(env.getProperty("registro.yaexiste")), HttpStatus.NOT_ACCEPTABLE);
        } else {
            try {
                user = userService.register(user);
            } catch (Exception e) {
                // Si se produce error inesperado también lo tratamos en el front, diferenciándolo con otro código de HttpStatus:
                return new ResponseEntity<>(new Error(e.getCause().getMessage()), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }
}