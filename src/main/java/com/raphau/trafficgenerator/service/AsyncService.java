package com.raphau.trafficgenerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raphau.trafficgenerator.dao.EndpointRepository;
import com.raphau.trafficgenerator.dto.*;
import com.raphau.trafficgenerator.entity.Endpoint;
import com.raphau.trafficgenerator.entity.Test;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Service
public class AsyncService {

    private static Logger log = LoggerFactory.getLogger(AsyncService.class);
    private HttpHeaders headers;
    // playing on stock
    private double STOCK_PLAY = 0;
    private double CREATE_A_COMPANY = 0.02;
    private double CREATE_A_BUY_OFFER = 0.44;
    private double CREATE_A_SELL_OFFER = 0.44;
    private double DELETE_A_SELL_OFFER = 0.05;
    private double DELETE_A_BUY_OFFER = 0.05;
    private double CONTINUE_STOCK_PLAY = 0.85;
    private double LOGOUT = 0.15;
    // checking data
    private double DATA_CHECK = 1;
    private double CHECK_BUY_OFFERS = 0;
    private double CHECK_SELL_OFFERS = 1;
    private double CHECK_USER_DATA = 0;
    private double CONTINUE_DATA_CHECK = 0.85;
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
    // private final String STOCKRATES = "stockRates";
    // private final String TRANSACTIONS = "transactions";
    private final String USER_RESOURCES = "user/resources";
    private final String USER = "user";
    private final String USER_BUYOFFERS = "user/buyOffers";
    private final String USER_BUYOFFERS_D = "user/buyOffers/id";
    private final String USER_SELLOFFERS = "user/sellOffers";
    private final String USER_SELLOFFERS_D = "user/sellOffers/ID";
    // private final String USER_LOGIN = "user/login";
    private static int strategy = 1;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EndpointRepository endpointRepository;

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Async("asyncExecutor")
    public void runTests(String username, List<ClientTestDTO> clientTestDTOList) throws JSONException, JsonProcessingException, InterruptedException {
        // try to register users
        postRegistration(username);
        // try to login users
        UserLogin userLogin = login(username);
        log.info("Logged in ----> " + userLogin.getUsername());
        ClientTestDTO clientTestDTO = new ClientTestDTO();
        TestDetailsDTO testDetailsDTO;
        double random = Math.random();
        if (random <= STOCK_PLAY) {
            for(;;) {
                log.info("GRANIE NA GIELDZIE " + username);
                random = Math.random();
                if (random <= CREATE_A_COMPANY) {
                    log.info("TWORZENIE FIRMY " + username);
                    testDetailsDTO = createCompany(userLogin.getJwt());
                    clientTestDTO.addTestDetails(COMPANY, testDetailsDTO);
                } else if (random > CREATE_A_COMPANY && random <= CREATE_A_SELL_OFFER + CREATE_A_COMPANY) {
                    log.info("TWORZENIE OFERTY SPRZEDAZY " + username);
                    strategyAddSellOffer(userLogin.getJwt(), strategy, clientTestDTO);
                } else if (random > CREATE_A_SELL_OFFER + CREATE_A_COMPANY && random <= CREATE_A_BUY_OFFER + CREATE_A_COMPANY + CREATE_A_SELL_OFFER) {
                    log.info("TWORZENIE OFERTY KUPNA " + username);
                    strategyAddBuyOffer(userLogin.getJwt(), strategy, clientTestDTO);
                } else if (random > CREATE_A_BUY_OFFER + CREATE_A_COMPANY + CREATE_A_SELL_OFFER && random <= CREATE_A_BUY_OFFER + CREATE_A_COMPANY + CREATE_A_SELL_OFFER + DELETE_A_SELL_OFFER) {
                    log.info("USUNIECIE OFERTY SPRZEDAZY " + username);
                } else {
                    log.info("USUNIECIE OFERTY KUPNA " + username);
                }
                random = Math.random();
                if(random <= LOGOUT){
                    log.info("LOGOUT  " + username);
                    break;
                }
            }
        } else {
            Gson gson = new Gson();
            JSONObject jsonObject;
            for(;;) {
                log.info("SPRAWDZANIE DANYCH " + username);
                random = Math.random();
                if(random <= CHECK_BUY_OFFERS){
                    log.info("SPRAWDZANIE OFERT KUPNA " + username);
                    jsonObject = new JSONObject(getBuyOffers(userLogin.getJwt()));
                    testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
                    clientTestDTO.addTestDetails(USER_BUYOFFERS, testDetailsDTO);
                } else if(random > CHECK_BUY_OFFERS && random <= CHECK_BUY_OFFERS + CHECK_SELL_OFFERS){
                    log.info("SPRAWDZANIE OFERT SPRZEDAZY " + username);
                    jsonObject = new JSONObject(getSellOffers(userLogin.getJwt()));
                    testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
                    clientTestDTO.addTestDetails(USER_SELLOFFERS, testDetailsDTO);
                } else {
                    log.info("SPRAWDZANIE SWOICH DANYCH " + username);
                    jsonObject = new JSONObject(getUser(userLogin.getJwt()));
                    testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
                    clientTestDTO.addTestDetails(USER, testDetailsDTO);
                }
                random = Math.random();
                if(random <= LOGOUT){
                    log.info("LOGOUT  " + username);
                    break;
                }
            }
        }
        clientTestDTOList.add(clientTestDTO);
        log.info("Client list size: " + clientTestDTOList.size());
    }



