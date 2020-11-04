package com.raphau.trafficgenerator.dto;

import java.math.BigDecimal;
import java.util.Date;

public class SellOffer {
    private int id;
    private Stock stock;
    private int startAmount;
    private int amount;
    private BigDecimal minPrice;
    private Date dateLimit;
    private boolean actual;

    public SellOffer(int id, Stock stock, int startAmount, int amount, BigDecimal minPrice, Date dateLimit, boolean actual) {
        this.id = id;
        this.stock = stock;
        this.startAmount = startAmount;
        this.amount = amount;
        this.minPrice = minPrice;
        this.dateLimit = dateLimit;
        this.actual = actual;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
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

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
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

    public SellOffer() {
    }
}
