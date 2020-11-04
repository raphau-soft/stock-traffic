package com.raphau.trafficgenerator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raphau.trafficgenerator.dao.EndpointRepository;
import com.raphau.trafficgenerator.dao.TestRepository;
import com.raphau.trafficgenerator.dto.ClientTestDTO;
import com.raphau.trafficgenerator.dto.RunTestDTO;
import com.raphau.trafficgenerator.dto.UserLogin;
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
@CrossOrigin(value = "*", maxAge = 3600)
public class TestController {

    private static Logger log = LoggerFactory.getLogger(TestController.class);
    private RunTestDTO runTestDTO = new RunTestDTO(20, 50, 0.9, 0.02, 0.44, 0.44, 0.05, 0.05, 0.9, 0.1, 0.1, 0.33, 0.33, 0.34, 0.9, 1);
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
    public void asyncTest(@RequestBody String name) throws Exception {

        log.info("testAsync start");
        log.info("Number of users " +  runTestDTO.getNumberOfUsers());
        if(testRepository.findAllByName(name).length != 0){
            throw new Exception();
        }

        // RunTests
        List<ClientTestDTO> clientTestDTOList = new ArrayList<>();
        for(int i = 0; i < runTestDTO.getNumberOfUsers(); i++){
            log.info("Register " +  i);
            asyncService.runTests(""+i, clientTestDTOList, runTestDTO);
        }
        while(clientTestDTOList.size() < runTestDTO.getNumberOfUsers());
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
            Test test = new Test(0, endpoint, name, numberOR, (int) runTestDTO.getNumberOfUsers(), averageDBTime, averageApiTime, averageAppTime);
            testRepository.save(test);
        }

    }

    @GetMapping("/getTest")
    public ResponseEntity<?> getTest(){
        List<Test> tests = testRepository.findAll();
        for(int i = 0; i < tests.size(); i++){
            log.info(tests.get(i).toString());
        }
        Map<String, Object> temp = new HashMap<>();
        temp.put("tests", tests);
        return ResponseEntity.ok(temp);
    }

    @PostMapping("/cleanDB")
    public void cleanDB(){
        testRepository.deleteAll();
    }

    @PostMapping("/setConf")
    public void setConf(@RequestBody RunTestDTO runTestDTO){
        this.runTestDTO = runTestDTO;
    }

    @GetMapping("/getConf")
    public ResponseEntity<?> getConf(){
        Map<String, Object> temp = new HashMap<>();
        temp.put("conf", this.runTestDTO);
        return ResponseEntity.ok(temp);
    }

}






















