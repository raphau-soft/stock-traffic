package com.raphau.trafficgenerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.raphau.trafficgenerator.dto.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.*;


@Service
public class AsyncService {

    private static Logger log = LoggerFactory.getLogger(AsyncService.class);
    private HttpHeaders headers;

    // playing on stock strategy
    private final int RAND_EXPENSIVE_ONE_COMP = 0;
    private final int RAND_RANDOM_MANY_COMP = 1;
    private final int RAND_CHEAP_MANY_COMP = 2;
    private final String SIGNIN = "signin";
    private final String SIGNUP = "signup";
    private final String BUYOFFER = "buyOffer";
    private final String COMPANIES = "companies";
    private final String COMPANY = "company";
    private final String SELLOFFER = "sellOffer";
    private final String STOCKRATES = "stockRates";
    // private final String TRANSACTIONS = "transactions";
    private final String USER_RESOURCES = "user/resources";
    private final String USER = "user";
    private final String USER_BUYOFFERS = "user/buyOffers";
    private final String USER_BUYOFFERS_D = "user/buyOffers/id";
    private final String USER_SELLOFFERS = "user/sellOffers";
    private final String USER_SELLOFFERS_D = "user/sellOffers/ID";
    private final String api = "http://172.20.0.2:8080/";
    // private final String USER_LOGIN = "user/login";

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Async("asyncExecutor")
    public void runTests(String username, List<ClientTestDTO> clientTestDTOList, RunTestDTO runTestDTO) throws JSONException, JsonProcessingException, InterruptedException {
        // try to register users
        postRegistration(username);
        // try to login users
        UserLogin userLogin = login(username);
        log.info("Logged in ----> " + userLogin.getUsername());
        ClientTestDTO clientTestDTO = new ClientTestDTO();
        double random = Math.random();
        if (random <= runTestDTO.getStockPlay()) {
            for(;;) {
                log.info("Playing on stock - " + username);
                random = Math.random();
                if (random <= runTestDTO.getCreateCompany()) {
                    log.info("Create a company - " + username);
                    createCompany(userLogin.getJwt(), clientTestDTO);
                } else if (random > runTestDTO.getCreateCompany() && random <= runTestDTO.getCreateSellOffer() + runTestDTO.getCreateCompany()) {
                    log.info("Create a sell offer - " + username);
                    strategyAddSellOffer(userLogin.getJwt(), runTestDTO.getStrategy(), clientTestDTO);
                } else if (random > runTestDTO.getCreateSellOffer() + runTestDTO.getCreateCompany() && random <= runTestDTO.getCreateBuyOffer() + runTestDTO.getCreateCompany() + runTestDTO.getCreateSellOffer()) {
                    log.info("Create a buy offer - " + username);
                    strategyAddBuyOffer(userLogin.getJwt(), runTestDTO.getStrategy(), clientTestDTO);
                } else if (random > runTestDTO.getCreateBuyOffer() + runTestDTO.getCreateCompany() + runTestDTO.getCreateSellOffer() && random <= runTestDTO.getCreateBuyOffer() + runTestDTO.getCreateCompany() + runTestDTO.getCreateSellOffer() + runTestDTO.getDeleteSellOffer()) {
                    log.info("USUNIECIE OFERTY SPRZEDAZY " + username);
                    deleteSellOffer(userLogin.getJwt(), clientTestDTO);
                } else {
                    log.info("USUNIECIE OFERTY KUPNA " + username);
                    deleteBuyOffer(userLogin.getJwt(), clientTestDTO);
                }
                Thread.sleep(runTestDTO.getTimeBetweenRequests());
                random = Math.random();
                if(random <= runTestDTO.getLogout()){
                    log.info("LOGOUT  " + username);
                    break;
                }
            }
        } else {
            for(;;) {
                log.info("SPRAWDZANIE DANYCH " + username);
                random = Math.random();
                if(random <= runTestDTO.getDataCheck()){
                    log.info("SPRAWDZANIE OFERT KUPNA " + username);
                    getBuyOffers(userLogin.getJwt(), clientTestDTO);
                } else if(random > runTestDTO.getCheckBuyOffers() && random <= runTestDTO.getCheckBuyOffers() + runTestDTO.getCheckSellOffers()){
                    log.info("SPRAWDZANIE OFERT SPRZEDAZY " + username);
                    getSellOffers(userLogin.getJwt(), clientTestDTO);
                } else {
                    log.info("SPRAWDZANIE SWOICH DANYCH " + username);
                    getUser(userLogin.getJwt(), clientTestDTO);
                }
                Thread.sleep(runTestDTO.getTimeBetweenRequests());
                random = Math.random();
                if(random <= runTestDTO.getLogout()){
                    log.info("LOGOUT  " + username);
                    break;
                }
            }
        }
        clientTestDTOList.add(clientTestDTO);
        log.info("" + clientTestDTOList.size());
    }

