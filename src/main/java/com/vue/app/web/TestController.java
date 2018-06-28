package com.vue.app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @RequestMapping(value="/")
    public String index() {
        System.out.println("1yyyy    yyyy    yyyy    yyyy    yyyy");
        return "test";
    }

    @RequestMapping(value="/asp")
    public String test() {
        System.out.println("1xxxx    xxxx    xxxx    xxxx    xxxx");
        return "test";
    }
}