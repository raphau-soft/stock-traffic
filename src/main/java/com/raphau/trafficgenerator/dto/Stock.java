package com.raphau.trafficgenerator.dto;

public class Stock {

    int id;
    User user;
    Company company;
    int amount;

    public Stock() {
    }

    public Stock(int id, User user, Company company, int amount) {
        this.id = id;
        this.user = user;
        this.company = company;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", user=" + user +
                ", company=" + company +
                ", amount=" + amount +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
