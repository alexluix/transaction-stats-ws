package pro.landlabs.transaction.stats.ws;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import pro.landlabs.transaction.stats.App;

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class TransactionRegistrationControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldRegisterTransaction() throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content("{\n" +
                        "\"amount\": 12.3,\n" +
                        "\"timestamp\": 1478192204000\n" +
                        "}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldNotRegisterWithoutTimestampTransaction() throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content("{\n" +
                        "\"amount\": 12.3\n" +
                        "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotRegisterEmptyJsonTransaction() throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotRegisterInvalidJsonTransaction() throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content("{\"unknownField\": 1}"))
                .andExpect(status().isBadRequest());
    }

}
