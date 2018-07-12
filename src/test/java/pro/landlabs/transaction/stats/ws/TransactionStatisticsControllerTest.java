package pro.landlabs.transaction.stats.ws;

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

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static pro.landlabs.transaction.stats.test.TransactionMother.NUMBER_PRECISION;

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
    public void shouldProduceStatisticsForSingleTransaction() throws Exception {
        // given
        DateTime currentDateTime = DateTime.now();
        Transaction transaction = TransactionMother.createTransaction(currentDateTime);

        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(json(transaction)))
                .andExpect(status().isCreated());

        // when then
        mockMvc.perform(get("/statistics")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("sum", closeTo(transaction.getAmount(), NUMBER_PRECISION)))
                .andExpect(jsonPath("min", closeTo(transaction.getAmount(), NUMBER_PRECISION)))
                .andExpect(jsonPath("max", closeTo(transaction.getAmount(), NUMBER_PRECISION)))
                .andExpect(jsonPath("avg", closeTo(transaction.getAmount(), NUMBER_PRECISION)))
                .andExpect(jsonPath("count", equalTo(1d)));
    }

    protected String json(Object o) throws Exception {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
