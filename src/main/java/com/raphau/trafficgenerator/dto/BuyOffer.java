package com.raphau.trafficgenerator.dto;

import java.math.BigDecimal;
import java.util.Date;

public class BuyOffer {
    private int id;
    private Company company;
    private User user;
    private BigDecimal maxPrice;
    private int startAmount;
    private int amount;
    private Date dateLimit;
    private boolean actual;

    public BuyOffer(int id, Company company, User user, BigDecimal maxPrice, int startAmount, int amount, Date dateLimit, boolean actual) {
        this.id = id;
        this.company = company;
        this.user = user;
        this.maxPrice = maxPrice;
        this.startAmount = startAmount;
        this.amount = amount;
        this.dateLimit = dateLimit;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(int startAmount) {
        this.startAmount = startAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(Date dateLimit) {
        this.dateLimit = dateLimit;
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    public BuyOffer() {
    }
}
