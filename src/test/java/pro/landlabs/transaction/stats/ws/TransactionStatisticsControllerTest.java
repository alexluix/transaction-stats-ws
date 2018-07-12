package pro.landlabs.transaction.stats.ws;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import pro.landlabs.transaction.stats.App;
import pro.landlabs.transaction.stats.test.TransactionMother;
import pro.landlabs.transaction.stats.ws.value.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static pro.landlabs.transaction.stats.App.STATS_PERIOD_SECONDS;
import static pro.landlabs.transaction.stats.service.TransactionStatisticsService.PRECISION_SCALE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class TransactionStatisticsControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext webApplicationContext;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertThat(mappingJackson2HttpMessageConverter, notNullValue());
    }

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        verifyEmptyStatisticsOutput();
    }

    private void verifyEmptyStatisticsOutput() throws Exception {
        mockMvc.perform(get("/statistics")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"sum\": 0,\n" +
                                "\"avg\": 0,\n" +
                                "\"max\": 0,\n" +
                                "\"min\": 0,\n" +
                                "\"count\": 0}"
                ));
    }

    @Test
    public void shouldProduceStatisticsForMultipleTransactionsSkippingOutOfRangeTransactions() throws Exception {
        // given
        DateTime currentDateTime = DateTime.now();

        List<Transaction> transactions = ImmutableList.of(
                TransactionMother.createTransaction(randomSecondsBackWithinRange(currentDateTime), 12.3),
                TransactionMother.createTransaction(randomSecondsBackWithinRange(currentDateTime)),
                TransactionMother.createTransaction(randomSecondsBackWithinRange(currentDateTime))
        );
        List<Transaction> outOfRangeTransactions = ImmutableList.of(
                TransactionMother.createTransaction(randomSecondsBackOtOfRange(currentDateTime)),
                TransactionMother.createTransaction(randomSecondsBackOtOfRange(currentDateTime)),
                TransactionMother.createTransaction(randomSecondsBackOtOfRange(currentDateTime))
        );
        List<Transaction> allTransactions = Lists.newArrayList();
        allTransactions.addAll(transactions);
        allTransactions.addAll(outOfRangeTransactions);
        Collections.shuffle(allTransactions);

        for (Transaction transaction : allTransactions) {
            registerTransaction(transaction);
        }

        // when then
        DoubleSummaryStatistics summaryStatistics =
                transactions.stream().collect(Collectors.summarizingDouble(Transaction::getAmount));

        mockMvc.perform(get("/statistics")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("sum", equalTo(formattedDouble(summaryStatistics.getSum()))))
                .andExpect(jsonPath("min", equalTo(formattedDouble(summaryStatistics.getMin()))))
                .andExpect(jsonPath("max", equalTo(formattedDouble(summaryStatistics.getMax()))))
                .andExpect(jsonPath("avg", equalTo(formattedDouble(summaryStatistics.getAverage()))))
                .andExpect(jsonPath("count", equalTo(transactions.size())));
    }

    private double formattedDouble(double number) {
        return new BigDecimal(Double.valueOf(number).toString()).setScale(PRECISION_SCALE, RoundingMode.HALF_UP).doubleValue();
    }

    private DateTime randomSecondsBackWithinRange(DateTime currentDateTime) {
        return currentDateTime.minusSeconds(randomSecondsWithinRange());
    }

    private DateTime randomSecondsBackOtOfRange(DateTime currentDateTime) {
        return currentDateTime.minusSeconds(STATS_PERIOD_SECONDS + randomSecondsWithinRange());
    }

    private int randomSecondsWithinRange() {
        return new Random().nextInt(STATS_PERIOD_SECONDS);
    }

    private void registerTransaction(Transaction transaction) throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(json(transaction)))
                .andExpect(status().isCreated());
    }

    protected String json(Object o) throws Exception {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
