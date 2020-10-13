package com.raphau.trafficgenerator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="endpoint")
public class Endpoint implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "method")
    private String method;

    @OneToMany(mappedBy = "endpoint", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Test> tests;

    public Endpoint() {
    }

    public Endpoint(int id, String endpoint, String method) {
        this.id = id;
        this.endpoint = endpoint;
        this.method = method;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "id=" + id +
                ", endpoint='" + endpoint + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }
}
