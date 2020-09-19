package com.raphau.trafficgenerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphau.trafficgenerator.dto.User;
import com.raphau.trafficgenerator.dto.UserLogin;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class AsyncService {

    private static Logger log = LoggerFactory.getLogger(AsyncService.class);
    private HttpHeaders headers;
    private JSONObject personJsonObject;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Async("asyncExecutor")
    public void postRegistration(String username, List<String> testList) throws InterruptedException, JSONException {
        log.info("Registration " + username + " starts");
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        personJsonObject = new JSONObject();
        personJsonObject.put("id", 0);
        personJsonObject.put("name", "John");
        personJsonObject.put("surname", "Johnny");
        personJsonObject.put("username", username);
        personJsonObject.put("email", "mail@mail.pl");
        personJsonObject.put("password", "testpassword");
        HttpEntity<String> request = new HttpEntity<>(personJsonObject.toString(), headers);
        String message = "";
        try {
            message = restTemplate.postForObject("http://172.20.0.2:8080/api/auth/signup", request, String.class);
            log.info(message);
            testList.add(message);
            log.info("Registration " + username + " ends");
        } catch (Exception e){
            testList.add(username + " error");
            log.info("Error " + username);
        }
    }

    @Async("asyncExecutor")
    public void runTests(String username) throws JSONException, JsonProcessingException {
        UserLogin userLogin = login(username);
        log.info(username + " ----> " + userLogin.getJwt());
        log.info("JSONResponse - " + getUser(userLogin.getJwt()));
    }

    private UserLogin login(String username) throws JSONException {
        log.info("Login " + username + " starts");
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        personJsonObject = new JSONObject();
        personJsonObject.put("username", username);
        personJsonObject.put("password", "testpassword");
        HttpEntity<String> request = new HttpEntity<>(personJsonObject.toString(), headers);
        UserLogin userLogin = null;
        try {
            userLogin = restTemplate.postForObject("http://172.20.0.2:8080/api/auth/signin", request, UserLogin.class);
            log.info("Login " + username + " ends");
        } catch (Exception e){
            log.info("Error " + username);
        }
        return userLogin;
    }

    private String getUser(String jwt) throws JSONException, JsonProcessingException {
        log.info("Get user from - " + jwt);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        try {
            jsonResponse = restTemplate.exchange(
                    "http://172.20.0.2:8080/api/user", HttpMethod.GET, entity, String.class, new Object());
            log.info("Get user from - " + jwt + " ends");
        } catch (Exception e){
            log.info("Error " + e);
        }
        assert jsonResponse != null;


        return (String) jsonResponse.getBody();
    }
}




















