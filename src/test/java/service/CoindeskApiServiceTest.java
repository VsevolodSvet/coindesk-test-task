package service;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CoindeskApiServiceTest {

    @Test
    public void coindeskApiServiceInit() {
        CoindeskApiService coindeskApiService = new CoindeskApiService();
        assertNotNull(coindeskApiService);
    }

    @Test
    public void printData() {

    }

    @Test
    public void processCurrentRateResponseString() {
    }

    @Test
    public void processPeriodRates() {
    }
}