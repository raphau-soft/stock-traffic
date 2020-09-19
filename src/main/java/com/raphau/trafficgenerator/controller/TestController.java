package com.raphau.trafficgenerator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raphau.trafficgenerator.service.AsyncService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class TestController {

    private static Logger log = LoggerFactory.getLogger(TestController.class);
    private String username = "userxd7";
    private List<String> testList = new ArrayList<>();

    @Autowired
    private AsyncService service;

    @GetMapping("/asyncTest")
    public void asyncTest() throws InterruptedException, ExecutionException, JSONException, JsonProcessingException {

        log.info("testAsynch start");

        for(int i = 0; i < 10; i++){
            log.info("Register " + username + i);
            service.postRegistration(username + i, testList);
        }
        while(testList.size() < 10);
        for(int i = 0; i < 10; i++){
            log.info("Login " + username + i);
            service.runTests(username + i);
        }
    }
}






















