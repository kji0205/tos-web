package kr.go.togetherschool.tosweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JoinController {

    @ResponseBody
    @GetMapping("/join")
    public String index() {
        return "join page";
    }
}
