package com.raphau.trafficgenerator.dto;

import com.raphau.trafficgenerator.entity.Endpoint;

import java.util.HashMap;
import java.util.Map;

public class ClientTestDTO {

    Map<String, Integer> numberOfRequests = new HashMap<>();
    Map<String, Long> summaryEndpointDatabaseTime = new HashMap<>();
    Map<String, Long> summaryEndpointTime = new HashMap<>();
    Map<String, Long> summaryApiTime = new HashMap<>();

    public ClientTestDTO(Map<String, Integer> numberOfRequests, Map<String, Long> summaryEndpointDatabaseTime, Map<String, Long> summaryEndpointTime, Map<String, Long> summaryApiTime) {
        this.numberOfRequests = numberOfRequests;
        this.summaryEndpointDatabaseTime = summaryEndpointDatabaseTime;
        this.summaryEndpointTime = summaryEndpointTime;
        this.summaryApiTime = summaryApiTime;
    }

    public ClientTestDTO() {
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

    public Map<String, Long> getSummaryApiTime() {
        return summaryApiTime;
    }

    public void setSummaryApiTime(Map<String, Long> summaryApiTime) {
        this.summaryApiTime = summaryApiTime;
    }

    @Override
    public String toString() {
        return "ClientTestDTO{" +
                "numberOfRequests=" + numberOfRequests +
                ", summaryEndpointDatabaseTime=" + summaryEndpointDatabaseTime +
                ", summaryEndpointTime=" + summaryEndpointTime +
                ", summaryApiTime=" + summaryApiTime +
                '}';
    }

    public void addTestDetails(String endpoint, TestDetailsDTO testDetailsDTO, long apiTime) {
        Integer number = numberOfRequests.get(endpoint);
        Long prevSummaryEndpointTime = summaryEndpointTime.get(endpoint);
        Long prevSummaryEndpointDatabaseTime = summaryEndpointDatabaseTime.get(endpoint);
        Long prevSummaryApiTime = summaryApiTime.get(endpoint);
        if(prevSummaryEndpointTime == null){
            numberOfRequests.put(endpoint, 1);
            summaryEndpointTime.put(endpoint, testDetailsDTO.getApplicationTime());
            summaryEndpointDatabaseTime.put(endpoint, testDetailsDTO.getDatabaseTime());
            summaryApiTime.put(endpoint, apiTime);
        } else {
            numberOfRequests.put(endpoint, ++number);
            summaryEndpointTime.put(endpoint, testDetailsDTO.getApplicationTime() + prevSummaryEndpointTime);
            summaryEndpointDatabaseTime.put(endpoint, testDetailsDTO.getDatabaseTime() + prevSummaryEndpointDatabaseTime);
            summaryApiTime.put(endpoint, prevSummaryApiTime + apiTime);
        }
    }
}
