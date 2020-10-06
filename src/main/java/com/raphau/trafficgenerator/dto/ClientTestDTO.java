package com.raphau.trafficgenerator.dto;

import com.raphau.trafficgenerator.entity.Endpoint;

import java.util.HashMap;
import java.util.Map;

public class ClientTestDTO {

    Map<String, Integer> numberOfRequests;
    Map<String, Long> summaryEndpointDatabaseTime;
    Map<String, Long> summaryEndpointTime;

    public ClientTestDTO(Map<String, Integer> numberOfRequests, Map<String, Long> summaryEndpointDatabaseTime, Map<String, Long> summaryEndpointTime) {
        this.numberOfRequests = numberOfRequests;
        this.summaryEndpointDatabaseTime = summaryEndpointDatabaseTime;
        this.summaryEndpointTime = summaryEndpointTime;
    }

    public ClientTestDTO() {
        numberOfRequests = new HashMap<>();
        summaryEndpointDatabaseTime = new HashMap<>();
        summaryEndpointTime = new HashMap<>();
    }

    public Map<String, Integer> getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(Map<String, Integer> numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public Map<String, Long> getSummaryEndpointDatabaseTime() {
        return summaryEndpointDatabaseTime;
    }

    public void setSummaryEndpointDatabaseTime(Map<String, Long> averageEndpointDatabaseTime) {
        this.summaryEndpointDatabaseTime = averageEndpointDatabaseTime;
    }

    public Map<String, Long> getSummaryEndpointTime() {
        return summaryEndpointTime;
    }

    public void setSummaryEndpointTime(Map<String, Long> averageEndpointTime) {
        this.summaryEndpointTime = averageEndpointTime;
    }

    @Override
    public String toString() {
        return "ClientTestDTO{" +
                "numberOfRequests=" + numberOfRequests +
                ", summaryEndpointDatabaseTime=" + summaryEndpointDatabaseTime +
                ", summaryEndpointTime=" + summaryEndpointTime +
                '}';
    }

    public void addTestDetails(String endpoint, TestDetailsDTO testDetailsDTO) {
        Integer number = numberOfRequests.get(endpoint);
        Long prevSummaryEndpointTime = summaryEndpointTime.get(endpoint);
        Long prevSummaryEndpointDatabaseTime = summaryEndpointDatabaseTime.get(endpoint);
        if(prevSummaryEndpointTime == null){
            numberOfRequests.put(endpoint, 1);
            summaryEndpointTime.put(endpoint, testDetailsDTO.getApplicationTime());
            summaryEndpointDatabaseTime.put(endpoint, testDetailsDTO.getDatabaseTime());
        } else {
            numberOfRequests.put(endpoint, ++number);
            summaryEndpointTime.put(endpoint, testDetailsDTO.getApplicationTime() + prevSummaryEndpointTime);
            summaryEndpointDatabaseTime.put(endpoint, testDetailsDTO.getDatabaseTime() + prevSummaryEndpointDatabaseTime);
        }
    }
}
