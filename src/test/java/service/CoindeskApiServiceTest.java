package service;

import com.squareup.okhttp.*;
import okio.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CoindeskApiServiceTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Mock
    OkHttpClient okHttpClient;
    @InjectMocks
    private CoindeskApiService coindeskApiService;

    @Mock
    Call call;

    @Mock
    ResponseBody responseBody;

    // avoid of mocking final class
    Response response;


    private static final String CURRENT_EUR_TEST = "{\"time\":{\"updated\":\"Aug 9, 2021 08:30:00 UTC\",\"updatedISO\":\"2021-08-09T08:30:00+00:00\",\"updateduk\":\"Aug 9, 2021 at 09:30 BST\"},\"disclaimer\":\"This data was produced from the CoinDesk Bitcoin Price Index (USD). Non-USD currency data converted using hourly conversion rate from openexchangerates.org\",\"bpi\":{\"USD\":{\"code\":\"USD\",\"rate\":\"43,680.1748\",\"description\":\"United States Dollar\",\"rate_float\":43680.1748},\"EUR\":{\"code\":\"EUR\",\"rate\":\"37,150.3818\",\"description\":\"Euro\",\"rate_float\":37150.3818}}}";
    private static final String HISTORICAL_EUR_TEST = "{\"bpi\":{\"2021-07-09\":28462.9115,\"2021-07-10\":28217.1493,\"2021-07-11\":28843.0326,\"2021-07-12\":27897.5012,\"2021-07-13\":27787.3857,\"2021-07-14\":27729.3279,\"2021-07-15\":26977.5477,\"2021-07-16\":26598.3552,\"2021-07-17\":26715.7633,\"2021-07-18\":26928.1826,\"2021-07-19\":26138.5656,\"2021-07-20\":25295.6699,\"2021-07-21\":27249.6477,\"2021-07-22\":27444.805,\"2021-07-23\":28582.9508,\"2021-07-24\":29129.4148,\"2021-07-25\":30083.4205,\"2021-07-26\":31576.5839,\"2021-07-27\":33414.3916,\"2021-07-28\":33789.7765,\"2021-07-29\":33681.2694,\"2021-07-30\":35567.7302,\"2021-07-31\":34936.2479,\"2021-08-01\":33608.9257,\"2021-08-02\":32982.2346,\"2021-08-03\":32198.7411,\"2021-08-04\":33575.1712,\"2021-08-05\":34549.4935,\"2021-08-06\":36433.8102,\"2021-08-07\":37921.7742,\"2021-08-08\":37248.1046},\"disclaimer\":\"This data was produced from the CoinDesk Bitcoin Price Index. BPI value data returned as EUR.\",\"time\":{\"updated\":\"Aug 9, 2021 00:03:00 UTC\",\"updatedISO\":\"2021-08-09T00:03:00+00:00\"}}";
    private static final String WRONG_CURRENCY_TEST = "Sorry, that currency was not found";

    @Before
    public void init() {
        System.setOut(new PrintStream(output));
        response = new Response.Builder()
                .request(new Request.Builder()
                        .url(new HttpUrl.Builder()
                                .scheme("http")
                                .host("api.coindesk")
                                .build())
                        .build())
                .protocol(Protocol.HTTP_1_1)
                .body(responseBody)
                .code(201)
                .build();
    }

    @Test
    public void coindeskApiServiceInit() {
        assertNotNull(coindeskApiService);
    }

    @Test
    public void printData_positive() throws IOException {
        //arrange
        when(okHttpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(responseBody.contentType()).thenReturn(MediaType.parse("UTF-8"));
        when(responseBody.source()).thenReturn(new Buffer());
        when(responseBody.string()).then(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                if (count++ == 1)
                    return CURRENT_EUR_TEST;

                return HISTORICAL_EUR_TEST;
            }
        });

        //act
        coindeskApiService.printData("eur");

        //assert
        assertEquals("Test string", output.toString());

    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }
}