package com.vue.app.web;

import com.vue.app.repo.model.User;
import com.vue.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);

        model.addAttribute("title", "Spring Security Login Form - Database Authentication");
        model.addAttribute("message", "This is default page!");
        model.addAttribute("principal", request.getUserPrincipal());

        return "home/home";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(Model model, HttpServletRequest request) {

        model.addAttribute("title", "Spring Security Login Form - Database Authentication");
        model.addAttribute("message", "This page is for ROLE_ADMIN only!");
        model.addAttribute("principal", request.getUserPrincipal());
        return "home/admin";

    }
}