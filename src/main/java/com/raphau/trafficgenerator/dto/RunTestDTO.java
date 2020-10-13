package com.raphau.trafficgenerator.dto;

public class RunTestDTO {

    private long timeBetweenRequests;
    private long numberOfUsers;
    private double stockPlay;
    private double createCompany;
    private double createBuyOffer;
    private double createSellOffer;
    private double deleteSellOffer;
    private double deleteBuyOffer;
    private double continueStockPlaying;
    private double logout;
    private double dataCheck;
    private double checkBuyOffers;
    private double checkSellOffers;
    private double checkUserData;
    private double continueDataChecking;
    private int strategy;

    public RunTestDTO() {
    }

    public RunTestDTO(long timeBetweenRequests, long numberOfUsers, double stockPlay, double createCompany, double createBuyOffer, double createSellOffer, double deleteSellOffer, double deleteBuyOffer, double continueStockPlaying, double logout, double dataCheck, double checkBuyOffers, double checkSellOffers, double checkUserData, double continueDataChecking, int strategy) {
        this.timeBetweenRequests = timeBetweenRequests;
        this.numberOfUsers = numberOfUsers;
        this.stockPlay = stockPlay;
        this.createCompany = createCompany;
        this.createBuyOffer = createBuyOffer;
        this.createSellOffer = createSellOffer;
        this.deleteSellOffer = deleteSellOffer;
        this.deleteBuyOffer = deleteBuyOffer;
        this.continueStockPlaying = continueStockPlaying;
        this.logout = logout;
        this.dataCheck = dataCheck;
        this.checkBuyOffers = checkBuyOffers;
        this.checkSellOffers = checkSellOffers;
        this.checkUserData = checkUserData;
        this.continueDataChecking = continueDataChecking;
        this.strategy = strategy;
    }

    public long getTimeBetweenRequests() {
        return timeBetweenRequests;
    }

    public void setTimeBetweenRequests(long timeBetweenRequests) {
        this.timeBetweenRequests = timeBetweenRequests;
    }

    public long getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(long numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public double getStockPlay() {
        return stockPlay;
    }

    public void setStockPlay(double stockPlay) {
        this.stockPlay = stockPlay;
    }

    public double getCreateCompany() {
        return createCompany;
    }

    public void setCreateCompany(double createCompany) {
        this.createCompany = createCompany;
    }

    public double getCreateBuyOffer() {
        return createBuyOffer;
    }

    public void setCreateBuyOffer(double createBuyOffer) {
        this.createBuyOffer = createBuyOffer;
    }

    public double getCreateSellOffer() {
        return createSellOffer;
    }

    public void setCreateSellOffer(double createSellOffer) {
        this.createSellOffer = createSellOffer;
    }

    public double getDeleteSellOffer() {
        return deleteSellOffer;
    }

    public void setDeleteSellOffer(double deleteSellOffer) {
        this.deleteSellOffer = deleteSellOffer;
    }

    public double getDeleteBuyOffer() {
        return deleteBuyOffer;
    }

    public void setDeleteBuyOffer(double deleteBuyOffer) {
        this.deleteBuyOffer = deleteBuyOffer;
    }

    public double getContinueStockPlaying() {
        return continueStockPlaying;
    }

    public void setContinueStockPlaying(double continueStockPlaying) {
        this.continueStockPlaying = continueStockPlaying;
    }

    public double getLogout() {
        return logout;
    }

    public void setLogout(double logout) {
        this.logout = logout;
    }

    public double getDataCheck() {
        return dataCheck;
    }

    public void setDataCheck(double dataCheck) {
        this.dataCheck = dataCheck;
    }

    public double getCheckBuyOffers() {
        return checkBuyOffers;
    }

    public void setCheckBuyOffers(double checkBuyOffers) {
        this.checkBuyOffers = checkBuyOffers;
    }

    public double getCheckSellOffers() {
        return checkSellOffers;
    }

    public void setCheckSellOffers(double checkSellOffers) {
        this.checkSellOffers = checkSellOffers;
    }

    public double getCheckUserData() {
        return checkUserData;
    }

    public void setCheckUserData(double checkUserData) {
        this.checkUserData = checkUserData;
    }

    public double getContinueDataChecking() {
        return continueDataChecking;
    }

    public void setContinueDataChecking(double continueDataChecking) {
        this.continueDataChecking = continueDataChecking;
    }

    public int getStrategy() {
        return strategy;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }
}
