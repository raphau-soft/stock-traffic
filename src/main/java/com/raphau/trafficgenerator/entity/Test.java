package com.raphau.trafficgenerator.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="test", schema="traffic_generator")
public class Test implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @ManyToOne(targetEntity = Endpoint.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "endpoint_id", nullable = false)
    private Endpoint endpoint;

    @Column(name = "name")
    private String name;

    @Column(name = "number_of_requests")
    private int numberOfRequests;

    @Column(name = "number_of_users")
    private int numberOfUsers;

    @Column(name = "database_time")
    private long databaseTime;

    @Column(name = "api_time")
    private long apiTime;

    @Column(name = "application_time")
    private long applicationTime;

    public Test() {
    }

    public Test(int id, Endpoint endpoint, String name, int numberOfRequests, int numberOfUsers, long databaseTime, long apiTime, long applicationTime) {
        this.id = id;
        this.endpoint = endpoint;
        this.name = name;
        this.numberOfRequests = numberOfRequests;
        this.numberOfUsers = numberOfUsers;
        this.databaseTime = databaseTime;
        this.apiTime = apiTime;
        this.applicationTime = applicationTime;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", numberOfRequests=" + numberOfRequests +
                ", numberOfUsers=" + numberOfUsers +
                ", databaseTime=" + databaseTime +
                ", apiTime=" + apiTime +
                ", applicationTime=" + applicationTime +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public long getDatabaseTime() {
        return databaseTime;
    }

    public void setDatabaseTime(long databaseTime) {
        this.databaseTime = databaseTime;
    }

    public long getApiTime() {
        return apiTime;
    }

    public void setApiTime(long apiTime) {
        this.apiTime = apiTime;
    }

    public long getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(long applicationTime) {
        this.applicationTime = applicationTime;
    }
}
