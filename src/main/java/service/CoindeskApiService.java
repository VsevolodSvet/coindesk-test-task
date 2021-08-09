package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CoindeskApiService {

    public static final String HOST = "https://api.coindesk.com/v1/bpi";
    public static final int PERIOD = 30;
    public OkHttpClient okHttpClient;

    public CoindeskApiService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public void printData(String currency) {
        Request request = new Request.Builder()
                .url(String.format("%s/currentprice/%s.json", HOST, currency))
                .method("GET", null)
                .build();
        String responseString = getResponse(request);
        if (responseString == null) return;
        currency = currency.toUpperCase();
        double currentRate = processCurrentRateResponseString(responseString, currency);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(PERIOD);
        request = new Request.Builder()
                .url(String.format("%s/historical/close.json?start=%s&end=%s&currency=%s", HOST, startDate, endDate, currency))
                .method("GET", null)
                .build();
        responseString = getResponse(request);
        if (responseString == null) return;
        processPeriodRates(responseString, currency, currentRate);
    }

    public double processCurrentRateResponseString(String responseString, String currency) {
        Map<String, Object> bpi = getBpiMap(responseString);
        Map<String, Object> currencyValues = (Map<String, Object>) bpi.get(currency);
        double currentRate = Double.parseDouble(currencyValues.get("rate_float").toString());
        System.out.printf("The current Bitcoin rate is %s %s%n", currentRate, currency);
        return currentRate;
    }

    public void processPeriodRates(String responseString, String currency, double currentRate) {
        Map<String, Object> bpi = getBpiMap(responseString);
        List<Double> periodRates = bpi.values().stream().map(o -> (Double) o).collect(Collectors.toList());
        periodRates.add(currentRate);
        System.out.printf("The lowest Bitcoin rate in the last %s days is %s %s%n", PERIOD, periodRates.stream().min(Comparator.naturalOrder()).get(), currency);
        System.out.printf("The highest Bitcoin rate in the last %s days is %s %s%n", PERIOD, periodRates.stream().max(Comparator.naturalOrder()).get(), currency);
    }

    private Map<String, Object> getBpiMap(String responseString) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = null;
        try {
            map = (Map<String, Object>) mapper.readValue(responseString, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return (Map<String, Object>) map.get("bpi");
    }

    private String getResponse(Request currentRateRequest) {
        String responseString = null;
        try {
            Response response = okHttpClient.newCall(currentRateRequest).execute();
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (responseString == null) {
            System.out.println("No response from server");
        } else if (responseString.startsWith("Sorry")) {
            System.out.println(responseString);
            return null;
        }
        return responseString;
    }
}
