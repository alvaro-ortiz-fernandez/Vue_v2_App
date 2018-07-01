package com.vue.app.web;

import com.vue.app.repo.model.User;
import com.vue.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class TestController {

    @Autowired
    UserService userService;

    @RequestMapping(value="/")
    public String index() {
        System.out.println("2yyyy    yyyy    yyyy    yyyy    yyyy");
        List<User> users = userService.getAll();
        return "test";
    }
}