package com.raphau.trafficgenerator.entity;

import javax.persistence.*;

@Entity
@Table(name="test", schema="traffic_generator")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

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

}