    private void postRegistration(String username) throws InterruptedException, JSONException {
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
            message = restTemplate.postForObject("http://172.20.0.2:8080/api/auth/signup", request, String.class);
            log.info(message);
            log.info("Registration " + username + " ends");
        } catch (Exception e){
            log.info("Error  registration" + username);
        }
    }

    private UserLogin login(String username) throws JSONException {
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
            userLogin = restTemplate.postForObject("http://172.20.0.2:8080/api/auth/signin", request, UserLogin.class);
            log.info("Login " + username + " ends");
        } catch (Exception e){
            log.info("Error login " + username);
        }
        return userLogin;
    }

    private String getUser(String jwt) throws JSONException, JsonProcessingException {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        try {
            jsonResponse = restTemplate.exchange(
                    "http://172.20.0.2:8080/api/user", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        assert jsonResponse != null;
        return (String) jsonResponse.getBody();
    }

    private String getBuyOffers(String jwt) {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        try {
            jsonResponse = restTemplate.exchange(
                    "http://172.20.0.2:8080/api/user/buyOffers", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        assert jsonResponse != null;
        return (String) jsonResponse.getBody();
    }

    private String getSellOffers(String jwt) {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        try {
            jsonResponse = restTemplate.exchange(
                    "http://172.20.0.2:8080/api/user/sellOffers", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        assert jsonResponse != null;
        return (String) jsonResponse.getBody();
    }

    private String getResources(String jwt) {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        try {
            jsonResponse = restTemplate.exchange(
                    "http://172.20.0.2:8080/api/user/resources", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        String response = null;
        if(jsonResponse != null)
            response = (String) jsonResponse.getBody();
        return response;
    }

    private String getStockRates(String jwt){
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        try {
            jsonResponse = restTemplate.exchange(
                    "http://172.20.0.2:8080/stockRates", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        assert jsonResponse != null;
        return (String) jsonResponse.getBody();
    }

    private TestDetailsDTO createCompany(String jwt) throws JSONException {
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
        try {
            testDetailsDTO = restTemplate.postForObject("http://172.20.0.2:8080/company", request, TestDetailsDTO.class);
        } catch (Exception e){
            log.info("Error " + e);
        }
        assert testDetailsDTO != null;
        log.info(testDetailsDTO.toString());
        return testDetailsDTO;
    }

    private String getCompanies(String jwt){
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity jsonResponse = null;
        try {
            jsonResponse = restTemplate.exchange(
                    "http://172.20.0.2:8080/companies", HttpMethod.GET, entity, String.class, new Object());
        } catch (Exception e){
            log.info("Error " + e);
        }
        assert jsonResponse != null;
        return (String) jsonResponse.getBody();
    }

    private void strategyAddBuyOffer(String jwt, int strategy, ClientTestDTO clientTestDTO) throws JSONException, JsonProcessingException {
        TestDetailsDTO testDetailsDTO;
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(getUser(jwt));
        User user = gson.fromJson(jsonObject.get("user").toString(), User.class);
        testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(USER, testDetailsDTO);
        jsonObject = new JSONObject(getCompanies(jwt));
        testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(COMPANIES, testDetailsDTO);
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
                testDetailsDTO = createBuyOffer(jwt, companyId, amount, price);
                if(testDetailsDTO != null)
                    clientTestDTO.addTestDetails(BUYOFFER, testDetailsDTO);
                break;
            case RAND_RANDOM_MANY_COMP:
                // buying stocks of many companies
                jsonObject = new JSONObject(getStockRates(jwt));
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
                    testDetailsDTO = createBuyOffer(jwt, company.getId(), amount, price);
                    if(testDetailsDTO != null) {
                        clientTestDTO.addTestDetails(BUYOFFER, testDetailsDTO);
                        log.info("Database time: " + testDetailsDTO.getDatabaseTime());
                    }
                    stockRates.remove(companyNum);
                }
                break;
        }
    }

    private void strategyAddSellOffer(String jwt, int strategy, ClientTestDTO clientTestDTO) throws JSONException, JsonProcessingException {
        TestDetailsDTO testDetailsDTO;
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(getUser(jwt));
        User user = gson.fromJson(jsonObject.get("user").toString(), User.class);
        testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(USER, testDetailsDTO);
        //log.info("@@@@@@@ RESOURCES: " + getResources(jwt));
        String resources = getResources(jwt);
        jsonObject = new JSONObject(resources);
        log.info("Resources: " + resources);
        testDetailsDTO = gson.fromJson(jsonObject.get("testDetails").toString(), TestDetailsDTO.class);
        clientTestDTO.addTestDetails(USER_RESOURCES, testDetailsDTO);
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
                testDetailsDTO = createSellOffer(jwt, companyId, amount, price);
                if(testDetailsDTO != null)
                    clientTestDTO.addTestDetails(SELLOFFER, testDetailsDTO);
                break;
            case RAND_RANDOM_MANY_COMP:
                // sell stocks of many companies
                jsonObject = new JSONObject(getStockRates(jwt));
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
                    testDetailsDTO = createSellOffer(jwt, company.getId(), amount, price);
                    if(testDetailsDTO != null)
                        clientTestDTO.addTestDetails(SELLOFFER, testDetailsDTO);
                    stocks.remove(stockNum);
                }
                break;
        }
    }

    private TestDetailsDTO createBuyOffer(String jwt, int companyId, int amount, double price) throws JSONException {
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
        try {
            testDetailsDTO = restTemplate.postForObject("http://172.20.0.2:8080/api/buyOffer", request, TestDetailsDTO.class);
        } catch (Exception e){
            log.info("Error " + e);
        }
        return testDetailsDTO;
    }

    private TestDetailsDTO createSellOffer(String jwt, int companyId, int amount, double price) throws JSONException {
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
        log.info("CREATING A BUY OFFER");
        TestDetailsDTO testDetailsDTO = null;
        try {
            testDetailsDTO = restTemplate.postForObject("http://172.20.0.2:8080/api/sellOffer", request, TestDetailsDTO.class);
        } catch (Exception e){
            log.info("Error " + e);
        }
        return testDetailsDTO;
    }
}




