    public void deleteSellOffer(String jwt, ClientTestDTO clientTestDTO) throws JSONException {
        JSONObject jsonObject;
        Gson gson = new Gson();
        jsonObject = new JSONObject(getSellOffers(jwt, clientTestDTO));
        Type sellOfferListType = new TypeToken<ArrayList<SellOffer>>(){}.getType();
        List<SellOffer> sellOffers = gson.fromJson(jsonObject.get("sellOffers").toString(), sellOfferListType);
        if(sellOffers.size() == 0) return;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);
        int i = Math.abs(new Random().nextInt()) % sellOffers.size();
        long apiTime = System.currentTimeMillis();
        restTemplate.exchange(
                this.api + "api/user/sellOffers/" + sellOffers.get(i).getId(), HttpMethod.DELETE, entity, String.class, new Object());
        apiTime = System.currentTimeMillis() - apiTime;
        TestDetailsDTO testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(USER_SELLOFFERS_D, testDetailsDTO, apiTime);
    }

    public void deleteBuyOffer(String jwt, ClientTestDTO clientTestDTO) throws JSONException {
        JSONObject jsonObject;
        Gson gson = new Gson();
        jsonObject = new JSONObject(getBuyOffers(jwt, clientTestDTO));
        Type buyOfferListType = new TypeToken<ArrayList<BuyOffer>>(){}.getType();
        List<BuyOffer> buyOffers = gson.fromJson(jsonObject.get("buyOffers").toString(), buyOfferListType);
        if(buyOffers.size() == 0) return;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);
        int i = Math.abs(new Random().nextInt()) % buyOffers.size();
        long apiTime = System.currentTimeMillis();
        restTemplate.exchange(
                this.api + "api/user/buyOffers/" + buyOffers.get(i).getId(), HttpMethod.DELETE, entity, String.class, new Object());
        apiTime = System.currentTimeMillis() - apiTime;
        TestDetailsDTO testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(USER_BUYOFFERS_D, testDetailsDTO, apiTime);
    }

    private void postRegistration(String username) throws JSONException {
        log.info("Registration " + username + " starts");
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject;
        personJsonObject = new JSONObject();
        personJsonObject.put("id", 0);
        personJsonObject.put("name", "John");
        personJsonObject.put("surname", "Johnny");
        personJsonObject.put("username", username);
        personJsonObject.put("email", "mail@mail.pl");
        personJsonObject.put("password", "testpassword");
        HttpEntity<String> request = new HttpEntity<>(personJsonObject.toString(), headers);
        String message = "";
        try {
            message = restTemplate.postForObject(this.api + "api/auth/signup", request, String.class);
            log.info(message);
            log.info("Registration " + username + " ends");
        } catch (Exception e){
            log.info("Error  registration" + username);
        }
    }

    public UserLogin login(String username) throws JSONException {
        log.info("Login " + username + " starts");
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject;
        personJsonObject = new JSONObject();
        personJsonObject.put("username", username);
        personJsonObject.put("password", "testpassword");
        HttpEntity<String> request = new HttpEntity<>(personJsonObject.toString(), headers);
        UserLogin userLogin = null;
        try {
            userLogin = restTemplate.postForObject(this.api + "api/auth/signin", request, UserLogin.class);
            log.info("Login " + username + " ends");
        } catch (Exception e){
            log.info("Error login " + username);
        }
        return userLogin;
    }

    private String getUser(String jwt, ClientTestDTO clientTestDTO) throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        long apiTime = System.currentTimeMillis();
        try {
            jsonResponse = restTemplate.exchange(
                    this.api + "api/user", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        apiTime = System.currentTimeMillis() - apiTime;
        assert jsonResponse != null;
        jsonObject = new JSONObject((String) jsonResponse.getBody());
        TestDetailsDTO testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(USER, testDetailsDTO, apiTime);
        return (String) jsonResponse.getBody();
    }

    private String getBuyOffers(String jwt, ClientTestDTO clientTestDTO) throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        long apiTime = System.currentTimeMillis();
        try {
            jsonResponse = restTemplate.exchange(
                    this.api + "api/user/buyOffers", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        apiTime = System.currentTimeMillis() - apiTime;
        assert jsonResponse != null;
        jsonObject = new JSONObject((String) jsonResponse.getBody());
        TestDetailsDTO testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(USER_BUYOFFERS, testDetailsDTO, apiTime);
        return (String) jsonResponse.getBody();
    }

    private String getSellOffers(String jwt, ClientTestDTO clientTestDTO) throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        long apiTime = System.currentTimeMillis();
        try {
            jsonResponse = restTemplate.exchange(
                    this.api + "api/user/sellOffers", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        apiTime = System.currentTimeMillis() - apiTime;
        assert jsonResponse != null;
        jsonObject = new JSONObject((String) jsonResponse.getBody());
        TestDetailsDTO testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(USER_SELLOFFERS, testDetailsDTO, apiTime);
        return (String) jsonResponse.getBody();
    }

    private String getResources(String jwt, ClientTestDTO clientTestDTO) throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        long apiTime = System.currentTimeMillis();
        try {
            jsonResponse = restTemplate.exchange(
                    this.api + "api/user/resources", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        apiTime = System.currentTimeMillis() - apiTime;
        String response = null;
        if(jsonResponse != null) {
            response = (String) jsonResponse.getBody();
            jsonObject = new JSONObject(response);
            TestDetailsDTO testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
            clientTestDTO.addTestDetails(USER_RESOURCES, testDetailsDTO, apiTime);
        }
        return response;
    }

    private String getStockRates(String jwt, ClientTestDTO clientTestDTO) throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        long apiTime = System.currentTimeMillis();
        try {
            jsonResponse = restTemplate.exchange(
                    this.api + "stockRates", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        apiTime = System.currentTimeMillis() - apiTime;
        assert jsonResponse != null;
        jsonObject = new JSONObject((String) jsonResponse.getBody());
        TestDetailsDTO testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(STOCKRATES, testDetailsDTO, apiTime);
        return (String) jsonResponse.getBody();
    }

    private void createCompany(String jwt, ClientTestDTO clientTestDTO) throws JSONException {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        JSONObject companyJsonObject;
        companyJsonObject = new JSONObject();
        companyJsonObject.put("id", "0");
        // Generate company name
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 7;
        Random random = new Random();
        StringBuilder sb = new StringBuilder(targetStringLength);
        for(int i = 0; i < targetStringLength; i++){
            int randomLimitedInt = leftLimit + (int) (random.nextDouble() * (rightLimit - leftLimit + 1));
            sb.append((char) randomLimitedInt);
        }
        String name = sb.toString();
        // Generate stock amount/price
        int amount = Math.abs(new Random().nextInt() % 1500);
        double price = Math.round(new Random().nextDouble() * 10000) / 100.0;
        companyJsonObject.put("name", name);
        companyJsonObject.put("amount", amount);
        companyJsonObject.put("price", price);
        HttpEntity<String> request = new HttpEntity<>(companyJsonObject.toString(), headers);

        TestDetailsDTO testDetailsDTO = null;
        long apiTime = System.currentTimeMillis();
        try {
            testDetailsDTO = restTemplate.postForObject(this.api + "company", request, TestDetailsDTO.class);
        } catch (Exception e){
            log.info("Error " + e);
        }
        apiTime = System.currentTimeMillis() - apiTime;
        assert testDetailsDTO != null;
        log.info(testDetailsDTO.toString());
        clientTestDTO.addTestDetails(COMPANY, testDetailsDTO, apiTime);
    }

    private String getCompanies(String jwt, ClientTestDTO clientTestDTO) throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        long apiTime = System.currentTimeMillis();
        try {
            jsonResponse = restTemplate.exchange(
                    this.api + "companies", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        apiTime = System.currentTimeMillis() - apiTime;
        assert jsonResponse != null;
        jsonObject = new JSONObject((String) jsonResponse.getBody());
        TestDetailsDTO testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(COMPANIES, testDetailsDTO, apiTime);
        return (String) jsonResponse.getBody();
    }

    private void strategyAddBuyOffer(String jwt, int strategy, ClientTestDTO clientTestDTO) throws JSONException, JsonProcessingException {
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(getUser(jwt, clientTestDTO));
        User user = gson.fromJson(jsonObject.get("user").toString(), User.class);
        jsonObject = new JSONObject(getCompanies(jwt, clientTestDTO));
        Type companyListType = new TypeToken<ArrayList<Company>>(){}.getType();
        List<Company> companies = gson.fromJson(jsonObject.get("company").toString(), companyListType);
        double price;
        int amount;
        switch(strategy){
            case RAND_CHEAP_MANY_COMP:

                break;
            case RAND_EXPENSIVE_ONE_COMP:
                // buying stock of one company logic
                price = Math.random() * 100.f % (user.getMoney()/4.f) * 100;
                price = Math.round(price)/100.0;
                amount = (int) Math.round(Math.random() * 100.f % (user.getMoney() / price / 2));
                int companyId = companies.get((int) Math.round(Math.random() * 100.f % (companies.size() - 1))).getId();
                createBuyOffer(jwt, companyId, amount, price, clientTestDTO);
                break;
            case RAND_RANDOM_MANY_COMP:
                // buying stocks of many companies
                jsonObject = new JSONObject(getStockRates(jwt, clientTestDTO));
                Type stockRateListType = new TypeToken<ArrayList<StockRate>>(){}.getType();
                List<StockRate> stockRates = gson.fromJson(jsonObject.get("stockRate").toString(), stockRateListType);
                int amountOfCompanies = Math.abs(new Random().nextInt() % (stockRates.size() / 3)) + 1;
                for(int i = 0; i < amountOfCompanies; i++){
                    int companyNum = Math.abs(new Random().nextInt() % stockRates.size());
                    StockRate stockRate = stockRates.get(companyNum);
                    Company company = stockRate.getCompany();
                    double rate = stockRate.getRate();
                    // price from 90% to 120% of rate (I think so)
                    price = Math.round((Math.abs(new Random().nextDouble()) % (rate * 0.3) + rate * 0.9) * 100) / 100.f;
                    amount = (int) Math.round(Math.random() * 100.f % (user.getMoney() / price / 10));
                    log.info("Price: " + price + " Money: " + user.getMoney() + "Amount: " + amount);
                    createBuyOffer(jwt, company.getId(), amount, price, clientTestDTO);
                    stockRates.remove(companyNum);
                }
                break;
        }
    }

    private void strategyAddSellOffer(String jwt, int strategy, ClientTestDTO clientTestDTO) throws JSONException, JsonProcessingException {
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(getUser(jwt, clientTestDTO));
        User user = gson.fromJson(jsonObject.get("user").toString(), User.class);
        //log.info("@@@@@@@ RESOURCES: " + getResources(jwt));
        String resources = getResources(jwt, clientTestDTO);
        if(resources == null) return;
        jsonObject = new JSONObject(resources);
        log.info("Resources: " + resources);
        Type stockListType = new TypeToken<ArrayList<Stock>>(){}.getType();
        List<Stock> stocks = gson.fromJson(jsonObject.get("stock").toString(), stockListType);
        if(stocks.size() <= 0) return;
        int stockNum;
        switch(strategy){
            case RAND_CHEAP_MANY_COMP:

                break;
            case RAND_EXPENSIVE_ONE_COMP:
                // buying stock of one company logic
                stockNum = Math.abs(new Random().nextInt() % stocks.size());
                log.info("Sekundy przed katastrofą: " + stockNum + "/" + stocks.size());
                double price = Math.random() * 100.f % 10000;
                price = Math.round(price)/100.0;
                int amount = (int) Math.round(Math.random() * 100.f % (stocks.get(stockNum).getAmount())) + 1;
                log.info("Cena: " + price + " Ilość:" + amount + " Faktyczna ilość: " + stocks.get(stockNum).getAmount());
                int companyId = stocks.get(stockNum).getCompany().getId();
                createSellOffer(jwt, companyId, amount, price, clientTestDTO);
                break;
            case RAND_RANDOM_MANY_COMP:
                // sell stocks of many companies
                jsonObject = new JSONObject(getStockRates(jwt, clientTestDTO));
                Type stockRateListType = new TypeToken<ArrayList<StockRate>>(){}.getType();
                List<StockRate> stockRates = gson.fromJson(jsonObject.get("stockRate").toString(), stockRateListType);
                int amountOfStocks = Math.abs(new Random().nextInt() % stocks.size())/3 + 1;
                for(int i = 0; i < amountOfStocks; i++){
                    stockNum = Math.abs(new Random().nextInt() % stocks.size());
                    log.info("Stocks size: " + stocks.size() + " stockNum: " + stockNum);
                    Stock stock = stocks.get(stockNum);
                    Company company = stock.getCompany();
                    StockRate stockRateTemp = new StockRate();
                    stockRateTemp.setCompany(company);
                    int stockRateNum = stockRates.indexOf(stockRateTemp);
                    log.info("StocksRate size: " + stockRates.size() + " stockRateNum: " + stockRateNum);
                    StockRate stockRate = stockRates.get(stockRateNum);
                    double rate = stockRate.getRate();
                    // price from 80% to 110% of rate (I think so)
                    price = Math.round((Math.abs(new Random().nextDouble()) % (rate * 0.3) + rate * 0.8)*100)/100.f;
                    amount = (int) Math.round(Math.random() * 100.f % (user.getMoney() / price / 10));
                    createSellOffer(jwt, company.getId(), amount, price, clientTestDTO);
                    stocks.remove(stockNum);
                }
                break;
        }
    }

    private void createBuyOffer(String jwt, int companyId, int amount, double price, ClientTestDTO clientTestDTO) throws JSONException {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        JSONObject companyJsonObject;
        companyJsonObject = new JSONObject();
        companyJsonObject.put("id", "0");
        companyJsonObject.put("company_id", companyId);
        companyJsonObject.put("maxPrice", price);
        companyJsonObject.put("amount", amount);
        companyJsonObject.put("dateLimit", "2014-05-09T00:48:16-04:00");
        HttpEntity<String> request = new HttpEntity<>(companyJsonObject.toString(), headers);
        log.info("CREATING A BUY OFFER");
        TestDetailsDTO testDetailsDTO = null;
        long apiTime = System.currentTimeMillis();
        try {
            testDetailsDTO = restTemplate.postForObject( this.api + "api/buyOffer", request, TestDetailsDTO.class);
        } catch (Exception e){
            log.info("Error " + e);
        }
        apiTime = System.currentTimeMillis() - apiTime;
        if(testDetailsDTO !=null)
            clientTestDTO.addTestDetails(BUYOFFER, testDetailsDTO, apiTime);
    }

    private void createSellOffer(String jwt, int companyId, int amount, double price, ClientTestDTO clientTestDTO) throws JSONException {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        JSONObject companyJsonObject;
        companyJsonObject = new JSONObject();
        companyJsonObject.put("id", "0");
        companyJsonObject.put("company_id", companyId);
        companyJsonObject.put("minPrice", price);
        companyJsonObject.put("amount", amount);
        companyJsonObject.put("dateLimit", "2014-05-09T00:48:16-04:00");
        HttpEntity<String> request = new HttpEntity<>(companyJsonObject.toString(), headers);
        log.info("CREATING A SELL OFFER");
        TestDetailsDTO testDetailsDTO = null;
        long apiTime = System.currentTimeMillis();
        try {
            testDetailsDTO = restTemplate.postForObject(this.api + "api/sellOffer", request, TestDetailsDTO.class);
        } catch (Exception e){
            log.info("Error " + e);
        }
        apiTime = System.currentTimeMillis() - apiTime;
        if(testDetailsDTO != null)
            clientTestDTO.addTestDetails(SELLOFFER, testDetailsDTO, apiTime);
    }
}




















