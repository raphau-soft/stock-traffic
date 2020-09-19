package com.raphau.trafficgenerator.service;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class UserThread implements  Runnable {

    private final String username;
    private final String password = "testpassword";
    private final AsyncService asyncService;
    private static Logger log = LoggerFactory.getLogger(AsyncService.class);

    public UserThread(String username, AsyncService asyncService){
        this.username = username;
        this.asyncService = asyncService;
    }

    @Override
    public void run() {
        log.info("Thread of user " + username);
        try {
//            CompletableFuture<String> message = asyncService.postRegistration(username);
//            CompletableFuture.allOf(message).join();
            log.info("MESSAGE ---->");// + message.get());
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
