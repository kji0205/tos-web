package kr.go.togetherschool.tosweb.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @ResponseBody
    @GetMapping("/index")
    public String index() {
        return "index page";
    }

    @ResponseBody
    @GetMapping("/public")
    public String pub() {
        return "public";
    }

    @ResponseBody
    @GetMapping("/private")
    public String pri() {
        return "private";
    }
}
