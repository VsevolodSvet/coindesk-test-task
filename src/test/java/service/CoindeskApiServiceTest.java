package service;

import com.squareup.okhttp.OkHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CoindeskApiServiceTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private CoindeskApiService coindeskApiService;

    private static final String TEST_PATTERN = "The current Bitcoin rate is [0-9\\.]+ EUR(\\r\\n|\\n)" +
            "The lowest Bitcoin rate in the last 30 days is [0-9\\.]+ EUR(\\r\\n|\\n)" +
            "The highest Bitcoin rate in the last 30 days is [0-9\\.]+ EUR(\\r\\n|\\n)?";
    private static final String WRONG_CURRENCY_TEST_PATTERN = "Sorry, your requested currency %s is not supported or is invalid(\\r\\n|\\n)";

    @Before
    public void init() {
        coindeskApiService = new CoindeskApiService(new OkHttpClient());
        System.setOut(new PrintStream(output));
    }

    @Test
    public void coindeskApiServiceInit() {
        assertNotNull(coindeskApiService);
    }

    @Test
    public void printData_positive() throws IOException {
        //arrange
        String testCurrency = "eur";

        //act
        coindeskApiService.printData("eur");

        //assert
        assertTrue(output.toString().matches(TEST_PATTERN));
    }

    @Test
    public void printData_negative() throws IOException {
        //arrange
        String testCurrency = "qqq";

        //act
        coindeskApiService.printData(testCurrency);

        //assert
        assertTrue(output.toString().matches(String.format(WRONG_CURRENCY_TEST_PATTERN, testCurrency.toUpperCase(Locale.ROOT))));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }
}