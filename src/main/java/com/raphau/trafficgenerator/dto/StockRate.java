package com.raphau.trafficgenerator.dto;


import java.util.Date;
import java.util.Objects;

public class StockRate {

    int id;
    Company company;
    double rate;
    Date date;
    boolean actual;

    public StockRate() {
    }

    public StockRate(int id, Company company, double rate, Date date, boolean actual) {
        this.id = id;
        this.company = company;
        this.rate = rate;
        this.date = date;
        this.actual = actual;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    @Override
    public String toString() {
        return "StockRate{" +
                "id=" + id +
                ", company=" + company +
                ", rate=" + rate +
                ", date=" + date +
                ", actual=" + actual +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockRate stockRate = (StockRate) o;
        return Objects.equals(company, stockRate.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company);
    }
}
