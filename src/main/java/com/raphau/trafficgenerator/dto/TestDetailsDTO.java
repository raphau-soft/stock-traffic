package com.raphau.trafficgenerator.dto;

public class TestDetailsDTO {

    private long databaseTime;
    private long applicationTime;

    public TestDetailsDTO() {
    }

    public TestDetailsDTO(long databaseTime, long applicationTime) {
        this.databaseTime = databaseTime;
        this.applicationTime = applicationTime;
    }

    public long getDatabaseTime() {
        return databaseTime;
    }

    public void setDatabaseTime(long databaseTime) {
        this.databaseTime = databaseTime;
    }

    public long getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(long applicationTime) {
        this.applicationTime = applicationTime;
    }

    @Override
    public String toString() {
        return "TestDetailsDTO{" +
                "databaseTime=" + databaseTime +
                ", applicationTime=" + applicationTime +
                '}';
    }
}
