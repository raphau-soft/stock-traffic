package com.raphau.trafficgenerator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raphau.trafficgenerator.dao.EndpointRepository;
import com.raphau.trafficgenerator.dao.TestRepository;
import com.raphau.trafficgenerator.dto.ClientTestDTO;
import com.raphau.trafficgenerator.dto.RunTestDTO;
import com.raphau.trafficgenerator.entity.Endpoint;
import com.raphau.trafficgenerator.entity.Test;
import com.raphau.trafficgenerator.service.AsyncService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    private static Logger log = LoggerFactory.getLogger(TestController.class);
    private String testName;
    private String username = "user1";
    private List<ClientTestDTO> clientTestDTOList;
    private int numberOfUsers = 10;
    Map<String, Integer> numberOfRequests = new HashMap<>();
    Map<String, Long> databaseTime = new HashMap<>();
    Map<String, Long> applicationTime = new HashMap<>();
    Map<String, Long> apiTime = new HashMap<>();

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private EndpointRepository endpointRepository;

    @Autowired
    private TestRepository testRepository;

    @PostMapping("/runTest")
    @CrossOrigin(value = "*", maxAge = 3600)
    public void asyncTest(@RequestBody RunTestDTO runTestDTO) throws InterruptedException,  JSONException, JsonProcessingException {

        log.info("testAsync start");
        testName = "test full sell offers id 10";
        // RunTests
        clientTestDTOList = new ArrayList<>();
        for(int i = 0; i < runTestDTO.getNumberOfUsers(); i++){
            log.info("Register " + username + i);
            asyncService.runTests(username + i, clientTestDTOList, runTestDTO);
        }
        while(clientTestDTOList.size() < numberOfUsers);
        for(ClientTestDTO clientTestDTO: clientTestDTOList){
            for(Map.Entry<String, Integer> entry : clientTestDTO.getNumberOfRequests().entrySet()){
                Integer number = numberOfRequests.get(entry.getKey());
                if(number == null){
                    numberOfRequests.put(entry.getKey(), entry.getValue());
                    applicationTime.put(entry.getKey(), clientTestDTO.getSummaryEndpointTime().get(entry.getKey()));
                    databaseTime.put(entry.getKey(), clientTestDTO.getSummaryEndpointDatabaseTime().get(entry.getKey()));
                    apiTime.put(entry.getKey(), clientTestDTO.getSummaryApiTime().get(entry.getKey()));
                } else {
                    numberOfRequests.put(entry.getKey(), entry.getValue() + numberOfRequests.get(entry.getKey()));
                    applicationTime.put(entry.getKey(), clientTestDTO.getSummaryEndpointTime().get(entry.getKey()) + applicationTime.get(entry.getKey()));
                    databaseTime.put(entry.getKey(), clientTestDTO.getSummaryEndpointDatabaseTime().get(entry.getKey()) + databaseTime.get(entry.getKey()));
                    apiTime.put(entry.getKey(), clientTestDTO.getSummaryApiTime().get(entry.getKey()) + apiTime.get(entry.getKey()));
                }
            }
        }

        for(Map.Entry<String, Integer> entry : numberOfRequests.entrySet()){
            int numberOR = entry.getValue();
            long averageAppTime = applicationTime.get(entry.getKey()) / numberOR;
            long averageDBTime = databaseTime.get(entry.getKey()) / numberOR;
            long averageApiTime = apiTime.get(entry.getKey()) / numberOR;
            Endpoint endpoint = endpointRepository.findByEndpoint(entry.getKey()).get();
            Test test = new Test(0, endpoint, testName, numberOR, numberOfUsers, averageDBTime, averageApiTime, averageAppTime);
            testRepository.save(test);
        }

    }

    @GetMapping("/getTest")
    @CrossOrigin(value = "*", maxAge = 3600)
    public ResponseEntity<?> getTest(){
        List<Test> tests = testRepository.findAll();
        for(int i = 0; i < tests.size(); i++){
            log.info(tests.get(i).toString());
        }
        Map<String, Object> temp = new HashMap<>();
        temp.put("tests", tests);
        return ResponseEntity.ok(temp);
    }

}






















