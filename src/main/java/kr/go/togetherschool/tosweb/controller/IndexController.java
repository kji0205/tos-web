package kr.go.togetherschool.tosweb.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

//    @Secured("USER")
    @GetMapping("/index")
    public String index() {
        return "index.html";
    }


}
