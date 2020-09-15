package com.raphau.trafficgenerator.controller;

import com.raphau.trafficgenerator.dto.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/test")
    public Test test(){
        return new Test(3, "Test");
    }
}
